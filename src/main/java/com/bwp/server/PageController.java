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

/**
 * MVC controller for rendering Thymeleaf pages.
 * <p>
 * Routes:
 * - GET /error  → error page with request attributes
 * - GET /       → home page
 * - GET /talent → talent listing page
 */
@Controller
public class PageController {

    /**
     * Renders the error page using standard servlet error attributes.
     *
     * @param request the HTTP request containing error attributes
     * @param session the current HTTP session
     * @param model   the view model populated with error details
     * @return the Thymeleaf template name "error"
     */
    @GetMapping("/error")
    public String error(HttpServletRequest request, HttpSession session, Model model) {
        model.addAttribute("status", request.getAttribute("javax.servlet.error.status_code"));
        model.addAttribute("error", request.getAttribute("javax.servlet.error.error_message"));
        model.addAttribute("message", request.getAttribute("javax.servlet.error.message"));
        model.addAttribute("path", request.getAttribute("javax.servlet.error.request_uri"));
        return "error";
    }

    /**
     * Renders the home page.
     *
     * @param model the view model
     * @return the Thymeleaf template name "home"
     * @throws SQLException reserved for future data access expansions
     */
    @GetMapping("/")
    public String home(Model model) throws SQLException {
        return "home";
    }

    /**
     * Renders the talent listing page with a map of full name -> Talent.
     *
     * @param model the view model populated with people
     * @return the Thymeleaf template name "talent"
     */
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
