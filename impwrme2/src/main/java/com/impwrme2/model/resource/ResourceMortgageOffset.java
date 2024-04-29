package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.MORTGAGE_OFFSET_ACCOUNT)
public class ResourceMortgageOffset extends Resource {

	private static final long serialVersionUID = 712383821174282527L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceMortgageOffset() {
		super();
	}
	
	public ResourceMortgageOffset(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.MORTGAGE_OFFSET_ACCOUNT;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.DEPOSIT_BALANCE, CashflowCategory.WITHDRAWAL_BALANCE);
	}
}
