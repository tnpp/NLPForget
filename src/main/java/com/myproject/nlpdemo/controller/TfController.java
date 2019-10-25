package com.myproject.nlpdemo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.myproject.nlpdemo.service.lstmcrf_ner.LstmCrfNerService;
import com.myproject.nlpdemo.service.textcnn.TfTextCnnPredictService;

@Controller
@RequestMapping(value = "/tf")
public class TfController {
	@Autowired
	private TfTextCnnPredictService tfTextCnnPredictService;
    @Autowired
    private LstmCrfNerService lstmCrfNerService;
    @RequestMapping(value = "to_textcnn")
    public ModelAndView toForget() {
        ModelAndView mv = new ModelAndView("tf/textcnn");
        return mv;
    }
    @ResponseBody
    @RequestMapping(value = "classify", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String classify(String txtWord) {
    	List<String> cs = tfTextCnnPredictService.getLabel(txtWord);
        return cs.stream().collect(Collectors.joining(", "));
    }
    @RequestMapping(value = "to_ner")
    public ModelAndView toNer() {
        ModelAndView mv = new ModelAndView("tf/lstmcrf_ner");
        return mv;
    }

    @ResponseBody
    @RequestMapping(value = "do_ner", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String doNer(String categoryName, String txtWord) {
        List<String[]> result = lstmCrfNerService.getLabel(categoryName, txtWord);
        String s = result.stream().map(e -> e[1] + ": " + e[0])
                .collect(Collectors.joining(", "));
        return s;
    }
}
