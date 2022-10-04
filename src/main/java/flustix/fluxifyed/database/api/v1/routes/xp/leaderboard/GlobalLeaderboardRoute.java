package flustix.fluxifyed.database.api.v1.routes.xp.leaderboard;

import com.sun.net.httpserver.HttpExchange;
import flustix.fluxifyed.database.api.v1.components.xp.GlobalLeaderboard;
import flustix.fluxifyed.database.api.v1.types.APIResponse;
import flustix.fluxifyed.database.api.v1.types.Route;
import flustix.fluxifyed.utils.json.JSONUtils;

import java.util.HashMap;

public class GlobalLeaderboardRoute implements Route {
    public APIResponse execute(HttpExchange exchange, HashMap<String, String> params) throws Exception {
        return new APIResponse(200, "OK", new GlobalLeaderboard());
    }
}
