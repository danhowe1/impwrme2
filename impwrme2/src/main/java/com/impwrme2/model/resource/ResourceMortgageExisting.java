package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.MORTGAGE_EXISTING)
public class ResourceMortgageExisting extends Resource {

	private static final long serialVersionUID = -2859351843058300669L;

	public static final String MORTGAGE_REPAYMENT_INTEREST_ONLY = "MORTGAGE_REPAYMENT_INTEREST_ONLY";
	public static final String MORTGAGE_REPAYMENT_PAY_OUT = "MORTGAGE_REPAYMENT_PAY_OUT";
	public static final String MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST = "MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST";

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceMortgageExisting() {
		super();
	}
	
	public ResourceMortgageExisting(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.MORTGAGE_EXISTING;
	}

	@Override
	public List<ResourceParamStringValueEnum> getListOfAllowedValues(ResourceParamNameEnum resourceParamName) {
		if (resourceParamName.equals(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE)) {
			return List.of(ResourceParamStringValueEnum.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST, 
					ResourceParamStringValueEnum.MORTGAGE_REPAYMENT_INTEREST_ONLY, 
					ResourceParamStringValueEnum.MORTGAGE_REPAYMENT_PAY_OUT);
		}
		return super.getListOfAllowedValues(resourceParamName);
	}
}
