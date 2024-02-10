package com.impwrme2.model.journalEntry;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.service.engine.IResourceEngine;

public class ResourceMonthlyAmounts {

	public ResourceMonthlyAmounts(final IResourceEngine resourceEngine, final YearMonth yearMonth) {
		this.resource = resourceEngine.getResource();
		this.balanceLiquidLegalMin = resourceEngine.getBalanceLiquidLegalMin(yearMonth);
	}
	
	private final Resource resource;

	private final BigDecimal balanceLiquidLegalMin;

	private BigDecimal liquidPotAmount = BigDecimal.valueOf(0);
	
	private BigDecimal liquidDepositsAmount = BigDecimal.valueOf(0);
	
	private BigDecimal fixedAmount = BigDecimal.valueOf(0);

	public BigDecimal getLiquidBalance( ) {
		return getLiquidDepositsAmount().add(getLiquidPotAmount()).add(balanceLiquidLegalMin);
	}
	
	public BigDecimal getAssetValue( ) {
		return getFixedAmount().add(getLiquidBalance());
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

	public BigDecimal getLiquidPotAmount() {
		return liquidPotAmount;
	}

	public BigDecimal getLiquidDepositsAmount() {
		return liquidDepositsAmount;
	}
	
	public void addToLiquidDepositsAmount(BigDecimal amount) {
		this.liquidDepositsAmount = this.liquidDepositsAmount.add(amount);
	}

	public BigDecimal getFixedAmount() {
		return fixedAmount;
	}
	
	public void addToFixedAmount(BigDecimal amount) {
		this.fixedAmount = this.fixedAmount.add(amount);
	}
}
