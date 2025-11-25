/* ShipEntity.java
 * March 27, 2006wd
 * Represents player's ship
 */
public class shipSprite extends Entity {
  private boolean spin = true;
  private boolean floats = true;
  private Sprite centre1 = (SpriteStore.get()).getSprite("sprites/centre1.png");
  private Sprite centre2 = (SpriteStore.get()).getSprite("sprites/centre2.png");
  private Sprite right1 = (SpriteStore.get()).getSprite("sprites/right1.png");
  private Sprite right2 = (SpriteStore.get()).getSprite("sprites/right2.png");
  private Sprite left1 = (SpriteStore.get()).getSprite("sprites/left1.png");
  private Sprite left2 = (SpriteStore.get()).getSprite("sprites/left2.png");
  private long spintime = 0;
  private long floatTime = 0;
  private ShipEntity ship;
  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public shipSprite(String r, int newX, int newY, ShipEntity s) {
    super(r, newX, newY);  // calls the constructor in Entity
    ship = s;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move ship 
   */
  @Override
  public void move (long delta){

    spintime += delta;
    floatTime += delta;
    if (spintime > 100000000){
      spin = (spin != true);
      spintime = 0;
    }

    if (floatTime > 200000000){
      floats = (floats != true);
      floatTime = 0;
    }

    switch (ship.getstate()){
      case 0: 
      if (spin){
        setSprite(centre2);
      } else {
        setSprite(centre1);
      } break;
      case 1:
      if (spin){
        setSprite(left2);
      } else {
        setSprite(left1);
      } break;
      case 2:
      if (spin){
        setSprite(right2);
      } else {
        setSprite(right1);
      }
    }

    update();
    
    if (floats){
      this.y -= 1;
    } else {
      this.y += 1;
    }

    super.move(delta);
  } // move

  @Override
   public void collidedWith(Entity other) {

   } // collidedWith   
   
  public void update(){
    this.x = ship.getX() - 5;
    this.y = ship.getY() - 5;
  }

} // ShipEntity class