package com.impwrme2.controller.quickSnapshot.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class QuickSnapshotDto implements Serializable {

	private static final long serialVersionUID = -5640589647520819062L;

	@NotEmpty(message = "{msg.validation.notEmpty}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "{msg.validation.monthYear}")
	private String birthMonthYear;
	
	@NotNull(message = "{msg.validation.notEmpty}")
	private Integer householdSavings;

	//-------------------
	// Getters & setters.
	//-------------------
	
	public String getBirthMonthYear() {
		return birthMonthYear;
	}

	public void setBirthMonthYear(String birthMonthYear) {
		this.birthMonthYear = birthMonthYear;
	}

	public Integer getHouseholdSavings() {
		return householdSavings;
	}

	public void setHouseholdSavings(Integer householdSavings) {
		this.householdSavings = householdSavings;
	}
}
