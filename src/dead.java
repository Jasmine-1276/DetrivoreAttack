public class dead extends Entity{
    private Game g;
    private long lasttime = 0;
    private long bouncetime = 0;
    private long spawn = 0;
    private double weight;
    public dead(Game game, int x, int y, int msx, int msy, String r, double weight){
        super(r, x, y);
        this.dx = msx;
        this.dy = msy;
        this.spawn = System.currentTimeMillis();
        this.weight = weight;
        g = game;
    }

    public void move(long delta){
    if (lasttime - System.currentTimeMillis() < -200){
            this.dx /= weight;
            this.dy /= weight;
    lasttime = System.currentTimeMillis();}
        super.move(delta);

    if (System.currentTimeMillis() - spawn >= 2500){
        g.removeEntity(this);
    }}

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof Tile && this.weight > 1){
            if (bouncetime - System.currentTimeMillis() < -100){
            this.dx *= -0.7;
            this.dy *= -0.7;
            bouncetime = System.currentTimeMillis();}
        }
    }
}
