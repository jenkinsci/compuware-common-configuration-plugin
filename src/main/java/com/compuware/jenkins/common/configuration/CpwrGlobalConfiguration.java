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

	public void setHostConnectionss(HostConnection... connections)
	{
		this.m_hostConnections = connections;
		save();
	}

	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest, net.sf.json.JSONObject)
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject json)
	{
		List<HostConnection> list = req.bindJSONToList(HostConnection.class, json.get(HOST_CONN_INSTANCE_ID));
		setHostConnectionss(list.toArray(new HostConnection[list.size()]));

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
}
