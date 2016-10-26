package com.nust.ticket.similarity.wordnet.knn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.njust.NLP.test.Pos;
import com.nust.ticket.knn.KNNNode;

public class KNN {
	/**
	 * 设置优先级队列的比较函数，距离越大，优先级越高
	 */
	private Comparator<KNNNode> comparator = new Comparator<KNNNode>() {
		public int compare(KNNNode o1, KNNNode o2) {
			if (o1.getDistance() >= o2.getDistance()) {
				return 1;
			} else {
				return 0;
			}
		}
	};

	/**
	 * 获取K个不同的随机数
	 * 
	 * @param k
	 *            随机数的个数
	 * @param max
	 *            随机数最大的范围
	 * @return 生成的随机数数组
	 */
	public List<Integer> getRandKNum(int k, int max) {
		List<Integer> rand = new ArrayList<Integer>(k);
		for (int i = 0; i < k; i++) {
			int temp = (int) (Math.random() * max);
			if (!rand.contains(temp)) {
				rand.add(temp);
			} else {
				i--;
			}
		}
		return rand;
	}


	/**
	 * wordnet distance融合方案一
	 * @param str1
	 * @param str2
	 * @param wordSimMap
	 * @return
	 */
	public double wordNetDistance(String str1, String str2, Map<String, Double> wordSimMap)
	{
		String[] ss1 = str1.split(" ");
		String[] ss2 = str2.split(" ");
		double sim = 0.0, sim1 = 0.0, sim2 = 0.0;
		for(String s1 : ss1)
		{
			sim1 += maxSim(s1, ss2, wordSimMap);
		}
		for(String s2 : ss2)
		{
			sim2 += maxSim(s2, ss1, wordSimMap);
		}
		sim = (sim1/ss1.length + sim2/ss2.length)/2;
		return 1 - sim;
	}

	/**
	 * wordnet similarity融合方案二
	 * take the idf into account
	 * @param str1
	 * @param str2
	 * @param wordSimMap
	 * @param idfMap
	 * @return
	 */
	public double wordNetDistance2(String str1, String str2, Map<String, Double> wordSimMap, Map<String, Double> idfMap)
	{
		String[] ss1 = str1.split(" ");
		String[] ss2 = str2.split(" ");
		double sim = 0.0, sim1 = 0.0, sim2 = 0.0;
		double idf1 = 0.0, idf2 = 0.0;
		for(String s1 : ss1)
		{
			double alpha = idfMap.get(s1);
			sim1 += maxSim(s1, ss2, wordSimMap)*alpha;
			idf1 += alpha;
		}
		for(String s2 : ss2)
		{
			double beta = idfMap.get(s2);
			sim2 += maxSim(s2, ss1, wordSimMap)*beta;
			idf2 += beta;
		}
		sim = (sim1/idf1 + sim2/idf2)/2;
		return 1 - sim;
	}
	
	
	/**
	 * wordnet similarity融合方案三
	 * take the pos into account
	 * @param str1
	 * @param str2
	 * @param wordSimMap
	 * @return
	 */
	public double wordNetDistance3(String str1, String str2, Map<String, Double> wordSimMap, Map<String, String> posMap)
	{
		String[] ss1 = str1.split(" ");
		String[] ss2 = str2.split(" ");
		double sim = 0.0, sim1 = 0.0, sim2 = 0.0;
		for(String s1 : ss1)
		{
			sim1 += maxSim2(s1, ss2, wordSimMap, posMap);
		}
		for(String s2 : ss2)
		{
			sim2 += maxSim2(s2, ss1, wordSimMap, posMap);
		}
		sim = (sim1/ss1.length + sim2/ss2.length)/2;
		return 1 - sim;
	}

	
	/**
	 * wordnet similarity融合方案四
	 * take the pos and idf into account
	 * @param str1
	 * @param str2
	 * @param wordSimMap
	 * @param posMap
	 * @param idfMap
	 * @return
	 */
	public double wordNetDistance4(String str1, String str2, Map<String, Double> wordSimMap, Map<String, String> posMap, Map<String, Double> idfMap)
	{
		String[] ss1 = str1.split(" ");
		String[] ss2 = str2.split(" ");
		double sim = 0.0, sim1 = 0.0, sim2 = 0.0;
		double idf1 = 0.0, idf2 = 0.0;
		for(String s1 : ss1)
		{
			double alpha = idfMap.get(s1);
			sim1 += maxSim2(s1, ss2, wordSimMap, posMap)*alpha;
			idf1 += alpha;
		}
		for(String s2 : ss2)
		{
			double beta = idfMap.get(s2);
			sim2 += maxSim2(s2, ss1, wordSimMap, posMap)*beta;
			idf2 += beta;
		}
		sim = (sim1/idf1 + sim2/idf2)/2;
		return 1 - sim;
	}
	
	/**
	 * normal
	 * @param s
	 * @param ss
	 * @param wordSimMap
	 * @return
	 */
	public double maxSim(String s, String[] ss, Map<String, Double> wordSimMap)
	{
		double max = 0.0;
		for(String tmp : ss)
		{
			if(wordSimMap.containsKey(s+" "+tmp))
			{
				double d = wordSimMap.get(s+" "+tmp);
				if(d > max)
				{
					max = d;
				}
			}else {
				double d = wordSimMap.get(tmp+" "+s);
				if(d > max)
				{
					max = d;
				}
			}
		}
		return max;
	}
	
	/**
	 * take the pos into account
	 * @param s
	 * @param ss
	 * @param wordSimMap
	 * @return
	 */
	public double maxSim2(String s, String[] ss, Map<String, Double> wordSimMap, Map<String, String> posMap)
	{
		double max = 0.0;
		String pos = posMap.get(s);
		for(String tmp : ss)
		{
			if(posMap.get(tmp).equals(pos))
			{
				if(wordSimMap.containsKey(s+" "+tmp))
				{
					double d = wordSimMap.get(s+" "+tmp);
					if(d > max)
					{
						max = d;
					}
				}else {
					double d = wordSimMap.get(tmp+" "+s);
					if(d > max)
					{
						max = d;
					}
				}
			}
		}
		return max;
	}
	/**
	 * knn for vector space model
	 * @param datas
	 * @param testData
	 * @param k
	 * @return
	 */
	public String knn(List<String[]> datas, String testData, int k, Map<String, Double> wordSimMap, Map<String, Double> idfMap, Map<String, String> posMap)
	{
		PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k,comparator);
		List<Integer> randNum = getRandKNum(k, datas.size()); // 产生K个随机数
		for (int i = 0; i < k; i++) {
			int index = randNum.get(i);
			String currData = datas.get(index)[0]; // 得到第i条
			String c = datas.get(index)[3];
//			KNNNode node = new KNNNode(index, wordNetDistance(testData, currData, wordSimMap), c);
//			KNNNode node = new KNNNode(index, wordNetDistance2(testData, currData, wordSimMap, idfMap), c);
			KNNNode node = new KNNNode(index, wordNetDistance3(testData, currData, wordSimMap, posMap), c);
//			KNNNode node = new KNNNode(index, wordNetDistance4(testData, currData, wordSimMap, posMap, idfMap), c);
			pq.add(node);
		}
		
		for (int i = 0; i < datas.size(); i++) {
			String s = datas.get(i)[0];
			String c = datas.get(i)[2];
//			double distance = wordNetDistance(testData, s, wordSimMap);
//			double distance = wordNetDistance2(testData, s, wordSimMap, idfMap);
			double distance = wordNetDistance3(testData, s, wordSimMap, posMap);
//			double distance = wordNetDistance4(testData, s, wordSimMap, posMap, idfMap);
			KNNNode top = pq.peek();
			if (top.getDistance() > distance) {
				pq.remove();
				pq.add(new KNNNode(i, distance, c));
			}
		}
		return getMostClass(pq);
	}
	

	/**
	 * 获取所得到的k个最近邻元组的多数类
	 * @param pq  存储k个最近近邻元组的优先级队列
	 * @return 多数类的名称
	 */
	private String getMostClass(PriorityQueue<KNNNode> pq) {
		Map<String, Integer> classCount = new HashMap<String, Integer>();
		for (int i = 0; i < pq.size(); i++) {
			KNNNode node = pq.remove();
			String c = node.getC();
			if (classCount.containsKey(c)) {
				classCount.put(c, classCount.get(c) + 1);
			} else {
				classCount.put(c, 1);
			}
		}
		int maxIndex = -1;
		int maxCount = 0;
		Object[] classes = classCount.keySet().toArray();
		for (int i = 0; i < classes.length; i++) {
			if (classCount.get(classes[i]) > maxCount) {
				maxIndex = i;
				maxCount = classCount.get(classes[i]);
			}
		}
		return classes[maxIndex].toString();
	}
}
