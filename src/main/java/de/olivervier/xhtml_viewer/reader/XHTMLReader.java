package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.io.IOException;
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
	public List<Page> readPages(List<File> files) {
		
		if(files == null) {
			throw new IllegalArgumentException("parameter 'files' must not be null!");
		}

		//Prefill
		Map<String, Page> pages = new HashMap<>();
		files.forEach(f -> pages.put(f.getName(), new Page(f.getName(),new ArrayList<>(),new ArrayList<>())));

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		for(File file : files) {			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				
				//dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				doc.getDocumentElement().normalize();

				Page currentPage = pages.get(file.getName());
				
				currentPage.getRelations().addAll(findCompositions(doc, pages));
				currentPage.getParameters().addAll(findParameters(doc));
				findIncludes(doc, pages).forEach(foreignPage -> foreignPage.getRelations().add(currentPage));

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
	public List<Page> findCompositions(Document doc, Map<String, Page> pages) {
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
			if(pages.containsKey(templateName)) {
				Page templatePage = pages.get(templateName);
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
	public List<Page> findIncludes(Document doc, Map<String, Page> pages) {
		
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
			if(pages.containsKey(srcValue)) {
				Page foreignPage = pages.get(srcValue);
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