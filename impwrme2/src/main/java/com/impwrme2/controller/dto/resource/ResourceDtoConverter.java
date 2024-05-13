package com.impwrme2.controller.dto.resource;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflow.CashflowType;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceCreditCard;
import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.model.resource.ResourceHousehold;
import com.impwrme2.model.resource.ResourceMortgageExisting;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerNegative;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParam.ResourceParamString;
import com.impwrme2.model.resourceParam.ResourceParamType;
import com.impwrme2.model.resourceParam.ResourceParamYearMonth;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerNegative;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueString;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueYearMonth;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.scenario.ScenarioService;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceDtoConverter {

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ScenarioService scenarioService;
	
	public ResourceDto entityToDto(Resource resource) {
		ResourceDto resourceDto = new ResourceDto();
		resourceDto.setId(resource.getId());
		resourceDto.setName(resource.getName());
		resourceDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(resource.getStartYearMonth()));
		resourceDto.setResourceType(resource.getResourceType().getValue());
		if (resource instanceof ResourceScenario || resource instanceof ResourceHousehold) {
			resourceDto.setUserCanDelete(false);
		}
		
		for (CashflowCategory cat : resource.getCashflowCategoriesUsersCanCreate()) {
			resourceDto.addCashflowCategoryUsersCanCreate(cat.getValue());
		}
		for (ResourceParam<?> resourceParam : resource.getResourceParamsUsersCanCreate()) {
			resourceDto.addResourceParamNameUsersCanCreate(resourceParam.getName().name() + "|" + resourceParam.getResourceParamType().getValue());
		}
		return resourceDto;
	}
	
	public Resource resourceCreateDtoToEntity(ResourceCreateDto resourceCreateDto, String userId) {
		Resource resource = resourceCreateDtoToResource(resourceCreateDto, userId);		
		for (ResourceCreateResourceParamWithValueDto resourceParamDto : resourceCreateDto.getResourceParamDtos()) {
			resource.addResourceParam(resourceCreateResourceParamWithValueDtoToEntity(resource.getStartYearMonth(), resourceParamDto));
		}
		for (ResourceCreateCashflowWithValueDto cashflowDto : resourceCreateDto.getCashflowDtos()) {
			if (cashflowDto.getValue() != 0) {
				resource.addCashflow(resourceCreateCashflowWithValueDtoToEntity(resource.getStartYearMonth(), cashflowDto));
			}
		}
		
		return resource;
	}
	
	private Resource resourceCreateDtoToResource(ResourceCreateDto resourceCreateDto, String userId) {
		Scenario scenario = scenarioService.findByIdAndUserId(resourceCreateDto.getScenarioId(), userId).orElseThrow(() -> new IllegalArgumentException("Invalid scenario id:" + resourceCreateDto.getScenarioId()));
		Resource resource;
		switch (ResourceType.valueOf(resourceCreateDto.getResourceType())) {
		case CREDIT_CARD:
			resource = new ResourceCreditCard(resourceCreateDto.getName());
			break;
		case CURRENT_ACCOUNT:
			resource = new ResourceCurrentAccount(resourceCreateDto.getName());
			break;
		case MORTGAGE_EXISTING:
			resource = new ResourceMortgageExisting(resourceCreateDto.getName());
			break;
		case PERSON:
			resource = new ResourcePerson(resourceCreateDto.getName());
			break;
		default:
			throw new IllegalStateException("Unknown resource type " + resourceCreateDto.getResourceType());
		}
		resource.setStartYearMonth(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(resourceCreateDto.getStartYearMonth()));
		scenario.addResource(resource);
		if (null != resourceCreateDto.getParentId()) {
			Resource parent = resourceService.findById(resourceCreateDto.getParentId()).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceCreateDto.getParentId()));
			parent.addChild(resource);
		}
		return resource;
	}
	
	private ResourceParam<?> resourceCreateResourceParamWithValueDtoToEntity(YearMonth yearMonth, ResourceCreateResourceParamWithValueDto resourceParamDto) {
		ResourceParam<?> resourceParam;
		switch (ResourceParamType.valueOf(resourceParamDto.getResourceParamType())) {
		case BIG_DECIMAL:
			resourceParam = new ResourceParamBigDecimal(ResourceParamNameEnum.getParamNameFromValue(resourceParamDto.getName()));
			break;
		case INTEGER_NEGATIVE:
			resourceParam = new ResourceParamIntegerNegative(ResourceParamNameEnum.getParamNameFromValue(resourceParamDto.getName()));
			break;
		case INTEGER_POSITIVE:
			resourceParam = new ResourceParamIntegerPositive(ResourceParamNameEnum.getParamNameFromValue(resourceParamDto.getName()));
			break;
		case STRING:
			resourceParam = new ResourceParamString(ResourceParamNameEnum.getParamNameFromValue(resourceParamDto.getName()));
			break;
		case YEAR_MONTH:
			resourceParam = new ResourceParamYearMonth(ResourceParamNameEnum.getParamNameFromValue(resourceParamDto.getName()));
			break;
		default:
			throw new IllegalStateException("Unknown resource param type " + resourceParamDto.getResourceParamType());
		}
		resourceParam.setUserAbleToCreateNewDateValue(resourceParamDto.isUserAbleToCreateNewDateValue());
		resourceParam.addResourceParamDateValueGeneric(resourceParamDateValueToEntity(yearMonth, resourceParamDto));
		return resourceParam;
	}

	private ResourceParamDateValue<?> resourceParamDateValueToEntity(YearMonth yearMonth, ResourceCreateResourceParamWithValueDto resourceParamDto) {
		ResourceParamDateValue<?> rpdv;
		switch (ResourceParamType.valueOf(resourceParamDto.getResourceParamType())) {
		case BIG_DECIMAL:
			rpdv = new ResourceParamDateValueBigDecimal(yearMonth, false, new BigDecimal(resourceParamDto.getValue()));
			break;
		case INTEGER_NEGATIVE:
			rpdv = new ResourceParamDateValueIntegerNegative(yearMonth, false, Integer.valueOf(resourceParamDto.getValue()));
			break;
		case INTEGER_POSITIVE:
			rpdv = new ResourceParamDateValueIntegerPositive(yearMonth, false, Integer.valueOf(resourceParamDto.getValue()));
			break;
		case STRING:
			rpdv = new ResourceParamDateValueString(yearMonth, false, resourceParamDto.getValue());
			break;
		case YEAR_MONTH:
			rpdv = new ResourceParamDateValueYearMonth(yearMonth, false, YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(resourceParamDto.getValue()));
			break;
		default:
			throw new IllegalStateException("Unknown resource param type " + resourceParamDto.getResourceParamType());
		}
		return rpdv;
	}

	private Cashflow resourceCreateCashflowWithValueDtoToEntity(YearMonth yearMonth, ResourceCreateCashflowWithValueDto cashflowDto) {
		Cashflow cashflow = new Cashflow(CashflowCategory.getCategory(cashflowDto.getCategory()),
										CashflowFrequency.getCashflowFrequency(cashflowDto.getFrequency()),
										cashflowDto.isCpiAffected());
		cashflow.addCashflowDateRangeValue(cashflowDateRangeValueToEntity(yearMonth, cashflowDto));
		return cashflow;
	}

	private CashflowDateRangeValue cashflowDateRangeValueToEntity(YearMonth yearMonth, ResourceCreateCashflowWithValueDto cashflowDto) {
		CashflowType cashflowType = CashflowCategory.getCategory(cashflowDto.getCategory()).getType();
		return new CashflowDateRangeValue(cashflowType, yearMonth, YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cashflowDto.getYearMonthEnd()), cashflowDto.getValue());
	}
}
