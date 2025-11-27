/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotEntity extends Entity {

  private double MSY; // vert speed shot moves
  private int used = 0; // true if shot hits something
  private Game game; // the game in which the ship exists
  private int type;
  private String r;
  private long spintime = 0;
  private boolean spin = false;
  private Sprite a1 = (SpriteStore.get()).getSprite("sprites/shot1/shot1a.png");
  private Sprite a2 = (SpriteStore.get()).getSprite("sprites/shot1/shot1b.png");
  private Sprite b1 = (SpriteStore.get()).getSprite("sprites/shot2/shot2a.png");
  private Sprite b2 = (SpriteStore.get()).getSprite("sprites/shot2/shot2b.png");

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public ShotEntity(Game g, String r, int newX, int newY, int used, int MSY, int MSX, int type) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    this.dy = MSY;
    this.dx = MSX;
    this.used = used;
    this.type = type;
    if (this.dy == 0 && this.dx == 0){
      game.removeEntity(this);
    }
  } // constructor

  /* move
   * input: delta - time elapsed since last move (MSY)
   * purpose: move shot
   */
  public void move (long delta){
    super.move(delta);  // calls the move method in Entity

    // if shot moves off top of screen, remove it from entity list
    if (y < -100) {
      game.removeEntity(this);
    } // if
    if (y > 1100) {
      game.removeEntity(this);
    } // if
    if (x < -100) {
      game.removeEntity(this);
    } // if
    if (x > 1100) {
      game.removeEntity(this);
    } // if

    spintime += delta;
    if (spintime > 100000000){
      spin = (spin != true);
      spintime = 0;
      if (spin){
        if (type == 0){
          setSprite(a1);
        } else {
          setSprite(b1);
        }
      } else {
        if (type == 0){
          setSprite(a2);
        } else {
          setSprite(b2);
        }
      }
    }

  } // move

  public int getType(){
    return type;
  }


  /* collidedWith
   * input: other - the entity with which the shot has collided
   * purpose: notification that the shot has collided
   *          with something
   */
  @Override
   public void collidedWith(Entity other) {
     // prevents double kills
      if (other instanceof Tile) {
      game.removeEntity(other);
      game.removeEntity(this);

    } // if

   } // collidedWith

} // ShipEntity class