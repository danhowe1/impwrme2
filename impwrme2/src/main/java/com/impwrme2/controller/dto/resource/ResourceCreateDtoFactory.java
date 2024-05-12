package com.impwrme2.controller.dto.resource;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamType;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.utils.YearMonthUtils;

public class ResourceCreateDtoFactory {

	public static ResourceCreateDto createResourceCreateDto(Scenario scenario, String resourceType) {
		if (ResourceType.valueOf(resourceType).equals(ResourceType.CURRENT_ACCOUNT)) {
//			return createResourceCurrentAccount(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.CREDIT_CARD)) {
			return createResourceCreditCard(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.MORTGAGE_EXISTING)) {
//			return createResourceMortgageExisting(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.MORTGAGE_NEW)) {
//			return createResourceMortgageNew(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.MORTGAGE_OFFSET_ACCOUNT)) {
//			return createResourceMortgageOffsetAccount(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PERSON)) {
			return createResourcePerson(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PROPERTY_EXISTING)) {
//			return createResourcePropertyExisting(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PROPERTY_NEW)) {
//			return createResourcePropertyNew(scenario);
		}  else if (ResourceType.valueOf(resourceType).equals(ResourceType.SAVINGS_ACCOUNT)) {
//			return createResourceSavingsAccount(scenario);
		}  else if (ResourceType.valueOf(resourceType).equals(ResourceType.SHARES)) {
//			return createResourceShares(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.SUPERANNUATION)) {
//			return createResourceSuperannuation(scenario);
		}
		throw new IllegalStateException("Unknown resource type " + resourceType + ".");
	}

	public static ResourceCreateDto createResourceCreditCard(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.CREDIT_CARD.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.CREDIT_CARD));

		ResourceCreateResourceParamWithValueDto liquidBalanceMin = new ResourceCreateResourceParamWithValueDto();
		liquidBalanceMin.setName(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN.getValue());
		liquidBalanceMin.setResourceParamType(ResourceParamType.INTEGER_NEGATIVE.getValue());
		liquidBalanceMin.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(liquidBalanceMin);

		ResourceCreateResourceParamWithValueDto openingLiquidBalance = new ResourceCreateResourceParamWithValueDto();
		openingLiquidBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_LIQUID.getValue());
		openingLiquidBalance.setResourceParamType(ResourceParamType.INTEGER_NEGATIVE.getValue());
		openingLiquidBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingLiquidBalance);
		
		ResourceCreateResourceParamWithValueDto interestRate = new ResourceCreateResourceParamWithValueDto();
		interestRate.setName(ResourceParamNameEnum.CREDIT_CARD_INTEREST_RATE.getValue());
		interestRate.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		interestRate.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(interestRate);

		return resourceCreateDto;
	}	

	public static ResourceCreateDto createResourcePerson(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.PERSON.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.PERSON));

		ResourceCreateResourceParamWithValueDto birthYearMonth = new ResourceCreateResourceParamWithValueDto();
		birthYearMonth.setName(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH.getValue());
		birthYearMonth.setResourceParamType(ResourceParamType.YEAR_MONTH.getValue());
		birthYearMonth.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(birthYearMonth);

		ResourceCreateResourceParamWithValueDto departureAge = new ResourceCreateResourceParamWithValueDto();
		departureAge.setName(ResourceParamNameEnum.PERSON_DEPARTURE_AGE.getValue());
		departureAge.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		departureAge.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(departureAge);
		
		ResourceCreateResourceParamWithValueDto retirementAge = new ResourceCreateResourceParamWithValueDto();
		retirementAge.setName(ResourceParamNameEnum.PERSON_RETIREMENT_AGE.getValue());
		retirementAge.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		retirementAge.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(retirementAge);

		ResourceCreateCashflowWithValueDto income = new ResourceCreateCashflowWithValueDto();
		income.setCategory(CashflowCategory.INCOME_EMPLOYMENT.getValue());
		income.setFrequency(CashflowFrequency.MONTHLY.getValue());
		income.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(income);
	
		ResourceCreateCashflowWithValueDto livingEssential = new ResourceCreateCashflowWithValueDto();
		livingEssential.setCategory(CashflowCategory.EXPENSE_LIVING_ESSENTIAL.getValue());
		livingEssential.setFrequency(CashflowFrequency.MONTHLY.getValue());
		livingEssential.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(livingEssential);
	
		ResourceCreateCashflowWithValueDto livingSplurge = new ResourceCreateCashflowWithValueDto();
		livingSplurge.setCategory(CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL.getValue());
		livingSplurge.setFrequency(CashflowFrequency.MONTHLY.getValue());
		livingSplurge.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(livingSplurge);
	
		return resourceCreateDto;
	}	
}
