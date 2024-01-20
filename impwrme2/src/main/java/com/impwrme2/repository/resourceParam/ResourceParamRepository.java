package com.impwrme2.repository.resourceParam;

import org.springframework.data.jpa.repository.JpaRepository;

import com.impwrme2.model.resourceParam.ResourceParam;

public interface ResourceParamRepository extends JpaRepository<ResourceParam<?>, Long> {

}
