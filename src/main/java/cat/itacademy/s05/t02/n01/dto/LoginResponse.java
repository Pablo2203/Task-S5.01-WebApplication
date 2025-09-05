package cat.itacademy.s05.t02.n01.dto;

import java.time.Instant;
import java.util.List;

public record LoginResponse(
        String token,
        List<String> roles,
        Instant expiresAt
) {}

