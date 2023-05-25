package src.pathfinding;

import ch.aplu.jgamegrid.Location;
import src.Game;
import src.Portal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Modified from https://github.com/patrykkrawczyk/2D-A-path-finding-in-Java/
 * Class used to find the best path from A to B.
 */
public class PathFinding {
    private Game game;
    /**
     * Method you should use to get path allowing 4 directional movement
     *
     * @param grid           grid to search in.
     * @param startPos       starting position.
     * @param targetPos      ending position.
     * @param allowDiagonals Pass true if you want 8 directional pathfinding, false for 4 direcitonal
     * @param portals
     * @return
     */
    public static List<Location> findPath(Grid grid, Location startPos, Location targetPos, boolean allowDiagonals, ArrayList<Portal> portals) {
        // Find path
        List<Node> pathInNodes = findPathNodes(grid, startPos, targetPos, allowDiagonals, portals);

        // Convert to a list of points and return
        List<Location> pathInPoints = new ArrayList<>();

        if (pathInNodes != null) {
            for (Node node : pathInNodes)
                pathInPoints.add(new Location(node.x, node.y));
        }

        return pathInPoints;
    }

    /**
     * Helper method for findPath8Directions()
     *
     * @param grid           Gird instance containing information about tiles
     * @param startPos       Starting position.
     * @param targetPos      Targeted position.
     * @param allowDiagonals Pass true if you want 8 directional pathfinding, false for 4 direcitonal
     * @param portals
     * @return List of Node's with found path.
     */
    private static List<Node> findPathNodes(Grid grid, Location startPos, Location targetPos, boolean allowDiagonals, ArrayList<Portal> portals) {
        Node startNode = grid.nodes[startPos.x][startPos.y];
        Node targetNode = grid.nodes[targetPos.x][targetPos.y];

        List<Node> openSet = new ArrayList<>();
        HashSet<Node> closedSet = new HashSet<>();
        openSet.add(startNode);

        while (openSet.size() > 0) {
            Node currentNode = openSet.get(0);

            for (int k = 1; k < openSet.size(); k++) {
                Node open = openSet.get(k);

                if (open.getFCost() < currentNode.getFCost() ||
                        open.getFCost() == currentNode.getFCost() &&
                                open.hCost < currentNode.hCost)
                    currentNode = open;
            }

            openSet.remove(currentNode);
            closedSet.add(currentNode);

            if (currentNode == targetNode) {
                return retracePath(startNode, targetNode);
            }

            List<Node> neighbours;
            if (allowDiagonals) neighbours = grid.get8Neighbours(currentNode);
            else neighbours = grid.get4Neighbours(currentNode, portals);

            for (Node neighbour : neighbours) {
                if (!neighbour.walkable || closedSet.contains(neighbour)) continue;
                int newMovementCostToNeighbour = currentNode.gCost + 1;
                if (newMovementCostToNeighbour < neighbour.gCost || !openSet.contains(neighbour)) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, targetNode);
                    neighbour.parent = currentNode;

                    if (!openSet.contains(neighbour)) openSet.add(neighbour);
                }
            }
        }
        return null;
    }

    private static List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private static int getDistance(Node nodeA, Node nodeB) {
        int distanceX = Math.abs(nodeA.x - nodeB.x);
        int distanceY = Math.abs(nodeA.y - nodeB.y);

        return distanceX + distanceY;
    }
}
