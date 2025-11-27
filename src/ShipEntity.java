/* ShipEntity.java
 * March 27, 2006wd
 * Represents player's ship
 */
public class ShipEntity extends Entity {

  private Game game; // the game in which the ship exists
  private int hp;
  private int state;
  private int count = 0;
  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public ShipEntity(Game g, String r, int newX, int newY, int hp) {
    super(r, newX, newY);  // calls the constructor in Entity
    this.hp = hp;
    game = g;

  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move ship 
   */
  public void move (long delta){

    // stop at left side of screen
    if (x > 990){
      this.x = 990;
      this.dx = 0;
    } else if (x < 0){
      this.x = 00;
      this.dx = 0;
    }
    if (y > 990){
      this.y = 990;
      this.dy = 0;
    } else if (y < 0){
      this.y = 00;
      this.dy = 0;
    }

    if (this.dx == 0){
      state = 0;
    } else if ( this.dx < 0){
      state = 1;
    } else {
      state = 2;
    }
      
    
    super.move(delta);  // calls the move method in Entity
    
  } // move

  public int getHP() {
    return hp;
  }

  public void hit(){
    if (game.getcombo() / 5 == 0){
      hp--;
    } else {
      hp -= game.getcombo() / 5;
    }
    
    if (hp <= 0){
      game.notifyDeath();
    }
  }

  public void heal(){
    hp++;
  }
  
  
  /* collidedWith
   * input: other - the entity with which the ship has collided
   * purpose: notification that the player's ship has collided
   *          with something
   */
  @Override
   public void collidedWith(Entity other) {
    if (other instanceof ShotEntity shot){
      if (shot.getType() == 1){
       hit();
       game.decombo();
       game.removeEntity(other);
      }
    }

    if (other instanceof alien1 || other instanceof alien2 || other instanceof alien3){
      
      hit();
      game.alienDead();
      game.decombo();
      game.removeEntity(other);
    }

    if (other instanceof splat) {
      count++;
      if (count > 4){
        hit();
        count = 0;
      } 
      game.removeEntity(other);
    }

    if (other instanceof Tile){
      double XO = (Math.min(this.x, other.getX()) - Math.max(this.x, other.getX()) + 10);
      double YO = (Math.min(this.y, other.getY()) - Math.max(this.y, other.getY()) + 10);

      if (this.x < other.getX() && dx > 0)
      {this.x -= 1.1*XO;}
      else if (this.x > other.getX() && dx < 0)
      {this.x += 1.1*XO;}
      if (this.y < other.getY() && dy > 0)
      {this.y -= 1.1*YO;}
      else if (this.y > other.getY() && dy < 0) 
      {this.y += 1.1*YO;}

      game.updateshipsprite();
    }   
} // collidedWith 

  public int getstate() {
    return state;
  }

} // ShipEntity class