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
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#escapeForScript(java.lang.String, boolean)}.
	 */
	@Test
	public void testEscapeForScript()
	{
		// TODO fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#wrapInQuotes(java.lang.String)}.
	 */
	@Test
	public void testWrapInQuotes()
	{
		// TODO fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.compuware.jenkins.common.utils.ArgumentUtils#prefixWithDash(java.lang.String)}.
	 */
	@Test
	public void testPrefixWithDash()
	{
		String strWithDash = "-hasDash";
		String strWithoutDash = "hasDash";

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
		Properties expectedPropertiesWithoutDashes = new Properties();
		expectedPropertiesWithoutDashes.put("cc.sources", "/testSrc,C:/Users/");
		expectedPropertiesWithoutDashes.setProperty("cc.repos", "XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2");
		expectedPropertiesWithoutDashes.setProperty("cc.system", "ccSystem");
		expectedPropertiesWithoutDashes.setProperty("cc.test", "ccTest");
		expectedPropertiesWithoutDashes.setProperty("cc.ddio.overrides", "XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2");

		Properties expectedPropertiesWithDashes = new Properties();
		expectedPropertiesWithDashes.put("-cc.sources", "/testSrc,C:/Users/");
		expectedPropertiesWithDashes.setProperty("-cc.repos", "XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2");
		expectedPropertiesWithDashes.setProperty("-cc.system", "ccSystem");
		expectedPropertiesWithDashes.setProperty("-cc.test", "ccTest");
		expectedPropertiesWithDashes.setProperty("-cc.ddio.overrides", "XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2");

		// test property String without dashes, without whitespace, with newlines
		String propertyStr1 = "cc.sources=/testSrc,C:/Users/\ncc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\ncc.system=ccSystem\ncc.test=ccTest\ncc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties1 = ArgumentUtils.convertStringToProperties(propertyStr1);
		assertThat("Expected property String without dashes, with newlines to be converted to Properties correctly.",
				properties1.entrySet(), is(expectedPropertiesWithoutDashes.entrySet()));

		// test property String with dashes, with newlines
		String propertyStr2 = "-cc.sources=/testSrc,C:/Users/\n-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n-cc.system=ccSystem\n-cc.test=ccTest\n-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties2 = ArgumentUtils.convertStringToProperties(propertyStr2);
		assertThat("Expected property String with dashes, with newlines to be converted to Properties correctly.",
				properties2.entrySet(), is(expectedPropertiesWithDashes.entrySet()));

		// test property String without dashes, with carriage returns
		String propertyStr3 = "cc.sources=/testSrc,C:/Users/\rcc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\rcc.system=ccSystem\rcc.test=ccTest\rcc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties3 = ArgumentUtils.convertStringToProperties(propertyStr3);
		assertThat("Expected property String without dashes, with carraige returns to be converted to Properties correctly.",
				properties3.entrySet(), is(expectedPropertiesWithoutDashes.entrySet()));

		// test property String with dashes, with carriage returns
		String propertyStr4 = "-cc.sources=/testSrc,C:/Users/\r-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\r-cc.system=ccSystem\r-cc.test=ccTest\r-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties4 = ArgumentUtils.convertStringToProperties(propertyStr4);
		assertThat("Expected property String with dashes, with carraige returns to be converted to Properties correctly.",
				properties4.entrySet(), is(expectedPropertiesWithDashes.entrySet()));

		// test property String without dashes, with newline/carriage returns
		String propertyStr5 = "cc.sources=/testSrc,C:/Users/\n\rcc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n\rcc.system=ccSystem\n\rcc.test=ccTest\n\rcc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties5 = ArgumentUtils.convertStringToProperties(propertyStr5);
		assertThat(
				"Expected property String without dashes, with newline/carraige returns to be converted to Properties correctly.",
				properties5.entrySet(), is(expectedPropertiesWithoutDashes.entrySet()));

		// test property String with dashes, with newline/carriage returns
		String propertyStr6 = "-cc.sources=/testSrc,C:/Users/\n\r-cc.repos=XDEVREG.CC.REPOSIT,XDEVREG.CC.REPOSIT2\n\r-cc.system=ccSystem\n\r-cc.test=ccTest\n\r-cc.ddio.overrides=XDEVREG.CC.DDIO,XDEVREG.CC.DDIO2";
		Properties properties6 = ArgumentUtils.convertStringToProperties(propertyStr6);
		assertThat(
				"Expected property String with dashes, with newline/carraige returns to be converted to Properties correctly.",
				properties6.entrySet(), is(expectedPropertiesWithDashes.entrySet()));
	}
}