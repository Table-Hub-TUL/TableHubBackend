package com.tablehub.thbackend.ui.view;

import com.tablehub.thbackend.model.*;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RestaurantSectionRepository;
import com.tablehub.thbackend.repo.RestaurantTableRepository;
import com.tablehub.thbackend.repo.SectionLayoutRepository;
import com.tablehub.thbackend.ui.layout.MainLayout;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.Optional;

@Route(value = "admin/layout", layout = MainLayout.class)
@PageTitle("Layout Editor | TableHub CMS")
@RolesAllowed({"ROLE_ADMIN", "ROLE_OWNER"})
@JsModule("./layout-editor.js")
@JsModule("./wall-drawer.js")
public class LayoutEditorView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RestaurantRepository restaurantRepo;
    private final RestaurantTableRepository tableRepo;
    private final RestaurantSectionRepository sectionRepo;
    private final SectionLayoutRepository layoutRepo;

    private Long restaurantId;
    private Restaurant currentRestaurant;
    private RestaurantSection currentSection;
    private boolean isDrawingWalls = false;

    private Div canvas = new Div();
    private ComboBox<RestaurantSection> sectionSelector = new ComboBox<>("Select Section");
    private Button addSectionButton = new Button("Add Section");
    private IntegerField capacityField = new IntegerField("Table Capacity");
    private Button addTableButton = new Button("Add Table");
    private TextField poiNameField = new TextField("POI Name");
    private Button addPoiButton = new Button("Add POI");
    private Button toggleWallDrawButton = new Button("Draw Walls");
    private Button clearWallsButton = new Button("Clear Walls");
    private TextField shapePathDisplay = new TextField("SVG Path");
    private Button saveLayoutButton = new Button("Save Shape");

    private class DraggableTable extends Div {
        public RestaurantTable table;
        private Span coordSpan;

        public DraggableTable(RestaurantTable table) {
            this.table = table;

            Span capacitySpan = new Span(String.valueOf(table.getCapacity()));
            coordSpan = new Span();
            updateCoordsText();

            capacitySpan.getStyle().set("font-weight", "bold").set("font-size", "1.2em");
            coordSpan.getStyle().set("font-size", "0.7em").set("display", "block");

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            deleteButton.getStyle()
                    .set("position", "absolute")
                    .set("top", "-5px")
                    .set("right", "-5px")
                    .set("width", "24px")
                    .set("height", "24px")
                    .set("padding", "0")
                    .set("min-width", "0")
                    .set("z-index", "15");
            deleteButton.addClickListener(e -> confirmAndDeleteTable());

            add(capacitySpan, coordSpan, deleteButton);

            setClassName("draggable-item");
            getElement().setAttribute("data-item-id", table.getId().toString());
            getElement().setAttribute("data-item-type", "table");

            getStyle().set("width", "60px").set("height", "60px")
                    .set("background", "lightblue").set("border", "1px solid black")
                    .set("position", "absolute")
                    .set("left", table.getPosition().getX() + "px")
                    .set("top", table.getPosition().getY() + "px")
                    .set("cursor", "move").set("z-index", "10")
                    .set("display", "flex").set("flex-direction", "column")
                    .set("align-items", "center").set("justify-content", "center")
                    .set("border-radius", "4px");
        }

        public void updateCoordsText() {
            String coords = String.format("X:%.0f, Y:%.0f", table.getPosition().getX(), table.getPosition().getY());
            coordSpan.setText(coords);
        }

        private void confirmAndDeleteTable() {
            showDeleteConfirmation("table", this::deleteTable);
        }

        private void deleteTable() {
            try {
                tableRepo.delete(table);
                canvas.remove(this);

                if (currentSection != null) {
                    currentSection = sectionRepo.findByIdWithTables(currentSection.getId()).orElse(null);
                }

                Notification.show("Table deleted.");

            } catch (Exception e) {
                Notification.show("Error deleting table: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            }
        }
    }

    private class DraggablePoi extends Div {
        public PointOfInterest poi;

        public DraggablePoi(PointOfInterest poi) {
            this.poi = poi;
            setText(poi.getDescription());

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            deleteButton.getStyle()
                    .set("position", "absolute")
                    .set("top", "-5px")
                    .set("right", "-5px")
                    .set("width", "24px")
                    .set("height", "24px")
                    .set("padding", "0")
                    .set("min-width", "0")
                    .set("z-index", "15");
            deleteButton.addClickListener(e -> confirmAndDeletePoi());

            add(deleteButton);

            addClassName("draggable-item");
            getElement().setAttribute("data-item-id", String.valueOf(poi.getId()));
            getElement().setAttribute("data-item-type", "poi");

            double x = poi.getTopLeft().getX();
            double y = poi.getTopLeft().getY();
            double width = poi.getBottomRight().getX() - x;
            double height = poi.getBottomRight().getY() - y;

            getStyle().set("width", width + "px").set("height", height + "px")
                    .set("background", "lightgreen").set("border", "1px dashed green")
                    .set("position", "absolute")
                    .set("left", x + "px")
                    .set("top", y + "px")
                    .set("cursor", "move")
                    .set("z-index", "10");
        }

        private void confirmAndDeletePoi() {
            showDeleteConfirmation("POI", this::deletePoi);
        }

        private void deletePoi() {
            if (currentSection != null) {
                try {
                    currentSection.getPois().remove(poi);
                    sectionRepo.save(currentSection);
                    canvas.remove(this);
                    Notification.show("POI deleted.");
                } catch (Exception e) {
                    Notification.show("Error deleting POI: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
                }
            }
        }
    }

    public LayoutEditorView(RestaurantRepository restaurantRepo, RestaurantTableRepository tableRepo,
                            RestaurantSectionRepository sectionRepo, SectionLayoutRepository layoutRepo) {
        this.restaurantRepo = restaurantRepo;
        this.tableRepo = tableRepo;
        this.sectionRepo = sectionRepo;
        this.layoutRepo = layoutRepo;

        canvas.setId("canvas");
        canvas.getStyle().set("width", "800px").set("height", "600px")
                .set("border", "1px solid black").set("position", "relative")
                .set("background", "#f4f4f4");

        shapePathDisplay.setWidth("400px");
        shapePathDisplay.setReadOnly(true);

        VerticalLayout sectionControls = new VerticalLayout(sectionSelector, addSectionButton);
        sectionControls.setAlignItems(Alignment.END);
        HorizontalLayout controls = getHorizontalLayout(sectionControls);
        add(controls, canvas);

        sectionSelector.addValueChangeListener(e -> loadLayout(e.getValue()));
        addSectionButton.addClickListener(e -> openAddSectionDialog());
        addTableButton.addClickListener(e -> addTable());
        addPoiButton.addClickListener(e -> addPoi());
        toggleWallDrawButton.addClickListener(e -> toggleWallDrawing());
        saveLayoutButton.addClickListener(e -> saveLayoutShape());
        clearWallsButton.addClickListener(e -> clearWalls());
    }

    private HorizontalLayout getHorizontalLayout(VerticalLayout sectionControls) {
        VerticalLayout tableControls = new VerticalLayout(capacityField, addTableButton);
        VerticalLayout poiControls = new VerticalLayout(poiNameField, addPoiButton);
        VerticalLayout wallControls = new VerticalLayout(toggleWallDrawButton, clearWallsButton, shapePathDisplay, saveLayoutButton);
        wallControls.setSpacing(false);

        HorizontalLayout controls = new HorizontalLayout(sectionControls, tableControls, poiControls, wallControls);
        controls.setAlignItems(Alignment.START);
        return controls;
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.restaurantId = parameter;
        restaurantRepo.findByIdWithSections(restaurantId).ifPresent(restaurant -> {
            this.currentRestaurant = restaurant;
            refreshSectionSelector();
        });
    }

    private void clearWalls() {
        if (isDrawingWalls) {
            toggleWallDrawing();
        }
        shapePathDisplay.setValue("");

        UI.getCurrent().getPage().executeJs(
                "var renderedWall = document.getElementById('rendered-wall-svg');" +
                        "if (renderedWall) { renderedWall.remove(); }" +
                        "var drawingSvg = document.getElementById('drawing-svg');" +
                        "if (drawingSvg) { drawingSvg.remove(); }"
        );
    }

    private void refreshSectionSelector() {
        sectionSelector.setItems(currentRestaurant.getSections());
        sectionSelector.setItemLabelGenerator(s -> s.getName().toString());
    }

    private void openAddSectionDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add New Section");

        ComboBox<SectionName> newSectionName = new ComboBox<>("Section Name");
        newSectionName.setItems(SectionName.values());

        Button saveButton = new Button("Save", e -> {
            if (newSectionName.getValue() != null) {
                addSection(newSectionName.getValue());
                dialog.close();
            } else {
                Notification.show("Please select a section name.");
            }
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(newSectionName);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void addSection(SectionName sectionName) {
        if (currentRestaurant == null) return;

        RestaurantSection newSection = RestaurantSection.builder()
                .name(sectionName)
                .restaurant(currentRestaurant)
                .pois(new ArrayList<>())
                .tables(new ArrayList<>())
                .build();

        sectionRepo.save(newSection);

        restaurantRepo.findByIdWithSections(restaurantId).ifPresent(r -> {
            this.currentRestaurant = r;
            refreshSectionSelector();
            sectionSelector.setValue(newSection);
        });
    }

    private void loadLayout(RestaurantSection section) {
        if (isDrawingWalls) {
            toggleWallDrawing();
        }

        canvas.removeAll();
        shapePathDisplay.clear();

        if (section == null) {
            currentSection = null;
            return;
        }

        sectionRepo.findByIdWithTables(section.getId()).ifPresent(fullSection -> {
            this.currentSection = fullSection;

            if (fullSection.getTables() != null) {
                for (RestaurantTable table : fullSection.getTables()) {
                    canvas.add(new DraggableTable(table));
                }
            }

            if (fullSection.getPois() != null) {
                for (PointOfInterest poi : fullSection.getPois()) {
                    canvas.add(new DraggablePoi(poi));
                }
            }

            renderSectionShape(fullSection.getLayout());
        });

        UI.getCurrent().getPage().executeJs("window.initDraggables($0)", getElement());
    }

    private void renderSectionShape(SectionLayout layout) {
        if (layout != null) {
            String svgPath = layout.getShape();
            if (svgPath == null) {
                svgPath = "";
            }

            shapePathDisplay.setValue(svgPath);

            Div svg = new Div();
            svg.setId("rendered-wall-svg");

            String svgString = String.format(
                    "<svg width='100%%' height='100%%' style='position:absolute; top:0; left:0; z-index: 0;'>" +
                            "  <path d='%s' fill='none' stroke='#888' stroke-width='3' />" +
                            "</svg>",
                    svgPath
            );

            svg.getElement().setProperty("innerHTML", svgString);

            canvas.add(svg);
        } else {
            shapePathDisplay.clear();
        }
    }

    /**
     * Saves the SVG path string (from the wall drawing) to the database.
     */
    private void saveLayoutShape() {
        if (currentSection == null) {
            Notification.show("Select a section first.");
            return;
        }

        SectionLayout layout = currentSection.getLayout();
        if (layout == null) {
            layout = new SectionLayout();
            layout.setViewportWidth(800);
            layout.setViewportHeight(600);
        }

        layout.setShape(shapePathDisplay.getValue());

        SectionLayout savedLayout = layoutRepo.save(layout);

        currentSection.setLayout(savedLayout);
        sectionRepo.save(currentSection);

        Notification.show("Layout shape saved!");

        loadLayout(currentSection);
    }

    private void addTable() {
        if (currentSection == null || capacityField.isEmpty()) {
            Notification.show("Select a section and set capacity first.");
            return;
        }

        RestaurantTable newTable = RestaurantTable.builder()
                .restaurantSection(currentSection)
                .capacity(capacityField.getValue())
                .status(TableStatus.UNKNOWN)
                .position(new Position(10, 10))
                .build();

        RestaurantTable savedTable = tableRepo.save(newTable);
        DraggableTable tableComponent = new DraggableTable(savedTable);
        canvas.add(tableComponent);
        UI.getCurrent().getPage().executeJs("window.initDraggables($0)", getElement());
        capacityField.clear();
    }

    private void toggleWallDrawing() {
        isDrawingWalls = !isDrawingWalls;
        if (isDrawingWalls) {
            toggleWallDrawButton.setText("Stop Drawing");
            Notification.show("Click on the canvas to draw wall points.");
        } else {
            toggleWallDrawButton.setText("Draw Walls");
        }
        UI.getCurrent().getPage().executeJs("window.toggleWallDrawing($0, $1)", getElement(), isDrawingWalls);
    }

    private void addPoi() {
        if (currentSection == null || poiNameField.isEmpty()) {
            Notification.show("Select a section and set POI name first.");
            return;
        }

        PointOfInterest newPoi = new PointOfInterest();
        newPoi.setId(System.currentTimeMillis());
        newPoi.setDescription(poiNameField.getValue());
        newPoi.setTopLeft(new Position(20, 20));
        newPoi.setBottomRight(new Position(120, 70));

        currentSection.getPois().add(newPoi);
        sectionRepo.save(currentSection);

        DraggablePoi poiComponent = new DraggablePoi(newPoi);
        canvas.add(poiComponent);
        UI.getCurrent().getPage().executeJs("window.initDraggables($0)", getElement());

        poiNameField.clear();
    }

    private void showDeleteConfirmation(String itemType, Runnable deleteAction) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirm Delete");
        dialog.add(new Paragraph("Are you sure you want to delete this " + itemType + "?"));

        Button deleteButton = new Button("Delete", e -> {
            deleteAction.run();
            dialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.getFooter().add(cancelButton, deleteButton);
        dialog.open();
    }

    @ClientCallable
    public void updateItemPosition(String itemType, String itemIdStr, double x, double y) {
        if ("table".equals(itemType)) {
            long tableId = Long.parseLong(itemIdStr);
            tableRepo.findById(tableId).ifPresent(table -> {
                table.setPosition(new Position(x, y));
                tableRepo.save(table);
            });

        } else if ("poi".equals(itemType)) {
            if (currentSection == null) return;

            double poiId = Double.parseDouble(itemIdStr);

            Optional<PointOfInterest> poiOpt = currentSection.getPois().stream()
                    .filter(p -> p.getId() == poiId)
                    .findFirst();

            if (poiOpt.isPresent()) {
                PointOfInterest poi = poiOpt.get();

                double width = poi.getBottomRight().getX() - poi.getTopLeft().getX();
                double height = poi.getBottomRight().getY() - poi.getTopLeft().getY();

                poi.setTopLeft(new Position(x, y));
                poi.setBottomRight(new Position(x + width, y + height));

                sectionRepo.save(currentSection);
            }
        }
    }

    @ClientCallable
    public void updateShapePath(String pathString) {
        shapePathDisplay.setValue(pathString);
    }
}
