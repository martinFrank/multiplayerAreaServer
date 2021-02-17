package com.github.martinfrank.multiplayerareaserver.server;

public class IdAttachment {

    public final String adress;
    public final long id;

    public IdAttachment(String adress, long id) {
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
        if (object instanceof IdAttachment) {
            IdAttachment attachment = (IdAttachment) object;
            return attachment.id == id;
        }
        return false;
    }
}
