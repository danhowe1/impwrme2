package com.impwrme2.controller.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.service.ui.UIDisplayFilter;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/dashboard/displayFilter")
public class DisplayFilterController {
	
	@GetMapping(value = { "/update/{filterValue}" })
    public String update(@PathVariable String filterValue, Model model, HttpSession session) {
    	UIDisplayFilter displayFilter = (UIDisplayFilter) session.getAttribute("SESSION_DISPLAY_FILTER");
    	
    	if (filterValue.equals(UIDisplayFilter.DISPLAY_STYLE_CHART)) {
        	displayFilter.setDisplayStyle(UIDisplayFilter.DISPLAY_STYLE_CHART);    		
    	} else if (filterValue.equals(UIDisplayFilter.DISPLAY_STYLE_BALANCES_TABLE)) {
        	displayFilter.setDisplayStyle(UIDisplayFilter.DISPLAY_STYLE_BALANCES_TABLE);    		
    	} else {
    		// TODO Handle error.
    	}
    	session.setAttribute("SESSION_DISPLAY_FILTER", displayFilter);
    	model.addAttribute("displayFilter", displayFilter);
		return "fragments/dashboard/dataDisplay :: dataDisplay";
    }
	
	@PostMapping(value = "/changeDisplayFilterYear")
	@ResponseBody
	public String changeDisplayFilterYear(@Valid Integer year, HttpSession session) {
    	UIDisplayFilter displayFilter = (UIDisplayFilter) session.getAttribute("SESSION_DISPLAY_FILTER");
		displayFilter.setYearEnd(year);
    	session.setAttribute("SESSION_DISPLAY_FILTER", displayFilter);
    	return "SUCCESS";
	}
}
