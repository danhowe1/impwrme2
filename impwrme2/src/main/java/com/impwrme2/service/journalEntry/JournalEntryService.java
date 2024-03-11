package com.impwrme2.service.journalEntry;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceUnallocated;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.engine.IResourceEngine;

@Service
public class JournalEntryService {

	private static final MathContext SCALE_4_ROUNDING_HALF_EVEN =new MathContext(4,RoundingMode.HALF_EVEN);
	private static final BigDecimal HUNDRED = new BigDecimal("100");
	
	private ResourceUnallocated resourceUnallocated;

	@Autowired
	private JournalEntryResponse journalEntryResponse;

	@Autowired
	private JournalEntryFactory journalEntryFactory;
	
	@Autowired
	private PotManagerService potManager;
	
	@Autowired
	private MessageSource messageSource;

	public JournalEntryResponse run(final Scenario scenario) {

		journalEntryResponse.setJournalEntries(generateJournalEntries(scenario));
		return journalEntryResponse;
	}

	private List<JournalEntry> generateJournalEntries(final Scenario scenario) {

		potManager.initialise(scenario);
		List<IResourceEngine> resourceEngines = getResourceEngines(scenario);
		YearMonth END_DATE = scenario.calculateEndYearMonth();

		// Set up initial state.
		List<JournalEntry> totalJournalEntries = new ArrayList<JournalEntry>();

		while (!potManager.getCurrentYearMonth().isAfter(END_DATE)) {

			List<JournalEntry> currentMonthsJournalEntries = new ArrayList<JournalEntry>();

			for (IResourceEngine resourceEngine : resourceEngines) {
				
				if (potManager.getCurrentYearMonth().equals(resourceEngine.getResource().getStartYearMonth())) {
					// Generate opening balances if required.
					currentMonthsJournalEntries.add(generateJournalEntryLiquidAmountOpeningBalance(resourceEngine));
					currentMonthsJournalEntries.add(generateJournalEntryFixedAmountOpeningBalance(resourceEngine));
				} else {
					// Contribute months opening balance to the pot.
					BigDecimal balanceLiquidOpeningAmount = potManager.getLiquidPot(resourceEngine);
					potManager.addToPotBalance(balanceLiquidOpeningAmount);
					potManager.subtractFromLiquidPot(resourceEngine, potManager.getLiquidPot(resourceEngine));
//					if (balanceLiquidOpeningAmount.compareTo(BigDecimal.ZERO) > 0) {
//						// Don't contribute debt to the pot
//						potManager.subtractFromLiquidPot(resourceEngine, potManager.getLiquidPot(resourceEngine));
//					}					
				}

				
				// Generate the monthly journal entries for each Expense and Income. We don't process the
				// Deposits yet because we don't know if there'll be enough in the pot to do so. We don't
				// process the Withdrawals yet because each engine may generate auto deposits that the
				// Withdrawals could go against.
				List<JournalEntry> resourceJournalEntries = generateJournalEntriesForExpensesAndIncome(resourceEngine);
				currentMonthsJournalEntries.addAll(resourceJournalEntries);
				
				// TODO Generate engine specific journal entries.
			}

			// TODO Generate users Withdrawals.
			
			// TODO Generate users Deposits if there's enough in the pot to do so.
			
			// If the pot is negative make auto withdrawals from the resources in reverse order of importance, based on the preferred balances.
			currentMonthsJournalEntries.addAll(generateJournalEntriesForAutoWithdrawalsOnPreferredLimits(resourceEngines));
						
			// TODO If the pot is still negative make auto withdrawals from the resources in reverse order of importance, based on the legal min and max values.
			
			// If the pot is still negative take the money from the debt resources (e.g. credit cards).
			generateNegativePotDistributionToDebtResources(resourceEngines);
			
			// TODO If there's money in the pot, redistribute to the resources based on the preferred balances.
			
			// TODO If there's still money in the pot, redistribute to the resources based on the legal min and max values.
			
			// TODO Generate the intra account transfers. Do we still want these?
			
			// Generate the closing balances (including unallocated if necessary).
			currentMonthsJournalEntries.addAll(generateJournalEntriesForClosingBalances(resourceEngines));
			
			if (potManager.getCurrentYearMonth().isBefore(YearMonth.of(2024, 3))) {
				Collections.sort(currentMonthsJournalEntries);
				for (JournalEntry journalEntry : currentMonthsJournalEntries) {
					System.out.println(journalEntry.toString());
				}
				System.out.println(potManager.toString());
			}
			
			// TODO Temporarily put a sanity check here to make sure we're not creating or destroying money.
			
			// Move to the next month.
			totalJournalEntries.addAll(currentMonthsJournalEntries);
			potManager.processEndOfMonth();
		}
		
		return totalJournalEntries;
	}

	private List<IResourceEngine> getResourceEngines(Scenario scenario) {
		List<IResourceEngine> engines = new ArrayList<IResourceEngine>();
		for (Resource resource : scenario.getSortedResources()) {
			if (!(resource instanceof ResourceScenario)) {
				Class<?>[] resourceClass = { resource.getClass() };					
				String engineClassName = "com.impwrme2.service.engine." + resource.getClass().getSimpleName() + "Engine";
				try {
					Class<?> engineClass = Class.forName(engineClassName);					
					Object engine = engineClass.getDeclaredConstructor(resourceClass).newInstance(resource);
					engines.add((IResourceEngine) engine);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return engines;
	}

	private JournalEntry generateJournalEntryLiquidAmountOpeningBalance(IResourceEngine resourceEngine) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resourceEngine.getResource().getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_LIQUID, resourceEngine.getResource().getStartYearMonth());
		Integer liquidAmount = Integer.valueOf(0);
		if (rpdvOpt.isPresent()) {
			liquidAmount = (Integer) rpdvOpt.get().getValue();
			if (liquidAmount.intValue() > 0) {
				// If there are savings in the account then we assume that these have been explicitly deposited by the user.
				potManager.addToLiquidDepositsAmount(resourceEngine, BigDecimal.valueOf(liquidAmount));
			} else if (liquidAmount.intValue() < 0) {
				// If this is debt then just put the amount straight into the pot. We assume the user wants to pay this off asap.
				potManager.addToPotBalance(BigDecimal.valueOf(liquidAmount));
			}
		}
		return journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), potManager.getLiquidBalance(resourceEngine, potManager.getCurrentYearMonth()), CashflowCategory.JE_BALANCE_OPENING_LIQUID);
	}
	
	private JournalEntry generateJournalEntryFixedAmountOpeningBalance(IResourceEngine resourceEngine) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resourceEngine.getResource().getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_FIXED, resourceEngine.getResource().getStartYearMonth());
		Integer fixedAmount = Integer.valueOf(0);
		if (rpdvOpt.isPresent()) {
			fixedAmount = (Integer) rpdvOpt.get().getValue();
		}
		potManager.addToFixedAmount(resourceEngine, BigDecimal.valueOf(fixedAmount));
		return journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), potManager.getAssetValue(resourceEngine, potManager.getCurrentYearMonth()), CashflowCategory.JE_BALANCE_OPENING_ASSET_VALUE);
	}
	
	private List<JournalEntry> generateJournalEntriesForExpensesAndIncome(IResourceEngine resourceEngine) {
		
		if (potManager.getCurrentYearMonth().isBefore(resourceEngine.getResource().getStartYearMonth())) {
			return Collections.emptyList();
		}

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (Cashflow cashflow : resourceEngine.getResource().getCashflows()) {
			Optional<CashflowDateRangeValue> cfdrvOpt = cashflow.getCashflowDateRangeValue(potManager.getCurrentYearMonth());
			if (cfdrvOpt.isPresent()) {
				CashflowDateRangeValue cfdrv = cfdrvOpt.get();
				if (cfdrv.getValue().intValue() != 0 && isCashflowDue(cfdrv, potManager.getCurrentYearMonth())) {
					BigDecimal amount = calculateAmountWithCpi(cfdrv, potManager.getCurrentYearMonth());
					journalEntries.add(createExpenseOrIncome(resourceEngine.getResource(), amount, cfdrv.getCashflow().getCategory(), cashflow.getDetail()));
				}
			}
		}
		return journalEntries;
	}

	private List<JournalEntry> generateJournalEntriesForAutoWithdrawalsOnPreferredLimits(final List<IResourceEngine> resourceEngines) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (potManager.getCurrentMonthsPotBalance().compareTo(BigDecimal.ZERO) < 1) {
			Collections.reverse(resourceEngines);
			for (IResourceEngine resourceEngine : resourceEngines) {
				BigDecimal withdrawalAmount = potManager.getLiquidDeposits(resourceEngine);
				BigDecimal balancePreferredMin = resourceEngine.getBalanceLiquidPreferredMin(potManager.getCurrentYearMonth());
				withdrawalAmount = withdrawalAmount.subtract(balancePreferredMin);
				withdrawalAmount = BigDecimal.valueOf(Math.min(withdrawalAmount.doubleValue(), potManager.getCurrentMonthsPotBalance().negate().doubleValue()));
				if (withdrawalAmount.compareTo(BigDecimal.ZERO) != 0) {
					journalEntries.add(createAutoWithdrawal(resourceEngine, withdrawalAmount));
				}
				if (potManager.getCurrentMonthsPotBalance().compareTo(BigDecimal.ZERO) < 1) {
					break;
				}
			}
		}
		return journalEntries;
	}
	
	private void generateNegativePotDistributionToDebtResources(final List<IResourceEngine> resourceEngines) {
		if (potManager.getCurrentMonthsPotBalance().compareTo(BigDecimal.ZERO) < 1) {
			Collections.reverse(resourceEngines);
			for (IResourceEngine resourceEngine : resourceEngines) {
				BigDecimal amount = resourceEngine.getBalanceLiquidLegalMin(potManager.getCurrentYearMonth());
				amount = BigDecimal.valueOf(Math.max(amount.doubleValue(), potManager.getCurrentMonthsPotBalance().doubleValue()));
				potManager.addToPotBalance(amount.negate());
				potManager.subtractFromLiquidPot(resourceEngine, amount.negate());
				if (potManager.getCurrentMonthsPotBalance().compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
	}
	
	private List<JournalEntry> generateJournalEntriesForClosingBalances(final List<IResourceEngine> resourceEngines) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (IResourceEngine resourceEngine : resourceEngines) {
			BigDecimal balanceLiquid = potManager.getLiquidBalance(resourceEngine, potManager.getCurrentYearMonth());
			journalEntries.add(journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), balanceLiquid, CashflowCategory.JE_BALANCE_CLOSING_LIQUID));	
			BigDecimal assetValue = potManager.getAssetValue(resourceEngine, potManager.getCurrentYearMonth());
			journalEntries.add(journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), assetValue, CashflowCategory.JE_BALANCE_CLOSING_ASSET_VALUE));	
		}
		if (potManager.getCurrentMonthsPotBalance().compareTo(BigDecimal.ZERO) != 0) {
			journalEntries.add(journalEntryFactory.create(getResourceUnallocated(), potManager.getCurrentYearMonth(), potManager.getCurrentMonthsPotBalance(), CashflowCategory.JE_BALANCE_CLOSING_LIQUID));				
			journalEntries.add(journalEntryFactory.create(getResourceUnallocated(), potManager.getCurrentYearMonth(), potManager.getCurrentMonthsPotBalance(), CashflowCategory.JE_BALANCE_CLOSING_ASSET_VALUE));
		}
		return journalEntries;
	}
	
	private ResourceUnallocated getResourceUnallocated() {
		if (null == resourceUnallocated) {
			String name = messageSource.getMessage("msg.class.resourceUnallocated.name", null, LocaleContextHolder.getLocale());
			resourceUnallocated = new ResourceUnallocated(name);
			resourceUnallocated.setStartYearMonth(potManager.getCurrentYearMonth());
		}
		return resourceUnallocated;
	}

	private JournalEntry createExpenseOrIncome(final Resource resource, final BigDecimal amount, CashflowCategory category, String detail) {
		potManager.addToPotBalance(amount);
		return journalEntryFactory.create(resource, potManager.getCurrentYearMonth(), amount, category, detail);		
	}

	private JournalEntry createAutoWithdrawal(final IResourceEngine resourceEngine, final BigDecimal amount) {
		potManager.addToPotBalance(amount);
		potManager.subtractFromLiquidDeposits(resourceEngine, amount);
		return journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), amount, CashflowCategory.JE_AUTO_WITHDRAWAL);	
	}

	private JournalEntry createWithdrawal(final IResourceEngine resourceEngine, final BigDecimal amount, CashflowCategory category, String detail) {
		BigDecimal allowedAmount = BigDecimal.valueOf(Math.min(amount.floatValue(), potManager.getLiquidDeposits(resourceEngine).floatValue()));
		return journalEntryFactory.create(resourceEngine.getResource(), potManager.getCurrentYearMonth(), allowedAmount, category, detail);	
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

	private BigDecimal calculateAmountWithCpi(CashflowDateRangeValue cfdrv, YearMonth yearMonth) {
		BigDecimal amount = BigDecimal.valueOf(cfdrv.getValue());
		if (cfdrv.getCashflow().getCpiAffected()) {
			BigDecimal cpi = cfdrv.getCashflow().getResource().getScenario().getResourceScenario().getCpi().divide(HUNDRED, SCALE_4_ROUNDING_HALF_EVEN);
			cpi = cpi.add(BigDecimal.ONE);
			int yearsOfCpi = yearMonth.getYear() - cfdrv.getYearMonthStart().getYear();
			amount = amount.multiply(cpi.pow(yearsOfCpi));
		}
		return amount;
	}
}
