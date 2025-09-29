package com.bwp.server;

import com.bwp.data.Actor;
import com.bwp.data.config.Configs;
import com.bwp.data.config.TalentConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    @GetMapping("/")
    public String home(Model model) throws SQLException {
        return "home";
    }

    @GetMapping("/talent")
    public String talent(Model model) throws SQLException {


        TalentConfig config = Configs.talent();
        Map<String, Actor> people = new HashMap<>();
        for(Actor actor : config.talents.values()){
            people.put(actor.fullName(), actor);

        }
        model.addAttribute("people", people);
        return "talent";
    }
}
