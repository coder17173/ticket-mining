package com.nust.ticket.similarity.sequence;

import java.util.ArrayList;
import java.util.List;

public class MCNLCSn {
	public static void main(String[] args)
	{
/*		String s1 = "albastru";
		String s2 = "alabaster";
	    double result = MCNLCSn.Mclcsn(s1, s2);
	    System.out.println(result);*/
	}
	
	public  double Mclcsn(String s1, String s2)
	{
		String result = null;
		String longString = null;
		String shortString = null;
		if(s1.length() <= s2.length())
		{
			longString = s2;
			shortString = s1;
		}else {
			longString = s1;
			shortString = s2;
		}
		
		List<String> Ngram = new ArrayList<String>();
		Ngram = getNgram(shortString);
		for(int i = Ngram.size()-1; i >= 0; i--)
		{
			if(longString.contains(Ngram.get(i)))
			{
				result = Ngram.get(i);
				break;
			}
		}
		int len1 = LCSR.getString(s1).length();
		int len2 = LCSR.getString(s2).length();
		double length = result.length();
		return length*length/(len1*len2);
	}
	
	public static List<String> getNgram(String s)
	{
		List<String> Ngram = new ArrayList<String>();
		for(int i = 1; i <= s.length(); i++)
		{
			for(int j = 0; j < s.length() - i + 1; j++)
			{
				Ngram.add(s.substring(j, j+i));
			}
		}
		return Ngram;
	}
}
