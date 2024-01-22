package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

	@GetMapping(value = "/showChart")
	public String showChart(Model model) throws InterruptedException {
		TimeUnit.SECONDS.sleep(5);
		return "fragments/ajaxdashboard/ajaxdashboardChart :: ajaxdashboardChart";		
	}

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
		if (null != rpdvDto.getId()) {
			updateExistingRpdv(rpdvDto);
		} else {
			saveNewRpdvOrUpdateExistingRpdv(rpdvDto);
		}
		return "SUCCESS";
	}

	@PostMapping(value = "/deleteResourceParamDateValue")
	@ResponseBody
	public String deleteResourceParamDateValue(@Valid ResourceParamDateValueDto rpdvDto) {
		if (!rpdvDto.isUserAbleToChangeDate()) {
			return messageSource.getMessage("msg.validation.resourceParamDateValue.deleteNotAllowed", null, LocaleContextHolder.getLocale());
		}
		ResourceParamDateValue<?> rpdv = resourceParamDateValueService.findById(rpdvDto.getId()).get();
		resourceParamService.deleteResourceParamDateValue(rpdv);
		return "SUCCESS";
	}

	/**
	 * An existing RPDV is being updated (i.e. the id of the RPDV already exists).
	 * 
	 * If there exists another RPDV with the same date, then the RPDV being updated here is deleted and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * If there doesn't exist another RPDV with the same date then the RPDV being updated by the user has both the date and value updated.
	 * 
	 * @param rpdvDto The ResourceParamDateValueDto object containing the updated data.
	 */
	private void updateExistingRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParamDateValue<?> rpdvOfCurrentDto = resourceParamDateValueService.findById(rpdvDto.getId()).get();
		Optional<Long> idOfAnotherRpdvWithSameDate = rpdvOfCurrentDto.getResourceParam().getIdOfRpdvWithDuplicateDate(rpdvDto.getId(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()));
		if (!idOfAnotherRpdvWithSameDate.isEmpty()) {
			deletePassedRpdvAndUpdateExistingWithSameDate(rpdvOfCurrentDto, idOfAnotherRpdvWithSameDate.get(), rpdvDto.getValue());
		} else {
			updateRpdvDateValue(rpdvOfCurrentDto, YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()), rpdvDto.getValue());
		}
	}

	/**
	 * Another RPDV with the same date exists so the RPDV being updated here is deleted and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * @param resourceParamDateValueToDelete The RPDV that the user is currently trying to update. This will be deleted.
	 * @param idOfPreExistingRpdvWithSameDate The ID of the pre-existing RPDV that has the same date.
	 * @param newValue The new value the user has supplied. The existing RPDV will be updated with this value.
	 */
	private void deletePassedRpdvAndUpdateExistingWithSameDate(ResourceParamDateValue<?> resourceParamDateValueToDelete, Long idOfPreExistingRpdvWithSameDate, String newValue) {
		// Delete the rpdv we've been passed as it's now a duplicate.
		resourceParamService.deleteResourceParamDateValue(resourceParamDateValueToDelete);

		// Update the value on the existing rpdv & save.
		ResourceParamDateValue<?> preExistingRpdv = resourceParamDateValueService.findById(idOfPreExistingRpdvWithSameDate).get();
		updateRpdvValueOnly(preExistingRpdv, newValue);
	}

	/**
	 * An new RPDV is attempting to bre created (i.e. the id of the RPDV is null).
	 * 
	 * If there exists another RPDV with the same date, then the RPDV being updated here is ignored and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * If there doesn't exist another RPDV with the same date then the RPDV being passed by the user is created with the passed date and value updated.
	 * 
	 * @param rpdvDto The ResourceParamDateValueDto object containing the new data.
	 */
	private void saveNewRpdvOrUpdateExistingRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParam<?> resourceParamOfCurrentDto = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		Optional<Long> idOfAnotherRpdvWithSameDate = resourceParamOfCurrentDto.getIdOfRpdvWithDuplicateDate(rpdvDto.getId(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()));
		if (!idOfAnotherRpdvWithSameDate.isEmpty()) {
			ResourceParamDateValue<?> preExistingRpdv = resourceParamDateValueService.findById(idOfAnotherRpdvWithSameDate.get()).get();
			updateRpdvValueOnly(preExistingRpdv, rpdvDto.getValue());
		} else {
			createBrandNewRpdv(rpdvDto);
		}
	}

	/**
	 * Create a brand new RPDV
	 * 
	 * @param rpdvDto The RPDV containing the new data.
	 */
	private void createBrandNewRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParam<?> resourceParam = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		ResourceParamDateValue<?> resourceParamDateValue = resourceParamDtoConverter.dtoToEntity(rpdvDto);
		resourceParam.addResourceParamDateValueGeneric(resourceParamDateValue);
		resourceParamDateValueService.save(resourceParamDateValue);
	}

	private void updateRpdvValueOnly(ResourceParamDateValue<?> resourceParamDateValue, String newValue) {
		updateRpdvDateValue(resourceParamDateValue, resourceParamDateValue.getYearMonth(), newValue);
	}

	private void updateRpdvDateValue(ResourceParamDateValue<?> resourceParamDateValue, YearMonth newYearMonth, String newValue) {
		resourceParamDateValue.setYearMonth(newYearMonth);
		resourceParamDateValue.setValueFromString(newValue);
		resourceParamDateValueService.save(resourceParamDateValue);
	}

	private void setUpModelAfterResourceUpdated(Resource resource, Model model, HttpSession session) {
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", resource.getId());
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("resourceParamTableDto", resourceParamDtoConverter.resourceParamsToResourceParamTableDto(resource.getResourceParams()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
	}

	private String getErrorMessageFromBindingResult(BindingResult result) {
		for (Object object : result.getAllErrors()) {
			if (object instanceof FieldError) {
				FieldError fieldError = (FieldError) object;
				return fieldError.getDefaultMessage();
			}
			if (object instanceof ObjectError) {
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
		cpi.addResourceParamDateValue(cpiVal);

		ResourceParamDateValueBigDecimal cpiVal2 = new ResourceParamDateValueBigDecimal(YearMonth.of(2027, 10), true, new BigDecimal("3.00"));
		cpiVal2.setUserAbleToChangeDate(true);
		cpi.addResourceParamDateValue(cpiVal2);

		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal("Share market growth");
		shareMarketGrowthRate.setResource(scenarioResource);
		scenarioResource.addResourceParam(shareMarketGrowthRate);

		ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.5"));
		shareMarketGrowthRate.addResourceParamDateValue(shareMarketGrowthRateVal);

		Resource householdResource = new ResourceHousehold("Howe Family", scenarioResource);
		householdResource.setStartYearMonth(YearMonth.of(2024, 1));

		Resource amandaResource = new ResourcePerson("Amanda", scenarioResource);
		amandaResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamInteger retirementAge = new ResourceParamInteger("Retirement age");
		retirementAge.setResource(amandaResource);
		amandaResource.addResourceParam(retirementAge);

		ResourceParamDateValueInteger retirementAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(65));
		retirementAge.addResourceParamDateValue(retirementAgeVal);

		Resource danResource = new ResourcePerson("Dan", scenarioResource);
		danResource.setStartYearMonth(YearMonth.of(2024, 1));

		return scenarioResource;
	}
}
