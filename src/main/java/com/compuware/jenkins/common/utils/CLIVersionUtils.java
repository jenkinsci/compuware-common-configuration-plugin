/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2017 Compuware Corporation
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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.compuware.jenkins.common.configuration.Messages;

import hudson.AbortException;
import hudson.FilePath;

/**
 *	Utility class used to check the version of the installed Topaz CLI.
 */
public class CLIVersionUtils
{
	/**
	 * Private constructor.
	 * <p>
	 * All methods should be called statically.
	 */
	private CLIVersionUtils()
	{
	}
	
	/**
	 * Checks if the version of the installed Topaz CLI is greater or equal to the Jenkins plugins
	 * required version. This will abort the Jenkins job if CLI is not compatible.
	 * 
	 * @param cliDirectory
	 * 		FilePath of the Topaz CLI install directory
	 * @param minimumVersion
	 * 		Minimum required CLI version of the Jenkins plugin
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void checkCLICompatibility(FilePath cliDirectory, String minimumVersion) throws IOException, InterruptedException
	{	
		if (!cliDirectory.exists())
		{
			throw new AbortException(Messages.cliNotInstalledError());
		}
		
		String version = getCLIVersion(cliDirectory, minimumVersion);
		
		if (compareVersions(version, minimumVersion) != 0)
		{
			throw new AbortException(Messages.cliOldVersionError(minimumVersion));
		}
	}
	
	/*
	 * 	Reads the version.xml file from the CLI directory and gets the version number
	 */
	private static String getCLIVersion(FilePath cliDirectory, String minimumVersion) throws IOException, InterruptedException
	{
		String version = "";
		
		FilePath versionFilePath = cliDirectory.child(cliDirectory.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE);
		
		if (!versionFilePath.exists())
		{
			throw new AbortException(Messages.cliOldVersionError(minimumVersion));
		}
				
		version = parseXml(versionFilePath.read());
		
		return version;
	}
	
	/*
	 * 	Compares the CLI version with the plugins required version
	 */
	private static int compareVersions(String cliVersion, String minimumVersion)
	{
		if (cliVersion.equalsIgnoreCase(""))
		{
			return 1;
		}
		
		int cliVersionNum = Integer.parseInt(cliVersion.replaceAll("\\.", ""));
		int minimumVersionNum = Integer.parseInt(minimumVersion.replaceAll("\\.", ""));
		
		return (cliVersionNum < minimumVersionNum) ? 1 : 0;
	}
	
	/*
	 * 	Parses the version.xml and converts "version" attribute to a version number
	 */
	private static String parseXml(InputStream versionfile) throws IOException
	{
		String version = "";
		
		try
		{
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document document = dBuilder.parse(versionfile);
		    
		    version = document.getDocumentElement().getAttribute("version");
		}
		catch (ParserConfigurationException | SAXException e)
		{
			throw new AbortException(Messages.cliParseXmlError());
		}
		
		return version;
	}

}
