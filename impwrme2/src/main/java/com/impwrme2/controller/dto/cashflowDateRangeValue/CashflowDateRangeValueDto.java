package com.impwrme2.controller.dto.cashflowDateRangeValue;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CashflowDateRangeValueDto {

	private Long id;
	
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.yearMonthStart.notNull}")
	private String yearMonthStart;

	private String yearMonthEnd;

	@NotNull(message = "{msg.validation.cashflowDateRangeValue.value.notNull}")
	private BigDecimal value;

	@NotNull(message = "{msg.validation.cashflowDateRangeValue.cashflow.notNull}")
	private Long cashflowId;

	@NotEmpty(message = "{msg.validation.cashflowDateRangeValue.cashflowType.notEmpty}")
	private String cashflowType;

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

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Long getCashflowId() {
		return cashflowId;
	}

	public void setCashflowId(Long cashflowId) {
		this.cashflowId = cashflowId;
	}

	public String getCashflowType() {
		return cashflowType;
	}

	public void setCashflowType(String cashflowType) {
		this.cashflowType = cashflowType;
	}
}
