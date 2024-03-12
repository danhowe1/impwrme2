package com.impwrme2.model.journalEntry;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.service.engine.IResourceEngine;

public class ResourcePotAmounts {

	public ResourcePotAmounts(final IResourceEngine resourceEngine) {
		this.resource = resourceEngine.getResource();
	}
	
	private final Resource resource;

	private Integer liquidPotAmount = 0;
	
	private Integer liquidDepositsAmount = 0;
	
	private Integer fixedAmount = 0;

	public Integer getLiquidBalance( ) {
		return getLiquidDepositsAmount() + getLiquidPotAmount();
	}
	
	public Integer getAssetValue( ) {
		return getFixedAmount() + getLiquidBalance();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$-" + 20 + "s", resource.getName()));
		sb.append(String.format("%1$" + 25 + "s", liquidPotAmount));
		sb.append(String.format("%1$" + 25 + "s", liquidDepositsAmount));
		sb.append(String.format("%1$" + 25 + "s", fixedAmount)).append("\n");
		return sb.toString();
	}
	
	//-------------------
	// Getters & setters.
	//-------------------

	public Resource getResource() {
		return resource;
	}

	public Integer getLiquidPotAmount() {
		return liquidPotAmount;
	}

	public void addToLiquidPotAmount(Integer amount) {
		this.liquidPotAmount = this.liquidPotAmount +amount;
	}

	public void subtractFromLiquidPotAmount(Integer amount) {
		this.liquidPotAmount = this.liquidPotAmount - amount;
	}

	public Integer getLiquidDepositsAmount() {
		return liquidDepositsAmount;
	}
	
	public void addToLiquidDepositsAmount(Integer amount) {
		this.liquidDepositsAmount = this.liquidDepositsAmount + amount;
	}

	public void subtractFromLiquidDepositsAmount(Integer amount) {
		this.liquidDepositsAmount = this.liquidDepositsAmount - amount;
	}

	public Integer getFixedAmount() {
		return fixedAmount;
	}
	
	public void addToFixedAmount(Integer amount) {
		this.fixedAmount = this.fixedAmount + amount;
	}
}
