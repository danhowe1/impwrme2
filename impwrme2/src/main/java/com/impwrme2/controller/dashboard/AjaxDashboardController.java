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

		System.out.println(result);
		return result;
	}

//			 [ '12 2024', 557643.93, null, null ], 
//			 [ '12 2025', 4166636.96, null, null ], 
//			 [ '12 2026', 4029662.43, 'point { size: 12; shape-type: star; fill-color: #a52714; }', createCustomChartTooltipContent('Griffin St sold for $4,500,000') ],
//				[ '12 2027', 3882082.35, null, null ], [ '12 2028', 3628303.20, null, null ], [ '12 2029', 3470565.03, null, null ], [ '12 2030', 3162952.03, null, null ], [ '12 2031', 4282311.85, null, null ],
//				[ '12 2032', 5460333.27, 'point { size: 12; shape-type: star; fill-color: #a52714; }', createCustomChartTooltipContent('Super - Dan Superannuation matures') ],
//				[ '12 2033', 5533304.46, 'point { size: 12; shape-type: star; fill-color: #a52714; }', createCustomChartTooltipContent('Super - Amanda Superannuation matures') ], [ '12 2034', 5625884.62, null, null ],
//				[ '12 2035', 5739795.09, null, null ], [ '12 2036', 5996246.45, null, null ], [ '12 2037', 6340919.17, null, null ], [ '12 2038', 6715082.12, null, null ], [ '12 2039', 7163521.02, null, null ],
//				[ '12 2040', 7654122.84, null, null ], [ '12 2041', 8186367.49, null, null ], [ '12 2042', 8764004.93, null, null ], [ '12 2043', 9421569.49, null, null ], [ '12 2044', 10136483.75, null, null ],
//				[ '12 2045', 10913945.23, null, null ], [ '12 2046', 11759626.59, null, null ], [ '12 2047', 12679719.16, null, null ], [ '12 2048', 13680980.49, null, null ], [ '12 2049', 14770786.14, null, null ],
//				[ '12 2050', 15957186.35, null, null ], [ '12 2051', 17248967.85, null, null ], [ '12 2052', 18655721.26, null, null ], [ '12 2053', 20187914.84, null, null ], [ '12 2054', 21856974.79, null, null ],
//				[ '12 2055', 23675373.09, null, null ], [ '12 2056', 25656723.27, null, null ], [ '12 2057', 27815885.01, null, null ], [ '12 2058', 30169078.30, null, null ], [ '12 2059', 32734008.10, null, null ],
//				[ '12 2060', 35530000.37, null, null ], [ '12 2061', 38578150.64, null, null ], [ '12 2062', 41901486.08, null, null ], [ '12 2063', 45525142.55, null, null ], [ '12 2064', 49476557.78, null, null ],
//				[ '12 2065', 53785682.31, null, null ], [ '12 2066', 58485209.74, null, null ], [ '12 2067', 63610828.06, null, null ], [ '12 2068', 69201494.05, null, null ], [ '12 2069', 75299732.81, null, null ],
//				[ '12 2070', 81951964.71, null, null ], [ '12 2071', 89229147.36, null, null ], [ '12 2072', 90532267.03, null, null ] ]			
}
