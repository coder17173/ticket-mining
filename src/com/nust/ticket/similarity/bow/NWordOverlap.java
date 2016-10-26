package com.nust.ticket.similarity.bow;

public class NWordOverlap {
/*	private String s1;
	private String s2;
	
	NWordOverlap(String s1, String s2)
	{
		this.s1 = s1;
		this.s2 = s2;
		overlapPhrase();
	}*/
	
	public double overlapPhrase(String s1, String s2)
	{
		String longString = null;
		String shortString = null;
		if(s1.length() >= s2.length())
		{
			longString = s1;
			shortString = s2;
		}else {
			longString = s2;
			shortString = s1;
		}
		String[] ss1 = shortString.split(" ");
		String[] ss2 = longString.split(" ");
		double overlap = 0.0;
		for(int i = 1; i <= ss1.length; i++)
		{
			int count = 0;
			for(int j = 0; j < ss1.length - i +1; j++)
			{
				String temp = "";
				for(int k = 0; k < i; k++)
				{
					temp = temp + ss1[j+k] + " ";
				}
				
				if(longString.contains(temp.trim()))
				{
					count++;
				}
			}
			overlap += count * Math.pow(i, 2);
		}
		
		double result = Math.tanh(overlap / (ss1.length + ss2.length));
		return result;
	}
	
	
	public static void main(String[] args)
	{
		String s1 = "Service in alert state name spooler state stop";
		String s2 = "Service in alert state name tsm client acceptor state start pend";
//		NWordOverlap nWordOverlap = new NWordOverlap(s1, s2);
		NWordOverlap nWordOverlap = new NWordOverlap();
		double result = nWordOverlap.overlapPhrase(s1, s2);
		System.out.println(result);
	}
}
