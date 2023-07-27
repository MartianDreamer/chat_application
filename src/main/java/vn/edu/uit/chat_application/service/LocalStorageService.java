package vn.edu.uit.chat_application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class LocalStorageService implements StorageService {

    @Value("${app.storage-location}")
    private String ROOT_LOCATION;

    @Override
    public void store(String prefix, String name, byte[] content) throws IOException {
        File directory = new File(ROOT_LOCATION + prefix);
        if ((directory.exists() && directory.isDirectory()) || directory.mkdirs()) {
            File file = new File(directory, name);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(content);
            }
        }
    }

    @Override
    public File serve(String prefix, String name) throws FileNotFoundException {
        File directory = new File(ROOT_LOCATION + prefix);
        if (directory.exists() && directory.isDirectory()) {
            return new File(directory, name);
        }
        throw new FileNotFoundException();
    }

    @Override
    public List<File> serve(String prefix) {
        File directory = new File(ROOT_LOCATION + prefix);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                return Arrays.asList(files);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void delete(String prefix, String name) {
        File directory = new File(ROOT_LOCATION + prefix);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(directory, name);
            if (file.exists() && !file.delete()) {
                throw new CustomRuntimeException("can not delete file", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    public void delete(String prefix) {
        File directory = new File(ROOT_LOCATION + prefix);
        if (!directory.delete()) {
            throw new CustomRuntimeException("can not delete file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public File serveFirstWithoutExtension(String prefix, String name) throws FileNotFoundException {
        File directory = new File(ROOT_LOCATION + prefix);
        if (directory.exists() && directory.isDirectory()) {
            File[] children = directory.listFiles();
            children = children == null ? new File[]{} : children;
            for (var file : children) {
                if (file.getName().contains(name)) {
                    return file;
                }
            }
        }
        throw new FileNotFoundException();
    }
}
