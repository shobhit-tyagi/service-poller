package com.example.servicepoller.api.v1.controller;

import com.example.servicepoller.api.v1.response.builder.GetServiceResponseBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@AllArgsConstructor
public class UIController {

    private final GetServiceResponseBuilder getServiceResponseBuilder;

    @GetMapping("/")
    public String showUserList(final Model model) {
        model.addAttribute("services", getServiceResponseBuilder.getAll().getBody());
        return "index";
    }
}
