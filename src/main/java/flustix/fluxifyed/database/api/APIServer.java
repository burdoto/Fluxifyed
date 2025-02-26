package flustix.fluxifyed.database.api;

import com.sun.net.httpserver.HttpServer;
import flustix.fluxifyed.database.api.v1.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIServer {
    public static final Logger LOGGER = LoggerFactory.getLogger("FluxifyedAPI");

    public static void main() throws Exception {
        Router router = new Router();
        router.init();

        int port = 6679;

        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        server.createContext("/", router);
        server.setExecutor(null);
        server.start();

        LOGGER.debug("API Server started on port {}", port);
    }
}
