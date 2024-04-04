package com.impwrme2.controller.dashboard;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.dto.cashflow.CashflowCreateDto;
import com.impwrme2.controller.dto.cashflow.CashflowDto;
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
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceCreditCard;
import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerNegative;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParam.ResourceParamYearMonth;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerNegative;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueYearMonth;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.scenario.ScenarioService;
import com.impwrme2.service.ui.UIDisplayFilter;

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

	@GetMapping(value = { "", "/" })
	public String dashboard(@AuthenticationPrincipal OidcUser user, Model model, HttpSession session, Locale locale) {

		String userId = user.getUserInfo().getSubject();
		Resource currentResource;
//session.removeAttribute("SESSION_CURRENT_RESOURCE_ID");
		Long currentResourceId = (Long) session.getAttribute("SESSION_CURRENT_RESOURCE_ID");
		if (null == currentResourceId) {
			List<Scenario> scenarios = scenarioService.findByUserId(userId);
			if (0 == scenarios.size()) {
				currentResource = populateInitialTestScenario(userId).getSortedResources().first();
			} else if (scenarios.size() == 1) {
				currentResource = scenarios.get(0).getSortedResources().first();
			} else {
				return "forward:/app/scenario/list";
			}
		} else {
			currentResource = resourceService.findById(currentResourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + currentResourceId));
		}
		 
		// Display filter.
		UIDisplayFilter displayFilter = getDisplayFilterFromSession(currentResource.getScenario(), session);
		model.addAttribute("displayFilter", displayFilter);

		setUpModelAfterResourceUpdated(currentResource, model, session);
		model.addAttribute("resourceDropdownDto", new ResourceDropdownDto(currentResource.getScenario(), currentResource.getResourceType()));
		
		return "dashboard/dashboard";
	}

	@GetMapping(value = { "/showResourceElements/{resourceId}" })
	public String showResourceElements(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		setUpModelAfterResourceUpdated(resource, model, session);
		return "fragments/dashboard/resourceElements :: resourceElements";
	}

	protected void setUpModelAfterResourceUpdated(Resource resource, Model model, HttpSession session) {
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", resource.getId());
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("resourceParamTableDto", resourceParamDtoConverter.resourceParamsToResourceParamTableDto(resource.getResourceParams()));
		model.addAttribute("cashflowTableDto", cashflowDtoConverter.cashflowsToCashflowTableDto(resource.getCashflows()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
		model.addAttribute("cashflowDto", new CashflowDto());
		model.addAttribute("cashflowCreateDto", new CashflowCreateDto());
		model.addAttribute("cashflowDateRangeValueDto", new CashflowDateRangeValueDto());
	}

	private Scenario populateInitialTestScenario(String userId) {
		Scenario scenario = getInitialTestScenario(userId);
		return scenarioService.save(scenario, userId);
	}

	private UIDisplayFilter getDisplayFilterFromSession(final Scenario scenario, final HttpSession session) {
		UIDisplayFilter displayFilter = (UIDisplayFilter) session.getAttribute("SESSION_DISPLAY_FILTER");
		if (null == displayFilter) {
			displayFilter = new UIDisplayFilter();
			displayFilter.setYearEnd(scenario.calculateEndYearMonth().getYear());
			for (int year=scenario.getStartYearMonth().getYear(); year <= displayFilter.getYearEnd(); year++) {
				displayFilter.addYearList(String.valueOf(year));
			}

			session.setAttribute("SESSION_DISPLAY_FILTER", displayFilter);
		}
		return displayFilter;
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

		ResourceParamIntegerPositive departureAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_DEPARTURE_AGE);
		amandaResource.addResourceParam(departureAge);

		ResourceParamDateValueIntegerPositive deprtureAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(100));
		departureAge.addResourceParamDateValue(deprtureAgeVal);

		ResourceParamIntegerPositive retirementAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_RETIREMENT_AGE);
		amandaResource.addResourceParam(retirementAge);

		ResourceParamDateValueIntegerPositive retirementAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(65));
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

		ResourceParamIntegerNegative balanceLegalMin = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
		balanceLegalMin.setUserAbleToCreateNewDateValue(true);
		creditCard.addResourceParam(balanceLegalMin);

		ResourceParamDateValueIntegerNegative balanceLegalMinVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-15000));
		balanceLegalMin.addResourceParamDateValue(balanceLegalMinVal);

		ResourceParamIntegerNegative balanceOpeningLiquid = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		creditCard.addResourceParam(balanceOpeningLiquid);

		ResourceParamDateValueIntegerNegative balanceOpeningLiquidVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-12000));
		balanceOpeningLiquid.addResourceParamDateValue(balanceOpeningLiquidVal);		
		
		scenario.addResource(creditCard);
		
		// ----------------
		// Current account.
		// ----------------
		
		Resource currentAccount = new ResourceCurrentAccount("Current Account");
		currentAccount.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamIntegerPositive currentAccountBalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		currentAccount.addResourceParam(currentAccountBalanceOpeningLiquid);

		ResourceParamDateValueIntegerPositive currentAccountBalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(910000));
		currentAccountBalanceOpeningLiquid.addResourceParamDateValue(currentAccountBalanceOpeningLiquidVal);		
		
		scenario.addResource(currentAccount);
		
		return scenario;
	}
}
