package com.impwrme2.controller.dashboard;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.impwrme2.controller.BaseController;
import com.impwrme2.controller.dto.cashflow.CashflowDtoConverter;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDtoConverter;
import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.service.cashflow.CashflowService;
import com.impwrme2.service.cashflowDateRangeValue.CashflowDateRangeValueService;
import com.impwrme2.service.resource.ResourceService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/dashboard/cashflow")
public class CashflowController {

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private CashflowDtoConverter cashflowDtoConverter;

	@Autowired
	private CashflowDateRangeValueDtoConverter cashflowDateRangeValueDtoConverter;

	@Autowired
	protected ResourceService resourceService;

	@Autowired
	private CashflowService cashflowService;

	@Autowired
	private CashflowDateRangeValueService cashflowDateRangeValueService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = { "/showCashflows/{resourceId}" })
	public String showCashflows(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		setUpModelAfterCashflowsUpdated(resource, model, session);
		return "fragments/dashboard/cashflows :: cashflows";
	}
	
	@PostMapping(value = "/saveCashflowDateRangeValue")
	@ResponseBody
	public String saveCashflowDateRangeValue(@Valid CashflowDateRangeValueDto cfdrvDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		if (result.hasErrors()) {
			return BaseController.getErrorMessageFromBindingResult(messageSource, result);
		}
		
		Cashflow cashflow = cashflowService.findById(cfdrvDto.getCashflowId()).get();
		CashflowDateRangeValue cfdrv = cashflowDateRangeValueDtoConverter.dtoToEntity(cfdrvDto);		
		List<CashflowDateRangeValue> cfdrvsToDelete = new ArrayList<CashflowDateRangeValue>();

		for (CashflowDateRangeValue existingCfdrv : cashflow.getCashflowDateRangeValues()) {
			if (!existingCfdrv.getId().equals(cfdrv.getId()) && datesOverlap(existingCfdrv, cfdrv)) {
				// We've found a different cfdrv and the dates overlap in some way.
				if (existingCfdrv.getYearMonthStart().isBefore(cfdrv.getYearMonthStart())) {
					// Existing cfdrv is before this one starts so just need to make sure it's end date is removed.
					existingCfdrv.setYearMonthEnd(null);
					cashflowDateRangeValueService.save(existingCfdrv);
				} else {
					// The existing one starts after this one so mark it for deletion.
					cfdrvsToDelete.add(existingCfdrv);
				}
			}
		}
		
		for (CashflowDateRangeValue c : cfdrvsToDelete) {
			cashflowService.deleteCashflowDateRangeValue(c);
		}
		
		cashflowDateRangeValueService.save(cfdrv);
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "SUCCESS";
	}

	@PostMapping(value = "/deleteCashflowDateRangeValue")
	@ResponseBody
	public String deleteCashflowDateRangeValue(@Valid CashflowDateRangeValueDto cfdrvDto, HttpSession session) {
		CashflowDateRangeValue cfdrv = cashflowDateRangeValueService.findById(cfdrvDto.getId()).get();
		if (cfdrv.getCashflow().getResource().getStartYearMonth().equals(cfdrv.getYearMonthStart())) {
			return messageSource.getMessage("msg.validation.cashflowDateRangeValue.deleteNotAllowed", null, LocaleContextHolder.getLocale());
		}
		cashflowService.deleteCashflowDateRangeValue(cfdrv);
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "SUCCESS";
	}

	private void setUpModelAfterCashflowsUpdated(Resource resource, Model model, HttpSession session) {
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("cashflowTableDto", cashflowDtoConverter.cashflowsToCashflowTableDto(resource.getCashflows()));
		model.addAttribute("cashflowDateRangeValueDto", new CashflowDateRangeValueDto());
	}

	/**
	 * Check for strictly overlapping dates. See https://www.baeldung.com/java-check-two-date-ranges-overlap.
	 * @param firstCfdrv The first CashflowDateRange value to be compared.
	 * @param secondCfdrv The second CashflowDateRange value to be compared.
	 * @return true if any of the dates overlap.
	 */
	private boolean datesOverlap(CashflowDateRangeValue firstCfdrv, CashflowDateRangeValue secondCfdrv) {
		YearMonth A = firstCfdrv.getYearMonthStart();
		YearMonth B = null != firstCfdrv.getYearMonthEnd() ? firstCfdrv.getYearMonthEnd() : YearMonth.of(5000, 1);
		YearMonth C = secondCfdrv.getYearMonthStart();
		YearMonth D = null != secondCfdrv.getYearMonthEnd() ? secondCfdrv.getYearMonthEnd() : YearMonth.of(5000, 1);
		return !(B.isBefore(C) || A.isAfter(D));
	}	
}
