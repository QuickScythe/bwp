package com.bwp.server.api;

import com.bwp.server.ApiController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User REST API placeholder for future user management endpoints.
 * Base path: /api/users
 * Currently extends ApiController without declaring endpoints. Add methods with
 * appropriate @GetMapping/@PostMapping as needed and reuse validateToken(...) for auth.
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController extends ApiController {
}
