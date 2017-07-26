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

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class containing helper methods for handling arguments.
 */
public class ArgumentUtils
{
	/**
	 * Private constructor.
	 * <p>
	 * All methods should be called statically.
	 */
	private ArgumentUtils()
	{
	}

	/**
	 * Returns an escaped version of the given input String for a Batch or Shell script.
	 * <p>
	 * If the input String is null/empty or does not require escaping, it is returned unchanged.
	 * 
	 * @param input
	 *            the <code>String</code> to escape
	 * @param isShell
	 *            <code>true</code> if the script is a Shell script, <code>false</code> if it is a Batch script
	 * 
	 * @return the escaped <code>String</code>
	 */
	public static String escapeForScript(String input, boolean isShell)
	{
		String output = input;

		if (StringUtils.isNotEmpty(input))
		{
			// escape any double quotes (") with another double quote (") for both Batch and Shell scripts
			output = StringUtils.replace(input, CommonConstants.DOUBLE_QUOTE, CommonConstants.DOUBLE_QUOTE_ESCAPED);

			// wrap the input in quotes for Batch scripts
			if (!isShell)
			{
				output = wrapInQuotes(output);
			}
		}

		return output;
	}

	/**
	 * Wraps the given input String in quotes.
	 * <p>
	 * If the input String is null/empty or is already wrapped in quotes, it is returned unchanged.
	 * 
	 * @param input
	 *            the <code>String</code> to wrap in quotes
	 * 
	 * @return the quoted <code>String</code>
	 */
	public static String wrapInQuotes(String input)
	{
		String output = input;

		if (StringUtils.isNotEmpty(input))
		{
			output = StringUtils.prependIfMissing(input, CommonConstants.DOUBLE_QUOTE);
			output = StringUtils.appendIfMissing(output, CommonConstants.DOUBLE_QUOTE);
		}

		return output;
	}

	/**
	 * Prefixes the given input String with a dash (-).
	 * <p>
	 * If the input String is already prefixed with a dash, it is returned unchanged.
	 * 
	 * @param input
	 *            the <code>String</code> to prefix
	 * 
	 * @return the prefixed <code>String</code>
	 */
	public static String prefixWithDash(String input)
	{
		String output = input;

		if (StringUtils.isNotEmpty(input))
		{
			output = StringUtils.prependIfMissing(input, CommonConstants.DASH);
		}

		return output;
	}

	/**
	 * Converts the given properties String to a Properties object.
	 * <p>
	 * If the given properties String is null/empty, an empty Properties object is returned.
	 * 
	 * @param propertiesString
	 *            the <code>String</code> to convert
	 * 
	 * @return the <code>Properties</code> object
	 * 
	 * @throws IOException
	 *             if an error occurred during conversion
	 * 
	 * @see java.util.Properties#load(Reader)
	 */
	public static Properties convertStringToProperties(String propertiesString) throws IOException
	{
		Properties properties = new Properties();

		if (StringUtils.isNotEmpty(propertiesString))
		{
			// escape any backslashes before loading the properties (the load() call will remove them, as per its javadoc)
			String escapedPropertiesString = StringUtils.replace(propertiesString, CommonConstants.BACKSLASH,
					CommonConstants.BACKSLASH_ESCAPED);
			properties.load(new StringReader(escapedPropertiesString));
		}

		return properties;
	}
}