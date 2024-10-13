package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;

public class XHTMLReader {
	
	/**
	 * Reads xhtml pages and finds relationships and parameters between them.
	 * @param files pages as xhtml to be read
	 * @return list of xhtml pages as Page objects
	 */
	public List<Page> readPages(String basepath, List<File> files) {
		
		if(basepath == null) {
			throw new IllegalArgumentException("basepath cannot be null!");
		}

		if(files == null) {
			throw new IllegalArgumentException("parameter 'files' must not be null!");
		}
		
		//Get path in OS style
		basepath = new File(basepath).getPath();
		
		//Prefill
		Map<String, Page> pages = new HashMap<>();
		for(File currentFile : files) {
			String relativePathName = null;
			
			if(!currentFile.getPath().contains(basepath)) {
				throw new IllegalArgumentException();
			} else {
				relativePathName = currentFile.getPath().replace(basepath, "");
			}

			pages.put(relativePathName, new Page(relativePathName,new ArrayList<>(),new ArrayList<>()));
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		for(File file : files) {			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				
				//dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				doc.getDocumentElement().normalize();

				if(file.getName().equals("AnzeigeFortbildungsstand.xhtml")) {
					System.out.println();
				}

				String relativePathName = file.getPath().replace(basepath, "");
				Page currentPage = pages.get(relativePathName);
				
				currentPage.getRelations().addAll(findCompositions(doc, basepath, pages));
				currentPage.getParameters().addAll(findParameters(doc));
				findIncludes(doc, basepath, pages).forEach(foreignPage -> foreignPage.getRelations().add(currentPage));

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		return pages.values().stream().toList();
	}
	
	
	/**
	 * Looks for ui:composition tags in the given xml structure. Returns composition names
	 * which match an entry in the given pages map.
	 * @param doc xml document of xhtml file
	 * @param pages Map containing all possible xhtml pages of type {@link Page}
	 * @return list of compositions matching name in pages-map
	 */
	public List<Page> findCompositions(Document doc, String basepath, Map<String, Page> pages) {
		NodeList compositions = doc.getElementsByTagName("ui:composition");
		List<Page> relations = new ArrayList<>();
		
		//Look in composition tag/s for name without xhtml and search in pages map
		for(int i = 0; i < compositions.getLength(); i++) {
			NamedNodeMap currentCompositionAttributes = compositions.item(i).getAttributes();
			if(currentCompositionAttributes == null) {
				continue;
			}
			Node templateAttribute = currentCompositionAttributes.getNamedItem("template");
			if(templateAttribute == null) {
				continue;
			}
			String templateName = templateAttribute.getNodeValue();
			
			File file = new File(Paths.get(basepath,templateName).toString());
			if(!file.exists()) {
				throw new IllegalArgumentException("Composition file does not exist!");
			}

			String relativeFilePath = file.getPath().replace(basepath, "");
			
			if(pages.containsKey(relativeFilePath)) {
				Page templatePage = pages.get(relativeFilePath);
				if(templatePage==null) {
					continue;
				}
				relations.add(templatePage);
			}
		}
		
		return relations;
	}
	
	/**
	 * Looks for ui:param tags in the given xml structure. 
	 * Returns parameters as type {@link Param}
	 * @param doc xml document of xhtml file
	 * @return list of ui:param as type {@link Param} of given xhtml document
	 */
	public List<Param> findParameters(Document doc) {
		
		NodeList parameterNodes = doc.getElementsByTagName("ui:param");
		List<Param> parameters = new ArrayList<>();
		
		//Look in parameters tag/s for parameters
		for(int i = 0; i < parameterNodes.getLength(); i++) {
			NamedNodeMap parameterAttributes = parameterNodes.item(i).getAttributes();
			if(parameterAttributes == null) {
				continue;
			}
			Node nameNode = parameterAttributes.getNamedItem("name");
			Node valueNode = parameterAttributes.getNamedItem("value");
			if(nameNode == null || valueNode == null) {
				continue;
			}
			parameters.add(new Param(nameNode.getNodeValue(), valueNode.getNodeValue()));
		}
		return parameters;
	}
	
	/**
	 * Looks for ui:include tags in the given xml structure. Searches the name in the ui:include src
	 * attribute in the pages map parameter.
	 * @param doc xml document of xhtml file
	 * @param pages Map containing all possible xhtml pages of type {@link Page}
	 * @return list of xhtml pages as type {@link Param} in xhtml document
	 */
	public List<Page> findIncludes(Document doc, String basepath, Map<String, Page> pages) {
		
		NodeList includeNodes = doc.getElementsByTagName("ui:include");
		List<Page> relations = new ArrayList<>();
		
		//Look for ui:includes, add relation in foreignPage to currentPage
		for(int i = 0; i < includeNodes.getLength(); i++) {
			NamedNodeMap includeAttributes = includeNodes.item(i).getAttributes();
			if(includeAttributes == null) {
				continue;
			}
			Node srcAttribute = includeAttributes.getNamedItem("src");
			if(srcAttribute == null) {
				continue;
			}
			String srcValue = srcAttribute.getNodeValue();

			File file = new File(Paths.get(basepath,srcValue).toString());
			if(!file.exists()) {
				throw new IllegalArgumentException("Include file does not exist!");
			}

			String relativeFilePath = file.getPath().replace(basepath, "");

			if(pages.containsKey(relativeFilePath)) {
				Page foreignPage = pages.get(relativeFilePath);
				if(foreignPage == null) {
					continue;
				}
				relations.add(foreignPage);
			}
		}
		
		return relations;
	}
	
	/**
	 * Get filename without extension
	 * @param file
	 * @return
	 */
	private String getFileName(String fileName) {
		if(fileName==null) {return null;}
		int pointIdx = fileName.lastIndexOf(".");
		return pointIdx!=-1 ? fileName.substring(0,pointIdx) : fileName;
	}
}