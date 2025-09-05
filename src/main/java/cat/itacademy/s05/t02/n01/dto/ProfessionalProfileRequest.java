package cat.itacademy.s05.t02.n01.dto;

import jakarta.validation.constraints.Size;

public record ProfessionalProfileRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 255) String studies,
        @Size(max = 50) String specialty,
        String bio,
        @Size(max = 255) String photoUrl
) {}

