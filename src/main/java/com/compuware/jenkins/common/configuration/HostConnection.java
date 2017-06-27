/*
 * Jenkins Plugin for SonarQube, open source software quality management tool. mailto:contact AT sonarsource DOT com
 *
 * Jenkins Plugin for SonarQube is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * Jenkins Plugin for SonarQube is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
/*
 * Sonar is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02
 */
package com.compuware.jenkins.common.configuration;

import java.io.Serializable;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

public class HostConnection implements Serializable
{

	private static final long serialVersionUID = 1L;

	private final String m_connectionName;
	private final String m_hostPort;
	// private final String m_credentialsId;
	private final String m_codePage;

	@DataBoundConstructor
	public HostConnection(String connectionName, String hostPort, String credentialsId, String codePage)
	{
		m_connectionName = StringUtils.trimToEmpty(connectionName);
		m_hostPort = StringUtils.trimToEmpty(hostPort);
		// m_credentialsId = StringUtils.trimToEmpty(credentialsId);
		m_codePage = StringUtils.trimToEmpty(codePage);
	}

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
	 * Gets the value of the 'Login Credentials'.
	 * 
	 * @return <code>String</code> value of m_credentialsId
	 */
	// public String getCredentialsId()
	// {
	// return m_credentialsId;
	// }
	
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
