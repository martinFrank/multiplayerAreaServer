package com.github.martinfrank.multiplayerareaserver.server;

public class SelectionKeyId {

    public final String adress;
    public final long id;

    public SelectionKeyId(String adress, long id) {
        this.adress = adress;
        this.id = id;
    }

    @Override
    public String toString() {
        return "IdAttachment{" +
                "adress='" + adress + '\'' +
                ", id=" + id +
                '}';
    }

    public boolean sameId(Object object) {
        if (object instanceof SelectionKeyId) {
            SelectionKeyId attachment = (SelectionKeyId) object;
            return attachment.id == id;
        }
        return false;
    }
}
