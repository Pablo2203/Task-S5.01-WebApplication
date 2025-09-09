package cat.itacademy.s05.t02.n01.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    @Value("${app.uploads.max-size-bytes:5242880}") // 5MB default
    private long maxSizeBytes;

    private static final Set<String> ALLOWED_EXTS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private static final Set<MediaType> ALLOWED_MEDIA = Set.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.valueOf("image/webp")
    );

    public Mono<String> savePublic(FilePart file) {
        try {
            var contentType = file.headers().getContentType();
            if (contentType == null || ALLOWED_MEDIA.stream().noneMatch(mt -> mt.includes(contentType))) {
                return Mono.error(new IllegalArgumentException("Tipo de archivo no permitido"));
            }

            String original = file.filename();
            String ext = extractSafeExtension(original);
            if (ext.isEmpty() || !ALLOWED_EXTS.contains(ext.toLowerCase())) {
                return Mono.error(new IllegalArgumentException("Extensión no permitida"));
            }

            long contentLength = file.headers().getContentLength();
            if (contentLength > 0 && contentLength > maxSizeBytes) {
                return Mono.error(new IllegalArgumentException("Archivo supera el tamaño máximo permitido"));
            }

            Path dir = Paths.get(uploadsDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String name = UUID.randomUUID() + ext;
            Path target = dir.resolve(name).normalize();
            if (!target.startsWith(dir)) {
                return Mono.error(new IllegalStateException("Ruta de destino inválida"));
            }

            return file.transferTo(target).thenReturn("/uploads/" + name);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private static String extractSafeExtension(String filename) {
        if (filename == null) return "";
        String base = filename.replace("\\", "/");
        int slash = base.lastIndexOf('/');
        if (slash >= 0) base = base.substring(slash + 1);
        int dot = base.lastIndexOf('.');
        if (dot <= 0) return "";
        return base.substring(dot).toLowerCase();
    }
}