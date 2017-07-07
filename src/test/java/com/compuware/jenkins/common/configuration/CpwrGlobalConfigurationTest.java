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
