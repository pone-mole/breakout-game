// Constants used throughout the Breakout Game
// NOTE: We saw this once but it is useful when
//       creating a game. Means if we change a number here
//       we should see related changes everywhere
// 
// Constants can be accessed by doing Constants.<name-of-constant>
// e.g. Constants.BALL_RAD will get the ball's radius

interface Constants {
  int CANVAS_HEIGHT = 500;
  int CANVAS_WIDTH = 500;
  double TICK_RATE = 1.0/28.0;

  // Height of the death plane
  // Named as such since once the ball falls here, there is no recovery
  int DEATH_PLANE = 50;
  
  // Properties of the board the player controls
  int BOARD_HEIGHT = 10;
  int BOARD_WIDTH = 50;
  int STARTING_BOARD_X = CANVAS_WIDTH/2;
  int STARTING_BOARD_Y = CANVAS_HEIGHT - BOARD_HEIGHT/2 - DEATH_PLANE;

  // Properties of the ball in the game
  int BALL_RAD = 10;
  int STARTING_BALL_X = STARTING_BOARD_X;
  int STARTING_BALL_Y = STARTING_BOARD_Y - BOARD_HEIGHT/2 - BALL_RAD;
  int STARTING_BALL_VEL_X = 5;
  int STARTING_BALL_VEL_Y = -5;
  
  // Properties of the bricks in the game
  int BRICK_HEIGHT = BOARD_HEIGHT;
  int BRICK_WIDTH = BOARD_WIDTH;
  int BRICK_SCORE = 10;
  int N_BRICK_ROWS = 10;
  int N_BRICK_COLS = 8;
  int STARTING_BRICK_X = (CANVAS_WIDTH - (N_BRICK_COLS*BRICK_WIDTH))/2 - BRICK_WIDTH/2;
  int STARTING_BRICK_Y = 100;
}