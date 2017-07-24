/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 Compuware Corporation
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.Stapler;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.TaskListener;
import hudson.remoting.Channel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Class for testing the Compuware global configurations.
 */
@SuppressWarnings("nls")
public class CpwrGlobalConfigurationTest
{
	private static final String EXPECTED_CONNECTION_DESCRIPTION_HCI_PROD = "HCI Prod";
	private static final String EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION = "HCI Production";
	private static final String EXPECTED_HOST_CW01 = "cw01";
	private static final String EXPECTED_PORT_30947 = "30947";
	private static final String EXPECTED_PORT_20947 = "20947";
	private static final String EXPECTED_CODE_PAGE_1047 = "1047";
	private static final String EXPECTED_CODE_PAGE_993 = "993";
	private static final String EXPECTED_CONNECTION_ID = "9876";
	private static final String EXPECTED_CONNECTION_ID_2 = "4567";
	private static final String EXPECTED_TOPAZ_CLI_LOCATION_LINUX = "/opt/Compuware/TopazCLI";
	private static final String EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS = "C:\\Program Files\\Compuware\\Topaz Workbench CLI";

	@Rule
	public JenkinsRule m_jenkinsRule = new JenkinsRule();

	private CpwrGlobalConfiguration m_globalConfig;

	@Before
	public void setUp()
	{
		m_globalConfig = new CpwrGlobalConfiguration();
	}

	/**
	 * Test the windows CLI location.
	 */
	@Test
	public void cliLocationWindowsTest()
	{
		TestLauncher windowsLauncher = new TestLauncher(false);
		m_globalConfig.setTopazCLILocationWindows(EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS);
		String actualLocation = m_globalConfig.getTopazCLILocation(windowsLauncher);
		assertEquals("Expected windows location.", EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS, actualLocation);
	}

	/**
	 * Test the linux CLI location.
	 */
	@Test
	public void cliLocationLinuxTest()
	{
		TestLauncher linuxLauncher = new TestLauncher(true);
		m_globalConfig.setTopazCLILocationLinux(EXPECTED_TOPAZ_CLI_LOCATION_LINUX);
		String actualLocation = m_globalConfig.getTopazCLILocation(linuxLauncher);
		assertEquals("Expected linux location.", EXPECTED_TOPAZ_CLI_LOCATION_LINUX, actualLocation);
	}

	/**
	 * Test saving and reloading of a host connection does not alter the initial connection id.
	 */
	@Test
	public void connectionIdTest()
	{
		try
		{
			JSONObject json = new JSONObject();

			JSONObject hostConnection = new JSONObject();
			hostConnection.put("description", EXPECTED_CONNECTION_DESCRIPTION_HCI_PROD);
			hostConnection.put("hostPort", EXPECTED_HOST_CW01 + ':' + EXPECTED_PORT_30947);
			hostConnection.put("codePage", EXPECTED_CODE_PAGE_1047);
			hostConnection.put("connectionId", EXPECTED_CONNECTION_ID);

			JSONArray hostConnections = new JSONArray();
			hostConnections.add(hostConnection);

			json.put("hostConn", hostConnections);

			json.put("topazCLILocationWindows", EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS);
			json.put("topazCLILocationLinux", EXPECTED_TOPAZ_CLI_LOCATION_LINUX);

			CpwrGlobalConfiguration before = new CpwrGlobalConfiguration();
			before.configure(Stapler.getCurrentRequest(), json);

			CpwrGlobalConfiguration after = new CpwrGlobalConfiguration();
			after.load();

			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", EXPECTED_CONNECTION_ID),
					after.getHostConnections()[0].getConnectionId(), equalTo(EXPECTED_CONNECTION_ID));

			// Change the description, save and reload again to make sure the description changed, but the connection id
			// remained the same.
			((JSONObject) json.getJSONArray("hostConn").get(0)).put("description",
					EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION);
			after.configure(Stapler.getCurrentRequest(), json);

			CpwrGlobalConfiguration after2 = new CpwrGlobalConfiguration();
			after2.load();

			assertThat(
					String.format("Expected configuration to contain description: \"%s\".",
							EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION),
					after.getHostConnections()[0].getDescription(), equalTo(EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION));

			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", EXPECTED_CONNECTION_ID),
					after.getHostConnections()[0].getConnectionId(), equalTo(EXPECTED_CONNECTION_ID));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Perform a round trip test on the global configuration.
	 * <p>
	 * A configuration is created, configured, submitted / saved, and reloaded where the original configuration is compared
	 * against the reloaded configuration for equality.
	 */
	@Test
	public void roundTripTest()
	{
		try
		{
			JSONObject json = new JSONObject();

			JSONObject hostConnection = new JSONObject();
			hostConnection.put("description", EXPECTED_CONNECTION_DESCRIPTION_HCI_PROD);

			String expectedHostPortStr1 = EXPECTED_HOST_CW01 + ':' + EXPECTED_PORT_30947;
			hostConnection.put("hostPort", expectedHostPortStr1);

			hostConnection.put("codePage", EXPECTED_CODE_PAGE_1047);
			hostConnection.put("connectionId", EXPECTED_CONNECTION_ID);

			JSONArray hostConnections = new JSONArray();
			hostConnections.add(hostConnection);

			hostConnection = new JSONObject();
			hostConnection.put("description", EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION);

			String expectedHostPortStr2 = EXPECTED_HOST_CW01 + ':' + EXPECTED_PORT_20947;
			hostConnection.put("hostPort", expectedHostPortStr2);

			hostConnection.put("codePage", EXPECTED_CODE_PAGE_993);
			hostConnection.put("connectionId", EXPECTED_CONNECTION_ID_2);
			hostConnections.add(hostConnection);

			json.put("hostConn", hostConnections);

			json.put("topazCLILocationWindows", EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS);
			json.put("topazCLILocationLinux", EXPECTED_TOPAZ_CLI_LOCATION_LINUX);

			CpwrGlobalConfiguration before = new CpwrGlobalConfiguration();
			before.configure(Stapler.getCurrentRequest(), json);

			CpwrGlobalConfiguration after = new CpwrGlobalConfiguration();
			after.load();

			assertThat(
					String.format("Expected configuration to contain description: \"%s\".",
							EXPECTED_CONNECTION_DESCRIPTION_HCI_PROD),
					after.getHostConnections()[0].getDescription(), equalTo(EXPECTED_CONNECTION_DESCRIPTION_HCI_PROD));

			assertThat(String.format("Expected configuration to contain host: \"%s\".", EXPECTED_HOST_CW01),
					after.getHostConnections()[0].getHost(), equalTo(EXPECTED_HOST_CW01));

			assertThat(String.format("Expected configuration to contain port: \"%s\".", EXPECTED_PORT_30947),
					after.getHostConnections()[0].getPort(), equalTo(EXPECTED_PORT_30947));

			assertThat(String.format("Expected configuration to contain host:port: \"%s\".", expectedHostPortStr1),
					after.getHostConnections()[0].getHostPort(), equalTo(expectedHostPortStr1));

			assertThat(String.format("Expected configuration to contain code page: \"%s\".", EXPECTED_CODE_PAGE_1047),
					after.getHostConnections()[0].getCodePage(), equalTo(EXPECTED_CODE_PAGE_1047));

			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", EXPECTED_CONNECTION_ID),
					after.getHostConnections()[0].getConnectionId(), equalTo(EXPECTED_CONNECTION_ID));

			assertThat(
					String.format("Expected configuration to contain description: \"%s\".",
							EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION),
					after.getHostConnections()[1].getDescription(), equalTo(EXPECTED_CONNECTION_DESCRIPTION_HCI_PRODUCTION));

			assertThat(String.format("Expected configuration to contain host: \"%s\".", EXPECTED_HOST_CW01),
					after.getHostConnections()[1].getHost(), equalTo(EXPECTED_HOST_CW01));

			assertThat(String.format("Expected configuration to contain port: \"%s\".", EXPECTED_PORT_20947),
					after.getHostConnections()[1].getPort(), equalTo(EXPECTED_PORT_20947));

			assertThat(String.format("Expected configuration to contain host:port: \"%s\".", expectedHostPortStr2),
					after.getHostConnections()[1].getHostPort(), equalTo(expectedHostPortStr2));

			assertThat(String.format("Expected configuration to contain code page: \"%s\".", EXPECTED_CODE_PAGE_993),
					after.getHostConnections()[1].getCodePage(), equalTo(EXPECTED_CODE_PAGE_993));

			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", EXPECTED_CONNECTION_ID_2),
					after.getHostConnections()[1].getConnectionId(), equalTo(EXPECTED_CONNECTION_ID_2));

			assertThat(
					String.format("Expected configuration to contain Topaz CLI location Windows: \"%s\".",
							EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS),
					after.getTopazCLILocationWindows(), equalTo(EXPECTED_TOPAZ_CLI_LOCATION_WINDOWS));

			assertThat(
					String.format("Expected configuration to contain Topaz CLI location Linux: \"%s\".",
							EXPECTED_TOPAZ_CLI_LOCATION_LINUX),
					after.getTopazCLILocationLinux(), equalTo(EXPECTED_TOPAZ_CLI_LOCATION_LINUX));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Test launcher
	 */
	private class TestLauncher extends Launcher
	{
		private boolean m_isUnix;

		public TestLauncher(boolean isUnix)
		{
			super(TaskListener.NULL, null);
			m_isUnix = isUnix;
		}

		/* 
		 * (non-Javadoc)
		 * @see hudson.Launcher#launch(hudson.Launcher.ProcStarter)
		 */
		@Override
		public Proc launch(ProcStarter starter) throws IOException
		{
			return null;
		}

		/* 
		 * (non-Javadoc)
		 * @see hudson.Launcher#launchChannel(java.lang.String[], java.io.OutputStream, hudson.FilePath, java.util.Map)
		 */
		@Override
		public Channel launchChannel(String[] cmd, OutputStream out, FilePath workDir, Map<String, String> envVars)
				throws IOException, InterruptedException
		{
			return null;
		}

		/* 
		 * (non-Javadoc)
		 * @see hudson.Launcher#kill(java.util.Map)
		 */
		@Override
		public void kill(Map<String, String> modelEnvVars) throws IOException, InterruptedException
		{
		}

		/* 
		 * (non-Javadoc)
		 * @see hudson.Launcher#isUnix()
		 */
		@Override
		public boolean isUnix()
		{
			return m_isUnix;
		}
	}
}