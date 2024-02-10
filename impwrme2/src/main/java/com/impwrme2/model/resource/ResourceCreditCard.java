package com.impwrme2.model.resource;

import java.util.Collections;
import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.CREDIT_CARD)
public class ResourceCreditCard extends Resource {

	private static final long serialVersionUID = 1183308310916669229L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceCreditCard() {
		super();
	}
	
	public ResourceCreditCard(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.CREDIT_CARD;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return Collections.emptyList();
	}
}
