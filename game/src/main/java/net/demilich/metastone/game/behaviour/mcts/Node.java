package net.demilich.metastone.game.behaviour.mcts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.PlayRandomBehaviour;

class Node {

	private GameContext state;
	private List<GameAction> validTransitions;
	private final List<Node> children = new LinkedList<>();
	private final GameAction incomingAction;
	private int visits;
	private int score;
	private final int player;

	public Node(GameAction incomingAction, int player) {
		this.incomingAction = incomingAction;
		this.player = player;
	}

	private boolean canFurtherExpanded() {
		return !validTransitions.isEmpty();
	}

	protected Node expand() {
		GameAction action = validTransitions.remove(0);
		GameContext newState = state.clone();


		try {
			newState.getLogic().performGameAction(newState.getActivePlayer().getId(), action);

			// Fixing bug: if action is end-turn, change game-context's turn state property
			if (action.getActionType() == ActionType.END_TURN) {
				newState.startTurn(newState.getActivePlayerId());
			}
		} catch (Exception e) {
			System.err.println("Exception on action: " + action + " state decided: " + state.gameDecided());
			e.printStackTrace();
			throw e;
		}

		Node child = new Node(action, getPlayer());
		child.initState(newState, newState.getValidActions());
//		System.out.println("Created " + child + " " + child.validTransitions);
		children.add(child);
		return child;
	}

	public GameAction getBestAction() {
		GameAction best = null;
		int bestScore = Integer.MIN_VALUE;
		for (Node node : children) {
			if (node.getScore() > bestScore) {
				best = node.incomingAction;
				bestScore = node.getScore();
			}
		}
		return best;
	}

	public List<Node> getChildren() {
		return children;
	}

	public int getPlayer() {
		return player;
	}

	public int getScore() {
		return score;
	}

	public GameContext getState() {
		return state;
	}

	public int getVisits() {
		return visits;
	}

	public List<GameAction> getValidTransitions() {
		return validTransitions;
	}

	public void addChild(Node child) {
		children.add(child);
	}


	public void initState(GameContext state, List<GameAction> validActions) {
		this.state = state.clone();
		this.validTransitions = new ArrayList<GameAction>(validActions);
	}

	public boolean isExpandable() {
		if (validTransitions.isEmpty()) {
			return false;
		}
		if (state.gameDecided()) {
			return false;
		}
		return getChildren().size() < validTransitions.size();
	}

	public boolean isLeaf() {
		return children == null || children.isEmpty();
	}

	private boolean isTerminal() {
		return state.gameDecided();
	}

	public void process(ITreePolicy treePolicy) {
		List<Node> visited = new LinkedList<Node>();
		Node current = this;
		visited.add(this);
		while (!current.isTerminal()) {
			if (current.canFurtherExpanded()) {
				current = current.expand();
				visited.add(current);
				break;
			} else {
				current = treePolicy.select(current);
				visited.add(current);
			}
		}

		int value = rollOut(current);
		int winnerID = value == 0 ? 1 - getPlayer() : getPlayer();
		System.out.println(value + " " + winnerID + " " + getPlayer());
		for (Node node : visited) {
			node.updateStats(value);
//			if (node.getState().getActivePlayerId() == winnerID) {
//				node.updateStats(value);
//			}
		}
	}

	public int rollOut(Node node) {
		if (node.getState().gameDecided()) {
			GameContext state = node.getState();
			return state.getWinningPlayerId() == getPlayer() ? 1 : 0;
		}

		GameContext simulation = node.getState().clone();
		for (Player player : simulation.getPlayers()) {
			player.setBehaviour(new PlayRandomBehaviour());
		}

		simulation.play();

		return simulation.getWinningPlayerId() == getPlayer() ? 1 : 0;
	}

	private void updateStats(int value) {
		visits++;
		score += value;
	}

}
