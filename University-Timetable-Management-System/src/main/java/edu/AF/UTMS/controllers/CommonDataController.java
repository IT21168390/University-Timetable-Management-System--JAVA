package edu.AF.UTMS.controllers;

import edu.AF.UTMS.models.consts.Locations;
import edu.AF.UTMS.services.CommonDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common")
public class CommonDataController {
    @Autowired
    private CommonDataService commonDataService;

    @GetMapping("/faculties")
    public ResponseEntity<List> getFaculties() throws IllegalAccessException {
        return ResponseEntity.ok(commonDataService.getFacultyList());
    }

    @GetMapping("/locations")
    public ResponseEntity<List<Locations>> getLocations() throws IllegalAccessException {
        return ResponseEntity.ok(commonDataService.getAllLocations());
    }
}
