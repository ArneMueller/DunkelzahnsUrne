package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.json.simple.*;
import org.junit.Test;
import de.piratenpartei.id.*;;

public class TestTopicList {

	private TopicList tl;
	
	public void init(){
		
	}
	
	@Test
	public void testNormal() {
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("Gartengestaltung");
		
		ArrayList<String> captions = new ArrayList<String>();
		captions.add("Blumen sind schön");
		captions.add("Blumen machen Arbeit");
		
		ArrayList<String> texts = new ArrayList<String>();
		texts.add("Ich finde, dass Blumen schön sind");
		texts.add("Ich finde, dass Blumen zu viel Arbeit machen");
		
		tl = new TopicList();
		tl.addIniInNewTopic(new Ini(captions.get(0),texts.get(0)) , tags);
		tl.addIniToTopic(new Ini(captions.get(1), texts.get(1)), 0);
		
		assertTrue(tl.getCategories().equals(tags));

		assertTrue(tl.getTopics().get(0).getTags().equals(tags));
		
		assertTrue(tl.getTopics().get(0).getInis().get(0).getCaption().equals(captions.get(0)));
		assertTrue(tl.getTopics().get(0).getInis().get(1).getCaption().equals(captions.get(1)));
		
		assertTrue(tl.getTopics().get(0).getInis().get(0).getText().equals(texts.get(0)));
		assertTrue(tl.getTopics().get(0).getInis().get(1).getText().equals(texts.get(1)));
		
		JSONObject jo = tl.toJSON();
		JSONObject jo2 = jo;
		jo = new JSONObject();
		jo.put("structure", "list");
		jo.put("data", jo2);
		System.out.println(jo);
		try {
			PrintWriter out = new PrintWriter("data.dat");
			out.println(jo);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			assertTrue(false);
		}
		tl = new TopicList(jo);

		assertTrue(tl.getCategories().equals(tags));

		assertTrue(tl.getTopics().get(0).getTags().equals(tags));
		
		assertTrue(tl.getTopics().get(0).getInis().get(0).getCaption().equals(captions.get(0)));
		assertTrue(tl.getTopics().get(0).getInis().get(1).getCaption().equals(captions.get(1)));
		
		assertTrue(tl.getTopics().get(0).getInis().get(0).getText().equals(texts.get(0)));
		assertTrue(tl.getTopics().get(0).getInis().get(1).getText().equals(texts.get(1)));
		
	}

}
