package com.hanyang.arttherapy.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanyang.arttherapy.common.exception.*;
import com.hanyang.arttherapy.common.exception.exceptionType.*;
import com.hanyang.arttherapy.domain.*;
import com.hanyang.arttherapy.dto.request.*;
import com.hanyang.arttherapy.dto.response.*;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseDto;
import com.hanyang.arttherapy.dto.response.artistResponse.ArtistResponseListDto;
import com.hanyang.arttherapy.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArtistsService {

  private final ArtistsRepository artistsRepository;

  public void registerArtist(ArtistRequestDto dto) {
    isStudentNoDuplicate(dto.studentNo());
    Artists artist = convertToEntity(dto);
    saveArtist(artist);
  }

  public ArtistResponseDto getArtist(Long artistNo) {
    return ArtistResponseDto.of(findArtistById(artistNo));
  }

  public ArtistResponseListDto getArtists() {
    List<Artists> artists = artistsRepository.findAll();
    List<ArtistResponseDto> responseDtos =
        artists.stream().map(ArtistResponseDto::of).collect(Collectors.toList());
    return ArtistResponseListDto.of(responseDtos);
  }

  public void updateArtist(long artistsNo, ArtistRequestDto dto) {
    Artists artist = findArtistById(artistsNo);

    Optional.ofNullable(dto.studentNo())
        .filter(newStudentNo -> !newStudentNo.equals(artist.getStudentNo()))
        .ifPresent(this::isStudentNoDuplicate);

    updateArtistInfo(artist, dto);
    artistsRepository.save(artist);
  }

  public void deleteArtist(Long artistsNo) {
    Artists artist = findArtistById(artistsNo);
    artistsRepository.delete(artist);
  }

  private static void updateArtistInfo(Artists artist, ArtistRequestDto dto) {
    artist.updateArtistInfo(
        Optional.ofNullable(dto.artistName()),
        Optional.ofNullable(dto.studentNo()),
        Optional.ofNullable(dto.cohort()));
  }

  private Artists findArtistById(Long artistsNo) {
    return artistsRepository
        .findById(artistsNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  public Artists findByStudentNo(String studentNo) {
    return artistsRepository
        .findByStudentNo(studentNo)
        .orElseThrow(() -> new CustomException(ArtistsException.ARTIST_NOT_FOUND));
  }

  private Artists saveArtist(Artists artist) {
    try {
      return artistsRepository.save(artist);
    } catch (Exception e) {
      throw new CustomException(ArtistsException.INTERNAL_SERVER_ERROR);
    }
  }

  public void isStudentNoDuplicate(String studentNo) {
    if (artistsRepository.existsByStudentNo(studentNo)) {
      throw new CustomException(ArtistsException.DUPLICATE_STUDENT_NO);
    }
  }

  private Artists convertToEntity(ArtistRequestDto dto) {
    return Artists.builder()
        .artistName(dto.artistName())
        .studentNo(dto.studentNo())
        .cohort(dto.cohort())
        .build();
  }
}
