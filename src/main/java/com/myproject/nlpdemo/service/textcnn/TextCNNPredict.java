package com.myproject.nlpdemo.service.textcnn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.tensorflow.Graph;

public class TextCNNPredict {
	private Graph tfGraph;
	private Map<String, Integer> wordVocabMap;
	private Map<Integer, String> categoryMap;
	private TextCNNPredict() {
		try {
			tfGraph = new Graph();
            byte[] graphBytes = IOUtils.toByteArray(TextCNNPredict.class.getClassLoader().getResourceAsStream("model/textcnn/model.pb"));
			tfGraph.importGraphDef(graphBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.wordVocabMap = readVocabMap();
		this.categoryMap = readCategoryMap();
	}
	
	private Map<String, Integer> readVocabMap() {
		Map<String, Integer> wordMap = new HashMap<String, Integer>();
		BufferedReader buffer = null;
		try {
            buffer = new BufferedReader(new InputStreamReader(TextCNNPredict.class.getClassLoader().getResourceAsStream("model/textcnn/words.vocab.txt")));
			int i = 0;
			String line = null;
			while ((line = buffer.readLine())!=null) {
				if (StringUtils.isBlank(line.trim())) {
					continue;
				}
				wordMap.put(line.trim(), i++);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffer != null) try { buffer.close(); } catch (Exception e) {}
		}
		return wordMap;
	}
	private Map<Integer, String> readCategoryMap() {
		Map<Integer, String> categoryMap = new HashMap<Integer, String>();
		BufferedReader buffer = null;
		try {
            buffer = new BufferedReader(new InputStreamReader(TextCNNPredict.class.getClassLoader().getResourceAsStream("model/textcnn/words.cat.txt")));
			int i = 0;
			String line = null;
			while ((line = buffer.readLine())!=null) {
				if (StringUtils.isBlank(line.trim())) {
					continue;
				}
				categoryMap.put(i++, line.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (buffer != null) try { buffer.close(); } catch (Exception e) {}
		}
		return categoryMap;
	}
	
	public Graph getTfGraph() {
		return this.tfGraph;
	}
	
	public Map<String, Integer> getWordVocabMap () {
		return this.wordVocabMap;
	}
	
	public Map<Integer, String> getCategoryMap() {
		return this.categoryMap;
	}
	private static class SingletonHolder {
		private final static TextCNNPredict instance = new TextCNNPredict();
	}
	public static TextCNNPredict getInstance() {
		return SingletonHolder.instance;
	}
}
