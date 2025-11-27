import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import javax.swing.*;

public class Game extends Canvas {


      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until a key is pressed
        private Sprite heart = (SpriteStore.get()).getSprite("sprites/heart.png");
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean firePressed = false; // true if firing
        private boolean jumpPressed = false;
        private boolean pausepressed = false;
        private boolean downpressed = false;
        private boolean temp = false;
        private boolean debug = false;
        private String HS = "";
        private int a3count = 0;
        private int best = getScore();
        private final int tileSize = 10;
        private int tutorial = 0;
        private boolean gameRunning = true;
        public ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities in game
        public ArrayList<Tile> tiles = new ArrayList<>();
        private final ArrayList<Entity>  removeEntities = new ArrayList<> (); // list of entities to remove this loop
        private ShipEntity ship;  // the ship
        private double moveSpeed = 500; // hor. vel. of ship (px/s)
        private long lastFire = 0; // time last shot fired
        private long firingInterval = 100; // interval between shots (ms)
        private int shadowtime = 0;
        private int round = 1;
        private int alienN = 0;
        private int count = 0;
        private int combocount;
        private int combo;
        private static final Path SAVE_FILE = Paths.get(System.getProperty("user.dir"), "save", "hs.txt");
        private Font f = new Font("arial", Font.BOLD, 12);
        private Sprite bg = (SpriteStore.get()).getSprite("sprites/bg.png");
        private Sprite uhc2 = (SpriteStore.get()).getSprite("sprites/uhc2.png");
        private Sprite start = (SpriteStore.get()).getSprite("sprites/start.png");
        private Sprite combo0 = (SpriteStore.get()).getSprite("sprites/combo0.png");
        private Sprite combo1 = (SpriteStore.get()).getSprite("sprites/combo1.png");
        private Sprite combo2 = (SpriteStore.get()).getSprite("sprites/combo2.png");
        private Sprite combo3 = (SpriteStore.get()).getSprite("sprites/combo3.png");
        private Sprite combo5 = (SpriteStore.get()).getSprite("sprites/combo5.png");
        private Sprite combo10 = (SpriteStore.get()).getSprite("sprites/combo10.png");
        private Sprite combo20 = (SpriteStore.get()).getSprite("sprites/combo20.png");
        private Sprite combo50 = (SpriteStore.get()).getSprite("sprites/combo50.png");
        private Sprite title = (SpriteStore.get()).getSprite("sprites/titleCard.png");
        private Sprite tutorial1 = (SpriteStore.get()).getSprite("sprites/tutorial1.png");
        private Sprite tutorial2 = (SpriteStore.get()).getSprite("sprites/tutorial2.png");
        private Color CLEAR = new Color(0, 0, 0, 0);
        private Color tShadow = new Color(20, 20, 50, 100);
        private Color ShadowColor = new Color(20,20, 50);
        private String message = ""; // message to display while waiting for a key press
        private AffineTransform at = AffineTransform.getScaleInstance(5.0, 5.0);
        private AffineTransform bt = AffineTransform.getScaleInstance(2.0, 2.0);
        BufferedImage shadowspace = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        BufferedImage textspace = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
        private int[] alienSpawners = new int[4];
        private boolean logicRequiredThisLoop = false; 
        private sfx combo1asfx = new sfx("sounds/combosfx1a.wav");
        private sfx combo1bsfx = new sfx("sounds/combosfx1b.wav");
        private sfx combo1csfx = new sfx("sounds/combosfx1c.wav");
        private sfx combo2asfx = new sfx("sounds/combosfx2a.wav");
        private sfx combo2bsfx = new sfx("sounds/combosfx2b.wav");
        private sfx combo2csfx = new sfx("sounds/combosfx2c.wav");
        private sfx combo3asfx = new sfx("sounds/combosfx3a.wav");
        private sfx combo3bsfx = new sfx("sounds/combosfx3b.wav");
        private sfx combo3csfx = new sfx("sounds/combosfx3c.wav");
        private sfx lvlSFX = new sfx("sounds/lvlSFX.wav");
        private sfx startTheme = new sfx("sounds/startTheme.wav");
        private bgm bgmHandler;
        private shipSprite shipfollower;
        
    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
            bgmHandler = new bgm();
            bgmHandler.setActiveTrack(5);
            
    		// create a frame to contain game
    		JFrame container = new JFrame("Detrivore Attack");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(1000,1000));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,1000,1000);
    		panel.add(this);
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);
    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(5);
    		strategy = getBufferStrategy();
    
    		// initialize entities
    		initEntities();
    
    		// start the game
    		gameLoop();
        } // constructor
    
    
        /* initEntities
         * input: none
         * output: none
         * purpose: Initialise the starting state of the ship and alien entities.
         *          Each entity will be added to the array of entities in the game.
    	 */
    	private void initEntities() {
              // create the ship and put in center of screen
              ship = new ShipEntity(this, "sprites/ship.png", 50, 900, 3);
              shipfollower = new shipSprite("sprites/ship.png", 5, 5, ship);
              initLevel("sprites/lvl3.png");
              initAliens();
              entities.add(ship);
              entities.add(shipfollower);
              
    
    	} // initEntities

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() {
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         /* Notification that the player has died.
          */
        public void notifyDeath() {
          if (round > getScore()){
            recordScore(round);
            best = round;
          }
          round = 1;
          alienN = 0;
          waitingForKeyPress = true;
          leftPressed = false;
          rightPressed = false;
          firePressed = false;
          jumpPressed = false;
          downpressed = false;
          count = 0;
          resetcombo();
        } // notifyDeath

        /* Attempt to fire.*/
        public void tryToFire() {
          // check that we've waited long enough to fire
          if ((System.currentTimeMillis() - lastFire) < firingInterval && !waitingForKeyPress){
            return;
          } // if

          // otherwise add a shot
          lastFire = System.currentTimeMillis();
          int msx = 0;
          int msy = 0;
          if (ship.getHorizontalMovement() != 0){
          if (ship.getHorizontalMovement() > 0) {
            msx = 1500;
          } else {msx = -1500;}}

          if (ship.getVerticalMovement() != 0){
          if (ship.getVerticalMovement() > 0) {
            msy = 1500;
          } else {msy = -1500;}}

          ShotEntity shot = new ShotEntity(this, "sprites/shot1/shot1a.png", 
            ship.getX() - 10, ship.getY() - 10, 1, 
            msy, msx, 0);
          entities.add(shot);
        } // tryToFire

	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {
          long lastLoopTime = System.nanoTime();

          // keep loop running until game ends
          while (gameRunning) {
            
            // calc. time since last update, will be used to calculate
            // entities movement
            long delta = System.nanoTime() - lastLoopTime;
            lastLoopTime = System.nanoTime();

            // get graphics context for the accelerated surface and make it black
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            bg.draw(g, 0, 0);

            // move each entity
            ship.move(delta);
            if (!waitingForKeyPress) {
              try{
                for (Entity e : entities){
                if (!(e instanceof ShipEntity)){
                  
                  e.move(delta);
                }}}
                  catch (Exception exce){}
              }

            
            // brute force collisions, compare every entity
            // against every other entity.  If any collisions
            // are detected notify both entities that it has
            // occurred
           for (int i = 0; i < entities.size(); i++) {
             for (int j = i + 1; j < entities.size(); j++) {
                try {
                Entity me = (Entity)entities.get(i);
                Entity him = (Entity)entities.get(j); 
                if (!(me instanceof Tile && him instanceof Tile || me instanceof splat && him instanceof splat)){
                if (me.collidesWith(him)) {
                  me.collidedWith(him);
                  him.collidedWith(me); }
                }} catch (Exception e) {
                  System.err.println("oop");
                } // if
             } // inner for
           } // outer for
            for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               if (!(entity instanceof Tile || entity instanceof ShipEntity)){
                entity.draw(g);}
            } // for
            if (shadowtime > 1) { 
            try {
              Graphics2D g2d = shadowspace.createGraphics();
              g2d.setBackground(CLEAR);
              g2d.clearRect(0, 0, 200, 200);
              cast(entities, getShipXY(), g2d);
              g.drawImage(shadowspace, at, null);
              g2d.dispose();}
              
           catch (Exception e) {
              System.out.println("oop2");
            }
            shadowtime = 0;
          } else {shadowtime++; g.drawImage(shadowspace, at, null);}
              
           for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               if (entity instanceof Tile){
                entity.draw(g);}
            }

            

           // remove dead entities
           entities.removeAll(removeEntities);
           for (Entity block : removeEntities) {
               if (block instanceof Tile tile) {
                tiles.remove(tile);
               }
           }
           
           removeEntities.clear();

           // run logic if required
           if (logicRequiredThisLoop) {
             for (int i = 0; i < entities.size(); i++) {
               Entity entity = (Entity) entities.get(i);
               entity.doLogic();
             } // for
             logicRequiredThisLoop = false;
           } // if

          if (debug){
            String frametime = "";
            String sround = "round ";
            frametime += 1000000000/(delta);
            frametime += " fps";
            g.setColor(Color.WHITE);
            g.drawString(frametime, 5, 990);}
           String sround = "round ";
           sround += round;
           Graphics2D ts = textspace.createGraphics();
           ts.setBackground(CLEAR);
           ts.setFont(f);
           ts.clearRect(0,0,500,500);
           ts.setColor(tShadow);
           ts.fill(new Rectangle(487 - ts.getFontMetrics(f).stringWidth(sround), 480, 100, 100));
           ts.setColor(Color.WHITE);
           ts.drawString(sround, 495 - ts.getFontMetrics(f).stringWidth(sround) , 495);
           if (waitingForKeyPress) {
             ts.setColor(tShadow);
             ts.fill(new Rectangle(0, 451, 135, 49));
             ts.setColor(Color.WHITE);
             ts.drawString("press space to start!!", 5, 480);
             ts.drawString("(or 1 for the tutorial)", 5, 495);
             ts.drawString("high score = " + best,5, 465);
           }
           g.drawImage(textspace, bt, null);
           ts.dispose();
          if (combo == 0){
            combo0.draw(g, 940, 10);
          } else if (combo == 1){
            combo1.draw(g, 940, 10);
          } else if (combo == 2){
            combo2.draw(g, 940, 10);
          } else if (combo == 3){
            combo3.draw(g, 940, 10);
          } else if (combo == 5){
            combo5.draw(g, 940, 10);
          } else if (combo == 10){
            combo10.draw(g, 940, 10);
          } else if (combo == 20){
            combo20.draw(g, 940, 10);
          } else if (combo == 50){
            combo50.draw(g, 940, 10);
          }
          g.setColor(tShadow);
          g.fill(new Rectangle(0,0, 30, 20*ship.getHP() + 10));
          for (int i = 0; i < ship.getHP(); i++) {
               heart.draw(g, 5, 5 + i*20);
          }
          if (waitingForKeyPress){
          if (tutorial == 0){
            title.draw(g, 250, 250);
          }
          if (tutorial == 1){
            tutorial1.draw(g, 0, 0);
          }
          if (tutorial == 2){
            tutorial2.draw(g, 0, 0);
          }
        }


            // clear graphics and flip buffer
            g.dispose();
            strategy.show();

            // ship should not move without user input
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);

            // respond to user moving ship
            if ((leftPressed) && (!rightPressed)) {
              ship.setHorizontalMovement(-moveSpeed);
            } else if ((rightPressed) && (!leftPressed)) {
              ship.setHorizontalMovement(moveSpeed);
            } // else
            if ((jumpPressed) && (!downpressed)){
              ship.setVerticalMovement(-moveSpeed);
            } else if ((downpressed)&&(!jumpPressed)){
              ship.setVerticalMovement(moveSpeed);
            }

            // if spacebar pressed, try to fire
            if (firePressed) {
              tryToFire();
            } // if
            //try { Thread.sleep(16); } catch (Exception e) {}
          } // while

          
	} // gameLoop


        /* startGame
         * input: none
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            
            
            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            firePressed = false;
            tutorial = 3;
            temp = false;
            bgmHandler.setVolume(0);
            startTheme.play();
            try {
              Thread.sleep(2500);
            } catch (Exception e) {
            }
            bgmHandler.setActiveTrack(0);
            bgmHandler.setVolume(5);
            
            try {
              Thread.sleep(500);
            } catch (Exception e) {
            }
            entities.clear();
            // ensure alien counter is reset when starting a fresh game
            alienN = 0;
            initEntities();
            waitingForKeyPress = false;
            tutorial = 0;
         } // startGame


        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {
                 
                 private int pressCount = 1;  // the number of key presses since
                                              // waiting for 'any' key press

                /* The following methods are required
                 * for any class that extends the abstract
                 * class KeyAdapter.  They handle keyPressed,
                 * keyReleased and keyTyped events.
                 */
		public void keyPressed(KeyEvent e) {

                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_P) {
                    notifyDeath();
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightPressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = true;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_W) {
                    jumpPressed = true;
                  } // if
                  if (e.getKeyCode() == KeyEvent.VK_S) {
                    downpressed = true;
                  } // if                

		} // keyPressed

		public void keyReleased(KeyEvent e) {
                  // if waiting for keypress to start game, do nothing
                  if (e.getKeyCode() == KeyEvent.VK_1) {
                    temp = false;
                  } // if

                  if (waitingForKeyPress) {
                    return;
                  } // if

                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_A) {
                    leftPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_D) {
                    rightPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_W) {
                    jumpPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_S) {
                    downpressed = false;
                  } // if


		} // keyReleased

 	        public void keyTyped(KeyEvent e) {

                   // if waiting for key press to start game
 	           if (waitingForKeyPress) {
                     if (pressCount == 1) {
                      if (e.getKeyChar() == KeyEvent.VK_SPACE){
                       startGame();
                       bgmHandler.setActiveTrack(0);
                       pressCount = 0;}
                      if (e.getKeyChar() == KeyEvent.VK_0){
                        debug = true;
                        pressCount = 0;
                      }
                      if (e.getKeyChar() == KeyEvent.VK_1){
                        if (tutorial == 0 && !temp){
                        tutorial = 1;
                        System.out.print('a');
                        temp = true;
                        pressCount = 0;}

                        if (tutorial == 1 && !temp){
                        tutorial = 2;
                        System.out.print('b');
                        temp = true; 
                        pressCount = 0;}
                      }
                     } else {
                       pressCount++;
                     } // else
                   } // if waitingForKeyPress

                   // if escape is pressed, end game
                   if (e.getKeyChar() == 27) {
                     System.exit(0);
                   } // if escape pressed

		} // keyTyped

	} // class KeyInputHandler

	/**
	 * Main Program
	 */
	public static void main(String [] args) {
        // instantiate this object
		new Game();
		
	} // main

  public void initLevel(String r){
    //followed a tutorial for this, https://www.youtube.com/watch?v=et5JeT-ESKk
    for (Entity i : entities) {
        if (i instanceof Tile) {
            removeEntity(i);
        }
    }

		Sprite img = (SpriteStore.get()).getSprite(r);
		
		System.out.println(img.getHeight());

		System.out.println(img.getWidth());

		for(int i = 0; i < img.getHeight(); i ++) {

			for(int j = 0; j < img.getWidth(); j ++) {

      switch (((BufferedImage) img.image).getRGB(i, j)) {
        case 0xFF000000:
          Tile tile = new Tile(i * tileSize, j * tileSize, tileSize, "sprites/block.png");
          entities.add(tile);
        break;
        case 0xFFFF0000:
          alienSpawners[0] = i*tileSize;
          alienSpawners[1] = j*tileSize;
        break;
        case 0xFF00FF00:
          alienSpawners[2] = i*tileSize;
          alienSpawners[3] = j*tileSize;
        break;
        default:
        break;
        }
			}
		}
	}

  public int[] getShipXY(){
    int[] returnInts = {ship.getX() + 5, ship.getY() + 5};
    return returnInts;
  }

  public void cast(ArrayList<Entity> shadowcasters, int[] lightXY, Graphics2D g){
        int lx = lightXY[0]/5 + 1;
        int ly = lightXY[1]/5 + 1;
        int[] pointx = {0, 0, 0, 0};
        int[] pointy = {0, 0, 0, 0};
         for (Entity i : shadowcasters) {
        if (i instanceof Tile shadowcaster){

            int x = shadowcaster.getX()/5;
            int y = shadowcaster.getY()/5;
            int size = shadowcaster.getSize()/5;
            
            
            if (x + 15 < lx && y + 15 < ly){
                pointx[0] = x + size;
                pointy[0] = y;
                
                pointx[1] = x;
                pointy[1] = y + size;
            } else if (x + 15 > lx && y + 15 < ly){
                pointx[0] = x + size;
                pointy[0] = y + size;
                
                pointx[1] = x;
                pointy[1] = y;
            } else if (x + 15 > lx && y + 15 > ly){
                pointx[0] = x;
                pointy[0] = y + size;
                
                pointx[1] = x + size;
                pointy[1] = y;
            } else if (x + 15 < lx && y + 15 > ly){
                pointx[0] = x;
                pointy[0] = y;
                
                pointx[1] = x + size;
                pointy[1] = y + size;
            }
            
            pointx[2] = (pointx[1] - lx)*200;
            pointy[2] = (pointy[1] - ly)*200;
            
            pointx[3] = (pointx[0] - lx)*200;
            pointy[3] = (pointy[0] - ly)*200;
            
            Polygon shadow = new Polygon(pointx, pointy, 4);
            g.setColor(ShadowColor);
            g.fill(shadow);
            }}
    }
long lasttime = 0;
    public void alienshoot(int x, int y, int a, int b){
      if (lasttime - System.currentTimeMillis() < -150){
      ShotEntity Ashot = new ShotEntity(this, "sprites/shot2/shot2a.png", (int)x, (int)y, 1, a * (2+combo/20), b * (2+combo/20), 1);
      entities.add(Ashot);
      lasttime = System.currentTimeMillis();}
    }

    public void death(int x, int y, int msx, int msy, String r, double h){
      entities.add(new dead(this, x, y, msx, msy, r, h));
    }

    public void initAliens(){

    alienN = 0;
    a3count = 0;

    if (Math.abs(alienSpawners[0] - ship.getX()) < 100 && Math.abs(alienSpawners[1] - ship.getY()) < 100){
      if (alienSpawners[0] > ship.getX()){
        if ((alienSpawners[0] - ship.getX()) < 100){
          alienSpawners[0] = ship.getX() + 100;
      }} else{
        if ((alienSpawners[0] - ship.getX()) > -100){
          alienSpawners[0] = ship.getX() + 100;
      }}

      if (alienSpawners[1] > ship.getY()){
        if ((alienSpawners[1] - ship.getY()) < 100){
          alienSpawners[1] = ship.getY() + 100;
      }} else{
        if ((alienSpawners[1] - ship.getY()) > -100){
          alienSpawners[1] = ship.getY() + 100;
      }}
    }
      if (alienSpawners[2] > ship.getX()){
        if ((alienSpawners[2] - ship.getX()) < 100){
          alienSpawners[2] = ship.getX() + 100;
      }} else{
        if ((alienSpawners[2] - ship.getX()) > -100){
          alienSpawners[2] = ship.getX() + 100;
      }}
    if (Math.abs(alienSpawners[2] - ship.getX()) < 100 && Math.abs(alienSpawners[3] - ship.getY()) < 100){
      if (alienSpawners[3] > ship.getY()){
        if ((alienSpawners[3] - ship.getY()) < 100){
          alienSpawners[3] = ship.getY() + 100;
      }} else{
        if ((alienSpawners[3] - ship.getY()) > -100){
          alienSpawners[3] = ship.getY() + 100;
      }}
    }

    for (int i = 0; i < round; i++){
      int random = (int) (Math.random() * 4);
        if (random == 1){
          alien2 aliena = new alien2(this, alienSpawners[0]+i*20, alienSpawners[1], round); 
          entities.add(aliena);
          alienN++;
        } else if (random == 2){
          alien1 alienb = new alien1(this, alienSpawners[2], alienSpawners[3]+i*30, round);
          entities.add(alienb);
          alienN++;
        } else if (random == 3){
          alien4 aliend = new alien4(this, alienSpawners[2]+i*25, alienSpawners[1], round);
          entities.add(aliend);
          alienN++;
        } else {
          if (a3count < 2 && round > 4){
          a3count++;
          alien3 alienc = new alien3(this, alienSpawners[0]+i*25, alienSpawners[3], round);
          entities.add(alienc);
          alienN++;
        } else {
          if ((int) (Math.random() * 2) == 1){
            alien2 aliena = new alien2(this, alienSpawners[0]+i*20, alienSpawners[1], round); 
            entities.add(aliena);
            alienN++;
          } else {
            alien1 alienb = new alien1(this, alienSpawners[2], alienSpawners[3]+i*30, round);
            entities.add(alienb);
            alienN++;
          }
          }
      }
    }
    }

    public void alienDead(){
      alienN--;
      int tempA = combo;
      combocount++;
      bgmHandler.setActiveTrack(0);
      if (combocount == 1){
        combo = 1;
        bgmHandler.setActiveTrack(1);
      } else if (combocount == 2){
        combo = 2;
        bgmHandler.setActiveTrack(1);
      } else if (combocount <= 4){
        combo = 3;
        bgmHandler.setActiveTrack(1);
      } else if (combocount <= 9){
        combo = 5;
        bgmHandler.setActiveTrack(2);
      } else if (combocount <= 19){
        combo = 10;
        bgmHandler.setActiveTrack(3);
      } else if (combocount <= 49){
        combo = 20;
        bgmHandler.setActiveTrack(4);
      } else if (combocount >= 50 ){
        combo = 50;
        bgmHandler.setActiveTrack(5);
      }
      if (combo != 50){
      moveSpeed = 500 + (20*combo);} else {
        moveSpeed = 1000;
      }
      if (combo > tempA && tempA != 1 && tempA != 2){
        switch ((int)(Math.random() * 3)) {
            case 0:
                combo1asfx.play();
                break;
            case 1:
                combo1bsfx.play();
                break;
            case 2:
                combo1csfx.play();
                break;
            default:
                throw new AssertionError();
        }
      } else {
        switch ((int)(Math.random() * 3)) {
            case 0:
                combo3asfx.play();
                break;
            case 1:
                combo3bsfx.play();
                break;
            case 2:
                combo3csfx.play();
                break;
            default:
                throw new AssertionError();
        }
      }

      if (alienN <= 0){
        alienN = 0;
        round++;
        count++;
        ship.heal();
        if (count == 2){
          initLevel("sprites/lvl" + (int) ((Math.random() * 10) + 1) + ".png");
          lvlSFX.play();
          count = 0;
        }
        initAliens();
      }
    }

    public void resetcombo(){
        switch ((int)(Math.random() * 3)) {
            case 0:
                combo2asfx.play();
                break;
            case 1:
                combo2bsfx.play();
                break;
            case 2:
                combo2bsfx.play();
                break;
            default:
                throw new AssertionError();
        }

      combocount = 0;
      combo = 0;
      moveSpeed = 500;
      bgmHandler.setActiveTrack(0);
    }

    public void decombo(){
      switch ((int)(Math.random() * 3)) {
            case 0:
                combo2asfx.play();
                break;
            case 1:
                combo2bsfx.play();
                break;
            case 2:
                combo2bsfx.play();
                break;
            default:
                throw new AssertionError();
        }
      if (combo == 1){
        combo = 0;
        combocount = 0;
        bgmHandler.setActiveTrack(0);
      } else if (combo == 2){
        combo = 1;
        combocount = 1;
        bgmHandler.setActiveTrack(1);
      } else if (combo == 3){
        combo = 2;
        combocount = 2;
        bgmHandler.setActiveTrack(1);
      } else if (combo == 5){
        combo = 3;
        combocount = 3;
        bgmHandler.setActiveTrack(1);
      } else if (combo == 10){
        combo = 5;
        combocount = 5;
        bgmHandler.setActiveTrack(2);
      } else if (combo == 20){
        combo = 10;
        combocount = 10;
        bgmHandler.setActiveTrack(3);
      } else if (combo == 50){
        combo = 20;
        combocount = 20;
        bgmHandler.setActiveTrack(4);
      } 

    }

    public int getcombo(){
      return combo;
    }

    public void updateshipsprite() {
      shipfollower.update();
    }

    public int getScore() {
        try {
            if (!Files.exists(SAVE_FILE)) {
                Files.createDirectories(SAVE_FILE.getParent());
                Files.writeString(SAVE_FILE, "0");
            }
            String content = Files.readString(SAVE_FILE).trim();
            return Integer.parseInt(content.isEmpty() ? "0" : content);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return 0; 
        }
    }

    public void splat(splat s) {
      entities.add(0, s);
    }

    public void recordScore(int score) {
        try {
            if (!Files.exists(SAVE_FILE.getParent())) {
                Files.createDirectories(SAVE_FILE.getParent());
            }
            Files.writeString(SAVE_FILE, String.valueOf(score), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} // Game
