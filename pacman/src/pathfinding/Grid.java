package src.pathfinding;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * The grid of nodes we use to find path
 */
public class Grid {
    public Node[][] nodes;
    private int gridWidth, gridHeight;

    /**
     * Modified from https://github.com/patrykkrawczyk/2D-A-path-finding-in-Java/
     * Create a new Grid with tile prices.
     *
     * @param width      Grid width
     * @param height     Grid height
     * @param tile_costs 2d array of floats, representing the cost of every tile.
     *                   0.0f = unwalkable tile.
     *                   1.0f = normal tile.
     */
    public Grid(int width, int height, float[][] tile_costs) {
        gridWidth = width;
        gridHeight = height;
        nodes = new Node[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new Node(x, y, tile_costs[x][y]);
            }
            System.out.println();
        }
    }

    /**
     * Create a new grid of just walkable / unwalkable tiles.
     *
     * @param width         Grid width
     * @param height        Grid height
     * @param walkableTiles the tilemap. true for walkable, false for blocking.
     */
    public Grid(int width, int height, boolean[][] walkableTiles) {
        gridWidth = width;
        gridHeight = height;
        nodes = new Node[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new Node(x, y, walkableTiles[x][y] ? 1.0f : 0.0f);
            }
        }
    }

    public List<Node> get8Neighbours(Node node) {
        List<Node> neighbours = new ArrayList<Node>();

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;

                int checkX = node.x + x;
                int checkY = node.y + y;

                if (checkX >= 0 && checkX < gridWidth && checkY >= 0 && checkY < gridHeight)
                    neighbours.add(nodes[checkX][checkY]);
            }

        return neighbours;
    }

    public List<Node> get4Neighbours(Node node, HashMap<Location, Location> portals) {
        List<Node> neighbours = new ArrayList<>();
        boolean addedPortal = false;

        if (node.y + 1 >= 0 && node.y + 1  < gridHeight) {
            Location nodeLoc = new Location(node.x, node.y + 1);
            for(Map.Entry<Location, Location> set : portals.entrySet()) {
                if(set.getKey().equals((nodeLoc))) {
                    neighbours.add(nodes[set.getValue().getX()][set.getValue().getY()]);
                    addedPortal = true;
                    break;
                }
            }
            if(!addedPortal) {
                neighbours.add(nodes[node.x][node.y + 1]); // N
            }
            addedPortal = false;
        }
        if (node.y - 1 >= 0 && node.y - 1  < gridHeight) {
            Location nodeLoc = new Location(node.x, node.y - 1);
            for(Map.Entry<Location, Location> set : portals.entrySet()) {
                if(set.getKey().equals((nodeLoc))) {
                    neighbours.add(nodes[set.getValue().getX()][set.getValue().getY()]);
                    addedPortal = true;
                    break;
                }
            }
            if(!addedPortal) {
                neighbours.add(nodes[node.x][node.y - 1]); // S
            }
            addedPortal = false;
        }
        if (node.x + 1 >= 0 && node.x + 1  < gridWidth) {
            Location nodeLoc = new Location(node.x + 1, node.y);
            for(Map.Entry<Location, Location> set : portals.entrySet()) {
                if(set.getKey().equals((nodeLoc))) {
                    neighbours.add(nodes[set.getValue().getX()][set.getValue().getY()]);
                    addedPortal = true;
                    break;
                }
            }
            if(!addedPortal) {
                neighbours.add(nodes[node.x + 1][node.y]); // E
            }
            addedPortal = false;
        }
        if (node.x - 1 >= 0 && node.x - 1  < gridWidth) {
            Location nodeLoc = new Location(node.x - 1, node.y);
            for(Map.Entry<Location, Location> set : portals.entrySet()) {
                if(set.getKey().equals((nodeLoc))) {
                    neighbours.add(nodes[set.getValue().getX()][set.getValue().getY()]);
                    addedPortal = true;
                    break;
                }
            }
            if(!addedPortal) {
                neighbours.add(nodes[node.x - 1][node.y]); // W
            }
        }

        return neighbours;
    }
}