package com.ruegnerlukas.wtutils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtils {

	
	
	public static Element getElementByTagName(Element root, String tagName) {
		NodeList nodeList = root.getElementsByTagName(tagName);
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				return (Element)node;
			}
		}
		return null;
	}
	
	
	
	
	public static List<Element> getElementsByTagName(Element root, String tagName) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodeList = root.getElementsByTagName(tagName);
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				list.add((Element)node);
			}
		}
		return list;
	}
	
	
	
	
	public static List<Element> getChildren(Element root) {
		List<Element> list = new ArrayList<Element>();
		NodeList nodeList = root.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				list.add((Element)node);
			}
		}
		return list;
	}
	
}




