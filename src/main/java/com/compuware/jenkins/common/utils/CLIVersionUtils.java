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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
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

	public static final String HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION = "19.4.1"; //$NON-NLS-1$

	/**
	 * Private constructor.
	 * <p>
	 * All methods should be called statically.
	 */
	private CLIVersionUtils()
	{
	}
	
	/**
	 * Reads the version.xml file from the CLI directory and gets the version number.
	 * 
	 * @param cliDirectory
	 * 		FilePath of the Topaz CLI install directory
	 * @param minimumVersion
	 * 		Minimum required CLI version of the Jenkins plugin
	 * 
	 * @return the CLI version
	 * @throws IOException if the CLI directory or the version file do not exist
	 * @throws InterruptedException if retrieval of CLI version is interrupted by another thread
	 */
	public static String getCLIVersion(FilePath cliDirectory, String minimumVersion) throws IOException, InterruptedException
	{
		String version = ""; //$NON-NLS-1$
		
		if (!cliDirectory.exists())
		{
			throw new AbortException(Messages.cliNotInstalledError());
		}

		FilePath versionFilePath = cliDirectory.child(cliDirectory.getRemote() + CommonConstants.SLASH + CommonConstants.VERSION_FILE);
		
		if (!versionFilePath.exists())
		{
			throw new AbortException(Messages.cliOldUnknownVersionError(minimumVersion));
		}
				
		version = parseXml(versionFilePath.read());
		
		return version;
	}

	/**
	 * Checks if the version of the installed Topaz CLI is greater or equal to the Jenkins plugins required version. This will abort the
	 * Jenkins job if CLI is not compatible.
	 * 
	 * @param version
	 *            current CLI version
	 * @param minimumVersion
	 *            minimum required CLI version of the Jenkins plugin
	 * 
	 * @throws IOException
	 *             if an I/O error occurs checking directory existence or getting the CLI version
	 * @throws InterruptedException
	 *             if checking directory existence or getting the CLI version is interrupted by another thread
	 */
	public static void checkCLICompatibility(String version, String minimumVersion) throws IOException, InterruptedException
	{
		if (StringUtils.isEmpty(version))
		{
			throw new AbortException(Messages.cliOldUnknownVersionError(minimumVersion));
		}
		else if (compareVersions(version, minimumVersion) < 0)
		{
			throw new AbortException(Messages.cliOldVersionError(version, minimumVersion));
		}
	}

	/**
	 * Checks if the version of the installed Topaz CLI is greater or equal to the Jenkins plugins required version. This will abort the
	 * Jenkins job if CLI is not compatible.
	 * 
	 * @param cliDirectory
	 *            FilePath of the Topaz CLI install directory
	 * @param minimumVersion
	 *            minimum required CLI version of the Jenkins plugin
	 * 
	 * @throws IOException
	 *             if an I/O error occurs checking directory existence or getting the CLI version
	 * @throws InterruptedException
	 *             if checking directory existence or getting the CLI version is interrupted by another thread
	 */
	public static void checkCLICompatibility(FilePath cliDirectory, String minimumVersion) throws IOException, InterruptedException {
		String version = getCLIVersion(cliDirectory, minimumVersion);
		checkCLICompatibility(version, minimumVersion);
	}

	/**
	 * Checks if encryption protocol is supported on a host connection for the specified CLI version.
	 * <p>
	 * The Jenkins job will be aborted if the CLI does not support encryption protocol.
	 * 
	 * @param cliVersion the CLI version to check
	 * 
	 * @throws AbortException if the encryption protocol is not supported
	 */
	public static void checkProtocolSupported(String cliVersion) throws AbortException 
	{
		if (compareVersions(cliVersion, HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION) < 0)
		{
			throw new AbortException(Messages.hostConnectionProtocolCliVersionError(cliVersion, HOST_CONNECTION_PROTOCOL_MINIMUM_VERSION));
		}
	}

	/*
	 * 	Compares the CLI version with the plugins required version
	 */
	private static int compareVersions(String cliVersion, String minimumVersion)
	{
		if (StringUtils.isEmpty(cliVersion))
		{
			return -1;
		}

		String[] minimumVersionParts = StringUtils.split(minimumVersion, '.');
		String[] cliVersionParts = StringUtils.split(cliVersion, '.');

		int length = Math.max(minimumVersionParts.length, cliVersionParts.length);
		for (int i = 0; i < length; i++)
		{
			int minimumVersionPart = i < minimumVersionParts.length ? Integer.parseInt(minimumVersionParts[i]) : 0;
			int cliVersionPart = i < cliVersionParts.length ? Integer.parseInt(cliVersionParts[i]) : 0;

			if (cliVersionPart < minimumVersionPart)
			{
				return -1;
			}
			else if (cliVersionPart > minimumVersionPart)
			{
				return 1;
			}
		}

		return 0;
	}
	
	/*
	 * 	Parses the version.xml and converts "version" attribute to a version number
	 */
	private static String parseXml(InputStream versionfile) throws IOException
	{
		String version = ""; //$NON-NLS-1$
		
		try
		{
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); 
		    dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document document = dBuilder.parse(versionfile);
		    
			version = document.getDocumentElement().getAttribute("version"); //$NON-NLS-1$
		}
		catch (ParserConfigurationException | SAXException e)
		{
			throw new AbortException(Messages.cliParseXmlError());
		}
		
		return version;
	}

}
