package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;

public interface IResource {

	public ResourceType getResourceType();
//	public ResourceScenario getResourceScenario();
	public String getPrioritisationWithinResourceType();
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate();
	public List<ResourceParam<?>> getResourceParamsUsersCanCreate();
	public List<ResourceParamStringValueEnum> getListOfAllowedValues(ResourceParamNameEnum resourceParamName);
}
