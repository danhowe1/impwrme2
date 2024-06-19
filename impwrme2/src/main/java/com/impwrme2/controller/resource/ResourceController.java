package com.impwrme2.controller.resource;

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
	ResourceCreateDtoFactory resourceCreateDtoFactory;
	
	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceService resourceService;
	
	@Autowired
	private ScenarioService scenarioService;

	@GetMapping(value = { "/showCreateForm/{resourceType}" })
	public String showCreateForm(@PathVariable String resourceType, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {

		Long currentResourceId = (Long) session.getAttribute("SESSION_CURRENT_RESOURCE_ID");
		Resource currentResource = resourceService.findById(currentResourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + currentResourceId));
		Scenario currentScenario = currentResource.getScenario();

		// Add default DTO to the model.
		model.addAttribute("resourceCreateDto", resourceCreateDtoFactory.createResourceCreateDto(currentScenario, resourceType));
		
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
//		return saveResource(resource, model, redirectAttributes, session);

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
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");

		return "redirect:/app/dashboard";
	}
	
	@PostMapping("/updateResourceName")
	public String updateResourceName(@RequestParam("resourceId") Long id, @RequestParam("resourceName") String resourceName,@AuthenticationPrincipal OidcUser user, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
		Resource resource = resourceService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + id));
		resource.setName(resourceName);
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
			redirectAttributes.addFlashAttribute("flashMessageError", message);
			return "redirect:/app/dashboard";
		}
		String message = messageSource.getMessage("msg.html.global.message.successfullySaved", new String[]{
				savedResource.getName() }, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("flashMessageSuccess", message);
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", savedResource.getId());
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");

		return "redirect:/app/dashboard";
	}
		
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
		Resource resource = resourceService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + id));
		Resource resourceScenario = resource.getScenario().getResourceScenario();
		scenarioService.deleteResource(resource);
		String message = messageSource.getMessage("msg.html.global.message.successfullyDeleted", new String[]{ resource.getName() }, LocaleContextHolder.getLocale());
		redirectAttributes.addFlashAttribute("flashMessageSuccess", message);
		session.setAttribute("SESSION_CURRENT_RESOURCE_ID", resourceScenario.getId());
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "redirect:/app/dashboard";
	}
}
