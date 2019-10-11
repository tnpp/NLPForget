package com.myproject.nlpdemo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MathsUtils {
	public static float getMean(float[] arr) { 
		float sum = 0; 
        int number = arr.length; 
        for (int i = 0; i < number; i++) { 
            sum += arr[i]; 
        } 
        return sum / number; 
    } 
	public static float getStandardDevition(float[] arr) { 
		float sum = 0; 
        int number = arr.length; 
        float avgValue = getMean(arr);//获取平均值 
        for (int i = 0; i < number; i++) { 
            sum += Math.pow((arr[i] - avgValue), 2); 
        } 

        return (float)Math.sqrt((sum / (number - 1))); 
    } 
	public static float getVariance(float[] arr) { 
		float dAve=getMean(arr);//求平均值
		float dVar=0;
		for(int i=0;i<arr.length;i++){//求方差
			dVar+=(arr[i]-dAve)*(arr[i]-dAve);
		}
		return dVar/arr.length;
	}
	
	/**
	 * 求最大值
	 * @param arr
	 * @return
	 */
	public static float getMax(float[] arr) {
		float max = Float.MIN_VALUE;
		for (float x:arr) {
			if (x > max) {
				max = x;
			}
		}
		return max;
	}
	
	/**
	 * 
	 * @param arr
	 * @return
	 */
	public static List<Float> getMaxs(float[] arr) {
		float max = getMax(arr);
		float stdev = getStandardDevition(arr);
		float split = max - (stdev * 0.5f);
		List<Float> result = new ArrayList<>();
		for (float a:arr) {
			if (a >= split) {
				result.add(a);
			}
		}
		return result;
		
	}
	
	public static float[] getFloats(double[] ds) {
		float[] fs = new float[ds.length];
		for (int i=0; i<ds.length; i++) {
			fs[i] = (float)ds[i];
		}
		return fs;
	}
	/**
     * 求中位数
     *
     * @param arr
     * @return
     */
    public static float getMedian(float[] arr) {
        float[] tempArr = Arrays.copyOf(arr, arr.length);
        Arrays.sort(tempArr);
        if (tempArr.length % 2 == 0) {
            return (tempArr[tempArr.length >> 1] + tempArr[(tempArr.length >> 1) - 1]) / 2;
        } else {
            return tempArr[(tempArr.length >> 1)];
        }
    }

	public static void main(String[] args){
		Double[] fsObj = {-9.664124, -15.916133, -8.196134, -6.895283, -3.7409256, -9.271866, -3.570164, -4.632916, -6.3934264, -0.6245208, -12.422898, -8.948807, -10.098067, -3.1879117, -10.054275, 2.936968, 10.042135, -3.9005702, -3.2892854, -7.0870132, -4.5488915, -0.6163413, -8.403946, -7.936515, -10.76196, -10.574102, -5.585893, -5.0456195, -9.817365, -10.352768, -6.801859, -16.870096, -9.351337, -14.234166, -6.2163506};
		List<Double> list = Arrays.asList(fsObj);
		Collections.sort(list);
		System.out.println(list);
		
		float [] fs = getFloats( new double[]{-9.664124, -15.916133, -8.196134, -6.895283, -3.7409256, -9.271866, -3.570164, -4.632916, -6.3934264, -0.6245208, -12.422898, -8.948807, -10.098067, -3.1879117, -10.054275, 2.936968, 10.042135, -3.9005702, -3.2892854, -7.0870132, -4.5488915, -0.6163413, -8.403946, -7.936515, -10.76196, -10.574102, -5.585893, -5.0456195, -9.817365, -10.352768, -6.801859, -16.870096, -9.351337, -14.234166, -6.2163506});
		System.out.println(getStandardDevition(fs)+", "+getVariance(fs));
		
		System.out.println(getMaxs(fs));
		
		
		
	}
}
