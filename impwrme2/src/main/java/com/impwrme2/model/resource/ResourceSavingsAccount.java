package com.impwrme2.model.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.SAVINGS_ACCOUNT)
public class ResourceSavingsAccount extends Resource {

	private static final long serialVersionUID = -7058676753566411406L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceSavingsAccount() {
		super();
	}
	
	public ResourceSavingsAccount(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.SAVINGS_ACCOUNT;
	}

	@Override
	public List<ResourceParam<?>> getResourceParamsUsersCanCreate() {
		ResourceParamIntegerPositive balanceLiquidLegalMin = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
		ResourceParamIntegerPositive balanceLiquidLegalMax = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX);
		ResourceParamIntegerPositive balanceLiquidPreferredMin = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN);
		ResourceParamIntegerPositive balanceLiquidPreferredMax = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX);
		List<ResourceParam<?>> resourceParams = new ArrayList<>(Arrays.asList(balanceLiquidLegalMin, balanceLiquidLegalMax, balanceLiquidPreferredMin, balanceLiquidPreferredMax));
		for (ResourceParam<?> existingResourceParam : getResourceParams()) {
			resourceParams.removeIf(resourceParam -> resourceParam.getName().equals(existingResourceParam.getName()));
		}
		return resourceParams;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.DEPOSIT_BALANCE, CashflowCategory.WITHDRAWAL_BALANCE);
	}
}
