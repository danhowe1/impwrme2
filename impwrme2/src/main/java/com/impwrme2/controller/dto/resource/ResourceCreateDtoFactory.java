package com.impwrme2.controller.dto.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.resourceParamDateValue.ValueMessagePairDto;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParamType;
import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceCreateDtoFactory {

	@Autowired
	MessageSource messageSource;
	
	public ResourceCreateDto createResourceCreateDto(Scenario scenario, String resourceType) {
		if (ResourceType.valueOf(resourceType).equals(ResourceType.CURRENT_ACCOUNT)) {
			return createResourceCurrentAccount(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.CREDIT_CARD)) {
			return createResourceCreditCard(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.MORTGAGE)) {
			return createResourceMortgage(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.MORTGAGE_OFFSET_ACCOUNT)) {
			return createResourceMortgageOffsetAccount(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PERSON)) {
			return createResourcePerson(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PROPERTY_EXISTING)) {
			return createResourcePropertyExisting(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.PROPERTY_NEW)) {
			return createResourcePropertyNew(scenario);
		}  else if (ResourceType.valueOf(resourceType).equals(ResourceType.SAVINGS_ACCOUNT)) {
			return createResourceSavingsAccount(scenario);
		}  else if (ResourceType.valueOf(resourceType).equals(ResourceType.SHARES)) {
			return createResourceShares(scenario);
		} else if (ResourceType.valueOf(resourceType).equals(ResourceType.SUPERANNUATION)) {
			return createResourceSuperannuation(scenario);
		}
		throw new IllegalStateException("Unknown resource type " + resourceType + ".");
	}

	public ResourceCreateDto createResourceCreditCard(Scenario scenario) {
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
		interestRate.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(interestRate);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourceCurrentAccount(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.CURRENT_ACCOUNT.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.CURRENT_ACCOUNT));

		ResourceCreateResourceParamWithValueDto openingLiquidBalance = new ResourceCreateResourceParamWithValueDto();
		openingLiquidBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_LIQUID.getValue());
		openingLiquidBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingLiquidBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingLiquidBalance);
		
		ResourceCreateResourceParamWithValueDto interestRate = new ResourceCreateResourceParamWithValueDto();
		interestRate.setName(ResourceParamNameEnum.CURRENT_ACCOUNT_INTEREST_RATE.getValue());
		interestRate.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		interestRate.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(interestRate);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourcePerson(Scenario scenario) {
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

	public ResourceCreateDto createResourceMortgage(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.MORTGAGE.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.MORTGAGE));

		ResourceCreateResourceParamWithValueDto openingFixedBalance = new ResourceCreateResourceParamWithValueDto();
		openingFixedBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_FIXED.getValue());
		openingFixedBalance.setResourceParamType(ResourceParamType.INTEGER_NEGATIVE.getValue());
		openingFixedBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingFixedBalance);
		
		ResourceCreateResourceParamWithValueDto interestRate = new ResourceCreateResourceParamWithValueDto();
		interestRate.setName(ResourceParamNameEnum.MORTGAGE_INTEREST_RATE.getValue());
		interestRate.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		interestRate.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(interestRate);

		ResourceCreateResourceParamWithValueDto remainingYears = new ResourceCreateResourceParamWithValueDto();
		remainingYears.setName(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS.getValue());
		remainingYears.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		remainingYears.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(remainingYears);

		ResourceCreateResourceParamWithValueDto remainingMonths = new ResourceCreateResourceParamWithValueDto();
		remainingMonths.setName(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS.getValue());
		remainingMonths.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		remainingMonths.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(remainingMonths);

		ResourceCreateResourceParamWithValueDto repaymentType = new ResourceCreateResourceParamWithValueDto();
		repaymentType.setName(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE.getValue());
		repaymentType.setResourceParamType(ResourceParamType.STRING.getValue());
		repaymentType.setUserAbleToCreateNewDateValue(true);
		repaymentType.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST));
		repaymentType.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.MORTGAGE_REPAYMENT_INTEREST_ONLY));
		resourceCreateDto.addResourceParamDto(repaymentType);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourceMortgageOffsetAccount(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.MORTGAGE_OFFSET_ACCOUNT.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.MORTGAGE_OFFSET_ACCOUNT));

		ResourceCreateResourceParamWithValueDto openingLiquidBalance = new ResourceCreateResourceParamWithValueDto();
		openingLiquidBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_LIQUID.getValue());
		openingLiquidBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingLiquidBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingLiquidBalance);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourcePropertyExisting(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.PROPERTY_EXISTING.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.PROPERTY_EXISTING));

		ResourceCreateResourceParamWithValueDto openingFixedBalance = new ResourceCreateResourceParamWithValueDto();
		openingFixedBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_FIXED.getValue());
		openingFixedBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingFixedBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingFixedBalance);

		ResourceCreateResourceParamWithValueDto propertyStatus = new ResourceCreateResourceParamWithValueDto();
		propertyStatus.setName(ResourceParamNameEnum.PROPERTY_STATUS.getValue());
		propertyStatus.setResourceParamType(ResourceParamType.STRING.getValue());
		propertyStatus.setUserAbleToCreateNewDateValue(true);
		propertyStatus.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.PROPERTY_STATUS_LIVING_IN));
		propertyStatus.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.PROPERTY_STATUS_RENTED));
		resourceCreateDto.addResourceParamDto(propertyStatus);

		ResourceCreateResourceParamWithValueDto saleCommission = new ResourceCreateResourceParamWithValueDto();
		saleCommission.setName(ResourceParamNameEnum.PROPERTY_ASSET_SALE_COMMISSION.getValue());
		saleCommission.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		saleCommission.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(saleCommission);

		ResourceCreateResourceParamWithValueDto saleFixed = new ResourceCreateResourceParamWithValueDto();
		saleFixed.setName(ResourceParamNameEnum.PROPERTY_ASSET_SALE_FIXED.getValue());
		saleFixed.setResourceParamType(ResourceParamType.INTEGER_NEGATIVE.getValue());
		saleFixed.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(saleFixed);

		ResourceCreateCashflowWithValueDto income = new ResourceCreateCashflowWithValueDto();
		income.setCategory(CashflowCategory.INCOME_RENTAL_PROPERTY.getValue());
		income.setFrequency(CashflowFrequency.MONTHLY.getValue());
		income.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(income);
	
		ResourceCreateCashflowWithValueDto expenseAssetOwnership = new ResourceCreateCashflowWithValueDto();
		expenseAssetOwnership.setCategory(CashflowCategory.EXPENSE_ASSET_OWNERSHIP.getValue());
		expenseAssetOwnership.setFrequency(CashflowFrequency.MONTHLY.getValue());
		expenseAssetOwnership.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(expenseAssetOwnership);
	
		ResourceCreateCashflowWithValueDto expenseRental = new ResourceCreateCashflowWithValueDto();
		expenseRental.setCategory(CashflowCategory.EXPENSE_RENTAL_PROPERTY.getValue());
		expenseRental.setFrequency(CashflowFrequency.MONTHLY.getValue());
		expenseRental.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(expenseRental);
	
		return resourceCreateDto;
	}	
	
	public ResourceCreateDto createResourcePropertyNew(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = createResourcePropertyExisting(scenario);

		ResourceCreateCashflowWithValueDto expenseDeposit = new ResourceCreateCashflowWithValueDto();
		expenseDeposit.setCategory(CashflowCategory.EXPENSE_ASSET_PURCHASE_DEPOSIT.getValue());
		expenseDeposit.setFrequency(CashflowFrequency.ONE_OFF.getValue());
		expenseDeposit.setCpiAffected(false);
		resourceCreateDto.addCashflowDto(expenseDeposit);
	
		ResourceCreateCashflowWithValueDto expenseAdditionalPurchaseCosts = new ResourceCreateCashflowWithValueDto();
		expenseAdditionalPurchaseCosts.setCategory(CashflowCategory.EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS.getValue());
		expenseAdditionalPurchaseCosts.setFrequency(CashflowFrequency.ONE_OFF.getValue());
		expenseAdditionalPurchaseCosts.setCpiAffected(false);
		resourceCreateDto.addCashflowDto(expenseAdditionalPurchaseCosts);
	
		return resourceCreateDto;
	}
	
	public ResourceCreateDto createResourceSavingsAccount(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.SAVINGS_ACCOUNT.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.SAVINGS_ACCOUNT));

		ResourceCreateResourceParamWithValueDto openingLiquidBalance = new ResourceCreateResourceParamWithValueDto();
		openingLiquidBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_LIQUID.getValue());
		openingLiquidBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingLiquidBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingLiquidBalance);
		
		ResourceCreateResourceParamWithValueDto interestRate = new ResourceCreateResourceParamWithValueDto();
		interestRate.setName(ResourceParamNameEnum.SAVINGS_ACCOUNT_INTEREST_RATE.getValue());
		interestRate.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		interestRate.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(interestRate);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourceShares(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.SHARES.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.SHARES));

		ResourceCreateResourceParamWithValueDto openingLiquidBalance = new ResourceCreateResourceParamWithValueDto();
		openingLiquidBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_LIQUID.getValue());
		openingLiquidBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingLiquidBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingLiquidBalance);
		
		ResourceCreateResourceParamWithValueDto dividendYield = new ResourceCreateResourceParamWithValueDto();
		dividendYield.setName(ResourceParamNameEnum.SHARES_DIVIDEND_YIELD.getValue());
		dividendYield.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		dividendYield.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(dividendYield);

		ResourceCreateResourceParamWithValueDto dividendProcessing = new ResourceCreateResourceParamWithValueDto();
		dividendProcessing.setName(ResourceParamNameEnum.SHARES_DIVIDEND_PROCESSING.getValue());
		dividendProcessing.setResourceParamType(ResourceParamType.STRING.getValue());
		dividendProcessing.setUserAbleToCreateNewDateValue(true);
		dividendProcessing.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_REINVEST));
		dividendProcessing.addValueMessagePair(valueMessagePairDto(ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME));
		resourceCreateDto.addResourceParamDto(dividendProcessing);

		return resourceCreateDto;
	}	

	public ResourceCreateDto createResourceSuperannuation(Scenario scenario) {
		ResourceCreateDto resourceCreateDto = new ResourceCreateDto();
		resourceCreateDto.setScenarioId(scenario.getId());
		resourceCreateDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(scenario.getStartYearMonth()));
		resourceCreateDto.setResourceType(ResourceType.SUPERANNUATION.getValue());
		resourceCreateDto.setListOfAllowedParentResources(scenario.getListOfAllowedParentResources(ResourceType.SUPERANNUATION));

		ResourceCreateResourceParamWithValueDto openingFixedBalance = new ResourceCreateResourceParamWithValueDto();
		openingFixedBalance.setName(ResourceParamNameEnum.BALANCE_OPENING_FIXED.getValue());
		openingFixedBalance.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		openingFixedBalance.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(openingFixedBalance);
		
		ResourceCreateResourceParamWithValueDto preservationAge = new ResourceCreateResourceParamWithValueDto();
		preservationAge.setName(ResourceParamNameEnum.SUPER_PRESERVATION_AGE.getValue());
		preservationAge.setResourceParamType(ResourceParamType.INTEGER_POSITIVE.getValue());
		preservationAge.setUserAbleToCreateNewDateValue(false);
		resourceCreateDto.addResourceParamDto(preservationAge);

		ResourceCreateResourceParamWithValueDto growthRate = new ResourceCreateResourceParamWithValueDto();
		growthRate.setName(ResourceParamNameEnum.SUPER_GROWTH_RATE.getValue());
		growthRate.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		growthRate.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(growthRate);

		ResourceCreateResourceParamWithValueDto mgmtFeeAnnualPercentage = new ResourceCreateResourceParamWithValueDto();
		mgmtFeeAnnualPercentage.setName(ResourceParamNameEnum.SUPER_MANAGEMENT_FEE_ANNUAL_PERCENTAGE.getValue());
		mgmtFeeAnnualPercentage.setResourceParamType(ResourceParamType.BIG_DECIMAL.getValue());
		mgmtFeeAnnualPercentage.setUserAbleToCreateNewDateValue(true);
		resourceCreateDto.addResourceParamDto(mgmtFeeAnnualPercentage);

		ResourceCreateCashflowWithValueDto adminFee = new ResourceCreateCashflowWithValueDto();
		adminFee.setCategory(CashflowCategory.DEPRECIATION_ADMIN_FEE.getValue());
		adminFee.setFrequency(CashflowFrequency.MONTHLY.getValue());
		adminFee.setCpiAffected(true);
		resourceCreateDto.addCashflowDto(adminFee);
	
		return resourceCreateDto;
	}	

	private ValueMessagePairDto valueMessagePairDto(final ResourceParamStringValueEnum rpsv) {
		String message = messageSource.getMessage(rpsv.getMessageCode(), null, LocaleContextHolder.getLocale());
		ValueMessagePairDto valueMessagePairDto = new ValueMessagePairDto(rpsv.getValue(), message);
		return valueMessagePairDto;
	}
}
