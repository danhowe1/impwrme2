package com.impwrme2.service.journalEntry;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.engine.IResourceEngine;

@Service
public class JournalEntryService {

	@Autowired
	private JournalEntryResponse journalEntryResponse;

	@Autowired
	private PotManagerService potManager;

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
				
				// Generate opening balances if required.
				if (potManager.getCurrentYearMonth().equals(resourceEngine.getResource().getStartYearMonth())) {
					currentMonthsJournalEntries.add(createJournalEntryLiquidDepositAmountOpeningBalance(resourceEngine));
					currentMonthsJournalEntries.add(createJournalEntryFixedAmountOpeningBalance(resourceEngine));
				}

				// Generate the monthly journal entries for each Resource.
				List<JournalEntry> resourceJournalEntries = resourceEngine.generateJournalEntriesForExpensesIncomeAndWithdrawals(potManager);
				currentMonthsJournalEntries.addAll(resourceJournalEntries);
				
				// TODO Contribute to the pot.
				totalJournalEntries.addAll(currentMonthsJournalEntries);
			}

			

			if (potManager.getCurrentYearMonth().equals(YearMonth.of(2024, 1))) {
				Collections.sort(currentMonthsJournalEntries);
				for (JournalEntry journalEntry : currentMonthsJournalEntries) {
					System.out.println(journalEntry.toString());
				}
				System.out.println(potManager.toString());
			}
			
			// TODO Do sanity check for testing to ensure no limits are breached.
			
			// Move to the next month.
			potManager.processEndOfMonth();
		}
		
		return totalJournalEntries;
	}

	private JournalEntry createJournalEntryLiquidDepositAmountOpeningBalance(IResourceEngine resourceEngine) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resourceEngine.getResource().getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_LIQUID, resourceEngine.getResource().getStartYearMonth());
		Integer liquidDepositsAmount = Integer.valueOf(0);
		if (rpdvOpt.isPresent()) {
			liquidDepositsAmount = (Integer) rpdvOpt.get().getValue();
		}
		potManager.addToLiquidDepositsAmount(resourceEngine, potManager.getCurrentYearMonth(), BigDecimal.valueOf(liquidDepositsAmount).subtract(resourceEngine.getBalanceLiquidLegalMin(resourceEngine.getResource().getStartYearMonth())));
		return JournalEntryFactory.createBalanceOpeningLiquid(resourceEngine.getResource(), potManager.getCurrentYearMonth(), potManager.getLiquidBalance(resourceEngine.getResource(), potManager.getCurrentYearMonth()));
	}
	
	private JournalEntry createJournalEntryFixedAmountOpeningBalance(IResourceEngine resourceEngine) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resourceEngine.getResource().getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_FIXED, resourceEngine.getResource().getStartYearMonth());
		Integer fixedAmount = Integer.valueOf(0);
		if (rpdvOpt.isPresent()) {
			fixedAmount = (Integer) rpdvOpt.get().getValue();
		}
		potManager.addToFixedAmount(resourceEngine, potManager.getCurrentYearMonth(), BigDecimal.valueOf(fixedAmount));
		return JournalEntryFactory.createBalanceOpeningAssetValue(resourceEngine.getResource(), potManager.getCurrentYearMonth(), potManager.getAssetValue(resourceEngine.getResource(), potManager.getCurrentYearMonth()));
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
}
