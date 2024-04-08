package com.impwrme2.service.journalEntry;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.impwrme2.model.journalEntry.ResourcePotAmounts;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.engine.IResourceEngine;

@Component
public class PotManagerService {

//	private Scenario scenario;
	private Map<String, ResourcePotAmounts> resourceMonthlyAmountsMap;
	private Integer potBalance = 0;
	
	public void initialise(final Scenario scenario) {
//		this.scenario = scenario;
		this.resourceMonthlyAmountsMap = new HashMap<String, ResourcePotAmounts>();
		this.potBalance = 0;
	}	
	
	public void addToPotBalance(Integer amount) {
		potBalance = potBalance + amount;
	}
	
	public void subtractFromPotBalance(Integer amount) {
		potBalance = potBalance - amount;
	}
	
	public Integer getPotBalance() {
		return potBalance;
	}
	
	public Integer getResourceLiquidPotAmount(final IResourceEngine resourceEngine) {
		return getResourceMonthlyBalances(resourceEngine).getLiquidPotAmount();
	}
	
	public void addToResourceLiquidPotAmount(final IResourceEngine resourceEngine, final Integer amount) {
		getResourceMonthlyBalances(resourceEngine).addToLiquidPotAmount(amount);
	}

	public void subtractFromResourceLiquidPotAmount(final IResourceEngine resourceEngine, final Integer amount) {
		getResourceMonthlyBalances(resourceEngine).subtractFromLiquidPotAmount(amount);
	}

	public Integer getResourceLiquidDepositsAmount(final IResourceEngine resourceEngine) {
		return getResourceMonthlyBalances(resourceEngine).getLiquidDepositsAmount();
	}
	
	public void subtractFromResourceLiquidDepositsAmount(final IResourceEngine resourceEngine, final Integer amount) {
		getResourceMonthlyBalances(resourceEngine).subtractFromLiquidDepositsAmount(amount);
	}

	public void addToResourceLiquidDepositsAmount(final IResourceEngine resourceEngine, final Integer amount) {
		getResourceMonthlyBalances(resourceEngine).addToLiquidDepositsAmount(amount);
	}

	public Integer getResourceLiquidBalance(final IResourceEngine resourceEngine, final YearMonth yearMonth) {
		return getResourceMonthlyBalances(resourceEngine).getLiquidBalance();
	}
	
	public Integer getResourceAssetValue(final IResourceEngine resourceEngine, final YearMonth yearMonth) {
		return getResourceMonthlyBalances(resourceEngine).getAssetValue();
	}
	
	public void addToResourceFixedAmount(final IResourceEngine resourceEngine, final Integer amount) {
		getResourceMonthlyBalances(resourceEngine).addToFixedAmount(amount);
	}

	private ResourcePotAmounts getResourceMonthlyBalances(final IResourceEngine resourceEngine) {
		ResourcePotAmounts resourceMonthlyBalances = resourceMonthlyAmountsMap.get(resourceMonthlyAmountsKey(resourceEngine.getResource()));
		if (null == resourceMonthlyBalances) {
			resourceMonthlyBalances = new ResourcePotAmounts(resourceEngine);
			resourceMonthlyAmountsMap.put(resourceMonthlyAmountsKey(resourceEngine.getResource()), resourceMonthlyBalances);
		}
		return resourceMonthlyBalances;
	}
	
	private String resourceMonthlyAmountsKey(Resource resource) {
		return resource.getName();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$" + 45 + "s", "liquidPotAmount"));
		sb.append(String.format("%1$" + 25 + "s", "liquidDepositsAmount"));
		sb.append(String.format("%1$" + 26 + "s", "fixedAmount\n"));
		for (ResourcePotAmounts monthlyAmounts : resourceMonthlyAmountsMap.values()) {
			sb.append(monthlyAmounts.toString());
		}
		sb.append(String.format("%1$-" + 20 + "s", "Total Pot Balance"));
		sb.append(String.format("%1$" + 25 + "s", potBalance) + "\n");
		sb.append("Pot Balances " + "----------------------------------------------------------------------------------\n");
		return sb.toString();
	}
}
