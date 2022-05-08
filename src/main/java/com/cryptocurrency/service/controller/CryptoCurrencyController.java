package com.cryptocurrency.service.controller;

import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yuvaraj.sanjeevi
 */
@Controller
@RequestMapping("/cryptoCurrencies")
public class CryptoCurrencyController {

    @GetMapping("/showForm")
    public String main(Model model) {
        model.addAttribute("cryptoCurrencyConvertRequest", new CryptoCurrencyConvert());
        return "index";
    }

    /*@PostMapping
    public String save(Order order, Model model) {
        model.addAttribute("order", order);
        return "saved";
    }*/
}
