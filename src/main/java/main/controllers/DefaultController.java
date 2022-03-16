package main.controllers;

import lombok.RequiredArgsConstructor;
import main.api.response.InitResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class DefaultController {

    @RequestMapping("/")
    public String index (Model model) {
        return "index";
    }
}
