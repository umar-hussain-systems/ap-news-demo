package com.systems.demo.apnewsdemo.dto.request;

import com.systems.demo.apnewsdemo.model.Gender;
import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** The type Create player dto. */
@Getter
@Setter
public class CreatePlayerDto {
  @Email(message = "please enter valid email address")
  private String email;

  @NotNull(message = "please enter age")
  private Integer age;

  @NotNull(message = "please enter player level")
  private Integer level;

  @NotNull(message = "please enter Gender Value")
  private Gender gender;
}
