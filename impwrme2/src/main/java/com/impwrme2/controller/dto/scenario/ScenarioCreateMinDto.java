package com.impwrme2.controller.dto.scenario;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ScenarioCreateMinDto {

	@NotEmpty(message = "Please provide a scenario name.")
	private String name;

	@NotEmpty(message = "Please provide a start date.")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Start date must be in format MM YYYY")
	private String startYearMonth;

	@NotEmpty(message = "Please provide a person name.")
	private String personName;

	@NotEmpty(message = "{msg.validation.notEmpty}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "{msg.validation.monthYear}")
	private String birthYearMonth;

	@NotNull(message = "Please provide a retirement age.")
	private Integer retirementAge;
	
	@NotNull(message = "Please provide monthly personal living expenses.")
	private Integer monthlyLivingExpenses;
	
	private Integer monthlyEmploymentIncome;
	
	//-------------------
	// Getters & setters.
	//-------------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartYearMonth() {
		return startYearMonth;
	}

	public void setStartYearMonth(String startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getBirthYearMonth() {
		return birthYearMonth;
	}

	public void setBirthYearMonth(String birthYearMonth) {
		this.birthYearMonth = birthYearMonth;
	}

	public Integer getRetirementAge() {
		return retirementAge;
	}

	public void setRetirementAge(Integer retirementAge) {
		this.retirementAge = retirementAge;
	}

	public Integer getMonthlyLivingExpenses() {
		return monthlyLivingExpenses;
	}

	public void setMonthlyLivingExpenses(Integer monthlyLivingExpenses) {
		this.monthlyLivingExpenses = monthlyLivingExpenses;
	}

	public Integer getMonthlyEmploymentIncome() {
		return monthlyEmploymentIncome;
	}

	public void setMonthlyEmploymentIncome(Integer monthlyEmploymentIncome) {
		this.monthlyEmploymentIncome = monthlyEmploymentIncome;
	}
}
