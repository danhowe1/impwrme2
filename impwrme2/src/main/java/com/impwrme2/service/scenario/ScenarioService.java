package com.impwrme2.service.scenario;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.repository.scenario.ScenarioRepository;

@Service
public class ScenarioService {

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
		if (null != scenario.getId() && !scenario.getUserId().equals(userId)) {
			throw new UnsupportedOperationException(messageSource.getMessage("msg.validation.unauthorisedAccess", null, LocaleContextHolder.getLocale()));			
		} else {
			return scenarioRepository.save(scenario);
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
	@Transactional
	public void deleteResource(Resource resource) {
		Scenario scenario = resource.getScenario();
		scenario.removeResource(resource);
		@SuppressWarnings("unused")
		Scenario savedScenario = scenarioRepository.save(scenario);
	}
}
