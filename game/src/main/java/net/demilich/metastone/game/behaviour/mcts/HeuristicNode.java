package net.demilich.metastone.game.behaviour.mcts;

import java.util.List;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.heuristic.IGameStateHeuristic;
import net.demilich.metastone.game.behaviour.heuristic.WeightedHeuristic;


public class HeuristicNode extends Node {

    private static final double HEURISTIC_WEIGHT = 1.0;
    private static final IGameStateHeuristic evaluator = new WeightedHeuristic();

    private double heuristics;

    public HeuristicNode(GameAction incomingAction, int player) {
        super(incomingAction, player);
    }

    private static double scaleHeuristics(double heuristics) {
        return heuristics * HEURISTIC_WEIGHT;
    }

    @Override
    public int getScore() {
        return super.getScore() + (int) heuristics;
    }


    @Override
    public void initState(GameContext state, List<GameAction> validActions) {
        super.initState(state, validActions);
        heuristics = scaleHeuristics(evaluator.getScore(getState(), getPlayer()));
    }

    @Override
    protected Node expand() {
        GameAction action = getValidTransitions().remove(0);
        GameContext newState = getState().clone();


        try {
            newState.getLogic().performGameAction(newState.getActivePlayer().getId(), action);

            // Fixing bug: if action is end-turn, change game-context's turn state property
            if (action.getActionType() == ActionType.END_TURN) {
                newState.startTurn(newState.getActivePlayerId());
            }
        } catch (Exception e) {
            System.err.println("Exception on action: " + action + " state decided: " + getState().gameDecided());
            e.printStackTrace();
            throw e;
        }

        HeuristicNode child = new HeuristicNode(action, getPlayer());
        child.initState(newState, newState.getValidActions());
        addChild(child);
        return child;
    }

}
