package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class TestJSON {

	@Test
	public void test() {
		
		JSONObject jo1 = new JSONObject();
		jo1.put("foo", "bar");
		String s = (String) jo1.get("foo");
		assertEquals("bar", s);
		System.out.println(s);

	}

}
