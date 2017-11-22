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
package com.compuware.jenkins.common.utils;

import org.apache.commons.lang.StringUtils;
import com.compuware.jenkins.common.configuration.Messages;
import hudson.util.FormValidation;

/**
 * Utility class containing helper methods for validation.
 */
public class ValidationUtils
{
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
	public static FormValidation validateHostPort(String host, String port)
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
}