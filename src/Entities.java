import java.awt.Color;

import javalib.funworld.WorldScene;
import javalib.worldimages.*;

// represents game objects
interface IEntity { 
  // returns an image representation of the entity
  WorldImage toImage();
  
  // returns a scene that has this entity added to the world
  WorldScene renderToScene(WorldScene world);
  
  // returns true if that collides with this entity
  boolean collidesWith(IEntity that);
  
  // returns true if that ball collides with this entity
  boolean collidesWith(Ball that);
  
  // returns true if that rectangular entity collides with this entity
  boolean collidesWith(ARectEntity that);
}

// represents any entity in the world
abstract class AEntity implements IEntity {
  Posn pos;
  Posn vel;
  
  AEntity(Posn pos, Posn vel) {
    this.pos = pos;
    this.vel = vel;
  }
  
  AEntity(int x, int y, int xVel, int yVel) {
    this.pos = new Posn(x, y);
    this.vel = new Posn(xVel, yVel);
  }
  
  // returns an image representation of this entity
  public abstract WorldImage toImage();
  
  // returns a scene where this entity is added to the image
  // at its known position
  public WorldScene renderToScene(WorldScene world) {
    return world.placeImageXY(this.toImage(), this.pos.x, this.pos.y);
  }
  
  // returns true if that entity collides with this
  public boolean collidesWith(IEntity that) {
    return that.collidesWith(this);
  }
  
  // returns false since no ball can collide with this
  public boolean collidesWith(Ball that) {
    return false;
  }
  
  // returns false since no rectangular entity can collide with this
  public boolean collidesWith(ARectEntity that) {
    return false;
  }
}

// represents rectangular entities
abstract class ARectEntity extends AEntity {
  int height;
  int width;
  
  ARectEntity(Posn pos, Posn vel, int height, int width) {
    super(pos, vel);
    this.height = height;
    this.width = width;
  }
  
  ARectEntity(int x, int y, int xVel, int yVel, int height, int width) {
    this(new Posn(x, y), new Posn(xVel, yVel), height, width);
  }
  
  // returns a rectangle representing this entity
  public WorldImage toImage() {
    return new RectangleImage(width, height, OutlineMode.SOLID, Color.GRAY);
  }
  
  // returns true if and only if this rectangle is contained within the
  // coordinates represented by the parameters
  // NOTE: (minX, minY) represents the top left corner,
  //       (maxX, maxY) represents the bottom right corner
  boolean isInBounds(double minX, double minY, double maxX, double maxY) {
    return this.pos.x - this.width/2 >= minX && this.pos.x + this.width/2 <= maxX
        && this.pos.y + this.height/2 <= maxY && this.pos.y + this.height/2 >= minY; 
  }
}

// represents the board the player moves
class Board extends ARectEntity {

  Board(Posn pos, Posn vel, int height, int width) {
    super(pos, vel, height, width);
  }
  
  Board(int x, int y, int xVel, int yVel, int height, int width) {
    super(x, y, xVel, yVel, height, width);
  }
  
  // returns a new Board that is guaranteed to be within the specified bounds
  // NOTE: (minX, minY) represents the top left corner,
  //       (maxX, maxY) represents the bottom right corner
  Board makeBoardInBounds(int minX, int minY, int maxX, int maxY) {
    int newX = Math.min(Math.max(this.pos.x, minX + this.width/2), maxX - this.width/2);
    int newY = Math.max(Math.min(this.pos.y, maxY - this.height/2), minY + this.height/2);
    return new Board(new Posn(newX, newY), this.vel, this.height, this.width);
  }
  
}

// represents the ball the player uses to hit bricks
class Ball extends AEntity {
  int radius;
  
  Ball(Posn pos, Posn vel, int radius) {
    super(pos, vel);
    this.radius = radius;
  }
  
  Ball(int x, int y, int xVel, int yVel, int radius) {
    super(x, y, xVel, yVel);
    this.radius = radius;
  }

  // returns a circle representing the ball
  public WorldImage toImage() {
    return new CircleImage(this.radius, OutlineMode.SOLID, Color.CYAN);
  }
  
  // returns a new ball moved by the vector vel
  public Ball moveBall() {
    return new Ball(new Posn(this.pos.x + this.vel.x, this.pos.y + this.vel.y), this.vel, this.radius);
  }
  
  // returns true if the ball is contained within the bounds specified
  // by the parameters
  // NOTE: (minX, minY) represents the top left corner,
  //       (maxX, maxY) represents the bottom right corner
  public boolean isInBounds(double minX, double minY, double maxX, double maxY) {
    return this.pos.x - this.radius >= minX && this.pos.x + this.radius <= maxX
        && this.pos.y - this.radius >= minY && this.pos.y + this.radius <= maxY;
  }
  
  // returns a new ball guaranteed to be in the bounds specified
  // by the parameters
  // NOTE: (minX, minY) represents the top left corner,
  //       (maxX, maxY) represents the bottom right corner
  Ball makeBallInBounds(int minX, int minY, int maxX, int maxY) {  
    return this.makeBallHelperX(minX, maxX).makeBallHelperY(minY, maxY);
  }
  
  // returns a new ball with an x position guaratneed to be in the
  // bounds specified by the parameters
  Ball makeBallHelperX(int minX, int maxX) {
    if (this.pos.x - this.radius < minX) {
      return new Ball(new Posn(minX + this.radius, this.pos.y), new Posn(-1 * this.vel.x, this.vel.y), this.radius);
    } else if (this.pos.x + this.radius > maxX) {
      return new Ball(new Posn(maxX - this.radius, this.pos.y), new Posn(-1 * this.vel.x, this.vel.y), this.radius);
    } else {
      return this;
    }
  }
  
  // returns a new ball with a y position guaranteed to be in the 
  // bounds specified by the parameters
  Ball makeBallHelperY(int minY, int maxY) {
    if (this.pos.y - this.radius < minY) {
      return new Ball(new Posn(this.pos.x, minY + this.radius), new Posn(this.vel.x, -1 * this.vel.y), this.radius);
    } else if (this.pos.y + this.radius > maxY) {
      return new Ball(new Posn(this.pos.x, maxY - this.radius), new Posn(this.vel.x, -1 * this.vel.y), this.radius);
    } else {
      return this;
    }
  }
  
  // returns true if the bounding box for this ball
  // overlap with the bounding box for that rectanguar entity
  public boolean collidesWith(ARectEntity that) {
    WorldImage boardRect = that.toImage();
    double boardMinX = that.pos.x - boardRect.getWidth()/2;
    double boardMaxX = that.pos.x + boardRect.getWidth()/2;
    double boardMinY = that.pos.y - boardRect.getHeight()/2;
    double boardMaxY = that.pos.y + boardRect.getHeight()/2;
    
    WorldImage circRect = this.toImage();
    double circMinX = this.pos.x - circRect.getWidth()/2;
    double circMaxX = this.pos.x + circRect.getWidth()/2;
    double circMinY = this.pos.y - circRect.getHeight()/2;
    double circMaxY = this.pos.y + circRect.getHeight()/2;

    // Check if boxes are clear of each other entirely
    if(circMinX >= boardMaxX || circMaxX <= boardMinX) {
      return false;
    } else if(circMaxY <= boardMinY || circMinY >= boardMaxY) {
      return false;
    }
    return true;
  }
  
  // returns a new Ball affected by collisions with the Board
  public Ball onCollision(Board that) {
    if(this.collidesWith(that)) {
      return this.flipVelocity().increaseVelocity();
    } else {
      return this;
    }
  }

  // returns a new Ball with the y-velocity flipped
  public Ball flipVelocity() {
    return new Ball(this.pos, new Posn(this.vel.x, this.vel.y * -1), this.radius);
  }
  
  // returns a new Ball with an increased velocity while maintaining
  // direction
  public Ball increaseVelocity() {
    return new Ball(this.pos, new Posn(this.vel.x + (1 * (int)Math.signum(this.vel.x)), this.vel.y + (1 * (int)Math.signum(this.vel.y))), this.radius);
  }

  // returns true if the ball touches or goes past the bottom
  // of the world as specified by canvasHeight
  public boolean isBeneathWorld(int canvasHeight) {
    return this.pos.y + this.radius >= canvasHeight;
  }
}

// represents the object that is destroyed by the ball
class Brick extends ARectEntity {

  Brick(Posn pos, Posn vel, int height, int width) {
    super(pos, vel, height, width);
  }
  
  Brick(int x, int y, int xVel, int yVel, int height, int width) {
    super(x, y, xVel, yVel, height, width);
  }
  
  // returns an outlined rectangular image representing this brick
  public WorldImage toImage() {
    //NOTE: super.toImage() calls the superclass' version of
    //      toImage. Doing so means we do not have to
    //      rewrite the rectangle drawing code unless we want to
    //      change it.
    return new FrameImage(super.toImage());
  }
  
}
