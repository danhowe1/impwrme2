package com.impwrme2.model.resourceParam;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "resource_param", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "resource_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_param_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ResourceParam<T> implements Comparable<ResourceParam<?>>, Serializable {

	private static final long serialVersionUID = 5615750602336670670L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceParam() {}

	public ResourceParam(final ResourceParamNameEnum paramName) {
		this.name = paramName;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "resource_param_id")
	private Long id;
	
	@Column(name = "name")
	@NotNull(message = "{msg.validation.resourceParam.name.notEmpty}")
	@Enumerated(EnumType.STRING)
	private ResourceParamNameEnum name;

	@Column(name = "user_can_create_new_date_value", columnDefinition = "boolean default false")
	@NotNull(message = "{msg.validation.resourceParam.userAbleToCreateNewDateValue.notNull}")
	private boolean userAbleToCreateNewDateValue;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "resource_id")
	private Resource resource;
	
	public abstract ResourceParamType getResourceParamType();

	@OneToMany(mappedBy = "resourceParam", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity=ResourceParamDateValue.class)
	private List<ResourceParamDateValue<T>> resourceParamDateValues = new ArrayList<ResourceParamDateValue<T>>();

	public abstract void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue);

	@Override
	public int compareTo(ResourceParam<?> o) {
		if (this.getName().compareTo(o.getName()) != 0) {
			return this.getName().compareTo(o.getName());
		}
		return 0;
	}

	/**
	 * Retrieves the ID of another ResourceParamDateValue (RPDV) with a duplicate date.
	 *
	 * @param id the ID of the current RPDV (can be null if creating a new one).
	 * @param yearMonth the YearMonth to check for duplicates.
	 * @return an Optional containing the ID of another RPDV with the same date, or empty if no duplicates are found.
	 */
	public Optional<Long> getIdOfRpdvWithDuplicateDate(Long id, YearMonth yearMonth) {
		Long idOfAnotherRpdvWithSameDate = null;
		for (ResourceParamDateValue<?> existingRpdv : getResourceParamDateValues()) {
			if ((null == id || !id.equals(existingRpdv.getId())) &&  yearMonth.equals(existingRpdv.getYearMonth())) {
				idOfAnotherRpdvWithSameDate = existingRpdv.getId();
				break;
			}
		}
		return Optional.ofNullable(idOfAnotherRpdvWithSameDate);
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

	public ResourceParamNameEnum getName() {
		return name;
	}

	public void setName(ResourceParamNameEnum name) {
		this.name = name;
	}

	public boolean isUserAbleToCreateNewDateValue() {
		return userAbleToCreateNewDateValue;
	}

	public void setUserAbleToCreateNewDateValue(boolean userAbleToCreateNewDateValue) {
		this.userAbleToCreateNewDateValue = userAbleToCreateNewDateValue;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List<ResourceParamDateValue<T>> getResourceParamDateValues() {
		return resourceParamDateValues;
	}
	
	public ResourceParam<?> addResourceParamDateValue(ResourceParamDateValue<T> resourceParamDateValue) {
		this.resourceParamDateValues.add(resourceParamDateValue);
		resourceParamDateValue.setResourceParam(this);
		return this;
	}

	public ResourceParam<?> removeResourceParamDateValue(ResourceParamDateValue<?> resourceParamDateValue) {
		resourceParamDateValues.remove(resourceParamDateValue);
		resourceParamDateValue.setResourceParam(null);
		return this;
	}
}
