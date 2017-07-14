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

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.TaskListener;
import hudson.remoting.Channel;

/**
 * Class for testing the Compuware global configurations.
 */
@SuppressWarnings("nls")
public class CpwrGlobalConfigurationTest
{
	@Rule
	public JenkinsRule j = new JenkinsRule();

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
		String expectedLocation = "C:\test\folder";
		m_globalConfig.setTopazCLILocationWindows(expectedLocation);
		String actualLocation = m_globalConfig.getTopazCLILocation(windowsLauncher);
		assertEquals("Expected windows location.", expectedLocation, actualLocation);
	}

	/**
	 * Test the linux CLI location.
	 */
	@Test
	public void cliLocationLinuxTest()
	{
		TestLauncher linuxLauncher = new TestLauncher(true);
		String expectedLocation = "opt\test\folder";
		m_globalConfig.setTopazCLILocationLinux(expectedLocation);
		String actualLocation = m_globalConfig.getTopazCLILocation(linuxLauncher);
		assertEquals("Expected linux location.", expectedLocation, actualLocation);
	}

//	/**
//	 * Test saving and reloading of a host connection does not alter the initial connection id.
//	 */
//	@Test
//	public void connectionIdTest()
//	{
//		String expectedAlteredHostConnDescription = "HCI Production";
//
//		String expectedHostConnDescription = "HCI Prod";
//		String expectedHostConnHostPort = "cw01:30947";
//		String expectedHostConnCodePage = "1047";
//		String expectedHostConnConnectionId = "9876";
//
//		String expectedTopazCLILocationLinux = "/opt/Compuware/TopazCLI";
//		String expectedTopazCLILocationWindows = "C:\\Program Files\\Compuware\\Topaz Workbench CLI";
//
//		try
//		{
//			JSONObject json = new JSONObject();
//
//			JSONObject hostConnection = new JSONObject();
//			hostConnection.put("description", expectedHostConnDescription);
//			hostConnection.put("hostPort", expectedHostConnHostPort);
//			hostConnection.put("codePage", expectedHostConnCodePage);
//			hostConnection.put("connectionId", expectedHostConnConnectionId);
//
//			JSONArray hostConnections = new JSONArray();
//			hostConnections.add(hostConnection);
//
//			json.put("hostConn", hostConnections);
//
//			json.put("topazCLILocationWindows", expectedTopazCLILocationWindows);
//			json.put("topazCLILocationLinux", expectedTopazCLILocationLinux);
//
//			CpwrGlobalConfiguration before = new CpwrGlobalConfiguration();
//			before.configure(Stapler.getCurrentRequest(), json);
//
//			CpwrGlobalConfiguration after = new CpwrGlobalConfiguration();
//			after.load();
//
//			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", expectedHostConnConnectionId),
//					after.getHostConnections()[0].getConnectionId(), equalTo(expectedHostConnConnectionId));
//
//			// Change the description, save and reload again to make sure the description changed, but the connection id
//			// remained the same.
//			((JSONObject) json.getJSONArray("hostConn").get(0)).put("description", expectedAlteredHostConnDescription);
//			after.configure(Stapler.getCurrentRequest(), json);
//
//			CpwrGlobalConfiguration after2 = new CpwrGlobalConfiguration();
//			after2.load();
//
//			assertThat(
//					String.format("Expected configuration to contain description: \"%s\".", expectedAlteredHostConnDescription),
//					after.getHostConnections()[0].getDescription(), equalTo(expectedAlteredHostConnDescription));
//
//			assertThat(String.format("Expected configuration to contain connection id: \"%s\".", expectedHostConnConnectionId),
//					after.getHostConnections()[0].getConnectionId(), equalTo(expectedHostConnConnectionId));
//		}
//		catch (Exception e)
//		{
//			fail(e.getMessage());
//		}
//	}

//	/**
//	 * Perform a round trip test on the global configuration.
//	 * 
//	 * <p>
//	 * A configuration is created, configured, submitted / saved, and reloaded where the original configuration is compared
//	 * against the reloaded configuration for equality.
//	 */
//	@Test
//	public void roundTripTest()
//	{
//		String expectedHostConnDescription_1 = "HCI Prod";
//		String expectedHostConnHostPort_1 = "cw01:30947";
//		String expectedHostConnCodePage_1 = "1047";
//		String expectedHostConnConnectionId_1 = "1234";
//
//		String expectedHostConnDescription_2 = "HCI Dev";
//		String expectedHostConnHostPort_2 = "cw01:20947";
//		String expectedHostConnCodePage_2 = "993";
//		String expectedHostConnConnectionId_2 = "4567";
//
//		String expectedTopazCLILocationLinux = "/opt/Compuware/TopazCLI";
//		String expectedTopazCLILocationWindows = "C:\\Program Files\\Compuware\\Topaz Workbench CLI";
//
//		try
//		{
//			JSONObject json = new JSONObject();
//
//			JSONObject hostConnection = new JSONObject();
//			hostConnection.put("description", expectedHostConnDescription_1);
//			hostConnection.put("hostPort", expectedHostConnHostPort_1);
//			hostConnection.put("codePage", expectedHostConnCodePage_1);
//			hostConnection.put("connectionId", expectedHostConnConnectionId_1);
//
//			JSONArray hostConnections = new JSONArray();
//			hostConnections.add(hostConnection);
//
//			hostConnection = new JSONObject();
//			hostConnection.put("description", expectedHostConnDescription_2);
//			hostConnection.put("hostPort", expectedHostConnHostPort_2);
//			hostConnection.put("codePage", expectedHostConnCodePage_2);
//			hostConnection.put("connectionId", expectedHostConnConnectionId_2);
//			hostConnections.add(hostConnection);
//
//			json.put("hostConn", hostConnections);
//
//			json.put("topazCLILocationWindows", expectedTopazCLILocationWindows);
//			json.put("topazCLILocationLinux", expectedTopazCLILocationLinux);
//
//			CpwrGlobalConfiguration before = new CpwrGlobalConfiguration();
//			before.configure(Stapler.getCurrentRequest(), json);
//
//			CpwrGlobalConfiguration after = new CpwrGlobalConfiguration();
//			after.load();
//
//			assertThat(String.format("Expected configuration to contain description: \"%s\".", expectedHostConnDescription_1),
//					after.getHostConnections()[0].getDescription(), equalTo(expectedHostConnDescription_1));
//
//			assertThat(String.format("Expected configuration to contain host:port: \"%s\".", expectedHostConnHostPort_1),
//					after.getHostConnections()[0].getHostPort(), equalTo(expectedHostConnHostPort_1));
//
//			assertThat(String.format("Expected configuration to contain code page: \"%s\".", expectedHostConnCodePage_1),
//					after.getHostConnections()[0].getCodePage(), equalTo(expectedHostConnCodePage_1));
//
//			assertThat(
//					String.format("Expected configuration to contain connection id: \"%s\".", expectedHostConnConnectionId_1),
//					after.getHostConnections()[0].getConnectionId(), equalTo(expectedHostConnConnectionId_1));
//
//			assertThat(String.format("Expected configuration to contain description: \"%s\".", expectedHostConnDescription_2),
//					after.getHostConnections()[1].getDescription(), equalTo(expectedHostConnDescription_2));
//
//			assertThat(String.format("Expected configuration to contain host:port: \"%s\".", expectedHostConnHostPort_2),
//					after.getHostConnections()[1].getHostPort(), equalTo(expectedHostConnHostPort_2));
//
//			assertThat(String.format("Expected configuration to contain code page: \"%s\".", expectedHostConnCodePage_2),
//					after.getHostConnections()[1].getCodePage(), equalTo(expectedHostConnCodePage_2));
//
//			assertThat(
//					String.format("Expected configuration to contain connection id: \"%s\".", expectedHostConnConnectionId_2),
//					after.getHostConnections()[1].getConnectionId(), equalTo(expectedHostConnConnectionId_2));
//
//			assertThat(
//					String.format("Expected configuration to contain Topaz CLI location Windows: \"%s\".",
//							expectedTopazCLILocationWindows),
//					after.getTopazCLILocationWindows(), equalTo(expectedTopazCLILocationWindows));
//
//			assertThat(
//					String.format("Expected configuration to contain Topaz CLI location Linux: \"%s\".",
//							expectedTopazCLILocationLinux),
//					after.getTopazCLILocationLinux(), equalTo(expectedTopazCLILocationLinux));
//		}
//		catch (Exception e)
//		{
//			fail(e.getMessage());
//		}
//	}

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

		/* (non-Javadoc)
		 * @see hudson.Launcher#launch(hudson.Launcher.ProcStarter)
		 */
		@Override
		public Proc launch(ProcStarter starter) throws IOException
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see hudson.Launcher#launchChannel(java.lang.String[], java.io.OutputStream, hudson.FilePath, java.util.Map)
		 */
		@Override
		public Channel launchChannel(String[] cmd, OutputStream out, FilePath workDir, Map<String, String> envVars)
				throws IOException, InterruptedException
		{
			return null;
		}

		/* (non-Javadoc)
		 * @see hudson.Launcher#kill(java.util.Map)
		 */
		@Override
		public void kill(Map<String, String> modelEnvVars) throws IOException, InterruptedException
		{
		}

		/* (non-Javadoc)
		 * @see hudson.Launcher#isUnix()
		 */
		@Override
		public boolean isUnix()
		{
			return m_isUnix;
		}
	}
}
