package src;

import ch.aplu.jgamegrid.Location;
import src.pathfinding.PathFinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClosestPillStrategy implements AutoPlayStrategy{
    private ArrayList<List<Location>> itemPaths = new ArrayList<>();
    private List<Location> currentPath;
    public ClosestPillStrategy() {
    }
    @Override
    public List<Location> getPath(Game game) {
        Location startPos = game.pacActor.getLocation();
        List<Location> itemLocations = game.getPillAndItemLocations();
        for (Location itemLocation : itemLocations) {
            if (game.grid.getCell(itemLocation, game.getCurrentLevel()) == 4) {
                continue;
            }
            if (itemLocation.equals(startPos)) {
                continue;
            }
            if ((itemLocation.x == startPos.getX() || itemLocation.y == startPos.getY())
                    && itemLocation.getDistanceTo(startPos) == 1) {
                List<Location> itemPath = new ArrayList<>();
                itemPath.add(itemLocation);
                itemPaths.add(itemPath);
                break;
            }
            ArrayList<GamePortal> portals = game.getPortals();
            HashMap<Location, Location> portalLocations = new HashMap<>();
            for (GamePortal portal : portals) {
                portalLocations.put(portal.getLocation(), portal.getPairedPortal().getLocation());
            }
            itemPaths.add(PathFinding.findPath(game.getPathFinderGrid(), startPos, itemLocation, false, portalLocations));
        }
        int shortestPath = 9000;
        for (List<Location> itemPath : itemPaths) {
            if (itemPath.size() < shortestPath) {
                currentPath = itemPath;
                shortestPath = itemPath.size();
            }
        }
        return currentPath;
    }
}
