package cat.itacademy.s05.t02.n01.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadService {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    public Mono<String> savePublic(FilePart file) {
        try {
            Path dir = Paths.get(uploadsDir).toAbsolutePath();
            Files.createDirectories(dir);
            String original = file.filename();
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot > 0) ext = original.substring(dot);
            String name = UUID.randomUUID() + ext;
            Path target = dir.resolve(name);
            return file.transferTo(target).thenReturn("/uploads/" + name);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}

