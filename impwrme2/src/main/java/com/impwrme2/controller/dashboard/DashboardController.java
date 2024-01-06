package com.impwrme2.controller.dashboard;

import java.util.Locale;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/app/dashboard")
public class DashboardController {

	@GetMapping(value = {"", "/"})
	public String dashboard(@AuthenticationPrincipal OidcUser user, Model model,  HttpSession session, Locale locale) {

		return "dashboard/dashboard";
	}
}
