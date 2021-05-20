/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 - 2019 Compuware Corporation
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.compuware.jenkins.common.configuration;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl.KeyStoreSource;
import com.compuware.jenkins.common.configuration.HostConnection.DescriptorImpl;

import hudson.model.FreeStyleProject;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.FormValidation.Kind;
import hudson.util.Secret;
import jenkins.model.Jenkins;

/**
 * Class for testing the Compuware global host connection configuration.
 */
public class HostConnectionTest {
	
	private static final String CERT_FILENAME = "/topaz-test.p12";
	private static final String CERT_PASSWORD = "compuware";
	
	@Rule
	public JenkinsRule j = new JenkinsRule();
	
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();
	
	private HostConnection m_globalHostConnectionConfig;

	@Before
	public void setUp()
	{
		m_globalHostConnectionConfig = new HostConnection("test", "cw01:1234", "TLSv1.2", "1047", "0", "1", "2");
	}
	
	private String getResourcePath(String resourceName) {
		URL is = this.getClass().getResource(resourceName);
		return is.toString().substring(6);
	}

	/**
	 * Test the host port definition.
	 */
	@Test
	public void hostPortTest()
	{
		// empty String
		String input = "";
		String expectedMsg = Messages.checkHostPortEmptyError();
		validateHostPortErrorMessage("Expecting empty host port message.", input, expectedMsg);

		// no host
		input = ":1234";
		expectedMsg = Messages.checkHostPortMissingHostError();
		validateHostPortErrorMessage("Expecting no host message.", input, expectedMsg);

		// no port
		input = "abcd:";
		expectedMsg = Messages.checkHostPortMissingPortError();
		validateHostPortErrorMessage("Expecting no port message.", input, expectedMsg);

		// invalid port
		input = "abcd:aaa";
		expectedMsg = Messages.checkHostPortInvalidPortError();
		validateHostPortErrorMessage("Expecting invalid port message.", input, expectedMsg);

		// no colon
		input = "abcd1234";
		expectedMsg = Messages.checkHostPortFormatError();
		validateHostPortErrorMessage("Expecting missing colon message.", input, expectedMsg);

		// multiple colons
		input = "ab:cd1:234";
		expectedMsg = Messages.checkHostPortFormatError();
		validateHostPortErrorMessage("Expecting error for multiple colons.", input, expectedMsg);

		// valid host:port
		input = "abc:1234";
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckHostPort(input);
		assertEquals("Expecting valid host port.", validation.kind, Kind.OK);
	}

	/**
	 * Validate the host port error messages.
	 * 
	 * @param msg
	 *            the msg to be displayed if the tests fails
	 * @param input
	 *            the input value for the host port
	 * @param expectedMsg
	 *            the expected error message
	 */
	private void validateHostPortErrorMessage(String msg, String input, String expectedMsg)
	{
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckHostPort(input);
		String actualMsg = validation.getMessage();
		System.out.println("Expected: " + expectedMsg);
		System.out.println("Actual  : " + expectedMsg);
		assertEquals(msg, expectedMsg, actualMsg);
	}

	/**
	 * Test the host connection name.
	 */
	@Test
	public void hostDescriptionTest() throws IOException, ServletException
	{
		// empty String
		String input = "";
		String expectedMsg = Messages.checkDescriptionEmptyError();
		validateConnectionNameErrorMessage("Expecting empty connection name message.", input, expectedMsg);

		input = "abc:1234";
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckDescription(input);
		assertEquals("Expecting valid connection name.", validation.kind, Kind.OK);
	}

	/**
	 * Validate the host connection name error messages.
	 * 
	 * @param msg
	 *            the msg to be displayed if the tests fails
	 * @param input
	 *            the input value for the host connection name
	 * @param expectedMsg
	 *            the expected error message
	 */
	private void validateConnectionNameErrorMessage(String msg, String input, String expectedMsg)
			throws IOException, ServletException
	{
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckDescription(input);
		String actualMsg = validation.getMessage();
		System.out.println("Expected: " + expectedMsg);
		System.out.println("Actual  : " + expectedMsg);
		assertEquals(msg, expectedMsg, actualMsg);
	}

	/**
	 * The timeout definition.
	 */
	@Test
	public void timeoutTest()
	{
		// negative timeout
		String input = "-10";
		String expectedMsg = Messages.checkTimeoutError();
		validateTimeoutErrorMessage("Expecting invalid timeout message for a negative value.", input, expectedMsg);

		// non-numeric timeout
		input = "abcd";
		expectedMsg = Messages.checkTimeoutError();
		validateTimeoutErrorMessage("Expecting invalid timeout message for a non-numeric value.", input, expectedMsg);

		// decimal timeout
		input = "1.5";
		expectedMsg = Messages.checkTimeoutError();
		validateTimeoutErrorMessage("Expecting invalid timeout message for a decimal value.", input, expectedMsg);

		// too-large timeout
		input = String.valueOf(Long.MAX_VALUE);
		expectedMsg = Messages.checkTimeoutError();
		validateTimeoutErrorMessage("Expecting invalid timeout message for too large of a value.", input, expectedMsg);

		// no timeout
		input = "";
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckTimeout(input);
		assertEquals("Expecting valid timeout when nothing is specified.", validation.kind, Kind.OK);

		// valid timeout
		input = "10";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckTimeout(input);
		assertEquals("Expecting valid timeout when a whole, positive integer is specfied.", validation.kind, Kind.OK);
	}

	/**
	 * Validate the timeout error messages.
	 * 
	 * @param msg
	 *            the msg to be displayed if the tests fails
	 * @param input
	 *            the input value for the timeout
	 * @param expectedMsg
	 *            the expected error message
	 */
	private void validateTimeoutErrorMessage(String msg, String input, String expectedMsg)
	{
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckTimeout(input);
		String actualMsg = validation.getMessage();
		System.out.println("Expected: " + expectedMsg);
		System.out.println("Actual  : " + expectedMsg);
		assertEquals(msg, expectedMsg, actualMsg);
	}

	/**
	 * Test the ces url.
	 */
	@Test
	public void checkCesUrlTest()
	{
		// test invalid URLs
		String input = "badurl";
		FormValidation validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertEquals(FormValidation.error(Messages.checkCesUrlInvalidError()).toString(), validation.toString());

		input = "www.myurl.combadurl";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertEquals(FormValidation.error(Messages.checkCesUrlInvalidError()).toString(), validation.toString());

		input = "www.myurl.com";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertEquals(FormValidation.error(Messages.checkCesUrlInvalidError()).toString(), validation.toString());

		// test valid URLs
		input = "https://www.myurl.com/";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertEquals(FormValidation.ok().toString(), validation.toString());

		input = "https://myurl.com";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertEquals(FormValidation.ok().toString(), validation.toString());
	}
	
	/**
	 * Test retrieves login information given a credentials identifier.
	 */
	@Test
	public void configure_system_credentials() throws Exception {

		final String credentialsId1 = "credsId1";
		final String credentialsId2 = "credsId2";
		final String username = "bob";
		final String password1 = "s$$cr3t";
		final String certUser = "CN=Margaret Smith, OU=Topaz, O=BMC, L=Detroit, ST=Michigan, C=US";

		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		StandardCredentials usernamePasswordCredentials = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
				credentialsId1, "sample", username, password1);

		StandardCredentials certificateCredentials = new CertificateCredentialsImpl(CredentialsScope.GLOBAL,
				credentialsId2, "sample", CERT_PASSWORD,
				new CertificateCredentialsImpl.FileOnMasterKeyStoreSource(getResourcePath(CERT_FILENAME)));

		FreeStyleProject project = j.createFreeStyleProject();
		CredentialsStore store = CredentialsProvider.lookupStores(Jenkins.getInstance()).iterator().next();
		store.addCredentials(Domain.global(), usernamePasswordCredentials);
		store.addCredentials(Domain.global(), certificateCredentials);

		StandardCredentials usernamePasswordCredentialsInfo = globalConfig.getLoginCredentials(project,	credentialsId1);
		UsernamePasswordCredentialsImpl credentials1 = (UsernamePasswordCredentialsImpl) usernamePasswordCredentialsInfo;
		assertEquals(credentials1.getId(), credentialsId1);						// NOSONAR
		assertEquals(credentials1.getUsername(), username);						// NOSONAR
		assertEquals(Secret.toString(credentials1.getPassword()), password1);	// NOSONAR

		StandardCredentials certificateCredentialsInfo = globalConfig.getLoginCredentials(project, credentialsId2);
		CertificateCredentialsImpl credentials2 = (CertificateCredentialsImpl) certificateCredentialsInfo;
		assertEquals(globalConfig.getCredentialsUser(credentials2), certUser);	// NOSONAR
		assertEquals(credentials2.getId(), credentialsId2);						// NOSONAR
		assertEquals(Secret.toString(credentials2.getPassword()), CERT_PASSWORD);	// NOSONAR

	}
	
	@Test
	public void getCertificateStr() throws Exception {

		final String credentialsId2 = "credsId2";
		final String sampleScriptInvoke = "sample.sh";
		final String cliVersion = "19.4.1";
		final String credentialsDescription = "sample";
		final String CMD_LINE = "sample.sh -host \"cw01\" -port \"1234\" -id \"\"CN=Margaret Smith, OU=Topaz, O=BMC, L=Detroit, ST=Michigan, C=US\"\" -certificate \"MIIEzjCCBHmgAwIBAgIEfRl9kTANBglghkgBZQMEAwIFADBpMQswCQYDVQQGEwJVUzERMA8GA1UECBMITWljaGlnYW4xEDAOBgNVBAcTB0RldHJvaXQxDDAKBgNVBAoTA0JNQzEOMAwGA1UECxMFVG9wYXoxFzAVBgNVBAMTDk1hcmdhcmV0IFNtaXRoMB4XDTIxMDUxOTE5MzYzNloXDTQ2MDUxMzE5MzYzNlowaTELMAkGA1UEBhMCVVMxETAPBgNVBAgTCE1pY2hpZ2FuMRAwDgYDVQQHEwdEZXRyb2l0MQwwCgYDVQQKEwNCTUMxDjAMBgNVBAsTBVRvcGF6MRcwFQYDVQQDEw5NYXJnYXJldCBTbWl0aDCCA0IwggI1BgcqhkjOOAQBMIICKAKCAQEAj3k12bmq6b+r7Yh6z0lRtvMuxZ47rzcY6OrElh8+/TYG50NRqcQYMzm4CefCrhxTm6dHW4XQEa24tHmHdUmEaVysDo8UszYIKKIv+icRCj1iqZNFNAmg/mlsRlj4S90ggZw3CaAQV7GVrc0AIz26VIS2KR+dZI74g0SGd5ec7AS0NKasLnXpmF3iPbApL8ERjJ/6nYGB5zONt5K3MNe540lZL2gJmHIVORXqPWuLRlPGM0WPgDsypMLg8nKQJW5OP4o7CDihxFDk4YwaKaN9316hQ95LZv8EkD7VzxYj4VjUh8YI6X8hHNgdyiPLbjgHZfgi40K+SEwFdjk5YBzWZwIdALr2lqaFePff3uf6Z8l3x4XvMrIzuuWAwLzVaV0CggEAFqZcWCBIUHBOdQKjl1cEDTTaOjR4wVTU5KXALSQu4E+W5h5L0JBKvayPN+6x4J8xgtI8kEPLZC+IAEFg7fnKCbMgdqecMqYn8kc+kYebosTnRL0ggVRMtVuALDaNH6g+1InpTg+gaI4yQopceMR4xo0FJ7ccmjq7CwvhLERoljnn08502xAaZaorh/ZMaCbbPscvS1WZg0u07bAvfJDppJbTpV1TW+v8RdT2GfY/Pe27hzklwvIk4HcxKW2oh+weR0j4fvtf3rdUhDFrIjLe5VPdrwIRKw0fAtowlzIk/ieu2oudSyki2bqL457Z4QOmPFKBC8aIt+LtQxbh7xfb3gOCAQUAAoIBAGJsJpFXqUau3tzWXFpc+MUZKFn7K9R/xyWwhmsZgR4ipIFmruVRoqG4XMyMe1z+8tI0Rt8RsbknTrA6OAUST4oukYlFNmLVgC7+Gu1nnBQJ5B3JFI4C6t8p7XIbdOxDqX6htyhDR81s+4HqojNUAEYPMJoAy6koe1JBU+8WuQhAROgHuozFDogyy9Efb/jZSfCm5DYTTZps2+gwX30UBhLVsZDRwYEdAj4/JZUyzZFyxKzxenuato3dC3Qy0foMdyZF/CPJB53p1pHrU2LWOcUcQQGmrfG29bO7jHdeCGyREN2saED0JuD9TppTrWEk8GzP4aEcRgkSW2B39LbdIsSjITAfMB0GA1UdDgQWBBQ1hkLO0+C4pKyOoMt1JnBOPzZSNzANBglghkgBZQMEAwIFAANAADA9AhwZuiLbEKBV+uHtSlOqywTwzqaQlufQmxJ984NyAh0ApX/SKgwUPmFu7aEqLp6yMWZm4SaxdasN86UJZg==\" -protocol TLSv1.2 -code 1047 -timeout \"0\"";

		FreeStyleProject project = j.createFreeStyleProject();
		
		KeyStoreSource keyStoreSource = new CertificateCredentialsImpl.FileOnMasterKeyStoreSource(getResourcePath(CERT_FILENAME));
		StandardCredentials certificateCredentials = new CertificateCredentialsImpl(CredentialsScope.GLOBAL,
				credentialsId2, credentialsDescription, CERT_PASSWORD, keyStoreSource);
		CredentialsStore store = CredentialsProvider.lookupStores(Jenkins.getInstance()).iterator().next();
		store.addCredentials(Domain.global(), certificateCredentials);

		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		globalConfig.addHostConnection(m_globalHostConnectionConfig);

		ArgumentListBuilder args = globalConfig.getArgumentBuilder(sampleScriptInvoke, cliVersion, project, credentialsId2,
				m_globalHostConnectionConfig.getConnectionId());
		assertEquals("Generated cmd line assertion failure!", CMD_LINE, args.toString());
	}
}
