/**
 * These materials contain confidential information and trade secrets of Compuware Corporation. You shall maintain the materials
 * as confidential and shall not disclose its contents to any third party except as may be required by law or regulation. Use,
 * disclosure, or reproduction is prohibited without the prior express written permission of Compuware Corporation.
 * 
 * All Compuware products listed within the materials are trademarks of Compuware Corporation. All other company or product
 * names are trademarks of their respective owners.
 * 
 * Copyright (c) 2017 Compuware Corporation. All rights reserved.
 */
package com.compuware.jenkins.common.configuration;

import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;

/**
 * 
 */
public class CESToken extends AbstractDescribableImpl<CESToken>
{
	// Member Variables
	private String host;
	private String token;
	private String cesTokenId;

	/**
	 * Constructor.
	 * 
	 * @param hostName
	 *            the host name
	 * @param token
	 *            the token
	 */
	@DataBoundConstructor
	public CESToken(String hostName, String token, String cesTokenId)
	{
		this.host = StringUtils.trimToEmpty(hostName);
		this.token = StringUtils.trimToEmpty(token);
		this.cesTokenId = StringUtils.trimToEmpty(cesTokenId);
	}

	public String getHostName() 
	{
		return host;
	}

	public void setHostName(String hostName) 
	{
		this.host = hostName;
	}

	public String getToken() 
	{
		return token;
	}

	public void setToken(String token) 
	{
		this.token = token;
	}

	/**
	 * @return the cesTokenId
	 */
	public String getCesTokenId()
	{
		return cesTokenId;
	}

	/**
	 * @param cesTokenId the cesTokenId to set
	 */
	public void setCesTokenId(String cesTokenId) //TODO no setters? see hostconnection.java
	{
		this.cesTokenId = cesTokenId;
	}

	/**
	 * Generates and returns a unique identifier for this CES token.
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
	
	@Override
	public String toString() {
		ToStringBuilder.setDefaultStyle(ToStringStyle.MULTI_LINE_STYLE);
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * DescriptorImpl is used to create instances of <code>CodeCoverageBuilder</code>. It also contains the global configuration
	 * options as fields, just like the <code>CodeCoverageBuilder</code> contains the configuration options for a job
	 */
	@Extension
	public static class DescriptorImpl extends Descriptor<CESToken>
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
		 *            the concrete <code>CESToken</code> class
		 */
		public DescriptorImpl(Class<? extends CESToken> clazz)
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
			return "CESToken";
		}
	
		/**
		 * Validation for the host name text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "hostName" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckHostName(@QueryParameter String value)
		{
			//TODO elaborate checks to verify format too
			String tempValue = StringUtils.trimToEmpty(value);
			if (tempValue.isEmpty())
			{
				return FormValidation.error(Messages.checkHostNameEmptyError());
			}
	
			return FormValidation.ok();
		}
		
		/**
		 * Validation for the token text field.
		 * 
		 * @param value
		 *            value passed from the config.jelly "token" field
		 * 
		 * @return validation message
		 */
		public FormValidation doCheckToken(@QueryParameter String value)
		{
			if (StringUtils.isBlank(value))
			{
				return FormValidation.error(Messages.checkTokenEmptyError());
			}
	
			return FormValidation.ok();
		}
	}
	
}
