package src.torusverse;

import ch.aplu.jgamegrid.Location;
import src.Logger;
import src.pathfinding.Grid;
import src.pathfinding.Node;
import src.pathfinding.PathFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessibilityCheck implements LevelCheck {

    private final char[][] map;
    private final ArrayList<LevelCheck> childChecks = new ArrayList<>();
    private final List<Node> goldLocations = new ArrayList<>();
    private final List<Node> pillLocations = new ArrayList<>();
    private final List<Node> pacLocations = new ArrayList<>();
    private final HashMap<String, List<MapPortal>> portals;
    private final List<MapPortal> linkedPortals = new ArrayList<>();
    private final int nbHorzCells;
    private final int nbVertCells;
    private String filename;

    public AccessibilityCheck(MapInterface map, int nbHorzCells, int nbVertCells, String filename)
    {
        this.map = map.getMap();
        this.portals = initialisePortalList();
        this.nbHorzCells = nbHorzCells;
        this.nbVertCells = nbVertCells;
        this.filename = filename;
        parseMap(this.map);
        linkPortals();
        addChildCheck(new NumPacCheck(pacLocations, filename));
        addChildCheck(new PortalsCheck(portals, filename));
    }
    public void parseMap(char[][] map){
        if (map.length == 0) return;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                char a = map[y][x];
                switch (a) {
                    case 'd' -> goldLocations.add(new Node(x, y));
                    case 'c' -> pillLocations.add(new Node(x, y));
                    case 'f' -> pacLocations.add(new Node(x, y));
                    case 'i' -> portals.get("White").add(new MapPortal(x, y));
                    case 'j' -> portals.get("Yellow").add(new MapPortal(x, y));
                    case 'k' -> portals.get("DarkGold").add(new MapPortal(x, y));
                    case 'l' -> portals.get("DarkGray").add(new MapPortal(x, y));
                    default -> {}
                }
            }
        }
    }
    public HashMap<String, List<MapPortal>> initialisePortalList() {
        HashMap<String, List<MapPortal>> portals = new HashMap<>();
        String[] portalColors = {"White", "Yellow", "DarkGold", "DarkGray"};
        for (String color : portalColors) {
            portals.put(color, new ArrayList<>());
        }
        return portals;
    }

    public void linkPortals(){
        for (Map.Entry<String, List<MapPortal>> entry: portals.entrySet()){
            List<MapPortal> portals = entry.getValue();
            if (portals.size() == 2) {
                portals.get(0).joinPortals(portals.get(1));
                this.linkedPortals.add(portals.get(0));
                this.linkedPortals.add(portals.get(1));
            }
        }
    }
    public boolean checkAllPillsAndGold(){
        boolean[][] walkableTiles = new boolean[nbHorzCells][nbVertCells];
        for(int i = 0; i < nbVertCells; i++) {
            for(int j = 0; j < nbHorzCells; j++) {
                char mapChar = map[i][j];
                if(mapChar != 'b') {
                    walkableTiles[j][i] = true;
                }
            }
        }
        Grid grid = new Grid(nbHorzCells, nbVertCells, walkableTiles);
        HashMap<Location, Location> portalLocations = new HashMap<>();
        for (MapPortal portal : linkedPortals) {
            Location portalLocation = new Location(portal.getNode().x, portal.getNode().y);
            MapPortal connectedPortal = portal.getConnectedPortal();
            Location connectedPortalLocation = new Location(connectedPortal.getNode().x, connectedPortal.getNode().y);
            portalLocations.put(portalLocation, connectedPortalLocation);
        }

        List<Node> inaccessible_pills = new ArrayList<>();
        Location pac_start = new Location(pacLocations.get(0).x, pacLocations.get(0).y);
        if (pac_start == null) return false;
        for (Node pill: pillLocations) {
            Location pillLocation = new Location(pill.x, pill.y);
            List<Location> path = PathFinding.findPath(grid, pac_start, pillLocation, false, portalLocations);
            if (path.isEmpty()) { // no path exists
                inaccessible_pills.add(pill);
            }
        }
        List<Node> inaccessible_gold = new ArrayList<>();
        for (Node gold: goldLocations) {
            Location goldLocation = new Location(gold.x, gold.y);
            List<Location> path = PathFinding.findPath(grid, pac_start, goldLocation, false, portalLocations);
            if (path.isEmpty()) { // no path exists
                inaccessible_gold.add(gold);
            }
        }

        if (inaccessible_gold.size() > 0 || inaccessible_pills.size() > 0) {
            Logger logger = Logger.getInstance();
            logger.logGoldPillNotAccessible(inaccessible_gold, inaccessible_pills, filename);
        }
        return inaccessible_pills.size() == 0 && inaccessible_gold.size() == 0;
    }

    private void addChildCheck(LevelCheck childCheck) {
        this.childChecks.add(childCheck);
    }
    @Override
    public boolean doCheck() {
        boolean passedChildChecks = true;
        for (LevelCheck childCheck: this.childChecks) {
            if (!childCheck.doCheck()) passedChildChecks = false;
        }
        if (!passedChildChecks) return false;
        return checkAllPillsAndGold();
    }
}
