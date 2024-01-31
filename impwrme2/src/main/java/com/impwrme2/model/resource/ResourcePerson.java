package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PERSON)
public class ResourcePerson extends Resource {

	private static final long serialVersionUID = -3109504020949719141L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePerson() {
		super();
	}
	
	public ResourcePerson(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PERSON;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_LIVING_ESSENTIAL, CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL, CashflowCategory.EXPENSE_MISC, CashflowCategory.INCOME_EMPLOYMENT);
	}
}
