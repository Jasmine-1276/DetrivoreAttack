import java.util.ArrayList;

public class alien3 extends Entity{
    private Game game;
    private int hp = 10;
    private boolean dead = false;
    private long spinTime = 0;
    private long splatTime = 0;
    private boolean spin = false;
    private ArrayList<splat> splats = new ArrayList<>();
    private Sprite l1 = (SpriteStore.get()).getSprite("sprites/alien3/left1.png");
    private Sprite l2 = (SpriteStore.get()).getSprite("sprites/alien3/left2.png");
    private Sprite r1 = (SpriteStore.get()).getSprite("sprites/alien3/right1.png");
    private Sprite r2 = (SpriteStore.get()).getSprite("sprites/alien3/right2.png");
    public alien3(Game g, int newX, int newY, int healthmult){
        super("sprites/alien3/left1.png", newX, newY);  // calls the constructor in Entity
        this.hp *= ((healthmult*healthmult)/20) + 0.95;
        game = g;
    }

    public void move (long delta){
        int[] temp = game.getShipXY();
        double tx = temp[0];
        double ty = temp[1];

        double vx = tx - (this.x + 10);
        double vy = ty - (this.y + 10);

        double len = Math.sqrt(vx*vx + vy*vy);

        this.dx = 0;
        this.dy = 0;

        if (len != 0) {
            vx /= len;
            vy /= len;
        }

        double speed = 150;
        this.dx = vx * speed;
        this.dy = vy * speed;

        splatTime += delta;
        if (splatTime > 100000000){
            splat s = splat.getInstance((int)this.x, (int)this.y, game, this);
            splats.add(s);
            game.splat(s);
            splatTime = 0;
        }

        spinTime += delta;
        if (spinTime > 250000000){
            spin = (spin != true);
            spinTime = 0;
            if (spin){
                if (this.dx > 0){
                setSprite(r1);
                } else {
                setSprite(l1);}
            } else {
                if (this.dx > 0){
                setSprite(r2);
                } else {
                setSprite(l2);
            }
        }
    }

        super.move(delta);

    }

    @Override
    public void collidedWith(Entity other) {
    if (other instanceof Tile tile){
      game.removeEntity(tile);
    } 
    if (other instanceof ShotEntity shot){
        if (shot.getType() == 0){
        game.removeEntity(shot);
        hp -= 1+ game.getcombo()/2;}
        if (hp < 1 && !dead){
        game.death((int)this.x, (int)this.y, (int)other.getHorizontalMovement(), (int)other.getVerticalMovement(), "sprites/dead3.png", false);
        dead = true;
        game.alienDead();
        game.removeEntity(this);
    }
    }
    }
    
}
