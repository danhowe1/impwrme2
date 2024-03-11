package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.controller.dto.cashflow.CashflowDtoConverter;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.controller.dto.resourceDropdown.ResourceDropdownDto;
import com.impwrme2.controller.dto.resourceParam.ResourceParamDtoConverter;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceCreditCard;
import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamInteger;
import com.impwrme2.model.resourceParam.ResourceParamYearMonth;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueYearMonth;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.journalEntry.JournalEntryService;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.scenario.ScenarioService;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/dashboard")
public class DashboardController {

	@Autowired
	private CashflowDtoConverter cashflowDtoConverter;

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceParamDtoConverter resourceParamDtoConverter;

	@Autowired
	private ScenarioService scenarioService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private JournalEntryService journalEntryService;

	@GetMapping(value = { "", "/" })
	public String dashboard(@AuthenticationPrincipal OidcUser user, Model model, HttpSession session, Locale locale) {

		String userId = user.getUserInfo().getSubject();
//session.removeAttribute("SESSION_CURRENT_RESOURCE");
		Resource currentResource = (Resource) session.getAttribute("SESSION_CURRENT_RESOURCE");
		if (null == currentResource) {
			List<Scenario> scenarios = scenarioService.findByUserId(userId);
			if (0 == scenarios.size()) {
				currentResource = populateInitialTestScenario(userId).getSortedResources().first();
			} else if (scenarios.size() == 1) {
				currentResource = scenarios.get(0).getSortedResources().first();
			} else {
				return "forward:/app/scenario/list";
			}
		}
		 
		setUpModelAfterResourceUpdated(currentResource, model, session);
		model.addAttribute("resourceDropdownDto", new ResourceDropdownDto(currentResource.getScenario(), currentResource.getResourceType()));
		
		return "dashboard/dashboard";
	}

	@GetMapping(value = "/getChartData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getChartData(@AuthenticationPrincipal OidcUser user, HttpSession session) {
		return generateJsonChartData(user, session);
	}

	@GetMapping(value = { "/showResourceElements/{resourceId}" })
	public String showResourceElements(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		setUpModelAfterResourceUpdated(resource, model, session);
		return "fragments/dashboard/resourceElements :: resourceElements";
	}

	protected void setUpModelAfterResourceUpdated(Resource resource, Model model, HttpSession session) {
		session.setAttribute("SESSION_CURRENT_RESOURCE", resource);
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("resourceParamTableDto", resourceParamDtoConverter.resourceParamsToResourceParamTableDto(resource.getResourceParams()));
		model.addAttribute("cashflowTableDto", cashflowDtoConverter.cashflowsToCashflowTableDto(resource.getCashflows()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
		model.addAttribute("cashflowDateRangeValueDto", new CashflowDateRangeValueDto());
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

		ResourceParamYearMonth birthYearMonth = new ResourceParamYearMonth(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH);
		amandaResource.addResourceParam(birthYearMonth);

		ResourceParamDateValueYearMonth birthYearMonthVal = new ResourceParamDateValueYearMonth(YearMonth.of(2024, 1), false, YearMonth.of(1972, 2));
		birthYearMonth.addResourceParamDateValue(birthYearMonthVal);

		ResourceParamInteger departureAge = new ResourceParamInteger(ResourceParamNameEnum.PERSON_DEPARTURE_AGE);
		amandaResource.addResourceParam(departureAge);

		ResourceParamDateValueInteger deprtureAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(100));
		departureAge.addResourceParamDateValue(deprtureAgeVal);

		ResourceParamInteger retirementAge = new ResourceParamInteger(ResourceParamNameEnum.PERSON_RETIREMENT_AGE);
		amandaResource.addResourceParam(retirementAge);

		ResourceParamDateValueInteger retirementAgeVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(65));
		retirementAge.addResourceParamDateValue(retirementAgeVal);
		
		Cashflow amandaEmploymentIncome = new Cashflow(CashflowCategory.INCOME_EMPLOYMENT, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaEmploymentIncome);
		
		CashflowDateRangeValue amandaEmploymentIncomeDRV = new CashflowDateRangeValue(amandaEmploymentIncome.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(2500));
		amandaEmploymentIncome.addCashflowDateRangeValue(amandaEmploymentIncomeDRV);
		
		Cashflow amandaLivingExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaLivingExpense);
		
		CashflowDateRangeValue amandaLivingExpenseDRV = new CashflowDateRangeValue(amandaLivingExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-3000));
		amandaLivingExpense.addCashflowDateRangeValue(amandaLivingExpenseDRV);
	
		scenario.addResource(amandaResource);
		
		// ------------
		// Credit card.
		// ------------
		
		Resource creditCard = new ResourceCreditCard("Credit Card");
		creditCard.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamInteger balanceLegalMin = new ResourceParamInteger(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
		creditCard.addResourceParam(balanceLegalMin);

		ResourceParamDateValueInteger balanceLegalMinVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(-15000));
		balanceLegalMin.addResourceParamDateValue(balanceLegalMinVal);

		ResourceParamInteger balanceOpeningLiquid = new ResourceParamInteger(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		creditCard.addResourceParam(balanceOpeningLiquid);

		ResourceParamDateValueInteger balanceOpeningLiquidVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(-12000));
		balanceOpeningLiquid.addResourceParamDateValue(balanceOpeningLiquidVal);		
		
		scenario.addResource(creditCard);
		
		// ----------------
		// Current account.
		// ----------------
		
		Resource currentAccount = new ResourceCurrentAccount("Current Account");
		currentAccount.setStartYearMonth(YearMonth.of(2024, 1));

//		ResourceParamInteger currentAccountBalanceLegalMax = new ResourceParamInteger(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX);
//		currentAccount.addResourceParam(currentAccountBalanceLegalMax);
//
//		ResourceParamDateValueInteger currentAccountBalanceLegalMaxVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.MAX_VALUE);
//		currentAccountBalanceLegalMax.addResourceParamDateValue(currentAccountBalanceLegalMaxVal);

		ResourceParamInteger currentAccountBalanceOpeningLiquid = new ResourceParamInteger(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		currentAccount.addResourceParam(currentAccountBalanceOpeningLiquid);

		ResourceParamDateValueInteger currentAccountBalanceOpeningLiquidVal = new ResourceParamDateValueInteger(YearMonth.of(2024, 1), false, Integer.valueOf(910000));
		currentAccountBalanceOpeningLiquid.addResourceParamDateValue(currentAccountBalanceOpeningLiquidVal);		
		
		scenario.addResource(currentAccount);
		
		return scenario;
	}

	private String generateJsonChartData(OidcUser user, HttpSession session) {

		JournalEntryResponse journalEntryResponse = getOrCreateJournalEntries(user, session);
		
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

	private JournalEntryResponse getOrCreateJournalEntries(OidcUser user, HttpSession session) {
		JournalEntryResponse journalEntryResponse = (JournalEntryResponse) session.getAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");		
		if (null == journalEntryResponse) {
			Resource sessionResource = (Resource) session.getAttribute("SESSION_CURRENT_RESOURCE");
			Scenario scenario = scenarioService.findByIdAndUserId(sessionResource.getScenario().getId(), user.getUserInfo().getSubject()).get();
			journalEntryResponse = journalEntryService.run(scenario);
			session.setAttribute("SESSION_JOURNAL_ENTRY_RESPONSE", journalEntryResponse);
		}
		return journalEntryResponse;
	}
}
