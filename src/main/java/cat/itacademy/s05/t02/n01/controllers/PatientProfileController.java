package cat.itacademy.s05.t02.n01.controllers;

import cat.itacademy.s05.t02.n01.model.PatientProfile;
import cat.itacademy.s05.t02.n01.services.PatientProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    public PatientProfileController(PatientProfileService patientProfileService) {
        this.patientProfileService = patientProfileService;
    }

    @PostMapping
    public PatientProfile create(@RequestBody PatientProfile profile) {
        return patientProfileService.save(profile);
    }

    @GetMapping("/{id}")
    public PatientProfile getById(@PathVariable Long id) {
        return patientProfileService.findById(id);
    }
}
