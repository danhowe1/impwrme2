package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.controller.dto.ResourceDropdownDto;
import com.impwrme2.controller.dto.ResourceDto;
import com.impwrme2.controller.dto.ResourceDtoConverter;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamInteger;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.service.resource.ResourceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/ajaxdashboard")
public class AjaxDashboardController {

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceService resourceService;
	
	@GetMapping(value = {"", "/"})
	public String ajaxdashboard(@AuthenticationPrincipal OidcUser user, Model model,  HttpSession session, Locale locale) {

		ResourceScenario scenario = populateInitialTestScenario();
		model.addAttribute("scenarioDto", scenario);
		model.addAttribute("resourceDto", getInitialTestResource());
		model.addAttribute("resourceDropdownDto", new ResourceDropdownDto(scenario, ResourceType.SCENARIO));
		return "ajaxdashboard/ajaxdashboard";
	}
	
	@GetMapping(value = "/showChart")
	public String showChart(Model model) {
		model.addAttribute("UpdatedChart", "Updated chart!");
		return "fragments/ajaxdashboard/ajaxdashboardChart :: ajaxdashboardChart";		
	}

	@GetMapping(value = {"/showResource/{resourceId}"})
	public String showResource(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		return "fragments/ajaxdashboard/ajaxdashboardResourceDisplay :: ajaxdashboardResourceDisplay";		
	}

	@GetMapping(value = "/showResources")
	public String showResources(Model model) {
//		Scenario scenario = new Scenario("Ajax scenario", BigDecimal.valueOf(2.0));
//		Resource family = new ResourceHousehold("Arse", scenario);
//		model.addAttribute("resource", family);
		return "fragments/ajaxdashboard/ajaxdashboardResources :: ajaxdashboardResources";
	}
	
    @PostMapping(value = "/saveResource")
    @ResponseBody
    public String saveResource(@ModelAttribute("resource") @RequestBody ResourceHousehold resource, Model model) {
//		Scenario scenario = new Scenario("Ajax scenario", BigDecimal.valueOf(2.0));
//		Resource family = new ResourceHousehold(resource.getName(), scenario);
//		model.addAttribute("resource", family);
        return "{\"status\":\"success\"}";
    }
    
    private ResourceScenario populateInitialTestScenario() {
    	Resource scenario = resourceService.findById(102L).orElse(getInitialTestScenario());
//    	return (ResourceScenario) resourceService.save(scenario);
    	return (ResourceScenario) scenario;
    }
    
    private ResourceScenario getInitialTestScenario() {
    	
    	ResourceScenario scenarioResource = new ResourceScenario("My first scenario");
    	scenarioResource.setStartYearMonth(YearMonth.of(2024, 1));

    	ResourceParamBigDecimal cpi = new ResourceParamBigDecimal("CPI");
    	cpi.setResource(scenarioResource);
    	scenarioResource.addResourceParam(cpi);
  
    	ResourceParamDateValueBigDecimal cpiVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), new BigDecimal("2.15"));
    	cpiVal.setResourceParam(cpi);
    	cpi.addResourceParamDateValue(cpiVal);
    	
    	ResourceParamDateValueBigDecimal cpiVal2 = new ResourceParamDateValueBigDecimal(YearMonth.of(2027, 10), new BigDecimal("3.00"));
    	cpiVal2.setResourceParam(cpi);
    	cpi.addResourceParamDateValue(cpiVal2);

    	ResourceParamInteger retirementAge = new ResourceParamInteger("Retirement age");
    	retirementAge.setResource(scenarioResource);
    	scenarioResource.addResourceParam(retirementAge);

    	ResourceParamDateValueInteger retirementAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2027, 10), Integer.valueOf(65));
    	retirementAgeVal.setResourceParam(retirementAge);
    	retirementAge.addResourceParamDateValue(retirementAgeVal);
    	
    	ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal("Share market growth");
    	shareMarketGrowthRate.setResource(scenarioResource);
    	scenarioResource.addResourceParam(shareMarketGrowthRate);
    	
    	ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), new BigDecimal("6.5"));
    	shareMarketGrowthRateVal.setResourceParam(shareMarketGrowthRate);
    	shareMarketGrowthRate.addResourceParamDateValue(shareMarketGrowthRateVal);
    	
    	Resource householdResource = new ResourceHousehold("Howe Family", scenarioResource);
    	householdResource.setStartYearMonth(YearMonth.of(2024, 1));
    	
    	Resource amandaResource = new ResourcePerson("Amanda", scenarioResource);
    	amandaResource.setStartYearMonth(YearMonth.of(2024, 1));

    	Resource danResource = new ResourcePerson("Dan", scenarioResource);
    	danResource.setStartYearMonth(YearMonth.of(2024, 1));

    	return scenarioResource;
    }
    
    private ResourceDto getInitialTestResource() {
    	
    	ResourceDto resourceDto = new ResourceDto();
    	resourceDto.setId(10L);
    	resourceDto.setName("My first scenario");
    	resourceDto.setStartYearMonth("01 2024");
    	resourceDto.setResourceType("SCENARIO");
    	return resourceDto;
    }
}
