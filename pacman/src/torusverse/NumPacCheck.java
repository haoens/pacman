package src.torusverse;

import src.Logger;
import src.pathfinding.Node;

import java.util.List;

public class NumPacCheck implements LevelCheck {

    private final List<Node> pacLocations;
    private final String filename;
    public NumPacCheck(List<Node> pacLocations, String filename){
        this.pacLocations = pacLocations;
        this.filename = filename;
    }
    @Override
    public boolean doCheck() {
        if (this.pacLocations.size() != 1) {
            Logger logger = Logger.getInstance();
            logger.logNotOnePac(this.pacLocations, filename);
            return false;
        }
        return true;
    }
}
