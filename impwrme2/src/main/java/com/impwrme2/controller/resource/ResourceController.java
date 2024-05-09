package com.impwrme2.controller.resource;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.impwrme2.controller.dto.resource.ResourceCreateDto;
import com.impwrme2.controller.dto.resource.ResourceCreateDtoFactory;
import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.scenario.ScenarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/dashboard/resource")
public class ResourceController {

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ScenarioService scenarioService;

	@GetMapping(value = { "/showCreateForm/{resourceType}" })
	public String showCreateForm(@PathVariable String resourceType, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {

		String userId = user.getUserInfo().getSubject();
		Scenario currentScenario;
		Long currentScenarioId = (Long) session.getAttribute("SESSION_CURRENT_SCENARIO_ID");
		if (null == currentScenarioId) {
			List<Scenario> scenarios = scenarioService.findByUserId(userId);
			if (scenarios.size() == 1) {
				currentScenario = scenarios.get(0);
				session.setAttribute("SESSION_CURRENT_SCENARIO_ID", currentScenario.getId());
			} else {
				return "forward:/app/scenario/list";
			}
		} else {
			currentScenario = scenarioService.findByIdAndUserId(currentScenarioId, userId).orElseThrow(() -> new IllegalArgumentException("Invalid scenario id:" + currentScenarioId));			
		}

		// Add default DTO to the model.
		model.addAttribute("resourceCreateDto", ResourceCreateDtoFactory.createResourceCreateDto(currentScenario, resourceType));
		
		// Send the user to the resource edit page.
		return "resource/resourceCreate";
	}

	@PostMapping("/save")
	public String save(@Valid ResourceCreateDto resourceCreateDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		if (result.hasErrors()) {
			model.addAttribute("resourceCreateDto", resourceCreateDto);
			String message = messageSource.getMessage("msg.html.resource.errorOnSaveFieldErrors", null, LocaleContextHolder.getLocale());
			model.addAttribute("flashMessageError", message);
			return "resource/resourceCreate";
		}

		String userId = user.getUserInfo().getSubject();
		Resource resource = resourceDtoConverter.resourceCreateDtoToEntity(resourceCreateDto, userId);

		Resource savedResource;
		try {
			savedResource = resourceService.save(resource);
		} catch (RuntimeException e) {
			String message = messageSource.getMessage("msg.html.resource.errorOnSaveUnknownReason", new String[]{
					resource.getName() }, LocaleContextHolder.getLocale());
			if (e instanceof DataIntegrityViolationException) {
				message = messageSource.getMessage("msg.html.resource.errorOnSaveResourceNameAlreadyExists", new String[]{
						resource.getName() }, LocaleContextHolder.getLocale());
			}
			model.addAttribute("flashMessageError", message);				
			return "resource/resourceCreate";
		}
		String message = messageSource.getMessage("msg.html.global.message.successfullySaved", new String[]{
				savedResource.getName() }, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("flashMessageSuccess", message);
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", savedResource.getId());

		return "redirect:/app/dashboard";
	}
}
