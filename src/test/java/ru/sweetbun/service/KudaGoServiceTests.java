package ru.sweetbun.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.sweetbun.entity.Identifiable;
import ru.sweetbun.storage.Storage;
import ru.sweetbun.storage.StorageObserver;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KudaGoServiceTests {

    @Mock
    private Storage<Identifiable> storage;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StorageObserver<Identifiable> observer;

    @InjectMocks
    KudaGoService<Identifiable> kudaGoService;

    private Identifiable entity;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        entity = mock(Identifiable.class);
        kudaGoService.addObserver(observer);
    }

    @Test
    void fetchAll_ValidURL_ReturnsEntities() {
        String URL = "https://kudago.com/public-api/v1.4/locations";
        Identifiable[] identifiables = new Identifiable[]{entity};
        when(restTemplate.getForObject(URL, Identifiable[].class)).thenReturn(identifiables);

        var res = kudaGoService.fetchAll(URL, Identifiable[].class);

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
        verify(restTemplate, times(1)).getForObject(URL, Identifiable[].class);
        verify(observer, times(1)).onEntityCreated(entity);
    }

    @Test
    void fetchAll_NullResponse_ReturnsEmptyList() {
        String URL = "https://kudago.com/public-api/v1.4/locations";
        when(restTemplate.getForObject(URL, Identifiable[].class)).thenReturn(null);

        var res = kudaGoService.fetchAll(URL, Identifiable[].class);

        assertNull(res);
        verify(restTemplate, times(1)).getForObject(URL, Identifiable[].class);
        verify(observer, never()).onEntityCreated(any());
    }

    @Test
    void findAll_EntitiesExist_ReturnAllEntities() {
        when(storage.findAll()).thenReturn(Collections.singletonMap(1L, entity));

        var res = kudaGoService.findAll();

        assertFalse(res.isEmpty());
        assertEquals(1, res.size());
    }

    @Test
    void findAll_EntitiesNotExist_ReturnNull() {
        when(storage.findAll()).thenReturn(Collections.emptyMap());

        var res = kudaGoService.findAll();

        assertTrue(res.isEmpty());
    }

    @Test
    void findById_IdExists_ReturnsEntity() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.ofNullable(entity));

        ResponseEntity<Identifiable> response = kudaGoService.findById(id);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(entity, response.getBody());
    }

    @Test
    void findById_IdNotFound_ReturnsNotFound() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Identifiable> response = kudaGoService.findById(id);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertNull(response.getBody());
    }

    @Test
    void create_ValidEntity_EntityCreated() {
        doNothing().when(observer).onEntityCreated(entity);

        ResponseEntity<Identifiable> res = kudaGoService.create(entity);

        assertTrue(res.getStatusCode().is2xxSuccessful());
        verify(observer, times(1)).onEntityCreated(entity);
    }

    @Test
    void update_IdExists_EntityUpdated() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.ofNullable(entity));
        doNothing().when(observer).onEntityUpdated(id, entity);

        var res = kudaGoService.update(id, entity);

        assertTrue(res.getStatusCode().is2xxSuccessful());
        verify(entity, times(1)).setId(id);
        verify(observer, times(1)).onEntityUpdated(id, entity);
    }

    @Test
    void update_IdNotFound_ReturnsNotFound() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.empty());

        var res = kudaGoService.update(id, entity);

        assertTrue(res.getStatusCode().is4xxClientError());
        verify(observer, never()).onEntityUpdated(anyLong(), any());
    }

    @Test
    void delete_IdExists_EntityDeleted() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.ofNullable(entity));
        doNothing().when(observer).onEntityDeleted(id);

        var res = kudaGoService.delete(id);

        assertTrue(res.getStatusCode().is2xxSuccessful());
        verify(observer, times(1)).onEntityDeleted(id);
    }

    @Test
    void delete_IdNotFound_ReturnsNotFound() {
        Long id = 1L;
        when(storage.findById(id)).thenReturn(Optional.empty());

        var res = kudaGoService.delete(id);

        assertTrue(res.getStatusCode().is4xxClientError());
        verify(observer, never()).onEntityDeleted(anyLong());
    }
}