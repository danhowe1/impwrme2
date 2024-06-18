package com.impwrme2.model.resource;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
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

public class ScenarioDeepCopy {

	public static Scenario copyScenario(Scenario existingScenario, String newName) {
		ResourceScenario newResourceScenario = copyResourceScenario(existingScenario.getResourceScenario());
		newResourceScenario.setName(newName);
		Scenario newScenario = new Scenario(newResourceScenario, existingScenario.getUserId());
		
		// Add resources.
		for (Resource existingResource : existingScenario.getResources()) {
			if (null == existingResource.getParent() && !(existingResource instanceof ResourceScenario)) {
				// Only need resources at the top of the tree as the rest will get created recursively.
				@SuppressWarnings("unused")
				Resource newResource = copyResource(existingResource, newScenario);
//				newScenario.addResource(copyResource(existingResource, newScenario));
			}
		}

		return newScenario;
	}
	
	private static ResourceScenario copyResourceScenario(ResourceScenario existingResourceScenario) {
		ResourceScenario newResourceScenario = new ResourceScenario(existingResourceScenario.getName());
		newResourceScenario.setStartYearMonth(YearMonth.of(existingResourceScenario.getStartYearMonth().getYear(), existingResourceScenario.getStartYearMonth().getMonth()));

		// Add resource params.
		for (ResourceParam<?> existingResourceParam : existingResourceScenario.getResourceParams()) {
			newResourceScenario.addResourceParam(copyResourceParam(existingResourceParam));
		}

		return newResourceScenario;
	}
	
	private static Resource copyResource(Resource existingResource, Scenario newScenario) {
		Resource newResource = createResource(existingResource);
		newScenario.addResource(newResource);
		newResource.setStartYearMonth(YearMonth.of(existingResource.getStartYearMonth().getYear(), existingResource.getStartYearMonth().getMonth()));

		// Add resource params.
		for (ResourceParam<?> existingResourceParam : existingResource.getResourceParams()) {
			newResource.addResourceParam(copyResourceParam(existingResourceParam));
		}
		
		// Add cash-flows.
		for (Cashflow existingCashflow : existingResource.getCashflows()) {
			newResource.addCashflow(copyCashflow(existingCashflow));
		}
		
		// Add child resources.
		for (Resource resourceChild : existingResource.getChildren()) {
			newResource.addChild(copyResource(resourceChild, newScenario));
		}

		return newResource;
	}

	private static ResourceParam<?> copyResourceParam(ResourceParam<?> existingResourceParam) {
		ResourceParam<?> newResourceParam = createResourceParam(existingResourceParam);
		newResourceParam.setUserAbleToCreateNewDateValue(existingResourceParam.isUserAbleToCreateNewDateValue());

		// Add resource param date values.
		for (ResourceParamDateValue<?> existingRpdv : existingResourceParam.getResourceParamDateValues()) {
			newResourceParam.addResourceParamDateValueGeneric(copyResourceParamDateValue(existingRpdv));
		}
		
		return newResourceParam;
	}

	private static ResourceParamDateValue<?> copyResourceParamDateValue(ResourceParamDateValue<?> existingRpdv) {
		ResourceParamDateValue<?> newRpdv = createResourceParamDateValue(existingRpdv);
		return newRpdv;
	}

	private static Cashflow copyCashflow(Cashflow existingCashflow) {
		Cashflow newCashflow = new Cashflow(
				existingCashflow.getCategory(), 
				existingCashflow.getDetail(), 
				existingCashflow.getFrequency(),
				existingCashflow.getCpiAffected()
				);

		// Add cash-flow date range values.
		for (CashflowDateRangeValue existingCfdrv : existingCashflow.getCashflowDateRangeValues()) {
			newCashflow.addCashflowDateRangeValue(copyCashflowDateRangeValue(existingCfdrv));
		}

		return newCashflow;
	}

	private static CashflowDateRangeValue copyCashflowDateRangeValue(CashflowDateRangeValue existingCfdrv) {
		CashflowDateRangeValue newCfdrv = new CashflowDateRangeValue(
				existingCfdrv.getCashflow().getCategory().getType(), 
				YearMonth.of(existingCfdrv.getYearMonthStart().getYear(), existingCfdrv.getYearMonthStart().getMonth()),
				null != existingCfdrv.getYearMonthEnd() ? YearMonth.of(existingCfdrv.getYearMonthEnd().getYear(), existingCfdrv.getYearMonthEnd().getMonth()) : null,
				existingCfdrv.getValue());
		return newCfdrv;
	}

	private static Resource createResource(Resource existingResource) {
		if (existingResource.getResourceType().equals(ResourceType.CURRENT_ACCOUNT)) {
			return new ResourceCurrentAccount(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.CREDIT_CARD)) {
			return new ResourceCreditCard(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.HOUSEHOLD)) {
			return new ResourceHousehold(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.MORTGAGE)) {
			return new ResourceMortgage(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.MORTGAGE_OFFSET_ACCOUNT)) {
			return new ResourceMortgageOffset(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.PERSON)) {
			return new ResourcePerson(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.PROPERTY_EXISTING)) {
			return new ResourcePropertyExisting(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.PROPERTY_NEW)) {
			return new ResourcePropertyNew(existingResource.getName());
		}  else if (existingResource.getResourceType().equals(ResourceType.SAVINGS_ACCOUNT)) {
			return new ResourceSavingsAccount(existingResource.getName());
		}  else if (existingResource.getResourceType().equals(ResourceType.SCENARIO)) {
			return new ResourceScenario(existingResource.getName());
		}  else if (existingResource.getResourceType().equals(ResourceType.SHARES)) {
			return new ResourceShares(existingResource.getName());
		} else if (existingResource.getResourceType().equals(ResourceType.SUPERANNUATION)) {
			return new ResourceSuperannuation(existingResource.getName());
		}
		throw new IllegalStateException("Unknown resource type " + existingResource.getResourceType().getValue() + ".");
	}

	private static ResourceParam<?> createResourceParam(ResourceParam<?> existingResourceParam) {
		if (existingResourceParam.getResourceParamType().equals(ResourceParamType.BIG_DECIMAL)) {
			return new ResourceParamBigDecimal(existingResourceParam.getName());
		} else if (existingResourceParam.getResourceParamType().equals(ResourceParamType.INTEGER_NEGATIVE)) {
			return new ResourceParamIntegerNegative(existingResourceParam.getName());
		} else if (existingResourceParam.getResourceParamType().equals(ResourceParamType.INTEGER_POSITIVE)) {
			return new ResourceParamIntegerPositive(existingResourceParam.getName());
		} else if (existingResourceParam.getResourceParamType().equals(ResourceParamType.STRING)) {
			return new ResourceParamString(existingResourceParam.getName());
		} else if (existingResourceParam.getResourceParamType().equals(ResourceParamType.YEAR_MONTH)) {
			return new ResourceParamYearMonth(existingResourceParam.getName());
		}
		throw new IllegalStateException("Unknown resource type " + existingResourceParam.getResourceParamType().getValue() + ".");
	}

	private static ResourceParamDateValue<?> createResourceParamDateValue(ResourceParamDateValue<?> existingRpdv) {
		YearMonth newYearMonth = YearMonth.of(existingRpdv.getYearMonth().getYear(), existingRpdv.getYearMonth().getMonth());
		if (existingRpdv instanceof ResourceParamDateValueBigDecimal) {
			return new ResourceParamDateValueBigDecimal(newYearMonth, existingRpdv.isUserAbleToChangeDate(), new BigDecimal(existingRpdv.getValue().toString()));
		} else if (existingRpdv instanceof ResourceParamDateValueIntegerNegative) {
			return new ResourceParamDateValueIntegerNegative(newYearMonth, existingRpdv.isUserAbleToChangeDate(), Integer.valueOf(existingRpdv.getValue().toString()));
		} else if (existingRpdv instanceof ResourceParamDateValueIntegerPositive) {
			return new ResourceParamDateValueIntegerPositive(newYearMonth, existingRpdv.isUserAbleToChangeDate(), Integer.valueOf(existingRpdv.getValue().toString()));
		} else if (existingRpdv instanceof ResourceParamDateValueString) {
			return new ResourceParamDateValueString(newYearMonth, existingRpdv.isUserAbleToChangeDate(), existingRpdv.getValue().toString());
		} else if (existingRpdv instanceof ResourceParamDateValueYearMonth) {
			YearMonth yearMonth = (YearMonth) existingRpdv.getValue();
			return new ResourceParamDateValueYearMonth(newYearMonth, existingRpdv.isUserAbleToChangeDate(), YearMonth.of(yearMonth.getYear(), yearMonth.getMonth()));
		}
		throw new IllegalStateException("Unknown resource param date value type " + existingRpdv.getResourceParam().getResourceParamType().getValue() + ".");
	}
}
