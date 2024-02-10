package com.impwrme2.model.resourceParam;

import java.time.YearMonth;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.YEAR_MONTH)
public class ResourceParamYearMonth extends ResourceParam<YearMonth> {

	private static final long serialVersionUID = -905166006936024392L;

	protected ResourceParamYearMonth() {
		super();
	}
	
	public ResourceParamYearMonth(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.YEAR_MONTH;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<YearMonth>) resourceParamDateValue);
	}
}
