package com.myproject.nlpdemo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.myproject.nlpdemo.service.cluster.ClusterService;

@Controller
@RequestMapping(value = "/cluster")
public class ClusterController {
    @Autowired
    private ClusterService clusterService;
    @RequestMapping(value = "to_cluster")
    public ModelAndView toCluster() {
        ModelAndView mv = new ModelAndView("cluster/cluster");
        Map<String, String> fileMap = new HashMap<>();
        try {
            fileMap = clusterService.readClusterFiles();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mv.addObject("files", fileMap);
        return mv;
    }
    @ResponseBody
    @RequestMapping(value = "show_content", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String showContent(String key) {
        Map<String, String> fileMap = new HashMap<>();
        try {
            fileMap = clusterService.readClusterFiles();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String content = fileMap.get(key);
        return content;
    }
    @ResponseBody
    @RequestMapping(value = "do_cluster", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String doCluster(Integer nCluster, Double limitEval) {
        String result = "";
        try {
            result = clusterService.doCluster(nCluster, limitEval);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
