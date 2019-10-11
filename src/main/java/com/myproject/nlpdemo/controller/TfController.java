package com.myproject.nlpdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/tf")
public class TfController {
    @RequestMapping(value = "to_textcnn")
    public ModelAndView toForget() {
        ModelAndView mv = new ModelAndView("tf/textcnn");
        return mv;
    }
}
