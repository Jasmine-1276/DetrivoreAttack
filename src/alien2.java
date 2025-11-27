public class alien2 extends Entity{
    private Game game;
    private int hp = 2;
    private boolean dead = false;
    private long spintime = 0;
    private boolean spin = false;
    private Sprite a = (SpriteStore.get()).getSprite("sprites/alien2/alien2a.png");
    private Sprite b = (SpriteStore.get()).getSprite("sprites/alien2/alien2b.png");
    public alien2(Game g, int newX, int newY, int healthmult){
        super("sprites/alien2/alien2a.png", newX, newY);  // calls the constructor in Entity
        this.hp *= ((healthmult*healthmult)/20) + 0.95;
        game = g;
    }

    public void move (long delta){
        int[] temp = game.getShipXY();
        double tx = temp[0];
        double ty = temp[1];

        double vx = tx - (this.x + 5);
        double vy = ty - (this.y + 5);

        double len = Math.sqrt(vx*vx + vy*vy);

        this.dx = 0;
        this.dy = 0;

        if (len != 0) {
            vx /= len;
            vy /= len;
        }
        if (Math.abs(len) > 300 ){
        double speed = 100;
        this.dx = vx * speed;
        this.dy = vy * speed;
        } else {
            game.alienshoot((int)this.x, (int)this.y, (int)(vy*200), (int)(vx*200));
        }

        spintime += delta;
        if (Math.abs(this.dx) > 0 || Math.abs(this.dy) > 0){
        if (spintime > 250000000){
            spin = (spin != true);
            spintime = 0;
            if (spin){
                setSprite(a);
            } else {
                setSprite(b);
            }
        }
        } else {
        if (spintime > 400000000){
            spin = (spin != true);
            spintime = 0;
            if (spin){
                setSprite(a);
            } else {
                setSprite(b);
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
        game.death((int)this.x, (int)this.y, (int)other.getHorizontalMovement(), (int)other.getVerticalMovement(), "sprites/dead2.png", false);
        dead = true;
        game.alienDead();
        game.removeEntity(this);
    }
    }
    }
    
}


