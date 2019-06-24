package game;

import java.util.*;

public class Model {

    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    public int score, maxTile;
    private boolean isSaveNeeded = true;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();

    public Model() {

        resetGameTiles();
    }

    private void addTile(){
        if (!getEmptyTiles().isEmpty() | getEmptyTiles().size()>0){
            Tile newTile = getEmptyTiles().get((int)(getEmptyTiles().size() * Math.random()));
            newTile.value = (Math.random() < 0.9 ? 2 : 4 );
        }
    }

    private List<Tile> getEmptyTiles(){
        List<Tile> list = new ArrayList<>();
        for(int i = 0; i<FIELD_WIDTH; i++){
            for (int j = 0; j<FIELD_WIDTH; j++){
                if (gameTiles[i][j].value == 0) list.add(gameTiles[i][j]);
            }
        }
        return list;
    }

    public void resetGameTiles(){
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        score = 0;
        maxTile = 0;
        for(int i = 0; i<FIELD_WIDTH; i++){
            for (int j = 0; j<FIELD_WIDTH; j++){
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();

    }

    private boolean compressTiles(Tile[] tiles){
        boolean changes = false;
        for (int i = 0; i<tiles.length; i++){
            if(tiles[i].isEmpty()){
                int j = i;
                while (tiles[j].isEmpty() && j < tiles.length-1){
                    j++;
                }
                if(!tiles[j].isEmpty()) changes = true;
                tiles[i] = tiles[j];
                tiles[j] = new Tile();
            }
        }
        return changes;
    }

    private boolean mergeTiles(Tile[] tiles){
        boolean res = false;
        for(int i=0; i<tiles.length-1; i++){
            if (tiles[i].value == tiles[i+1].value & tiles[i].value !=0){
                tiles[i].value *= 2;
                tiles[i+1].value = 0;
                res = true;
                if(tiles[i].value > maxTile) maxTile = tiles[i].value;
                score += tiles[i].value;
                compressTiles(tiles);
            }
        }
        return res;
    }

    public void left(){
        saveState(gameTiles);
        boolean isChanged = false;
        for(int i = 0; i<FIELD_WIDTH; i++){
            if((compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i]))) isChanged = true;
        }
        if (isChanged) addTile();
        isSaveNeeded = true;
    }

    public void up(){ // поворот на 90 1 раз
        saveState(gameTiles);
        boolean isChanged = false;
        gameTiles = rotate(gameTiles);

        for(int i = 0; i<FIELD_WIDTH; i++){
            if((compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i]))) isChanged = true;
        }
        if (isChanged) addTile();
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);
        isSaveNeeded = true;
    }

    public void right(){
        boolean isChanged = false;
        saveState(gameTiles);
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);

        for(int i = 0; i<FIELD_WIDTH; i++){
            if( mergeTiles(gameTiles[i])|(compressTiles(gameTiles[i]))) isChanged = true;
        }
        if (isChanged) addTile();
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);
        isSaveNeeded = true;
    }

    public void down(){
        saveState(gameTiles);
        boolean isChanged = false;
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);
        gameTiles = rotate(gameTiles);
        for(int i = 0; i<FIELD_WIDTH; i++){
            if((compressTiles(gameTiles[i]) | mergeTiles(gameTiles[i]))) isChanged = true;
        }
        if (isChanged) addTile();
        gameTiles = rotate(gameTiles);
        isSaveNeeded = true;
    }

    private Tile[][] rotate(Tile[][] tiles){//против часовой, первая строка становится первым столбцом,первый столбец становится последней строкой
        Tile[][] rTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i< tiles.length; i++){
            for(int j = 0; j<tiles[i].length; j++){
                rTiles[tiles[i].length-1-j][i] = tiles[i][j];
            }
        }
        return rTiles;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    public boolean canMove(){
        Tile[][] temp = Arrays.copyOf(gameTiles, gameTiles.length);
        if (!getEmptyTiles().isEmpty()) return true;
        for(Tile[] x: temp){
            if (mergeTiles(x)) return true;
        }
        temp = rotate(temp);
        for(Tile[] x: temp){
            if (mergeTiles(x)) return true;
        }
        temp = rotate(temp);
        for(Tile[] x: temp){
            if (mergeTiles(x)) return true;
        }
        temp = rotate(temp);
        for(Tile[] x: temp){
            if (mergeTiles(x)) return true;
        }

        return false;
    }

    private void saveState(Tile[][] tiles){
        Tile[][] matrix = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for(int i = 0; i<FIELD_WIDTH; i++){
            for(int j = 0; j<FIELD_WIDTH; j++){
                matrix[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(matrix);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback(){
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }

    }

    public void randomMove(){
        int n = ((int) (Math.random()*4)); // Зачем  ((int) (Math.random() * 100)) % 4
        switch (n){
            case 0: left(); break;
            case 1: right(); break;
            case 2: up(); break;
            case 3: down(); break;
        }
    }

    public boolean hasBoardChanged(){
        for(int i = 0; i<FIELD_WIDTH; i++){
            for(int j = 0; j < FIELD_WIDTH; j++){
                if(gameTiles[i][j].value != previousStates.peek()[i][j].value) return true;
            }
        }
        return false;
    }

    public MoveEfficiency getMoveEfficiency(Move move){
        MoveEfficiency m;
        move.move();
        if (!hasBoardChanged()) m = new MoveEfficiency(-1, 0, move);
        else m = new MoveEfficiency(getEmptyTiles().size(), score, move);
        rollback();
        return m;
    }

    public void autoMove(){
        PriorityQueue<MoveEfficiency> queue = new PriorityQueue<>(4, Collections.reverseOrder());
        queue.add(getMoveEfficiency(()->left()));
        queue.add(getMoveEfficiency(()->right()));
        queue.add(getMoveEfficiency(()->up()));
        queue.add(getMoveEfficiency(()->down()));

        queue.peek().getMove().move();
    }
}
