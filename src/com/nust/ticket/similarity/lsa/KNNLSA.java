package com.nust.ticket.similarity.lsa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import com.nust.ticket.knn.KNNNode;

public class KNNLSA {
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
	 * @param k  随机数的个数
	 * @param max 随机数最大的范围
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
	 * @param a 原矩阵
	 * @param testMatrix 测试矩阵
	 * @param s  SVD分解矩阵
	 * @param i  语义空间中的某句话
	 * @return   距离
	 */
/*	public double calDistance(RealMatrix a, RealMatrix testMatrix, SingularValueDecomposition s, int i)
	{
		RealMatrix inverseS=getInversS(s.getS());//Sigma矩阵的逆矩阵
		RealMatrix transposeU=s.getUT();//U矩阵的转置矩阵
		RealMatrix AdoubleMatrix=countMatrix(inverseS,transposeU,a.getSubMatrix(0, a.getRowDimension()-1, i, i));
		return  1 - getCOS(testMatrix, AdoubleMatrix, inverseS, transposeU);
	}*/

	/**
	 * @param a 
	 * @param testMatrix
	 * @param inverseS
	 * @param transposeU
	 * @param i
	 * @return
	 */
	public double calDistance(RealMatrix mappingMatrix, RealMatrix testMatrix/*, RealMatrix inverseS, RealMatrix transposeU*/, int j)
	{
		RealMatrix AdoubleMatrix = mappingMatrix.getSubMatrix(0, mappingMatrix.getRowDimension()-1, j, j);
		return 1-getCOS(testMatrix, AdoubleMatrix);
//		RealMatrix AdoubleMatrix=countMatrix(inverseS,transposeU,a.getSubMatrix(0, a.getRowDimension()-1, i, i));
//		return  1 - getCOS(testMatrix, AdoubleMatrix, inverseS, transposeU);
//		return  1 - getCOS(testMatrix, a.getSubMatrix(0, a.getRowDimension()-1, i, i), inverseS, transposeU);
	}

	/**
	 * @param datas 训练数据集
	 * @param a  初始矩阵
	 * @param testMatrix 
	 * @param s
	 * @param k
	 * @return
	 */
	public List<String> knn(List<String[]> datas, RealMatrix a, RealMatrix testMatrix, SingularValueDecomposition s,int k) {
//		PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
		List<Integer> randNum = getRandKNum(k, datas.size()); // 产生K个随机数
		
		RealMatrix inverseS=getInversS(s.getS());//Sigma矩阵的逆矩阵
		RealMatrix transposeU=s.getUT();//U矩阵的转置矩阵
		
		//double[][] mapping = new double[inverseS.getRowDimension()][transposeU.getColumnDimension()];
		//double[][] mappingTmp = new double[a.getColumnDimension()][inverseS.getRowDimension()]; 
		System.out.println("将初始矩阵中每一列映射到语义空间中....");
		double[][] mappingTmp = new double[inverseS.getRowDimension()][a.getColumnDimension()];
		for(int i = 0; i < a.getColumnDimension(); i++)
		{
			RealMatrix itarator = a.getSubMatrix(0, a.getRowDimension()-1, i, i);
			RealMatrix mapping1 = countMatrix(inverseS, transposeU, itarator);
			double[] d = getDouble(mapping1);
//			mappingTmp[i] = d;
			for(int j = 0; j < inverseS.getRowDimension(); j++)
			{
				mappingTmp[j][i] = d[j];
			}
		}
		RealMatrix mapping = MatrixUtils.createRealMatrix(mappingTmp);
		System.out.println("映射完成");
		
		/*
		for (int i = 0; i < k; i++) {
			int index = randNum.get(i);
			String[] currData = datas.get(index); // 得到第i条
			String c = currData[2].toString(); // FC2
			KNNNode node = new KNNNode(index, calDistance(a,testMatrix,s,index), c);
			pq.add(node);
		}
		for (int i = 0; i < datas.size(); i++) {
			String[] t = datas.get(i);
			double distance = calDistance(a, testMatrix, s, i);
			KNNNode top = pq.peek();
			if (top.getDistance() > distance) {
				pq.remove();
				pq.add(new KNNNode(i, distance, t[2].toString()));
			}
		}

		return getMostClass(pq);*/
		
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < testMatrix.getColumnDimension(); i++)
		{
			PriorityQueue<KNNNode> pq = new PriorityQueue<KNNNode>(k, comparator);
			RealMatrix temp = testMatrix.getSubMatrix(0, testMatrix.getRowDimension()-1, i, i);
			RealMatrix newMatrix=countMatrix(inverseS,transposeU,temp);//映射到语义空间
			
/*			for(int j = 0; j < k; j++)
			{
				int index = randNum.get(j);
				String[] currData = datas.get(index);
				String c = currData[2];
				KNNNode node = new KNNNode(index, calDistance(mapping, newMatrix, index), c);
//				KNNNode node = new KNNNode(index, Double.MAX_VALUE, c);
				pq.add(node);
			}*/
			
			for(int j = 0; j < k; j++)
			{
				KNNNode node = new KNNNode(j, Double.MAX_VALUE, String.valueOf(j));
				pq.add(node);
			}
			
			for(int j = 0; j < datas.size(); j++)
			{
				String[] t = datas.get(j);
				//double distance = calDistance(a, testMatrix.getSubMatrix(0, testMatrix.getRowDimension()-1, i, i), s, j);
				//double distance = calDistance(a, testMatrix.getSubMatrix(0, testMatrix.getRowDimension()-1, i, i), inverseS, transposeU, j);
				double distance = calDistance(mapping, newMatrix,/* inverseS, transposeU,*/ j);
				KNNNode top = pq.peek();
				if(top.getDistance() > distance)
				{
					pq.remove();
					pq.add(new KNNNode(j, distance, t[2]));
				}
			}
			result.add(getMostClass(pq));
		}
		return result;
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
	
	//matrix utils
	public static RealMatrix getInversS(RealMatrix S){
		return new SingularValueDecomposition(S).getSolver().getInverse();
	}
	
	public static double getCOS(RealMatrix newdoubleMatrix,RealMatrix AdoubleMatrix/*,RealMatrix inverseS,RealMatrix transposeU*/){
		//RealMatrix newMatrix=countMatrix(inverseS,transposeU,newdoubleMatrix);//将查询文档映射到语义空间 inverseS.times(transposeU).times(newdoubleMatrix);
		double[] Adouble=getDouble(AdoubleMatrix);
		double[] ndouble=getDouble(newdoubleMatrix);
		return cosinSim(ndouble,Adouble);
	}
	
	public static RealMatrix countMatrix(RealMatrix inverseS,RealMatrix transposeU,RealMatrix newdoubleMatrix){
		return inverseS.multiply(transposeU).multiply(newdoubleMatrix);//将查询文档映射到语义空间
	}
	
	public static double[] getDouble(RealMatrix doubleMatrix){
		return doubleMatrix.getColumn(0);
	}
	
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
}
