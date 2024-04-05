package com.impwrme2.controller.dashboard;

import java.time.YearMonth;
import java.util.Optional;

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
import com.impwrme2.controller.dto.resource.ResourceDtoConverter;
import com.impwrme2.controller.dto.resourceParam.ResourceParamCreateDto;
import com.impwrme2.controller.dto.resourceParam.ResourceParamDtoConverter;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.resource.ResourceService;
import com.impwrme2.service.resourceParam.ResourceParamService;
import com.impwrme2.service.resourceParamDateValue.ResourceParamDateValueService;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/dashboard/resourceParam")
public class ResourceParamController {

	@Autowired
	private ResourceDtoConverter resourceDtoConverter;

	@Autowired
	private ResourceParamDtoConverter resourceParamDtoConverter;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private ResourceParamService resourceParamService;

	@Autowired
	private ResourceParamDateValueService resourceParamDateValueService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping(value = { "/showResourceParams/{resourceId}" })
	public String showResourceParams(@PathVariable Long resourceId, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		Resource resource = resourceService.findById(resourceId).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceId));
		setUpModelAfterResourceParamsUpdated(resource, model, session);
		return "fragments/dashboard/resourceParams :: resourceParams";
	}

	@PostMapping(value = "/saveResourceParamCreate")
	@ResponseBody
	public String saveCashflowCreate(@Valid ResourceParamCreateDto resourceParamCreateDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		if (result.hasErrors()) {
			return BaseController.getErrorMessageFromBindingResult(messageSource, result);
		}
		Resource resource = resourceService.findById(resourceParamCreateDto.getResourceId()).orElseThrow(() -> new IllegalArgumentException("Invalid resource id:" + resourceParamCreateDto.getResourceId()));
		ResourceParam<?> resourceParam = resourceParamDtoConverter.dtoToNewEntity(resourceParamCreateDto, resource);
		resourceParamService.save(resourceParam);
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "SUCCESS";
	}

	@PostMapping(value = "/saveResourceParamDateValue")
	@ResponseBody
	public String saveResourceParamDateValue(@Valid ResourceParamDateValueDto rpdvDto, BindingResult result, @AuthenticationPrincipal OidcUser user, Model model, HttpSession session) {
		if (result.hasErrors()) {
			return BaseController.getErrorMessageFromBindingResult(messageSource, result);
		}
		if (null != rpdvDto.getId()) {
			updateExistingRpdv(rpdvDto);
		} else {
			saveNewRpdvOrUpdateExistingRpdv(rpdvDto);
		}
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "SUCCESS";
	}

	@PostMapping(value = "/deleteResourceParamDateValue")
	@ResponseBody
	public String deleteResourceParamDateValue(@Valid ResourceParamDateValueDto rpdvDto, HttpSession session) {
		if (!rpdvDto.isUserAbleToChangeDate()) {
			return messageSource.getMessage("msg.validation.resourceParamDateValue.deleteNotAllowed", null, LocaleContextHolder.getLocale());
		}
		ResourceParamDateValue<?> rpdv = resourceParamDateValueService.findById(rpdvDto.getId()).get();
		ResourceParam<?> resourceParam = rpdv.getResourceParam();
		if (resourceParam.getResourceParamDateValues().size() == 1) {
			// We're deleting the only remaining date/value so delete the resource param.
			resourceService.deleteResourceParam(resourceParam);
		} else {
			// Just delete the date/value.
			resourceParamService.deleteResourceParamDateValue(rpdv);			
		}
		session.removeAttribute("SESSION_JOURNAL_ENTRY_RESPONSE");
		return "SUCCESS";
	}

	private void setUpModelAfterResourceParamsUpdated(Resource resource, Model model, HttpSession session) {
		model.addAttribute("resourceDto", resourceDtoConverter.entityToDto(resource));
		model.addAttribute("resourceParamTableDto", resourceParamDtoConverter.resourceParamsToResourceParamTableDto(resource.getResourceParams()));
		model.addAttribute("resourceParamDateValueDto", new ResourceParamDateValueDto());
		model.addAttribute("resourceParamCreateDto", new ResourceParamCreateDto());
	}

	/**
	 * An existing RPDV is being updated (i.e. the id of the RPDV already exists).
	 * 
	 * If there exists another RPDV with the same date, then the RPDV being updated here is deleted and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * If there doesn't exist another RPDV with the same date then the RPDV being updated by the user has both the date and value updated.
	 * 
	 * @param rpdvDto The ResourceParamDateValueDto object containing the updated data.
	 */
	private void updateExistingRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParamDateValue<?> rpdvOfCurrentDto = resourceParamDateValueService.findById(rpdvDto.getId()).get();
		Optional<Long> idOfAnotherRpdvWithSameDate = rpdvOfCurrentDto.getResourceParam().getIdOfRpdvWithDuplicateDate(rpdvDto.getId(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()));
		if (!idOfAnotherRpdvWithSameDate.isEmpty()) {
			deletePassedRpdvAndUpdateExistingWithSameDate(rpdvOfCurrentDto, idOfAnotherRpdvWithSameDate.get(), rpdvDto.getValue());
		} else {
			updateRpdvDateValue(rpdvOfCurrentDto, YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()), rpdvDto.getValue());
		}
	}

	/**
	 * Another RPDV with the same date exists so the RPDV being updated here is deleted and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * @param resourceParamDateValueToDelete  The RPDV that the user is currently trying to update. This will be deleted.
	 * @param idOfPreExistingRpdvWithSameDate The ID of the pre-existing RPDV that has the same date.
	 * @param newValue                        The new value the user has supplied. The existing RPDV will be updated with this value.
	 */
	private void deletePassedRpdvAndUpdateExistingWithSameDate(ResourceParamDateValue<?> resourceParamDateValueToDelete, Long idOfPreExistingRpdvWithSameDate, String newValue) {
		// Delete the rpdv we've been passed as it's now a duplicate.
		resourceParamService.deleteResourceParamDateValue(resourceParamDateValueToDelete);

		// Update the value on the existing rpdv & save.
		ResourceParamDateValue<?> preExistingRpdv = resourceParamDateValueService.findById(idOfPreExistingRpdvWithSameDate).get();
		updateRpdvValueOnly(preExistingRpdv, newValue);
	}

	/**
	 * An new RPDV is attempting to bre created (i.e. the id of the RPDV is null).
	 * 
	 * If there exists another RPDV with the same date, then the RPDV being updated here is ignored and the existing one is updated with the value from the passed rpdvDto.
	 * 
	 * If there doesn't exist another RPDV with the same date then the RPDV being passed by the user is created with the passed date and value updated.
	 * 
	 * @param rpdvDto The ResourceParamDateValueDto object containing the new data.
	 */
	private void saveNewRpdvOrUpdateExistingRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParam<?> resourceParamOfCurrentDto = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		Optional<Long> idOfAnotherRpdvWithSameDate = resourceParamOfCurrentDto.getIdOfRpdvWithDuplicateDate(rpdvDto.getId(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth()));
		if (!idOfAnotherRpdvWithSameDate.isEmpty()) {
			ResourceParamDateValue<?> preExistingRpdv = resourceParamDateValueService.findById(idOfAnotherRpdvWithSameDate.get()).get();
			updateRpdvValueOnly(preExistingRpdv, rpdvDto.getValue());
		} else {
			createBrandNewRpdv(rpdvDto);
		}
	}

	/**
	 * Create a brand new RPDV
	 * 
	 * @param rpdvDto The RPDV containing the new data.
	 */
	private void createBrandNewRpdv(ResourceParamDateValueDto rpdvDto) {
		ResourceParam<?> resourceParam = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		ResourceParamDateValue<?> resourceParamDateValue = resourceParamDtoConverter.dtoToEntity(rpdvDto);
		resourceParam.addResourceParamDateValueGeneric(resourceParamDateValue);
		resourceParamDateValueService.save(resourceParamDateValue);
	}

	private void updateRpdvValueOnly(ResourceParamDateValue<?> resourceParamDateValue, String newValue) {
		updateRpdvDateValue(resourceParamDateValue, resourceParamDateValue.getYearMonth(), newValue);
	}

	private void updateRpdvDateValue(ResourceParamDateValue<?> resourceParamDateValue, YearMonth newYearMonth, String newValue) {
		resourceParamDateValue.setYearMonth(newYearMonth);
		resourceParamDateValue.setValueFromString(newValue);
		resourceParamDateValueService.save(resourceParamDateValue);
	}
}
