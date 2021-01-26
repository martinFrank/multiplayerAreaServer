package com.github.martinfrank.multiplayerserver.model;

import java.util.Calendar;

public class MapChanges {
    public String createBroadCastMessage() {
        return "mapChanges: "+ Calendar.getInstance().get(Calendar.SECOND);
    }
}
