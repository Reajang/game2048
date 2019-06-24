package game;

public class MoveEfficiency implements Comparable<MoveEfficiency> {

    private Move move;
    private int numberOfEmptyTiles, score;

    public Move getMove() {
        return move;
    }

    public MoveEfficiency( int numberOfEmptyTiles, int score, Move move) {
        this.move = move;
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
    }

    @Override
    public int compareTo(MoveEfficiency o) {

        if (this.numberOfEmptyTiles > o.numberOfEmptyTiles) return 1;
        else if (this.numberOfEmptyTiles < o.numberOfEmptyTiles) return -1;
        else {
            if (this.score > o.score) return 1;
            else if (this.score < o.score) return -1;
            else  return 0;
        }
    }
}
