package com.impwrme2.repository.scenario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.impwrme2.model.scenario.Scenario;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

	public List<Scenario> findByUserId(String userId);

	public Optional<Scenario> findByIdAndUserId(Long id, String userId);
}
