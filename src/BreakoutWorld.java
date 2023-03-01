import java.awt.Color;
import java.util.Random;

import javalib.funworld.*;
import javalib.worldimages.*;

// represents the state and logic of the Breakout game
class BreakoutWorld extends World {

  int score;
  
  // Flag to note when the game has actually started
  boolean gameStarted;
  
  ILoBrick bricks;
  Board board;
  Ball ball;
  
  // Constructor for a brand new game
  BreakoutWorld() {
    this.score = 0;
    this.bricks = this.createListOfBricks(Constants.N_BRICK_ROWS, Constants.N_BRICK_COLS, new MtLoBrick(), new Random());
    this.board = new Board(Constants.CANVAS_WIDTH/2, Constants.STARTING_BOARD_Y, 0, 0, Constants.BOARD_HEIGHT, Constants.BOARD_WIDTH);
    this.ball = new Ball(Constants.CANVAS_WIDTH/2, Constants.STARTING_BALL_Y, 0, 0, Constants.BALL_RAD);
    this.gameStarted = false;
  }
  
  // Constructor for passing on new state of the game
  BreakoutWorld(int score, ILoBrick bricks, Board board, Ball ball, boolean gameStarted) {
    this.score = score;
    this.bricks = bricks;
    this.board = board;
    this.ball = ball;
    this.gameStarted = gameStarted;
  }
  
  // creates the initial list of bricks for the game
  ILoBrick createListOfBricks(int nRows, int nCols, ILoBrick soFar, Random rand) {
    if(nCols == 0 && nRows == 0) {
      return soFar;
    }
    
    if(nCols == 0 && nRows > 0) {
      return createListOfBricks(nRows-1, Constants.N_BRICK_COLS, soFar, rand);
    }
    
    if(rand.nextBoolean()) {
      return createListOfBricks(nRows, nCols-1, new ConsLoBrick(new Brick(Constants.STARTING_BRICK_X + nCols*Constants.BRICK_WIDTH, Constants.STARTING_BRICK_Y + nRows*Constants.BRICK_HEIGHT, 0, 0, Constants.BRICK_HEIGHT, Constants.BRICK_WIDTH), soFar), rand);
    } else {
      return createListOfBricks(nRows, nCols-1, soFar, rand);
    }
  }
  
  //Returns the rendered world to be shown on screen
  public WorldScene makeScene() {
    TextImage scoreText = new TextImage("Score: " + score, Color.BLACK);
    return ball.renderToScene(
           bricks.renderToScene(
           board.renderToScene(
             new WorldScene(Constants.CANVAS_WIDTH,
                            Constants.CANVAS_HEIGHT)
               .placeImageXY(scoreText, (int)scoreText.width/2, (int)scoreText.height/2))));
  }
  
  // creates a new World on each tick depending on what has
  // occurred in the game so far
  public World onTick() {
    if(!this.gameStarted) {
      return this;
    }

    if(this.isGameOver()) {
      return this.endOfWorld("Final score: " + this.score);
    }
       
    return this.checkBoardInBounds()
        .checkBallInBounds()
        .checkBoardCollision()
        .checkBrickCollisions()
        .moveBall();
  }
  
  // returns true if the game is over
  //   The game is over if
  //   1) there are no bricks left to hit
  //   2) the ball has fallen beneath the world
  boolean isGameOver() {
    return this.bricks.isEmpty() || this.ball.isBeneathWorld(Constants.CANVAS_HEIGHT);
  }
  
  // returns the final scene of the game with the player's score
  public WorldScene lastScene(String s) {
    return new WorldScene(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT).placeImageXY(new TextImage(s, Color.BLACK), Constants.CANVAS_WIDTH/2, Constants.CANVAS_HEIGHT/2);
  }

  // returns a game with the board in bounds of the canvas
  public BreakoutWorld checkBoardInBounds() {
    if(this.board.isInBounds(0, 0, Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT)) {
      return this;
    } else {
      return new BreakoutWorld(this.score, this.bricks, this.board.makeBoardInBounds(0, 0, Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT), this.ball, this.gameStarted);
    }
  }
  
  // returns a game with the ball in bounds of the canvas
  public BreakoutWorld checkBallInBounds() {
    if(this.ball.isInBounds(0, 0, Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT)) {
      return this;
    } else {
      return new BreakoutWorld(this.score, this.bricks, this.board, this.ball.makeBallInBounds(0, 0, Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT), this.gameStarted);
    }   
  }
  
  // returns a game where the ball has moved according to its velocity
  public BreakoutWorld moveBall() {
    return new BreakoutWorld(this.score, this.bricks, this.board, this.ball.moveBall(), this.gameStarted);
  }
  
  // returns a game resulting from the ball colliding with the board
  public BreakoutWorld checkBoardCollision() {
    return new BreakoutWorld(this.score, this.bricks, this.board, this.ball.onCollision(this.board), this.gameStarted);
  }
  
  // returns a game resulting from the ball colliding with the bricks
  public BreakoutWorld checkBrickCollisions() {
    return this.bricks.collidesWithBall(this);
  }
  
  // returns a game depending on whether the mouse has been clicked
  // NOTE: This starts the game proper by moving the ball
  public BreakoutWorld onMouseClicked(Posn pos) {
    if(!this.gameStarted) {
      return new BreakoutWorld(this.score, this.bricks, this.board, new Ball(this.ball.pos, new Posn(Constants.STARTING_BALL_VEL_X, Constants.STARTING_BALL_VEL_Y), this.ball.radius), true);
    } else {
      return this;
    }
  }
  
  // returns a game with the board's new position assuming the game
  //   has been started
  public BreakoutWorld onMouseMoved(Posn pos) {
    if(!this.gameStarted) {
      return this;
    } else {
      return new BreakoutWorld(this.score, this.bricks, new Board(new Posn(pos.x, this.board.pos.y), this.board.vel, this.board.height, this.board.width), this.ball, this.gameStarted).checkBoardInBounds();
      
    }
  }
}
