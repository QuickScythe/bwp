package com.bwp.server.api;

import com.bwp.data.account.User;
import com.bwp.data.config.UsersConfig;
import com.bwp.server.ApiController;
import com.bwp.utils.Utils;
import com.bwp.utils.secrets.Permission;
import com.bwp.utils.secrets.Permissions;
import com.bwp.utils.secrets.Token;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/token")
public class TokenApiController extends ApiController {

    @PostMapping("/list")
    public List<String> list(@RequestBody String requestRaw){
        Token token = validateToken(requestRaw, Permissions.READ_SECRETS);
        if (token == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There was an error validating your token.");
        List<String> response = new ArrayList<>();
        for(Token otherToken : token.user().tokens.values()){
            response.add(otherToken.id);
        }
        return response;
    }

    @PostMapping("/generate")
    public String generate(@RequestBody String tokenContainer) {
        Token token = validateToken(tokenContainer, Permissions.WRITE_SECRETS);
        if (token == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There was an error validating your token.");
        JSONObject request = new JSONObject(tokenContainer);
        JSONObject response = new JSONObject();
        if (!request.has("permissions"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required Array field: permissions");
        JSONArray permsArray = request.getJSONArray("permissions");
        Permission[] perms = new Permission[permsArray.length()];
        User user = token.user();
        if(user == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token owner not found.");
        for (int i = 0; i < permsArray.length(); i++) {
            Object obj = permsArray.get(i);
            if (!(obj instanceof String key))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid permission format in permissions array. Must be String Array");
            if(!user.permissions.contains(key) && !user.permissions.contains(Permissions.ALL))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission '" + key + "' and therefor can not assign it to the new token.");
            if(Permissions.of(key).isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission '" + key + "' does not exist.");
            perms[i] = Permissions.of(key).get();
        }
        Token newToken = new Token(user);
        response.put("token", newToken.id);
        for (Permission perm : perms) {
            newToken.permissions.put(perm);
        }
        Utils.getConfig(UsersConfig.class).save();
        return response.toString();
    }
}
