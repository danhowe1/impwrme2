package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

import com.impwrme2.controller.dto.ResourceDropdownTabDto;
import com.impwrme2.controller.dto.ResourceDropdownTabItemDto;
import com.impwrme2.controller.dto.ResourceDto;
import com.impwrme2.controller.dto.ScenarioDto;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceFamily;
import com.impwrme2.model.scenario.Scenario;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/ajaxdashboard")
public class AjaxDashboardController {

	@GetMapping(value = {"", "/"})
	public String ajaxdashboard(@AuthenticationPrincipal OidcUser user, Model model,  HttpSession session, Locale locale) {

		model.addAttribute("scenarioDto", getInitialTestScenarioDto());
		model.addAttribute("resourceDropdownTabs", getResourceDropdownTabs());
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
    
    private ScenarioDto getInitialTestScenarioDto() {
    	ScenarioDto scenarioDto = new ScenarioDto();
    	scenarioDto.setId(1L);
    	scenarioDto.setName("My first Scenario");
    	
    	ResourceDto scenarioResourceDto = new ResourceDto();
    	scenarioResourceDto.setId(10L);
    	scenarioResourceDto.setScenarioId(scenarioDto.getId());
    	scenarioResourceDto.setName(scenarioDto.getName());
    	scenarioResourceDto.setStartYearMonth("01 2024");
    	scenarioResourceDto.setResourceType("RESOURCE_SCENARIO");
    	scenarioDto.addResourceDto(scenarioResourceDto);

    	ResourceDto amandaResourceDto = new ResourceDto();
    	amandaResourceDto.setId(11L);
    	amandaResourceDto.setScenarioId(scenarioDto.getId());
    	amandaResourceDto.setName("Amanda");
    	amandaResourceDto.setStartYearMonth("01 2024");
    	amandaResourceDto.setResourceType("RESOURCE_ADULT");
    	scenarioDto.addResourceDto(amandaResourceDto);

    	ResourceDto danResourceDto = new ResourceDto();
    	danResourceDto.setId(12L);
    	danResourceDto.setScenarioId(scenarioDto.getId());
    	danResourceDto.setName("Dan");
    	danResourceDto.setStartYearMonth("01 2024");
    	danResourceDto.setResourceType("RESOURCE_ADULT");
    	scenarioDto.addResourceDto(danResourceDto);

    	return scenarioDto;
    }
    
    private List<ResourceDropdownTabDto> getResourceDropdownTabs() {
    	List<ResourceDropdownTabDto> resourceTabs = new ArrayList<ResourceDropdownTabDto>();
    	
    	ResourceDropdownTabDto scenarioTab = new ResourceDropdownTabDto();
    	scenarioTab.setResourceTabLabel("Scenario");

    	ResourceDropdownTabItemDto scenarioItem = new ResourceDropdownTabItemDto();
    	scenarioItem.setResourceItemLabel("My first Scenario");
    	scenarioItem.setResourceId(10L);
    	scenarioTab.addResourceTabItem(scenarioItem);
    	resourceTabs.add(scenarioTab);
    	
    	ResourceDropdownTabDto familyTab = new ResourceDropdownTabDto();
    	familyTab.setResourceTabLabel("Family");
    	
    	ResourceDropdownTabItemDto amandaItem = new ResourceDropdownTabItemDto();
    	amandaItem.setResourceItemLabel("Amanda");
    	amandaItem.setResourceId(11L);
    	familyTab.addResourceTabItem(amandaItem);
    	
    	ResourceDropdownTabItemDto danItem = new ResourceDropdownTabItemDto();
    	danItem.setResourceItemLabel("Dan");
    	danItem.setResourceId(12L);
    	familyTab.addResourceTabItem(danItem);
    	
    	resourceTabs.add(familyTab);
    	
    	return resourceTabs;
    }
}
