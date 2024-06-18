package com.impwrme2.service.journalEntry;

import java.lang.reflect.InvocationTargetException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowType;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.model.resource.ResourceMortgageOffset;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceUnallocated;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.engine.IResourceEngine;

public class JournalEntryService {
	
	private ResourceUnallocated resourceUnallocated;
	private final Scenario scenario;
	private YearMonth currentYearMonth;
	private BalanceTracker balanceTracker;
	private JournalEntryResponse journalEntryResponse;
	private final MessageSource messageSource;

	public JournalEntryService(final Scenario scenario, final MessageSource messageSource) {
		this.scenario = scenario;
		this.messageSource = messageSource;
		balanceTracker = new BalanceTracker(scenario.getResources());
		currentYearMonth = scenario.getStartYearMonth();
	}
	
	public JournalEntryResponse run() {
		journalEntryResponse = new JournalEntryResponse(messageSource);
		journalEntryResponse.setJournalEntries(generateJournalEntries());
		return journalEntryResponse;
	}

	private List<JournalEntry> generateJournalEntries() {

		// Set up initial state.
		List<IResourceEngine> resourceEngines = getResourceEngines(scenario);
		YearMonth END_DATE = scenario.calculateEndYearMonth();
		List<JournalEntry> totalJournalEntries = new ArrayList<JournalEntry>();

		while (!currentYearMonth.isAfter(END_DATE)) {

			List<JournalEntry> currentMonthsProcessedJournalEntries = new ArrayList<JournalEntry>();
			List<JournalEntry> currentMonthsUnprocessedJournalEntries = new ArrayList<JournalEntry>();

			for (IResourceEngine resourceEngine : resourceEngines) {
				
				if (currentYearMonth.equals(resourceEngine.getResource().getStartYearMonth())) {
					// Generate opening balances if required.
					currentMonthsProcessedJournalEntries.addAll(generateJournalEntryOpeningBalances(resourceEngine.getResource()));
				}

				// Contribute months opening balance to the pot.
				Integer balanceLiquidOpeningAmount = balanceTracker.getResourceLiquidAmount(resourceEngine.getResource());
				if (balanceLiquidOpeningAmount > 0) {
					balanceTracker.addToPotBalance(balanceLiquidOpeningAmount);
					balanceTracker.subtractFromResourceLiquidAmount(resourceEngine.getResource(), balanceLiquidOpeningAmount);
				} else if (balanceLiquidOpeningAmount < 0) {
					balanceTracker.subtractFromPotBalance(balanceLiquidOpeningAmount);
					balanceTracker.addToResourceLiquidAmount(resourceEngine.getResource(), balanceLiquidOpeningAmount);						
				}

				//Get this resources raw journal entries.
				currentMonthsUnprocessedJournalEntries.addAll(resourceEngine.generateJournalEntries(currentYearMonth, balanceTracker));
				
				// Process the expenses and income as these have to happen regardless of pot balances. 
				// Process the appreciations and depreciations as they don't affect the pot.
				// We don't process the Deposits yet because we don't know if there'll be enough in 
				// the pot to do so. 
				// We don't process the Withdrawals yet because each engine may generate auto deposits
				// that the Withdrawals could go against.
				List<JournalEntry> currentMonthsNonDepositsAndNonWithdrawals = processNonDepositsAndNonWithdrawals(currentMonthsUnprocessedJournalEntries);
				currentMonthsProcessedJournalEntries.addAll(currentMonthsNonDepositsAndNonWithdrawals);
				currentMonthsUnprocessedJournalEntries.removeAll(currentMonthsNonDepositsAndNonWithdrawals);				
			}

			// Ensure resource engines are in priority order.
			Collections.sort(resourceEngines);

			// Handle the case where the liquid deposit amount is greater than the max preferred or allowed.
			currentMonthsProcessedJournalEntries.addAll(generateJournalEntriesForAutoWithdrawalsOnMaxLimits(resourceEngines));
			
			// Handle the case where the liquid deposit amount is less than the min preferred or allowed.
			currentMonthsProcessedJournalEntries.addAll(generateJournalEntriesForAutoDepositsOnMinLimits(resourceEngines));
			
			// Generate users Withdrawals.
			List<JournalEntry> currentMonthsUserWithdrawals = processUserWithdrawals(currentMonthsUnprocessedJournalEntries);
			currentMonthsProcessedJournalEntries.addAll(currentMonthsUserWithdrawals);
			currentMonthsUnprocessedJournalEntries.removeIf(journalEntry -> journalEntry.getCategory().getType().equals(CashflowType.WITHDRAWAL));
					
			// Generate users Deposits if there's enough in the pot to do so.
			List<JournalEntry> currentMonthsUserDeposits = processUserDeposits(resourceEngines, currentMonthsUnprocessedJournalEntries);
			currentMonthsProcessedJournalEntries.addAll(currentMonthsUserDeposits);
			currentMonthsUnprocessedJournalEntries.removeIf(journalEntry -> journalEntry.getCategory().getType().equals(CashflowType.DEPOSIT));

			// TODO Remove this sanity check when testing complete...
			if (currentMonthsUnprocessedJournalEntries.size() > 0) {
				throw new IllegalStateException("Unprocessed journal entries on " + currentYearMonth.toString());
			}
			
			// Order in reverse for the following withdrawals.
			Collections.sort(resourceEngines, Collections.reverseOrder()); 

			// If the pot is negative make auto withdrawals from the resources in reverse order of importance, based on the preferred balances.
			currentMonthsProcessedJournalEntries.addAll(generateJournalEntriesForAutoWithdrawalsOnPreferredMinLimits(resourceEngines));
						
			// If the pot is still negative make auto withdrawals from the resources in reverse order of importance, based on the legal min and max values.
			currentMonthsProcessedJournalEntries.addAll(generateJournalEntriesForAutoWithdrawalsOnLegalMinLimits(resourceEngines));
			
			// If the pot is still negative take the money from the debt resources (e.g. credit cards).
			generateNegativePotDistributionToDebtResources(resourceEngines);
			
			// Return to preferred order so the pot distributes to the highest priorities first.
			Collections.sort(resourceEngines);
			
			// If there's money in the pot, redistribute to the resources based on the legal min balances.
			generatePotDistributionBasedOnLegalMinBalances(resourceEngines);
			
			// If there's still money in the pot, redistribute to the resources based on the preferred min balances.
			generatePotDistributionBasedOnPreferredMinBalances(resourceEngines);
			
			// If there's still money in the pot, redistribute to the resources based on the preferred max balances.
			generatePotDistributionBasedOnPreferredMaxBalances(resourceEngines);
			
			// If there's still money in the pot, redistribute to the resources based on the legal max balances.
			generatePotDistributionBasedOnLegalMaxBalances(resourceEngines);
			
			// TODO Generate the intra account transfers. Do we still want these?
			
			// Generate the closing balances (including unallocated if necessary).
			currentMonthsProcessedJournalEntries.addAll(generateJournalEntriesForClosingBalances(resourceEngines));
			
//			if (currentYearMonth.isBefore(YearMonth.of(2034, 5))) {
//				Collections.sort(currentMonthsProcessedJournalEntries);
//				for (JournalEntry journalEntry : currentMonthsProcessedJournalEntries) {
//					System.out.println(journalEntry.toString());
//				}
//				System.out.println(balanceTracker.toString());
//			}
			
			// TODO Temporarily put a sanity check here to make sure we're not creating or destroying money.
			
			// Move to the next month.
			totalJournalEntries.addAll(currentMonthsProcessedJournalEntries);
			balanceTracker.processEndOfMonth();
			currentYearMonth = currentYearMonth.plusMonths(1);
		}
		
		for (IResourceEngine resourceEngine : resourceEngines) {
			journalEntryResponse.addMilestones(resourceEngine.getMilestones());
		}

		Collections.sort(totalJournalEntries);
		return totalJournalEntries;
	}

	private List<IResourceEngine> getResourceEngines(Scenario scenario) {
		List<IResourceEngine> engines = new ArrayList<IResourceEngine>();
		for (Resource resource : scenario.getSortedResources()) {
			if (!(resource instanceof ResourceScenario)) {
				try {
					Class<?> engineClass = Class.forName(resourceEngineClassName(resource));
					Class<?> [] paramTypes = { resource.getClass(), BalanceTracker.class };
					Object [] paramValues = {  resource, balanceTracker };
					Object engine = engineClass.getConstructor(paramTypes).newInstance(paramValues);
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

	private String resourceEngineClassName(Resource resource) {
		String name = resource.getClass().getSimpleName();
		name = name.replace("Existing", "");
		name = name.replace("New", "");
		return "com.impwrme2.service.engine." + name + "Engine";
	}
	
	private List<JournalEntry> generateJournalEntryOpeningBalances(Resource resource) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		Integer balanceOpeningLiquid = Integer.valueOf(0);
		Integer balanceOpeningFixed = Integer.valueOf(0);

		Optional<ResourceParamDateValue<?>> balanceOpeningLiquidOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_LIQUID, resource.getStartYearMonth());
		if (balanceOpeningLiquidOpt.isPresent()) balanceOpeningLiquid = (Integer) balanceOpeningLiquidOpt.get().getValue();

		Optional<ResourceParamDateValue<?>> balanceOpeningFixedOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_FIXED, resource.getStartYearMonth());
		if (balanceOpeningFixedOpt.isPresent()) balanceOpeningFixed = (Integer) balanceOpeningFixedOpt.get().getValue();

		journalEntries.add(JournalEntryFactory.create(resource, currentYearMonth, balanceOpeningLiquid, CashflowCategory.JE_BALANCE_OPENING_LIQUID));
		journalEntries.add(JournalEntryFactory.create(resource, currentYearMonth, balanceOpeningLiquid + balanceOpeningFixed, CashflowCategory.JE_BALANCE_OPENING_ASSET_VALUE));
		
		if (balanceOpeningLiquid.intValue() > 0) {
			if (resource instanceof ResourceCurrentAccount || resource instanceof ResourceMortgageOffset) {
				// For current accounts and mortgage offset accounts we assume the balance is available to the pot.
				balanceTracker.addToResourceLiquidAmount(resource, balanceOpeningLiquid);				
			} else {
				// For all other liquid accounts we assume that these have been explicitly deposited by the user.
				balanceTracker.addToResourceLiquidDepositAmount(resource, balanceOpeningLiquid);
			}
			balanceTracker.addToResourceFixedAmount(resource, balanceOpeningFixed);
		} else if (balanceOpeningLiquid.intValue() < 0) {
			// If this is debt then just put the amount straight into the pot. We assume the user wants to pay this off asap.
			balanceTracker.subtractFromPotBalance(balanceOpeningLiquid);
			balanceTracker.subtractFromResourceFixedAmount(resource, balanceOpeningFixed);
		}
		
		if (balanceOpeningFixed > 0) {
			balanceTracker.addToResourceFixedAmount(resource, balanceOpeningFixed);
		} else if (balanceOpeningFixed < 0) {
			balanceTracker.subtractFromResourceFixedAmount(resource, balanceOpeningFixed);		
		}
		return journalEntries;
	}

	private List<JournalEntry> generateJournalEntriesForAutoWithdrawalsOnMaxLimits(final List<IResourceEngine> resourceEngines) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (IResourceEngine resourceEngine : resourceEngines) {
			Integer maxLimit = Math.min(resourceEngine.getBalanceLiquidLegalMax(currentYearMonth, balanceTracker), resourceEngine.getBalanceLiquidPreferredMax(currentYearMonth, balanceTracker));
			Integer withdrawalAmount = Math.max(0, balanceTracker.getResourceLiquidDepositAmount(resourceEngine.getResource()) - maxLimit);
			if (withdrawalAmount > 0) {
				journalEntries.add(createAutoWithdrawal(resourceEngine, withdrawalAmount));

			}		
		}
		return journalEntries;
	}

	/**
	 * This can occur if the minimum liquid balance has been increased to be above the existing liquid deposit amount.
	 * @param resourceEngines Engines to check.
	 * @return A list of auto deposits.
	 */
	private List<JournalEntry> generateJournalEntriesForAutoDepositsOnMinLimits(final List<IResourceEngine> resourceEngines) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (IResourceEngine resourceEngine : resourceEngines) {
			Integer minLimit = Math.max(resourceEngine.getBalanceLiquidLegalMin(currentYearMonth), resourceEngine.getBalanceLiquidPreferredMin(currentYearMonth));
			Integer depositAmount = Math.max(0, minLimit - balanceTracker.getResourceLiquidDepositAmount(resourceEngine.getResource()));
			if (depositAmount > 0) {
				journalEntries.add(createAutoDeposit(resourceEngine, depositAmount));
			}		
		}
		return journalEntries;
	}

	private List<JournalEntry> processNonDepositsAndNonWithdrawals(List<JournalEntry> unprocessedJournalEntries) {
		List<JournalEntry> processedJournalEntries = new ArrayList<JournalEntry>();
		for (JournalEntry journalEntry : unprocessedJournalEntries) {
			
			switch (journalEntry.getCategory().getType()) {
			case APPRECIATION_FIXED:
				balanceTracker.addToResourceFixedAmount(journalEntry.getResource(), journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			case APPRECIATION_LIQUID:
				balanceTracker.addToResourceLiquidDepositAmount(journalEntry.getResource(), journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			case EXPENSE:
				balanceTracker.subtractFromPotBalance(journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			case DEPRECIATION_FIXED:
				balanceTracker.subtractFromResourceFixedAmount(journalEntry.getResource(), journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			case DEPRECIATION_LIQUID:
				balanceTracker.subtractFromResourceLiquidDepositAmount(journalEntry.getResource(), journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			case INCOME:
				balanceTracker.addToPotBalance(journalEntry.getAmount());
				processedJournalEntries.add(journalEntry);
				break;
			default:
				break;				
			}
		}
		return processedJournalEntries;
	}

	private List<JournalEntry> processUserWithdrawals(List<JournalEntry> unprocessedJournalEntries) {
		List<JournalEntry> processedJournalEntries = new ArrayList<JournalEntry>();
		for (JournalEntry journalEntry : unprocessedJournalEntries) {
			if (journalEntry.getCategory().getType().equals(CashflowType.WITHDRAWAL)) {
				Integer withdrawalAvailable = -balanceTracker.getResourceLiquidDepositAmount(journalEntry.getResource());
				Integer withdrawalAmount = Math.max(journalEntry.getAmount(), withdrawalAvailable);

				// Withdrawal amount available may be different to the user requested amount so create new one.
				JournalEntry journalEntryNew = JournalEntryFactory.create(journalEntry.getResource(), currentYearMonth, withdrawalAmount, journalEntry.getCategory(), journalEntry.getDetail());
				balanceTracker.addToPotBalance(-journalEntryNew.getAmount());
				balanceTracker.subtractFromResourceLiquidDepositAmount(journalEntryNew.getResource(), -journalEntryNew.getAmount());
				processedJournalEntries.add(journalEntryNew);
			}
		}
		return processedJournalEntries;
	}

	private List<JournalEntry> processUserDeposits(List<IResourceEngine> resourceEngines, List<JournalEntry> unprocessedJournalEntries) {
		List<JournalEntry> processedJournalEntries = new ArrayList<JournalEntry>();
		for (IResourceEngine resourceEngine : resourceEngines) {
			for (JournalEntry journalEntry : unprocessedJournalEntries) {
				if (journalEntry.getResource().equals(resourceEngine.getResource())) {
					if (journalEntry.getCategory().getType().equals(CashflowType.DEPOSIT)) {
						Integer depositAmount = journalEntry.getAmount();
						// Can't deposit an amount that would exceed the preferred or legal liquid balance.
						Integer balanceLiquidMax = Math.min(resourceEngine.getBalanceLiquidPreferredMax(currentYearMonth, balanceTracker), 
								resourceEngine.getBalanceLiquidLegalMax(currentYearMonth, balanceTracker));
						Integer maxDepositAmount = balanceLiquidMax - balanceTracker.getResourceLiquidBalance(journalEntry.getResource());
						depositAmount = Math.min(depositAmount, maxDepositAmount);
						
						// Deposit amount available may be different to the user requested amount so create new one.
						JournalEntry journalEntryNew = JournalEntryFactory.create(journalEntry.getResource(), currentYearMonth, depositAmount, journalEntry.getCategory(), journalEntry.getDetail());
						balanceTracker.subtractFromPotBalance(journalEntryNew.getAmount());
						balanceTracker.addToResourceLiquidDepositAmount(resourceEngine.getResource(), journalEntryNew.getAmount());
						processedJournalEntries.add(journalEntryNew);
					}
				}
			}
		}
		return processedJournalEntries;
	}

	private List<JournalEntry> generateJournalEntriesForAutoWithdrawalsOnPreferredMinLimits(final List<IResourceEngine> resourceEngines) {
		return generateJournalEntriesForAutoWithdrawalsOnLegalOrPreferredMinLimits(resourceEngines, "Preferred");
	}

	private List<JournalEntry> generateJournalEntriesForAutoWithdrawalsOnLegalMinLimits(final List<IResourceEngine> resourceEngines) {
		return generateJournalEntriesForAutoWithdrawalsOnLegalOrPreferredMinLimits(resourceEngines, "Legal");
	}

	private List<JournalEntry> generateJournalEntriesForAutoWithdrawalsOnLegalOrPreferredMinLimits(final List<IResourceEngine> resourceEngines, String legalOrPreferred) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (balanceTracker.getPotBalance() < 0) {
			for (IResourceEngine resourceEngine : resourceEngines) {
				Integer withdrawalAmount = balanceTracker.getResourceLiquidDepositAmount(resourceEngine.getResource());
				
				Integer balanceMin;
				if (legalOrPreferred.equals("Preferred")) {
					balanceMin = resourceEngine.getBalanceLiquidPreferredMin(currentYearMonth);
				} else {
					balanceMin = resourceEngine.getBalanceLiquidLegalMin(currentYearMonth);
				}
				
				withdrawalAmount = Math.max(0, withdrawalAmount - balanceMin);
				withdrawalAmount = Math.min(withdrawalAmount, balanceTracker.getPotBalance() * -1);
				withdrawalAmount = Math.min(withdrawalAmount, balanceTracker.getResourceLiquidDepositAmount(resourceEngine.getResource()));
				if (withdrawalAmount != 0) {
					journalEntries.add(createAutoWithdrawal(resourceEngine, withdrawalAmount));
				}
				if (balanceTracker.getPotBalance() == 0) {
					break;
				}
			}
		}
		return journalEntries;
	}

	private void generateNegativePotDistributionToDebtResources(final List<IResourceEngine> resourceEngines) {
		if (balanceTracker.getPotBalance() < 0) {
			for (IResourceEngine resourceEngine : resourceEngines) {
				Integer amount = resourceEngine.getBalanceLiquidLegalMin(currentYearMonth);
				amount = Math.max(amount, balanceTracker.getPotBalance());
				balanceTracker.addToPotBalance(amount);
				balanceTracker.subtractFromResourceLiquidAmount(resourceEngine.getResource(), amount);
				if (balanceTracker.getPotBalance() == 0) {
					break;
				}
			}
		}
	}
	
	private void generatePotDistributionBasedOnLegalMinBalances(final List<IResourceEngine> resourceEngines) {
		generatePotDistributionBasedOnLegalOrPreferredBalances(resourceEngines, "Legal Min");		
	}

	private void generatePotDistributionBasedOnPreferredMinBalances(final List<IResourceEngine> resourceEngines) {
		generatePotDistributionBasedOnLegalOrPreferredBalances(resourceEngines, "Preferred Min");
	}

	private void generatePotDistributionBasedOnPreferredMaxBalances(final List<IResourceEngine> resourceEngines) {
		generatePotDistributionBasedOnLegalOrPreferredBalances(resourceEngines, "Preferred Max");
	}

	private void generatePotDistributionBasedOnLegalMaxBalances(final List<IResourceEngine> resourceEngines) {
		generatePotDistributionBasedOnLegalOrPreferredBalances(resourceEngines, "Legal Max");		
	}

	private void generatePotDistributionBasedOnLegalOrPreferredBalances(final List<IResourceEngine> resourceEngines, String legalOrPreferred) {
		if (balanceTracker.getPotBalance() > 0) {
			for (IResourceEngine resourceEngine : resourceEngines) {
				Integer balanceToAttain = null;
				if (legalOrPreferred.equals("Legal Min")) {
					// We don't want to put money into debt resources here. This has been done earlier if necessary.
					balanceToAttain = Math.max(resourceEngine.getBalanceLiquidLegalMin(currentYearMonth), 0);
				} else if (legalOrPreferred.equals("Preferred Min")) {
					balanceToAttain = resourceEngine.getBalanceLiquidPreferredMin(currentYearMonth);
				} else if (legalOrPreferred.equals("Preferred Max")) {
					balanceToAttain = resourceEngine.getBalanceLiquidPreferredMax(currentYearMonth, balanceTracker);
				} else if (legalOrPreferred.equals("Legal Max")) {
					balanceToAttain = resourceEngine.getBalanceLiquidLegalMax(currentYearMonth, balanceTracker);
				}
				Integer existingBalance = balanceTracker.getResourceLiquidBalance(resourceEngine.getResource());
				if (balanceToAttain > existingBalance) {
					Integer amountToDistribute = balanceToAttain - existingBalance;
					amountToDistribute = Math.min(amountToDistribute, balanceTracker.getPotBalance());
					balanceTracker.subtractFromPotBalance(amountToDistribute);
					balanceTracker.addToResourceLiquidAmount(resourceEngine.getResource(), amountToDistribute);
					if (balanceTracker.getPotBalance() == 0) {
						break;
					}					
				}
			}
		}
	}

	private List<JournalEntry> generateJournalEntriesForClosingBalances(final List<IResourceEngine> resourceEngines) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		for (IResourceEngine resourceEngine : resourceEngines) {
			Integer balanceLiquid = balanceTracker.getResourceLiquidBalance(resourceEngine.getResource());
			journalEntries.add(JournalEntryFactory.create(resourceEngine.getResource(), currentYearMonth, balanceLiquid, CashflowCategory.JE_BALANCE_CLOSING_LIQUID));	
			journalEntryResponse.amendYearToLiquidBalance(currentYearMonth, balanceLiquid);
			Integer assetValue = balanceTracker.getResourceAssetValue(resourceEngine.getResource());
			journalEntries.add(JournalEntryFactory.create(resourceEngine.getResource(), currentYearMonth, assetValue, CashflowCategory.JE_BALANCE_CLOSING_ASSET_VALUE));	
		}
		if (balanceTracker.getPotBalance() != 0) {
			journalEntries.add(JournalEntryFactory.create(getResourceUnallocated(), currentYearMonth, balanceTracker.getPotBalance(), CashflowCategory.JE_BALANCE_CLOSING_LIQUID));				
			journalEntries.add(JournalEntryFactory.create(getResourceUnallocated(), currentYearMonth, balanceTracker.getPotBalance(), CashflowCategory.JE_BALANCE_CLOSING_ASSET_VALUE));
			journalEntryResponse.amendYearToLiquidBalance(currentYearMonth, balanceTracker.getPotBalance());
		}
		return journalEntries;
	}
	
	private ResourceUnallocated getResourceUnallocated() {
		if (null == resourceUnallocated) {
			String name = messageSource.getMessage("msg.class.resourceUnallocated.name", null, LocaleContextHolder.getLocale());
			resourceUnallocated = new ResourceUnallocated(name);
			resourceUnallocated.setStartYearMonth(currentYearMonth);
		}
		return resourceUnallocated;
	}

	private JournalEntry createAutoDeposit(final IResourceEngine resourceEngine, final Integer amount) {
		balanceTracker.subtractFromPotBalance(Math.abs(amount));
		balanceTracker.addToResourceLiquidDepositAmount(resourceEngine.getResource(), Math.abs(amount));
		return JournalEntryFactory.create(resourceEngine.getResource(), currentYearMonth, Math.abs(amount), CashflowCategory.JE_AUTO_DEPOSIT);	
	}

	private JournalEntry createAutoWithdrawal(final IResourceEngine resourceEngine, final Integer amount) {
		balanceTracker.addToPotBalance(Math.abs(amount));
		balanceTracker.subtractFromResourceLiquidDepositAmount(resourceEngine.getResource(), Math.abs(amount));
		return JournalEntryFactory.create(resourceEngine.getResource(), currentYearMonth, -1 * Math.abs(amount), CashflowCategory.JE_AUTO_WITHDRAWAL);	
	}
}
