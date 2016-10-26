package com.nust.ticket.test;

import java.util.Comparator;
import java.util.PriorityQueue;

public class APP {
	
	private static Comparator<Double> comparator = new Comparator<Double>() {
		@Override
		public int compare(Double o1, Double o2) {
			if (o1 > o2) {
				return -1;
			}else if(o1 == o2){
			    return 0;
			}else{
				return 1;
			}
		}
	};
	
	public static void main(String[] args)
	{
		
		PriorityQueue<Double> pq = new PriorityQueue<Double>(3, comparator);
//		PriorityQueue<Integer> p = new PriorityQueue<Integer>(Integer.MAX_VALUE,comparator);
		pq.add(1.0);
		pq.add(5.0);
		pq.add(2.0);
//		System.out.println(pq.poll());
		System.out.println(pq);
		pq.add(3.0);
		System.out.println(pq);
	}
}
