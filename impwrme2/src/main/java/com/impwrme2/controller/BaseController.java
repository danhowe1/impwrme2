package com.impwrme2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BaseController {

	@GetMapping(value = {"", "/"})
	public String index() {
		return "index";
	}
}