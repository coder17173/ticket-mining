package com.nust.ticket.similarity.wordnet.knn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.csvreader.CsvReader;
import com.njust.NLP.test.Pos;
import com.nust.ticket.similarity.wordnet.Similar;
import com.nust.ticket.utils.JCSVUtils;

public class TestKNN {
	public static final String PATH = "." + File.separator + "data"	+ File.separator + "SGT(final).csv";
	public static final String PATH1 = "." + File.separator + "data" + File.separator + "KNN" + File.separator + "3.txt";
	public static final String PATH2 = "." + File.separator + "data" + File.separator + "simmap.txt";
	/**
	 * 从数据文件中读取数据
	 * 
	 * @param datas
	 *            存储数据的集合对象
	 * @param path
	 *            数据文件的路径
	 * @throws IOException
	 */
	public void read(List<String[]> datas, String path) throws IOException {
		try {
			CsvReader reader = new CsvReader(path, ',', Charset.forName("SJIS"));
			while (reader.readRecord()) {
				datas.add(reader.getValues());
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 程序执行入口
	 * 
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		List<String[]> datas = JCSVUtils.readeCsv(PATH);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(PATH1))));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH2))));
		//构建所有concept相似度缓存
		Map<String, Double> wordSimMap = new HashMap<String, Double>();
		List<String> concepts = new ArrayList<>();
		for(String[] data : datas)
		{
			String[] ss = data[0].split(" ");
			for(String s : ss)
			{
				if(!concepts.contains(s))
				{
					concepts.add(s);
				}
			}
		}
		for(int i = 0; i < concepts.size(); i++)
		{
			for(int j = i; j < concepts.size(); j++)
			{
				String s1 = concepts.get(i);
				String s2 = concepts.get(j);
				double sim = Similar.maxScoreOfLine(s1, s2);
				wordSimMap.put(s1+" "+s2, sim);
			}
		}
		/*String line = null;
		for(;(line = br.readLine()) != null;)
		{
			String[] ss = line.trim().split(" ");
			wordSimMap.put(ss[0]+" "+ss[1], Double.valueOf(ss[2]));
		}
		br.close();
		System.out.println("concepts缓存构建完成");*/
	/*	for(String key : wordSimMap.keySet())
		{
			bw.write(key + " " + wordSimMap.get(key));
			bw.newLine();
		}*/
		
		
		//所有concept的IDF
		Map<String, Double> idfMap = new HashMap<String, Double>();
		double size = datas.size();
		for(String s : concepts)
		{
			int count = 0;
			for(String[] t : datas)
			{
				if(t[0].contains(s))
				{
					count++;
				}
			}
			double idf = Math.log(size/count);
			idfMap.put(s, idf);
		}
		System.out.println("IDF缓存构建完成");
		
		//词性标注
		Map<String, String> posMap = new HashMap<String, String>();
		for(String s : concepts)
		{
			String pos = Pos.getPos(s).split("/")[1];
			posMap.put(s, pos);
//			System.out.println(s + " " + pos);
		}
		System.out.println("词性标注完成");
		
		
		// divided into ten groups
		List<List<String[]>> groups = new ArrayList<List<String[]>>();
		for (int i = 0; i < 10; i++) {
			List<String[]> temp = new ArrayList<String[]>();
			for (int j = i * 200; j < (i + 1) * 200; j++) {
				temp.add(datas.get(j));
			}
			groups.add(temp);
		}

		

		for (int k = 1; k <= 15; k = k + 2) {
			double[] precisions = new double[10];
			double[] recalls = new double[10];
			double[] fs = new double[10];
			// 进行十重交叉验证
			for (int i = 0; i < 10; i++) {
				List<String[]> testDataList = groups.get(i);

				List<String[]> trainDataList = new ArrayList<String[]>();
				for (int j = 0; j < 10; j++) {
					if (j != i) {
						trainDataList.addAll(groups.get(j));
					}
				}
				
				Map<String, Integer> categoryMap = new HashMap<String, Integer>();
				for(String[] tt : testDataList)
				{
					String FC2 = tt[2];
					if(categoryMap.containsKey(FC2))
					{
						categoryMap.put(FC2, categoryMap.get(FC2)+1);
					}else {
						categoryMap.put(FC2, 1);
					}
				}

				
				KNN knn = new KNN();
				int correctCount = 0, wrongCount = 0;
				Map<String, Integer> recallComput = new HashMap<String, Integer>();
				
				for (String[] t1 : testDataList) {
					String summary = t1[0];
//					String category = knn.knn(trainDataList, summary, k, wordSimMap);
					String category = knn.knn(trainDataList, summary, k, wordSimMap, idfMap, posMap);
					boolean flag;
					if (category.trim().equals(t1[2].trim())) {
						flag = true;
						correctCount++;
					} else {
						flag = false;
						wrongCount++;
					}

					if (recallComput.containsKey(t1[2])) {
						if(flag == true){
							recallComput.put(t1[2], recallComput.get(t1[2])+1);
						}
					} else {
						if(flag == true){
							recallComput.put(t1[2], 1);
						}
					}
					//System.out.println(category + "--------->" + t1.FC2 + "-------->" + flag);
					//bw.write(category + "--------->" + t1.FC2 + "-------->" + flag);
					//bw.newLine();
				}
				double precision = correctCount / (double) testDataList.size(); // precision
				double tempRecall = 0.0;
				for (String key : recallComput.keySet()) {
					/*double count = 0.0;
					List<Boolean> list = recallComput.get(key);
					for (int j = 0; j < list.size(); j++) {
						if (list.get(j) == true) {
							count++;
						}
					}*/
					double count = recallComput.get(key);
					
					tempRecall = tempRecall + count*1.0/categoryMap.get(key);
				}
				double recall = tempRecall / recallComput.size(); // recall
				double f1 = 2 * (precision * recall) / (precision + recall); // f1 score

				precisions[i] = precision;
				recalls[i] = recall;
				fs[i] = f1;
				//bw.write("---------------------" + i + "重结束------------------------------");
				//bw.newLine();
			}

			double sumprecision = 0.0, sumrecall = 0.0, sumf1 = 0.0;
			for (int i = 0; i < precisions.length; i++) {
				sumprecision += precisions[i];
				sumrecall += recalls[i];
				sumf1 += fs[i];
			}
			System.out.println("------------k = "+ k + "---------------------");
			System.out.println("正确率 = "+sumprecision/10+"    "+ "召回率为 = "+sumrecall/10+"      "+"f1 = "+sumf1/10);
			/*bw.write("------------k = "+ k + "---------------------");
			bw.newLine();
			bw.write("正确率 = "+sumprecision/10+"    "+ "召回率为 = "+sumrecall/10+"      "+"f1 = "+sumf1/10);
			bw.newLine();
			bw.write("---------------------------------------------------");
			bw.newLine();*/
		}
		bw.close();
	}
}
