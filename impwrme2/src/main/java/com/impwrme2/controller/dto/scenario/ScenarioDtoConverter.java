package com.impwrme2.controller.dto.scenario;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourcePersonAdult;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParam.ResourceParamYearMonth;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueYearMonth;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ScenarioDtoConverter {

	@Autowired
	MessageSource messageSource;
	
	public Scenario scenarioCreateMinDtoToEntity(ScenarioCreateMinDto scenarioCreateMinDto, String userId) {
		ResourceScenario resourceScenario = createResourceScenarioDefault(scenarioCreateMinDto.getName(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()));
		Scenario scenario = new Scenario(resourceScenario, userId);
		scenario.addResource(createResourceHouseholdDefault(scenarioCreateMinDto.getPersonName()));	
		scenario.addResource(createResourcePersonAdultDefault(scenarioCreateMinDto));	
		return scenario;
	}

	private ResourceScenario createResourceScenarioDefault(String scenarioName, YearMonth startYearMonth) {
		ResourceScenario resourceScenario = new ResourceScenario(scenarioName);
		resourceScenario.setStartYearMonth(startYearMonth);

		ResourceParamBigDecimal cpi = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_CPI);
		cpi.setUserAbleToCreateNewDateValue(true);
		resourceScenario.addResourceParam(cpi);
		String cpiDefault = messageSource.getMessage("msg.class.scenario.cpi.default", null, LocaleContextHolder.getLocale());
		ResourceParamDateValueBigDecimal cpiVal = new ResourceParamDateValueBigDecimal(startYearMonth, false, new BigDecimal(cpiDefault));
		cpi.addResourceParamDateValue(cpiVal);

		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_SHARE_MARKET_GROWTH_RATE);
		shareMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
		resourceScenario.addResourceParam(shareMarketGrowthRate);
		String shareMarketGrowthRateDefault = messageSource.getMessage("msg.class.scenario.shareMarketGrowthRate.default", null, LocaleContextHolder.getLocale());
		ResourceParamDateValueBigDecimal shareMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(startYearMonth, false, new BigDecimal(shareMarketGrowthRateDefault));
		shareMarketGrowthRate.addResourceParamDateValue(shareMarketGrowthRateVal);

		ResourceParamBigDecimal housingMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SCENARIO_HOUSING_MARKET_GROWTH_RATE);
		housingMarketGrowthRate.setUserAbleToCreateNewDateValue(true);
		resourceScenario.addResourceParam(housingMarketGrowthRate);
		String housingMarketGrowthRateDefault = messageSource.getMessage("msg.class.scenario.housingMarketGrowthRate.default", null, LocaleContextHolder.getLocale());
		ResourceParamDateValueBigDecimal housingMarketGrowthRateVal = new ResourceParamDateValueBigDecimal(startYearMonth, false, new BigDecimal(housingMarketGrowthRateDefault));
		housingMarketGrowthRate.addResourceParamDateValue(housingMarketGrowthRateVal);

		return resourceScenario;
	}
	
	private ResourceHousehold createResourceHouseholdDefault(String personName) {
		String householdName = messageSource.getMessage("msg.class.resourceHousehold.name.default", new String[] {personName }, LocaleContextHolder.getLocale());
		ResourceHousehold householdResource = new ResourceHousehold(householdName);
		householdResource.setStartYearMonth(YearMonth.of(2024, 1));
		return householdResource;
	}
	
	private ResourcePersonAdult createResourcePersonAdultDefault(ScenarioCreateMinDto scenarioCreateMinDto) {
		ResourcePersonAdult person = new ResourcePersonAdult(scenarioCreateMinDto.getPersonName());
		person.setStartYearMonth(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()));

		ResourceParamYearMonth birthYearMonth = new ResourceParamYearMonth(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH);
		person.addResourceParam(birthYearMonth);
		ResourceParamDateValueYearMonth birthYearMonthVal = new ResourceParamDateValueYearMonth(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()), false, YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getBirthYearMonth()));
		birthYearMonth.addResourceParamDateValue(birthYearMonthVal);

		ResourceParamIntegerPositive departureAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_DEPARTURE_AGE);
		person.addResourceParam(departureAge);
		String departureAgeDefault = messageSource.getMessage("msg.class.resourcePersonAdult.departureAge.default", null, LocaleContextHolder.getLocale());
		ResourceParamDateValueIntegerPositive deprtureAgeVal = new ResourceParamDateValueIntegerPositive(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()), false, Integer.valueOf(departureAgeDefault));
		departureAge.addResourceParamDateValue(deprtureAgeVal);

		ResourceParamIntegerPositive retirementAge = new ResourceParamIntegerPositive(ResourceParamNameEnum.PERSON_ADULT_RETIREMENT_AGE);
		person.addResourceParam(retirementAge);
		ResourceParamDateValueIntegerPositive retirementAgeVal = new ResourceParamDateValueIntegerPositive(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()), false, Integer.valueOf(scenarioCreateMinDto.getRetirementAge()));
		retirementAge.addResourceParamDateValue(retirementAgeVal);
		
		if (null != scenarioCreateMinDto.getMonthlyEmploymentIncome() && scenarioCreateMinDto.getMonthlyEmploymentIncome().intValue() != 0) {
			Cashflow employmentIncome = new Cashflow(CashflowCategory.INCOME_EMPLOYMENT, CashflowFrequency.MONTHLY, Boolean.TRUE);
			person.addCashflow(employmentIncome);
			CashflowDateRangeValue amandaEmploymentIncomeDRV = new CashflowDateRangeValue(employmentIncome.getCategory().getType(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()), Integer.valueOf(scenarioCreateMinDto.getMonthlyEmploymentIncome()));
			employmentIncome.addCashflowDateRangeValue(amandaEmploymentIncomeDRV);
		}
		
		Cashflow livingExpense = new Cashflow(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowFrequency.MONTHLY, Boolean.TRUE);
		person.addCashflow(livingExpense);		
		CashflowDateRangeValue amandaLivingExpenseDRV = new CashflowDateRangeValue(livingExpense.getCategory().getType(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(scenarioCreateMinDto.getStartYearMonth()), Integer.valueOf(scenarioCreateMinDto.getMonthlyLivingExpenses()));
		livingExpense.addCashflowDateRangeValue(amandaLivingExpenseDRV);

		return person;
	}
}
