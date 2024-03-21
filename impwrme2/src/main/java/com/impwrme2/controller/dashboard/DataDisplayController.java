package com.impwrme2.controller.dashboard;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.journalEntry.JournalEntryService;
import com.impwrme2.service.scenario.ScenarioService;
import com.impwrme2.service.ui.UIDisplayFilter;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.JsonArray;
import com.nimbusds.jose.shaded.gson.JsonObject;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/dashboard/dataDisplay")
public class DataDisplayController {

	@Autowired
	private ScenarioService scenarioService;

	@Autowired
	private JournalEntryService journalEntryService;

	@GetMapping(value = "/getChartData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getChartData(@AuthenticationPrincipal OidcUser user, @RequestParam String jsDisplayFilter, final HttpSession session) {
		System.out.println(jsDisplayFilter);

		UIDisplayFilter displayFilter = new Gson().fromJson(jsDisplayFilter, UIDisplayFilter.class);
		session.setAttribute("SESSION_DISPLAY_FILTER", displayFilter);

		return generateJsonChartData(user, displayFilter, session);
	}

	@GetMapping(value = "/getBalancesTableData", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getBalancesTableData(@AuthenticationPrincipal OidcUser user, @RequestParam String jsDisplayFilter, final HttpSession session) {

		UIDisplayFilter displayFilter = new Gson().fromJson(jsDisplayFilter, UIDisplayFilter.class);
		session.setAttribute("SESSION_DISPLAY_FILTER", displayFilter);
		
		return generateJsonTableData(user, displayFilter, session);
	}

	private String generateJsonChartData(final OidcUser user, final UIDisplayFilter displayFilter, final HttpSession session) {

		Resource sessionResource = (Resource) session.getAttribute("SESSION_CURRENT_RESOURCE");
		Scenario scenario = scenarioService.findByIdAndUserId(sessionResource.getScenario().getId(), user.getUserInfo().getSubject()).get();

		JournalEntryResponse journalEntryResponse = getOrCreateJournalEntries(session, scenario);
		
		//TODO Remove...
		displayFilter.setBreakdownAggregate(false);
		
		List<JournalEntry> filteredClosingBalances = getFilteredClosingBalances(journalEntryResponse.getJournalEntries(), displayFilter);
		
		Map<String, Integer> dateResourceToAmountMap = getClosingBalances(filteredClosingBalances, displayFilter);

		JsonObject dataTable = new JsonObject();
		JsonArray jsonRows = new JsonArray();

		SortedSet<String> filteredResourceNames = getFilteredResourceNames(filteredClosingBalances ,displayFilter);
		
		List<String[][]> columnDefinitions = new ArrayList<String[][]>();
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Date" }, { "pattern", "" }, { "type", "string" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Total" }, { "pattern", "" }, { "type", "number" } });
		
		for (String resourceName : filteredResourceNames) {
			columnDefinitions.add(new String[][] { { "id", "" }, { "label", resourceName }, { "pattern", "" }, { "type", "number" } });
		}
		
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Style" }, { "role", "style" }, { "type", "string" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Toooltip" }, { "role", "tooltip" }, { "type", "string" } });

		JsonArray columns = new JsonArray();
		for (String[][] columnDefinition : columnDefinitions) {
			JsonObject cell = new JsonObject();
			for (int i = 0; i < columnDefinition.length; i++) {
				cell.addProperty(columnDefinition[i][0], columnDefinition[i][1]);
			}
			columns.add(cell);
		}
		dataTable.add("cols", columns);

		YearMonth currentYearMonth = scenario.getStartYearMonth();
		while (currentYearMonth.getYear() <= displayFilter.getYearEnd()) {
			if (!displayFilter.isTimePeriodAnnually() || currentYearMonth.getMonthValue() == 12) {
				JsonObject row = new JsonObject();
				JsonArray cells = new JsonArray();

				JsonObject cellDate = new JsonObject();
				cellDate.addProperty("v", currentYearMonth.toString());
				cells.add(cellDate);
			
				JsonObject cellTotal = new JsonObject();
				cellTotal.addProperty("v", dateResourceToAmountMap.get(currentYearMonth.toString()));
				cells.add(cellTotal);

				for (String resourceName : filteredResourceNames) {
					JsonObject cell = new JsonObject();
					cell.addProperty("v", dateResourceToAmountMap.get(currentYearMonth.toString() + "-" + resourceName));
					cells.add(cell);
				}

				JsonObject cellStyle = new JsonObject();
				cellStyle.addProperty("v", "");					
				cells.add(cellStyle);

				JsonObject cellTooltip = new JsonObject();
				cellTooltip.addProperty("v", "");					
				cells.add(cellTooltip);

				row.add("c", cells);
				jsonRows.add(row);				
			}
			currentYearMonth = currentYearMonth.plusMonths(1);
		}
		
//		for (Map.Entry<String, Integer> entry : dateResourceToAmountMap.entrySet()) {
//			JsonObject row = new JsonObject();
//			JsonArray cells = new JsonArray();
//			JsonObject cellDate = new JsonObject();
//			JsonObject cellTotal = new JsonObject();
//			JsonObject cellCurrentAccount = new JsonObject();
//			JsonObject cellStyle = new JsonObject();
//			JsonObject cellTooltip = new JsonObject();
//
//			cellDate.addProperty("v", entry.getKey());
//			cells.add(cellDate);
//		
//			cellTotal.addProperty("v", entry.getValue().intValue());
//			cells.add(cellTotal);
//
//			cellCurrentAccount.addProperty("v", entry.getValue().intValue() + 10000);
//			cells.add(cellCurrentAccount);
//
//			cellStyle.addProperty("v", "");					
//			cells.add(cellStyle);
//
//			cellTooltip.addProperty("v", "");					
//			cells.add(cellTooltip);
//
//			row.add("c", cells);
//			jsonRows.add(row);
//		}
		
//		List<Object[]> rows = new ArrayList<Object[]>();
//		rows.add(new Object[] { "12 2024", Integer.valueOf(324), null, null });
//		rows.add(new Object[] { "12 2025", Integer.valueOf(654), "point { size: 12; shape-type: star; fill-color: #a52714; }", "Griffin St sold for $4,500,000" });
//		rows.add(new Object[] { "12 2026", Integer.valueOf(700), null, null });

//		for (Object[] rowData : rows) {
//			JsonObject row = new JsonObject();
//			JsonArray cells = new JsonArray();
//
//			for (Object cellData : rowData) {
//				JsonObject cell = new JsonObject();
//				if (null == cellData) {
//					cell.addProperty("v", "");					
//				} else if (cellData instanceof Integer) {
//					cell.addProperty("v", ((Integer) cellData).intValue());
//				} else {
//					cell.addProperty("v", cellData.toString());
//				}
//				cells.add(cell);
//			}
//
//			row.add("c", cells);
//			jsonRows.add(row);
//		}

		dataTable.add("rows", jsonRows);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result = gson.toJson(dataTable);

		return result;
	}

	private String generateJsonTableData(final OidcUser user, final UIDisplayFilter displayFilter, final HttpSession session) {
		JsonObject dataTable = new JsonObject();

		JsonArray columns = new JsonArray();

		JsonObject col1 = new JsonObject();
		col1.addProperty("data", "DATE");
		col1.addProperty("title", "DATE");
		columns.add(col1);

		JsonObject col2 = new JsonObject();
		col2.addProperty("data", "TOTAL");
		col2.addProperty("title", "TOTAL");
		columns.add(col2);

		dataTable.add("columns", columns);
		
		JsonArray journalEntryRows = new JsonArray();
		
		JsonObject journalEntry1 = new JsonObject();
		journalEntry1.addProperty("DATE", "2024-01");
		journalEntry1.addProperty("TOTAL", "1200");
		journalEntryRows.add(journalEntry1);

		JsonObject journalEntry2 = new JsonObject();
		journalEntry2.addProperty("DATE", "2024-02");
		journalEntry2.addProperty("TOTAL", "1674");
		journalEntryRows.add(journalEntry2);

		dataTable.add("journalEntries", journalEntryRows);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result = gson.toJson(dataTable);

		return result;
	}
	
	private JournalEntryResponse getOrCreateJournalEntries(HttpSession session, Scenario scenario) {
		JournalEntryResponse journalEntryResponse = (JournalEntryResponse) session.getAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");		
		if (null == journalEntryResponse) {
			journalEntryResponse = journalEntryService.run(scenario);
			session.setAttribute("SESSION_JOURNAL_ENTRY_RESPONSE", journalEntryResponse);
		}
		return journalEntryResponse;
	}
	
	private List<JournalEntry> getFilteredClosingBalances(final List<JournalEntry> journalEntries, final UIDisplayFilter displayFilter) {
		List<JournalEntry> filteredJournalEntries = new ArrayList<JournalEntry>();
		for (JournalEntry journalEntry : journalEntries) {
			if (journalEntry.getDate().getYear() > displayFilter.getYearEnd()) {
				continue;
			}
			if (journalEntry.getAmount().intValue() == 0) {
				continue;
			}
			if (displayFilter.isTimePeriodAnnually() && journalEntry.getDate().getMonthValue() != 12) {
				continue;
			}
			if (journalEntry.getCategory().equals(CashflowCategory.JE_BALANCE_CLOSING_LIQUID) &&
				displayFilter.isAssetTypeLiquid()) {
				filteredJournalEntries.add(journalEntry);
			} else if (journalEntry.getCategory().equals(CashflowCategory.JE_BALANCE_CLOSING_ASSET_VALUE) &&
				!displayFilter.isAssetTypeLiquid()) {
				filteredJournalEntries.add(journalEntry);
			}
		}
		return filteredJournalEntries;
	}

	private SortedSet<String> getFilteredResourceNames(final List<JournalEntry> journalEntries, final UIDisplayFilter displayFilter) {
		SortedSet<String> resourceNames = new TreeSet<String>();
		if (!displayFilter.isBreakdownAggregate()) {
			for (JournalEntry journalEntry : journalEntries) {
				resourceNames.add(journalEntry.getResource().getName());
			}
		}
		return resourceNames;
	}

	private Map<String, Integer> getClosingBalances (final List<JournalEntry> journalEntries, final UIDisplayFilter displayFilter) {
		Map<String, Integer> journalEntryDateResourceToAmountMap = new TreeMap<String, Integer>();
		for (JournalEntry journalEntry : journalEntries) {
			if (!displayFilter.isTimePeriodAnnually() || journalEntry.getDate().getMonth().getValue() == 12) {
				if (journalEntry.getAmount().intValue() != 0 && !displayFilter.isBreakdownAggregate()) {
					String dateResourceKey = String.valueOf(journalEntry.getDate().toString() + "-" + journalEntry.getResource().getName());
					Integer currentResourceValue = journalEntryDateResourceToAmountMap.get(dateResourceKey);
					if (null == currentResourceValue) {
						journalEntryDateResourceToAmountMap.put(dateResourceKey, journalEntry.getAmount());
					} else {
						journalEntryDateResourceToAmountMap.put(dateResourceKey, journalEntry.getAmount() + currentResourceValue);
					}
				}
				String dateTotalKey = String.valueOf(journalEntry.getDate().toString());
				Integer currentTotalValue = journalEntryDateResourceToAmountMap.get(dateTotalKey);
				if (null == currentTotalValue) {
					journalEntryDateResourceToAmountMap.put(dateTotalKey, journalEntry.getAmount());
				} else {
					journalEntryDateResourceToAmountMap.put(dateTotalKey, journalEntry.getAmount() + currentTotalValue);
				}
			}
		}
		return journalEntryDateResourceToAmountMap;
	}
}
