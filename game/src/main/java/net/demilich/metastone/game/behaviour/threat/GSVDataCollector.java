package net.demilich.metastone.game.behaviour.threat;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Env;

import org.fusesource.lmdbjni.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;

public class GSVDataCollector extends GameStateValueBehaviour {

    private static final String DATA_PATH = "/Users/dianchen/metastone/training/data/run_9";
    private static final Logger logger = LoggerFactory.getLogger(GameContext.class);

    private Env env;
    private Database db;
    private int count;

    public GSVDataCollector() {
        super();
        env = new Env(DATA_PATH);
        env.setMapSize(Integer.MAX_VALUE);
        db = env.openDatabase();
        count = (int) db.stat().ms_entries;
    }

    @Override
    public GSVDataCollector clone() {
        return new GSVDataCollector();
    }

    @Override
    public String getName() {
        return "GSV (Data collector)";
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        GameAction chosenAction = super.requestAction(context, player, validActions);

        if (chosenAction.getActionType() != ActionType.BATTLECRY && chosenAction.getActionType() != ActionType.DISCOVER) {

            /* Save the state, action pair */
            FloatBuffer state = context.getEncodedState(player.getId());
            FloatBuffer action = context.getEncodedAction(chosenAction, context, player.getId());

            FloatBuffer pair = FloatBuffer.allocate(state.capacity() + action.capacity());

            pair.put(state);
            pair.put(action);

            pair.position(0);

            ByteBuffer tmp = ByteBuffer.allocate(4 * pair.capacity());
            tmp.asFloatBuffer().put(pair);


            db.put(toByteArray(count++), tmp.array());

            logger.debug(String.format("Saved {} to database", count));

        }

        return chosenAction;
    }

    @Override
    public void onGameOver(GameContext context, int playerId, int winningPlayerId) {
        db.close();
        env.close();
    }

    private static byte[] toByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }
}
