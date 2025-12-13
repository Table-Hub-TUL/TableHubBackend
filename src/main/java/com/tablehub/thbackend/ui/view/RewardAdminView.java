package com.tablehub.thbackend.ui.view;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RewardRepository;
import com.tablehub.thbackend.service.implementations.ImageStorageService;
import com.tablehub.thbackend.ui.component.RewardForm;
import com.tablehub.thbackend.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/rewards", layout = MainLayout.class)
@PageTitle("Manage Rewards | TableHub CMS")
@RolesAllowed({"ROLE_ADMIN", "ROLE_OWNER"})
public class RewardAdminView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RewardRepository rewardRepo;
    private final RestaurantRepository restaurantRepo;
    private final RewardForm form;

    private final Grid<Reward> grid = new Grid<>(Reward.class);
    private final SplitLayout splitLayout = new SplitLayout();
    private Restaurant restaurant;

    public RewardAdminView(RewardRepository rewardRepo,
                           RestaurantRepository restaurantRepo,
                           ImageStorageService imageStorageService) {
        this.rewardRepo = rewardRepo;
        this.restaurantRepo = restaurantRepo;
        this.form = new RewardForm(imageStorageService);

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        configureGrid();
        configureForm();
        configureSplitLayout();

        add(getToolbar(), splitLayout);
    }

    private void configureSplitLayout() {
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(getGridContainer());
        splitLayout.addToSecondary(form);
        splitLayout.setSplitterPosition(70);
    }

    private Div getGridContainer() {
        Div div = new Div(grid);
        div.setSizeFull();
        div.getStyle().set("padding", "0 1em");
        return div;
    }

    @Override
    public void setParameter(BeforeEvent event, Long restaurantId) {
        restaurantRepo.findById(restaurantId).ifPresentOrElse(r -> {
            this.restaurant = r;
            updateList();
        }, () -> event.rerouteTo(""));
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        grid.removeAllColumns();

        grid.addComponentColumn(reward -> {
            com.vaadin.flow.component.html.Image img = new com.vaadin.flow.component.html.Image();
            img.getStyle().set("width", "40px").set("height", "40px").set("object-fit", "cover").set("border-radius", "6px");

            if (reward.getImage() != null && reward.getImage().getUrl() != null) {
                StreamResource resource = getStreamResource(reward);
                img.setSrc(resource);
            } else {
                img.setSrc("");
                img.setVisible(false);
            }
            return img;
        }).setHeader("Image").setWidth("80px").setFlexGrow(0);

        grid.addColumn(Reward::getTitle).setHeader("Title").setAutoWidth(true);
        grid.addColumn(Reward::getCost).setHeader("Cost (pts)").setWidth("120px").setFlexGrow(0);

        grid.asSingleSelect().addValueChangeListener(event -> editReward(event.getValue()));
    }

    private static StreamResource getStreamResource(Reward reward) {
        String url = reward.getImage().getUrl();
        StreamResource resource = new StreamResource("grid-" + reward.getId(), () -> {
            try {
                String filename = url.substring(url.lastIndexOf("/") + 1);
                return new java.io.FileInputStream(java.nio.file.Paths.get("/media", filename).toFile());
            } catch (java.io.FileNotFoundException e) {
                return null;
            }
        });
        return resource;
    }

    private void configureForm() {
        form.setVisible(false);
        form.addSaveListener(this::saveReward);
        form.addDeleteListener(this::deleteReward);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addRewardButton = new Button("Add Reward", VaadinIcon.PLUS.create());
        addRewardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addRewardButton.addClickListener(click -> addReward());

        HorizontalLayout toolbar = new HorizontalLayout(addRewardButton);
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        return toolbar;
    }

    private void saveReward(RewardForm.SaveEvent event) {
        Reward reward = event.getReward();
        reward.setRestaurant(restaurant);
        rewardRepo.save(reward);
        updateList();
        closeEditor();
    }

    private void deleteReward(RewardForm.DeleteEvent event) {
        rewardRepo.delete(event.getReward());
        updateList();
        closeEditor();
    }

    private void addReward() {
        grid.asSingleSelect().clear();
        editReward(new Reward());
    }

    private void editReward(Reward reward) {
        if (reward == null) {
            closeEditor();
        } else {
            form.setReward(reward);
            form.setVisible(true);
            splitLayout.setSplitterPosition(70);
        }
    }

    private void closeEditor() {
        form.setReward(null);
        form.setVisible(false);
        splitLayout.setSplitterPosition(100);
    }

    private void updateList() {
        if (restaurant != null) {
            grid.setItems(rewardRepo.findAllByRestaurantId(restaurant.getId()));
        }
    }
}