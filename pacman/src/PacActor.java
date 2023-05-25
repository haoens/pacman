// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import src.pathfinding.Grid;
import src.pathfinding.PathFinding;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;
  private Grid grid;
  private ArrayList<List<Location>> itemPaths = new ArrayList<>();
  private List<Location> itemLocations;
  private List<Location> currentPath;
  private int seed;
  private boolean isAuto = false;
  private Random randomiser = new Random();
  public PacActor(Game game)
  {
    super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
    this.game = game;
  }

  public void setAuto(boolean auto) {
    isAuto = auto;
  }

  public void setSeed(int seed) {
    this.seed = seed;
    randomiser.setSeed(seed);
  }

  public void keyRepeated(int keyCode)
  {
    if (isAuto) {
      return;
    }
    if (isRemoved())  // Already removed
      return;
    Location next = null;
    switch (keyCode) {
      case KeyEvent.VK_LEFT -> {
        next = getLocation().getNeighbourLocation(Location.WEST);
        setDirection(Location.WEST);
      }
      case KeyEvent.VK_UP -> {
        next = getLocation().getNeighbourLocation(Location.NORTH);
        setDirection(Location.NORTH);
      }
      case KeyEvent.VK_RIGHT -> {
        next = getLocation().getNeighbourLocation(Location.EAST);
        setDirection(Location.EAST);
      }
      case KeyEvent.VK_DOWN -> {
        next = getLocation().getNeighbourLocation(Location.SOUTH);
        setDirection(Location.SOUTH);
      }
    }
    if (next != null && canMove(next))
    {
      int cellID = game.grid.getCell(next, game.getCurrentLevel());
      if(8 <= cellID && cellID <= 11) {
        for (Portal portal : game.getPortals()) {
          if (portal.getLocation().equals(next)) {
            next = portal.getPairedPortal().getLocation();
            break;
          }
        }
      }
      setLocation(next);
      eatPill(next);
    }
  }

  public void act()
  {
    show(idSprite);
    idSprite++;
    if (idSprite == nbSprites)
      idSprite = 0;

    if (isAuto) {
      moveInAutoMode();
    }
    this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
  }

  private void moveInAutoMode() {
    if(itemPaths.isEmpty()) {
      for(Location itemLocation : itemLocations) {
        if(game.grid.getCell(itemLocation, game.getCurrentLevel()) == 4) {
          continue;
        }
        if(itemLocation.equals(this.getLocation())) {
          continue;
        }
        if((itemLocation.x == this.getLocation().getX() || itemLocation.y == this.getLocation().getY())
            && itemLocation.getDistanceTo(this.getLocation()) == 1){
          List<Location> itemPath = new ArrayList<>();
          itemPath.add(itemLocation);
          itemPaths.add(itemPath);
          break;
        }
        ArrayList<Portal> portals = game.getPortals();
        HashMap<Location, Location> portalLocations = new HashMap<>();
        for(Portal portal : portals){
          portalLocations.put(portal.getLocation(), portal.getPairedPortal().getLocation());
        }
        itemPaths.add(PathFinding.findPath(grid, this.getLocation(), itemLocation, false, portalLocations));
      }
      int shortestPath = 9000;
      for(List<Location> itemPath : itemPaths) {
        if(itemPath.size() < shortestPath){
          currentPath = itemPath;
          shortestPath = itemPath.size();
        }
      }
    }
    Location next = currentPath.remove(0);
    setDirection(this.getLocation().get4CompassDirectionTo(next));
    setLocation(next);
    eatPill(next);
    if(currentPath.size() == 0) {
      itemPaths.clear();
      for(Location itemLocation : itemLocations){
        if(itemLocation.equals(next)) {
          itemLocations.remove(itemLocation);
          break;
        }
      }
    }
  }

  private boolean canMove(Location location)
  {
    Color c = getBackground().getColor(location);
    return !c.equals(Color.gray) && location.getX() < game.getNumHorzCells()
            && location.getX() >= 0 && location.getY() < game.getNumVertCells() && location.getY() >= 0;
  }

  public int getNbPills() {
    return nbPills;
  }
  public void resetNbPills() {this.nbPills = 0;}

  private void eatPill(Location location)
  {
    Color c = getBackground().getColor(location);
    if (c.equals(Color.white))
    {
      nbPills++;
      score++;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "pills");
    } else if (c.equals(Color.yellow)) {
      nbPills++;
      score+= 5;
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "gold");
      game.removeItem("gold",location);
    } else if (c.equals(Color.blue)) {
      getBackground().fillCell(location, Color.lightGray);
      game.getGameCallback().pacManEatPillsAndItems(location, "ice");
      game.removeItem("ice",location);
    }
    String title = "[PacMan in the Multiverse] Current score: " + score;
    gameGrid.setTitle(title);
  }

  public void setGrid(){
    int gridWidth = game.getNumHorzCells();
    int gridHeight = game.getNumVertCells();
    boolean[][] walkableTiles = new boolean[gridWidth][gridHeight];
    for(int i = 0; i < gridWidth; i++) {
      for(int j = 0; j < gridHeight; j ++) {
        if(game.getGrid().getCell(new Location(i, j), game.getCurrentLevel()) > 0) {
          walkableTiles[i][j] = true;
        }
      }
    }
    this.grid = new Grid(gridWidth, gridHeight, walkableTiles);
  }
  public void getItemLocations() {
    this.itemLocations = game.getPillAndItemLocations();
  }
}


