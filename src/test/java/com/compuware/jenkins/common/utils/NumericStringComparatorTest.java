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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
		catch (NumberFormatException e)
		{
			// expected
		}
	}
}