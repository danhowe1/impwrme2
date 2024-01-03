package com.impwrme2.controller.quickSnapshot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.quickSnapshot.dto.QuickSnapshotDto;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class QuickSnapshotController {

	@PostMapping("/saveQuickSnapshot")
	public String saveQuickSnapshot(@Valid QuickSnapshotDto quickSnapshotDto, BindingResult result, Model model) {
		
		if (result.hasErrors()) {
			model.addAttribute("quickSnapshotDto", quickSnapshotDto);
			return "index";
		}
		
		return "redirect:/app/dashboard";		
	}
}
