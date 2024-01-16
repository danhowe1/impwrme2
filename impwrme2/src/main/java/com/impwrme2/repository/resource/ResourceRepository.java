package com.impwrme2.repository.resource;

import org.springframework.data.jpa.repository.JpaRepository;

import com.impwrme2.model.resource.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
