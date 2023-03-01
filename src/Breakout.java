import tester.Tester;

class Breakout {
  boolean testBigBang(Tester t) {
    BreakoutWorld game = new BreakoutWorld();
    return game.bigBang(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT, Constants.TICK_RATE);
  }
}
