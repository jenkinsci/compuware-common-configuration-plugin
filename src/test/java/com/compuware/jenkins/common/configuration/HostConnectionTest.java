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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
@SuppressWarnings("nls")
public class HostConnectionTest {
	
	private static final String CERT_FILENAME = "/topaz-test.p12";
	private static final String CERT_PASSWORD = "compuware";
	
	@Rule
	public JenkinsRule j = new JenkinsRule();
	
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
		assertThat("Expecting valid host port.", validation.kind, is(equalTo(Kind.OK)));
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
		assertThat(msg, actualMsg, is(equalTo(expectedMsg)));
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
		assertThat("Expecting valid connection name.", validation.kind, is(equalTo(Kind.OK)));
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
		assertThat(msg, actualMsg, is(equalTo(expectedMsg)));
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
		assertThat("Expecting valid timeout when nothing is specified.", validation.kind, is(equalTo(Kind.OK)));

		// valid timeout
		input = "10";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckTimeout(input);
		assertThat("Expecting valid timeout when a whole, positive integer is specfied.", validation.kind, is(equalTo(Kind.OK)));
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
		assertThat(msg, actualMsg, is(equalTo(expectedMsg)));
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
		assertThat(validation.toString(), is(equalTo(FormValidation.error(Messages.checkCesUrlInvalidError()).toString())));

		input = "www.myurl.combadurl";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertThat(validation.toString(), is(equalTo(FormValidation.error(Messages.checkCesUrlInvalidError()).toString())));

		input = "www.myurl.com";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertThat(validation.toString(), is(equalTo(FormValidation.error(Messages.checkCesUrlInvalidError()).toString())));

		// test valid URLs
		input = "https://www.myurl.com/";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertThat(validation.toString(), is(equalTo(FormValidation.ok().toString())));

		input = "https://myurl.com";
		validation = ((DescriptorImpl) m_globalHostConnectionConfig.getDescriptor()).doCheckCesUrl(input);
		assertThat(validation.toString(), is(equalTo(FormValidation.ok().toString())));
	}
	
	/**
	 * Test retrieves login information given a credentials identifier.
	 */
	@Test
	public void configureSystemCredentialsTest() throws Exception {

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
		assertThat(credentials1.getId(), is(equalTo(credentialsId1)));
		assertThat(credentials1.getUsername(), is(equalTo(username)));
		assertThat(Secret.toString(credentials1.getPassword()), is(equalTo(password1)));

		StandardCredentials certificateCredentialsInfo = globalConfig.getLoginCredentials(project, credentialsId2);
		CertificateCredentialsImpl credentials2 = (CertificateCredentialsImpl) certificateCredentialsInfo;
		assertThat(globalConfig.getCredentialsUser(credentials2), is(equalTo(certUser)));
		assertThat(credentials2.getId(), is(equalTo(credentialsId2)));
		assertThat(Secret.toString(credentials2.getPassword()), is(equalTo(CERT_PASSWORD)));

	}
	
	@Test
	public void getCertificateStrTest() throws Exception {

		final String credentialsId2 = "credsId2";
		final String sampleScriptInvoke = "sample.sh";
		final String cliVersion = "19.4.1";
		final String credentialsDescription = "sample";
		final String CMD_LINE = "sample.sh -host \"cw01\" -port \"1234\" -protocol TLSv1.2 -code 1047 -timeout \"0\" -certificate ******";

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
		assertThat("Generated cmd line assertion failure!", args.toString(), is(equalTo(CMD_LINE)));
	}
}
