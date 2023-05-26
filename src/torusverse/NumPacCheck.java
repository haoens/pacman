package torusverse;

import src.pathfinding.Node;

import java.util.List;
import java.util.stream.Collectors;

public class NumPacCheck implements LevelCheck {

    private final List<Node> pacLocations;
    private final String filename;
    public NumPacCheck(List<Node> pacLocations, String filename){
        this.pacLocations = pacLocations;
        this.filename = filename;
    }
    @Override
    public boolean doCheck() {
        if (this.pacLocations.size() < 1){
            System.out.println("[Level " + filename + " - no start for PacMan]");
            return false;
        }
        else if (this.pacLocations.size() > 1) {
            List<String> locations = this.pacLocations.stream().
                    map(Object::toString).
                    collect(Collectors.toList());
            System.out.println("[Level " + filename + " - more than one start for Pacman: " +
                    String.join("; ", locations) + "]");
            return false;
        }
        return true;
    }
}
