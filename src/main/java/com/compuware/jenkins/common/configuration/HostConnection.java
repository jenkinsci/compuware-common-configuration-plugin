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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import com.compuware.jenkins.common.utils.CommonConstants;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

/**
 * Class used to store the data for a host connection.
 */
public class HostConnection extends AbstractDescribableImpl<HostConnection>
{
	// Member Variables
	private final String m_description;
	private final String m_hostPort;
	private final String m_codePage;
	private final String m_timeout;
	private final String m_connectionId;
	private final String m_cesUrl;

	/**
	 * Constructor.
	 * 
	 * @param description
	 *            the description of the host connection
	 * @param hostPort
	 *            the host:port
	 * @param codePage
	 *            the code page to be used for the host connection
	 * @param timeout
	 *            the read/write timeout to honor on the host connection
	 * @param connectionId
	 *            a unique identifier
	 * @param cesUrl
	 * 			  a CES URL
	 */
	@DataBoundConstructor
	public HostConnection(String description, String hostPort, String codePage, String timeout, String connectionId, String cesUrl)
	{
		m_description = StringUtils.trimToEmpty(description);
		m_hostPort = StringUtils.trimToEmpty(hostPort);
		m_codePage = StringUtils.trimToEmpty(codePage);
		m_timeout = StringUtils.trimToEmpty(timeout);
		m_connectionId = generateId(connectionId);
		m_cesUrl = StringUtils.trimToEmpty(cesUrl);
	}

	/**
	 * Returns this connection's description.
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
		return m_description;
	}

	/**
	 * Returns this connection's host and port.
	 * <p>
	 * The format is "host:port".
	 * 
	 * @return the host/port
	 */
	public String getHostPort()
	{
		return m_hostPort;
	}

	/**
	 * Returns this connection's host.
	 * 
	 * @return the host
	 */
	public String getHost()
	{
		return StringUtils.substringBefore(getHostPort(), CommonConstants.COLON);
	}

	/**
	 * Returns this connection's port.
	 * 
	 * @return the port
	 */
	public String getPort()
	{
		return StringUtils.substringAfter(getHostPort(), CommonConstants.COLON);
	}

	/**
	 * Returns this connection's code page.
	 * 
	 * @return the code page
	 */
	public String getCodePage()
	{
		return m_codePage;
	}

	/**
	 * Returns this connection's read/write timeout.
	 * <p>
	 * If the user did not specify a timeout, the default of 0 is returned.
	 * 
	 * @return <code>String</code> timeout
	 */
	public String getTimeout()
	{
		return StringUtils.isBlank(m_timeout) ? "0" : m_timeout; //$NON-NLS-1$
	}

	/**
	 * Returns this connection's unique identifier.
	 * 
	 * @return the unique identifier
	 */
	public String getConnectionId()
	{
		return m_connectionId;
	}

	/**
	 * Generates and returns a unique identifier for this connection.
	 * <p>
	 * If the supplied identifier is null or empty, then a unique identifier is generated.
	 *
	 * @param id
	 *            the unique identifier
	 * 
	 * @return a unique identifier
	 */
	public static String generateId(String id)
	{
		String generatedId = id;

		if (StringUtils.isEmpty(generatedId))
		{
			generatedId = UUID.randomUUID().toString();
		}

		return generatedId;
	}

	/**
	 * Get the CES URL
	 * 
	 * @return the CES URL
	 */
	public String getCesUrl()
	{
		return m_cesUrl;
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
		 *            the concrete <code>HostCredentials</code> class
		 */
		public DescriptorImpl(Class<? extends HostConnection> clazz)
		{
			super(clazz);
		}

		/* 
		 * (non-Javadoc)
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
				String[] hostPortParts = StringUtils.split(tempValue, CommonConstants.COLON);
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
					int index = tempValue.indexOf(CommonConstants.COLON);
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
		 * 
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
		 * Validation for the host description text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "description" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckDescription(@QueryParameter String value)
		{
			String tempValue = StringUtils.trimToEmpty(value);
			if (tempValue.isEmpty())
			{
				return FormValidation.error(Messages.checkDescriptionEmptyError());
			}

			return FormValidation.ok();
		}

		/**
		 * Validation for the host timeout text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "timeout" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckTimeout(@QueryParameter String value)
		{
			String tempValue = StringUtils.trimToEmpty(value);
			if (!tempValue.isEmpty())
			{
				if (!StringUtils.isNumeric(tempValue))
				{
					return FormValidation.error(Messages.checkTimeoutError());
				}
				else
				{
					try
					{
						Integer.parseInt(tempValue);
					}
					catch (NumberFormatException e)
					{
						return FormValidation.error(Messages.checkTimeoutError());
					}
				}
			}

			return FormValidation.ok();
		}

		/**
		 * Validation for the 'CES URL' text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "cesUrl" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckCesUrl(@QueryParameter String value)
		{
			FormValidation result = FormValidation.ok();

			if (StringUtils.isNotBlank(value))
			{
				// verify if url is valid
				if (!value.endsWith("/")) //$NON-NLS-1$
				{
					value = value + "/"; //$NON-NLS-1$
				}
				try
				{
					new URL(value);
				}
				catch (MalformedURLException e)
				{
					result = FormValidation.error(Messages.checkCesUrlInvalidError());
				}
			}

			return result;
		}
	}
}