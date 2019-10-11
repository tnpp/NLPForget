package com.myproject.nlpdemo.service.textcnn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.myproject.nlpdemo.utils.MathsUtils;
import com.myproject.nlpdemo.utils.TextHandler;

@Service
public class TfTextCnnPredictService {
	private final static Logger logger = LoggerFactory.getLogger(TfTextCnnPredictService.class);
	
	public static void main(String[] args) throws IOException {
		TfTextCnnPredictService s = new TfTextCnnPredictService();
        System.out.println("消火栓增压稳压装置:" + s.getLabel("消火栓增压稳压装置"));
        System.out.println("DN150 止回阀: " + s.getLabel("DN150 止回阀"));
        System.out.println("人防插板阀: " + s.getLabel("人防插板阀"));
        System.out.println("LED120W 路灯: " + s.getLabel("LED120W 路灯"));
		System.out.println("消防栓: "+s.getLabel("消防栓"));
	}
	public List<String> getLabel(String text) {
		text = TextHandler.thesaurusMerge(text);
		int[][] arr = getTextToId(text, TextCNNPredict.getInstance().getWordVocabMap());
		Graph graph = TextCNNPredict.getInstance().getTfGraph();
		float[][] y = new float[][]{{1}};
		try (Session session = new Session(graph);
				Tensor input = Tensor.create(arr);
				Tensor x = Tensor.create(1.0f);
				Tensor result = session.runner().feed("input_x", input).feed("keep_prob", x).feed("input_y", Tensor.create(y)).fetch("score/fc2/BiasAdd").run().get(0);){
			
			// Runner runner = session.runner().feed("input_x", input).feed("keep_prob", x).feed("input_y", Tensor.create(y));
			long[] rshape = result.shape();
			int nlabels = (int) rshape[1];
			
			float[][] vector = (float[][])result.copyTo(new float[1][nlabels]);
			if (vector == null || vector.length<1) {
				System.out.println("Cannot get result!");
			} else {
				Map<Integer, String> categoryMap = TextCNNPredict.getInstance().getCategoryMap();
				List<String> scoreList = new ArrayList<String>();
				float[] scores = vector[0];
				for (int i=0; i<scores.length; i++) {
					String scoreItem = scores[i] + "_" +i+"_"+categoryMap.get(i);
					scoreList.add(scoreItem);
				}
				Collections.sort(scoreList, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						float score1 = Float.parseFloat(o1.substring(0, o1.indexOf('_')));
						float score2 = Float.parseFloat(o2.substring(0, o2.indexOf('_')));
						return score1>score2?-1:1;
					}
					
				});
                // System.out.println(scoreList);
				List<String> labels = new ArrayList<String>();
				List<Float> maxs = MathsUtils.getMaxs(scores);
				for (int i=0; i<maxs.size(); i++) {
					String item = scoreList.get(i);
					labels.add(item.substring(item.lastIndexOf('_')+1));
				}
				return labels;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	private int[][] getTextToId(String text, Map<String, Integer> word_to_id) {
		int[][] xpad = new int[1][600];
		if (StringUtils.isBlank(text)) {
			return xpad;
		}
		char[] chs = text.trim().toLowerCase().toCharArray();
		List<Integer> list = new ArrayList<Integer>();
		for (int i=0; i<chs.length; i++) {
			String element = Character.toString(chs[i]);
			if (word_to_id.containsKey(element)) {
				list.add(word_to_id.get(element));
			}
		}
		if (list.size() == 0) {
			return xpad;
		}
		int size = list.size();
		Integer[] targetInter = (Integer[])list.toArray(new Integer[size]);
		int[] target = Arrays.stream(targetInter).mapToInt(Integer::valueOf).toArray();
		if (size <= 600) {
			System.arraycopy(target, 0, xpad[0], xpad[0].length-size, target.length);
		} else {
			System.arraycopy(target, size-xpad[0].length, xpad[0], 0, xpad[0].length);
		}
		return xpad;
	}
}
