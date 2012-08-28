package de.piratenpartei.id;
import java.io.IOException;
import java.util.LinkedList;

import javax.management.RuntimeErrorException;

import net.sf.json.*;


public class VAOV {
	private IniTree inis;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public void VAOV() throws IOException {
		String s = "";
		s = Config.loadInis();
		
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(s);

		inis = new IniTree(jo);
		
		System.out.println("Building IniTree finished");
	} 

	public static String loadInisFromFile(String path) throws IOException{
		LinkedList<String> list = new LinkedList<String>();
		
		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path));
		String s;
		while((s = br.readLine()) != null) list.add(s);
		br.close();
		
		String result = "";
		while(!list.isEmpty()) result += list.pop();
		
		return result;
	}

}
