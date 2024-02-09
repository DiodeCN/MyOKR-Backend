package cn.diodecn.MyOKR.demo.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private final Path configFilePath;
    private final Properties configProperties = new Properties();

    public ConfigManager(String configFileName) {
        this.configFilePath = Paths.get(configFileName).toAbsolutePath().normalize();
        initialize();
    }

    private void initialize() {
        try {
            // Check if the config file exists
            if (Files.notExists(configFilePath)) {
                // Create config file and set default FileSavingDirectory
                Files.createFile(configFilePath);
                String fileSavingDirectory = configFilePath.getParent().resolve("Storage").toString();
                configProperties.setProperty("FileSavingDirectory", fileSavingDirectory);
                saveProperties();
            } else {
                // Load existing properties
                loadProperties();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error initializing configuration", e);
        }
    }

    private void loadProperties() throws IOException {
        try (InputStream inputStream = Files.newInputStream(configFilePath)) {
            configProperties.load(inputStream);
        }
    }

    private void saveProperties() throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(configFilePath)) {
            configProperties.store(outputStream, null);
        }
    }

    public String getFileSavingDirectory() {
        return configProperties.getProperty("FileSavingDirectory");
    }
}
