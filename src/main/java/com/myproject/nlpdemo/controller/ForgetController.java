package com.myproject.nlpdemo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.myproject.nlpdemo.service.forget.SolutionForm;
import com.myproject.nlpdemo.service.forget.pojo.DictionaryObj;

@Controller
@RequestMapping(value = "/forget")
public class ForgetController {
    @RequestMapping(value = "to_forget")
    public ModelAndView toForget() {
        ModelAndView mv = new ModelAndView("forget/upload_words");
        return mv;
    }

    @RequestMapping(value = "do_upload_and_generate_dic")
    public ModelAndView doUpdateAndGenerateDic() {
        SolutionForm sf = new SolutionForm();
        List<DictionaryObj> dics = sf.catchWordIndexColl();
        ModelAndView mv = new ModelAndView("forget/upload_words");
        mv.addObject("dics", dics);
        mv.addObject("msg", "字典学习完成：生成 " + dics.size() + " 个词汇");
        return mv;
    }

    @ResponseBody
    @RequestMapping(value = "segment", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    public String segment(String txtWord, Integer wordLen) {
        if (wordLen == null) {
            wordLen = 2;
        }
        SolutionForm sf = new SolutionForm();
        List<String> segments = sf.showWordSegments(txtWord, wordLen);
        return segments.stream().collect(Collectors.joining(", "));
    }
}
