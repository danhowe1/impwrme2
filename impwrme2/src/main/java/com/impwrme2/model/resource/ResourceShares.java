package com.impwrme2.model.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.SHARES)
public class ResourceShares extends Resource {

	private static final long serialVersionUID = 3605829636707309071L;

	public static final String SHARES_DIVIDEND_PROCESSING_REINVEST = "SHARES_DIVIDEND_PROCESSING_REINVEST";
	public static final String SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME = "SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME";
	
	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceShares() {
		super();
	}
	
	public ResourceShares(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.SHARES;
	}

	@Override
	public List<ResourceParam<?>> getResourceParamsUsersCanCreate() {
		ResourceParamIntegerPositive balanceLiquidPreferredMin = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN);
		ResourceParamIntegerPositive balanceLiquidPreferredMax = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX);
		ResourceParamBigDecimal shareMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.SHARES_SHARE_MARKET_GROWTH_RATE);
		List<ResourceParam<?>> resourceParams = new ArrayList<>(Arrays.asList(balanceLiquidPreferredMin, balanceLiquidPreferredMax, shareMarketGrowthRate));
		for (ResourceParam<?> existingResourceParam : getResourceParams()) {
			resourceParams.removeIf(resourceParam -> resourceParam.getName().equals(existingResourceParam.getName()));
		}
		return resourceParams;
	}

	@Override
	public List<ResourceParamStringValueEnum> getListOfAllowedValues(ResourceParamNameEnum resourceParamName) {
		if (resourceParamName.equals(ResourceParamNameEnum.SHARES_DIVIDEND_PROCESSING)) {
			return List.of(ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_REINVEST, 
					ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME);
		}
		return super.getListOfAllowedValues(resourceParamName);
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.DEPOSIT_BALANCE, CashflowCategory.WITHDRAWAL_BALANCE);
	}
}
