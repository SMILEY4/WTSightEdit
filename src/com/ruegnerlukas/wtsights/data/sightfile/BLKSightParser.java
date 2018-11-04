package com.ruegnerlukas.wtsights.data.sightfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.ruegnerlukas.simpleutils.logging.logger.Logger;

public class BLKSightParser {

	
	public static Block parse(File file) {
		Logger.get().info("Parsing sight: " + file);
		String rawContent = BLKSightParser.read(file);
		String ppContent = BLKSightParser.prepare(rawContent);
		List<String> strElements = BLKSightParser.split(ppContent);
		Block root = BLKSightParser.parseElements(strElements);
		return root;
	}
	
	
	
	
	
	public static String read(File file) {
		
		StringBuilder sb = new StringBuilder();
		
		try {
		
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String str;
			while( (str = reader.readLine()) != null) {
				sb.append(str).append(System.lineSeparator());
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			Logger.get().error(e);
		} catch (IOException e) {
			Logger.get().error(e);
		}
		
		return sb.toString();
		
	}
	
	
	
	
	public static String prepare(String rawFileContent) {
		final String regexWS = "\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)"; // find all whitespace outside of quotes
//		final String regexBC = "(\\/\\*([^*]|[\\r\\n]|(\\*+([^*\\/]|[\\r\\n])))*\\*+\\/)|(\\/\\/.*)"; // find all (block-)comments
		final String regexBC = "(\\/\\*([^*]|[\\r\\n]|(\\*+([^*\\/]|[\\r\\n])))*\\*+\\/)|(\\/\\/(?!--).*)"; // find all (block-)comments, exept starting with "//--"
		final String regexTB = "\\t"; // find all tabs
		
		rawFileContent = rawFileContent
				.replaceAll(regexTB, "")
				.replaceAll(regexBC, "")
				.replaceAll(System.lineSeparator(), ";")
				.replaceAll("\n", ";")
				.replaceAll("\r\n", ";")
				.replaceAll("\r", ";")
				.replaceAll(regexWS, "");
		
		return rawFileContent;
	}
	
	
	
	
	public static List<String> split(String prepContent) {
		
		String[] arrElements = prepContent.split(";");
		List<String> listElementsRaw = new ArrayList<String>(arrElements.length);
		for(String s : arrElements) {
			listElementsRaw.add(s);
		}
		
		List<String> listElements = new ArrayList<String>(listElementsRaw.size());

		
		while(!listElementsRaw.isEmpty()) {
			
			String current = listElementsRaw.remove(0);
			
			if(current.isEmpty()) {
				continue; 
			}
			
			if(current.startsWith("//--")) {
				listElements.add(current);
				continue;
			}
			
			if(current.contains("{") && !current.endsWith("{")) {

				int index = current.indexOf("{");
				String head = current.substring(0, index+1);
				String tail = current.substring(index+1, current.length());
				
				listElements.add(head);
				listElementsRaw.add(0, tail);
				
				continue;
			}
			
			if(current.contains("}") && !current.startsWith("}") ) {
				
				int index = current.indexOf("}");
				String head = current.substring(0, index);
				String tail = current.substring(index, current.length());
				
				listElements.add(head);
				listElementsRaw.add(0, tail);
				
				continue;
			}
			
			
			listElements.add(current);
			
		}
		
		return listElements;
	}
	
	
	
	
	public static Block parseElements(List<String> listElements) {
		
		Block root = new Block("ROOT", "ROOT");
		
		Stack<Block> stack = new Stack<Block>();
		stack.push(root);
		
		
		String lastMetaData = null;
		
		for(int i=0; i<listElements.size(); i++) {
			String current = listElements.get(i);
			
			if(current.startsWith("//--")) {
				lastMetaData = current.substring(4, current.length());
				continue;
			}
			
			// BLOCK START
			if(current.contains("{")) {
				Block block = new Block(current.replaceAll("\\{", ""), lastMetaData);
				Block currentBlock = stack.peek();
				currentBlock.elements.add(block);
				stack.push(block);
				
			// BLOCK END
			} else if(current.contains("}")) {
				stack.pop();
				
			// PARAMETER	
			} else {
				stack.peek().elements.add(parseParameter(current, lastMetaData));
			}
			
			lastMetaData = null;

		}
		
		return root;
	}
	
	
	
	
	private static Parameter parseParameter(String strParameter, String metadata) {
		
		String paramName = strParameter.substring(0, strParameter.indexOf(":"));
		String paramBody = strParameter.substring(strParameter.indexOf(":")+1, strParameter.length());
		
		String strType = paramBody.substring(0, paramBody.indexOf("="));
		String strValue = paramBody.substring(paramBody.indexOf("=")+1, paramBody.length());
		
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.TEXT.token)) {
			return new ParamText(paramName, metadata, strValue);
		}
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.BOOL.token)) {
			if(strValue.equalsIgnoreCase("0")) {
				strValue = "false";
			} else if(strValue.equalsIgnoreCase("1")) {
				strValue = "true";
			}
			if(strValue.equalsIgnoreCase("no")) {
				strValue = "false";
			} else if(strValue.equalsIgnoreCase("yes")) {
				strValue = "true";
			}
			return new ParamBool(paramName, metadata, Boolean.parseBoolean(strValue));
		}
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.FLOAT.token)) {
			try {
				return new ParamFloat(paramName, metadata, Float.parseFloat(strValue));
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + strParameter + "' is not a float.");
			}
		}
		
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.INTEGER.token)) {
			try {
				return new ParamInteger(paramName, metadata, Integer.parseInt(strValue));
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + strParameter + "' is not a integer.");
			}
		}
		
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.VEC2.token)) {
			String[] values = strValue.split(",");
			if(values.length != 2) {
				Logger.get().error("Error parsing parameter " + strParameter + ": invalid length");
				return null;
			}
			try {
				return new ParamVec2(paramName, metadata, Float.parseFloat(values[0]), Float.parseFloat(values[1]));
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + values[0] + " or " + values[1] + "' is not a float.");
			}
		}
		
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.VEC3.token)) {
			String[] values = strValue.split(",");
			if(values.length != 3) {
				Logger.get().error("Error parsing parameter " + strParameter + ": invalid length");
				return null;
			}
			try {
				return new ParamVec3(paramName, metadata, Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + values[0] + " or " + values[1] + " or " + values[2] + "' is not a float.");
			}
		}
		
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.VEC4.token)) {
			String[] values = strValue.split(",");
			if(values.length != 4) {
				Logger.get().error("Error parsing parameter " + strParameter + ": invalid length");
				return null;
			}
			try {
				return new ParamVec4(paramName, metadata, Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + values[0] + " or " + values[1] + " or " + values[2] + " or " + values[3] + "' is not a float.");
			}
		}
		
		if(strType.equalsIgnoreCase(Parameter.ParameterType.COLOR.token)) {
			String[] values = strValue.split(",");
			try {
				if(values.length == 3) {
					return new ParamColor(paramName, metadata, Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
				} else if(values.length == 4) {
					return new ParamColor(paramName, metadata, Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]));
				} else {
					Logger.get().error("Error parsing parameter " + strParameter + ": invalid length");
					return null;
				}
			} catch(NumberFormatException e) {
				Logger.get().error("NumberFormatException: '" + strValue + "' is not a color.");
			}
			
		}
		
		Logger.get().error("Unknown error parsing parameter: " + strParameter);
		return null;
	}
	
	
}








