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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import java.io.IOException;
import java.util.Properties;
import org.junit.Test;

/**
 * ArgumentUtils unit tests.
 */
@SuppressWarnings("nls")
public class ArgumentUtilsTest
{
	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#escapeForScript(java.lang.String)}.
	 */
	@Test
	public void testEscapeForScript()
	{
		String strWithDoubleQuotesAndWrapped = "\"double\"Quotes\"Wrapped\"Test\"";
		String strWithoutDoubleQuotesAndWrapped = "\"doubleQuotesWrappedTest\"";
		String strWithDoubleQuotesNotWrapped = "double\"Quotes\"Wrapped\"Test";
		String strWithoutDoubleQuotesNotWrapped = "doubleQuotesWrappedTest";
		String strWithDoubleQuotesInBetween = "doubleQuotes\"WrappedTest";
		String strWrappedInQuotesAndQuoteInBetween = "\"doubleQuotes\"WrappedTest\"";
		
		String expectedStr = "\"\"double\"\"Quotes\"\"Wrapped\"\"Test\"\"";
		String strWithDoubleQuotesAndWrappedAfterCall = ArgumentUtils.escapeForScript(strWithDoubleQuotesAndWrapped);
		assertThat("Expected the string with double quotes and wrapped to be changed after call.",
				strWithDoubleQuotesAndWrappedAfterCall, equalTo(expectedStr));

		expectedStr = "\"\"doubleQuotesWrappedTest\"\"";
		String strWithoutDoubleQuotesAndWrappedAfterCall = ArgumentUtils.escapeForScript(strWithoutDoubleQuotesAndWrapped);
		assertThat("Expected the string without double quotes and wrapped to not be changed after call.",
				strWithoutDoubleQuotesAndWrappedAfterCall, equalTo(expectedStr));

		expectedStr = "\"double\"\"Quotes\"\"Wrapped\"\"Test\"";
		String strWithDoubleQuotesNotWrappedAfterCall = ArgumentUtils.escapeForScript(strWithDoubleQuotesNotWrapped);
		assertThat("Expected the string with double quotes not wrapped to be changed after call.",
				strWithDoubleQuotesNotWrappedAfterCall, equalTo(expectedStr));

		String strWithoutDoubleQuotesNotWrappedAfterCall = ArgumentUtils.escapeForScript(strWithoutDoubleQuotesNotWrapped);
		assertThat("Expected the string without double quotes not wrapped to be changed after call.",
				strWithoutDoubleQuotesNotWrappedAfterCall, equalTo(strWithoutDoubleQuotesAndWrapped));
		
		expectedStr = "\"doubleQuotes\"\"WrappedTest\"";
		String strWithDoubleQuotesInBetweenAfterCall = ArgumentUtils.escapeForScript(strWithDoubleQuotesInBetween);
		assertThat("Expected the string with double quotes in between to have the middle quotes escaped.",
				strWithDoubleQuotesInBetweenAfterCall, equalTo(expectedStr));
		
		expectedStr = "\"\"doubleQuotes\"\"WrappedTest\"\"";
		String strWrappedInQuotesAndQuoteInBetweenAfterCall = ArgumentUtils.escapeForScript(strWrappedInQuotesAndQuoteInBetween);
		assertThat("Expected the string with double quotes in between to have the middle quotes escaped.",
				strWrappedInQuotesAndQuoteInBetweenAfterCall, equalTo(expectedStr));
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#escapeCommaDelimitedPathsForScript(String)}.
	 */
	@Test
	public void testEscapeCommaDelimitedPathsForScript()
	{
		String strWithoutDoubleQuotesAndWrapped = "\"doubleQuotesWrappedTest\"";
		String strWithDoubleQuotesAndWrapped = "\"double\"Quotes\"Wrapped\"Test\"";
		String strWithoutDoubleQuotesNotWrapped = "doubleQuotesWrappedTest";
		String strWithDoubleQuotesNotWrapped = "double\"Quotes\"Wrapped\"Test";

		String strWithoutDoubleQuotesAndWrappedAfterCall = ArgumentUtils
				.escapeCommaDelimitedPathsForScript(strWithoutDoubleQuotesAndWrapped);
		assertThat("Expected the string without double quotes and wrapped to not be changed after call.",
				strWithoutDoubleQuotesAndWrappedAfterCall, equalTo(strWithoutDoubleQuotesAndWrapped));

		String strWithDoubleQuotesAndWrappedAfterCall = ArgumentUtils
				.escapeCommaDelimitedPathsForScript(strWithDoubleQuotesAndWrapped);
		assertThat("Expected the string with double quotes and wrapped to be changed after call.",
				strWithDoubleQuotesAndWrappedAfterCall, equalTo(strWithoutDoubleQuotesAndWrapped));

		String strWithoutDoubleQuotesNotWrappedAfterCall = ArgumentUtils
				.escapeCommaDelimitedPathsForScript(strWithoutDoubleQuotesNotWrapped);
		assertThat("Expected the string without double quotes not wrapped to be changed after call.",
				strWithoutDoubleQuotesNotWrappedAfterCall, equalTo(strWithoutDoubleQuotesAndWrapped));

		String strWithDoubleQuotesNotWrappedAfterCall = ArgumentUtils
				.escapeCommaDelimitedPathsForScript(strWithDoubleQuotesNotWrapped);
		assertThat("Expected the string with double quotes not wrapped to be changed after call.",
				strWithDoubleQuotesNotWrappedAfterCall, equalTo(strWithoutDoubleQuotesAndWrapped));
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#wrapInDoubleQuotes(java.lang.String)}.
	 */
	@Test
	public void testWrapInDoubleQuotes()
	{
		String strWithDoubleQuotes = "\"doubleQuotesTest\"";
		String strWithoutDoubleQuotes = "doubleQuotesTest";

		String strWithDoubleQuotesAfterCall = ArgumentUtils.wrapInDoubleQuotes(strWithDoubleQuotes);
		assertThat("Expected the string with double quotes to not be changed after call.", strWithDoubleQuotesAfterCall,
				equalTo(strWithDoubleQuotes));

		String strWithoutDoubleQuotesAfterCall = ArgumentUtils.wrapInDoubleQuotes(strWithoutDoubleQuotes);
		assertThat("Expected the string without double quotes to be changed after call.", strWithoutDoubleQuotesAfterCall,
				equalTo(strWithDoubleQuotes));
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#prefixWithDash(java.lang.String)}.
	 */
	@Test
	public void testPrefixWithDash()
	{
		String strWithDash = "-dashTest";
		String strWithoutDash = "dashTest";

		String strWithDashAfterCall = ArgumentUtils.prefixWithDash(strWithDash);
		assertThat("Expected the string with a dash to not be changed after call.", strWithDashAfterCall, equalTo(strWithDash));

		String strWithoutDashAfterCall = ArgumentUtils.prefixWithDash(strWithoutDash);
		assertThat("Expected the string without a dash to be changed after call.", strWithoutDashAfterCall,
				equalTo(strWithDash));
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#convertStringToProperties(java.lang.String)}.
	 */
	@Test
	public void testConvertStringToProperties() throws IOException
	{
		Properties expectedPropertiesWithoutDashesOrBackslashes = new Properties();
		expectedPropertiesWithoutDashesOrBackslashes.put("cc.sources", "/testSrc,C:/Users/");
		expectedPropertiesWithoutDashesOrBackslashes.setProperty("cc.repos", "XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2");
		expectedPropertiesWithoutDashesOrBackslashes.setProperty("cc.system", "ccSystem");
		expectedPropertiesWithoutDashesOrBackslashes.setProperty("cc.test", "ccTest");
		expectedPropertiesWithoutDashesOrBackslashes.setProperty("cc.ddio.overrides", "XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2");

		Properties expectedPropertiesWithDashesAndBackslashes = new Properties();
		expectedPropertiesWithDashesAndBackslashes.put("-cc.sources", "\\testSrc,C:\\Users\\");
		expectedPropertiesWithDashesAndBackslashes.setProperty("-cc.repos", "XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2");
		expectedPropertiesWithDashesAndBackslashes.setProperty("-cc.system", "ccSystem");
		expectedPropertiesWithDashesAndBackslashes.setProperty("-cc.test", "ccTest");
		expectedPropertiesWithDashesAndBackslashes.setProperty("-cc.ddio.overrides", "XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2");

		// test property String without dashes, with newlines
		String propertyStr1 = "cc.sources=/testSrc,C:/Users/\ncc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\ncc.system=ccSystem\ncc.test=ccTest\ncc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties1 = ArgumentUtils.convertStringToProperties(propertyStr1);
		assertThat("Expected property String without dashes, with newlines to be converted to Properties correctly.",
				properties1.entrySet(), is(expectedPropertiesWithoutDashesOrBackslashes.entrySet()));

		// test property String with dashes, with newlines
		String propertyStr2 = "-cc.sources=\\testSrc,C:\\Users\\\n-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n-cc.system=ccSystem\n-cc.test=ccTest\n-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties2 = ArgumentUtils.convertStringToProperties(propertyStr2);
		assertThat("Expected property String with dashes, with newlines to be converted to Properties correctly.",
				properties2.entrySet(), is(expectedPropertiesWithDashesAndBackslashes.entrySet()));

		// test property String without dashes, with carriage returns
		String propertyStr3 = "cc.sources=/testSrc,C:/Users/\rcc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\rcc.system=ccSystem\rcc.test=ccTest\rcc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties3 = ArgumentUtils.convertStringToProperties(propertyStr3);
		assertThat("Expected property String without dashes, with carraige returns to be converted to Properties correctly.",
				properties3.entrySet(), is(expectedPropertiesWithoutDashesOrBackslashes.entrySet()));

		// test property String with dashes, with carriage returns
		String propertyStr4 = "-cc.sources=\\testSrc,C:\\Users\\\r-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\r-cc.system=ccSystem\r-cc.test=ccTest\r-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties4 = ArgumentUtils.convertStringToProperties(propertyStr4);
		assertThat("Expected property String with dashes, with carraige returns to be converted to Properties correctly.",
				properties4.entrySet(), is(expectedPropertiesWithDashesAndBackslashes.entrySet()));

		// test property String without dashes, with newline/carriage returns
		String propertyStr5 = "cc.sources=/testSrc,C:/Users/\n\rcc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n\rcc.system=ccSystem\n\rcc.test=ccTest\n\rcc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties5 = ArgumentUtils.convertStringToProperties(propertyStr5);
		assertThat(
				"Expected property String without dashes, with newline/carraige returns to be converted to Properties correctly.",
				properties5.entrySet(), is(expectedPropertiesWithoutDashesOrBackslashes.entrySet()));

		// test property String with dashes, with newline/carriage returns
		String propertyStr6 = "-cc.sources=\\testSrc,C:\\Users\\\n\r-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n\r-cc.system=ccSystem\n\r-cc.test=ccTest\n\r-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties6 = ArgumentUtils.convertStringToProperties(propertyStr6);
		assertThat(
				"Expected property String with dashes, with newline/carraige returns to be converted to Properties correctly.",
				properties6.entrySet(), is(expectedPropertiesWithDashesAndBackslashes.entrySet()));
	}
}