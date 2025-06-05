package com.practice1.controller;

import com.practice1.dto.AdminDto;
import com.practice1.entities.Admin;
import com.practice1.mapper.AdminMapper;
import com.practice1.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("/signup")
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        Admin admin = adminService.createAdmin(adminDto);
        return ResponseEntity.ok().body(adminMapper.toAdminDto(admin));
    }
}
