package src;

import ch.aplu.jgamegrid.Actor;

public class Portal extends Actor {
    private Portal pairedPortal;
    private int index;

    public Portal(String filename, int index) {
        super(filename);
        this.index = index;
    }

    public void setPairedPortal(Portal pairedPortal) {
        this.pairedPortal = pairedPortal;
    }

    public Portal getPairedPortal() {
        return pairedPortal;
    }

    public int getIndex() {
        return index;
    }
}
