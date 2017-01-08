package net.demilich.metastone.game.behaviour.mcts;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;

import java.util.List;

public class HeuristicMonteCarloTreeSearch extends MonteCarloTreeSearch {

    private static final int ITERATIONS = 500;

    @Override
    public String getName() {
        return "HMCTS";
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        if (validActions.size() == 1) {
            // logger.info("MCTS selected best action {}", validActions.get(0));
            return validActions.get(0);
        }
		HeuristicNode root = new HeuristicNode(null, player.getId());
        root.initState(context, validActions);
        UctPolicy treePolicy = new UctPolicy();
        for (int i = 0; i < ITERATIONS; i++) {
            root.process(treePolicy);
        }
        GameAction bestAction = root.getBestAction();
        // logger.info("MCTS selected best action {}", bestAction);
        return bestAction;
    }
}
