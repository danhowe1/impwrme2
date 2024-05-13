package com.impwrme2.model.scenario;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceMortgage;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.ResourcePropertyNew;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "scenario")
public class Scenario implements Serializable {

	private static final long serialVersionUID = -5054918413735405815L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected Scenario() {
		this.userId = null;
	}

	public Scenario(ResourceScenario resourceScenario, String userId) {
		this.userId = userId;
		this.resourceScenario = resourceScenario;
		this.addResource(resourceScenario);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "scenario_id")
	private Long id;

	@Column(name = "userId")
	@NotEmpty(message = "{msg.validation.scenario.userId.notEmpty}")
	private final String userId;

	@Transient
	private ResourceScenario resourceScenario;
	
	@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Resource> resources = new HashSet<Resource>();

	public YearMonth calculateEndYearMonth() {
		YearMonth endYearMonth = YearMonth.now();
		for (Resource resource : getResources()) {
			if (resource instanceof ResourcePerson) {
				YearMonth birthYearMonth = (YearMonth) resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH, resource.getStartYearMonth()).get().getValue();
				Integer departureAge = (Integer) resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_DEPARTURE_AGE, resource.getStartYearMonth()).get().getValue();
				YearMonth endDate = birthYearMonth.plusYears(departureAge);
				if (endDate.isAfter(endYearMonth)) {
					endYearMonth = endDate;
				}
			}
		}
		return endYearMonth;
	}
	
	public List<Resource> getListOfAllowedParentResources(ResourceType resourceType) {
		List<Resource> allowedResources = new ArrayList<Resource>();
		switch (resourceType) {
		case MORTGAGE:
			for (Resource resource : getResources()) {
				if (resource instanceof ResourcePropertyExisting || resource instanceof ResourcePropertyNew) {
					allowedResources.add(resource);
				}
			}
			break;
		case MORTGAGE_OFFSET_ACCOUNT:
			for (Resource resource : getResources()) {
				if (resource instanceof ResourceMortgage) {
					allowedResources.add(resource);
				}
			}
			break;
		case SUPERANNUATION:
			for (Resource resource : getResources()) {
				if (resource instanceof ResourcePerson) {
					allowedResources.add(resource);
				}
			}
			break;
		default:
			break;
		}
		return allowedResources;
	}

	//-------------------
	// Getters & setters.
	//-------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return this.getResourceScenario().getName();
	}
	
	public YearMonth getStartYearMonth() {
		return this.getResourceScenario().getStartYearMonth();
	}
	
	public Set<Resource> getResources() {
		return resources;
	}

	public ResourceScenario getResourceScenario() {	
		if (null == resourceScenario) {
			for (Resource resource : getResources()) {
				if (resource instanceof ResourceScenario) {
					resourceScenario = (ResourceScenario) resource;
					break;
				}
			}
		}
		return resourceScenario;	
	}
	
	@Transient
	public SortedSet<Resource> getSortedResources() {
		SortedSet<Resource> sortedResources = new TreeSet<Resource>();
		sortedResources.addAll(getResources());
		return sortedResources;
	}
	
	public Scenario addResource(Resource resource) {
		resources.add(resource);
		resource.setScenario(this);
		return this;
	}
	
	public Scenario removeResource(Resource resource) {
		
		Resource parent = resource.getParent();
		if (null != parent) {
			parent.removeChild(resource);
		}
		
		resources.remove(resource);
		resource.setScenario(null);
		return this;
	}
}
