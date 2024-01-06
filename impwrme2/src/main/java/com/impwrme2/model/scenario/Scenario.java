package com.impwrme2.model.scenario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.impwrme2.model.resource.Resource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "scenario")
public class Scenario implements Serializable {

	private static final long serialVersionUID = 1626650168937964936L;

	public Scenario(String name, BigDecimal cpi) {
		setName(name);
		setCpi(cpi);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "scenario_id")
	private Long id;

	@Column(name = "name")
	@NotEmpty(message = "{msg.validation.scenario.name.notEmpty}")
	private String name;
	
	@Column(name = "cpi", precision = 3, scale = 2, columnDefinition = "decimal(3, 2)")
	@NotNull(message = "{msg.validation.scenario.cpi.notNull}")
	private BigDecimal cpi;

	@OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<Resource> resources = new HashSet<Resource>();

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

	public BigDecimal getCpi() {
		return cpi;
	}

	public void setCpi(BigDecimal cpi) {
		this.cpi = cpi;
	}

	public Set<Resource> getResources() {
		return resources;
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
