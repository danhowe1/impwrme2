package com.impwrme2.service.journalEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.impwrme2.model.resource.Resource;

@Component
public class BalanceTracker {

	private Integer potBalance;
	private Map<Resource, Integer> resourceLiquidAmount;
	private Map<Resource, Integer> resourceLiquidDepositAmount;
	private Map<Resource, Integer> resourceFixedAmount;

	public void initialise(final Set<Resource> resources) {
		potBalance = 0;
		resourceLiquidAmount = new HashMap<Resource, Integer>();
		resourceLiquidDepositAmount = new HashMap<Resource, Integer>();
		resourceFixedAmount = new HashMap<Resource, Integer>();
		for (Resource resource : resources) {
			resourceLiquidAmount.put(resource, Integer.valueOf(0));
			resourceLiquidDepositAmount.put(resource, Integer.valueOf(0));
			resourceFixedAmount.put(resource, Integer.valueOf(0));
		}
	}
	
	// ----
	// Pot.
	// ----
	
	public Integer getPotBalance() {
		return potBalance;
	}

	public void addToPotBalance(Integer amount) {
		potBalance = potBalance + Math.abs(amount);
	}
	
	public void subtractFromPotBalance(Integer amount) {
		potBalance = potBalance - Math.abs(amount);
	}

	// ------------------
	// Resource BALANCES.
	// ------------------
	
	public Integer getResourceLiquidBalance(Resource resource) {
		return getResourceLiquidAmount(resource) + getResourceLiquidDepositAmount(resource);
	}

	public Integer getResourceAssetValue(Resource resource) {
		return getResourceLiquidBalance(resource) + getResourceFixedAmount(resource);
	}

	// -----------------------
	// Resource liquid AMOUNT.
	// -----------------------
	
	public Integer getResourceLiquidAmount(Resource resource) {
		return resourceLiquidAmount.get(resource);
	}
	
	public void addToResourceLiquidAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceLiquidAmount.get(resource);
		currentAmount = currentAmount + Math.abs(amount);
		resourceLiquidAmount.put(resource, currentAmount);
	}

	public void subtractFromResourceLiquidAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceLiquidAmount.get(resource);
		currentAmount = currentAmount - Math.abs(amount);
		resourceLiquidAmount.put(resource, currentAmount);
	}

	// -------------------------------
	// Resource liquid deposit AMOUNT.
	// -------------------------------
	
	public Integer getResourceLiquidDepositAmount(Resource resource) {
		return resourceLiquidDepositAmount.get(resource);
	}
	
	public void addToResourceLiquidDepositAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceLiquidDepositAmount.get(resource);
		currentAmount = currentAmount + Math.abs(amount);
		resourceLiquidDepositAmount.put(resource, currentAmount);
	}

	public void subtractFromResourceLiquidDepositAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceLiquidDepositAmount.get(resource);
		currentAmount = currentAmount - Math.abs(amount);
		resourceLiquidDepositAmount.put(resource, currentAmount);
	}

	// ----------------------
	// Resource fixed AMOUNT.
	// ----------------------
	
	public Integer getResourceFixedAmount(Resource resource) {
		return resourceFixedAmount.get(resource);
	}

	public void addToResourceFixedAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceFixedAmount.get(resource);
		currentAmount = currentAmount + Math.abs(amount);
		resourceFixedAmount.put(resource, currentAmount);
	}

	public void subtractFromResourceFixedAmount(Resource resource, Integer amount) {
		Integer currentAmount = resourceFixedAmount.get(resource);
		currentAmount = currentAmount - Math.abs(amount);
		resourceFixedAmount.put(resource, currentAmount);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$" + 45 + "s", "liquidPotAmount"));
		sb.append(String.format("%1$" + 25 + "s", "liquidDepositsAmount"));
		sb.append(String.format("%1$" + 26 + "s", "fixedAmount\n"));
		for (Resource resource : resourceFixedAmount.keySet()) {
			sb.append(String.format("%1$-" + 20 + "s", resource.getName()));
			sb.append(String.format("%1$" + 25 + "s", resourceLiquidAmount.get(resource)));
			sb.append(String.format("%1$" + 25 + "s", resourceLiquidDepositAmount.get(resource)));
			sb.append(String.format("%1$" + 25 + "s", resourceFixedAmount.get(resource))).append("\n");			
		}		
		sb.append(String.format("%1$-" + 20 + "s", "Total Pot Balance"));
		sb.append(String.format("%1$" + 25 + "s", potBalance) + "\n");
		sb.append("Pot Balances " + "----------------------------------------------------------------------------------\n");
		return sb.toString();
	}
}
