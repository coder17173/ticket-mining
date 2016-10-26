package com.nust.ticket.machinelearning.pos;

import com.njust.NLP.test.Pos;

public class PosTest {
	public static void main(String[] args)
	{
		String s = "space";
		String result = Pos.getPos(s);
		System.out.println(result.split("/")[1]);
	}
}
