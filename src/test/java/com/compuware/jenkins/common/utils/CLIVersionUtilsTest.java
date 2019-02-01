/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 - 2019 Compuware Corporation
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions: The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
*/
package com.compuware.jenkins.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import org.mockito.Mockito;
import com.compuware.jenkins.common.configuration.Messages;

import hudson.AbortException;
import hudson.FilePath;

/**
 *	CLIVersionUtils unit test class.
 */
@SuppressWarnings("nls")
public class CLIVersionUtilsTest
{
	final String CLI_VERSION = "1.0.0";
	
	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.CLIVersionUtils#checkCLICompatibility(hudson.FilePath, java.lang.String)}.
	 *
	 */
	@Test
	public void testCheckCLICompatibility()
	{
		try
		{	
			testCliDirectoryNotExist();
			testVersionFileNotExist();
			testParseXml();
		}
		catch (IOException | InterruptedException e)
		{
			fail("Unexpected exception occured.");
		}
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.CLIVersionUtils#checkProtocolSupported(java.lang.String)}
	 * 
	 */
	@Test
	public void testCheckProtocolNotSupported() throws IOException, InterruptedException
	{
		final FilePath installPath = Mockito.mock(FilePath.class);
		final FilePath versionFilePath = Mockito.mock(FilePath.class);
		
		Mockito.when(installPath.exists()).thenReturn(true);
		Mockito.when(versionFilePath.exists()).thenReturn(true);
		
		Mockito.when(installPath.child(installPath.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE)).thenReturn(versionFilePath);

		try
		{
			String equalVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
			equalVersionXml += "<product name=\"Compuware Topaz\" version=\"1.0.0\" build=\"188\"/>";
			InputStream inputStream = new ByteArrayInputStream(equalVersionXml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(inputStream);

			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLIVersionUtils.HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION);
			CLIVersionUtils.checkProtocolSupported(cliVersion);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.hostConnectionProtocolCliVersionError(CLI_VERSION, CLIVersionUtils.HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION), e.getMessage());
		}
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.CLIVersionUtils#checkProtocolSupported(java.lang.String)}
	 * 
	 */
	@Test
	public void testCheckProtocolSupported() throws IOException, InterruptedException
	{
		final FilePath installPath = Mockito.mock(FilePath.class);
		final FilePath versionFilePath = Mockito.mock(FilePath.class);
		
		Mockito.when(installPath.exists()).thenReturn(true);
		Mockito.when(versionFilePath.exists()).thenReturn(true);
		
		Mockito.when(installPath.child(installPath.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE)).thenReturn(versionFilePath);

		String equalVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		equalVersionXml += "<product name=\"Compuware Topaz\" version=\"19.4.1\" build=\"188\"/>";
		InputStream inputStream = new ByteArrayInputStream(equalVersionXml.getBytes());
		Mockito.when(versionFilePath.read()).thenReturn(inputStream);

		String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLIVersionUtils.HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION);
		CLIVersionUtils.checkProtocolSupported(cliVersion);
	}

	private void testCliDirectoryNotExist() throws IOException, InterruptedException
	{
		final FilePath installPath = Mockito.mock(FilePath.class);
		
		Mockito.when(installPath.exists()).thenReturn(false);
		
		try
		{
			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLI_VERSION);
			CLIVersionUtils.checkCLICompatibility(cliVersion, CLI_VERSION);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.cliNotInstalledError(), e.getMessage());		
		}
	}
	
	private void testVersionFileNotExist() throws IOException, InterruptedException
	{
		final FilePath installPath = Mockito.mock(FilePath.class);
		final FilePath versionFilePath = Mockito.mock(FilePath.class);
		
		Mockito.when(installPath.exists()).thenReturn(true);
		Mockito.when(versionFilePath.exists()).thenReturn(false);
		
		Mockito.when(installPath.child(installPath.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE)).thenReturn(versionFilePath);
		
		try
		{
			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLI_VERSION);
			CLIVersionUtils.checkCLICompatibility(cliVersion, CLI_VERSION);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.cliOldUnknownVersionError(CLI_VERSION), e.getMessage());
		}
	}
	
	private void testParseXml() throws IOException, InterruptedException
	{
		final FilePath installPath = Mockito.mock(FilePath.class);
		final FilePath versionFilePath = Mockito.mock(FilePath.class);
		
		Mockito.when(installPath.exists()).thenReturn(true);
		Mockito.when(versionFilePath.exists()).thenReturn(true);
		Mockito.when(installPath.child(installPath.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE)).thenReturn(versionFilePath);
		
		try
		{	
			String equalVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
			equalVersionXml += "<product name=\"Compuware Topaz\" version=\"1.0.0\" build=\"188\"/>";
			InputStream inputStream = new ByteArrayInputStream(equalVersionXml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(inputStream);
			
			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLI_VERSION);
			CLIVersionUtils.checkCLICompatibility(cliVersion, CLI_VERSION);
			
			String newerVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
			newerVersionXml += "<product name=\"Compuware Topaz\" version=\"1.0.1\" build=\"188\"/>";
			inputStream = new ByteArrayInputStream(newerVersionXml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(inputStream);
			
			cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLI_VERSION);
			CLIVersionUtils.checkCLICompatibility(cliVersion, CLI_VERSION);
		}
		catch (AbortException e)
		{
			fail("Expected version.xml to match minimum version.");
		}
		
		String oldVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		oldVersionXml += "<product name=\"Compuware Topaz\" version=\"0.1.0\" build=\"188\"/>";
		testXmlAbortException(oldVersionXml, installPath, versionFilePath, CLI_VERSION, "0.1.0");

		String emptyVersionXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		emptyVersionXml += "<product name=\"Compuware Topaz\" version=\"\" build=\"188\"/>";	
		testXmlAbortException(emptyVersionXml, installPath, versionFilePath, CLI_VERSION);
		
		String noVersionTagXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
		noVersionTagXml += "<product name=\"Compuware Topaz\" build=\"188\"/>";	
		testXmlAbortException(noVersionTagXml, installPath, versionFilePath, CLI_VERSION);
		
		try
		{
			//XML missing close tag
			String badXml =  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";
			badXml += "<product name=\"Compuware Topaz\" version=\"1.0.0\" build=\"188\"";
			
			InputStream badInputStream = new ByteArrayInputStream(badXml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(badInputStream);
			
			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, CLI_VERSION);
			CLIVersionUtils.checkCLICompatibility(cliVersion, CLI_VERSION);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.cliParseXmlError(), e.getMessage());
		}
	}
	
	private void testXmlAbortException(String xml, FilePath installPath, FilePath versionFilePath, String version) throws IOException, InterruptedException
	{
		try
		{
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(inputStream);
			
			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, version);
			CLIVersionUtils.checkCLICompatibility(cliVersion, version);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.cliOldUnknownVersionError(version), e.getMessage());
		}
	}

	private void testXmlAbortException(String xml, FilePath installPath, FilePath versionFilePath, String version,
			String installedVersion) throws IOException, InterruptedException
	{
		try
		{
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
			Mockito.when(versionFilePath.read()).thenReturn(inputStream);

			String cliVersion = CLIVersionUtils.getCLIVersion(installPath, version);
			CLIVersionUtils.checkCLICompatibility(cliVersion, version);
			fail("Expected an AbortException to occur.");
		}
		catch (AbortException e)
		{
			assertEquals(Messages.cliOldVersionError(installedVersion, version), e.getMessage());
		}
	}
}
