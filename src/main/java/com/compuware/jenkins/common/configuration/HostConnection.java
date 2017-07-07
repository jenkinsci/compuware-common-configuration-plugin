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

import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.compuware.jenkins.common.utils.Constants;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

/**
 * Class used to store the data for a host connection.
 */
public class HostConnection extends AbstractDescribableImpl<HostConnection>
{
	private final String m_connectionName;
	private final String m_hostPort;
	private final String m_codePage;
	private final String m_id;

	/**
	 * Constructor.
	 * 
	 * @param connectionName
	 *            the name of the host connection
	 * @param hostPort
	 *            the host:port
	 * @param codePage
	 *            the code page to be used for the host connection
	 * @param id
	 *            a unique identifier
	 */
	@DataBoundConstructor
	public HostConnection(String connectionName, String hostPort, String codePage, String id)
	{
		m_connectionName = StringUtils.trimToEmpty(connectionName);
		m_hostPort = StringUtils.trimToEmpty(hostPort);
		m_codePage = StringUtils.trimToEmpty(codePage);
		m_id = generateId(id);
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

	/**
	 * Gets the generated connection identifier.
	 * 
	 * @return <code>String</code> generated connection identifier
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * Returns either the unique id or a generated new unique id if the supplied id is missing.
	 *
	 * @param id
	 *            the unique identifier.
	 * @return either the unique id or a generated new unique id if the supplied id is missing.
	 */
	public static String generateId(String id)
	{
		return StringUtils.isEmpty(id) ? UUID.randomUUID().toString() : id;
	}

	/**
	 * DescriptorImpl is used to create instances of <code>CodeCoverageBuilder</code>. It also contains the global configuration
	 * options as fields, just like the <code>CodeCoverageBuilder</code> contains the configuration options for a job
	 */
	@Extension
	public static class DescriptorImpl extends Descriptor<HostConnection>
	{
		/**
		 * Constructor.
		 */
		public DescriptorImpl()
		{
			// Do nothing
		}

		/**
		 * Constructor.
		 *
		 * @param clazz
		 *            The concrete <code>HostCredentials</code> class.
		 */
		public DescriptorImpl(Class<? extends HostConnection> clazz)
		{
			super(clazz);
		}

		/* (non-Javadoc)
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName()
		{
			return Messages.displayNameHostConnection();
		}

		/**
		 * Validation for the 'Host:port' text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "hostPort" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckHostPort(@QueryParameter String value)
		{
			FormValidation result;

			String tempValue = StringUtils.trimToEmpty(value);
			if (tempValue.isEmpty())
			{
				result = FormValidation.error(Messages.checkHostPortEmptyError());
			}
			else
			{
				String[] hostPortParts = StringUtils.split(tempValue, Constants.COLON);
				if (hostPortParts.length == 2)
				{
					String host = StringUtils.trimToEmpty(hostPortParts[0]);
					String port = StringUtils.trimToEmpty(hostPortParts[1]);
					result = validateHostPort(host, port);
				}
				else if (hostPortParts.length > 2)
				{
					result = FormValidation.error(Messages.checkHostPortFormatError());
				}
				else
				{
					int index = tempValue.indexOf(Constants.COLON);
					if (index == -1)
					{
						result = FormValidation.error(Messages.checkHostPortFormatError());
					}
					else if (index == 0)
					{
						result = FormValidation.error(Messages.checkHostPortMissingHostError());
					}
					else
					{
						result = FormValidation.error(Messages.checkHostPortMissingPortError());
					}
				}
			}

			return result;
		}

		/**
		 * Validation for the host and port.
		 * 
		 * @param host
		 *            the host value
		 * @param port
		 *            the port value
		 * @return validation message
		 */
		private FormValidation validateHostPort(String host, String port)
		{
			FormValidation result = FormValidation.ok();

			if (host.isEmpty())
			{
				result = FormValidation.error(Messages.checkHostPortMissingHostError());
			}
			else if (port.isEmpty())
			{
				result = FormValidation.error(Messages.checkHostPortMissingPortError());
			}
			else if (!StringUtils.isNumeric(port))
			{
				result = FormValidation.error(Messages.checkHostPortInvalidPortError());
			}
			return result;
		}

		/**
		 * Validation for the host connection name text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "connectionName" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckConnectionName(@QueryParameter String value)
		{
			String tempValue = StringUtils.trimToEmpty(value);
			if (tempValue.isEmpty())
			{
				return FormValidation.error(Messages.checkConnectionNameEmptyError());
			}

			return FormValidation.ok();
		}
	}
}
