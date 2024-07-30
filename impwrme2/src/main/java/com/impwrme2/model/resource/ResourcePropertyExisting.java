package com.impwrme2.model.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PROPERTY_EXISTING)
public class ResourcePropertyExisting extends Resource {

	private static final long serialVersionUID = 923864474563399441L;

	public static final String PROPERTY_STATUS_LIVING_IN = "PROPERTY_STATUS_LIVING_IN";
	public static final String PROPERTY_STATUS_RENTED = "PROPERTY_STATUS_RENTED";
	public static final String PROPERTY_STATUS_SOLD = "PROPERTY_STATUS_SOLD";
	
	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePropertyExisting() {
		super();
	}
	
	public ResourcePropertyExisting(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROPERTY_EXISTING;
	}
	
	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_MISC, 
				CashflowCategory.INCOME_MISC);
	}

	@Override
	public List<ResourceParam<?>> getResourceParamsUsersCanCreate() {
		ResourceParamBigDecimal housingMarketGrowthRate = new ResourceParamBigDecimal(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE);
		List<ResourceParam<?>> resourceParams = new ArrayList<>(Arrays.asList(housingMarketGrowthRate));
		for (ResourceParam<?> existingResourceParam : getResourceParams()) {
			resourceParams.removeIf(resourceParam -> resourceParam.getName().equals(existingResourceParam.getName()));
		}
		return resourceParams;
	}

	@Override
	public List<ResourceParamStringValueEnum> getListOfAllowedValues(ResourceParamNameEnum resourceParamName) {
		if (resourceParamName.equals(ResourceParamNameEnum.PROPERTY_STATUS)) {
			return List.of(ResourceParamStringValueEnum.PROPERTY_STATUS_LIVING_IN, ResourceParamStringValueEnum.PROPERTY_STATUS_RENTED, ResourceParamStringValueEnum.PROPERTY_STATUS_SOLD);
		}
		return super.getListOfAllowedValues(resourceParamName);
	}
}
