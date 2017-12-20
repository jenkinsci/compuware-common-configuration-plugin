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

/**
 * Constants used by common configuration and any plugins that use this plugin.
 */
@SuppressWarnings("nls")
public class CommonConstants
{
	/**
	 * Private constructor.
	 * <p>
	 * All constants should be accessed statically.
	 */
	private CommonConstants()
	{
	}

	// Constants
	public static final String FILE_SEPARATOR_PROPERTY_KEY = "file.separator";
	public static final String UTF_8 = "UTF-8";

	public static final String CODE_PAGE_PARM = "-code";
	public static final String DATA_PARM = "-data";
	public static final String HOST_PARM = "-host";
	public static final String PW_PARM = "-pass";
	public static final String PORT_PARM = "-port";
	public static final String USERID_PARM = "-id";
	public static final String TARGET_FOLDER_PARM = "-targetFolder";
	public static final String TIMEOUT_PARM = "-timeout";
	public static final String TOPAZ_CLI_WORKSPACE = "TopazCliWkspc";
	public static final String VERSION_FILE = "version.xml";

	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String DASH = "-";
	public static final String LINE_RETURN = "\n";
	public static final String SPACE = " ";
	public static final String SLASH = "/";

	public static final String BACKSLASH = "\\";
	public static final String BACKSLASH_ESCAPED = "\\\\";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String DOUBLE_QUOTE_ESCAPED = "\"\"";
}