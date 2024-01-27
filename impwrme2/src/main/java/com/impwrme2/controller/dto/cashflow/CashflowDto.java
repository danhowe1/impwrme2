package com.impwrme2.controller.dto.cashflow;

import java.util.ArrayList;
import java.util.List;

import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CashflowDto {

	private Long id;
	
	@NotEmpty(message = "{msg.validation.cashflow.name.notEmpty}")
	private String name;

	@NotNull(message = "{msg.validation.cashflow.detail.notNull}")
	private String detail;

	@NotNull(message = "{msg.validation.cashflow.frequency.notNull}")
	private String frequency;

	@NotNull(message = "{msg.validation.cashflow.cpiffected.notNull}")
    private boolean cpiAffected;

	@NotEmpty(message = "{msg.validation.notEmpty}")
	private String cashflowType;

	private List<CashflowDateRangeValueDto> cashflowDateRangeValueDtos = new ArrayList<CashflowDateRangeValueDto>();

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

	public String getCashflowType() {
		return cashflowType;
	}

	public void setCashflowType(String cashflowType) {
		this.cashflowType = cashflowType;
	}

	public List<CashflowDateRangeValueDto> getCashflowDateRangeValueDtos() {
		return cashflowDateRangeValueDtos;
	}
	
	public void addCashflowDateRangeValueDto(CashflowDateRangeValueDto cfdrvDto) {
		cashflowDateRangeValueDtos.add(cfdrvDto);	
	}
}
