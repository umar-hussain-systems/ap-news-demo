package com.systems.demo.apnewsdemo.controller;

import com.systems.demo.apnewsdemo.dto.request.CreateSportsDto;
import com.systems.demo.apnewsdemo.dto.request.DocumentDto;
import com.systems.demo.apnewsdemo.dto.response.SportsDto;
import com.systems.demo.apnewsdemo.service.SportsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/** The type Sports controller. */
@RestController
@RequestMapping("/api/sports")
@RequiredArgsConstructor
@Slf4j
public class SportsController {

  private final SportsService sportsService;

  /**
   * Gets sport with multiple players.
   *
   * @param playerCount the player count
   * @return the sport with multiple playersMED
   */
  @GetMapping("/multiple-players/{playerCount}")
  public ResponseEntity<List<SportsDto>> getSportWithMultiplePlayers(
      @Valid @PathVariable(name = "playerCount") Integer playerCount) {
    return new ResponseEntity<>(
        sportsService.getSportsWithPlayersGreaterThanOrEqualTo(playerCount), HttpStatus.OK);
  }

  /**
   * No players enlisted response entity.
   *
   * @return the response entity
   */
  @GetMapping("/no-player-enlisted")
  public ResponseEntity<List<SportsDto>> noPlayersEnlisted() {
    return new ResponseEntity<>(sportsService.noPlayersEnlisted(), HttpStatus.OK);
  }

  /**
   * Gets sports by names.
   *
   * @param sportNames the sport names
   * @return the sports by names
   */
  @GetMapping("/order-by-names")
  public ResponseEntity<List<SportsDto>> getSportsByNames(
      @Valid @NotEmpty(message = "sportNames list cannot be empty") @RequestParam("sportNames")
          Set<String> sportNames) {
    Set<String> linkedHashSet = new LinkedHashSet<>(sportNames);
    return new ResponseEntity<>(sportsService.getSportsByName(linkedHashSet), HttpStatus.OK);
  }

  /**
   * Create sport response entity.
   *
   * @param createSportsDto the create sports dto
   * @return the response entity
   */
  // crud
  @PostMapping
  public ResponseEntity<SportsDto> createSport(
      @Valid @RequestBody CreateSportsDto createSportsDto) {
    return new ResponseEntity<>(sportsService.createSport(createSportsDto), HttpStatus.CREATED);
  }

  /**
   * Gets sport.
   *
   * @param id the id
   * @return the sport
   */
  @GetMapping("/{Id}")
  public ResponseEntity<SportsDto> getSport(
      @NotNull(message = "Id cannot be null") @PathVariable(name = "Id") Integer id) {
    return new ResponseEntity<>(sportsService.getSport(id), HttpStatus.OK);
  }

  /**
   * Delete sport response entity.
   *
   * @param id the id
   * @return the response entity
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteSport(@PathVariable(name = "id") Integer id) {
    sportsService.deleteSports(id);
    return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
  }

  @PostMapping(value = "/upload/",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_OCTET_STREAM_VALUE})
  public ResponseEntity<String>uploadFile(@RequestParam("files")MultipartFile file, @RequestPart("details") List<DocumentDto> documentDto) {
    log.info("file content type {}", file.getContentType());
    log.info("document Id {}",documentDto.get(0).getDocumentId());
    log.info("file name {}", file.getName());
    String sdf = "///";
    return new ResponseEntity<>(file.getName(), HttpStatus.OK);
  }
}
