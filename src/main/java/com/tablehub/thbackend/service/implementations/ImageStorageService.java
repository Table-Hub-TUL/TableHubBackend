package com.tablehub.thbackend.service.implementations;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path rootLocation = Paths.get("/media");

    public String store(InputStream inputStream, String originalFilename) throws IOException {
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path destinationFile = this.rootLocation.resolve(filename);
        Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        return "/media/" + filename;
    }
}