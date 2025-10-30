package com.tablehub.thbackend.ui.view;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.repo.AddressRepository;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.service.interfaces.GeocodingService;
import com.tablehub.thbackend.ui.component.RestaurantForm;
import com.tablehub.thbackend.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/restaurants", layout = MainLayout.class)
@PageTitle("Restaurant Management | TableHub CMS")
@RolesAllowed({"ROLE_ADMIN", "ROLE_OWNER"})
public class RestaurantAdminView extends VerticalLayout {

    private final RestaurantRepository restaurantRepo;
    private final Grid<Restaurant> grid = new Grid<>(Restaurant.class);
    private final RestaurantForm form;

    public RestaurantAdminView(RestaurantRepository restaurantRepo,
                               AddressRepository addressRepo,
                               GeocodingService geocodingService) {
        this.restaurantRepo = restaurantRepo;

        this.form = new RestaurantForm(geocodingService, restaurantRepo, addressRepo);

        form.addSaveListener(e -> {
            refreshGrid();
            form.setRestaurant(null);
        });

        setSizeFull();
        configureGrid();

        Button addRestaurantButton = new Button("Add New Restaurant",
                click -> form.setRestaurant(new Restaurant())
        );

        HorizontalLayout mainContent = new HorizontalLayout(grid, form);
        mainContent.setSizeFull();
        grid.setSizeFull();

        add(addRestaurantButton, mainContent);

        refreshGrid();
        form.setRestaurant(null);
    }

    private void configureGrid() {
        grid.setColumns("id", "name");
        grid.addColumn(r -> r.getAddress() != null ? r.getAddress().getCity() : "N/A").setHeader("City");
        grid.addColumn(r -> r.getCuisineName()).setHeader("Cuisine");

        grid.addComponentColumn(restaurant ->
                new Button("Edit Layout", click -> {
                    getUI().ifPresent(ui ->
                            ui.navigate("admin/layout/" + restaurant.getId())
                    );
                })
        ).setHeader("Layout");

        grid.asSingleSelect().addValueChangeListener(e ->
                form.setRestaurant(e.getValue())
        );
    }

    private void refreshGrid() {
        grid.setItems(restaurantRepo.findAll());
    }
}
