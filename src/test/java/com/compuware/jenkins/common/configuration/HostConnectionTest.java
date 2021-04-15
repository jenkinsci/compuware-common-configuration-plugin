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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;

import org.apache.tools.ant.taskdefs.TempFile;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.JenkinsRule;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.CertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCertificateCredentials;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.Domain;
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl.KeyStoreSource;
import com.compuware.jenkins.common.configuration.HostConnection.DescriptorImpl;
import com.compuware.jenkins.common.utils.ArgumentUtils;
import com.compuware.jenkins.common.utils.CommonConstants;

import hudson.AbortException;
import hudson.model.FreeStyleProject;
import hudson.util.ArgumentListBuilder;
import hudson.util.FormValidation;
import hudson.util.FormValidation.Kind;
import hudson.util.Secret;
import jenkins.model.Jenkins;

/**
 * Class for testing the Compuware global host connection configuration.
 */
@SuppressWarnings("nls")
public class HostConnectionTest
{
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
		final String password2 = "secret";
		File certificate = tmp.newFile("a.certificate");

		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
		StandardCredentials usernamePasswordCredentials = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
				credentialsId1, "sample", username, password1);

		StandardCredentials certificateCredentials = new CertificateCredentialsImpl(CredentialsScope.GLOBAL,
				credentialsId2, "sample", password2,
				new CertificateCredentialsImpl.FileOnMasterKeyStoreSource(certificate.getAbsolutePath()));

		FreeStyleProject project = j.createFreeStyleProject();
		CredentialsStore store = CredentialsProvider.lookupStores(Jenkins.getInstance()).iterator().next();
		store.addCredentials(Domain.global(), usernamePasswordCredentials);
		store.addCredentials(Domain.global(), certificateCredentials);

		StandardCredentials usernamePasswordCredentialsInfo = globalConfig.getUserLoginInformation(project,
				credentialsId1);

		UsernamePasswordCredentialsImpl credentials1 = (UsernamePasswordCredentialsImpl) usernamePasswordCredentialsInfo;
		assertEquals(credentials1.getId(), credentialsId1);
		assertEquals(credentials1.getUsername(), username);
		assertEquals(Secret.toString(credentials1.getPassword()), password1);

		StandardCredentials certificateCredentialsInfo = globalConfig.getUserLoginInformation(project, credentialsId2);
		CertificateCredentialsImpl credentials2 = (CertificateCredentialsImpl) certificateCredentialsInfo;
		assertEquals(credentials2.getId(), credentialsId2);
		assertEquals(Secret.toString(credentials2.getPassword()), password2);

	}
	
//	We need getCertificateStr() to test globalConfig.getCertificate() using certificate of type (.pfx ).
//	We need to use a certificate of type (.pfx) because we use a default keystore of type "PKCS12".
//	The problem is we have to export the private Key in the certificate to make it of type ".pfx" and we can't add this certificate in our plugin for security since we publish this code in GitHub so I commented out getCertificateStr().
//	We can't run getCertificateStr() as part of HostConnectionTest since I didn't add the certificate that we need to use in the test but we can run this test manually after creating a certificate of type (.pfx )
//	this link shows how to do this (https://mindfulsoftware.com.au/blog/1409024/Converting-a-cer-file-to-pfx-using-the-Windows-MMC-snapin).
//	then add this certificate to compuware-common-configuration-plugin -> src/main/resources.	
//	
//	@Test
//	public void getCertificateStr() throws Exception {
//
//		final String credentialsId2 = "credsId2";
//
//		final String password2 = "changeit";
//
//		String fullCertPath = "/newCert.pfx";
//
//		URL is = this.getClass().getResource(fullCertPath);
//
////		InputStream b = this.getClass().getResourceAsStream(fullCertPath);
//
//		File file = new File(is.toString());
//
//		String path = is.toString().substring(6);
//
//		CpwrGlobalConfiguration globalConfig = CpwrGlobalConfiguration.get();
//
//		KeyStoreSource keyStoreSource = new CertificateCredentialsImpl.FileOnMasterKeyStoreSource(path);
//
//		StandardCredentials certificateCredentials = new CertificateCredentialsImpl(CredentialsScope.GLOBAL,
//				credentialsId2, "sample", password2, keyStoreSource);
//
//		CredentialsStore store = CredentialsProvider.lookupStores(Jenkins.getInstance()).iterator().next();
//
//		store.addCredentials(Domain.global(), certificateCredentials);
//
//		globalConfig.getCertificate((StandardCertificateCredentials) certificateCredentials);
//
//		ArgumentListBuilder args = globalConfig.ArgumentListBuilder("19.4.1", certificateCredentials, "protocol",
//				"codePage", "timeout", "host", "port");
//
//		System.out.println(args.toString());
//
//	}

}




