package com.nust.ticket.similarity.lsa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import com.nust.ticket.utils.JCSVUtils;


public class TestKNNLSA {
	private static String[] stopwords = { "and", "edition", "for", "in", "little", "of", "the", "to" };
    private static String[] ignorechars = { "'", "\"", ",", ":", "!" };
    private static String newString1="CPU Workload High CPU _Total busy time";//新的文档
    private static String newString2="ITM Agent Offline Primary CT11PWPBMC001 NT";
    
	public static void main(String[] args)
	{
		List<String[]> datauid2 = JCSVUtils.readeCsv(getPath2("train1"));
		List<List<String>> titleList = new ArrayList<List<String>>();
		for (int i = 0; i < datauid2.size(); i++) {
			String[] wordListtemp = datauid2.get(i)[0].split(" ");
			List<String> wordList = new ArrayList<String>();
			for (String w : wordListtemp) {
				boolean flag = true;
				for (String s : stopwords) {
					if (w.toLowerCase().equals(s)) {
						flag = false;
						break;
					}
				}
				if (flag == false) {
					continue;
				}
/*				for (String isString : ignorechars) {
					if (w.contains(isString)) {
						w = w.replace(isString, "");
					}
				}*/
				if (flag != false) {
					wordList.add(w.toLowerCase());
				}
			}
			titleList.add(wordList); //去除停用词和符号的文档集合
		}
		
		//实词总数量
		HashSet<String> wordSet = new HashSet<String>();
		for (List<String> sList : titleList) {
			for (String s : sList) {
				wordSet.add(s);
			}
		}
		List<String> wordSetList = new ArrayList<String>();
		for (String s : wordSet) {
			wordSetList.add(s);
		}
		
		//构造初始举矩阵
		System.out.println("构造初始矩阵....");
		RealMatrix A = MatrixUtils.createRealMatrix(createMatrix(wordSetList, titleList));
		System.out.println("初始矩阵构造完成");
		//SVD分解
		System.out.println("SVD分解....");
		SingularValueDecomposition s = new SingularValueDecomposition(A); //奇异值分解
		System.out.println("SVD分解完成");
		
		System.out.println("开始预测....");
		
		List<String[]> tests = JCSVUtils.readeCsv(getPath2("test3"));
		List<String> list = new ArrayList<String>();
		List<String> realType = new ArrayList<String>();
		for(String[] s1 : tests)
		{
			list.add(s1[0]);
			realType.add(s1[2]);
		}
		RealMatrix testMatrix = getTestMatrix(wordSetList, list);
		System.out.println("测试矩阵构造完成");
		
		long start = System.currentTimeMillis();
		KNNLSA knn = new KNNLSA();
		List<String> predict = knn.knn(datauid2, A, testMatrix, s, 9);
		long end = System.currentTimeMillis();
		System.out.println("耗时"+(end-start));
//		System.out.println(predict);
		System.out.println();
		int count = 0;
		for(int i = 0; i < predict.size(); i++)
		{
			if(predict.get(i).equals(realType.get(i)))
			{
				count++;
			}
			System.out.println(predict.get(i)+"            "+realType.get(i));
		}
		System.out.println(count);
		
		
		/*List<String> list = new ArrayList<String>();
		list.add("CPU Workload High CPU _Total busy time");
		list.add("ITM Agent Offline Primary CT11PWPBMC001 NT");
		RealMatrix testMatrix = getTestMatrix(wordSetList, list);
		System.out.println("测试矩阵构造完成");
		
		long start = System.currentTimeMillis();
		KNNLSA knn = new KNNLSA();
		List<String> predict = knn.knn(datauid2, A, testMatrix, s, 5);
		long end = System.currentTimeMillis();
		System.out.println("耗时"+(end-start));
		System.out.println(predict);*/
		
		/*//处理待分类数据
		RealMatrix newdoubleMatrix1 = getTestMatrix(wordSetList, newString1);
		RealMatrix newdoubleMatrix2 = getTestMatrix(wordSetList, newString2);
		
		//---------------------------------
		
		KNNLSA knn = new KNNLSA();
		String category1 = knn.knn(datauid2, A, newdoubleMatrix1, s, 5);
		String category2 = knn.knn(datauid2, A, newdoubleMatrix2, s, 5);
		System.out.println("预测类别为："+category1);
		System.out.println("预测类别为："+category2);*/
		
	}
	
	
	public static RealMatrix getTestMatrix(List<String> wordSetList, List<String> test)
	{
		double[][] newdouble=new double[wordSetList.size()][test.size()];
		for(int i = 0; i < test.size(); i++)
		{
			String[] newStringArray = test.get(i).split(" ");
			List<String> newStringsList=new ArrayList<String>();
			for (int j = 0; j < newStringArray.length; j++) {
				boolean flag = true;
				for (String ss : stopwords) {
					if (newStringArray[j].toLowerCase().equals(ss)) {
						flag = false;
						break;
					}
				}
				if (flag == false) {
					continue;
				}
				if (flag != false) {
					newStringsList.add(newStringArray[j].toLowerCase());
				}
			}
			for(int j=0;j<wordSetList.size();j++){
				newdouble[j][i]=wordcount(newStringsList, wordSetList.get(j));//查询文档的词向量
			}
		}
		RealMatrix newdoubleMatrix=MatrixUtils.createRealMatrix(newdouble);
		return newdoubleMatrix;
	}
	
	
	public static RealMatrix getTestMatrix(List<String> wordSetList, String s)
	{
		String[] newStringArray = s.split(" ");
		List<String> newStringsList=new ArrayList<String>();
		for (int i = 0; i < newStringArray.length; i++) {
			boolean flag = true;
			for (String ss : stopwords) {
				if (newStringArray[i].toLowerCase().equals(ss)) {
					flag = false;
					break;
				}
			}
			if (flag == false) {
				continue;
			}
/*			for (String isString : ignorechars) {
				if (newStringArray[i].contains(isString)) {
					newStringArray[i] = newStringArray[i].replace(isString, "");
				}
			}*/
			if (flag != false) {
				newStringsList.add(newStringArray[i].toLowerCase());
			}
		}
		
		double[][] newdouble=new double[wordSetList.size()][1];
		for(int i=0;i<wordSetList.size();i++){
			newdouble[i][0]=wordcount(newStringsList, wordSetList.get(i));//查询文档的词向量
		}
		
		RealMatrix newdoubleMatrix=MatrixUtils.createRealMatrix(newdouble);
		return newdoubleMatrix;
	}
	
	
	/**
	 * 构建词-文档矩阵
	 * @param wordSetList 文档中所有的词
	 * @param titleList 所有文档
	 * @return
	 */
	public static double[][] createMatrix(List<String> wordSetList,List<List<String>> titleList){
		double[][] matrixStrings = new double[wordSetList.size()][titleList.size()];// 构建矩阵数组
		int k;
		for (int i = 0; i < titleList.size(); i++) {
			for (int j = 0; j < wordSetList.size(); j++) {
				k=wordcount(titleList.get(i),wordSetList.get(j));//计算词在每个文档中出现的次数
				matrixStrings[j][i] = k;
			}
		}
		return matrixStrings;
	}
	
	public static int wordcount(List<String> list,String s){
		int k=0;
		for(String slist:list){
			if(s.equals(slist)){
				k++;
			}
		}
		return k;
	}
	
	private static String getPath2(String string) {
		return "." + File.separator + "data" + File.separator + "LSA" + File.separator + "SGT("
				+ string + ").csv";
	}
}

