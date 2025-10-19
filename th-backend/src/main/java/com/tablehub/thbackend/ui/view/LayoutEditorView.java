package com.tablehub.thbackend.ui.view;

import com.tablehub.thbackend.model.Position;
import com.tablehub.thbackend.model.RestaurantSection;
import com.tablehub.thbackend.model.RestaurantTable;
import com.tablehub.thbackend.model.SectionName;
import com.tablehub.thbackend.model.TableStatus;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Optional;

@Route(value = "admin/layout", layout = MainLayout.class)
@PageTitle("Layout Editor | TableHub CMS")
@RolesAllowed({"ROLE_ADMIN", "ROLE_OWNER"})
public class LayoutEditorView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RestaurantRepository restaurantRepo;
    private final RestaurantTableRepository tableRepo;

    private Long restaurantId;
    private RestaurantSection currentSection;

    // UI Components
    private Div canvas = new Div();
    private ComboBox<RestaurantSection> sectionSelector = new ComboBox<>("Select Section");
    private IntegerField capacityField = new IntegerField("Capacity");
    private Button addTableButton = new Button("Add Table");
    private Button saveLayoutButton = new Button("Save Layout");

    // We'll use a simple component for tables in the MVP
    private static class DraggableTable extends Div {
        public RestaurantTable table;
        public DraggableTable(RestaurantTable table) {
            this.table = table;
            setText(String.valueOf(table.getCapacity()));
            // Basic styling
            getStyle().set("width", "50px").set("height", "50px")
                    .set("background", "lightblue").set("border", "1px solid black")
                    .set("position", "absolute")
                    .set("left", table.getPosition().getX() + "px")
                    .set("top", table.getPosition().getY() + "px");

            // In a real app, you'd add a drag-and-drop library here.
            // For the MVP, we just load and save positions.
        }
    }

    public LayoutEditorView(RestaurantRepository restaurantRepo, RestaurantTableRepository tableRepo) {
        this.restaurantRepo = restaurantRepo;
        this.tableRepo = tableRepo;

        // Configure Canvas
        canvas.getStyle().set("width", "800px").set("height", "600px")
                .set("border", "1px solid black").set("position", "relative")
                .set("background", "#f4f4f4");

        // Configure Controls
        HorizontalLayout controls = new HorizontalLayout(sectionSelector, capacityField, addTableButton, saveLayoutButton);
        add(controls, canvas);

        // Event Handlers
        sectionSelector.addValueChangeListener(e -> loadLayout(e.getValue()));
        addTableButton.addClickListener(e -> addTable());
        saveLayoutButton.addClickListener(e -> saveLayout());
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.restaurantId = parameter;
        // Load sections for this restaurant
        restaurantRepo.findById(restaurantId).ifPresent(restaurant -> {
            sectionSelector.setItems(restaurant.getSections());
            sectionSelector.setItemLabelGenerator(s -> s.getName().toString());
        });
    }

    private void loadLayout(RestaurantSection section) {
        if (section == null) return;
        this.currentSection = section;
        canvas.removeAll();

        List<RestaurantTable> tables = tableRepo.findByRestaurantSection(section);
        for (RestaurantTable table : tables) {
            canvas.add(new DraggableTable(table));
        }
    }

    private void addTable() {
        if (currentSection == null || capacityField.isEmpty()) {
            Notification.show("Select a section and set capacity first.");
            return;
        }

        // Create a new table at a default position
        RestaurantTable newTable = RestaurantTable.builder()
                .restaurantSection(currentSection)
                .capacity(capacityField.getValue())
                .status(TableStatus.UNKNOWN)
                .position(new Position(10, 10)) // Default position
                .build();

        // For the MVP, we save it immediately to get an ID and then draw it
        RestaurantTable savedTable = tableRepo.save(newTable);
        canvas.add(new DraggableTable(savedTable));
        capacityField.clear();
    }

    private void saveLayout() {
        // In a real app with dragging, you'd update the 'table.position'
        // on all DraggableTable components before saving.
        // For this MVP, saving happens when adding.
        Notification.show("Layout saved (positions are not updated in this MVP without drag-drop).");
    }
}