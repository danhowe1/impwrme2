package com.impwrme2.controller.dto.cashflow;

import java.util.List;

import com.impwrme2.model.cashflow.CashflowFrequency;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CashflowCreateDto {

	private Long id;
	
	@NotEmpty(message = "{msg.validation.cashflow.category.notEmpty}")
	private String category;

	@NotNull(message = "{msg.validation.cashflow.detail.notNull}")
	private String detail;

	@NotNull(message = "{msg.validation.cashflow.frequency.notNull}")
	private String frequency;

	@NotNull(message = "{msg.validation.cashflow.cpiffected.notNull}")
    private boolean cpiAffected = true;

	@NotNull(message = "{msg.validation.cashflow.resourceId.notNull}")
	private Long resourceId;

	private  String yearMonthStart;

	private  String yearMonthEnd;

	@NotNull(message = "{msg.validation.cashflow.value.notNull}")
	private Integer value;

	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Boolean getCpiAffected() {
		return cpiAffected;
	}

	public void setCpiAffected(Boolean cpiAffected) {
		this.cpiAffected = cpiAffected;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getYearMonthStart() {
		return yearMonthStart;
	}

	public void setYearMonthStart(String yearMonthStart) {
		this.yearMonthStart = yearMonthStart;
	}

	public String getYearMonthEnd() {
		return yearMonthEnd;
	}

	public void setYearMonthEnd(String yearMonthEnd) {
		this.yearMonthEnd = yearMonthEnd;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public List<String> getListOfFrequencies() {
		return CashflowFrequency.listOfValues();
	}
}
