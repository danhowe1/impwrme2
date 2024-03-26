package com.impwrme2.model.cashflow;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.Resource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "cashflow", uniqueConstraints = @UniqueConstraint(columnNames = {"category", "detail", "resource_id"}))
public class Cashflow implements Comparable<Cashflow>, Serializable {

	private static final long serialVersionUID = -7351769216489053280L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected Cashflow() {
		this.category = null;
		this.detail = null;
		this.frequency = null;
		this.cpiAffected = null;}
	
	public Cashflow(CashflowCategory category, CashflowFrequency frequency, Boolean cpiAffected) {
		this(category, "", frequency, cpiAffected);
	}
	
	public Cashflow(CashflowCategory category, String detail, CashflowFrequency frequency, Boolean cpiAffected) {
		this.category = category;
		this.detail = detail;
		this.frequency = frequency;
		this.cpiAffected = cpiAffected;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cashflow_id")
	private Long id;
	
	@Column(name = "category")
	@NotNull(message = "{msg.validation.cashflow.category.notNull}")
	@Enumerated(EnumType.STRING)
	private final CashflowCategory category;

	@Column(name = "detail")
	@NotNull(message = "{msg.validation.cashflow.detail.notNull}")
	private final String detail;

	@Column(name = "frequency", columnDefinition = "varchar(32)")
	@NotNull(message = "{msg.validation.cashflow.frequency.notNull}")
	@Enumerated(EnumType.STRING)
	private final CashflowFrequency frequency;

	@Column(name = "cpi_affected", columnDefinition = "boolean")
	@NotNull(message = "{msg.validation.cashflow.cpiffected.notNull}")
    private final Boolean cpiAffected;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "resource_id")
	private Resource resource;

	@OneToMany(mappedBy = "cashflow", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, targetEntity=CashflowDateRangeValue.class)
	private List<CashflowDateRangeValue> cashflowDateRangeValues = new ArrayList<CashflowDateRangeValue>();

	@Override
	public int compareTo(Cashflow o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns a CashflowDateRangeValue if the yearMonth is equal to or after the start date and before the end date. 
	 * If more than one date is found the highest start date is chosen.
	 * @param yearMonth Date to test against.
	 * @return The CashflowDateRangeValue adhering to the date criteria.
	 */
	public Optional<CashflowDateRangeValue> getCashflowDateRangeValue(YearMonth yearMonth) {
		Optional<CashflowDateRangeValue> cfdrvOpt = Optional.empty();
		for (CashflowDateRangeValue cfdrv : getCashflowDateRangeValues()) {
			if (!cfdrv.getYearMonthStart().isAfter(yearMonth)) {
				if (null == cfdrv.getYearMonthEnd() || (!cfdrv.getYearMonthEnd().isBefore(yearMonth))) {
					if (cfdrvOpt.isEmpty()) {
						cfdrvOpt = Optional.of(cfdrv);
					} else {
						if (cfdrv.getYearMonthStart().isAfter(cfdrvOpt.get().getYearMonthStart())) {
							cfdrvOpt = Optional.of(cfdrv);							
						}
					}
				}
			}
		}		
		return cfdrvOpt;
	}
	
	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CashflowCategory getCategory() {
		return category;
	}

	public String getDetail() {
		return detail;
	}

	public CashflowFrequency getFrequency() {
		return frequency;
	}

	public Boolean getCpiAffected() {
		return cpiAffected;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public List<CashflowDateRangeValue> getCashflowDateRangeValues() {
		return cashflowDateRangeValues;
	}
	
	public Cashflow addCashflowDateRangeValue(CashflowDateRangeValue cashflowDateRangeValue) {
		this.cashflowDateRangeValues.add(cashflowDateRangeValue);
		cashflowDateRangeValue.setCashflow(this);
		return this;
	}

	public Cashflow removeCashflowDateRangeValue(CashflowDateRangeValue cashflowDateRangeValue) {
		cashflowDateRangeValues.remove(cashflowDateRangeValue);
		cashflowDateRangeValue.setCashflow(null);
		return this;
	}
}
