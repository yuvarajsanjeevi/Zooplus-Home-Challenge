package com.cryptocurrency.service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yuvaraj.sanjeevi
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String main(Model model) {
        return "redirect:/cryptoCurrencies/showForm";
    }
}
