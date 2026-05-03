package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    private MediaVm mediaVm;

    @BeforeEach
    void setUp() {
        mediaVm = new MediaVm(1L, "caption", "file.gif", "image/gif", "/url");
    }

    @Test
    void create_WhenValid_ShouldReturnNoFileMediaVm() {
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile", "file.gif", "image/gif", new byte[0]);
        MediaPostVm mediaPostVm = new MediaPostVm("caption", file, "file.gif");

        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("file.gif");
        media.setMediaType("image/gif");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        ResponseEntity<Object> response = mediaController.create(mediaPostVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        NoFileMediaVm noFileMediaVm = (NoFileMediaVm) response.getBody();
        assertNotNull(noFileMediaVm);
        assertEquals(1L, noFileMediaVm.id());
    }

    @Test
    void delete_WhenValidId_ShouldReturnNoContent() {
        doNothing().when(mediaService).removeMedia(1L);

        ResponseEntity<Void> response = mediaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void get_WhenValidId_ShouldReturnMediaVm() {
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    void get_WhenInvalidId_ShouldReturnNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getByIds_WhenValidIds_ShouldReturnListOfMediaVm() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of(mediaVm));

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getByIds_WhenEmptyResult_ShouldReturnNotFound() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getFile_WhenValid_ShouldReturnFileContent() {
        MediaDto mediaDto = MediaDto.builder()
                .content(new ByteArrayInputStream(new byte[0]))
                .mediaType(MediaType.IMAGE_GIF)
                .build();
        
        when(mediaService.getFile(1L, "file.gif")).thenReturn(mediaDto);

        ResponseEntity<InputStreamResource> response = mediaController.getFile(1L, "file.gif");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
