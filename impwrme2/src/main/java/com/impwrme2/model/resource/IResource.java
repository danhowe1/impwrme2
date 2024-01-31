package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

public interface IResource {

	public ResourceType getResourceType();
	public ResourceScenario getResourceScenario();
	public String getPrioritisationWithinResourceType();
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate();
}
