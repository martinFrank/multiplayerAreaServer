package com.github.martinfrank.multiplayerserver;

import com.github.martinfrank.multiplayerserver.map.MapProvider;
import com.github.martinfrank.multiplayerserver.model.AreaModel;
import com.github.martinfrank.multiplayerserver.server.AcceptHandler;
import com.github.martinfrank.multiplayerserver.server.NonBlockingServer;
import com.github.martinfrank.multiplayerserver.server.ReadHandler;

import java.io.IOException;

public class AreaService {

    private final AreaTickRunnable areaTickRunnable;
    private final AreaModel areaModel;

    private final NonBlockingServer nonBlockingServer;
    private final AcceptHandler acceptHandler;
    private final ReadHandler readHandler;

    public AreaService () throws Exception {
        MapProvider mapProvider = new MapProvider();
        areaModel = new AreaModel(mapProvider.getMap()); //FIXME areaspecific parameters in construktor (from this constructors paramaters)
        acceptHandler = new AcceptHandler();
        readHandler = new ReadHandler();
        nonBlockingServer = new NonBlockingServer(10523, acceptHandler, readHandler); //FIXME magic Number!
        areaTickRunnable = new AreaTickRunnable(250, areaModel, nonBlockingServer); //FIXME magic Number!
        new Thread(areaTickRunnable).start();
        new Thread(nonBlockingServer).start();
    }

    public static void main(String[] args) throws Exception {
        new AreaService();
    }


}
