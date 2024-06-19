package com.impwrme2.service.scenario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.model.scenario.ScenarioYearBalance;
import com.impwrme2.repository.scenario.ScenarioRepository;

import jakarta.persistence.EntityManager;

@Service
public class ScenarioService {

	@Autowired
	EntityManager entityManager;
	
	@Autowired
	private ScenarioRepository scenarioRepository;
	
	@Autowired
	private MessageSource messageSource;
	
	@Transactional(readOnly = true)
	public List<Scenario> findByUserId(String userId) {
		return scenarioRepository.findByUserId(userId);
	}

	@Transactional(readOnly = true)
	public Optional<Scenario> findByIdAndUserId(Long id, String userId) {
		return scenarioRepository.findByIdAndUserId(id, userId);
	}

	@Transactional(readOnly = false)
	public Scenario save(Scenario scenario, String userId) {
		if (duplicateScenarioName(scenario, userId)) {
			throw new DataIntegrityViolationException(messageSource.getMessage("msg.html.scenario.errorOnSaveScenarioNameAlreadyExists", 
					new String[] {scenario.getName() }, LocaleContextHolder.getLocale()));
		} else if (null != scenario.getId() && !scenario.getUserId().equals(userId)) {
			throw new UnsupportedOperationException(messageSource.getMessage("msg.validation.unauthorisedAccess", 
					null, LocaleContextHolder.getLocale()));			
		} else {
			return scenarioRepository.save(scenario);
		}
	}
	
	@Transactional(readOnly = false)
	public Scenario repopulateYearBalances(Scenario scenario, String userId, Set<ScenarioYearBalance> yearBalances) {
		if (null != scenario.getId() && !scenario.getUserId().equals(userId)) {
			throw new UnsupportedOperationException(messageSource.getMessage("msg.validation.unauthorisedAccess", null, LocaleContextHolder.getLocale()));			
		} else {
			scenario.getYearBalances().clear();
			Scenario scenario2 = scenarioRepository.save(scenario);
			entityManager.flush();			
			for (ScenarioYearBalance syb : yearBalances) {
				scenario2.addYearBalance(syb);
			}
			return scenarioRepository.save(scenario2);
		}
	}

	@Transactional(readOnly = false)
	public void delete(Scenario scenario, String userId) {
		if (scenario.getUserId().equals(userId)) {
			scenarioRepository.delete(scenario);
		} else {
			throw new UnsupportedOperationException(messageSource.getMessage("msg.validation.unauthorisedAccess", null, LocaleContextHolder.getLocale()));			
		}
	}

	/**
	 * See https://vladmihalcea.com/orphanremoval-jpa-hibernate for explanation of
	 * why this delete appears here and not in the ResourceService.
	 * @param resource The resource to be deleted.
	 */
	@Transactional(readOnly = false)
	public void deleteResource(Resource resource) {
		Scenario scenario = resource.getScenario();
		scenario.removeResource(resource);
		@SuppressWarnings("unused")
		Scenario savedScenario = scenarioRepository.save(scenario);
	}

	private boolean duplicateScenarioName(Scenario scenario, String userId) {
		List<Scenario> existingScenarios = findByUserId(userId);
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
}
