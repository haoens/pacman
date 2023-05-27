// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;

import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PacActor extends Actor implements GGKeyRepeatListener
{
  private static final int nbSprites = 4;
  private int idSprite = 0;
  private int nbPills = 0;
  private int score = 0;
  private Game game;
  private List<Location> itemLocations;
  private List<Location> currentPath;
  private int seed;
  private AutoPlayStrategy autoPlayStrategy;
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
        for (GamePortal portal : game.getPortals()) {
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
    if(currentPath == null || currentPath.size() == 0) {
      // Strategy would be determined based on game information for an extended autoplayer
      currentPath = autoPlayStrategy.getPath(game);
    }
    Location next = currentPath.remove(0);
    setDirection(this.getLocation().get4CompassDirectionTo(next));
    setLocation(next);
    eatPill(next);
    if(currentPath.size() == 0) {
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
  public void getItemLocations() {
    this.itemLocations = game.getPillAndItemLocations();
  }

  public void setAutoPlayStrategy(AutoPlayStrategy strategy) {
    this.autoPlayStrategy = strategy;
  }
}


