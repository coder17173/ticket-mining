package com.nust.ticket.similarity.wordnet;

import edu.sussex.nlp.jws.AdaptedLesk;
import edu.sussex.nlp.jws.AdaptedLeskTanimoto;
import edu.sussex.nlp.jws.JWS;
import edu.sussex.nlp.jws.JiangAndConrath;
import edu.sussex.nlp.jws.LeacockAndChodorow;
import edu.sussex.nlp.jws.LeskGlossOverlaps;
import edu.sussex.nlp.jws.Lin;
import edu.sussex.nlp.jws.Resnik;
import edu.sussex.nlp.jws.WuAndPalmer;

public class Similar {
//	private String str1;
//	private String str2;
//	private String dir = "D:/WordNet";
//	private JWS ws = new JWS(dir, "2.1");
//	private String dir = "C:/Program Files (x86)/WordNet";
//	private JWS ws = new JWS(dir, "2.0");
	public static String dir = "D:/WordNet";
	public static JWS ws = new JWS(dir, "2.1");

/*	public Similar(String str1, String str2)
	{
		this.str1 = str1;
		this.str2 = str2;
	}*/
	 
	/*public double getSimilarity()
	{
		String[] strs1 = splitStrings(str1);
		String[] strs2 = splitStrings(str2);
		double sum = 0.0;
		for(String s1 : strs1)
		{
			for(String s2 : strs2)
			{
				double sc = maxScoreOfLine(s1, s2);
				sum += sc;
			}
		}
		//double similarity = sum / (strs1.length * strs2.length);
		double similarity = sum / (strs1.length + Math.abs(strs1.length-strs2.length) / 2);
		sum = 0;
		return similarity;
	}*/
	
	private String[] splitStrings(String str)
	{
		String[] ret = str.split("[ ,.:]+");
		return ret;
	}
	
	public static double maxScoreOfLine(String str1, String str2)
	{
		LeacockAndChodorow lin = ws.getLeacockAndChodorow();
//		AdaptedLesk lin = ws.getAdaptedLesk();
//		WuAndPalmer lin = ws.getWuAndPalmer();
//		Resnik lin = ws.getResnik();
//		Lin lin = ws.getLin();
//		JiangAndConrath lin = ws.getJiangAndConrath();
		
		double sc = lin.max(str1, str2, "n");
		if(sc == 0)
		{
			sc = lin.max(str1, str2, "v");
		}
		return sc;
	}
	
	
	public static void main(String[] args)
	{
		String s1 = "trade";
		String s2 = "smile";
		
//		String s1 = "cpu";
//		String s2 = "cpu";
//		Similar sm = new Similar();
//		System.out.println("相似度　= " + sm.getSimilarity());
		System.out.println("similarity = " + maxScoreOfLine(s1, s2));
	}
	
}
