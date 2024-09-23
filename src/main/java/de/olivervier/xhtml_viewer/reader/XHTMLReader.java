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

				final String fileName = file.getName();
				NodeList compositions = doc.getElementsByTagName("ui:composition");
				NodeList parameters = doc.getElementsByTagName("ui:param");
				NodeList includes = doc.getElementsByTagName("ui:include");
				
				Page currentPage = pages.get(fileName);
				
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
						currentPage.getRelations().add(templatePage);
					}
				}
				
				//Look in parameters tag/s for parameters
				for(int i = 0; i < parameters.getLength(); i++) {
					NamedNodeMap parameterAttributes = parameters.item(i).getAttributes();
					if(parameterAttributes == null) {
						continue;
					}
					Node nameNode = parameterAttributes.getNamedItem("name");
					Node valueNode = parameterAttributes.getNamedItem("value");
					if(nameNode == null || valueNode == null) {
						continue;
					}
					currentPage.getParameters().add(new Param(nameNode.getNodeValue(), valueNode.getNodeValue()));
				}
				
				//Look for ui:includes, add relation in foreignPage to currentPage
				for(int i = 0; i < includes.getLength(); i++) {
					NamedNodeMap includeAttributes = includes.item(i).getAttributes();
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
						foreignPage.getRelations().add(currentPage);
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		return pages.values().stream().toList();
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