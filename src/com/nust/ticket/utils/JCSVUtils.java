package com.nust.ticket.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class JCSVUtils {

	public static final String OPATH1 = "data/SGT(final2).csv";
	public static final String OPATH2 = "data/UGT(final2).csv";
	public static final String DPATH1 = "data/SGT(final).csv";
	public static final String DPATH2 = "data/UGT(final).csv";
	public static void main(String[] args) {
		List<String[]> SGT = readeCsv(OPATH1);
		List<String[]> UGT = readeCsv(OPATH2);
		for(String[] s : SGT)
		{
			s[0] = s[0].toLowerCase();
			s[1] = s[1].toLowerCase();
			s[2] = s[2].toLowerCase();
			s[3] = s[3].toLowerCase();
		}
		writeCsv(DPATH1, SGT);
		
		for(String[] s : UGT)
		{
			s[0] = s[0].toLowerCase();
			s[1] = s[1].toLowerCase();
			s[2] = s[2].toLowerCase();
			s[3] = s[3].toLowerCase();
		}
		writeCsv(DPATH2, UGT);
	}

	/**
	 * 读取CSV文件
	 */
	public static ArrayList<String[]> readeCsv(String filepath) {
		ArrayList<String[]> csvList = new ArrayList<String[]>(); // 用来保存数据
		try {

			// String csvFilePath = "G:/test.csv";
			String csvFilePath = filepath;
			CsvReader reader = new CsvReader(csvFilePath, ',',	Charset.forName("SJIS")); // 一般用这编码读就可以了

//			reader.readHeaders(); // 跳过表头 如果需要表头的话，不要写这句。

			while (reader.readRecord()) { // 逐行读入除表头的数据
				csvList.add(reader.getValues());
			}
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex);
		}
		return csvList;
	}

	/**
	 * 写入CSV文件
	 */
	public static void writeCsv(String filepath,List<String[]> contentlist) {
		try {

			String csvFilePath = filepath;
			CsvWriter wr = new CsvWriter(csvFilePath, ',',
					Charset.forName("SJIS"));
			for(String[] contents:contentlist){
				wr.writeRecord(contents);
			}
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
