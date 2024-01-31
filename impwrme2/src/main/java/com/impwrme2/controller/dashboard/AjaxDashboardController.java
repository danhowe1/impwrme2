package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
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

import com.impwrme2.controller.dto.cashflow.CashflowDtoConverter;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDtoConverter;
import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.controller.dto.resourceDropdown.ResourceDropdownDto;
import com.impwrme2.controller.dto.resourceParam.ResourceParamDtoConverter;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamInteger;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.cashflow.CashflowService;
import com.impwrme2.service.cashflowDateRangeValue.CashflowDateRangeValueService;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.resourceParam.ResourceParamService;
import com.impwrme2.service.resourceParamDateValue.ResourceParamDateValueService;
import com.impwrme2.service.scenario.ScenarioService;
import com.impwrme2.utils.YearMonthUtils;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/ajaxdashboard")
public class AjaxDashboardController {

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private CashflowDtoConverter cashflowDtoConverter;

	@Autowired
	private CashflowDateRangeValueDtoConverter cashflowDateRangeValueDtoConverter;

	@Autowired
	private ResourceParamDtoConverter resourceParamDtoConverter;

	@Autowired
	private ScenarioService scenarioService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceParamService resourceParamService;

	@Autowired
	private ResourceParamDateValueService resourceParamDateValueService;

	@Autowired
	private CashflowService cashflowService;

	@Autowired
	private CashflowDateRangeValueService cashflowDateRangeValueService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = { "", "/" })
	public String ajaxdashboard(@AuthenticationPrincipal OidcUser user, Model model, HttpSession session, Locale locale) {

		String userId = user.getUserInfo().getSubject();
		Resource resource;
//session.removeAttribute("SESSION_CURRENT_RESOURCE_ID");
		Long currentResourceId = (Long) session.getAttribute("SESSION_CURRENT_RESOURCE_ID");
		if (null == currentResourceId) {
			List<Scenario> scenarios = scenarioService.findByUserId(userId);
			if (0 == scenarios.size()) {
				resource = populateInitialTestScenario(userId).getSortedResources().first();
			} else if (scenarios.size() == 1) {
				resource = scenarios.get(0).getSortedResources().first();
			} else {
				return "forward:/app/scenario/list";
			}
			session.setAttribute("SESSION_CURRENT_RESOURCE_ID", resource.getId());
		} else {
			resource = resourceService.findById(currentResourceId).get();
		}
		 
		setUpModelAfterResourceUpdated(resource, model, session);
		model.addAttribute("resourceDropdownDto", new ResourceDropdownDto(resource.getScenario(), resource.getResourceType()));
		return "ajaxdashboard/ajaxdashboard";
	}

	@GetMapping(value = "/getChartData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getChartData() {
		return generateJsonChartData();
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

	@PostMapping(value = "/saveCashflowDateRangeValue")
	@ResponseBody
	public String saveCashflowDateRangeValue(@Valid CashflowDateRangeValueDto cfdrvDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model) {
		if (result.hasErrors()) {
			return getErrorMessageFromBindingResult(result);
		}
		
		Cashflow cashflow = cashflowService.findById(cfdrvDto.getCashflowId()).get();
		CashflowDateRangeValue cfdrv = cashflowDateRangeValueDtoConverter.dtoToEntity(cfdrvDto);		
		List<CashflowDateRangeValue> cfdrvsToDelete = new ArrayList<CashflowDateRangeValue>();

		for (CashflowDateRangeValue existingCfdrv : cashflow.getCashflowDateRangeValues()) {
			if (!existingCfdrv.getId().equals(cfdrv.getId()) && datesOverlap(existingCfdrv, cfdrv)) {
				// We've found a different cfdrv and the dates overlap in some way.
				if (existingCfdrv.getYearMonthStart().isBefore(cfdrv.getYearMonthStart())) {
					// Existing cfdrv is before this one starts so just need to make sure it's end date is removed.
					existingCfdrv.setYearMonthEnd(null);
					cashflowDateRangeValueService.save(existingCfdrv);
				} else {
					// The existing one starts after this one so mark it for deletion.
					cfdrvsToDelete.add(existingCfdrv);
				}
			}
		}
		
		for (CashflowDateRangeValue c : cfdrvsToDelete) {
			cashflowService.deleteCashflowDateRangeValue(c);
		}
		
		cashflowDateRangeValueService.save(cfdrv);
		return "SUCCESS";
	}

	@PostMapping(value = "/deleteCashflowDateRangeValue")
	@ResponseBody
	public String deleteCashflowDateRangeValue(@Valid CashflowDateRangeValueDto cfdrvDto) {
		CashflowDateRangeValue cfdrv = cashflowDateRangeValueService.findById(cfdrvDto.getId()).get();
		if (cfdrv.getCashflow().getResource().getStartYearMonth().equals(cfdrv.getYearMonthStart())) {
			return messageSource.getMessage("msg.validation.cashflowDateRangeValue.deleteNotAllowed", null, LocaleContextHolder.getLocale());
		}
		cashflowService.deleteCashflowDateRangeValue(cfdrv);
		return "SUCCESS";
	}

	/**
	 * Check for strictly overlapping dates. See https://www.baeldung.com/java-check-two-date-ranges-overlap.
	 * @param firstCfdrv The first CashflowDateRange value to be compared.
	 * @param secondCfdrv The second CashflowDateRange value to be compared.
	 * @return true if any of the dates overlap.
	 */
	private boolean datesOverlap(CashflowDateRangeValue firstCfdrv, CashflowDateRangeValue secondCfdrv) {
		YearMonth A = firstCfdrv.getYearMonthStart();
		YearMonth B = null != firstCfdrv.getYearMonthEnd() ? firstCfdrv.getYearMonthEnd() : YearMonth.of(5000, 1);
		YearMonth C = secondCfdrv.getYearMonthStart();
		YearMonth D = null != secondCfdrv.getYearMonthEnd() ? secondCfdrv.getYearMonthEnd() : YearMonth.of(5000, 1);
		return !(B.isBefore(C) || A.isAfter(D));
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
	 * @param resourceParamDateValueToDelete  The RPDV that the user is currently trying to update. This will be deleted.
	 * @param idOfPreExistingRpdvWithSameDate The ID of the pre-existing RPDV that has the same date.
	 * @param newValue                        The new value the user has supplied. The existing RPDV will be updated with this value.
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
		model.addAttribute("cashflowTableDto", cashflowDtoConverter.cashflowsToCashflowTableDto(resource.getCashflows()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
		model.addAttribute("cashflowDateRangeValueDto", new CashflowDateRangeValueDto());
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

	private Scenario populateInitialTestScenario(String userId) {
		Scenario scenario = getInitialTestScenario(userId);
		return scenarioService.save(scenario, userId);
	}

	private Scenario getInitialTestScenario(String userId) {

		// -----------------
		// ScenarioResource.
		// -----------------
		
		ResourceScenario scenarioResource = new ResourceScenario("My first scenario");
		scenarioResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamBigDecimal cpi = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_CPI);
		cpi.setUserAbleToCreateNewDateValue(true);
		scenarioResource.addResourceParam(cpi);

		ResourceParamDateValueBigDecimal cpiVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("2.15"));
		cpi.addResourceParamDateValue(cpiVal);

		ResourceParamDateValueBigDecimal cpiVal2 = new ResourceParamDateValueBigDecimal(YearMonth.of(2027, 10), true, new BigDecimal("3.00"));
		cpiVal2.setUserAbleToChangeDate(true);
		cpi.addResourceParamDateValue(cpiVal2);

		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_SHARE_MARKET_GROWTH_RATE);
		shareMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
		scenarioResource.addResourceParam(shareMarketGrowthRate);

		ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.5"));
		shareMarketGrowthRate.addResourceParamDateValue(shareMarketGrowthRateVal);

		Scenario scenario = new Scenario(scenarioResource, userId);
		
		// ------------------
		// ScenarioHousehold.
		// ------------------
		
		Resource householdResource = new ResourceHousehold("Howe Family");
		householdResource.setStartYearMonth(YearMonth.of(2024, 1));

		scenario.addResource(householdResource);
		
		// -------
		// Amanda.
		// -------
		
		Resource amandaResource = new ResourcePerson("Amanda");
		amandaResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamInteger retirementAge = new ResourceParamInteger(ResourceParamNameEnum.PERSON_RETIREMENT_AGE);
		retirementAge.setResource(amandaResource);
		amandaResource.addResourceParam(retirementAge);

		ResourceParamDateValueInteger retirementAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(65));
		retirementAge.addResourceParamDateValue(retirementAgeVal);

		Cashflow amandaEmploymentIncome = new Cashflow(CashflowCategory.INCOME_EMPLOYMENT, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaEmploymentIncome);
		
		CashflowDateRangeValue amandaEmploymentIncomeDRV = new CashflowDateRangeValue(YearMonth.of(2024, 1), Integer.valueOf(2500));
		amandaEmploymentIncome.addCashflowDateRangeValue(amandaEmploymentIncomeDRV);
		
		Cashflow amandaLivingExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaLivingExpense);
		
		CashflowDateRangeValue amandaLivingExpenseDRV = new CashflowDateRangeValue(YearMonth.of(2024, 1), Integer.valueOf(-3000));
		amandaLivingExpense.addCashflowDateRangeValue(amandaLivingExpenseDRV);
	
		scenario.addResource(amandaResource);
		
		// ----
		// Dan.
		// ----
		
		Resource danResource = new ResourcePerson("Dan");
		danResource.setStartYearMonth(YearMonth.of(2024, 1));

		scenario.addResource(danResource);
		
		return scenario;
	}

	private String generateJsonChartData() {

		JsonObject dataTable = new JsonObject();
		JsonArray jsonRows = new JsonArray();

		List<String[][]> columnDefinitions = new ArrayList<String[][]>();
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Date" }, { "pattern", "" }, { "type", "string" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Total" }, { "pattern", "" }, { "type", "number" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Style" }, { "role", "style" }, { "type", "string" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Toooltip" }, { "role", "tooltip" }, { "type", "string" } });

		JsonArray columns = new JsonArray();
		for (String[][] columnDefinition : columnDefinitions) {
			JsonObject cell = new JsonObject();
			for (int i = 0; i < columnDefinition.length; i++) {
				cell.addProperty(columnDefinition[i][0], columnDefinition[i][1]);
			}
			columns.add(cell);
		}
		dataTable.add("cols", columns);

		List<Object[]> rows = new ArrayList<Object[]>();
		rows.add(new Object[] { "12 2024", Integer.valueOf(324), null, null });
		rows.add(new Object[] { "12 2025", Integer.valueOf(654), "point { size: 12; shape-type: star; fill-color: #a52714; }", "Griffin St sold for $4,500,000" });
		rows.add(new Object[] { "12 2026", Integer.valueOf(700), null, null });

		for (Object[] rowData : rows) {
			JsonObject row = new JsonObject();
			JsonArray cells = new JsonArray();

			for (Object cellData : rowData) {
				JsonObject cell = new JsonObject();
				if (null == cellData) {
					cell.addProperty("v", "");					
				} else if (cellData instanceof Integer) {
					cell.addProperty("v", ((Integer) cellData).intValue());
				} else {
					cell.addProperty("v", cellData.toString());
				}
				cells.add(cell);
			}

			row.add("c", cells);
			jsonRows.add(row);
		}

		dataTable.add("rows", jsonRows);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result = gson.toJson(dataTable);

		return result;
	}
}
