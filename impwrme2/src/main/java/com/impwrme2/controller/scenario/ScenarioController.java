package com.impwrme2.controller.scenario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.impwrme2.controller.dto.scenario.ScenarioCreateMinDto;
import com.impwrme2.controller.dto.scenario.ScenarioDtoConverter;
import com.impwrme2.model.resource.ScenarioDeepCopy;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.model.scenario.ScenarioYearBalance;
import com.impwrme2.service.scenario.ScenarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/scenario")
public class ScenarioController {

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	private ScenarioDtoConverter scenarioDtoConverter;
	
	@Autowired
	private ScenarioService scenarioService;
	
	@GetMapping(value = {"", "/", "/list" })
	public String list(@AuthenticationPrincipal OidcUser user, Model model) {

		String userId = user.getUserInfo().getSubject();
		List<Scenario> scenarios = scenarioService.findByUserId(userId);
		model.addAttribute("scenarios", scenarios);
		model.addAttribute("chartData", generateChartData(scenarios));
		
		// Send the user to the scenario list page.
		return "scenario/list";
	}

	@GetMapping("/{scenarioId}")
	public String select(@PathVariable("scenarioId") Long id, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		String userId = user.getUserInfo().getSubject();		
		Scenario newScenario = scenarioService.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Invalid scenario id:" + id));		
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", newScenario.getResourceScenario().getId());
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "redirect:/app/dashboard";		
	}

	@PostMapping("/clone")
	public String clone(@RequestParam("id") Long id, @RequestParam("name") String name, @AuthenticationPrincipal OidcUser user, HttpSession session) {
		String userId = user.getUserInfo().getSubject();
		Scenario existingScenario = scenarioService.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Invalid scenario id:" + id));			
		Scenario newScenario = ScenarioDeepCopy.copyScenario(existingScenario, name);
		Scenario savedScenario = scenarioService.save(newScenario, userId);
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", savedScenario.getResourceScenario().getId());
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "redirect:/app/dashboard";		
	}
	
	@GetMapping("/delete/{scenarioId}")
	public String delete(@PathVariable("scenarioId") Long id, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		String userId = user.getUserInfo().getSubject();
		Scenario scenario = scenarioService.findByIdAndUserId(id, userId).orElseThrow(() -> new IllegalArgumentException("Invalid scenario id:" + id));
		scenarioService.delete(scenario, userId);
		redirectAttributes.addFlashAttribute("flashMessageSuccess", "Scenario deleted successfully.");
		session.removeAttribute("SESSION_CURRENT_RESOURCE_ID");
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "redirect:/app/scenario/";
	}

	@GetMapping(value = { "/createMinFormShow" })
	public String createMinFormShow(@AuthenticationPrincipal OidcUser user, Model model) {

		// Add default DTO to the model.
		model.addAttribute("scenarioCreateMinDto", new ScenarioCreateMinDto());
		
		// Send the user to the resource edit page.
		return "scenario/scenarioCreateMin";
	}

	@PostMapping("/createMinFormSave")
	public String createMinFormSave(@Valid ScenarioCreateMinDto scenarioCreateMinDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		if (result.hasErrors()) {
			model.addAttribute("scenarioCreateMinDto", scenarioCreateMinDto);
			String message = messageSource.getMessage("msg.html.resource.errorOnSaveFieldErrors", null, LocaleContextHolder.getLocale());
			model.addAttribute("flashMessageError", message);
			return "scenario/scenarioCreateMin";
		}

		String userId = user.getUserInfo().getSubject();
		Scenario scenario = scenarioDtoConverter.scenarioCreateMinDtoToEntity(scenarioCreateMinDto, userId);

		Scenario savedScenario;
		try {
			savedScenario = scenarioService.save(scenario, userId);
		} catch (RuntimeException e) {
			String message = messageSource.getMessage("msg.html.resource.errorOnSaveUnknownReason", new String[]{
					scenario.getName() }, LocaleContextHolder.getLocale());
			if (e instanceof DataIntegrityViolationException) {
				message = messageSource.getMessage("msg.html.scenario.errorOnSaveScenarioNameAlreadyExists", new String[]{
						scenario.getName() }, LocaleContextHolder.getLocale());
			}
			model.addAttribute("flashMessageError", message);				
			return "resource/resourceCreate";
		}
		String message = messageSource.getMessage("msg.html.global.message.successfullySaved", new String[]{
				savedScenario.getName() }, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("flashMessageSuccess", message);
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", savedScenario.getResourceScenario().getId());
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");

		return "redirect:/app/dashboard";
	}

	private Object[][] generateChartData(List<Scenario> scenarios) {
		Integer minYear = Integer.MAX_VALUE;
		Integer maxYear = Integer.MIN_VALUE;
		for (Scenario scenario : scenarios) {
			for (ScenarioYearBalance yearBalance : scenario.getYearBalances()) {
				if (yearBalance.getYear() < minYear) {
					minYear = yearBalance.getYear();
				}
				if (yearBalance.getYear() > maxYear) {
					maxYear = yearBalance.getYear();
				}
			}
		}
		
		int totalRows = maxYear-minYear+2;
		int totalColumns = 1 + scenarios.size();
		
		Object[][] chartData = new Object[totalRows][totalColumns];

		// First row is header row.
		chartData[0][0] = "Year";
		int firstRowCol = 1;
		for (Scenario scenario : scenarios) {
			chartData[0][firstRowCol] = scenario.getName();
			firstRowCol++;
		}

		int currentRow = 1;
		for (int year = minYear; year <= maxYear; year++) {
			chartData[currentRow][0] = year;
			int currentColumn = 1;
			for (Scenario scenario : scenarios) {
				chartData[currentRow][currentColumn] = scenario.getYearBalance(year);
				currentColumn++;
			}
			currentRow++;
		}
		
		return chartData;
	}
}
