package src;

import ch.aplu.jgamegrid.Location;

import java.util.List;

public interface AutoPlayStrategy {
    List<Location> getPath(Game game);
}
