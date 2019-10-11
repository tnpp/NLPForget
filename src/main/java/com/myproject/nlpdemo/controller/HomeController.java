package com.myproject.nlpdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/")
	public String hhome(ModelMap map) {
		return index(map);
	}
	@RequestMapping("/index")
	public String index(ModelMap map) {
        return "forget/upload_words";
	}
}
