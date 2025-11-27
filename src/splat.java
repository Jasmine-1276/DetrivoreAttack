public class splat extends Entity{
    private Game game;
    private long Time = 0;
    private alien3 link;
    
    // Static sprite cache - loaded once
    private static Sprite[] goop = new Sprite[5];
    private static splat[] pool = new splat[40];  // Object pool
    private static int poolIndex = 0;
    private boolean inUse = false;
    
    static {
        goop[0] = (SpriteStore.get()).getSprite("sprites/goop/goop1.png");
        goop[1] = (SpriteStore.get()).getSprite("sprites/goop/goop2.png");
        goop[2] = (SpriteStore.get()).getSprite("sprites/goop/goop3.png");
        goop[3] = (SpriteStore.get()).getSprite("sprites/goop/goop4.png");
        goop[4] = (SpriteStore.get()).getSprite("sprites/goop/goop5.png");
    }

    public splat(int newX, int newY, Game g, alien3 a){
        super("sprites/goop/goop1.png", newX, newY);  // calls the constructor in Entity
        
        this.setSprite(goop[(int)(Math.random() * 5)]);
        
        this.dx = 0;
        this.dy = 0;
        link = a;
        game = g;
        inUse = true;
    }

    public static splat getInstance(int newX, int newY, Game g, alien3 a) {
        splat s = null;
        
        // Check if there is a reusable splat in the pool
        for (int i = 0; i < pool.length; i++) {
            if (pool[i] != null && !pool[i].inUse) {
                s = pool[i];
                s.x = newX;
                s.y = newY;
                s.Time = 0;
                s.link = a;
                s.game = g;
                s.setSprite(goop[(int)(Math.random() * 5)]);
                s.inUse = true;
                return s;
            }
        }
        
        // If no splat in pool, create a new one
        s = new splat(newX, newY, g, a);
        
        // Store in pool if there's space
        if (poolIndex < pool.length) {
            pool[poolIndex++] = s;
        }
        
        return s;
    }

    @Override
    public void collidedWith(Entity other) {}

    public void move (long delta){
        Time += delta;
        if (Time > 4000000000L){
            game.removeEntity(this);
            inUse = false;  // Mark as available for reuse in pool
        }
        super.move(delta);
    }
    
}
