package com.nust.ticket.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class String2Vector {
	public static void main(String[] args)
	{
		int n = StringUtils.countMatches("11 12 22", "12");
		System.out.println(n);
	}
	
	/**
	 * turn string to vector(value is 0/1)
	 * @param feature
	 * @param ss
	 * @return vectors
	 */
	public static double[][] binary(List<String> feature, List<String> ss)
	{
		int size = ss.size();
		int featureSize = feature.size();
		double[][] vectors = new double[size][featureSize];
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < featureSize; j++)
			{
				if(ss.get(i).contains(feature.get(j)))
				{
					vectors[i][j] = 1;
				}else {
					vectors[i][j] = 0;
				}
			}
		}
		return vectors;
	}
	
	
	/**
	 * turn string to vector(value is Term frequency)
	 * @param feature
	 * @param ss
	 * @return vectors
	 */
	public static double[][] TF(List<String> feature, List<String> ss)
	{
		int size = ss.size();
		int featureSize = feature.size();
		double[][] vectors = new double[size][featureSize];
		for(int i = 0; i < ss.size(); i++)
		{
			for(int j = 0; j < featureSize; j++)
			{
				int n = StringUtils.countMatches(ss.get(i), feature.get(j));
				vectors[i][j] = n;
			}
		}
		return vectors;
	}
	
	
	/**
	 * turn string to vector(value is Document frequency )
	 * @param feature
	 * @param ss
	 * @return
	 */
	public static double[][] DF(List<String> feature, List<String> ss)
	{
		int size = ss.size();
		int featureSize = feature.size();
		double[][] vectors = new double[size][featureSize];
		Map<String, Integer> featureFreq = documentFreq(feature, ss);
		
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < featureSize; j++)
			{
				String key = feature.get(j);
				if(ss.get(i).contains(key))
				{
					vectors[i][j] = featureFreq.get(key);
				}else{
					vectors[i][j] = 0;
				}
			}
		}
		return vectors;
	}
	
	
	/**
	 * turn string to vector(value is Inverse document frequency)
	 * @param feature
	 * @param ss
	 * @return
	 */
	public static double[][] IDF(List<String> feature, List<String> ss)
	{
		int size = ss.size();
		int featureSize = feature.size();
		double[][] vectors = new double[size][featureSize];
		double[][] tmp = DF(feature, ss);
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < featureSize; j++)
			{
				if(tmp[i][j] != 0)
				{
					vectors[i][j] = 1.0/tmp[i][j];
				}else{
					vectors[i][j] = 0;
				}
			}
		}
		return vectors;
	}
	
	
	/**
	 * turn string to vector(value is TF-IDF)
	 * @param feature
	 * @param ss
	 * @return
	 */
	public static double[][] TFIDF(List<String> feature, List<String> ss)
	{
		int size = ss.size();
		int featureSize = feature.size();
		double[][] vectors = new double[size][featureSize];
		double[][] tf = TF(feature, ss);
		
		Map<String, Double> featureIDF = new HashMap<String, Double>();
		for(int i = 0; i < feature.size(); i++)
		{
			String key = feature.get(i);
			int count = 0;
			for(String s : ss)
			{
				if(s.contains(key))
				{
					count++;
				}
			}
			featureIDF.put(key, Math.log(size*1.0/count));
		}
		
/*		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < featureSize; j++)
			{
				vectors[i][j] = tf[i][j]*featureIDF.get(feature.get(j));
			}
		}*/
		for(int i = 0; i < featureSize; i++)
		{
			double idf = featureIDF.get(feature.get(i));
			for(int j = 0; j < size; j++)
			{
				vectors[j][i] = tf[j][i]*idf;
			}
		}
		
		return vectors;
	}
	
	
	
	public static Map<String, Integer> documentFreq(List<String> feature, List<String> ss)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < feature.size(); i++)
		{
			String key = feature.get(i);
			int count = 0;
			for(String s : ss)
			{
				if(s.contains(key))
				{
					count++;
				}
			}
			map.put(key, count);
		}
		return map;
	}
}
