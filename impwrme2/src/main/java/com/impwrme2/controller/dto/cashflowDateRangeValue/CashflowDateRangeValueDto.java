package com.impwrme2.controller.dto.cashflowDateRangeValue;

import com.impwrme2.controller.validator.CashflowDateRangeValueConstraint;

import jakarta.validation.constraints.NotNull;

@CashflowDateRangeValueConstraint
public class CashflowDateRangeValueDto {

	private Long id;
	
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.yearMonthStart.notNull}")
	private String yearMonthStart;

	private String yearMonthEnd;

	@NotNull(message = "{msg.validation.cashflowDateRangeValue.value.notNull}")
	private Integer value;

	@NotNull(message = "{msg.validation.cashflowDateRangeValue.cashflow.notNull}")
	private Long cashflowId;

	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getCashflowId() {
		return cashflowId;
	}

	public void setCashflowId(Long cashflowId) {
		this.cashflowId = cashflowId;
	}
}
