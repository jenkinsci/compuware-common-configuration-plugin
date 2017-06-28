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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import com.compuware.jenkins.common.utils.Constants;
import com.compuware.jenkins.common.utils.NumericStringComparator;
import hudson.CopyOnWrite;
import hudson.Extension;
import hudson.Launcher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

/**
 * Class to handle Compuware global configuration settings.
 */
@Extension
public class CpwrGlobalConfiguration extends GlobalConfiguration
{
	private static final String CODE_PAGE_MAPPINGS = "com.compuware.jenkins.common.configuration.codePageMappings"; //$NON-NLS-1$

	// Host connection instance ID defined in config.jelly
	private static final String HOST_CONN_INSTANCE_ID = "hostConn"; //$NON-NLS-1$

	@CopyOnWrite
	private volatile HostConnection[] m_hostConnections = new HostConnection[0];

	private String m_topazCLILocationWindows;
	private String m_topazCLILocationLinux;

	/**
	 * Constructor.
	 */
	public CpwrGlobalConfiguration()
	{
		load();
	}

	/**
	 * 
	 * @return
	 */
	public HostConnection[] getHostConnections()
	{
		return m_hostConnections;
	}

	public void setHostConnections(HostConnection... connections)
	{
		m_hostConnections = connections;
	}

	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
	{
		List<HostConnection> list = req.bindJSONToList(HostConnection.class, json.get(HOST_CONN_INSTANCE_ID));
		setHostConnections(list.toArray(new HostConnection[list.size()]));

		m_topazCLILocationWindows = req.getParameter("topazCLILocationWindows"); //$NON-NLS-1$
		m_topazCLILocationLinux = req.getParameter("topazCLILocationLinux"); //$NON-NLS-1$

		save();

		return true;
	}

	/**
	 * Fills in the Code page selection box with code pages.
	 *
	 * @return code page selections
	 */
	public ListBoxModel doFillCodePageItems()
	{
		ListBoxModel codePageModel = new ListBoxModel();

		ResourceBundle cpBundle = ResourceBundle.getBundle(CODE_PAGE_MAPPINGS);
		Set<String> cpNumberSet = cpBundle.keySet();

		// sort the code page values (for display purposes)
		List<String> cpNumberList = new ArrayList<>(cpNumberSet);
		Collections.sort(cpNumberList, new NumericStringComparator());

		Iterator<String> iterator = cpNumberList.iterator();
		while (iterator.hasNext())
		{
			String cpNumber = iterator.next();
			String cpDescription = cpBundle.getString(cpNumber);

			codePageModel.add(cpDescription, cpNumber);
		}

		return codePageModel;
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

	/**
	 * Returns the Topaz Workbench CLI location based on node
	 * 
	 * @return CLI location
	 */
	public String getTopazCLILocation(Launcher launcher)
	{
		if (launcher.isUnix())
		{
			return m_topazCLILocationLinux;
		}
		else
		{
			return m_topazCLILocationWindows;
		}
	}

	/**
	 * Returns the value of the topazCLILocationLinux. Used for databinding.
	 * 
	 * @return CLI location - Windows
	 */
	public String getTopazCLILocationWindows()
	{
		return m_topazCLILocationWindows;
	}

	/**
	 * Returns the value of the topazCLILocationLinux field. Used for databinding.
	 * 
	 * @return CLI location - Linux
	 */
	public String getTopazCLILocationLinux()
	{
		return m_topazCLILocationLinux;
	}

	/**
	 * Set the Topaz CLI installation location for Windows systems.
	 * 
	 * @param location
	 *            the install directory
	 */
	public void setTopazCLILocationWindows(String location)
	{
		m_topazCLILocationWindows = location;
	}

	/**
	 * Set the Topaz CLI installation location for Linux systems.
	 * 
	 * @param location
	 *            the install directory
	 */
	public void setTopazCLILocationLinux(String location)
	{
		m_topazCLILocationLinux = location;
	}
}
