package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
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
	public List<ResourceParamStringValueEnum> getListOfAllowedValues(ResourceParamNameEnum resourceParamName) {
		if (resourceParamName.equals(ResourceParamNameEnum.SHARES_DIVIDEND_PROCESSING)) {
			return List.of(ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_REINVEST, 
					ResourceParamStringValueEnum.SHARES_DIVIDEND_PROCESSING_TAKE_AS_INCOME);
		}
		return super.getListOfAllowedValues(resourceParamName);
	}
}
