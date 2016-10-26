package com.nust.ticket.feature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.nust.ticket.utils.JCSVUtils;

public class FeatureExtract {
	public static final String PATH = "./data/SGT(final).csv";
	public static final String PATH2 = "./data/feature1.txt";
	public static final String PATH3 = "./data/feature.txt";
	public static void main(String[] args) throws IOException
	{
/*		Map<String, Integer> map = new HashMap<String, Integer>();
	      map.put("图书" , 4);
	      map.put("音像" , 6);
	      map.put("素材" , 9);
	      map.put("音乐" , 8);
	      map.put("影视" , 7);
	      map.put("动漫" , 4);
	      map.put("歌曲" , 3);
	      map.put("图片" , 2);
	      map.put("图标" , 6);
	      ArrayList<Map.Entry<String, Integer>> list = sortMap(map);
	      for(Map.Entry<String, Integer> entry : list)
	      {
	    	  System.out.println(entry.getKey()+":"+entry.getValue());
	      }*/
/*		List<String[]> datas = JCSVUtils.readeCsv(PATH);
		List<String> ss = new ArrayList<String>();
		for(String[] s : datas)
		{
			ss.add(s[0]);
		}
		Map<String, Integer> wordFreq = termFreq(ss);
		ArrayList<Map.Entry<String, Integer>> sortedMap = sortMap(wordFreq);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(PATH2))));
		for(Map.Entry<String, Integer> entry : sortedMap)
		{
			bw.write(entry.getKey() + " " + entry.getValue());
			bw.newLine();
		}
		bw.close();*/
/*		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH2))));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(PATH3))));
		String line = null;
		for(;(line = br.readLine()) != null;)
		{
			String feature = line.split(" ")[0];
			bw.write(feature);
			bw.newLine();
		}
		br.close();
		bw.close();*/
	}
	
	/**
	 * to compute the frequency of each term
	 * @param ss
	 * @return
	 */
	public static Map<String, Integer> termFreq(List<String> ss)
	{
		Map<String, Integer> wordFreq = new HashMap<String, Integer>();
		for(String s : ss)
		{
			String[] splitS = s.split(" ");
			for(String key : splitS)
			{
				if(key.matches("[0-9]+"))
				{
					continue;
				}
				if(wordFreq.containsKey(key))
				{
					wordFreq.put(key, wordFreq.get(key)+1);
				}else {
					wordFreq.put(key, 1);
				}
			}
		}
		return wordFreq;
	}
	
	/**
	 * get top n feature
	 * @param ss
	 * @param topN
	 * @return
	 */
	public ArrayList<String> featureExtract(List<String> ss, int topN)
	{
		Map<String, Integer> wordFreq = termFreq(ss);
		ArrayList<Map.Entry<String,Integer>> entries= sortMap(wordFreq);
		ArrayList<String> feature = new ArrayList<String>();
		for(int i = 0; i < topN; i++)
		{
			feature.add(entries.get(i).getKey());
		}
		return feature;
	}
	
	/**
	 * sort the map by value
	 * @param map
	 * @return 
	 */
	public static ArrayList<Map.Entry<String,Integer>> sortMap(Map<String, Integer> map){
	     List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
	     Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
	         public int compare(Map.Entry<String, Integer> obj1 , Map.Entry<String, Integer> obj2) {
	             return obj2.getValue() - obj1.getValue();
	         }
	     });
	      return  (ArrayList<Entry<String, Integer>>) entries;
	    }
}
