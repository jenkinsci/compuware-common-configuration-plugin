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

import java.io.Serializable;
import java.util.Comparator;

/**
 * Performs numeric string comparison.
 */
public class NumericStringComparator implements Comparator<String>, Serializable
{
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(String numStr1, String numStr2)
	{
		int intVal1 = Integer.parseInt(numStr1);
		int intVal2 = Integer.parseInt(numStr2);

		return intVal1 - intVal2;
	}
}
