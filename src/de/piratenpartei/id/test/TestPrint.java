package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import gnu.jpdf.PDFTest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import de.piratenpartei.id.Account;
import de.piratenpartei.id.KeyException;

public class TestPrint {

	@Test
	public void testPrintAccount() throws KeyException, IOException {
		Account a = new Account("i9YMFc+vWTpO3B5hxwsg80UvHX6sGUKdhyly7RQOfg230UZfaVfN6fqHMtfb3bxydpRUEuQvgKgW450FwzrSww==");
		FileOutputStream out = new FileOutputStream("test_files/account.pdf");
		a.print(out);
		out.close();
	}

}
