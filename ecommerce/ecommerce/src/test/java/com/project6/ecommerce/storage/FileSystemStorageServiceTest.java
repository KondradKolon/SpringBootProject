package com.project6.ecommerce.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileSystemStorageServiceTest {

    private FileSystemStorageService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setLocation(tempDir.toString());
        service = new FileSystemStorageService(properties);
        service.init();
    }

    @Test
    void load_ShouldReturnPath() {
        Path path = service.load("test.txt");
        assertNotNull(path);
        assertEquals(tempDir.resolve("test.txt"), path);
    }

    @Test
    void loadAsResource_ShouldReturnResource_WhenFileExists() throws IOException {
        // Arrange
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file, "Hello World");

        // Act
        Resource resource = service.loadAsResource("test.txt");

        // Assert
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void loadAsResource_ShouldThrowException_WhenFileDoesNotExist() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            service.loadAsResource("nonexistent.txt");
        });
        assertTrue(exception.getMessage().contains("Could not read file"));
    }

    @Test
    void store_ShouldSaveFile_WhenValid() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("image.png");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));

        // Act
        String savedFilename = service.store(mockFile);

        // Assert
        assertNotNull(savedFilename);
        assertTrue(savedFilename.endsWith(".png"));
        assertTrue(Files.exists(tempDir.resolve(savedFilename)));
    }

    @Test
    void store_ShouldThrowException_WhenFileIsEmpty() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.store(mockFile));
    }

    @Test
    void store_ShouldThrowException_WhenPathTraversalAttempted() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("../../../etc/passwd");
        // Only needed if execution proceeds past emptiness check
        try {
            when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("hacker".getBytes()));
        } catch (IOException e) {
            fail("Mock setup failed");
        }

        Exception exception = assertThrows(RuntimeException.class, () -> service.store(mockFile));
        assertTrue(exception.getMessage().contains("Cannot store file outside current directory") || 
                   exception.getMessage().contains("Failed to store file"));
    }
}
