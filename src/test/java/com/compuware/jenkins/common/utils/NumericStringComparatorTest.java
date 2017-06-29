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
package com.compuware.jenkins.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the numeric string comparitor.
 */
@SuppressWarnings("nls")
public class NumericStringComparatorTest
{
	/**
	 * Test the numeric string comparator for equals, less than, greater than, null, and bad values.
	 */
	@Test
	public void compareTest()
	{
		NumericStringComparator comparator = new NumericStringComparator();

		// equals
		String leftSide = "3";
		String rightSide = "3";
		int expectedResult = 0;
		int actualResult = comparator.compare(leftSide, rightSide);
		assertEquals("Expect equals.", expectedResult, actualResult);

		// less than
		leftSide = "1";
		rightSide = "3";
		actualResult = comparator.compare(leftSide, rightSide);
		assertTrue("Expect less than.", actualResult < 0);

		// greater than

		leftSide = "7";
		rightSide = "3";
		expectedResult = 1;
		actualResult = comparator.compare(leftSide, rightSide);
		assertTrue("Expect greater than.", actualResult > 0);

		// bad value
		leftSide = "XXX7";
		rightSide = "3";
		try
		{
			actualResult = comparator.compare(leftSide, rightSide);
			fail("Expect NumberFormatException");
		}
		catch (NumberFormatException exc)
		{
			// expected
		}

		// null value
		leftSide = null;
		rightSide = "3";
		try
		{
			actualResult = comparator.compare(leftSide, rightSide);
			fail("Expect NumberFormatException");
		}
		catch (NumberFormatException exc)
		{
			// expected
		}
	}
}
