package com.tablehub.thbackend.ui.component;

import com.tablehub.thbackend.model.Image;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.service.implementations.ImageStorageService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RewardForm extends Div {

    private final ImageStorageService imageStorageService;

    TextField title = new TextField("Title");
    TextArea additionalDescription = new TextArea("Description");
    IntegerField cost = new IntegerField("Cost (Points)");

    private String currentImageUrl;
    private final com.vaadin.flow.component.html.Image imagePreview = new com.vaadin.flow.component.html.Image();
    private final Div imageContainer = new Div();

    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);

    Binder<Reward> binder = new Binder<>(Reward.class);

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete");

    public RewardForm(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
        addClassName("reward-form");

        getStyle().set("padding", "20px");
        getStyle().set("background-color", "var(--lumo-base-color)");
        getStyle().set("height", "100%");
        getStyle().set("box-sizing", "border-box");
        getStyle().set("display", "flex");
        getStyle().set("flex-direction", "column");
        getStyle().set("gap", "var(--lumo-space-m)");

        configureFields();
        configureImageArea();
        bindFields();

        add(title, cost, additionalDescription);
        add(new NativeLabel("Reward Image"));
        add(imageContainer, upload);
        add(createButtonsLayout());
    }

    private void configureFields() {
        title.setWidthFull();
        title.setPlaceholder("e.g., Free Coffee");

        cost.setWidthFull();
        cost.setMin(0);
        cost.setStepButtonsVisible(true);

        additionalDescription.setWidthFull();
        additionalDescription.setMinHeight("100px");
    }

    private void configureImageArea() {
        imageContainer.setWidth("100%");
        imageContainer.setHeight("200px");
        imageContainer.getStyle().set("background-color", "var(--lumo-contrast-5pct)");
        imageContainer.getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        imageContainer.getStyle().set("display", "flex");
        imageContainer.getStyle().set("align-items", "center");
        imageContainer.getStyle().set("justify-content", "center");
        imageContainer.getStyle().set("overflow", "hidden");
        imageContainer.getStyle().set("border", "1px dashed var(--lumo-contrast-20pct)");
        imageContainer.getStyle().set("position", "relative");

        imagePreview.setWidth("100%");
        imagePreview.setHeight("100%");
        imagePreview.getStyle().set("object-fit", "cover");
        imagePreview.setVisible(false);

        Span placeholder = new Span("No Image Selected");
        placeholder.getStyle().set("color", "var(--lumo-secondary-text-color)");
        placeholder.getStyle().set("pointer-events", "none");

        imageContainer.add(imagePreview, placeholder);

        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/webp", "image/gif");
        upload.setMaxFiles(1);
        upload.setDropLabel(new Span("Drop file here"));

        upload.addSucceededListener(event -> {
            try {
                String url = imageStorageService.store(buffer.getInputStream(), event.getFileName());
                this.currentImageUrl = url;

                updatePreview(url);

                Notification.show("Upload successful").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Upload failed: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    private void updatePreview(String url) {
        if (url != null && !url.isBlank()) {
            // Logic to convert "/media/filename.jpg" -> File Stream
            // This bypasses HTTP static resource issues
            StreamResource resource = new StreamResource("preview-" + System.currentTimeMillis(), () -> {
                try {
                    // Extract filename from URL (assuming /media/filename format)
                    String filename = url.substring(url.lastIndexOf("/") + 1);
                    Path path = Paths.get("/media", filename);
                    return new FileInputStream(path.toFile());
                } catch (FileNotFoundException e) {
                    return null;
                }
            });

            imagePreview.setSrc(resource);
            imagePreview.setVisible(true);

            imageContainer.getChildren()
                    .filter(c -> c instanceof Span)
                    .findFirst()
                    .ifPresent(c -> c.setVisible(false));

            imageContainer.getStyle().set("border", "none");
        } else {
            imagePreview.setVisible(false);
            imageContainer.getChildren()
                    .filter(c -> c instanceof Span)
                    .findFirst()
                    .ifPresent(c -> c.setVisible(true));

            imageContainer.getStyle().set("border", "1px dashed var(--lumo-contrast-20pct)");
        }
    }

    private void bindFields() {
        binder.forField(title)
                .asRequired("Title is required")
                .bind(Reward::getTitle, Reward::setTitle);

        binder.forField(cost)
                .asRequired("Cost is required")
                .withValidator(c -> c != null && c >= 0, "Cost must be positive")
                .bind(Reward::getCost, Reward::setCost);

        binder.bind(additionalDescription, Reward::getAdditionalDescription, Reward::setAdditionalDescription);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, cancel);
    }

    private void validateAndSave() {
        if (binder.validate().isOk()) {
            Reward reward = binder.getBean();
            // Map the image URL to the entity
            if (currentImageUrl != null) {
                if (reward.getImage() == null) reward.setImage(new Image());
                reward.getImage().setUrl(currentImageUrl);
                reward.getImage().setRatio(1.0);
            }
            fireEvent(new SaveEvent(this, reward));
        }
    }

    public void setReward(Reward reward) {
        binder.setBean(reward);
        upload.clearFileList();

        if (reward != null && reward.getImage() != null) {
            this.currentImageUrl = reward.getImage().getUrl();
            updatePreview(this.currentImageUrl);
        } else {
            this.currentImageUrl = null;
            updatePreview(null);
        }
    }

    public static abstract class RewardFormEvent extends ComponentEvent<RewardForm> {
        private Reward reward;
        protected RewardFormEvent(RewardForm source, Reward reward) {
            super(source, false);
            this.reward = reward;
        }
        public Reward getReward() { return reward; }
    }

    public static class SaveEvent extends RewardFormEvent {
        SaveEvent(RewardForm source, Reward reward) { super(source, reward); }
    }
    public static class DeleteEvent extends RewardFormEvent {
        DeleteEvent(RewardForm source, Reward reward) { super(source, reward); }
    }
    public static class CloseEvent extends RewardFormEvent {
        CloseEvent(RewardForm source) { super(source, null); }
    }

    public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
        return addListener(SaveEvent.class, listener);
    }
    public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        return addListener(DeleteEvent.class, listener);
    }
    public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
        return addListener(CloseEvent.class, listener);
    }
}