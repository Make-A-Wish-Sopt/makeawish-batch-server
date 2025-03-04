package com.makeawishbatchserver.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public class AlarmTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long templateId;
    private String templateContent;
    private String templateButtonUrl;
    private String templateVariables;
    private String templateCode;
    private String templateImageUrl;
    private String templateButton;
    private String templateName;
}
