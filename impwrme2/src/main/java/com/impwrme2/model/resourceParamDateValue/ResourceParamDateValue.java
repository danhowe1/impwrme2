package com.impwrme2.model.resourceParamDateValue;

import java.io.Serializable;
import java.time.YearMonth;

import com.impwrme2.model.YearMonthIntegerAttributeConverter;
import com.impwrme2.model.resourceParam.ResourceParam;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

//@MappedSuperclass
@Entity
@Table(name = "resource_param_date_value", uniqueConstraints = @UniqueConstraint(columnNames = {"year_month", "resource_param_id"}))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_param_date_value_type", discriminatorType = DiscriminatorType.STRING)
public abstract class ResourceParamDateValue<T> implements Comparable<ResourceParamDateValue<?>>, Serializable {

	private static final long serialVersionUID = 7891540962084713877L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceParamDateValue() {}

	public ResourceParamDateValue(final YearMonth yearMonth, final T value) {
		this.yearMonth = yearMonth;
		this.setValue(value);
	}
	
	public ResourceParamDateValue(final YearMonth yearMonth, final String value) {
		this.yearMonth = yearMonth;
		this.setValueFromString(value);
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "resource_param_date_value_id")
	private Long id;
	
	@Column(name = "year_month", columnDefinition = "int")
	@Convert(converter = YearMonthIntegerAttributeConverter.class)
	@NotNull(message = "{msg.validation.resourceParamDateValue.yearMonth.notNull}")
	private YearMonth yearMonth;

	@ManyToOne(fetch = FetchType.EAGER, optional = false, targetEntity=ResourceParam.class)
	@JoinColumn(name = "resource_param_id")
	private ResourceParam<T> resourceParam;

	@Column(name = "value", columnDefinition = "varchar(32)")
	public abstract T getValue();
	
	public abstract void setValue(T value);

	public abstract void setValueFromString(String value);

	public abstract void setResourceParamGeneric(ResourceParam<?> resourceParam);

	@Override
	public int compareTo(ResourceParamDateValue<?> o) {
		if (this.getYearMonth().compareTo(o.getYearMonth()) != 0) {
			return this.getYearMonth().compareTo(o.getYearMonth());
		}
		return 0;
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

	public YearMonth getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(YearMonth yearMonth) {
		this.yearMonth = yearMonth;
	}

	public ResourceParam<T> getResourceParam() {
		return resourceParam;
	}

	public void setResourceParam(ResourceParam<T> resourceParam) {
		this.resourceParam = resourceParam;
	}
}
