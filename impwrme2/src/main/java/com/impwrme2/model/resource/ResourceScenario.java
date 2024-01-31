package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.SCENARIO)
public class ResourceScenario extends Resource {

	private static final long serialVersionUID = -7777701346681233926L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceScenario() {
		super();
	}
	
	public ResourceScenario(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.SCENARIO;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of();
	}
}
