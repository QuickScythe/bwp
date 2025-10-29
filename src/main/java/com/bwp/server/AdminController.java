package com.bwp.server;

import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.CookieManager;
import java.util.List;

@Controller
public class AdminController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        Cookie[] cookies = request.getCookies();
        if (!(cookies == null)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    session.setAttribute("userId", cookie.getValue());
                }
            }
        }
        if (session.getAttribute("userId") != null)
            return "redirect:/admin";
        return "login";
    }

    @PostMapping("/login/submit")
    public String loginSubmit(HttpServletResponse response, HttpSession session, @RequestBody String raw) {
        System.out.println("Raw: " + raw);
        String username = "";
        String password = "";
        if(!raw.trim().startsWith("{") || !raw.trim().endsWith("}")){
            String[] parts = raw.split("&");

            for(String part : parts){
                if(part.startsWith("username="))
                    username = part.replace("username=", "");
                else if(part.startsWith("password="))
                    password = part.replace("password=", "");
            }
        } else {
            JSONObject body = new JSONObject(raw);
            if(!body.has("username") || !body.has("password"))
                return "redirect:/login?error=missing_fields";
            username = body.getString("username");
            password = body.getString("password");
        }
        if(username.isBlank() || password.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid talent data format.");

        UsersConfig usersConfig = Utils.getConfig(UsersConfig.class);
        for (User user : usersConfig.users.values()) {
            if (user.username.equals(username) && user.password.equals(password)) {
                Cookie cookie = new Cookie("userId", user.id());
                cookie.setPath("/");
                cookie.setMaxAge(3 * 24 * 60 * 60); // 3 days
                response.addCookie(cookie);
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
    public String logout(HttpServletResponse response, HttpSession session) {
        session.invalidate();
        // Remove the cookie
        Cookie cookie = new Cookie("userId", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set cookie to expire immediately
        response.addCookie(cookie);
        return "redirect:/login?message=logged_out";
    }


}
