package com.myproject.nlpdemo.service.cluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.hankcs.hanlp.mining.cluster.ClusterAnalyzer;

@Service
public class ClusterService {
    public static void main(String[] args) throws Exception {
        ClusterAnalyzer<String> analyzer = new ClusterAnalyzer<String>();
        ClusterService s = new ClusterService();
        Map<String, String> words = s.readClusterFiles();
        for (Map.Entry<String, String> entry : words.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            analyzer.addDocument(key.endsWith(".txt")?key.substring(0, key.length()-4):key, value);
        }
        
        List<Set<String>> list1 = analyzer.kmeans(20);
        String s1 = list1.stream().map(e->e.toString()).collect(Collectors.joining("\n"));
        System.out.println(s1);
        System.out.println("-------------------------------------");
        List<Set<String>> list2 = analyzer.repeatedBisection(0.8d);
        String s2 = list2.stream().map(e->e.toString()).collect(Collectors.joining("\n"));
        System.out.println(s2);

    }
    /**
     * 
     * @param nCluster 预设聚类个数
     * @param limitEval 准则函数的增幅设定阈值, 当一个簇的二分增幅小于beta时不再对该簇进行划分
     * @return
     * @throws Exception
     */
    public String doCluster(Integer nCluster, Double limitEval) throws Exception {
        ClusterAnalyzer<String> analyzer = new ClusterAnalyzer<String>();
        Map<String, String> words = readClusterFiles();
        for (Map.Entry<String, String> entry : words.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            analyzer.addDocument(key.endsWith(".txt")?key.substring(0, key.length()-4):key, value);
        }
        List<Set<String>> list = new ArrayList<>();
        if (nCluster != null && limitEval != null) {
            list = analyzer.repeatedBisection(nCluster, limitEval);
        } else if (nCluster != null) {
            list = analyzer.repeatedBisection(nCluster);
        } else {
            list = analyzer.repeatedBisection(limitEval);
        }
        String str = list.stream().map(e->e.toString()).collect(Collectors.joining("\n\n"));
        return str;
    }
    public Map<String, String> readClusterFiles() throws Exception {
        URL url = this.getClass().getResource("/file/cluster/");
        File path = ResourceUtils.getFile(url);
        File[] fs = path.listFiles();
        Map<String, String> result = new HashMap<>();
        for (File f : fs) {
            String name = f.getName();
            StringBuilder sb = new StringBuilder();
            try (InputStream is = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));) {
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    if (StringUtils.isNotBlank(lineTxt)) {
                        sb.append(lineTxt.trim()).append("\n");
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String content = sb.toString();
            if (StringUtils.isNotBlank(content)) {
                result.put(name, content);
            }
        }
        return result;
    }
}
