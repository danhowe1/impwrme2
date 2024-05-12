package com.impwrme2.controller.dto.resource;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowFrequency;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ResourceCreateCashflowWithValueDto {

	@NotEmpty(message = "{msg.validation.cashflow.category.notEmpty}")
	private String category;

//	@NotNull(message = "{msg.validation.cashflow.detail.notNull}")
	private String detail;

	@NotNull(message = "{msg.validation.cashflow.frequency.notNull}")
	private String frequency;

	@NotNull(message = "{msg.validation.cashflow.cpiffected.notNull}")
    private boolean cpiAffected = true;

	private  String yearMonthEnd;

	@NotNull(message = "{msg.validation.cashflow.value.notNull}")
	private Integer value;

	//-------------------
	// Getters & setters.
	//-------------------

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public boolean isCpiAffected() {
		return cpiAffected;
	}

	public void setCpiAffected(boolean cpiAffected) {
		this.cpiAffected = cpiAffected;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getYearMonthEnd() {
		return yearMonthEnd;
	}

	public void setYearMonthEnd(String yearMonthEnd) {
		this.yearMonthEnd = yearMonthEnd;
	}

	public List<String> getListOfFrequencies() {
		return CashflowFrequency.listOfValues();
	}
}
