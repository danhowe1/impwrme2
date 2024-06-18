package com.impwrme2.model.scenario;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "scenario_year_balance", uniqueConstraints = @UniqueConstraint(columnNames = {"scenario_id", "year"}))
public class ScenarioYearBalance implements Serializable {

	private static final long serialVersionUID = -6425848376225456110L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ScenarioYearBalance() {
		this.year = null;
		this.balance = null;
	}

	public ScenarioYearBalance(final Integer year, final Integer balance) {
		this.year = year;
		this.balance = balance;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "year")
	@NotNull(message = "{msg.validation.scenarioYearBalance.year.notNull}")
	private final Integer year;

	@Column(name = "balance")
	@NotNull(message = "{msg.validation.scenarioYearBalance.balance.notNull}")
	private final Integer balance;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name="scenario_id")
	private Scenario scenario;

	//-------------------
	// Getters & setters.
	//-------------------
	
	public Integer getYear() {
		return year;
	}

	public Integer getBalance() {
		return balance;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public Scenario getScenario() {
		return scenario;
	}
}
