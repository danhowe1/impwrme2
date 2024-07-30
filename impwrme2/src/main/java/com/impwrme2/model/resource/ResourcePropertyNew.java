package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PROPERTY_NEW)
public class ResourcePropertyNew extends ResourcePropertyExisting {

	private static final long serialVersionUID = 2191667699136239436L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePropertyNew() {
		super();
	}
	
	public ResourcePropertyNew(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROPERTY_NEW;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.EXPENSE_MISC, 
				CashflowCategory.INCOME_MISC);
	}
}
