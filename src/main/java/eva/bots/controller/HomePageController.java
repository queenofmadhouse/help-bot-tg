package eva.bots.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@Slf4j
public class HomePageController {


    @GetMapping("/")
    public String getHomePage() {
        log.info("Trying to get home page");
        System.out.println("Trying to get home page");

        return "index";
    }
}
