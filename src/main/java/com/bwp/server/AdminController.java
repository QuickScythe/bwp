package com.bwp.server;

import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class AdminController {

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("userId") != null)
            return "redirect:/admin";
        return "login";
    }

    @PostMapping("/login/submit")
    public String loginSubmit(HttpSession session, @RequestBody String raw) {
        if(!raw.trim().startsWith("{") || !raw.trim().endsWith("}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid talent data format.");
        JSONObject body = new JSONObject(raw);
        if(!body.has("username") || !body.has("password"))
            return "redirect:/login?error=missing_fields";
        String username = body.getString("username");
        String password = body.getString("password");
        UsersConfig usersConfig = Utils.getConfig(UsersConfig.class);
        for (User user : usersConfig.users.values()) {
            if (user.username.equals(username) && user.password.equals(password)) {
                session.setAttribute("userId", user.id());
                return "redirect:/admin";
            }
        }
        return "redirect:/login?error=invalid_credentials";
    }

    @GetMapping("/admin")
    public String admin(HttpSession session) {
        if (session.getAttribute("userId") == null)
            return "redirect:/login";

        String userId = (String) session.getAttribute("userId");
        User user = Utils.getConfig(UsersConfig.class).get(userId);
        if (user == null)
            return "redirect:/login?error=invalid_user";

        return "admin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?message=logged_out";
    }
}
