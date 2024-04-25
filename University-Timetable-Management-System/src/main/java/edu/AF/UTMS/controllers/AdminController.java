package edu.AF.UTMS.controllers;

import edu.AF.UTMS.dto.CourseDTO;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Welcome Admin. So far so good...");
    }

    @PutMapping("/manageUsers/changeRole/{userId}")
    public ResponseEntity changeUserRole(@RequestBody UserRoles userRole, @PathVariable String userId) {
        boolean isDone = adminService.changeUserRole(userRole, userId);
        if (isDone)
            return new ResponseEntity<>(HttpStatus.CREATED);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
