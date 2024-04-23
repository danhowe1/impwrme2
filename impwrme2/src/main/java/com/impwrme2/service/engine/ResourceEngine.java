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
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public abstract class ResourceEngine implements IResourceEngine {

	private static final MathContext SCALE_4_ROUNDING_HALF_EVEN =new MathContext(4,RoundingMode.HALF_EVEN);
	private static final BigDecimal HUNDRED = new BigDecimal("100");
	private static final MathContext SCALE_10_ROUNDING_HALF_EVEN =new MathContext(10,RoundingMode.HALF_EVEN);
	private static final double POWER_OF_ONE_TWELFTH = BigDecimal.ONE.divide(new BigDecimal("12"), SCALE_10_ROUNDING_HALF_EVEN).doubleValue();

	protected final Resource resource;
	private Optional<Integer> balanceLiquidLegalMaxConstant = Optional.empty();
	private Optional<Integer> balanceLiquidLegalMinConstant = Optional.empty();
	private Optional<Integer> balanceLiquidPreferredMaxConstant = Optional.empty();
	private Optional<Integer> balanceLiquidPreferredMinConstant = Optional.empty();
	
	/**
	 * No-args constructor required for Spring instantiation.
	 */
	protected ResourceEngine() {
		this.resource = null;
	}

	public ResourceEngine(final Resource resource) {
		this.resource = resource;
		
		// Set the initial balances because the get methods for these may not get called in month 1 and therefore never be set.
		
		Optional<ResourceParam<?>> rpLegalMaxOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX);
		if (rpLegalMaxOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidLegalMaxConstant =  Optional.of(getBalanceLiquidLegalMaxIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpLegalMaxOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpLegalMaxOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidLegalMaxConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpLegalMinOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
		if (rpLegalMinOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidLegalMinConstant =  Optional.of(getBalanceLiquidLegalMinIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpLegalMinOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpLegalMinOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidLegalMinConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpPreferredMaxOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX);
		if (rpPreferredMaxOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidPreferredMaxConstant =  Optional.of(getBalanceLiquidPreferredMaxIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpPreferredMaxOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpPreferredMaxOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidPreferredMaxConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpPreferredMinOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN);
		if (rpPreferredMinOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidPreferredMinConstant =  Optional.of(getBalanceLiquidPreferredMinIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpPreferredMinOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpPreferredMinOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidPreferredMinConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}
		
	}

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {
		return generateJournalEntriesFromCashflows(yearMonth, getResource().getCashflows());
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public final Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		if (balanceLiquidLegalMaxConstant.isPresent()) {
			return balanceLiquidLegalMaxConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidLegalMaxIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		if (balanceLiquidLegalMinConstant.isPresent()) {
			return balanceLiquidLegalMinConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidLegalMinIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		if (balanceLiquidPreferredMaxConstant.isPresent()) {
			return balanceLiquidPreferredMaxConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidPreferredMaxIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		if (balanceLiquidPreferredMinConstant.isPresent()) {
			return balanceLiquidPreferredMinConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidPreferredMinIfNotSpecified();
	}

	@Override
	public int compareTo(IResourceEngine o) {
		return this.getResource().compareTo(o.getResource());
	}

	protected List<JournalEntry> generateJournalEntriesFromCashflows(final YearMonth currentYearMonth, final List<Cashflow> cashflows) {
		if (currentYearMonth.isBefore(getResource().getStartYearMonth())) {
			return Collections.emptyList();
		}

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (Cashflow cashflow : cashflows) {
			Optional<CashflowDateRangeValue> cfdrvOpt = cashflow.getCashflowDateRangeValue(currentYearMonth);
			if (cfdrvOpt.isPresent()) {
				CashflowDateRangeValue cfdrv = cfdrvOpt.get();
				if (cfdrv.getValue().intValue() != 0 && isCashflowDue(cfdrv, currentYearMonth)) {
					Integer amount = calculateAmountWithCpi(cfdrv, currentYearMonth);
					if (amount != 0) {
						journalEntries.add(JournalEntryFactory.create(resource, currentYearMonth, amount, cashflow.getCategory(), cashflow.getDetail()));
					}
				}
			}
		}
		return journalEntries;
	}

	protected BigDecimal calculateMonthlyGrowthRateFromAnnualGrowthRate(BigDecimal annualGrowthRate) {
		BigDecimal result = annualGrowthRate.divide(HUNDRED, SCALE_4_ROUNDING_HALF_EVEN);
		result = result.add(BigDecimal.ONE);
		result = BigDecimal.valueOf(Math.pow(result.doubleValue(), POWER_OF_ONE_TWELFTH));
		return result.subtract(BigDecimal.ONE);
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

	private Integer calculateAmountWithCpi(CashflowDateRangeValue cfdrv, YearMonth yearMonth) {
		BigDecimal amount = BigDecimal.valueOf(cfdrv.getValue());
		if (cfdrv.getCashflow().getCpiAffected()) {
			BigDecimal cpi = cfdrv.getCashflow().getResource().getScenario().getResourceScenario().getCpi().divide(HUNDRED, SCALE_4_ROUNDING_HALF_EVEN);
			cpi = cpi.add(BigDecimal.ONE);
			int yearsOfCpi = yearMonth.getYear() - cfdrv.getYearMonthStart().getYear();
			amount = amount.multiply(cpi.pow(yearsOfCpi));
		}
		return integerOf(amount);
	}

	protected Integer integerOf(BigDecimal decimal) {
		return decimal.setScale(0, RoundingMode.HALF_UP).intValue();
	}
}
