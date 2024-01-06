package com.impwrme2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impwrme2.model.scenario.Scenario;
import com.impwrme2.repository.scenario.ScenarioRepository;

@Service
public class ScenarioService {

	@Autowired
	private ScenarioRepository scenarioRepository;

	@Transactional(readOnly = false)
	public Scenario save(Scenario scenario) {
		return scenarioRepository.save(scenario);
	}
}
