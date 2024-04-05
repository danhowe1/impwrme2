package com.impwrme2.model.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.CURRENT_ACCOUNT)
public class ResourceCurrentAccount extends Resource {

	private static final long serialVersionUID = 5117303230905806486L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceCurrentAccount() {
		super();
	}
	
	public ResourceCurrentAccount(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.CURRENT_ACCOUNT;
	}

	@Override
	public List<ResourceParam<?>> getResourceParamsUsersCanCreate() {
		ResourceParamIntegerPositive balanceLiquidPreferredMin = new ResourceParamIntegerPositive(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN);
		List<ResourceParam<?>> resourceParams = new ArrayList<>(Arrays.asList(balanceLiquidPreferredMin));
		for (ResourceParam<?> existingResourceParam : getResourceParams()) {
			resourceParams.removeIf(resourceParam -> resourceParam.getName().equals(existingResourceParam.getName()));
		}
		return resourceParams;
	}
}
