package com.impwrme2.model.cashflowDateRangeValue;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.YearMonthIntegerAttributeConverter;
import com.impwrme2.model.cashflow.Cashflow;

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

	public CashflowDateRangeValue(final YearMonth yearMonthStart, BigDecimal value) {
		this(yearMonthStart, null, value);
	}
	
	public CashflowDateRangeValue(final YearMonth yearMonthStart, final YearMonth yearMonthEnd, BigDecimal value) {
		this.yearMonthStart = yearMonthStart;
		this.yearMonthEnd = yearMonthEnd;
		this.value = value;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cashflow_date_range_value_id")
	private Long id;
	
	@Column(name = "year_month_start", columnDefinition = "int")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.yearMonthStart.notNull}")
	private final YearMonth yearMonthStart;

	@Column(name = "year_month_end", columnDefinition = "int")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	private final YearMonth yearMonthEnd;

	@Column(name = "value", precision = 8, scale = 2, columnDefinition = "decimal(8, 2)")
	@NotNull(message = "{msg.validation.cashflowDateRangeValue.value.notNull}")
	private final BigDecimal value;
	
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

	public YearMonth getYearMonthEnd() {
		return yearMonthEnd;
	}

	public BigDecimal getValue() {
		return value;
	}

	public Cashflow getCashflow() {
		return cashflow;
	}

	public void setCashflow(Cashflow cashflow) {
		this.cashflow = cashflow;
	}
}
