package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflow.CashflowType;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.JournalEntryFactory;
import com.impwrme2.service.journalEntry.PotManagerService;

public class ResourceEngine implements IResourceEngine {

	private static final MathContext SCALE_4_ROUNDING_HALF_EVEN =new MathContext(4,RoundingMode.HALF_EVEN);
	private static final BigDecimal HUNDRED = new BigDecimal("100");

	protected final Resource resource;
	private BigDecimal balanceLiquidLegalMax = BigDecimal.ZERO;
	private BigDecimal balanceLiquidLegalMin = BigDecimal.ZERO;
	
	/**
	 * No-args constructor required for Spring instantiation.
	 */
	protected ResourceEngine() {
		this.resource = null;
	}

	public ResourceEngine(final Resource resource) {
		this.resource = resource;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public BigDecimal getBalanceLiquidLegalMax(YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidLegalMax = BigDecimal.valueOf((Integer) rpdvOpt.get().getValue());
		}
		return balanceLiquidLegalMax;
	}

	@Override
	public BigDecimal getBalanceLiquidLegalMin(YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidLegalMin = BigDecimal.valueOf((Integer) rpdvOpt.get().getValue());
		}
		return balanceLiquidLegalMin;
	}

	@Override
	public BigDecimal getBalanceLiquidPreferred(YearMonth yearMonth) {
		return BigDecimal.ZERO;
	}

	@Override
	public final List<JournalEntry> generateJournalEntriesForExpensesIncomeAndWithdrawals(PotManagerService potManager) {
		
		if (potManager.getCurrentYearMonth().isBefore(resource.getStartYearMonth())) {
			return Collections.emptyList();
		}

		// Generate cashflows.
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (Cashflow cashflow : resource.getCashflows()) {
			Optional<CashflowDateRangeValue> cfdrvOpt = cashflow.getCashflowDateRangeValue(potManager.getCurrentYearMonth());
			if (cfdrvOpt.isPresent()) {
				CashflowDateRangeValue cfdrv = cfdrvOpt.get();
				if (cfdrv.getValue().intValue() != 0 && isCashflowDue(cfdrv, potManager.getCurrentYearMonth())) {
					if (cfdrv.getCashflow().getCategory().getType().equals(CashflowType.EXPENSE) ||
						cfdrv.getCashflow().getCategory().getType().equals(CashflowType.INCOME) ||
						cfdrv.getCashflow().getCategory().getType().equals(CashflowType.WITHDRAWAL)) {
						BigDecimal amount = calculateAmountWithCpi(cfdrv, potManager.getCurrentYearMonth());
						journalEntries.add(JournalEntryFactory.create(resource, potManager.getCurrentYearMonth(), amount, cfdrv.getCashflow().getCategory()));
					}
				}
			}
		}
		return journalEntries;
	}

	private boolean isCashflowDue(CashflowDateRangeValue cfdrv, YearMonth currentYearMonth) {
		if (cfdrv.getCashflow().getFrequency().equals(CashflowFrequency.MONTHLY)) {
			return true;
		} else if (cfdrv.getCashflow().getFrequency().equals(CashflowFrequency.QUARTERLY)) {
			int monthsDiff = Math.abs(cfdrv.getYearMonthStart().getMonthValue() - currentYearMonth.getMonthValue());
			if (Math.floorMod(monthsDiff, 3) == 0) {
				return true;
			}
		} else if (cfdrv.getCashflow().getFrequency().equals(CashflowFrequency.ANNUALLY)) {
			if (cfdrv.getYearMonthStart().getMonthValue() == currentYearMonth.getMonthValue()) {
				return true;
			}
		} else if (cfdrv.getCashflow().getFrequency().equals(CashflowFrequency.ONE_OFF)) {
			if (cfdrv.getYearMonthStart().equals(currentYearMonth)) {
				return true;
			}
		}
		return false;
	}

	public BigDecimal calculateAmountWithCpi(CashflowDateRangeValue cfdrv, YearMonth yearMonth) {
		BigDecimal amount = BigDecimal.valueOf(cfdrv.getValue());
		if (cfdrv.getCashflow().getCpiAffected()) {
			BigDecimal cpi = getResource().getScenario().getResourceScenario().getCpi().divide(HUNDRED, SCALE_4_ROUNDING_HALF_EVEN);
			cpi = cpi.add(BigDecimal.ONE);
			int yearsOfCpi = yearMonth.getYear() - cfdrv.getYearMonthStart().getYear();
			amount = amount.multiply(cpi.pow(yearsOfCpi));
		}
		return amount;
	}
}
