package torusverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Basic Path finding took from here https://gamedev.stackexchange.com/questions/197165/java-simple-2d-grid-pathfinding
public class LevelChecker {
    private final char[][] map;
    private final List<Point> goldLocations;
    private final List<Point> pillLocations;
    private final List<Point> pacLocations;

    private final HashMap<String, List<Portal>> portals;
    private final List<Portal> linkedPortals;

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
                    List<String> portalLocations = portals.stream().map(Portal::getPoint)
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

    public boolean IsWalkable( Point point) {
        if (point.y < 0 || point.y > map.length - 1) return false;
        if (point.x < 0 || point.x > map[0].length - 1) return false;
        return map[point.y][point.x] != 'b';
    }

    public boolean pointOutOfBounds(Point point){
        if (map.length == 0) return false;

        return point.y >= map.length || point.y < 0 || point.x < 0 || point.x >= map[0].length;
    }

    public List<Point> FindNeighbors(Point point) {
        List<Point> neighbors = new ArrayList<>();
        Point up = point.offset(0,  1);
        if (pointOutOfBounds(up)) up.y = 0;
        Point down = point.offset(0,  -1);
        if (pointOutOfBounds(down)) down.y = map.length - 1;
        Point left = point.offset(-1, 0);
        if (pointOutOfBounds(left)) left.x = map[0].length - 1;
        Point right = point.offset(1, 0);
        if (pointOutOfBounds(right)) right.x = 0;

//      Check if neighbour locations are portals
        for (Portal portal: linkedPortals) {
            Point connectedPortalPoint = portal.getConnectedPortal().getPoint();
            if (portal.getPoint().equals(up)) up = connectedPortalPoint;
            else if (portal.getPoint().equals(down)) down = connectedPortalPoint;
            else if (portal.getPoint().equals(left)) left = connectedPortalPoint;
            else if (portal.getPoint().equals(right)) right = connectedPortalPoint;
        }

        if (IsWalkable(up)) neighbors.add(up);
        if (IsWalkable(down)) neighbors.add(down);
        if (IsWalkable(left)) neighbors.add(left);
        if (IsWalkable(right)) neighbors.add(right);
        return neighbors;
    }

    public List<Point> findPath(Point start, Point end) {
        boolean finished = false;
        List<Point> used = new ArrayList<>();
        used.add(start);
        while (!finished) {
            List<Point> newOpen = new ArrayList<>();
            for(int i = 0; i < used.size(); ++i){
                Point point = used.get(i);
                for (Point neighbor : FindNeighbors(point)) {
                    if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
                        newOpen.add(neighbor);
                    }
                }
            }
            for(Point point : newOpen) {
                used.add(point);
                if (end.equals(point)) {
                    finished = true;
                    break;
                }
            }

            if (!finished && newOpen.isEmpty())
                return null;
        }

        List<Point> path = new ArrayList<>();
        Point point = used.get(used.size() - 1);
        while(point.previous != null) {
            path.add(0, point);
            point = point.previous;
        }
        return path;
    }

    public void parseMap(){
        if (map.length == 0) return;

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                char a = map[y][x];
                switch (a) {
                    case 'd' -> goldLocations.add(new Point(x, y, null));
                    case 'c' -> pillLocations.add(new Point(x, y, null));
                    case 'f' -> pacLocations.add(new Point(x, y, null));
                    case 'i' -> portals.get("White").add(new Portal(x, y));
                    case 'j' -> portals.get("Yellow").add(new Portal(x, y));
                    case 'k' -> portals.get("DarkGold").add(new Portal(x, y));
                    case 'l' -> portals.get("DarkGray").add(new Portal(x, y));
                    default -> {}
                }
            }
        }
        System.out.print("Gold: ");
        for (Point gold: goldLocations) {
            System.out.print(gold + " ");
        }
        System.out.println();
        System.out.print("Pills: ");
        for (Point pill: pillLocations) {
            System.out.print(pill + " ");
        }
        System.out.println();
    }

    public boolean checkNumberPacs(){
        return pacLocations.size() == 1;
    }
    public boolean checkAllPillsAndGold(){
        List<Point> inaccessible_pills = new ArrayList<>();
        Point pac_start = pacLocations.get(0);
        if (pac_start == null) return false;
        for (Point pill: pillLocations) {
            List<Point> path = findPath(pac_start, pill);
            if (path == null) { // no path exists
                inaccessible_pills.add(pill);
            }
        }
        List<Point> inaccessible_gold = new ArrayList<>();
        for (Point gold: goldLocations) {
            List<Point> path = findPath(pac_start, gold);
            if (path == null) { // no path exists
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
