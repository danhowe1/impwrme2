package com.impwrme2.controller.quickSnapshot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.quickSnapshot.dto.QuickSnapshotDto;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourceScenario;
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
		
		ResourceScenario scenario = createQuickSnapshotScenario(quickSnapshotDto);
		session.setAttribute("SESSION_QUICK_SNAPSHOT_SCENARIO", scenario);

		return "redirect:/public/showQuickSnapshot";		
	}
	
	@GetMapping("/showQuickSnapshot")
	public String showQuickSnapshot(Model model, HttpSession session) {
	
		ResourceScenario scenario = (ResourceScenario)session.getAttribute("SESSION_QUICK_SNAPSHOT_SCENARIO");
		model.addAttribute("scenario", scenario);
		return "showQuickSnapshot";
	}
	
	private ResourceScenario createQuickSnapshotScenario(QuickSnapshotDto quickSnapshotDto) {
		ResourceScenario resourceScenario = new ResourceScenario("My quick snapshot scenario");
		Scenario scenario = new Scenario(resourceScenario, "TODO tmp");
		ResourceHousehold family = new ResourceHousehold("My family");
		scenario.addResource(family);
		return resourceScenario;
	}
}
