package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PERSON_CHILD)
public class ResourcePersonChild extends Resource {

	private static final long serialVersionUID = -1873377722596299020L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePersonChild() {
		super();
	}
	
	public ResourcePersonChild(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PERSON_CHILD;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_HOLIDAYS, 
				CashflowCategory.EXPENSE_LIVING_ESSENTIAL, 
				CashflowCategory.EXPENSE_LIVING_NON_ESSENTIAL, 
				CashflowCategory.EXPENSE_MISC, 
				CashflowCategory.INCOME_MISC);
	}
}
