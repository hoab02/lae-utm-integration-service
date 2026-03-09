package com.viettelpost.fms.utm_integration.repository;

import com.viettelpost.fms.utm_integration.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;

public interface ExampleRepository extends JpaRepository<Example, String>, ListQuerydslPredicateExecutor<Example> {
}
