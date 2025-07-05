package com.hanyang.arttherapy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hanyang.arttherapy.dto.request.*;
import com.hanyang.arttherapy.dto.response.artistResponse.*;
import com.hanyang.arttherapy.service.ArtistsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin/artists")
@RequiredArgsConstructor
public class ArtistController {

  private final ArtistsService artistsService;

  @PostMapping
  public ResponseEntity<Void> registerArtist(@RequestBody ArtistRequestDto dto) {
    artistsService.registerArtist(dto);
    return ResponseEntity.status(201).build();
  }

  @GetMapping("{artistsNo}")
  public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long artistsNo) {
    return ResponseEntity.ok(artistsService.getArtist(artistsNo));
  }

  @GetMapping
  public ResponseEntity<ArtistResponseListDto> getArtists() {
    return ResponseEntity.ok(artistsService.getArtists());
  }

  @PatchMapping("/{artistsNo}")
  public ResponseEntity<Void> updateArtist(
      @PathVariable Long artistsNo, @RequestBody ArtistRequestDto dto) {
    artistsService.updateArtist(artistsNo, dto);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{artistsNo}")
  public ResponseEntity<Void> deleteArtist(@PathVariable Long artistsNo) {
    artistsService.deleteArtist(artistsNo);
    return ResponseEntity.noContent().build();
  }
}
