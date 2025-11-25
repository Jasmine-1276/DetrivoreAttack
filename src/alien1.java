public class alien1 extends Entity{
    private Game game;
    private boolean dead = false;
    private int hp = 5;
    private int speedmult;
    private long spintime = 0;
    private boolean spin = false;
    private Sprite a = (SpriteStore.get()).getSprite("sprites/alien1/alien1a.png");
    private Sprite b = (SpriteStore.get()).getSprite("sprites/alien1/alien1b.png");
    public alien1(Game g, int newX, int newY, int healthmult){
        super("sprites/alien1/alien1a.png", newX, newY);  // calls the constructor in Entity
        this.hp *= ((healthmult*healthmult)/10) + 0.9;
        speedmult = (healthmult/100) + 1;
        game = g;
    }

public void move (long delta){
    int[] temp = game.getShipXY();
    double tx = temp[0];
    double ty = temp[1];

    double vx = tx - (this.x + 15);
    double vy = ty - (this.y + 15);

    double len = Math.sqrt(vx*vx + vy*vy);

    if (len != 0) {
        vx /= len;
        vy /= len;
    }

    spintime += delta/1000000;
    if (spintime > 500){
        spin = (spin != true);
        spintime = 0;
        if (spin){
            setSprite(a);
        } else {
            setSprite(b);
        }
    }

    double speed = 100;
    this.dx = vx * speed * speedmult;
    this.dy = vy * speed * speedmult;

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
        if (shot.getType() == 1){
        game.removeEntity(shot);
        hp--;
        }
        if (hp < 1 && !dead){
        game.death((int)this.x, (int)this.y, (int)other.getHorizontalMovement(), (int)other.getVerticalMovement(), "sprites/dead1.png", true);
        dead = true;
        game.alienDead();
        game.removeEntity(this);
    }
    }
    
    }
    
}


