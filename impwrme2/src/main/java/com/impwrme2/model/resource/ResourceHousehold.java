package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.HOUSEHOLD)
public class ResourceHousehold extends Resource {

	private static final long serialVersionUID = -8656133449708260413L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceHousehold() {
		super();
	}
	
	public ResourceHousehold(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.HOUSEHOLD;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_HOLIDAYS, 
				CashflowCategory.EXPENSE_RENT,
				CashflowCategory.EXPENSE_MISC, 
				CashflowCategory.INCOME_MISC);
	}
}
