package com.impwrme2.service.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.repository.resource.ResourceRepository;
import com.impwrme2.service.scenario.ScenarioService;

@Service
public class ResourceService {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired 
	private ScenarioService scenarioService;
	
	@Transactional(readOnly = true)
	public Optional<Resource> findById(Long id) {
		return resourceRepository.findById(id);
	}

	@Transactional(readOnly = false)
	public Resource save(Resource resource) {
		if (duplicateScenarioName(resource.getScenario())) {
			throw new DataIntegrityViolationException(messageSource.getMessage("msg.html.scenario.errorOnSaveScenarioNameAlreadyExists", 
					new String[] {resource.getScenario().getName() }, LocaleContextHolder.getLocale()));
		}
		return resourceRepository.save(resource);
	}

	private boolean duplicateScenarioName(Scenario scenario) {
		List<Scenario> existingScenarios = scenarioService.findByUserId(scenario.getUserId());
		for (Scenario existingScenario : existingScenarios) {
			if (scenario.getName().equals(existingScenario.getName())) {
				if (null == scenario.getId() ||
					!existingScenario.getId().equals(scenario.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
	 * why this delete appears here and not in the ResourceService.
	 * @param resource The resource to be deleted.
	 */
	@Transactional
	public void deleteResourceParam(ResourceParam<?> resourceParam) {
		Resource resource = resourceParam.getResource();
		resource.removeResourceParam(resourceParam);
		@SuppressWarnings("unused")
		Resource savedResource = resourceRepository.save(resource);
	}

	/**
	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
	 * why this delete appears here and not in the ResourceService.
	 * @param resource The resource to be deleted.
	 */
	@Transactional
	public void deleteCashflow(Cashflow cashflow) {
		Resource resource = cashflow.getResource();
		resource.removeCashflow(cashflow);
		@SuppressWarnings("unused")
		Resource savedResource = resourceRepository.save(resource);
	}
}
