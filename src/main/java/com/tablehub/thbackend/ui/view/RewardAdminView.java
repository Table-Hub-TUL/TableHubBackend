package com.tablehub.thbackend.ui.view;

import com.tablehub.thbackend.model.Restaurant;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.repo.RestaurantRepository;
import com.tablehub.thbackend.repo.RewardRepository;
import com.tablehub.thbackend.service.implementations.ImageStorageService;
import com.tablehub.thbackend.ui.component.RewardForm;
import com.tablehub.thbackend.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/rewards", layout = MainLayout.class)
@PageTitle("Manage Rewards | TableHub CMS")
@RolesAllowed({"ROLE_ADMIN", "ROLE_OWNER"})
public class RewardAdminView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RewardRepository rewardRepo;
    private final RestaurantRepository restaurantRepo;
    private final RewardForm form;

    private Grid<Reward> grid = new Grid<>(Reward.class);
    private Restaurant restaurant;

    public RewardAdminView(RewardRepository rewardRepo,
                           RestaurantRepository restaurantRepo,
                           ImageStorageService imageStorageService) {
        this.rewardRepo = rewardRepo;
        this.restaurantRepo = restaurantRepo;
        this.form = new RewardForm(imageStorageService);

        setSizeFull();
        configureGrid();
        configureForm();

        add(new H2("Manage Rewards"), getToolbar(), new HorizontalLayout(grid, form));
    }

    @Override
    public void setParameter(BeforeEvent event, Long restaurantId) {
        restaurantRepo.findById(restaurantId).ifPresentOrElse(r -> {
            this.restaurant = r;
            updateList();
        }, () -> {
            // Handle case where restaurant is not found
            event.rerouteTo("");
        });
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn(Reward::getTitle).setHeader("Title");
        grid.addColumn(Reward::getCost).setHeader("Cost");
        grid.addComponentColumn(reward -> {
            if (reward.getImage() != null && reward.getImage().getUrl() != null) {
                Image img = new Image(reward.getImage().getUrl(), "Reward Image");
                img.setHeight("50px");
                return img;
            }
            return new com.vaadin.flow.component.html.Span("No Image");
        }).setHeader("Image");

        grid.asSingleSelect().addValueChangeListener(event -> editReward(event.getValue()));
    }

    private void configureForm() {
        form.setWidth("25em");
        form.addSaveListener(this::saveReward);
        form.addDeleteListener(this::deleteReward);
        form.addCloseListener(e -> closeEditor());
        closeEditor();
    }

    private HorizontalLayout getToolbar() {
        Button addRewardButton = new Button("Add Reward");
        addRewardButton.addClickListener(click -> addReward());
        return new HorizontalLayout(addRewardButton);
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
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setReward(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        if (restaurant != null) {
            grid.setItems(rewardRepo.findAllByRestaurantId(restaurant.getId()));
        }
    }
}