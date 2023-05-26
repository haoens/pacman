package src.torusverse;

import src.Logger;
import src.pathfinding.Node;

import java.util.List;

public class MinPillAndGoldCheck implements LevelCheck {

    private final List<Node> goldLocations;
    private final List<Node> pillLocations;
    private String filename;
    public MinPillAndGoldCheck(List<Node> goldLocations, List<Node> pillLocations, String filename){
        this.goldLocations = goldLocations;
        this.pillLocations = pillLocations;
        this.filename = filename;
    }

    @Override
    public boolean doCheck() {
        if (goldLocations.size() + pillLocations.size() < 2) {
            //System.out.println("[Level " + filename + " - less than 2 Gold and Pill]");
            Logger logger = Logger.getInstance();
            logger.logAtLeastTwoGoldPill(filename);
            return false;
        }
        return true;
    }
}
