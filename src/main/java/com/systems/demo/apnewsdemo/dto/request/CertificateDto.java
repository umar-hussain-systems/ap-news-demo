package com.systems.demo.apnewsdemo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificateDto extends DocumentDto {
    String certificateId;
    String certificateName;
}
