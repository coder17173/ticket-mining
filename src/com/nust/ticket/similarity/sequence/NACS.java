package com.nust.ticket.similarity.sequence;

public class NACS {
	public static void main(String[] args)
	{
/*		String s1 = "itm agent offline primary va10pwpapp041 nt";
		String s2 = "security userid root had 1 login failures from an ssh client at ip address 30.124.251.83";
		double sim = ACSSim(s1, s2);
		System.out.println(sim);*/
		
	}
	
	
	public double ACSSim(String str1, String str2)
	{
		int count = 0;
		for(int i = 0; i < str1.length(); i++)
		{
			for(int j = i+1; j < str1.length(); j++)
			{
				if(str2.contains(str1.substring(i, j)))
				{
					count++;
				}
			}
		}
		return count*count/(subNum(str1)*subNum(str2));
	}
	
	public static double subNum(String str)
	{
		int len = str.length();
		double num = (1+len)*len/2.0;
		return num;
	}
}
