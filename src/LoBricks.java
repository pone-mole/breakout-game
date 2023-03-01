import javalib.funworld.WorldScene;

//represents a list of bricks
interface ILoBrick {
  // returns a new game caused by colliding with any bricks
  BreakoutWorld collidesWithBall(BreakoutWorld game);
  
  // returns a new game caused by colliding with any of the bricks
  // ACC: soFar collects the bricks that have NOT been collided with so far
  BreakoutWorld collidesWithBallHelper(BreakoutWorld game, ILoBrick soFar);
  
  // returns a new scene with the list of bricks visually represented
  WorldScene renderToScene(WorldScene scene);
  
  // returns true if the list is empty
  // NOTE: This is a win-condition for the game
  boolean isEmpty();
}

// represents an empty list
class MtLoBrick implements ILoBrick {

  // returns the game given as no collisions could occur
  public BreakoutWorld collidesWithBall(BreakoutWorld game) {
    return game;
  }

  // returns a new game with the list of bricks that did NOT collide
  //   with the ball in the game
  // ACC: soFar collects the bricks that have NOT collided with the game's ball so far
  public BreakoutWorld collidesWithBallHelper(BreakoutWorld game, ILoBrick soFar) {
    return new BreakoutWorld(game.score, soFar, game.board, game.ball, game.gameStarted);
  }

  // returns the scene given as an empty list cannot add to the scene
  public WorldScene renderToScene(WorldScene scene) {
    return scene;
  }

  // returns true since this class represents an empty list
  public boolean isEmpty() {
    return true;
  }
}

// represents a non-empty list
class ConsLoBrick implements ILoBrick {
  Brick first;
  ILoBrick rest;
  
  ConsLoBrick(Brick first, ILoBrick rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns a new game resulting from the ball colliding with any
  //   brick in this list
  public BreakoutWorld collidesWithBall(BreakoutWorld game) {
    return this.collidesWithBallHelper(game, new MtLoBrick());
  }

  // returns a new game resulting from the ball colliding with this brick
  //   or any brick in the rest
  // ACC: soFar collects the bricks that have NOT collided with the game's ball so far
  public BreakoutWorld collidesWithBallHelper(BreakoutWorld game, ILoBrick soFar) {
    if(game.ball.collidesWith(this.first)) {
      return this.rest.collidesWithBallHelper(new BreakoutWorld(game.score + Constants.BRICK_SCORE, game.bricks, game.board, game.ball.flipVelocity(), game.gameStarted), soFar);
    } else {
      return this.rest.collidesWithBallHelper(game, new ConsLoBrick(this.first, soFar));
    }
  }

  // returns a new scene with first rendered on and then the rest
  // of the bricks rendered onto it
  public WorldScene renderToScene(WorldScene scene) {
    return this.rest.renderToScene(this.first.renderToScene(scene));
  }
  
  // returns false since this list is never empty
  public boolean isEmpty() {
    return false;
  }
}
