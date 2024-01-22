package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.controller.dto.resourceDropdown.ResourceDropdownDto;
import com.impwrme2.controller.dto.resourceParam.ResourceParamDtoConverter;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamInteger;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.resourceParam.ResourceParamService;
import com.impwrme2.service.resourceParamDateValue.ResourceParamDateValueService;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/ajaxdashboard")
public class AjaxDashboardController {

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceParamDtoConverter resourceParamDtoConverter;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceParamService resourceParamService;

	@Autowired
	private ResourceParamDateValueService resourceParamDateValueService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = { "", "/" })
	public String ajaxdashboard(@AuthenticationPrincipal OidcUser user, Model model, HttpSession session, Locale locale) {

//		session.removeAttribute("SESSION_CURRENT_RESOURCE_ID");
		Long currentResourceId = (Long) session.getAttribute("SESSION_CURRENT_RESOURCE_ID");
		if (null == currentResourceId) {
			// TODO Redirect to the Scenario selection page for user to choose Scenario.
			currentResourceId = Long.valueOf(752L);
		}
		Resource resource = resourceService.findById(currentResourceId).orElseGet(() -> populateInitialTestScenario());
		setUpModelAfterResourceUpdated(resource, model, session);
		model.addAttribute("resourceDropdownDto", new ResourceDropdownDto((ResourceScenario) resource.getResourceScenario(), resource.getResourceType()));
		return "ajaxdashboard/ajaxdashboard";
	}

//	@GetMapping(value = "/showChart")
//	public String showChart(Model model) throws InterruptedException {
//		TimeUnit.SECONDS.sleep(5);
//		return "fragments/ajaxdashboard/ajaxdashboardChart :: ajaxdashboardChart";		
//	}

	@GetMapping(value = { "/showResource/{resourceId}" })
	public String showResource(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		setUpModelAfterResourceUpdated(resource, model, session);
		return "fragments/ajaxdashboard/ajaxdashboardResourceDisplay :: ajaxdashboardResourceDisplay";
	}

	@PostMapping(value = "/saveResourceParamDateValue")
	@ResponseBody
	public String saveResourceParamDateValue(@Valid ResourceParamDateValueDto rpdvDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model) {
		if (result.hasErrors()) {
			return getErrorMessageFromBindingResult(result);
		}
		ResourceParamDateValue<?> resourceParamDateValue;
//		ResourceParam<?> resourceParam;
		
		if (null != rpdvDto.getId()) {
			// Editing existing rpdv.
			resourceParamDateValue = resourceParamDateValueService.findById(rpdvDto.getId()).get();
			Long idOfAnotherRpdvWithSameDate = getIdOfAnotherResourceParamDateValueWithSameDate(rpdvDto, resourceParamDateValue.getResourceParam());
			if (null != idOfAnotherRpdvWithSameDate) {
				// Delete the rpdv we've been passed as it's now a duplicate.
				resourceParamService.deleteResourceParamDateValue(resourceParamDateValue);

				// Update the value on the existing rpdv & save.
				ResourceParamDateValue<?> existingRpdv = resourceParamDateValueService.findById(idOfAnotherRpdvWithSameDate).get();
				existingRpdv.setValueFromString(rpdvDto.getValue());
				resourceParamDateValueService.save(existingRpdv);
			} else {
				// Update existing rpdv and save.
				resourceParamDateValue.setYearMonth(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()));
				resourceParamDateValue.setValueFromString(rpdvDto.getValue());
				resourceParamDateValueService.save(resourceParamDateValue);				
			}			
		} else {
			// New rpdv.
			ResourceParam<?> resourceParam = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
			Long idOfAnotherRpdvWithSameDate = getIdOfAnotherResourceParamDateValueWithSameDate(rpdvDto, resourceParam);
			if (null != idOfAnotherRpdvWithSameDate) {
				// Update the value on the existing rpdv & save.
				ResourceParamDateValue<?> existingRpdv = resourceParamDateValueService.findById(idOfAnotherRpdvWithSameDate).get();
				existingRpdv.setValueFromString(rpdvDto.getValue());
				resourceParamDateValueService.save(existingRpdv);
			} else {
				// Create brand new rpdv.
				resourceParamDateValue = resourceParamDtoConverter.dtoToEntity(rpdvDto);
//				resourceParamDateValue.setResourceParamGeneric(resourceParam);
				resourceParam.addResourceParamDateValueGeneric(resourceParamDateValue);
				resourceParamDateValueService.save(resourceParamDateValue);
			}
		}
		return "SUCCESS";
	}
	
	private Long getIdOfAnotherResourceParamDateValueWithSameDate(final ResourceParamDateValueDto rpdvDto, ResourceParam<?> resourceParam) {
		Long idOfAnotherRpdvWithSameDate = null;
		YearMonth rpdvDtoYearMonth = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth());
		for (ResourceParamDateValue<?> existingRpdv : resourceParam.getResourceParamDateValues()) {
			if ((null == rpdvDto.getId() || !rpdvDto.getId().equals(existingRpdv.getId())) &&  rpdvDtoYearMonth.equals(existingRpdv.getYearMonth())) {
				idOfAnotherRpdvWithSameDate = existingRpdv.getId();
				break;
			}
		}
		return idOfAnotherRpdvWithSameDate;
	}
	
	private void setUpModelAfterResourceUpdated(Resource resource, Model model, HttpSession session) {
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", resource.getId());
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("resourceParamTableDto", resourceParamDtoConverter.resourceParamsToResourceParamTableDto(resource.getResourceParams()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
	}

	private String getErrorMessageFromBindingResult(BindingResult result) {
		for (Object object : result.getAllErrors()) {
		    if(object instanceof FieldError) {
		        FieldError fieldError = (FieldError) object;
		        return fieldError.getDefaultMessage();
		    }
		    if(object instanceof ObjectError) {
		        ObjectError objectError = (ObjectError) object;
		        return objectError.getDefaultMessage();
		    }
		}
		return messageSource.getMessage("msg.validation.unknownError", null, LocaleContextHolder.getLocale());
	}

	private ResourceScenario populateInitialTestScenario() {
		Resource scenario = getInitialTestScenario();
		return (ResourceScenario) resourceService.save(scenario);
	}

	private ResourceScenario getInitialTestScenario() {

		ResourceScenario scenarioResource = new ResourceScenario("My first scenario");
		scenarioResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamBigDecimal cpi = new ResourceParamBigDecimal("CPI");
		cpi.setUserAbleToCreateNewDateValue(true);
		cpi.setResource(scenarioResource);
		scenarioResource.addResourceParam(cpi);

		ResourceParamDateValueBigDecimal cpiVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("2.15"));
//.setResourceParam(cpi);
		cpi.addResourceParamDateValue(cpiVal);

		ResourceParamDateValueBigDecimal cpiVal2 = new ResourceParamDateValueBigDecimal(YearMonth.of(2027, 10), true, new BigDecimal("3.00"));
		cpiVal2.setUserAbleToChangeDate(true);
//		cpiVal2.setResourceParam(cpi);
		cpi.addResourceParamDateValue(cpiVal2);

		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal("Share market growth");
		shareMarketGrowthRate.setResource(scenarioResource);
		scenarioResource.addResourceParam(shareMarketGrowthRate);

		ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.5"));
//		shareMarketGrowthRateVal.setResourceParam(shareMarketGrowthRate);
		shareMarketGrowthRate.addResourceParamDateValue(shareMarketGrowthRateVal);

		Resource householdResource = new ResourceHousehold("Howe Family", scenarioResource);
		householdResource.setStartYearMonth(YearMonth.of(2024, 1));

		Resource amandaResource = new ResourcePerson("Amanda", scenarioResource);
		amandaResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamInteger retirementAge = new ResourceParamInteger("Retirement age");
		retirementAge.setResource(amandaResource);
		amandaResource.addResourceParam(retirementAge);

		ResourceParamDateValueInteger retirementAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(65));
//		retirementAgeVal.setResourceParam(retirementAge);
		retirementAge.addResourceParamDateValue(retirementAgeVal);

		Resource danResource = new ResourcePerson("Dan", scenarioResource);
		danResource.setStartYearMonth(YearMonth.of(2024, 1));

		return scenarioResource;
	}
}
