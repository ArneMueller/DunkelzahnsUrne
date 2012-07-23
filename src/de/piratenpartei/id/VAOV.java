package de.piratenpartei.id;
import java.util.LinkedList;


public class VAOV {
	private static IniTree inis;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = loadInisFromFile("data.dat");
		inis = new IniTree(s);
		System.out.println("asdf");
	}

	public static String loadInisFromFile(String path){
		LinkedList<String> list = new LinkedList<String>();
		
		try {
			java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(path));
			String s;
			while((s = br.readLine()) != null) list.add(s);
			br.close();
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		return null;
	}
}
