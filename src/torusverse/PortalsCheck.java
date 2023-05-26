package torusverse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PortalsCheck implements LevelCheck {
    private final HashMap<String, List<Portal>> portals;
    private final String filename;
    public PortalsCheck(HashMap<String, List<Portal>> portals, String filename){
        this.portals = portals;
        this.filename = filename;
    }
    @Override
    public boolean doCheck() {
        boolean validPortals = true;
        for (Map.Entry<String, List<Portal>> entry: portals.entrySet()){
            List<Portal> portals = entry.getValue();
            if (portals.size() != 2) {
                if (portals.size() != 0) {
                    List<String> portalLocations = portals.stream().map(Portal::getNode)
                            .map(Object::toString)
                            .collect(Collectors.toList());
                    System.out.println("[Level " + filename + " - portal " + entry.getKey() + " count is not 2: " +
                            String.join("; ", portalLocations) + "]");
                    validPortals = false;
                }
            }
        }
        return validPortals;
    }
}
