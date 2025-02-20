package io.quarkiverse.playpen;

import java.util.function.Supplier;

import org.jboss.logging.Logger;

import io.quarkiverse.playpen.client.PlaypenConnectionConfig;
import io.quarkus.runtime.ShutdownContext;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;

@Recorder
public class LocalPlaypenRecorder {
    private static final Logger log = Logger.getLogger(LocalPlaypenRecorder.class);

    static VirtualPlaypenClient client;
    public static PlaypenConnectionConfig config;
    static Vertx vertx;

    public void init(Supplier<Vertx> vertx, ShutdownContext shutdown, PlaypenConnectionConfig c, boolean delayConnect) {
        config = c;
        LocalPlaypenRecorder.vertx = vertx.get();
        if (!delayConnect) {
            startSession(LocalPlaypenRecorder.vertx, c);
            shutdown.addShutdownTask(() -> {
                closeSession();
            });
        }
    }

    public static void startSession() {
        startSession(vertx, config);
    }

    public static void startSession(Vertx vertx, PlaypenConnectionConfig config) {
        client = new VirtualPlaypenClient();
        HttpClientOptions options = new HttpClientOptions();
        options.setDefaultHost(config.host);
        options.setDefaultPort(config.port);
        if (config.ssl) {
            options.setSsl(true).setTrustAll(true);
        }
        client.setCredentials(config.credentials);
        client.setProxyClient(vertx.createHttpClient(options));
        client.vertx = vertx;
        client.initUri(config);
        client.start();
    }

    public static void closeSession() {
        if (client != null)
            client.shutdown();
        client = null;
    }
}
