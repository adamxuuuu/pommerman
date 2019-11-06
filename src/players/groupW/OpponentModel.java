package players.groupW;

import core.GameState;
import objects.Bomb;
import objects.GameObject;
import utils.Types;
import utils.Vector2d;

import java.util.ArrayList;

public class OpponentModel {

    // observable game state
    private GameState gs;

    private MyPlayer myPlayer;

    // enemies to observe
    private ArrayList<GameObject> enemies;
    private ArrayList<Opponent> opponents;

    public OpponentModel(GameState gameState, MyPlayer myPlayer) {
        this.gs = gameState;
        this.myPlayer = myPlayer;
    }

    Types.ACTIONS[] estimateActions(){

        ArrayList<Types.TILETYPE> enemiesObs = gs.getAliveEnemyIDs();
        Types.ACTIONS[] actions = new Types.ACTIONS[gs.getEnemies().length];

        // get game board
        Types.TILETYPE[][] board = gs.getBoard();

        int[][] bombBlastStrength = gs.getBombBlastStrength();
        int[][] bombLife = gs.getBombLife();

        ArrayList<Bomb> bombs = new ArrayList<>();
        enemies = new ArrayList<>();
        opponents = new ArrayList<>();

        // TODO generate board wrt the player
        int boardSizeX = board.length;
        int boardSizeY = board[0].length;

        for (int x = 0; x < boardSizeX; x++) {
            for (int y = 0; y < boardSizeY; y++) {

                Types.TILETYPE type = board[y][x];

                if(type == Types.TILETYPE.BOMB || bombBlastStrength[y][x] > 0){
                    // Create bomb object
                    Bomb bomb = new Bomb();
                    bomb.setPosition(new Vector2d(x, y));
                    bomb.setBlastStrength(bombBlastStrength[y][x]);
                    bomb.setLife(bombLife[y][x]);
                    bombs.add(bomb);
                }
                else if(Types.TILETYPE.getAgentTypes().contains(type) &&
                        type.getKey() != gs.getPlayerId()){ // May be an enemy
                    if(enemiesObs.contains(type)) { // Is enemy
                        // Create enemy object
                        GameObject enemy = new GameObject(type);
                        enemy.setPosition(new Vector2d(x, y));
                        enemies.add(enemy); // no copy needed

                    }
                }
            }
        }

        for (GameObject enemy : enemies) {
            Vector2d pos = enemy.getPosition();
            Opponent opponent = new Opponent(board, pos, bombs, findEnemies(enemy));
            opponents.add(opponent);
        }

        for (int i = 0; i < opponents.size(); i++) {
            actions[i] = opponents.get(i).getAction();
        }

        for (int i = 0; i < actions.length; i++) {
            if(actions[i] == null){
                actions[i] = Types.ACTIONS.ACTION_STOP;
            }
        }
        //System.out.println(Arrays.toString(actions));
        return actions;
    }

    private ArrayList<GameObject> findEnemies(GameObject self){

        ArrayList<GameObject> res = new ArrayList<>();
        for (GameObject enemy : enemies) {
            if(!enemy.getPosition().equals(self.getPosition())){
                res.add(enemy);
            }
        }
        return res;
    }
}
