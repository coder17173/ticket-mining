package com.nust.ticket.similarity.bow;

import java.util.HashMap;
import java.util.Map;

public class Dice {
	public double diceSim(String str1,String str2)
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
		return 2*count/(str1s.length+str2s.length);
	}
	
	public static void main(String[] args)
	{
		String s1 = "Service in alert state name spooler state stop";
		String s2 = "Service in alert state name tsm client acceptor state start pend";
		double result = new Dice().diceSim(s1, s2);
		System.out.println(result);
	}
}
