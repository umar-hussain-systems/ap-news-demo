package com.systems.demo.apnewsdemo.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.systems.demo.apnewsdemo.constant.DocumentType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = RecordDto.class,name = "Record"),
    @JsonSubTypes.Type(value = CertificateDto.class,name = "Certification")
})
public abstract class DocumentDto {
private DocumentType documentType;
private String documentId;
private String documentName;
}
