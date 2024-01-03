package com.impwrme2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.quickSnapshot.dto.QuickSnapshotDto;

@Controller
@RequestMapping("/")
public class BaseController {

	@GetMapping(value = {"", "/", "/index"})
	public String index(Model model) {
		
		// Add default DTO to the model.
		model.addAttribute("quickSnapshotDto", new QuickSnapshotDto());

		return "index";
	}
}