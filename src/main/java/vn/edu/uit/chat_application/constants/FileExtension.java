package vn.edu.uit.chat_application.constants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
@Getter
public abstract class FileExtension {

    public static final FileExtension NO_FILE = new NoFile();

    @Column(nullable = false, length = 10)
    protected String extension;

    @Embeddable
    @NoArgsConstructor
    public static final class ImageFileExtension extends FileExtension {
        public static final ImageFileExtension JPEG = new ImageFileExtension("JPEG");
        public static final ImageFileExtension JPG = new ImageFileExtension("JPG");
        public static final ImageFileExtension PNG = new ImageFileExtension("PNG");

        private ImageFileExtension(String imageExtension) {
            super(imageExtension);
        }
    }

    private static final class NoFile extends FileExtension {
        private NoFile() {
            super("NULL");
        }
    }
}
