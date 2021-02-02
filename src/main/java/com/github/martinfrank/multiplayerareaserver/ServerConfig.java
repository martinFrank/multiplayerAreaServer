package com.github.martinfrank.multiplayerareaserver;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:ServerConfig.properties"})
public interface ServerConfig extends Config {

    @Key("server.http.port")
    int port();

    @Key("server.host.name")
    String hostname();

    @Key("server.max.threads")
        //@DefaultValue("42")
    int maxThreads();

    @Key("meta.server.address")
    String metaServerAddress();

    @Key("meta.server.port")
    int metaServerPort();

    @Key("area.mapid")
    String areaMapId();

    @Key("area.downloadDir")
    String areaDownloadDir();

    @Key("area.downloadFile")
    String areaDownloadFile();

}
