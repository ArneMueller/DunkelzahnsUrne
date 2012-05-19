package de.piratenpartei.id.test;

import static org.junit.Assert.*;

import java.security.Provider;
import java.security.Security;

import org.junit.Test;

public class TestProviders {

	@Test
	public void test() {
		Provider[] providers = Security.getProviders();
		System.out.println("found "+providers.length+" provider");
		for(Provider p : providers) {
			System.out.println(p.getName());
			System.out.println(p.getInfo());
			System.out.println();
		}
	}

}
