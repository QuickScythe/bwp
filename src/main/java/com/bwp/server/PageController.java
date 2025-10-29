package com.bwp.server;

import com.bwp.Main;
import com.bwp.data.Talent;
import com.bwp.data.config.TalentConfig;
import com.quiptmc.core.config.ConfigManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PageController {

    @GetMapping("/error")
    public String error(HttpServletRequest request, HttpSession session, Model model) {
        model.addAttribute("status", request.getAttribute("javax.servlet.error.status_code"));
        model.addAttribute("error", request.getAttribute("javax.servlet.error.error_message"));
        model.addAttribute("message", request.getAttribute("javax.servlet.error.message"));
        model.addAttribute("path", request.getAttribute("javax.servlet.error.request_uri"));
        return "error";
    }

    @GetMapping("/")
    public String home(Model model) throws SQLException {
        return "home";
    }

    @GetMapping("/talent")
    public String talent(Model model) {


        TalentConfig config = ConfigManager.getConfig(Main.INTEGRATION, TalentConfig.class);

        Map<String, Talent> people = new HashMap<>();
        for(Talent talent : config.talents.values()){
            people.put(talent.fullName(), talent);

        }
        model.addAttribute("people", people);
        return "talent";
    }
}
