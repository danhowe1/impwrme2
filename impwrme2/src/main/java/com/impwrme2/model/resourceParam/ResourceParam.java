package com.impwrme2.model.resourceParam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
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
import jakarta.validation.constraints.NotEmpty;
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

	public ResourceParam(final String name) {
		this.name = name;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "resource_param_id")
	private Long id;
	
	@Column(name = "name")
	@NotEmpty(message = "{msg.validation.resourceParam.name.notEmpty}")
	private String name;

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

	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
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
	
	public void addResourceParamDateValue(ResourceParamDateValue<T> resourceParamDateValue) {
		this.resourceParamDateValues.add(resourceParamDateValue);
	}
}
