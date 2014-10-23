package com.versionone.apiclient.integration.tests;

import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.versionone.apiclient.ConnectionException;
import com.versionone.apiclient.ICookiesManager;
import com.versionone.apiclient.ProxyProvider;
import com.versionone.apiclient.V1APIConnector;

//TODO:
//		Error: Bad instance URL
//		Error: Invalid credentials

public class ConnectorTests {

	private static final String V1_PATH = APIClientSuiteIT.getInstanceUrl();
    private static final String V1_USERNAME = "admin";
    private static final String V1_PASSWORD = "admin";

	@Before
	public void clearCookes() {
		V1APIConnector testMe = new V1APIConnector(V1_PATH);
		testMe.getCookiesJar().deleteAllCookies();
	}

	@Test(expected = ConnectionException.class)
	public void testInvalidUser() throws ConnectionException {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, "foo", "bar");
		testMe.getData("rest-1.v1/Data/Scope/0");
	}

	@Test
	public void testValidUser() throws ConnectionException {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_PASSWORD);
		Reader results = testMe.getData("/rest-1.v1/Data/Scope/0");
		Assert.assertTrue(results != null);
	}

	@Test(expected = ConnectionException.class)
	public void testURLInvalidUserAfterValid() throws ConnectionException {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_PASSWORD);
		Reader results = testMe.getData("/rest-1.v1/Data/Scope/0");
        testMe = new V1APIConnector(V1_PATH);
        results = testMe.getData("/meta.v1/Scope");
		Assert.assertTrue(results != null);
		testMe = new V1APIConnector(V1_PATH, "foo", "bar");
		testMe.getData("/rest-1.v1/Data/Scope/0");
	}
	
	@Test
	@Ignore("Need to run it manually. Required proxy server.")
	public void testProxy() throws ConnectionException, URISyntaxException {
		URI uri = new URI("http://integvm01.internal.corp:3128");
		ProxyProvider proxy = new ProxyProvider(uri, "user1", "user1");
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_PASSWORD, proxy);
		testMe.getData("rest-1.v1/Data/Scope/0");
	}	

	@Test
	public void testCookiesManger2() {
		Date expireDate = new Date();
		expireDate.setTime(new Date().getTime() + 1000000);
		String name1 = "name1";
		String value1 = "value1";
		String name2 = "name2";
		String value2 = "value2";
		V1APIConnector testMe = new V1APIConnector(V1_PATH, "foo", "bar");
		ICookiesManager cookiesManager = testMe.getCookiesJar();
		cookiesManager.addCookie(name1, value1, expireDate);
		cookiesManager.addCookie(name2, value2, expireDate);
		Assert.assertEquals(value1, cookiesManager.getCookie(name1));
		Assert.assertEquals(value2, cookiesManager.getCookie(name2));
		cookiesManager.deleteCookie(name1);
		Assert.assertEquals(null, cookiesManager.getCookie(name1));
		Assert.assertEquals(value2, cookiesManager.getCookie(name2));
	}

	@Test(expected = ConnectionException.class)
	public void testEmptyUserAfterValid() throws ConnectionException {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_USERNAME);
		testMe.getData("/rest-1.v1/Data/Scope/0");
		testMe = new V1APIConnector(V1_PATH, "", "");
		testMe.getData("/rest-1.v1/Data/Scope/0");
	}

	@Test()
	public void testUserAgentHeadersWithOutApp() {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_USERNAME);
		Package p = this.getClass().getPackage();
		String header = "Java/" + System.getProperty("java.version") + " " + p.getImplementationTitle() + "/" + p.getImplementationVersion();
		Assert.assertEquals("", header, testMe.getUserAgentHeader());
	}

	@Test()
	public void testUserAgentHeadersWithApp() {
		V1APIConnector testMe = new V1APIConnector(V1_PATH, V1_USERNAME, V1_USERNAME);
		String app_name = "myApp";
		String app_version = "1.0.0";
		testMe.setUserAgentHeader(app_name, app_version);
		Package p = this.getClass().getPackage();
		String header = "Java/" + System.getProperty("java.version") + " " + p.getImplementationTitle() + "/" + p.getImplementationVersion();
		header = header + " " + app_name + "/" + app_version;
		Assert.assertEquals("", header, testMe.getUserAgentHeader());
	}

}