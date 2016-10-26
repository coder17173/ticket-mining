package com.nust.ticket.similarity.sequence;

import java.util.HashMap;
import java.util.Map;

public class SM {
	
	public static void main(String[] args)
	{
/*		String s1 = "itm agent offline primary camvp50001 nt";
		String s2 = "itm agent offline primary cc01ant004 nt";
		double sim = SMValue(s1, s2);
		System.out.println(sim);*/
	}
	
	/**
	 * @param str1
	 * @param str2
	 * @return similarity
	 */
	public double SMValue(String str1, String str2)
	{
		double sim = 0.0;
		if(str1.length() == 0 || str2.length() == 0)
		{
			return sim;
		}
		
		Map<String, Integer> wordFreq = new HashMap<String, Integer>();
		String[] str1s = str1.trim().split(" ");
		String[] str2s = str2.trim().split(" ");
		for(String key : str1s)
		{
			if(wordFreq.containsKey(key))
			{
				wordFreq.put(key, wordFreq.get(key)+1);
			}else{
				wordFreq.put(key, 1);
			}
		}
		
		double count = 0;
		for(String s : str2s)
		{
			if(wordFreq.containsKey(s) && (wordFreq.get(s) != 0))
			{
				count++;
				wordFreq.put(s, wordFreq.get(s)-1);
			}
		}
		return count/Math.sqrt(str1s.length*str2s.length);
	}
}
