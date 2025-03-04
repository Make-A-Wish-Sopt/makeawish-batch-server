package com.makeawishbatchserver.repository;

import com.makeawishbatchserver.domain.AlarmTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlarmTemplateRepository extends JpaRepository<AlarmTemplate, Long> {
    Optional<AlarmTemplate> findAlarmTemplateByTemplateName(String templateName);
}
