package com.cryptocurrency.service.controller;

import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import com.cryptocurrency.service.service.CryptoCurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yuvaraj.sanjeevi
 */
@AllArgsConstructor
@Controller
@RequestMapping("/cryptoCurrencies")
public class CryptoCurrencyController {

    private final CryptoCurrencyService cryptoCurrencyService;

    @GetMapping("/showForm")
    public String showForm(Model model) {
        model.addAttribute("cryptoCurrencyConvertRequest", new CryptoCurrencyConvert());
        model.addAttribute("cryptoCurrencyList", cryptoCurrencyService.getAll());
        return "index";
    }

    @PostMapping(value = "/convertToLocalCurrency")
    public String convertToLocalCurrency(@ModelAttribute("cryptoCurrencyConvertRequest") CryptoCurrencyConvert cryptoCurrencyConvert, Model model, HttpServletRequest request) {
        model.addAttribute("cryptoCurrencyConvertRequest", cryptoCurrencyConvert);
        model.addAttribute("cryptoCurrencyList", cryptoCurrencyService.getAll());
        cryptoCurrencyService.convertToLocalCurrency(cryptoCurrencyConvert, request, model);
        return "index";
    }
}
