public class Tile extends Entity {
    private int size;

    public Tile(int x, int y, int size, String spritePath) {
        super(spritePath, x, y);
        this.size = size;
    }

    @Override
    public void collidedWith(Entity other) {
    }

}