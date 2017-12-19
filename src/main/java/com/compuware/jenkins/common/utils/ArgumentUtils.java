/**
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 - 2018 Compuware Corporation
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
import java.nio.file.Path;
import java.nio.file.Paths;
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
	 * For comma-delimited path Strings, use {@link #escapeCommaDelimitedPathsForScript(String)} instead as this method does not
	 * account for the possibility of the user surrounding each path with double quotes.
	 * <p>
	 * If the input String is null/empty or does not require escaping, it is returned unchanged.
	 * 
	 * @param input
	 *            the <code>String</code> to escape
	 * 
	 * @return the escaped <code>String</code>
	 */
	public static String escapeForScript(String input)
	{
		String output = input;

		if (StringUtils.isNotEmpty(input))
		{
			// escape any double quote (") with another double quote (")
			output = StringUtils.replace(input, CommonConstants.DOUBLE_QUOTE, CommonConstants.DOUBLE_QUOTE_ESCAPED);

			// wrap the input in double quotes
			output = wrapInDoubleQuotes(output);
		}

		return output;
	}

	/**
	 * Returns an escaped version of the given comma-delmited paths input String for a Batch or Shell script.
	 * <p>
	 * This method accounts for the possibility of the user surrounding each path in the input String with double quotes. For
	 * all other Strings, use {@link #escapeForScript(String)} instead.
	 * <p>
	 * If the input String is null/empty or does not require escaping, it is returned unchanged.
	 * 
	 * @param input
	 *            the comma-delimited paths <code>String</code> to escape
	 * 
	 * @return the escaped <code>String</code>
	 */
	public static String escapeCommaDelimitedPathsForScript(String input)
	{
		String output = input;

		if (StringUtils.isNotEmpty(input))
		{
			// remove all double quotes from the path
			output = StringUtils.remove(input, CommonConstants.DOUBLE_QUOTE);

			// wrap the input in double quotes
			output = wrapInDoubleQuotes(output);
		}

		return output;
	}

	/**
	 * Wraps the given input String in double quotes.
	 * <p>
	 * If the input String is null/empty or is already wrapped in double quotes, it is returned unchanged.
	 * 
	 * @param input
	 *            the <code>String</code> to wrap in double quotes
	 * 
	 * @return the double-quoted <code>String</code>
	 */
	public static String wrapInDoubleQuotes(String input)
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
	
	/**
	 * Resolves a folder name to an absolute path.
	 * 
	 * @param folder
	 *            the name of a folder or an absolute path
	 * @param workspacePath
	 *            absolute path of the project workspace
	 * @return absolute path of of a folder
	 */
	public static String resolvePath(String folder, String workspacePath)
	{
		String resolvedFolderPath = folder;
		Path folderPath = Paths.get(folder);
		
		if (!folderPath.isAbsolute())
		{
			Path absPath = Paths.get(workspacePath, resolvedFolderPath);
			resolvedFolderPath = absPath.toString();
		}
		
		resolvedFolderPath = escapeForScript(resolvedFolderPath);
		
		return resolvedFolderPath;
	}
}