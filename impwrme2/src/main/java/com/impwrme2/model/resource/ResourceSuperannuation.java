package com.impwrme2.model.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowCategory;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.SUPERANNUATION)
public class ResourceSuperannuation extends Resource {

	private static final long serialVersionUID = 5028205240793185084L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceSuperannuation() {
		super();
	}
	
	public ResourceSuperannuation(final String name) {
		super(name);
	}
	@Override
	public ResourceType getResourceType() {
		return ResourceType.SUPERANNUATION;
	}

	@Override
	public List<CashflowCategory> getCashflowCategoriesUsersCanCreate() {
		return List.of(CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_CONCESSIONAL_EMPLOYER, 
				CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_CONCESSIONAL_PERSONAL, 
				CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_GOVT, 
				CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL);
	}
}
