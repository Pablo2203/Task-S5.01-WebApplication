package cat.itacademy.s05.t02.n01.dto;

public record ProfessionalProfileResponse(
        Long userId,
        String firstName,
        String lastName,
        String studies,
        String specialty,
        String bio,
        String photoUrl
) {}

