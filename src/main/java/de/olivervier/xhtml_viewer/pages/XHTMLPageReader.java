package de.olivervier.xhtml_viewer.pages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.olivervier.xhtml_viewer.cli.UserInteraction;
import de.olivervier.xhtml_viewer.model.InputOption;
import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;
import de.olivervier.xhtml_viewer.model.Relation;
import de.olivervier.xhtml_viewer.model.Relation.RelationType;

public class XHTMLPageReader implements PageReader{
	
	private List<Page> pages;

	@Override
	public void init(Path directoryPath) {
		List<File> files = new FileFinder(directoryPath, InputOption.XHTML).read();
		this.pages = readPages(directoryPath, files);
	}

	@Override
	public List<Page> getPages() {
		return pages;
	}

	/**
	 * Reads xhtml pages and finds relationships and parameters between them.
	 * @param files pages as xhtml to be read
	 * @return list of xhtml pages as Page objects
	 */
	private List<Page> readPages(Path directoryPath, List<File> files) {
		
		if(directoryPath == null) {
			throw new IllegalArgumentException("basepath cannot be null!");
		}
		if(files == null) {
			throw new IllegalArgumentException("parameter 'files' must not be null!");
		}
				
		//Prefill
		Map<String, Page> pagesMap = prefillPages(files, directoryPath);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		for(File file : files) {			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				
				//dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				doc.getDocumentElement().normalize();
				
				Page currentPage = pagesMap.get(file.toPath().toString());
				if(currentPage == null ) {
					System.err.println("Could not find page created for the following path: " + file.toPath()); 
					continue;
				}

				
				currentPage.getRelations().addAll(findCompositions(doc, directoryPath, pagesMap));
				currentPage.getParameters().addAll(findParameters(doc));
				
				// Fix: ui:include src attribute only works with resources that are relative to the file.
				
				//findIncludes(doc, currentFolderPath, directoryPath, pagesMap).forEach(foreignPage -> currentPage.getRelations().add(foreignPage));
				findIncludes(doc, directoryPath, file, pagesMap).forEach(foreignPage -> currentPage.getRelations().add(foreignPage));

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		ArrayList<Page> pagesList = new ArrayList<>();
		pagesList.addAll(pagesMap.values().stream().toList());
		return pagesList;
	}

	private Map<String, Page> prefillPages(List<File> files, Path directoryPath) {
		Map<String, Page> pagesMap = new HashMap<>();
		for(File currentFile : files) {
			Path absPath = currentFile.toPath();
			pagesMap.put(absPath.toString(),new Page(absPath, new ArrayList<>(), new ArrayList<>()));
		}
		return pagesMap;
	}
	
	/**
	 * Looks for ui:composition tags in the given xml structure. Returns composition names
	 * which match an entry in the given pages map.
	 * @param doc xml document of xhtml file
	 * @param pages Map containing all possible xhtml pages of type {@link Page}
	 * @return list of compositions matching name in pages-map
	 */
	private List<Relation> findCompositions(Document doc, Path webappRootFolder, Map<String, Page> pages) {
		NodeList compositions = doc.getElementsByTagName("ui:composition");
		List<Relation> relations = new ArrayList<>();
		
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
			String templateValue = templateAttribute.getNodeValue();
						
			Path templateFilePath = Path.of(webappRootFolder.toAbsolutePath().toString(), templateValue);
			
			if(pages.containsKey(templateFilePath.toAbsolutePath().toString())) {
				Page templatePage = pages.get(templateFilePath.toAbsolutePath().toString());
				if(templatePage==null) {
					continue;
				}
				relations.add(new Relation(templatePage, RelationType.COMPOSITION));
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
	private List<Param> findParameters(Document doc) {
		
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
	private List<Relation> findIncludes(Document doc, Path webappRootPath, File currentFile, Map<String, Page> pages) {
		
		NodeList includeNodes = doc.getElementsByTagName("ui:include");
		List<Relation> relations = new ArrayList<>();
		
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
			
			
			Path path = Path.of(srcValue);
			if(Objects.isNull(path)) {
				UserInteraction.sendMessage("Could not create path from srcValue " + srcValue);
				continue;
			}
			
			
			
			Path resolvedPath = currentFile.toPath().toAbsolutePath().resolveSibling(path);
			// When currentFile and composition file are not in the same folder.
			if(!resolvedPath.startsWith(currentFile.toPath().getParent().toAbsolutePath())) {
				resolvedPath = Path.of(webappRootPath.toAbsolutePath().toString(), path.toString());
			}
			
			
			if(pages.containsKey(resolvedPath.toString())) {
				Page foreignPage = pages.get(resolvedPath.toString());
				if(foreignPage == null) {
					continue;
				}
				relations.add(new Relation(foreignPage, RelationType.INCLUDE));
			} else {
				System.err.println(resolvedPath.toString() + " not found");
			}
		}
		
		return relations;
	}
}