package players.groupW;

import core.GameState;
import players.Player;
import players.optimisers.ParameterizedPlayer;
import utils.ElapsedCpuTimer;
import utils.Types;

import java.util.ArrayList;
import java.util.Random;

public class MyPlayer extends ParameterizedPlayer {

    /**
     * Random generator.
     */
    private Random m_rnd;

    /**
     * all possible actions
     */
    public Types.ACTIONS[] actions;

    /**
     * Params for this MCTS
     */
    public MyParams params;

    public MyPlayer(long seed, int pId) {
        this(seed, pId, new MyParams());
    }

    public MyPlayer(long seed, int id, MyParams params){
        super(seed, id, params);
        reset(seed, id);

        ArrayList<Types.ACTIONS> actionsList = Types.ACTIONS.all();
        actions = new Types.ACTIONS[actionsList.size()];
        int i = 0;
        for (Types.ACTIONS act : actionsList) {
            actions[i++] = act;
        }
    }

    @Override
    public void reset(long seed, int playerID) {
        super.reset(seed, playerID);
        m_rnd = new Random(seed);

        this.params = (MyParams) getParameters();
        if (this.params == null) {
            this.params = new MyParams();
            super.setParameters(this.params);
        }
    }

    @Override
    public Types.ACTIONS act(GameState gs) {
        ElapsedCpuTimer ect = new ElapsedCpuTimer();
        ect.setMaxTimeMillis(params.num_time);

        // Number of actions available
        int num_actions = actions.length;

        // Root of the tree
        MyTreeNode m_root = new MyTreeNode(params, m_rnd, num_actions, actions);
        m_root.setRootGameState(gs);

        //Determine the action using MCTS...
        OpponentModel model = new OpponentModel(gs, this);
        m_root.mctsSearch(ect, model);

        //Determine the best action to take and return it.
        int action = m_root.mostVisitedAction();

        // TODO update message memory

        //... and return it.
        return actions[action];
    }

    @Override
    public int[] getMessage() {
        return new int[Types.MESSAGE_LENGTH];
    }

    @Override
    public Player copy() {
        return new MyPlayer(seed, playerID, params);
    }
}
