public class Brick {
    private BrickState brickState;

    public Brick(BrickState brickState){
        this.brickState = brickState;
    }

    public BrickState getBrickState() {
        return brickState;
    }

    public void setBrickState(BrickState brickState) {
        this.brickState = brickState;
    }
}
