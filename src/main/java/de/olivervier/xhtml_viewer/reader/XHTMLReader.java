package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.olivervier.xhtml_viewer.model.Page;

public class XHTMLReader {

	public List<Page> readPages(List<File> files) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		for(File file : files) {			
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(file);
				
				//dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				doc.getDocumentElement().normalize();

				String fileName = file.getName();
				NodeList compositions = doc.getElementsByTagName("ui:composition");
				NodeList parameters = doc.getElementsByTagName("ui:param");

			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Get filename without extension
	 * @param file
	 * @return
	 */
	private String getFileName(String fileName) {
		int pointIdx = fileName.lastIndexOf(".");
		return pointIdx!=-1 ? fileName.substring(0,pointIdx) : fileName;
	}
}