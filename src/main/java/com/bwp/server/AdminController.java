package com.bwp.server;

import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.bwp.utils.Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * Administrative MVC controller handling login/logout and the admin dashboard.
 * Routes:
 * - GET /login           → Render login page; auto-logs in if cookie present
 * - POST /login/submit   → Process login credentials (supports JSON or form-encoded)
 * - GET /admin           → Render admin dashboard (requires session)
 * - GET /logout          → Invalidate session and clear cookie
 */
@Controller
public class AdminController {

    /**
     * Renders the login page and restores an existing session from the userId cookie if present.
     *
     * @param request the HTTP request used to read cookies
     * @param session the current HTTP session, updated with userId when cookie exists
     * @return view name "login" or redirect to "/admin" if already logged in
     */
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

    /**
     * Processes login form submissions.
     * Supports either application/json (with fields "username" and "password")
     * or application/x-www-form-urlencoded (username=...&password=...).
     * <p>
     * On success, sets a session attribute and a persistent userId cookie (3 days) and redirects to /admin.
     * On failure, redirects back to /login with an error indicator.
     *
     * @param response HTTP response used to set the userId cookie
     * @param session  HTTP session used to store the authenticated user id
     * @param raw      raw request body containing credentials in JSON or form encoding
     * @return redirect to "/admin" on success; otherwise redirect to "/login?error=..."
     * @throws ResponseStatusException 400 when missing or blank fields are provided
     */
    @PostMapping("/login/submit")
    public String loginSubmit(HttpServletResponse response, HttpSession session, @RequestBody String raw) {
        System.out.println("Raw: " + raw);
        String username = "";
        String password = "";
        if (!raw.trim().startsWith("{") || !raw.trim().endsWith("}")) {
            String[] parts = raw.split("&");

            for (String part : parts) {
                if (part.startsWith("username="))
                    username = part.replace("username=", "");
                else if (part.startsWith("password="))
                    password = part.replace("password=", "");
            }
        } else {
            JSONObject body = new JSONObject(raw);
            if (!body.has("username") || !body.has("password"))
                return "redirect:/login?error=missing_fields";
            username = body.getString("username");
            password = body.getString("password");
        }
        if (username.isBlank() || password.isBlank())
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

    /**
     * Renders the admin dashboard view for an authenticated session.
     *
     * @param session current HTTP session; must contain a "userId" attribute
     * @return view name "admin" or redirect to "/login" if not authenticated
     */
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

    /**
     * Logs out the current user by invalidating the session and expiring the userId cookie.
     *
     * @param response HTTP response used to clear the cookie
     * @param session  current HTTP session to invalidate
     * @return redirect to the login page with a logout message
     */
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
