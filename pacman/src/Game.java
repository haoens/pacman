// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;
import torusverse.LevelChecker;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid;

  protected PacActor pacActor = new PacActor(this);
  private Monster troll = new Monster(this, MonsterType.Troll);
  private Monster tx5 = new Monster(this, MonsterType.TX5);
  private ArrayList<Location> pillAndItemLocations = new ArrayList<>();
  private ArrayList<Actor> iceCubes = new ArrayList<>();
  private ArrayList<Actor> goldPieces = new ArrayList<>();
  private ArrayList<Portal> portals = new ArrayList<>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private ArrayList<Location> propertyGoldLocations = new ArrayList<>();
  private int level = 0;
  private boolean hasMonsters = false;

  // added attributes
  private boolean failedChecking = false;

  public Game(GameCallback gameCallback, Properties properties, String filepath)
  {

    //Setup game
    super(nbHorzCells, nbVertCells, 20, false);
    this.gameCallback = gameCallback;
    this.properties = properties;
    this.grid = new PacManGameGrid(nbHorzCells, nbVertCells, filepath);
    setSimulationPeriod(100);
    setTitle("[PacMan in the Multiverse]");
    String title = "";
    addKeyRepeatListener(pacActor);

    while(true) {
      //Do Level Check before test
      LevelChecker levelChecker = new LevelChecker(grid.getNthMazeArray(level));
      if (!levelChecker.checkLevel()){
        failedChecking = true;
        System.out.println("Failed");
        break;
      }

      //Setup for auto test
      pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
      seed = Integer.parseInt(properties.getProperty("seed"));

      GGBackground bg = getBg();
      drawGrid(bg);

      //Setup Random seeds
      seed = Integer.parseInt(properties.getProperty("seed"));
      pacActor.setSeed(seed);
      troll.setSeed(seed);
      tx5.setSeed(seed);

      setKeyRepeatPeriod(150);
      troll.setSlowDown(3);
      tx5.setSlowDown(3);
      pacActor.setSlowDown(3);
      tx5.stopMoving(5);
      setupActorLocations();

      //Run the game
      doRun();
      show();
      // Loop to look for collision in the application thread
      // This makes it improbable that we miss a hit
      boolean hasPacmanBeenHit;
      boolean hasPacmanEatAllPills;
      setupPillAndItemsLocations();
      int maxPillsAndItems = countPillsAndItems();

      do {
        if (hasMonsters) {
          hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
                  tx5.getLocation().equals(pacActor.getLocation());
        } else {
          hasPacmanBeenHit = false;
        }
        hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
        delay(10);
      } while (!hasPacmanBeenHit && !hasPacmanEatAllPills);
      delay(120);

      Location loc = pacActor.getLocation();
      troll.setStopMoving(true);
      tx5.setStopMoving(true);
      pacActor.removeSelf();
      troll.removeSelf();
      tx5.removeSelf();

      if (hasPacmanBeenHit) {
        bg.setPaintColor(Color.red);
        title = "GAME OVER";
        addActor(new Actor("sprites/explosion3.gif"), loc);
        break;
      }
      else if (hasPacmanEatAllPills && lastLevel()) {
        bg.setPaintColor(Color.yellow);
        title = "YOU WIN";
        break;
      }
      else {
        level++;
        pacActor.resetNbPills();
        hasMonsters = false;
      }
    }
    setTitle(title);
    gameCallback.endOfGame(title);
    getFrame().dispose();
    doPause();
  }

  public GameCallback getGameCallback() {
    return gameCallback;
  }

  private void setupActorLocations() {
    for (int y = 0; y < nbVertCells; y++) {
      for (int x = 0; x < nbHorzCells; x++) {
        Location location = new Location(x, y);
        if (grid.getCell(location, getCurrentLevel()) == 5) {
          addActor(pacActor, location);
        }
        if (grid.getCell(location, getCurrentLevel()) == 6) {
          addActor(troll, location, Location.NORTH);
          hasMonsters = true;
        }
        if (grid.getCell(location, getCurrentLevel()) == 7) {
          addActor(tx5, location, Location.NORTH);
          hasMonsters = true;
        }
      }
    }
  }

  private int countPillsAndItems() {
    int pillsAndItemsCount = 0;
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location, getCurrentLevel());
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          pillsAndItemsCount++;
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          pillsAndItemsCount++;
        }
      }
    }
    if (propertyPillLocations.size() != 0) {
      pillsAndItemsCount += propertyPillLocations.size();
    }

    if (propertyGoldLocations.size() != 0) {
      pillsAndItemsCount += propertyGoldLocations.size();
    }

    return pillsAndItemsCount;
  }

  public ArrayList<Location> getPillAndItemLocations() {
    return pillAndItemLocations;
  }

  private void setupPillAndItemsLocations() {
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        Location location = new Location(x, y);
        int a = grid.getCell(location, getCurrentLevel());
        if (a == 1 && propertyPillLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 3 &&  propertyGoldLocations.size() == 0) {
          pillAndItemLocations.add(location);
        }
        if (a == 4) {
          pillAndItemLocations.add(location);
        }
      }
    }

    if (propertyPillLocations.size() > 0) {
      for (Location location : propertyPillLocations) {
        pillAndItemLocations.add(location);
      }
    }
    if (propertyGoldLocations.size() > 0) {
      for (Location location : propertyGoldLocations) {
        pillAndItemLocations.add(location);
      }
    }
  }

  private void drawGrid(GGBackground bg)
  {
    bg.clear(Color.gray);
    bg.setPaintColor(Color.white);
    for (int y = 0; y < nbVertCells; y++)
    {
      for (int x = 0; x < nbHorzCells; x++)
      {
        bg.setPaintColor(Color.white);
        Location location = new Location(x, y);
        int a = grid.getCell(location, getCurrentLevel());
        if (a > 0)
          bg.fillCell(location, Color.lightGray);
        if (a == 1 && propertyPillLocations.size() == 0) { // Pill
          putPill(bg, location);
        } else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
          putGold(bg, location);
        } else if (a == 4) {
          putIce(bg, location);
        } else if (a >= 8 && a <= 11) {
          putPortal(location, a);
        }
      }
    }

    for (Location location : propertyPillLocations) {
      putPill(bg, location);
    }
    for (Location location : propertyGoldLocations) {
      putGold(bg, location);
    }
  }

  private void putPill(GGBackground bg, Location location){
    bg.fillCircle(toPoint(location), 5);
  }

  private void putGold(GGBackground bg, Location location){
    bg.setPaintColor(Color.yellow);
    bg.fillCircle(toPoint(location), 5);
    Actor gold = new Actor("sprites/gold.png");
    this.goldPieces.add(gold);
    addActor(gold, location);
  }

  private void putIce(GGBackground bg, Location location){
    bg.setPaintColor(Color.blue);
    bg.fillCircle(toPoint(location), 5);
    Actor ice = new Actor("sprites/ice.png");
    this.iceCubes.add(ice);
    addActor(ice, location);
  }

  private void putPortal(Location location, int index) {
    // Need to set paired portal when possible
    Portal portal = null;
    switch (index) {
      case 8 -> {
        portal = new Portal("sprites/portalDarkGoldTile.png", index);
        checkPortalPair(portal, index);
      }
      case 9 -> {
        portal = new Portal("sprites/portalDarkGrayTile.png", index);
        checkPortalPair(portal, index);
      }
      case 10 -> {
        portal = new Portal("sprites/portalWhiteTile.png", index);
        checkPortalPair(portal, index);
      }
      case 11 -> {
        portal = new Portal("sprites/portalYellowTile.png", index);
        checkPortalPair(portal, index);
      }
    }
    this.portals.add(portal);
    addActor(portal, location);
  }

  private void checkPortalPair(Portal portal, int index) {
    for(Portal otherPortal : portals) {
      if (portal.getIndex() == index) {
        portal.setPairedPortal(otherPortal);
        otherPortal.setPairedPortal(portal);
      }
    }
  }

  public void removeItem(String type,Location location){
    if(type.equals("gold")){
      for (Actor item : this.goldPieces){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
    else if(type.equals("ice")){
      for (Actor item : this.iceCubes){
        if (location.getX() == item.getLocation().getX() && location.getY() == item.getLocation().getY()) {
          item.hide();
        }
      }
    }
  }

  public int getNumHorzCells(){
    return this.nbHorzCells;
  }
  public int getNumVertCells(){
    return this.nbVertCells;
  }
  public ArrayList<Portal> getPortals() {
    return this.portals;
  }
  public int getCurrentLevel() {
    if (level < grid.getMazeArraySize()) {
      return level;
    }
    return -1;
  }
  public boolean lastLevel() {
    return getCurrentLevel() == grid.getMazeArraySize()-1;
  }

  public PacManGameGrid getGrid() { return grid; }
  public boolean hasFailedChecking() { return failedChecking; }
}
