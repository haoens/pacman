package src.torusverse;

import src.pathfinding.Node;

import java.util.ArrayList;
import java.util.List;

public class BaseTorusCheck implements LevelCheck {
    private final ArrayList<LevelCheck> childChecks = new ArrayList<>();
    private final List<Node> goldLocations = new ArrayList<>();
    private final List<Node> pillLocations = new ArrayList<>();
    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;
    public BaseTorusCheck(MapInterface map, String filename) {
        parseMap(map.getMap());
        addChildCheck(new AccessibilityCheck(map, nbHorzCells, nbVertCells, filename));
        addChildCheck(new MinPillAndGoldCheck(this.goldLocations, this.pillLocations, filename));
    }

    public void parseMap(char[][] map){
        if (map.length == 0) return;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                char a = map[y][x];
                switch (a) {
                    case 'd' -> goldLocations.add(new Node(x, y));
                    case 'c' -> pillLocations.add(new Node(x, y));
                    default -> {}
                }
            }
        }
    }
    private void addChildCheck(LevelCheck childCheck) {
        this.childChecks.add(childCheck);
    }
    @Override
    public boolean doCheck() {
        boolean passAllChecks = true;
        for (LevelCheck levelCheck: childChecks) {
            if (!levelCheck.doCheck()) passAllChecks = false;
        }
        return passAllChecks;
    }
}
