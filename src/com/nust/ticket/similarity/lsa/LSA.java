package com.nust.ticket.similarity.lsa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import com.nust.ticket.utils.JCSVUtils;

//import org.apache.lucene.search.FieldCache.DoubleParser;



public class LSA {
	
	private static String[] stopwords = { "and", "edition", "for", "in", "little", "of", "the", "to" };

	private static String[] ignorechars = { "'", "\"", ",", ":", "!" };

	private static String newString="ITM Agent Offline Primary CT11PWPBMC001 NT";//新的文档
	
	private static int Acolumn=0;//第Vcolumn篇文档
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String[]> datauid2 = JCSVUtils.readeCsv(getPath2("train"));
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
				for (String isString : ignorechars) {
					if (w.contains(isString)) {
						w = w.replace(isString, "");
					}
				}
				if (flag != false) {
					wordList.add(w.toLowerCase());
				}
			}
			titleList.add(wordList); //去除停用词和符号的文档集合
		}
		// System.out.println(titleList);
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
		System.out.println("总的实词数量："+wordSetList.size());//总的实词数量
		
		RealMatrix A = MatrixUtils.createRealMatrix(createMatrix(wordSetList, titleList));

		// compute the singular vallue decomposition
		System.out.println("A = U S V^T");
		
		long startTime=System.currentTimeMillis();
		SingularValueDecomposition s=new SingularValueDecomposition(A); //奇异值分解
		long endTime=System.currentTimeMillis();
		System.out.println("奇异值分解耗时"+(endTime-startTime)+"ms");
		
		String[] newStringArray=newString.split(" ");
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
			for (String isString : ignorechars) {
				if (newStringArray[i].contains(isString)) {
					newStringArray[i] = newStringArray[i].replace(isString, "");
				}
			}
			if (flag != false) {
				newStringsList.add(newStringArray[i].toLowerCase());
			}
		}
		
		double[][] newdouble=new double[wordSetList.size()][1];
		for(int i=0;i<wordSetList.size();i++){
			newdouble[i][0]=wordcount(newStringsList, wordSetList.get(i));//查询文档的词向量
		}
		
		RealMatrix newdoubleMatrix=MatrixUtils.createRealMatrix(newdouble);
//		System.out.println("词语数"+newdouble.length);
//
//		RealMatrix inverseS=getInversS(S);//Sigma矩阵的逆矩阵
////		RealMatrix transposeU=U.getSubMatrix(0, U.getRowDimension()-1, 0, rank-1).transpose();//U矩阵的转置矩阵
//		RealMatrix transposeU=s.getUT();//U矩阵的转置矩阵
		System.out.println(getLSACOS(A,newdoubleMatrix,s,Acolumn));
		System.out.println(getLSACOS2(s,5,7));
//		System.out.println("耗时"+(endTime-startTime)+"ms");
	}
	
	/**
	 * 得到LSA的相似性度量值，一个新的句子和语义空间中某句话进行比较
	 * @param A 原矩阵
	 * @param newdoubleMatrix 新的矩阵
	 * @param s 奇异值分解矩阵
	 * @param j 语义空间中的某句话
	 * @return
	 */
	public static double getLSACOS(RealMatrix A,RealMatrix newdoubleMatrix,SingularValueDecomposition s,int j){
		RealMatrix inverseS=getInversS(s.getS());//Sigma矩阵的逆矩阵
		RealMatrix transposeU=s.getUT();//U矩阵的转置矩阵
		RealMatrix AdoubleMatrix=countMatrix(inverseS,transposeU,A.getSubMatrix(0, A.getRowDimension()-1, j, j));
		return getCOS(newdoubleMatrix, AdoubleMatrix,inverseS, transposeU);
	}
	
	/**
	 * 计算语义空间中两篇文档之间的相似性
	 * @param s 奇异值分解矩阵
	 * @param j 其中一篇文档
	 * @param q 另一篇文档
	 * @return
	 */
	public static double getLSACOS2(SingularValueDecomposition s,int j,int q){
		RealMatrix VT=s.getVT();
		RealMatrix jMatrix=s.getS().multiply(VT.getSubMatrix(0, VT.getRowDimension()-1, j, j));
		RealMatrix qMatrix=s.getS().multiply(VT.getSubMatrix(0, VT.getRowDimension()-1, q, q));
		double[] jdouble=getDouble(jMatrix);
		double[] qdouble=getDouble(qMatrix);
		return cosinSim(jdouble,qdouble);
	}
	
	public static RealMatrix getInversS(RealMatrix S){
		return new SingularValueDecomposition(S).getSolver().getInverse();
	}
	
	public static double getCOS(RealMatrix newdoubleMatrix,RealMatrix AdoubleMatrix,RealMatrix inverseS,RealMatrix transposeU){
		RealMatrix newMatrix=countMatrix(inverseS,transposeU,newdoubleMatrix);//将查询文档映射到语义空间 inverseS.times(transposeU).times(newdoubleMatrix);
		double[] Adouble=getDouble(AdoubleMatrix);
		double[] ndouble=getDouble(newMatrix);
		return cosinSim(ndouble,Adouble);
	}
	
	
	public static double[] getDouble(RealMatrix doubleMatrix){
		return doubleMatrix.getColumn(0);
	}
	
	public static void printMatrix(RealMatrix m){
        double[][] d = m.getData();

        for(int row = 0; row < d.length; row++){
            for(int col = 0; col < d[row].length; col++){
                System.out.printf("%6.4f\t", m.getEntry(row, col));
            }
            System.out.println();
        }
        System.out.println();
    }
	
	/**
	 * 计算两个向量之间余弦相似度
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double cosinSim(double[] v1, double[] v2)
	{
		double sim = 0.0;
		double a = 0.0, b = 0.0, c = 0.0;
		for(int i = 0; i < v1.length; i++)
		{
			a += v1[i] * v2[i];
			b += v1[i] * v1[i];
			c += v2[i] * v2[i];
		}
		sim = a / (Math.sqrt(b) * Math.sqrt(c));
		return sim;
	}
	

	/**
	 * 将查询文档映射到语义空间
	 * @param inverseS Sigma矩阵的逆矩阵
	 * @param transposeU U矩阵的转置矩阵
	 * @param newdoubleMatrix 查询文档的词向量矩阵
	 * @return
	 */
	public static RealMatrix countMatrix(RealMatrix inverseS,RealMatrix transposeU,RealMatrix newdoubleMatrix){
		return inverseS.multiply(transposeU).multiply(newdoubleMatrix);//将查询文档映射到语义空间
	}
	
	/**
	 * 构建词-文档矩阵
	 * @param wordSetList 文档中所有的词
	 * @param titleList 所有文档
	 * @return
	 */
	public static double[][] createMatrix(List<String> wordSetList,List<List<String>> titleList){
		List<Map<String, Integer>> worddocList = new ArrayList<Map<String, Integer>>();
		double[][] matrixStrings = new double[wordSetList.size()][titleList.size()];// 构建矩阵数组
		int k;
		for (int i = 0; i < titleList.size(); i++) {
//			Map<String, Integer> wordMap = new HashMap<String, Integer>();
			for (int j = 0; j < wordSetList.size(); j++) {
				k=wordcount(titleList.get(i),wordSetList.get(j));//计算词在每个文档中出现的次数
//				wordMap.put(wordSetList.get(j), k);
				matrixStrings[j][i] = k;
			}
//			System.out.println(wordMap);
//			worddocList.add(wordMap);
		}
	//	Matrix A = new Matrix(matrixStrings);
		return matrixStrings;
		
	}
	
	
	/**
	 * 计算文档中某个词出现的次数
	 * @param list 一篇文档
	 * @param s 语义空间中的词
	 * @return
	 */
	public static int wordcount(List<String> list,String s){
		int k=0;
		for(String slist:list){
			if(s.equals(slist)){
				k++;
			}
		}
		return k;
	}

	private static String getPath(String string) {
		return "." + File.separator + "data" + File.separator + "UGT("
				+ string + ").csv";
	}
	
	private static String getPath2(String string) {
		return "." + File.separator + "data" + File.separator + "LSA" + File.separator + "SGT("
				+ string + ").csv";
	}
	
}
