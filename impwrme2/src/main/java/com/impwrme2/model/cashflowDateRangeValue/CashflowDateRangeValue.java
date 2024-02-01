package com.impwrme2.model.cashflowDateRangeValue;

import java.time.YearMonth;

import com.impwrme2.model.YearMonthIntegerAttributeConverter;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "cashflow_date_range_value", uniqueConstraints = @UniqueConstraint(columnNames = {"year_month_start", "cashflow_id"}))
public class CashflowDateRangeValue {

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected CashflowDateRangeValue() {
		this.yearMonthStart = null;
		this.yearMonthEnd = null;
		this.value = null;
		}

	public CashflowDateRangeValue(final CashflowType type, final YearMonth yearMonthStart, final Integer value) {
		this(type, yearMonthStart, null, value);
	}
	
	public CashflowDateRangeValue(final CashflowType type, final YearMonth yearMonthStart, final YearMonth yearMonthEnd, final Integer value) {
		this.yearMonthStart = yearMonthStart;
		this.yearMonthEnd = yearMonthEnd;
		this.setValue(value, type);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cashflow_date_range_value_id")
	private Long id;
	
	@Column(name = "year_month_start", columnDefinition = "int")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.yearMonthStart.notNull}")
	private  YearMonth yearMonthStart;

	@Column(name = "year_month_end", columnDefinition = "int")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	private  YearMonth yearMonthEnd;

	@Column(name = "value", columnDefinition = "int")
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.value.notNull}")
	private Integer value;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity=Cashflow.class)
	@JoinColumn(name = "cashflow_id")
	private Cashflow cashflow;

	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public YearMonth getYearMonthStart() {
		return yearMonthStart;
	}

	public void setYearMonthStart(YearMonth yearMonthStart) {
		this.yearMonthStart = yearMonthStart;
	}
	
	public YearMonth getYearMonthEnd() {
		return yearMonthEnd;
	}

	public void setYearMonthEnd(YearMonth yearMonthEnd) {
		this.yearMonthEnd = yearMonthEnd;
	}
	
	public Integer getValue() {
		return value;
	}

	public void setValue(final Integer value, final CashflowType type) {
		this.value = value;
		if ((value.intValue()>0) && (type.equals(CashflowType.EXPENSE) || type.equals(CashflowType.WITHDRAWAL))) {
			this.value = -value;
		} else if ((value.intValue()<=0) && (type.equals(CashflowType.INCOME) || type.equals(CashflowType.DEPOSIT))) {
			this.value = -value;
		}
	}
	
	public Cashflow getCashflow() {
		return cashflow;
	}

	public void setCashflow(Cashflow cashflow) {
		this.cashflow = cashflow;
	}
}
