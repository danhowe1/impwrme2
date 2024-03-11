package com.impwrme2.controller.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.journalEntry.JournalEntryResponse;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.journalEntry.JournalEntryService;
import com.impwrme2.service.scenario.ScenarioService;
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
	public String getChartData(@AuthenticationPrincipal OidcUser user, HttpSession session) {
		return generateJsonChartData(user, session);
	}

	private String generateJsonChartData(OidcUser user, HttpSession session) {

		JournalEntryResponse journalEntryResponse = getOrCreateJournalEntries(user, session);
		
		JsonObject dataTable = new JsonObject();
		JsonArray jsonRows = new JsonArray();

		List<String[][]> columnDefinitions = new ArrayList<String[][]>();
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Date" }, { "pattern", "" }, { "type", "string" } });
		columnDefinitions.add(new String[][] { { "id", "" }, { "label", "Total" }, { "pattern", "" }, { "type", "number" } });
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

		Map<String, Integer> dataMap = getAnnualClosingLiquidBalances(journalEntryResponse.getJournalEntries());

		for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
			JsonObject row = new JsonObject();
			JsonArray cells = new JsonArray();
			JsonObject cellDate = new JsonObject();
			JsonObject cellTotal = new JsonObject();
			JsonObject cellStyle = new JsonObject();
			JsonObject cellTooltip = new JsonObject();

			cellDate.addProperty("v", entry.getKey());
			cells.add(cellDate);
		
			cellTotal.addProperty("v", entry.getValue().intValue());
			cells.add(cellTotal);

			cellStyle.addProperty("v", "");					
			cells.add(cellStyle);

			cellTooltip.addProperty("v", "");					
			cells.add(cellTooltip);

			row.add("c", cells);
			jsonRows.add(row);
		}
		
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

	private JournalEntryResponse getOrCreateJournalEntries(OidcUser user, HttpSession session) {
		JournalEntryResponse journalEntryResponse = (JournalEntryResponse) session.getAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");		
		if (null == journalEntryResponse) {
			Resource sessionResource = (Resource) session.getAttribute("SESSION_CURRENT_RESOURCE");
			Scenario scenario = scenarioService.findByIdAndUserId(sessionResource.getScenario().getId(), user.getUserInfo().getSubject()).get();
			journalEntryResponse = journalEntryService.run(scenario);
			session.setAttribute("SESSION_JOURNAL_ENTRY_RESPONSE", journalEntryResponse);
		}
		return journalEntryResponse;
	}

	private Map<String, Integer> getAnnualClosingLiquidBalances (final List<JournalEntry> journalEntries) {
		
		Map<String, Integer> journalEntryYearTotalMap = new TreeMap<String, Integer>();
		for (JournalEntry journalEntry : journalEntries) {		
			if (journalEntry.getCategory().equals(CashflowCategory.JE_BALANCE_CLOSING_LIQUID) &&
				journalEntry.getDate().getMonth().getValue() == 12) {
				String key = String.valueOf(journalEntry.getDate().getYear());
				Integer currentYearTotal = journalEntryYearTotalMap.get(key);
				if (null == currentYearTotal) {
					journalEntryYearTotalMap.put(key, journalEntry.getAmount());
				} else {
					journalEntryYearTotalMap.put(key, currentYearTotal + journalEntry.getAmount());					
				}
			}
		}
		return journalEntryYearTotalMap;
	}
}
