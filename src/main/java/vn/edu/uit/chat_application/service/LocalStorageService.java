package vn.edu.uit.chat_application.service;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.uit.chat_application.exception.CustomRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class LocalStorageService implements StorageService {
    private static final String ROOT_LOCATION = "/chat_application";

    @Override
    public void store(String prefix, String name, byte[] content) {
        File directory = new File(ROOT_LOCATION + prefix);
        if ((directory.exists() && directory.isDirectory()) || directory.mkdirs()) {
            File file = new File(directory, name);
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(content);
                return;
            } catch (IOException e) {
                throw new CustomRuntimeException("can not save file", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        throw CustomRuntimeException.notFound();
    }

    @SneakyThrows
    @Override
    public InputStream serve(String prefix, String name) {
        File directory = new File(ROOT_LOCATION + prefix);
        if (directory.exists() && directory.isDirectory()) {
            File file = new File(directory, name);
            return new FileInputStream(file);
        }
        throw CustomRuntimeException.notFound();
    }
}
