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

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Class used to store the data for a host connection.
 */
public class HostConnection implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final String m_connectionName;
	private final String m_hostPort;
	private final String m_codePage;

	/**
	 * Constructor.
	 * 
	 * @param connectionName
	 *            the name of the host connection
	 * @param hostPort
	 *            the host:port
	 * @param codePage
	 *            the code page to be used for the host connection
	 */
	@DataBoundConstructor
	public HostConnection(String connectionName, String hostPort, String codePage)
	{
		m_connectionName = StringUtils.trimToEmpty(connectionName);
		m_hostPort = StringUtils.trimToEmpty(hostPort);
		m_codePage = StringUtils.trimToEmpty(codePage);
	}

	/**
	 * Gets the value of the host connection 'Name'.
	 * 
	 * @return <code>String</code> value of m_connectionName
	 */
	public String getConnectionName()
	{
		return m_connectionName;
	}

	/**
	 * Gets the value of the 'Host:Port'.
	 * 
	 * @return <code>String</code> value of m_hostport
	 */
	public String getHostPort()
	{
		return m_hostPort;
	}

	/**
	 * Gets the value of the 'Code Page'.
	 * 
	 * @return <code>String</code> value of m_codePage
	 */
	public String getCodePage()
	{
		return m_codePage;
	}
}
