package torusverse;

import ch.aplu.jgamegrid.Location;
import src.pathfinding.Grid;
import src.pathfinding.Node;
import src.pathfinding.PathFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Basic Path finding took from here https://gamedev.stackexchange.com/questions/197165/java-simple-2d-grid-pathfinding
public class LevelChecker {
    private final char[][] map;
    private final List<Node> goldLocations;
    private final List<Node> pillLocations;
    private final List<Node> pacLocations;

    private final HashMap<String, List<Portal>> portals;
    private final List<Portal> linkedPortals;
    private final static int nbHorzCells = 20;
    private final static int nbVertCells = 11;

    public LevelChecker(char[][] map) {
        this.map = map;
        this.goldLocations = new ArrayList<>();
        this.pillLocations = new ArrayList<>();
        this.pacLocations = new ArrayList<>();
        this.portals = initialisePortalList();
        this.linkedPortals = new ArrayList<>();
        parseMap();
        linkPortals();
    }

    public LevelChecker(int[][] intMap) {
        this.map = intMapToChar(intMap);
        this.goldLocations = new ArrayList<>();
        this.pillLocations = new ArrayList<>();
        this.pacLocations = new ArrayList<>();
        this.portals = initialisePortalList();
        this.linkedPortals = new ArrayList<>();
        parseMap();
        linkPortals();
    }

    public char[][] intMapToChar(int[][] intMap) {
        char[][] arr = new char[intMap.length][intMap[0].length];
        for (int y = 0; y < intMap.length; y++){
            for (int x = 0; x < intMap[0].length; x++){
                int a = intMap[y][x];
                char c ;
                switch (a) {
                    case 0 -> c = 'b';
                    case 1 -> c = 'c';
                    case 3 -> c = 'd';
                    case 4 -> c = 'e';
                    case 5 -> c = 'f';
                    case 6 -> c = 'g';
                    case 7 -> c = 'h';
                    case 8 -> c = 'i';
                    case 9 -> c = 'j';
                    case 10 -> c = 'k';
                    case 11 -> c = 'l';
                    default -> c = 'a';
                }
                arr[y][x] = c;
            }
        }
        return arr;
    }

    public HashMap<String, List<Portal>> initialisePortalList() {
        HashMap<String, List<Portal>> portals = new HashMap<>();
        String[] portalColors = {"White", "Yellow", "DarkGold", "DarkGray"};
        for (String color: portalColors) {
            portals.put(color, new ArrayList<>());
        }
        return portals;
    }

    public boolean checkPortals(){
        for (Map.Entry<String, List<Portal>> entry: portals.entrySet()){
            List<Portal> portals = entry.getValue();
            if (portals.size() != 2) {
                if (portals.size() != 0) {
                    List<String> portalLocations = portals.stream().map(Portal::getNode)
                            .map(Object::toString)
                            .collect(Collectors.toList());
                    System.out.println("portal " + entry.getKey() + " count is not 2: " +
                            String.join("; ", portalLocations));
                }
            }
        }
        return true;
    }

    public void linkPortals(){
        for (Map.Entry<String, List<Portal>> entry: portals.entrySet()){
            List<Portal> portals = entry.getValue();
            if (portals.size() == 2) {
                portals.get(0).joinPortals(portals.get(1));
                this.linkedPortals.add(portals.get(0));
                this.linkedPortals.add(portals.get(1));
            }
        }
    }

    public void parseMap(){
        if (map.length == 0) return;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                char a = map[y][x];
                switch (a) {
                    case 'd' -> goldLocations.add(new Node(x, y));
                    case 'c' -> pillLocations.add(new Node(x, y));
                    case 'f' -> pacLocations.add(new Node(x, y));
                    case 'i' -> portals.get("White").add(new Portal(x, y));
                    case 'j' -> portals.get("Yellow").add(new Portal(x, y));
                    case 'k' -> portals.get("DarkGold").add(new Portal(x, y));
                    case 'l' -> portals.get("DarkGray").add(new Portal(x, y));
                    default -> {}
                }
            }
        }
        System.out.print("Gold: ");
        for (Node gold: goldLocations) {
            System.out.print(gold + " ");
        }
        System.out.println();
        System.out.print("Pills: ");
        for (Node pill: pillLocations) {
            System.out.print(pill + " ");
        }
        System.out.println();
    }

    public boolean checkNumberPacs(){
        return pacLocations.size() == 1;
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
        for(Portal portal : linkedPortals) {
            Location portalLocation = new Location(portal.getNode().x, portal.getNode().y);
            Portal connectedPortal = portal.getConnectedPortal();
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
        if (inaccessible_gold.size() > 0) {
            List<String> inaccessible_locations = inaccessible_gold.stream().
                    map(Object::toString).
                    collect(Collectors.toList());
            System.out.println("Gold not accessible: " + String.join("; ", inaccessible_locations));
        }
        if (inaccessible_pills.size() > 0) {
            List<String> inaccessible_locations = inaccessible_pills.stream().
                    map(Object::toString).
                    collect(Collectors.toList());
            System.out.println("Pill not accessible: " + String.join("; ", inaccessible_locations));
        }
        return inaccessible_pills.size() == 0 && inaccessible_gold.size() == 0;
    }

    public boolean checkLevel(){
        return checkPortals() && checkNumberPacs() && checkMinPillsAndGold() && checkAllPillsAndGold();
    }

    public boolean checkMinPillsAndGold(){
        if (goldLocations.size() + pillLocations.size() < 2) {
            System.out.println("less than 2 Gold and Pill");
            return false;
        }
        return true;
    }
}
