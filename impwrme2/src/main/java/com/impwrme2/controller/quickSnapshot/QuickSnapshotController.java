package com.impwrme2.controller.quickSnapshot;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.quickSnapshot.dto.QuickSnapshotDto;
import com.impwrme2.model.resource.ResourceFamily;
import com.impwrme2.model.scenario.Scenario;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/public/")
public class QuickSnapshotController {

	@PostMapping("/saveQuickSnapshot")
	public String saveQuickSnapshot(@Valid QuickSnapshotDto quickSnapshotDto, BindingResult result, Model model, HttpSession session) {
		
		if (result.hasErrors()) {
			model.addAttribute("quickSnapshotDto", quickSnapshotDto);
			return "index";
		}
		
		Scenario scenario = createQuickSnapshotScenario(quickSnapshotDto);
		session.setAttribute("SESSION_QUICK_SNAPSHOT_SCENARIO", scenario);

		return "redirect:/public/showQuickSnapshot";		
	}
	
	@GetMapping("/showQuickSnapshot")
	public String showQuickSnapshot(Model model, HttpSession session) {
	
		Scenario scenario = (Scenario)session.getAttribute("SESSION_QUICK_SNAPSHOT_SCENARIO");
		model.addAttribute("scenario", scenario);
		return "showQuickSnapshot";
	}
	
	private Scenario createQuickSnapshotScenario(QuickSnapshotDto quickSnapshotDto) {
		Scenario scenario = new Scenario("My quick snapshot scenario", BigDecimal.valueOf(2.0));
		ResourceFamily family = new ResourceFamily("My family", scenario);
		scenario.addResource(family);
		return scenario;
	}
}
