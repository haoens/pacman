package src;

import ch.aplu.jgamegrid.Actor;

public class GamePortal extends Actor {
    private GamePortal pairedPortal;
    private int index;

    public GamePortal(String filename, int index) {
        super(filename);
        this.index = index;
    }

    public void setPairedPortal(GamePortal pairedPortal) {
        this.pairedPortal = pairedPortal;
    }

    public GamePortal getPairedPortal() {
        return pairedPortal;
    }

    public int getIndex() {
        return index;
    }
}
