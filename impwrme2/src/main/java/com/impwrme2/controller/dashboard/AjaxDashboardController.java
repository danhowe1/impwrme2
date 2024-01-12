package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceFamily;
import com.impwrme2.model.scenario.Scenario;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/ajaxdashboard")
public class AjaxDashboardController {

	@GetMapping(value = {"", "/"})
	public String ajaxdashboard(@AuthenticationPrincipal OidcUser user, Model model,  HttpSession session, Locale locale) {

		Scenario scenario = new Scenario("Ajax scenario", BigDecimal.valueOf(2.0));
		Resource family = new ResourceFamily("Howes", scenario);
		model.addAttribute("resource", family);
		return "ajaxdashboard/ajaxdashboard";
	}
	
	@GetMapping(value = "/showChart")
	public String showChart(Model model) {
		model.addAttribute("UpdatedChart", "Updated chart!");
		return "fragments/ajaxdashboard/ajaxdashboardChart :: ajaxdashboardChart";		
	}
	
	@GetMapping(value = "/showResources")
	public String showResources(Model model) {
		Scenario scenario = new Scenario("Ajax scenario", BigDecimal.valueOf(2.0));
		Resource family = new ResourceFamily("Arse", scenario);
		model.addAttribute("resource", family);
		return "fragments/ajaxdashboard/ajaxdashboardResources :: ajaxdashboardResources";
	}
	
    @PostMapping(value = "/saveResource")
    @ResponseBody
    public String saveResource(@ModelAttribute("resource") @RequestBody ResourceFamily resource, Model model) {
		Scenario scenario = new Scenario("Ajax scenario", BigDecimal.valueOf(2.0));
		Resource family = new ResourceFamily(resource.getName(), scenario);
		model.addAttribute("resource", family);
        return "{\"status\":\"success\"}";
    }
}
