// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.pathfinding.Grid;
import src.utility.GameCallback;
import src.torusverse.BaseTorusCheck;
import src.torusverse.PacManGameGridAdapter;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class Game extends GameGrid
{
  private final static int nbHorzCells = 20;
  private final static int nbVertCells = 11;
  protected PacManGameGrid grid;
  private Grid pathFinderGrid;

  protected PacActor pacActor = new PacActor(this);

  private ArrayList<Monster> trollArray = new ArrayList<>();

  private ArrayList<Monster> tx5Array = new ArrayList<>();
  private ArrayList<Location> pillAndItemLocations = new ArrayList<>();
  private ArrayList<Actor> iceCubes = new ArrayList<>();
  private ArrayList<Actor> goldPieces = new ArrayList<>();
  private ArrayList<GamePortal> portals = new ArrayList<>();
  private GameCallback gameCallback;
  private Properties properties;
  private int seed = 30006;
  private ArrayList<Location> propertyPillLocations = new ArrayList<>();
  private ArrayList<Location> propertyGoldLocations = new ArrayList<>();

  // added attributes
  private boolean failedChecking = false;
  private int level = 0;
  private boolean hasMonsters = false;
  private int tx5Index = 0;
  private int trollIndex = 0;

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
      // Do Gamecheck before test
      if (grid.getFailedGameCheck()) {
        break;
      }
      //Do Level Check before test
      if (!doLevelCheck(filepath)){
        failedChecking = true;
        break;
      }
      //Setup for auto test
      pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
      seed = Integer.parseInt(properties.getProperty("seed"));
      GGBackground bg = getBg();
      drawGrid(bg);
      this.setGrid();

      // Initialize the needed amount of tx5 and Troll.
      for (int i = 0; i < grid.getNumTroll(level); i++) {
        trollArray.add(new Monster(this, MonsterType.Troll));
      }
      for (int i = 0; i < grid.getNumTx5(level); i++) {
        tx5Array.add(new Monster(this, MonsterType.TX5));
      }

      //Setup Random seeds
      seed = Integer.parseInt(properties.getProperty("seed"));
      pacActor.setSeed(seed);
      for (Monster value : trollArray) {
        value.setSeed(seed);
        value.setSlowDown(3);
      }
      for (Monster monster : tx5Array) {
        monster.setSeed(seed);
        monster.setSlowDown(3);
        monster.stopMoving(5);
      }

      setKeyRepeatPeriod(150);
      pacActor.setSlowDown(3);
      setupActorLocations();

      //Run the game
      doRun();
      show();
      // Loop to look for collision in the application thread
      // This makes it improbable that we miss a hit
      boolean hasPacmanBeenHit = false;
      boolean hasPacmanEatAllPills;
      setupPillAndItemsLocations();
      int maxPillsAndItems = countPillsAndItems();
      ClosestPillStrategy closestPillStrategy = new ClosestPillStrategy();
      pacActor.setAutoPlayStrategy(closestPillStrategy);
      pacActor.getItemLocations();

      do {
        if (hasMonsters) {
          for (Monster monster : trollArray) {
            hasPacmanBeenHit = monster.getLocation().equals(pacActor.getLocation());
            if (hasPacmanBeenHit) {
              break;
            }
          }
          if (!hasPacmanBeenHit) {
            for (Monster monster : tx5Array) {
              hasPacmanBeenHit = monster.getLocation().equals(pacActor.getLocation());
              if (hasPacmanBeenHit) {
                break;
              }
            }
          }
        } else {
          hasPacmanBeenHit = false;
        }
        hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
        delay(10);
      } while (!hasPacmanBeenHit && !hasPacmanEatAllPills);
      delay(120);

      Location loc = pacActor.getLocation();

      for (Monster value : trollArray) {
        value.setStopMoving(true);
        value.removeSelf();
      }

      for (Monster monster : tx5Array) {
        monster.setStopMoving(true);
        monster.removeSelf();
      }

      pacActor.removeSelf();
      if (hasPacmanBeenHit) {
        bg.setPaintColor(Color.red);
        title = "GAME OVER";
        addActor(new Actor("sprites/explosion3.gif"), loc);
        break;
      }
      else if (lastLevel()) {
        bg.setPaintColor(Color.yellow);
        title = "YOU WIN";
        break;
      }
      else {
        // Go to the next level in game folder.
        level++;
        pacActor.resetNbPills();
        trollIndex = 0;
        trollArray.clear();
        tx5Index= 0;
        tx5Array.clear();
        hasMonsters = false;
        for(Actor ice : iceCubes) {
          ice.removeSelf();
        }
        for(GamePortal portal : portals) {
          portal.removeSelf();
        }
        portals.clear();
        pillAndItemLocations.clear();
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
          addActor(trollArray.get(trollIndex), location, Location.NORTH);
          trollIndex++;
          hasMonsters = true;
        }
        if (grid.getCell(location, getCurrentLevel()) == 7) {
          addActor(tx5Array.get(tx5Index), location, Location.NORTH);
          tx5Index++;
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
      pillAndItemLocations.addAll(propertyPillLocations);
    }
    if (propertyGoldLocations.size() > 0) {
      pillAndItemLocations.addAll(propertyGoldLocations);
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
    GamePortal portal = null;
    switch (index) {
      case 8 -> {
        portal = new GamePortal("sprites/portalDarkGoldTile.png", index);
        checkPortalPair(portal, index);
      }
      case 9 -> {
        portal = new GamePortal("sprites/portalDarkGrayTile.png", index);
        checkPortalPair(portal, index);
      }
      case 10 -> {
        portal = new GamePortal("sprites/portalWhiteTile.png", index);
        checkPortalPair(portal, index);
      }
      case 11 -> {
        portal = new GamePortal("sprites/portalYellowTile.png", index);
        checkPortalPair(portal, index);
      }
    }
    this.portals.add(portal);
    addActor(portal, location);
  }

  private void checkPortalPair(GamePortal portal, int index) {
    for(GamePortal otherPortal : portals) {
      if (otherPortal.getIndex() == index) {
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
    return nbHorzCells;
  }
  public int getNumVertCells(){
    return nbVertCells;
  }
  public ArrayList<GamePortal> getPortals() {
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

  private boolean doLevelCheck(String filepath) {
    File file = new File(filepath);
    PacManGameGridAdapter adapter = new PacManGameGridAdapter(this.grid, this.level);
    BaseTorusCheck baseTorusCheck;
    if (file.isDirectory()) {
      File targetFile = new File(grid.getGameFolderController().getNthFileSorted(file, this.level));
      baseTorusCheck = new BaseTorusCheck(adapter, targetFile.getName());
    }
    else {
      baseTorusCheck = new BaseTorusCheck(adapter, file.getName());
    }
    return baseTorusCheck.doCheck();
  }

  public void setGrid(){
    int gridWidth = nbHorzCells;
    int gridHeight = nbVertCells;
    boolean[][] walkableTiles = new boolean[gridWidth][gridHeight];
    for(int i = 0; i < gridWidth; i++) {
      for(int j = 0; j < gridHeight; j ++) {
        if(grid.getCell(new Location(i, j), level) > 0) {
          walkableTiles[i][j] = true;
        }
      }
    }
    this.pathFinderGrid = new Grid(gridWidth, gridHeight, walkableTiles);
  }

  public Grid getPathFinderGrid(){
    return pathFinderGrid;
  }
}
