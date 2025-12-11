package com.tablehub.thbackend.service.implementations;

import com.tablehub.thbackend.dto.request.TableUpdateRequest;
import com.tablehub.thbackend.model.*;
import com.tablehub.thbackend.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableStatusServiceImplTest {

    @Mock private RestaurantTableRepository tableRepository;
    @Mock private UserRepository userRepository;
    @Mock private ActionRepository actionRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private TableStatusServiceImpl tableStatusService;

    private RestaurantTable table;
    private AppUser user;
    private TableUpdateRequest request;

    @BeforeEach
    void setUp() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn("testUser");

        user = AppUser.builder().userName("testUser").points(0).lifetimePoints(0).build();

        RestaurantSection section = RestaurantSection.builder().id(1L).restaurant(Restaurant.builder().id(1L).build()).build();
        table = RestaurantTable.builder().id(100L).restaurantSection(section).status(TableStatus.AVAILABLE).build();

        request = new TableUpdateRequest();
        request.setTableId(100L);
        request.setSectionId(1L);
        request.setRestaurantId(1L);
    }

    @Test
    void updateTableStatus_UpdatesBothPointCounters() {
        request.setRequestedStatus(TableStatus.OCCUPIED);

        when(tableRepository.findByIdWithSectionAndRestaurant(100L)).thenReturn(Optional.of(table));
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));

        Action action = Action.builder().name(ActionType.REPORT_NEW).points((short) 10).build();
        when(actionRepository.findByName(ActionType.REPORT_NEW)).thenReturn(Optional.of(action));

        tableStatusService.updateTableStatus(request);

        assertEquals(10, user.getPoints(), "Current points should increase by 10");
        assertEquals(10, user.getLifetimePoints(), "Lifetime points should increase by 10");
    }
}