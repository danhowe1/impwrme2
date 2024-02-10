package com.impwrme2.service.journalEntry;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.impwrme2.model.journalEntry.ResourceMonthlyAmounts;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.engine.IResourceEngine;

@Component
public class PotManagerService {

	private Scenario scenario;
	private	YearMonth currentYearMonth;
	private Map<String, ResourceMonthlyAmounts> resourceMonthlyAmountsMap = new HashMap<String, ResourceMonthlyAmounts>();
	
	public void initialise(final Scenario scenario) {
		this.scenario = scenario;
		this.currentYearMonth = scenario.getResourceScenario().getStartYearMonth();
	}	
	
	public YearMonth getCurrentYearMonth() {
		return currentYearMonth;
	}

	public void processEndOfMonth() {
		currentYearMonth = currentYearMonth.plusMonths(1);
	}

	public BigDecimal getLiquidBalance(final Resource resource, final YearMonth yearMonth) {
		return resourceMonthlyAmountsMap.get(resourceMonthlyAmountsKey(resource, yearMonth)).getLiquidBalance();
	}
	
	public void addToLiquidDepositsAmount(final IResourceEngine resourceEngine, final YearMonth yearMonth, final BigDecimal amount) {
		ResourceMonthlyAmounts resourceMonthlyBalances = resourceMonthlyAmountsMap.get(resourceMonthlyAmountsKey(resourceEngine.getResource(), yearMonth));
		if (null == resourceMonthlyBalances) {
			resourceMonthlyBalances = new ResourceMonthlyAmounts(resourceEngine, yearMonth);
			resourceMonthlyAmountsMap.put(resourceMonthlyAmountsKey(resourceEngine.getResource(), yearMonth), resourceMonthlyBalances);
		}
		resourceMonthlyBalances.addToLiquidDepositsAmount(amount);
	}

	public BigDecimal getAssetValue(final Resource resource, final YearMonth yearMonth) {
		return resourceMonthlyAmountsMap.get(resourceMonthlyAmountsKey(resource, yearMonth)).getAssetValue();
	}
	
	public void addToFixedAmount(final IResourceEngine resourceEngine, final YearMonth yearMonth, final BigDecimal amount) {
		ResourceMonthlyAmounts resourceMonthlyBalances = resourceMonthlyAmountsMap.get(resourceMonthlyAmountsKey(resourceEngine.getResource(), yearMonth));
		if (null == resourceMonthlyBalances) {
			resourceMonthlyBalances = new ResourceMonthlyAmounts(resourceEngine, yearMonth);
			resourceMonthlyAmountsMap.put(resourceMonthlyAmountsKey(resourceEngine.getResource(), yearMonth), resourceMonthlyBalances);
		}
		resourceMonthlyBalances.addToFixedAmount(amount);
	}

//	private void initialiseResourceMonthlyAmounts() {
//		for (Resource resource : scenario.getResources()) {
//			
//			resourceMonthlyAmountsMap.put(resourceMonthlyAmountsKey(resource, resource.getStartYearMonth()), new ResourceMonthlyAmounts(resource));
//			
//			Optional<ResourceParam<?>> liquidAmountOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_OPENING_LIQUID);
//			if (liquidAmountOpt.isPresent()) {
//				ResourceParam<?> rpLiquidDeposit = liquidAmountOpt.get();
//				Integer liquidDepositAmount = (Integer) rpLiquidDeposit.getResourceParamDateValue().getValue();
//				addToLiquidDepositsAmount(resource, resource.getStartYearMonth(), BigDecimal.valueOf(liquidDepositAmount.intValue()));
//			}
//			
//			Optional<ResourceParam<?>> fixedAmountOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_OPENING_FIXED);
//			if (fixedAmountOpt.isPresent()) {
//				ResourceParam<?> rpFixed = fixedAmountOpt.get();
//				Integer fixedAmount = (Integer) rpFixed.getResourceParamDateValue().getValue();
//				addToFixedAmount(resource, resource.getStartYearMonth(), BigDecimal.valueOf(fixedAmount.intValue()));
//			}
//		}
//	}
	
//	private String resourceMonthlyAmountsKey(Resource resource) {
//		return resourceMonthlyAmountsKey(resource, resource.getStartYearMonth());
//	}

	private String resourceMonthlyAmountsKey(Resource resource, YearMonth yearMonth) {
		return yearMonth.toString() + resource.getName();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$-" + 20 + "s", currentYearMonth.toString() + " Pot Balances"));
		sb.append(String.format("%1$" + 25 + "s", "liquidPotAmount"));
		sb.append(String.format("%1$" + 25 + "s", "liquidDepositsAmount"));
		sb.append(String.format("%1$" + 26 + "s", "fixedAmount\n"));
		for (ResourceMonthlyAmounts monthlyAmounts : resourceMonthlyAmountsMap.values()) {
			sb.append(monthlyAmounts.toString());
		}
		sb.append(currentYearMonth.toString() + " Pot Balances " + "--------------------------------------------------------------------------\n");
		return sb.toString();
	}
}
