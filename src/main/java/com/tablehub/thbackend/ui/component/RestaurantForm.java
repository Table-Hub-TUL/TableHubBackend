package com.tablehub.thbackend.ui.component;

import com.tablehub.thbackend.model.Address;
import com.tablehub.thbackend.model.CuisineName;
import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.repo.AddressRepository;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.service.interfaces.GeocodingService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.locationtech.jts.geom.Point;

import java.util.Objects;

public class RestaurantForm extends FormLayout {

    private final GeocodingService geocodingService;
    private final RestaurantRepository restaurantRepo;
    private final AddressRepository addressRepo;

    TextField name = new TextField("Restaurant Name");
    ComboBox<CuisineName> cuisineName = new ComboBox<>("Cuisine");
    NumberField streetNumber = new NumberField("Street Number");
    TextField street = new TextField("Street Name");
    TextField city = new TextField("City");
    TextField postalCode = new TextField("Postal Code");
    TextField country = new TextField("Country");

    Binder<Restaurant> restaurantBinder = new Binder<>(Restaurant.class);
    Binder<Address> addressBinder = new Binder<>(Address.class);

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    public RestaurantForm(GeocodingService geocodingService,
                          RestaurantRepository restaurantRepo,
                          AddressRepository addressRepo) {
        this.geocodingService = geocodingService;
        this.restaurantRepo = restaurantRepo;
        this.addressRepo = addressRepo;

        cuisineName.setItems(CuisineName.values());

        name.setRequired(true);
        cuisineName.setRequired(true);
        street.setRequired(true);
        streetNumber.setRequired(true);
        city.setRequired(true);
        postalCode.setRequired(true);
        country.setRequired(true);

        add(name, cuisineName, street, streetNumber, city, postalCode, country, createButtonLayout());

        restaurantBinder.forField(name)
                .withValidator(s -> s != null && !s.trim().isEmpty(), "Restaurant name is required.")
                .bind(Restaurant::getName, Restaurant::setName);

        restaurantBinder.forField(cuisineName)
                .withValidator(Objects::nonNull, "Cuisine is required.")
                .bind(Restaurant::getCuisineName, Restaurant::setCuisineName);

        addressBinder.forField(streetNumber)
                .withValidator(val -> val != null && val > 0, "Street number is required and must be positive.")
                .withConverter(
                        Double::intValue,
                        Integer::doubleValue
                )
                .bind(Address::getStreetNumber, Address::setStreetNumber);

        addressBinder.forField(street)
                .withValidator(s -> s != null && !s.trim().isEmpty(), "Street name is required.")
                .bind(Address::getStreet, Address::setStreet);

        addressBinder.forField(city)
                .withValidator(s -> s != null && !s.trim().isEmpty(), "City is required.")
                .bind(Address::getCity, Address::setCity);

        addressBinder.forField(postalCode)
                .withValidator(s -> s != null && !s.trim().isEmpty(), "Postal code is required.")
                .bind(Address::getPostalCode, Address::setPostalCode);

        addressBinder.forField(country)
                .withValidator(s -> s != null && !s.trim().isEmpty(), "Country is required.")
                .bind(Address::getCountry, Address::setCountry);

        setRestaurant(null);

        save.addClickListener(e -> {
            if (restaurantBinder.validate().hasErrors() || addressBinder.validate().hasErrors()) {
                Notification.show("Please fix validation errors.", 3000, Notification.Position.MIDDLE);
                return;
            }
            saveRestaurant();
        });

        cancel.addClickListener(e -> setRestaurant(null));
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return new HorizontalLayout(save, cancel);
    }

    public void setRestaurant(Restaurant restaurant) {
        if (restaurant == null) {
            setVisible(false);
        } else {
            setVisible(true);
            restaurantBinder.setBean(restaurant);
            addressBinder.setBean(restaurant.getAddress() != null ? restaurant.getAddress() : new Address());
            name.focus();
        }
    }

    private void saveRestaurant() {
        try {
            Restaurant restaurant = restaurantBinder.getBean();
            Address address = addressBinder.getBean();

            String fullAddress = String.format("%s %s, %s, %s, %s",
                    streetNumber.getValue(), street.getValue(), city.getValue(), postalCode.getValue(), country.getValue());

            Point location = geocodingService.getCoordinatesFromAddress(fullAddress);
            if (location == null) {
                Notification.show("Error: Could not geocode address.", 3000, Notification.Position.MIDDLE);
                return;
            }
            restaurant.setLocation(location);

            Address savedAddress = addressRepo.save(address);
            restaurant.setAddress(savedAddress);
            restaurantRepo.save(restaurant);

            Notification.show("Restaurant saved successfully.", 2000, Notification.Position.MIDDLE);

            fireEvent(new SaveEvent(this));

        } catch (Exception e) {
            Notification.show("An error occurred: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    public static abstract class RestaurantFormEvent extends ComponentEvent<RestaurantForm> {
        private RestaurantFormEvent(RestaurantForm source) {
            super(source, false);
        }
    }

    public static class SaveEvent extends RestaurantFormEvent {
        SaveEvent(RestaurantForm source) {
            super(source);
        }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
}