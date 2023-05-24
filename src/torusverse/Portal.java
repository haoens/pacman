package torusverse;

public class Portal {
    private Point point;
    private Portal connectedPortal;

    public Portal(int x, int y){
        this.point = new Point(x, y, null);
    }

    public Point getPoint(){
        return point;
    }

    public Portal getConnectedPortal(){
        return connectedPortal;
    }
    public void joinPortals(Portal targetPortal){
        this.connectedPortal = targetPortal;
        targetPortal.connectedPortal = this;
    }
}
