package com.nust.ticket.similarity.sequence;

import com.nust.ticket.utils.LCS;

public class NLCS {
	public static void main(String[] args) {
/*		String s1 = "itm agent offline primary ac0gads047 nt";
		String s2 = "itm agent offline primary camvp50001 nt";
		double similarity1 = Nlcs(s1, s2);
		System.out.println("NLCS similarity = " + similarity1);*/
	}

	/**
	 * @param s1
	 * @param s2
	 * @return NLCS相似度
	 */
	public double Nlcs(String s1, String s2) {
		double similarity;
		String longest = LCS.getSubsequence(s1, s2);
		int length = longest.length();
		double len1 = LCSR.getString(s1).length();
		double len2 = LCSR.getString(s2).length();
		similarity = length*length / (len1 * len2);
		return similarity;
	}
}
