package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.piratenpartei.id.KeyException;
import de.piratenpartei.id.PrivateAccount;

public class TestCreate {

	@Test
	public void test() throws KeyException {
		PrivateAccount pa = new PrivateAccount();
		pa.publish();
	}

}
