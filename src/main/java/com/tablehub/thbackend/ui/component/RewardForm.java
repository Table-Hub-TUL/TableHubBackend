package com.tablehub.thbackend.ui.component;

import com.tablehub.thbackend.model.Image;
import com.tablehub.thbackend.model.Reward;
import com.tablehub.thbackend.service.implementations.ImageStorageService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class RewardForm extends FormLayout {

    private final ImageStorageService imageStorageService;

    TextField title = new TextField("Title");
    TextArea additionalDescription = new TextArea("Description");
    IntegerField cost = new IntegerField("Cost (Points)");

    // Image fields
    private String currentImageUrl;
    private String currentImageName;

    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);
    Div imagePreview = new Div();

    Binder<Reward> binder = new Binder<>(Reward.class);

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete");

    public RewardForm(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
        addClassName("reward-form");

        configureUpload();

        binder.bindInstanceFields(this);

        add(title,
                additionalDescription,
                cost,
                new Div(new NativeLabel("Reward Image"), upload, imagePreview),
                createButtonsLayout());
    }

    private void configureUpload() {
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFileSize(5 * 1024 * 1024); // 5MB limit

        upload.addSucceededListener(event -> {
            try {
                String url = imageStorageService.store(buffer.getInputStream(), event.getFileName());
                this.currentImageUrl = url;
                this.currentImageName = event.getFileName();
                imagePreview.setText("Uploaded: " + event.getFileName());
                Notification.show("Image uploaded successfully");
            } catch (Exception e) {
                Notification.show("Upload failed: " + e.getMessage());
            }
        });
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
        if (binder.isValid()) {
            Reward reward = binder.getBean();
            // Construct the Image object
            if (currentImageUrl != null) {
                Image img = new Image();
                img.setUrl(currentImageUrl);
                img.setAltText(title.getValue()); // Default alt text to title
                // In a real scenario, you might calculate ratio from the BufferedImage
                img.setRatio(1.0);
                reward.setImage(img);
            }
            fireEvent(new SaveEvent(this, reward));
        }
    }

    public void setReward(Reward reward) {
        binder.setBean(reward);
        if (reward != null && reward.getImage() != null) {
            currentImageUrl = reward.getImage().getUrl();
            imagePreview.setText("Current image: " + reward.getImage().getUrl());
        } else {
            currentImageUrl = null;
            imagePreview.setText("");
        }
    }

    // Events
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