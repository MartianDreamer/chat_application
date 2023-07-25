package vn.edu.uit.chat_application.service;

import java.io.InputStream;

public interface StorageService {
    void store(String prefix, String name, byte[] content);
    InputStream serve(String prefix, String name);
}
