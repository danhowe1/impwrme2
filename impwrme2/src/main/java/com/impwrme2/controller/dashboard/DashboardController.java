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
import com.impwrme2.controller.dto.resourceParam.ResourceParamCreateDto;
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
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerNegative;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParam.ResourceParamString;
import com.impwrme2.model.resourceParam.ResourceParamYearMonth;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerNegative;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueString;
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
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
		model.addAttribute("resourceParamCreateDto", new ResourceParamCreateDto());
		model.addAttribute("cashflowTableDto", cashflowDtoConverter.cashflowsToCashflowTableDto(resource.getCashflows()));
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

		ResourceParamBigDecimal housingMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_HOUSING_MARKET_GROWTH_RATE);
		housingMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
		scenarioResource.addResourceParam(housingMarketGrowthRate);

		ResourceParamDateValueBigDecimal housingMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
		housingMarketGrowthRate.addResourceParamDateValue(housingMarketGrowthRateVal);

		Scenario scenario = new Scenario(scenarioResource, userId);
		
		// ----------
		// Household.
		// ----------
		
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
		
		// ------------------
		// Current account 1.
		// ------------------
		
		Resource currentAccount1 = new ResourceCurrentAccount("Current Account 1");
		currentAccount1.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamIntegerPositive currentAccount1BalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		currentAccount1.addResourceParam(currentAccount1BalanceOpeningLiquid);

		ResourceParamDateValueIntegerPositive currentAccount1BalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(30000));
		currentAccount1BalanceOpeningLiquid.addResourceParamDateValue(currentAccount1BalanceOpeningLiquidVal);		
		
		scenario.addResource(currentAccount1);
		
		// ------------------
		// Current account 2.
		// ------------------
		
		Resource currentAccount2 = new ResourceCurrentAccount("Current Account 2");
		currentAccount2.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamIntegerPositive currentAccount2BalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		currentAccount2.addResourceParam(currentAccount2BalanceOpeningLiquid);

		ResourceParamDateValueIntegerPositive currentAccount2BalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(20000));
		currentAccount2BalanceOpeningLiquid.addResourceParamDateValue(currentAccount2BalanceOpeningLiquidVal);		
		
		scenario.addResource(currentAccount2);

		// ---------------
		// Griffin Street.
		// ---------------
		
		Resource griffinSt = new ResourcePropertyExisting("Griffin Street");
		griffinSt.setStartYearMonth(YearMonth.of(2024, 1));

		// Griffin Street BALANCE_OPENING_FIXED.
		ResourceParamIntegerPositive griffinStOpeningAssetValue = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		griffinSt.addResourceParam(griffinStOpeningAssetValue);
		ResourceParamDateValueIntegerPositive griffinStOpeningAssetVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(3700000));
		griffinStOpeningAssetValue.addResourceParamDateValue(griffinStOpeningAssetVal);		

		// Griffin Street PROPERTY_STATUS.
		ResourceParamString griffinStStatus = new ResourceParamString(ResourceParamNameEnum.PROPERTY_STATUS);
		griffinStStatus.setUserAbleToCreateNewDateValue(true);
		griffinSt.addResourceParam(griffinStStatus);
		ResourceParamDateValueString griffinStStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourcePropertyExisting.PROPERTY_STATUS_LIVING_IN);
		griffinStStatus.addResourceParamDateValue(griffinStStatusVal);		

		// Griffin Street ASSET_OWNERSHIP.
		Cashflow griffinStAssetOwnership = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, CashflowFrequency.MONTHLY, Boolean.TRUE);
		griffinSt.addCashflow(griffinStAssetOwnership);
		CashflowDateRangeValue griffinStAssetOwnershipDRV = new CashflowDateRangeValue(griffinStAssetOwnership.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-1500));
		griffinStAssetOwnership.addCashflowDateRangeValue(griffinStAssetOwnershipDRV);

		// Griffin Street HOUSING_MARKET_GROWTH_RATE.
//		ResourceParamBigDecimal griffinStGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE);
//		griffinStGrowthRate.setUserAbleToCreateNewDateValue(true);
//		griffinSt.addResourceParam(griffinStGrowthRate);
//		ResourceParamDateValueBigDecimal griffinStGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
//		griffinStGrowthRate.addResourceParamDateValue(griffinStGrowthRateVal);

		scenario.addResource(griffinSt);

		return scenario;
	}
}
