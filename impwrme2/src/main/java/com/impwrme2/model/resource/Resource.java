package com.impwrme2.model.resource;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.impwrme2.model.scenario.Scenario;

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

@Entity
@Table(name = "resource", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "scenario_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Resource implements IResource, Comparable<Resource>, Serializable {

	private static final long serialVersionUID = -6863611557805664844L;
	
	public Resource(String name, Scenario scenario) {
		setName(name);
		setScenario(scenario);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "resource_id")
	private Long id;

	@Column(name = "name")
	@NotEmpty(message = "*Please provide a resource name.")
	private String name;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="scenario_id")
	private Scenario scenario;

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name="parent_id")
	private Resource parent;

	@OneToMany(mappedBy="parent", fetch=FetchType.EAGER, cascade=CascadeType.ALL, orphanRemoval = true)
	private final Set<Resource> children = new HashSet<Resource>();

	@Override
	public String getPrioritisationWithinResourceType() {
		return "";
	}

	@Override
	public int compareTo(Resource o) {
		
		int compareResourceType = this.getResourceType().compareTo(o.getResourceType());
		if (compareResourceType != 0) {
			return compareResourceType;
		}
		
		int comparePrioritisation = this.getPrioritisationWithinResourceType().compareTo(o.getPrioritisationWithinResourceType());
		if (comparePrioritisation != 0) {
			return comparePrioritisation;
		}
		
		int compareName = this.getName().compareTo(o.getName());
		if (compareName != 0) {
			return compareName;
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

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Set<Resource> getChildren() {
		return children;
	}

	public void addChild(Resource resource) {
		this.children.add(resource);
		resource.setParent(this);
	}

	public void removeChild(Resource resource) {
		children.remove(resource);
		resource.setParent(null);
	}

	public Resource getParent() {
		return this.parent;
	}
	
	public void setParent(Resource parent) {
		this.parent = parent;
	}
}
