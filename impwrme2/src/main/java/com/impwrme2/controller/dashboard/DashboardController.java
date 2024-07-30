package com.impwrme2.controller.dashboard;

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
import com.impwrme2.model.resource.Resource;
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
			if (1 == scenarios.size()) {
				currentResource = scenarios.get(0).getResourceScenario();
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

//	private Scenario populateInitialTestScenario(String userId) {
//		Scenario scenario = getInitialTestScenario(userId);
//		return scenarioService.save(scenario, userId);
//	}

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

	/*
	private Scenario getInitialTestScenario(String userId) {

		// -----------------
		// ScenarioResource.
		// -----------------
		
		ResourceScenario scenarioResource = new ResourceScenario("My first scenario");
		scenarioResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamBigDecimal cpi = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_CPI);
		cpi.setUserAbleToCreateNewDateValue(true);
		scenarioResource.addResourceParam(cpi);
		ResourceParamDateValueBigDecimal cpiVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("2.00"));
		cpi.addResourceParamDateValue(cpiVal);

		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_SHARE_MARKET_GROWTH_RATE);
		shareMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
		scenarioResource.addResourceParam(shareMarketGrowthRate);
		ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("4.0"));
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

		Cashflow householdCarExpense = new Cashflow(CashflowCategory.EXPENSE_MISC, "Car", CashflowFrequency.ONE_OFF, Boolean.FALSE);
		householdResource.addCashflow(householdCarExpense);		
		CashflowDateRangeValue householdCarExpenseDRV = new CashflowDateRangeValue(householdCarExpense.getCategory().getType(), YearMonth.of(2028, 4), Integer.valueOf(-100000));
		householdCarExpense.addCashflowDateRangeValue(householdCarExpenseDRV);
	
		scenario.addResource(householdResource);
		
		// -------
		// Amanda.
		// -------
		
		Resource amandaResource = new ResourcePersonAdult("Amanda");
		amandaResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamYearMonth birthYearMonth = new ResourceParamYearMonth(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH);
		amandaResource.addResourceParam(birthYearMonth);
		ResourceParamDateValueYearMonth birthYearMonthVal = new ResourceParamDateValueYearMonth(YearMonth.of(2024, 1), false, YearMonth.of(1972, 2));
		birthYearMonth.addResourceParamDateValue(birthYearMonthVal);

		ResourceParamIntegerPositive departureAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_DEPARTURE_AGE);
		amandaResource.addResourceParam(departureAge);
		ResourceParamDateValueIntegerPositive deprtureAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(100));
		departureAge.addResourceParamDateValue(deprtureAgeVal);

		ResourceParamIntegerPositive retirementAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_ADULT_RETIREMENT_AGE);
		amandaResource.addResourceParam(retirementAge);
		ResourceParamDateValueIntegerPositive retirementAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(45));
		retirementAge.addResourceParamDateValue(retirementAgeVal);
		
//		Cashflow amandaEmploymentIncome = new Cashflow(CashflowCategory.INCOME_EMPLOYMENT, CashflowFrequency.MONTHLY, Boolean.TRUE);
//		amandaResource.addCashflow(amandaEmploymentIncome);
//		CashflowDateRangeValue amandaEmploymentIncomeDRV = new CashflowDateRangeValue(amandaEmploymentIncome.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(2500));
//		amandaEmploymentIncome.addCashflowDateRangeValue(amandaEmploymentIncomeDRV);
		
		Cashflow amandaLivingExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaLivingExpense);		
		CashflowDateRangeValue amandaLivingExpenseDRV = new CashflowDateRangeValue(amandaLivingExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-3000));
		amandaLivingExpense.addCashflowDateRangeValue(amandaLivingExpenseDRV);
	
		Cashflow amandaLivingSplurgeExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaLivingSplurgeExpense);		
		CashflowDateRangeValue amandaLivingSplurgeExpenseDRV = new CashflowDateRangeValue(amandaLivingSplurgeExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-2500));
		amandaLivingSplurgeExpense.addCashflowDateRangeValue(amandaLivingSplurgeExpenseDRV);
	
		Cashflow amandaFlightsExpense = new Cashflow(CashflowCategory.EXPENSE_HOLIDAYS, "Flights", CashflowFrequency.ANNUALLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaFlightsExpense);		
		CashflowDateRangeValue amandaFlightsExpenseDRV = new CashflowDateRangeValue(amandaFlightsExpense.getCategory().getType(), YearMonth.of(2024, 6), YearMonth.of(2043, 1), Integer.valueOf(-10000));
		amandaFlightsExpense.addCashflowDateRangeValue(amandaFlightsExpenseDRV);
	
		Cashflow amandaUKPensionIncome = new Cashflow(CashflowCategory.INCOME_MISC, "UK pension", CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaResource.addCashflow(amandaUKPensionIncome);		
		CashflowDateRangeValue amandaUKPensionIncomeDRV = new CashflowDateRangeValue(amandaUKPensionIncome.getCategory().getType(), YearMonth.of(2039, 3), Integer.valueOf(2000));
		amandaUKPensionIncome.addCashflowDateRangeValue(amandaUKPensionIncomeDRV);
	
		scenario.addResource(amandaResource);
		
		// ---------------
		// Super (Amanda).
		// ---------------

		Resource amandaSuper = new ResourceSuperannuation("Super - Amanda");
		amandaSuper.setStartYearMonth(YearMonth.of(2024, 1));

		amandaResource.addChild(amandaSuper);

		// Super (Amanda) BALANCE_OPENING_FIXED.
		ResourceParamIntegerPositive amandaSuperBalanceOpeningFixed = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		amandaSuper.addResourceParam(amandaSuperBalanceOpeningFixed);
		ResourceParamDateValueIntegerPositive amandaSuperBalanceOpeningFixedVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(713185));
		amandaSuperBalanceOpeningFixed.addResourceParamDateValue(amandaSuperBalanceOpeningFixedVal);

		// Super (Amanda) SUPER_PRESERVATION_AGE.
		ResourceParamIntegerPositive amandaPreservationAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.SUPER_PRESERVATION_AGE);
		amandaSuper.addResourceParam(amandaPreservationAge);
		ResourceParamDateValueIntegerPositive amandaPreservationAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(60));
		amandaPreservationAge.addResourceParamDateValue(amandaPreservationAgeVal);

		// Super (Amanda) SUPER_GROWTH_RATE.
		ResourceParamBigDecimal amandaSuperGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SUPER_GROWTH_RATE);
		amandaSuperGrowthRate.setUserAbleToCreateNewDateValue(true);
		amandaSuper.addResourceParam(amandaSuperGrowthRate);
		ResourceParamDateValueBigDecimal amandaSuperGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
		amandaSuperGrowthRate.addResourceParamDateValue(amandaSuperGrowthRateVal);

		// Super (Amanda) SUPER_FEE_ANNUAL_PERCENTAGE.
		ResourceParamBigDecimal amandaSuperFeeAnnualPercentage = new ResourceParamBigDecimal(ResourceParamNameEnum.SUPER_MANAGEMENT_FEE_ANNUAL_PERCENTAGE);
		amandaSuperFeeAnnualPercentage.setUserAbleToCreateNewDateValue(true);
		amandaSuper.addResourceParam(amandaSuperFeeAnnualPercentage);
		ResourceParamDateValueBigDecimal amandaSuperFeeAnnualPercentageVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("0.02"));
		amandaSuperFeeAnnualPercentage.addResourceParamDateValue(amandaSuperFeeAnnualPercentageVal);

		// Super (Amanda) DEPRECIATION_FEE.
		Cashflow amandaSuperAdminFee = new Cashflow(CashflowCategory.DEPRECIATION_ADMIN_FEE, CashflowFrequency.MONTHLY, Boolean.TRUE);
		amandaSuper.addCashflow(amandaSuperAdminFee);		
		CashflowDateRangeValue amandaSuperAdminFeeDRV = new CashflowDateRangeValue(amandaSuperAdminFee.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-6));
		amandaSuperAdminFee.addCashflowDateRangeValue(amandaSuperAdminFeeDRV);

		// Super (Amanda) APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL.
		Cashflow amandaSuperContribNonConcPersonal = new Cashflow(CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL, CashflowFrequency.ANNUALLY, Boolean.FALSE);
		amandaSuper.addCashflow(amandaSuperContribNonConcPersonal);		
		CashflowDateRangeValue amandaSuperContribNonConcPersonalDRV = new CashflowDateRangeValue(amandaSuperAdminFee.getCategory().getType(), YearMonth.of(2026, 6), YearMonth.of(2032, 2), Integer.valueOf(27500));
		amandaSuperContribNonConcPersonal.addCashflowDateRangeValue(amandaSuperContribNonConcPersonalDRV);
		
		scenario.addResource(amandaSuper);

		// ----
		// Dan.
		// ----
		
		Resource danResource = new ResourcePersonAdult("Dan");
		danResource.setStartYearMonth(YearMonth.of(2024, 1));

		ResourceParamYearMonth danBirthYearMonth = new ResourceParamYearMonth(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH);
		danResource.addResourceParam(danBirthYearMonth);
		ResourceParamDateValueYearMonth danBirthYearMonthVal = new ResourceParamDateValueYearMonth(YearMonth.of(2024, 1), false, YearMonth.of(1971, 11));
		danBirthYearMonth.addResourceParamDateValue(danBirthYearMonthVal);

		ResourceParamIntegerPositive danDepartureAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_DEPARTURE_AGE);
		danResource.addResourceParam(danDepartureAge);
		ResourceParamDateValueIntegerPositive danDeprtureAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(100));
		danDepartureAge.addResourceParamDateValue(danDeprtureAgeVal);

		ResourceParamIntegerPositive danRetirementAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_ADULT_RETIREMENT_AGE);
		danResource.addResourceParam(danRetirementAge);
		ResourceParamDateValueIntegerPositive danRetirementAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(45));
		danRetirementAge.addResourceParamDateValue(danRetirementAgeVal);
		
//		Cashflow danEmploymentIncome = new Cashflow(CashflowCategory.INCOME_EMPLOYMENT, CashflowFrequency.MONTHLY, Boolean.TRUE);
//		danResource.addCashflow(danEmploymentIncome);
//		CashflowDateRangeValue danEmploymentIncomeDRV = new CashflowDateRangeValue(danEmploymentIncome.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(2500));
//		danEmploymentIncome.addCashflowDateRangeValue(danEmploymentIncomeDRV);
		
		Cashflow danLivingExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		danResource.addCashflow(danLivingExpense);		
		CashflowDateRangeValue danLivingExpenseDRV = new CashflowDateRangeValue(danLivingExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-3000));
		danLivingExpense.addCashflowDateRangeValue(danLivingExpenseDRV);
	
		Cashflow danLivingSplurgeExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		danResource.addCashflow(danLivingSplurgeExpense);		
		CashflowDateRangeValue danLivingSplurgeExpenseDRV = new CashflowDateRangeValue(danLivingSplurgeExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-2500));
		danLivingSplurgeExpense.addCashflowDateRangeValue(danLivingSplurgeExpenseDRV);
	
		Cashflow danFlightsExpense = new Cashflow(CashflowCategory.EXPENSE_HOLIDAYS, "Flights", CashflowFrequency.ANNUALLY, Boolean.TRUE);
		danResource.addCashflow(danFlightsExpense);		
		CashflowDateRangeValue danFlightsExpenseDRV = new CashflowDateRangeValue(danFlightsExpense.getCategory().getType(), YearMonth.of(2024, 6), YearMonth.of(2043, 1), Integer.valueOf(-10000));
		danFlightsExpense.addCashflowDateRangeValue(danFlightsExpenseDRV);
	
		Cashflow danCanadaExpense = new Cashflow(CashflowCategory.EXPENSE_HOLIDAYS, "Canada heli ski", CashflowFrequency.ONE_OFF, Boolean.FALSE);
		danResource.addCashflow(danCanadaExpense);		
		CashflowDateRangeValue danCanadaExpenseDRV = new CashflowDateRangeValue(danCanadaExpense.getCategory().getType(), YearMonth.of(2025, 3), Integer.valueOf(-25000));
		danCanadaExpense.addCashflowDateRangeValue(danCanadaExpenseDRV);
	
		Cashflow danUKPensionIncome = new Cashflow(CashflowCategory.INCOME_MISC, "UK pension", CashflowFrequency.MONTHLY, Boolean.TRUE);
		danResource.addCashflow(danUKPensionIncome);		
		CashflowDateRangeValue danUKPensionIncomeDRV = new CashflowDateRangeValue(danUKPensionIncome.getCategory().getType(), YearMonth.of(2038, 12), Integer.valueOf(2000));
		danUKPensionIncome.addCashflowDateRangeValue(danUKPensionIncomeDRV);
	
		scenario.addResource(danResource);
		
		// ------------
		// Super (Dan).
		// ------------

		Resource danSuper = new ResourceSuperannuation("Super - Dan");
		danSuper.setStartYearMonth(YearMonth.of(2024, 1));

		danResource.addChild(danSuper);

		// Super (Dan) BALANCE_OPENING_FIXED.
		ResourceParamIntegerPositive danSuperBalanceOpeningFixed = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		danSuper.addResourceParam(danSuperBalanceOpeningFixed);
		ResourceParamDateValueIntegerPositive danSuperBalanceOpeningFixedVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(808644));
		danSuperBalanceOpeningFixed.addResourceParamDateValue(danSuperBalanceOpeningFixedVal);

		// Super (Dan) SUPER_PRESERVATION_AGE.
		ResourceParamIntegerPositive danPreservationAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.SUPER_PRESERVATION_AGE);
		danSuper.addResourceParam(danPreservationAge);
		ResourceParamDateValueIntegerPositive danPreservationAgeVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(60));
		danPreservationAge.addResourceParamDateValue(danPreservationAgeVal);

		// Super (Dan) SUPER_GROWTH_RATE.
		ResourceParamBigDecimal danSuperGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SUPER_GROWTH_RATE);
		danSuperGrowthRate.setUserAbleToCreateNewDateValue(true);
		danSuper.addResourceParam(danSuperGrowthRate);
		ResourceParamDateValueBigDecimal danSuperGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
		danSuperGrowthRate.addResourceParamDateValue(danSuperGrowthRateVal);

		// Super (Dan) SUPER_FEE_ANNUAL_PERCENTAGE.
		ResourceParamBigDecimal danSuperFeeAnnualPercentage = new ResourceParamBigDecimal(ResourceParamNameEnum.SUPER_MANAGEMENT_FEE_ANNUAL_PERCENTAGE);
		danSuperFeeAnnualPercentage.setUserAbleToCreateNewDateValue(true);
		danSuper.addResourceParam(danSuperFeeAnnualPercentage);
		ResourceParamDateValueBigDecimal danSuperFeeAnnualPercentageVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("0.02"));
		danSuperFeeAnnualPercentage.addResourceParamDateValue(danSuperFeeAnnualPercentageVal);

		// Super (Dan) DEPRECIATION_FEE.
		Cashflow danSuperAdminFee = new Cashflow(CashflowCategory.DEPRECIATION_ADMIN_FEE, CashflowFrequency.MONTHLY, Boolean.TRUE);
		danSuper.addCashflow(danSuperAdminFee);		
		CashflowDateRangeValue danSuperAdminFeeDRV = new CashflowDateRangeValue(danSuperAdminFee.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-6));
		danSuperAdminFee.addCashflowDateRangeValue(danSuperAdminFeeDRV);

		// Super (Dan) APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL.
		Cashflow danSuperContribNonConcPersonal = new Cashflow(CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL, CashflowFrequency.ANNUALLY, Boolean.FALSE);
		danSuper.addCashflow(danSuperContribNonConcPersonal);		
		CashflowDateRangeValue danSuperContribNonConcPersonalDRV = new CashflowDateRangeValue(danSuperAdminFee.getCategory().getType(), YearMonth.of(2026, 6), YearMonth.of(2032, 2), Integer.valueOf(27500));
		danSuperContribNonConcPersonal.addCashflowDateRangeValue(danSuperContribNonConcPersonalDRV);
		
		scenario.addResource(danSuper);

		// ------------
		// Credit card.
		// ------------
		
//		Resource creditCard = new ResourceCreditCard("Credit Card");
//		creditCard.setStartYearMonth(YearMonth.of(2024, 1));
//
//		ResourceParamIntegerNegative balanceLegalMin = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
//		balanceLegalMin.setUserAbleToCreateNewDateValue(true);
//		creditCard.addResourceParam(balanceLegalMin);
//
//		ResourceParamDateValueIntegerNegative balanceLegalMinVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-15000));
//		balanceLegalMin.addResourceParamDateValue(balanceLegalMinVal);
//
//		ResourceParamIntegerNegative balanceOpeningLiquid = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
//		creditCard.addResourceParam(balanceOpeningLiquid);
//
//		ResourceParamDateValueIntegerNegative balanceOpeningLiquidVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-12000));
//		balanceOpeningLiquid.addResourceParamDateValue(balanceOpeningLiquidVal);		
//		
//		scenario.addResource(creditCard);
		
		// ------------------
		// Current account 1.
		// ------------------
		
//		Resource currentAccount1 = new ResourceCurrentAccount("Current Account 1");
//		currentAccount1.setStartYearMonth(YearMonth.of(2024, 1));
//
//		ResourceParamIntegerPositive currentAccount1BalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
//		currentAccount1.addResourceParam(currentAccount1BalanceOpeningLiquid);
//
//		ResourceParamDateValueIntegerPositive currentAccount1BalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(30000));
//		currentAccount1BalanceOpeningLiquid.addResourceParamDateValue(currentAccount1BalanceOpeningLiquidVal);		
//		
//		scenario.addResource(currentAccount1);
		
		// ------------------
		// Current account 2.
		// ------------------
		
//		Resource currentAccount2 = new ResourceCurrentAccount("Current Account 2");
//		currentAccount2.setStartYearMonth(YearMonth.of(2024, 1));
//
//		ResourceParamIntegerPositive currentAccount2BalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
//		currentAccount2.addResourceParam(currentAccount2BalanceOpeningLiquid);
//
//		ResourceParamDateValueIntegerPositive currentAccount2BalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(20000));
//		currentAccount2BalanceOpeningLiquid.addResourceParamDateValue(currentAccount2BalanceOpeningLiquidVal);		
//		
//		scenario.addResource(currentAccount2);

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
		ResourceParamDateValueString griffinStStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourcePropertyExisting.PROPERTY_STATUS_RENTED);
		griffinStStatus.addResourceParamDateValue(griffinStStatusVal);		

		ResourceParamDateValueString griffinStStatus2Val = new ResourceParamDateValueString(YearMonth.of(2024, 2), true, ResourcePropertyExisting.PROPERTY_STATUS_LIVING_IN);
		griffinStStatus.addResourceParamDateValue(griffinStStatus2Val);		

		ResourceParamDateValueString griffinStStatus3Val = new ResourceParamDateValueString(YearMonth.of(2025, 11), true, ResourcePropertyExisting.PROPERTY_STATUS_SOLD);
		griffinStStatus.addResourceParamDateValue(griffinStStatus3Val);		

		// Griffin Street EXPENSE_ASSET_OWNERSHIP.
		Cashflow griffinStAssetOwnership = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, CashflowFrequency.MONTHLY, Boolean.TRUE);
		griffinSt.addCashflow(griffinStAssetOwnership);
		CashflowDateRangeValue griffinStAssetOwnershipDRV = new CashflowDateRangeValue(griffinStAssetOwnership.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-1500));
		griffinStAssetOwnership.addCashflowDateRangeValue(griffinStAssetOwnershipDRV);

		// Griffin Street EXPENSE_ASSET_OWNERSHIP RENOS.
		Cashflow griffinStAssetOwnershipRenos = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, "Renos", CashflowFrequency.ONE_OFF, Boolean.FALSE);
		griffinSt.addCashflow(griffinStAssetOwnershipRenos);
		CashflowDateRangeValue griffinStAssetOwnershipRenosDRV = new CashflowDateRangeValue(griffinStAssetOwnershipRenos.getCategory().getType(), YearMonth.of(2024, 5), Integer.valueOf(-70000));
		griffinStAssetOwnershipRenos.addCashflowDateRangeValue(griffinStAssetOwnershipRenosDRV);

		// Griffin Street HOUSING_MARKET_GROWTH_RATE.
//		ResourceParamBigDecimal griffinStGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE);
//		griffinStGrowthRate.setUserAbleToCreateNewDateValue(true);
//		griffinSt.addResourceParam(griffinStGrowthRate);
//		ResourceParamDateValueBigDecimal griffinStGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
//		griffinStGrowthRate.addResourceParamDateValue(griffinStGrowthRateVal);

		// Griffin Street EXPENSE_RENTAL_PROPERTY.
		Cashflow griffinStRentalExpense = new Cashflow(CashflowCategory.EXPENSE_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
		griffinSt.addCashflow(griffinStRentalExpense);
		CashflowDateRangeValue griffinStRentalExpenseDRV = new CashflowDateRangeValue(griffinStRentalExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-400));
		griffinStRentalExpense.addCashflowDateRangeValue(griffinStRentalExpenseDRV);

		// Griffin Street INCOME_RENTAL_PROPERTY.
		Cashflow griffinStRentalIncome = new Cashflow(CashflowCategory.INCOME_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
		griffinSt.addCashflow(griffinStRentalIncome);
		CashflowDateRangeValue griffinStRentalIncomeDRV = new CashflowDateRangeValue(griffinStRentalIncome.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(6933));
		griffinStRentalIncome.addCashflowDateRangeValue(griffinStRentalIncomeDRV);

		// Griffin Street EXPENSE_ASSET_SALE_COMMISSION.
		ResourceParamBigDecimal griffinStSaleCommission = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_ASSET_SALE_COMMISSION);
		griffinSt.addResourceParam(griffinStSaleCommission);
		ResourceParamDateValueBigDecimal griffinStSaleCommissionVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("1.5"));
		griffinStSaleCommission.addResourceParamDateValue(griffinStSaleCommissionVal);

		// Griffin Street EXPENSE_ASSET_SALE_FIXED.
		ResourceParamIntegerPositive griffinStSaleFixed = new ResourceParamIntegerPositive(ResourceParamNameEnum.PROPERTY_ASSET_SALE_FIXED);
		griffinSt.addResourceParam(griffinStSaleFixed);
		ResourceParamDateValueIntegerPositive griffinStSaleFixedVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(10000));
		griffinStSaleFixed.addResourceParamDateValue(griffinStSaleFixedVal);

		scenario.addResource(griffinSt);

		// ------------------------
		// Griffin Street Mortgage.
		// ------------------------
		
		Resource griffinStMortgage = new ResourceMortgage("Griffin Street Variable");
		griffinStMortgage.setStartYearMonth(YearMonth.of(2024, 1));
		
		griffinSt.addChild(griffinStMortgage);

		// Griffin Street Mortgage BALANCE_OPENING_FIXED.
		ResourceParamIntegerNegative griffinStMortgageOpeningAssetValue = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		griffinStMortgage.addResourceParam(griffinStMortgageOpeningAssetValue);
		ResourceParamDateValueIntegerNegative griffinStMortgageOpeningAssetVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-656116));
		griffinStMortgageOpeningAssetValue.addResourceParamDateValue(griffinStMortgageOpeningAssetVal);		

		// Griffin Street Mortgage MORTGAGE_INTEREST_RATE.
		ResourceParamBigDecimal griffinStInterestRate = new ResourceParamBigDecimal(ResourceParamNameEnum.MORTGAGE_INTEREST_RATE);
		griffinStInterestRate.setUserAbleToCreateNewDateValue(true);
		griffinStMortgage.addResourceParam(griffinStInterestRate);
		ResourceParamDateValueBigDecimal griffinStInterestRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal(7.14));
		griffinStInterestRate.addResourceParamDateValue(griffinStInterestRateVal);		

		// Griffin Street Mortgage MORTGAGE_REMAINING_MONTHS.
		ResourceParamIntegerPositive griffinStRemainingMonths = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS);
		griffinStMortgage.addResourceParam(griffinStRemainingMonths);
		ResourceParamDateValueIntegerPositive griffinStRemainingMonthsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(8));
		griffinStRemainingMonths.addResourceParamDateValue(griffinStRemainingMonthsVal);

		// Griffin Street Mortgage MORTGAGE_REMAINING_YEARS.
		ResourceParamIntegerPositive griffinStRemainingYears = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS);
		griffinStMortgage.addResourceParam(griffinStRemainingYears);
		ResourceParamDateValueIntegerPositive griffinStRemainingYearsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(13));
		griffinStRemainingYears.addResourceParamDateValue(griffinStRemainingYearsVal);
		
		// Griffin Street Mortgage MORTGAGE_REPAYMENT_TYPE.
		ResourceParamString griffinStMortgageStatus = new ResourceParamString(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE);
		griffinStMortgageStatus.setUserAbleToCreateNewDateValue(true);
		griffinStMortgage.addResourceParam(griffinStMortgageStatus);
		ResourceParamDateValueString griffinStMortgageStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourceMortgage.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST);
		griffinStMortgageStatus.addResourceParamDateValue(griffinStMortgageStatusVal);		

		scenario.addResource(griffinStMortgage);

		// -------------
		// Addison Road.
		// -------------
		
		Resource addisonRd = new ResourcePropertyExisting("Addison Road");
		addisonRd.setStartYearMonth(YearMonth.of(2024, 1));

		// Addison Road BALANCE_OPENING_FIXED.
		ResourceParamIntegerPositive addisonRdOpeningAssetValue = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		addisonRd.addResourceParam(addisonRdOpeningAssetValue);
		ResourceParamDateValueIntegerPositive addisonRdOpeningAssetVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(6000000));
		addisonRdOpeningAssetValue.addResourceParamDateValue(addisonRdOpeningAssetVal);		

		// Addison Road PROPERTY_STATUS.
		ResourceParamString addisonRdStatus = new ResourceParamString(ResourceParamNameEnum.PROPERTY_STATUS);
		addisonRdStatus.setUserAbleToCreateNewDateValue(true);
		addisonRd.addResourceParam(addisonRdStatus);
		ResourceParamDateValueString addisonRdStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourcePropertyExisting.PROPERTY_STATUS_RENTED);
		addisonRdStatus.addResourceParamDateValue(addisonRdStatusVal);		

		ResourceParamDateValueString addisonRdStatus2Val = new ResourceParamDateValueString(YearMonth.of(2026, 3), false, ResourcePropertyExisting.PROPERTY_STATUS_LIVING_IN);
		addisonRdStatus.addResourceParamDateValue(addisonRdStatus2Val);		

		// Addison Road EXPENSE_ASSET_OWNERSHIP.
		Cashflow addisonRdAssetOwnership = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, CashflowFrequency.MONTHLY, Boolean.TRUE);
		addisonRd.addCashflow(addisonRdAssetOwnership);
		CashflowDateRangeValue addisonRdAssetOwnershipDRV = new CashflowDateRangeValue(addisonRdAssetOwnership.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-1300));
		addisonRdAssetOwnership.addCashflowDateRangeValue(addisonRdAssetOwnershipDRV);

		// Addison Road EXPENSE_ASSET_OWNERSHIP CPT.
		Cashflow addisonRdAssetOwnershipCPT = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, "CPT", CashflowFrequency.ANNUALLY, Boolean.FALSE);
		addisonRd.addCashflow(addisonRdAssetOwnershipCPT);
		CashflowDateRangeValue addisonRdAssetOwnershipCPTDRV = new CashflowDateRangeValue(addisonRdAssetOwnershipCPT.getCategory().getType(), YearMonth.of(2024, 10), YearMonth.of(2031, 11), Integer.valueOf(-10250));
		addisonRdAssetOwnershipCPT.addCashflowDateRangeValue(addisonRdAssetOwnershipCPTDRV);

		// Addison Road EXPENSE_ASSET_OWNERSHIP RENOS.
		Cashflow addisonRdAssetOwnershipRenos = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, "Renos", CashflowFrequency.ONE_OFF, Boolean.FALSE);
		addisonRd.addCashflow(addisonRdAssetOwnershipRenos);
		CashflowDateRangeValue addisonRdAssetOwnershipRenosDRV = new CashflowDateRangeValue(addisonRdAssetOwnershipRenos.getCategory().getType(), YearMonth.of(2030, 4), Integer.valueOf(-150000));
		addisonRdAssetOwnershipRenos.addCashflowDateRangeValue(addisonRdAssetOwnershipRenosDRV);

		// Addison Road HOUSING_MARKET_GROWTH_RATE.
//		ResourceParamBigDecimal addisonRdGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE);
//		addisonRdGrowthRate.setUserAbleToCreateNewDateValue(true);
//		addisonRd.addResourceParam(addisonRdGrowthRate);
//		ResourceParamDateValueBigDecimal addisonRdGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("6.0"));
//		addisonRdGrowthRate.addResourceParamDateValue(addisonRdGrowthRateVal);

		// Addison Road EXPENSE_RENTAL_PROPERTY.
		Cashflow addisonRdRentalExpense = new Cashflow(CashflowCategory.EXPENSE_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
		addisonRd.addCashflow(addisonRdRentalExpense);
		CashflowDateRangeValue addisonRdRentalExpenseDRV = new CashflowDateRangeValue(addisonRdRentalExpense.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(-600));
		addisonRdRentalExpense.addCashflowDateRangeValue(addisonRdRentalExpenseDRV);

		// Addison Road INCOME_RENTAL_PROPERTY.
		Cashflow addisonRdRentalIncome = new Cashflow(CashflowCategory.INCOME_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
		addisonRd.addCashflow(addisonRdRentalIncome);
		CashflowDateRangeValue addisonRdRentalIncomeDRV = new CashflowDateRangeValue(addisonRdRentalIncome.getCategory().getType(), YearMonth.of(2024, 1), Integer.valueOf(10768));
		addisonRdRentalIncome.addCashflowDateRangeValue(addisonRdRentalIncomeDRV);

		// Addison Road EXPENSE_ASSET_SALE_COMMISSION.
		ResourceParamBigDecimal addisonRdSaleCommission = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_ASSET_SALE_COMMISSION);
		addisonRd.addResourceParam(addisonRdSaleCommission);
		ResourceParamDateValueBigDecimal addisonRdSaleCommissionVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("1.5"));
		addisonRdSaleCommission.addResourceParamDateValue(addisonRdSaleCommissionVal);

		// Addison Road EXPENSE_ASSET_SALE_FIXED.
		ResourceParamIntegerPositive addisonRdSaleFixed = new ResourceParamIntegerPositive(ResourceParamNameEnum.PROPERTY_ASSET_SALE_FIXED);
		addisonRd.addResourceParam(addisonRdSaleFixed);
		ResourceParamDateValueIntegerPositive addisonRdSaleFixedVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(10000));
		addisonRdSaleFixed.addResourceParamDateValue(addisonRdSaleFixedVal);

		scenario.addResource(addisonRd);

		// ------------------------
		// Addison Road Mortgage 1.
		// ------------------------
		
		Resource addisonRdMortgage = new ResourceMortgage("Addison Road Variable");
		addisonRdMortgage.setStartYearMonth(YearMonth.of(2024, 1));
		
		addisonRd.addChild(addisonRdMortgage);

		// Addison Road Mortgage BALANCE_OPENING_FIXED.
		ResourceParamIntegerNegative addisonRdMortgageOpeningAssetValue = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		addisonRdMortgage.addResourceParam(addisonRdMortgageOpeningAssetValue);
		ResourceParamDateValueIntegerNegative addisonRdMortgageOpeningAssetVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-1347025));
		addisonRdMortgageOpeningAssetValue.addResourceParamDateValue(addisonRdMortgageOpeningAssetVal);		

		// Addison Road Mortgage MORTGAGE_INTEREST_RATE.
		ResourceParamBigDecimal addisonRdInterestRate = new ResourceParamBigDecimal(ResourceParamNameEnum.MORTGAGE_INTEREST_RATE);
		addisonRdInterestRate.setUserAbleToCreateNewDateValue(true);
		addisonRdMortgage.addResourceParam(addisonRdInterestRate);
		ResourceParamDateValueBigDecimal addisonRdInterestRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal(6.79));
		addisonRdInterestRate.addResourceParamDateValue(addisonRdInterestRateVal);		

		// Addison Road Mortgage MORTGAGE_REMAINING_MONTHS.
		ResourceParamIntegerPositive addisonRdRemainingMonths = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS);
		addisonRdMortgage.addResourceParam(addisonRdRemainingMonths);
		ResourceParamDateValueIntegerPositive addisonRdRemainingMonthsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(9));
		addisonRdRemainingMonths.addResourceParamDateValue(addisonRdRemainingMonthsVal);

		// Addison Road Mortgage MORTGAGE_REMAINING_YEARS.
		ResourceParamIntegerPositive addisonRdRemainingYears = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS);
		addisonRdMortgage.addResourceParam(addisonRdRemainingYears);
		ResourceParamDateValueIntegerPositive addisonRdRemainingYearsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(21));
		addisonRdRemainingYears.addResourceParamDateValue(addisonRdRemainingYearsVal);
		
		// Addison Road Mortgage MORTGAGE_REPAYMENT_TYPE.
		ResourceParamString addisonRdMortgageStatus = new ResourceParamString(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE);
		addisonRdMortgageStatus.setUserAbleToCreateNewDateValue(true);
		addisonRdMortgage.addResourceParam(addisonRdMortgageStatus);
		ResourceParamDateValueString addisonRdMortgageStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourceMortgage.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST);
		addisonRdMortgageStatus.addResourceParamDateValue(addisonRdMortgageStatusVal);		

		scenario.addResource(addisonRdMortgage);

		// -------------------------------
		// Addison Road Mortgage Offset.
		// -------------------------------
		
		Resource addisonRdMortgageOffset = new ResourceMortgageOffset("StG Personal");
		addisonRdMortgageOffset.setStartYearMonth(YearMonth.of(2024, 1));
		
		addisonRdMortgage.addChild(addisonRdMortgageOffset);

		// Addison Road Mortgage Offset BALANCE_OPENING_LIQUID.
		ResourceParamIntegerPositive addisonRdMortgageOffsetBalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		addisonRdMortgageOffset.addResourceParam(addisonRdMortgageOffsetBalanceOpeningLiquid);
		ResourceParamDateValueIntegerPositive addisonRdMortgageOffsetBalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(910452));
		addisonRdMortgageOffsetBalanceOpeningLiquid.addResourceParamDateValue(addisonRdMortgageOffsetBalanceOpeningLiquidVal);		

		scenario.addResource(addisonRdMortgageOffset);
		
		// ------------------------
		// Addison Road Mortgage 2.
		// ------------------------
		
		Resource addisonRd2Mortgage = new ResourceMortgage("Addison Road Fixed");
		addisonRd2Mortgage.setStartYearMonth(YearMonth.of(2024, 1));
		
		addisonRd.addChild(addisonRd2Mortgage);

		// Addison Road Mortgage BALANCE_OPENING_FIXED.
		ResourceParamIntegerNegative addisonRd2MortgageOpeningAssetValue = new ResourceParamIntegerNegative(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
		addisonRd2Mortgage.addResourceParam(addisonRd2MortgageOpeningAssetValue);
		ResourceParamDateValueIntegerNegative addisonRd2MortgageOpeningAssetVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 1), false, Integer.valueOf(-722118));
		addisonRd2MortgageOpeningAssetValue.addResourceParamDateValue(addisonRd2MortgageOpeningAssetVal);		

		// Addison Road Mortgage MORTGAGE_INTEREST_RATE.
		ResourceParamBigDecimal addisonRd2InterestRate = new ResourceParamBigDecimal(ResourceParamNameEnum.MORTGAGE_INTEREST_RATE);
		addisonRd2InterestRate.setUserAbleToCreateNewDateValue(true);
		addisonRd2Mortgage.addResourceParam(addisonRd2InterestRate);
		ResourceParamDateValueBigDecimal addisonRd2InterestRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal(6.79));
		addisonRd2InterestRate.addResourceParamDateValue(addisonRd2InterestRateVal);		

		// Addison Road Mortgage MORTGAGE_REMAINING_MONTHS.
		ResourceParamIntegerPositive addisonRd2RemainingMonths = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS);
		addisonRd2Mortgage.addResourceParam(addisonRd2RemainingMonths);
		ResourceParamDateValueIntegerPositive addisonRd2RemainingMonthsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(9));
		addisonRd2RemainingMonths.addResourceParamDateValue(addisonRd2RemainingMonthsVal);

		// Addison Road Mortgage MORTGAGE_REMAINING_YEARS.
		ResourceParamIntegerPositive addisonRd2RemainingYears = new ResourceParamIntegerPositive(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS);
		addisonRd2Mortgage.addResourceParam(addisonRd2RemainingYears);
		ResourceParamDateValueIntegerPositive addisonRd2RemainingYearsVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(21));
		addisonRd2RemainingYears.addResourceParamDateValue(addisonRd2RemainingYearsVal);
		
		// Addison Road Mortgage MORTGAGE_REPAYMENT_TYPE.
		ResourceParamString addisonRd2MortgageStatus = new ResourceParamString(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE);
		addisonRd2MortgageStatus.setUserAbleToCreateNewDateValue(true);
		addisonRd2Mortgage.addResourceParam(addisonRd2MortgageStatus);
		ResourceParamDateValueString addisonRd2MortgageStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourceMortgage.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST);
		addisonRd2MortgageStatus.addResourceParamDateValue(addisonRd2MortgageStatusVal);		

		scenario.addResource(addisonRd2Mortgage);

		// -------------------------------
		// Addison Road Mortgage Offset.
		// -------------------------------
		
		Resource addisonRd2MortgageOffset = new ResourceMortgageOffset("StG Personal 2");
		addisonRd2MortgageOffset.setStartYearMonth(YearMonth.of(2024, 1));
		
		addisonRd2Mortgage.addChild(addisonRd2MortgageOffset);

		// Addison Road Mortgage Offset BALANCE_OPENING_LIQUID.
		ResourceParamIntegerPositive addisonRd2MortgageOffsetBalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		addisonRd2MortgageOffset.addResourceParam(addisonRd2MortgageOffsetBalanceOpeningLiquid);
		ResourceParamDateValueIntegerPositive addisonRd2MortgageOffsetBalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(0));
		addisonRd2MortgageOffsetBalanceOpeningLiquid.addResourceParamDateValue(addisonRd2MortgageOffsetBalanceOpeningLiquidVal);		

		scenario.addResource(addisonRd2MortgageOffset);
		
		// -------
		// Shares.
		// -------
		
		Resource shares = new ResourceShares("Shares");
		shares.setStartYearMonth(YearMonth.of(2024, 1));
		
		// Shares BALANCE_OPENING_LIQUID.
		ResourceParamIntegerPositive sharesBalanceOpeningLiquid = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
		shares.addResourceParam(sharesBalanceOpeningLiquid);
		ResourceParamDateValueIntegerPositive sharesBalanceOpeningLiquidVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 1), false, Integer.valueOf(6156));
		sharesBalanceOpeningLiquid.addResourceParamDateValue(sharesBalanceOpeningLiquidVal);		

		// Shares SHARES_SHARE_MARKET_GROWTH_RATE.
//		ResourceParamBigDecimal sharesShareMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SHARES_SHARE_MARKET_GROWTH_RATE);
//		sharesShareMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
//		shares.addResourceParam(sharesShareMarketGrowthRate);
//		ResourceParamDateValueBigDecimal sharesShareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("4.0"));
//		sharesShareMarketGrowthRate.addResourceParamDateValue(sharesShareMarketGrowthRateVal);

		// Shares SHARES_DIVIDEND_YIELD.
		ResourceParamBigDecimal sharesDividendYield = new ResourceParamBigDecimal(ResourceParamNameEnum.SHARES_DIVIDEND_YIELD);
		sharesDividendYield.setUserAbleToCreateNewDateValue(true);
		shares.addResourceParam(sharesDividendYield);
		ResourceParamDateValueBigDecimal sharesDividendYieldVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 1), false, new BigDecimal("3.0"));
		sharesDividendYield.addResourceParamDateValue(sharesDividendYieldVal);

		// Shares SHARES_DIVIDEND_PROCESSING.
		ResourceParamString sharesDividendProcessing = new ResourceParamString(ResourceParamNameEnum.SHARES_DIVIDEND_PROCESSING);
		sharesDividendProcessing.setUserAbleToCreateNewDateValue(true);
		shares.addResourceParam(sharesDividendProcessing);
		ResourceParamDateValueString sharesDividendProcessingVal = new ResourceParamDateValueString(YearMonth.of(2024, 1), false, ResourceShares.SHARES_DIVIDEND_PROCESSING_REINVEST);
		sharesDividendProcessing.addResourceParamDateValue(sharesDividendProcessingVal);		

		ResourceParamDateValueString sharesDividendProcessing2Val = new ResourceParamDateValueString(YearMonth.of(2026, 1), true, ResourceShares.SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME);
		sharesDividendProcessing.addResourceParamDateValue(sharesDividendProcessing2Val);		

		scenario.addResource(shares);
		
		// --------------------
		// Investment property.
		// --------------------
		
//		Resource investmentProperty = new ResourcePropertyNew("Investment Property");
//		investmentProperty.setStartYearMonth(YearMonth.of(2024, 3));
//
//		// Investment Property BALANCE_OPENING_FIXED.
//		ResourceParamIntegerPositive investmentPropertyOpeningAssetValue = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
//		investmentProperty.addResourceParam(investmentPropertyOpeningAssetValue);
//		ResourceParamDateValueIntegerPositive investmentPropertyOpeningAssetVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 3), false, Integer.valueOf(1000000));
//		investmentPropertyOpeningAssetValue.addResourceParamDateValue(investmentPropertyOpeningAssetVal);		
//
//		// Investment Property PROPERTY_ASSET_PURCHASE_DEPOSIT.
//		ResourceParamIntegerNegative investmentPropertyDeposit = new ResourceParamIntegerNegative(ResourceParamNameEnum.PROPERTY_ASSET_PURCHASE_DEPOSIT);
//		investmentProperty.addResourceParam(investmentPropertyDeposit);
//		ResourceParamDateValueIntegerNegative investmentPropertyDepositVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 3), false, Integer.valueOf(200000));
//		investmentPropertyDeposit.addResourceParamDateValue(investmentPropertyDepositVal);		
//
//		// Investment Property PROPERTY_ASSET_PURCHASE_ADDITIONAL_COSTS.
//		ResourceParamIntegerNegative investmentPropertyAdditionalCosts = new ResourceParamIntegerNegative(ResourceParamNameEnum.PROPERTY_ASSET_PURCHASE_ADDITIONAL_COSTS);
//		investmentProperty.addResourceParam(investmentPropertyAdditionalCosts);
//		ResourceParamDateValueIntegerNegative investmentPropertyAdditionalCostsVal = new ResourceParamDateValueIntegerNegative(YearMonth.of(2024, 3), false, Integer.valueOf(53425));
//		investmentPropertyAdditionalCosts.addResourceParamDateValue(investmentPropertyAdditionalCostsVal);
//
//		// Investment Property PROPERTY_STATUS.
//		ResourceParamString investmentPropertyStatus = new ResourceParamString(ResourceParamNameEnum.PROPERTY_STATUS);
//		investmentPropertyStatus.setUserAbleToCreateNewDateValue(true);
//		investmentProperty.addResourceParam(investmentPropertyStatus);
//		ResourceParamDateValueString investmentPropertyStatusVal = new ResourceParamDateValueString(YearMonth.of(2024, 3), false, ResourcePropertyExisting.PROPERTY_STATUS_RENTED);
//		investmentPropertyStatus.addResourceParamDateValue(investmentPropertyStatusVal);		
//
//		// Investment Property EXPENSE_ASSET_OWNERSHIP.
//		Cashflow investmentPropertyAssetOwnership = new Cashflow(CashflowCategory.EXPENSE_ASSET_OWNERSHIP, CashflowFrequency.MONTHLY, Boolean.TRUE);
//		investmentProperty.addCashflow(investmentPropertyAssetOwnership);
//		CashflowDateRangeValue investmentPropertyAssetOwnershipDRV = new CashflowDateRangeValue(investmentPropertyAssetOwnership.getCategory().getType(), YearMonth.of(2024, 3), Integer.valueOf(-900));
//		investmentPropertyAssetOwnership.addCashflowDateRangeValue(investmentPropertyAssetOwnershipDRV);
//
//		// Investment Property HOUSING_MARKET_GROWTH_RATE.
////		ResourceParamBigDecimal investmentPropertyGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE);
////		investmentPropertyGrowthRate.setUserAbleToCreateNewDateValue(true);
////		investmentProperty.addResourceParam(investmentPropertyGrowthRate);
////		ResourceParamDateValueBigDecimal investmentPropertyGrowthRateVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 3), false, new BigDecimal("6.0"));
////		investmentPropertyGrowthRate.addResourceParamDateValue(investmentPropertyGrowthRateVal);
//
//		// Investment Property EXPENSE_RENTAL_PROPERTY.
//		Cashflow investmentPropertyRentalExpense = new Cashflow(CashflowCategory.EXPENSE_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
//		investmentProperty.addCashflow(investmentPropertyRentalExpense);
//		CashflowDateRangeValue investmentPropertyRentalExpenseDRV = new CashflowDateRangeValue(investmentPropertyRentalExpense.getCategory().getType(), YearMonth.of(2024, 3), Integer.valueOf(-670));
//		investmentPropertyRentalExpense.addCashflowDateRangeValue(investmentPropertyRentalExpenseDRV);
//
//		// Investment Property INCOME_RENTAL_PROPERTY.
//		Cashflow investmentPropertyRentalIncome = new Cashflow(CashflowCategory.INCOME_RENTAL_PROPERTY, CashflowFrequency.MONTHLY, Boolean.TRUE);
//		investmentProperty.addCashflow(investmentPropertyRentalIncome);
//		CashflowDateRangeValue investmentPropertyRentalIncomeDRV = new CashflowDateRangeValue(investmentPropertyRentalIncome.getCategory().getType(), YearMonth.of(2024, 3), Integer.valueOf(4200));
//		investmentPropertyRentalIncome.addCashflowDateRangeValue(investmentPropertyRentalIncomeDRV);
//
//		// Investment Property EXPENSE_ASSET_SALE_COMMISSION.
//		ResourceParamBigDecimal investmentPropertySaleCommission = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_ASSET_SALE_COMMISSION);
//		investmentProperty.addResourceParam(investmentPropertySaleCommission);
//		ResourceParamDateValueBigDecimal investmentPropertySaleCommissionVal = new ResourceParamDateValueBigDecimal(YearMonth.of(2024, 3), false, new BigDecimal("1.5"));
//		investmentPropertySaleCommission.addResourceParamDateValue(investmentPropertySaleCommissionVal);
//
//		// Investment Property EXPENSE_ASSET_SALE_FIXED.
//		ResourceParamIntegerPositive investmentPropertySaleFixed = new ResourceParamIntegerPositive(ResourceParamNameEnum.PROPERTY_ASSET_SALE_FIXED);
//		investmentProperty.addResourceParam(investmentPropertySaleFixed);
//		ResourceParamDateValueIntegerPositive investmentPropertySaleFixedVal = new ResourceParamDateValueIntegerPositive(YearMonth.of(2024, 3), false, Integer.valueOf(10000));
//		investmentPropertySaleFixed.addResourceParamDateValue(investmentPropertySaleFixedVal);
//
//		scenario.addResource(investmentProperty);

		return scenario;
	}
	*/
}
