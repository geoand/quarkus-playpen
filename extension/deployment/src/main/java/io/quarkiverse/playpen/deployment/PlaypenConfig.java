package io.quarkiverse.playpen.deployment;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "playpen", phase = ConfigPhase.BUILD_TIME)
public class PlaypenConfig {
    /**
     * Only usable with quarkus dev mode and the quarkus.playpen.command property is NOT set.
     *
     * URL that points to playpen proxy server.
     * Query parameters provide additional config
     *
     * Should specify scheme, ip/dns name, and path the matches the playpen.
     * Usually this is:
     *
     * http[s]://host:port[/ingress-prefix]/local/:who
     *
     * ":who" identifies who is making this playpen connection and is required
     * Can be any valid path string.
     *
     * Query parameters:
     *
     * global - default value is "false"
     * if "true", playpen proxy will route all requests to local playpen
     * if "false", playpen proxy routes request only if "X-Playpen-Session" header is set
     * or one of the other config parameters adds additional ways to match a session
     * query - "query=name=value" If any requests have a query parameter that matches the name and
     * value specified, then the request will be routed to playpen
     * header - "header=name=value" If any requests have a request header that matches the name and
     * value specified, then the request will be routed to playpen
     * path - "path=prefix" If any request urls are prefixed with the pathvalue, that matches and
     * requests will be routed
     * clientIp - "clientIp=ipAddress" If client IP address of request matches, then requests will
     * be routed to playpen
     *
     */
    @ConfigItem
    public Optional<String> local;

    /**
     * Only usable in remote dev mode and the quarkus.playpen.command property is NOT set.
     *
     * URL that points to playpen proxy server.
     * Query parameters provide additional config
     *
     * if url scheme, host, port and path are not set, then they must
     * be defined in quarkus.live-reload.url
     * Usually this is:
     *
     * http[s]://host:port[/ingress-prefix]/remote/:who
     *
     * ":who" identifies who is making this playpen connection and is required
     * Can be any valid path string.
     *
     * Query parameters:
     * If "host" parameter is not set, then the code if your project will be uploaded
     * to a temporary pod running within the development cluster. When the remote dev session
     * is ended, the temporary pod will be terminated.
     *
     *
     * cleanup - "cleanup=true|false" Whether the remote playpen container should be cleaned up when disconnecting
     * host - "host=hostname[:port] Host and port of a container within the development cluster that
     * is running your playpen
     * global - default value is "false"
     * if "true", playpen proxy will route all requests to local playpen
     * if "false", playpen proxy routes request only if "X-Playpen-Session" header is set
     * or one of the other config parameters adds additional ways to match a session
     * query - "query=name=value" If any requests have a query parameter that matches the name and
     * value specified, then the request will be routed to playpen
     * header - "header=name=value" If any requests have a request header that matches the name and
     * value specified, then the request will be routed to playpen
     * path - "path=prefix" If any request urls are prefixed with the pathvalue, that matches and
     * requests will be routed
     * clientIp - "clientIp=ipAddress" If client IP address of request matches, then requests will
     * be routed to playpen
     */
    @ConfigItem
    public Optional<String> remote;

    /**
     * Credentials for creating a connection. Secret or "username:password".
     */
    @ConfigItem
    public Optional<String> credentials;

    /**
     * Must set quarkus.playpen.remote. If this variable isn't a full connection url
     * you must also specify quarkus.live-reload.url.
     *
     * Execute a playpen command.
     * Only the command will execute. dev and remote-dev modes will not create a connection
     *
     * remote-create - creates a remote container based on this project.
     * remote-delete - delete a remote container
     * remote-get - Specify host[:port] of remote playpen container
     */
    @ConfigItem
    public Optional<String> command;

    /**
     * If true, quarkus will not connect to local playpen on boot. Connection would have
     * to be done manually from the recorder method.
     *
     * This is for internal testing purposes only.
     */
    @ConfigItem(defaultValue = "false")
    public boolean manualStart;

}
