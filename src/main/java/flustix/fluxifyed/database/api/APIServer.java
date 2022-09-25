package flustix.fluxifyed.database.api;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import flustix.fluxifyed.database.api.routes.CommandsRoute;
import flustix.fluxifyed.database.api.routes.guild.GuildRoute;
import flustix.fluxifyed.database.api.routes.guild.GuildsRoute;
import flustix.fluxifyed.database.api.routes.LoginRoute;
import flustix.fluxifyed.database.api.routes.xp.XPUserRoute;
import flustix.fluxifyed.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIServer {
    public static Logger LOGGER = LoggerFactory.getLogger("FluxifyedAPI");

    public static void main() throws Exception {
        Router router = new Router();

        router.addRoute("/guilds", new GuildsRoute());
        router.addRoute("/guild/:id", new GuildRoute());

        router.addRoute("/login", new LoginRoute());

        router.addRoute("/commands", new CommandsRoute());

        router.addRoute("/xp/:guild/:user", new XPUserRoute());

        router.addRoute("/db", (exchange, params) -> {
            JsonObject json = new JsonObject();
            json.addProperty("connectionCount", Database.connectionCount());
            return json;
        });

        int port = 6679;

        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        server.createContext("/", router);
        server.setExecutor(null);
        server.start();

        LOGGER.debug("API Server started on port {}", port);
    }
}
