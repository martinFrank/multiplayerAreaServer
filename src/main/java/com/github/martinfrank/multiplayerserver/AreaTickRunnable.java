package com.github.martinfrank.multiplayerserver;

import com.github.martinfrank.multiplayerserver.model.AreaModel;
import com.github.martinfrank.multiplayerserver.model.MapChanges;
import com.github.martinfrank.multiplayerserver.server.NonBlockingServer;

import java.io.IOException;
import java.util.Calendar;

public class AreaTickRunnable implements Runnable{

    private final int delayInMillis;
    private final AreaModel areaModel;
    private final NonBlockingServer nonBlockingServer;
    public AreaTickRunnable(int delayInMillis, AreaModel areaModel, NonBlockingServer nonBlockingServer) {
        this.delayInMillis = delayInMillis;
        this.areaModel = areaModel;
        this.nonBlockingServer = nonBlockingServer;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(delayInMillis);
                MapChanges mapChanges = areaModel.tick();
                nonBlockingServer.broadcast(mapChanges);
            } catch (InterruptedException | IOException e) {
                //FIXME
                e.printStackTrace();
                break;
            }
        }
    }
}
