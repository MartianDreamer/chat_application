package vn.edu.uit.chat_application.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface StorageService {
    void store(String prefix, String name, byte[] content) throws IOException;
    File serve(String prefix, String name) throws FileNotFoundException;
    List<File> serve(String prefix);
    void delete(String prefix, String name);
    void delete(String prefix);
    default File serveFirstWithoutExtension(String prefix, String name) throws FileNotFoundException {
        return serve(prefix, name);
    }
}
