package com.impwrme2.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.impwrme2.controller.quickSnapshot.dto.QuickSnapshotDto;

@Controller
@RequestMapping("/")
public class BaseController {

	@GetMapping(value = {"", "/", "/index"})
	public String index(Model model) {
		
		// Add default DTO to the model.
		model.addAttribute("quickSnapshotDto", new QuickSnapshotDto());

		return "index";
	}

	public static String getErrorMessageFromBindingResult(final MessageSource messageSource, final BindingResult result) {
		for (Object object : result.getAllErrors()) {
			if (object instanceof FieldError) {
				FieldError fieldError = (FieldError) object;
				return fieldError.getDefaultMessage();
			}
			if (object instanceof ObjectError) {
				ObjectError objectError = (ObjectError) object;
				return objectError.getDefaultMessage();
			}
		}
		return messageSource.getMessage("msg.validation.unknownError", null, LocaleContextHolder.getLocale());
	}
}