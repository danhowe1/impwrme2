package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PERSON_ADULT)
public class ResourcePersonAdult extends Resource {

	private static final long serialVersionUID = -3109504020949719141L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePersonAdult() {
		super();
	}
	
	public ResourcePersonAdult(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PERSON_ADULT;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_HOLIDAYS, 
				CashflowCategory.EXPENSE_LIVING_ESSENTIAL, 
				CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL, 
				CashflowCategory.EXPENSE_MISC, 
				CashflowCategory.INCOME_EMPLOYMENT,
				CashflowCategory.INCOME_MISC);
	}
}
