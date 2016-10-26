package com.nust.ticket.similarity.sequence;

import com.nust.ticket.utils.LCS;

/* Name: Srinivasan Nambi	
 * 49er ID: 800861816
 * This program accepts the folder path and filenames as input from the user and
 * calculates the Longest common subsequence between two sequences 
 * by storing the table in memory and displays the output.
 */
public class LCSR {

/*	public String getSubsequence(String s1, String s2) {
		return buildSubsequence(s1, s2);
	}*/

	/*
	 * Calculates the longest common subsequence between two sequences by
	 * storing the entire table and finally returns the LCS between two given
	 * sequences
	 */
	public static double lcsSim(String s1, String s2) {
		String subSequence = LCS.getSubsequence(s1, s2);
		String str1 = getString(s1);
		String str2 = getString(s2);
		double len1 = str1.length();
		double len2 = str2.length();
		// return the subsequence after popping from stack
		return subSequence.toString().length()/(len1>len2?len1:len2);
	}

	public static String getString(String str)
	{
		StringBuffer sb = new StringBuffer();
		String[] ss = str.split(" ");
		for(String s : ss)
		{
			sb.append(s);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String s1 = "itm agent offline primary ac0gads047 nt";
		String s2 = "itm agent offline primary camvp50001 nt";
		System.out.println(s1.length());
		double sim = lcsSim(s1, s2);
		System.out.println(sim);
	}
}
