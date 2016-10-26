package com.nust.ticket.similarity.bow;

import java.util.HashMap;
import java.util.Map;

public class Jaccard {

	public static void main(String[] args) {
		String string1 = "high space use";
		String string2 = "high space use";
		System.out.println(new Jaccard().jaccardDistance(string1, string2));
	}

	public double jaccardDistance(String str1, String str2) {
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
		return count/(str1s.length+str2s.length);
	}
}
