package net.demilich.metastone.game.behaviour.learning;

import net.demilich.metastone.game.GameContext;
import net.demilich.metastone.game.Player;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.behaviour.Behaviour;
import net.demilich.metastone.game.behaviour.GreedyOptimizeTurn;
import net.demilich.metastone.game.cards.Card;

import org.deeplearning4j.datasets.iterator.FloatsDataSetIterator;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PolicyGradientBehaviour extends Behaviour {

    private final Logger logger = LoggerFactory.getLogger(PolicyGradientBehaviour.class);

    private static final int BATCHSIZE = 64;
    private static final String trainingPrefix = "train";
    private static final String validationPrefix = "test";

    private ComputationGraph model;

    public PolicyGradientBehaviour(String deckName) throws IOException {
        File location = new File(TrainingConfig.MODEL_LOCATION + deckName);
        if (location.exists()) {
            /* Load trained model */
            model = ModelSerializer.restoreComputationGraph(location);

        } else {
            /* If model does not exist, train the model */
            String trainingSetName = trainingPrefix + "_" + deckName;
            String validationSetName = validationPrefix + "_" + deckName;
            trainModel(location, trainingSetName, validationSetName);
        }
    }

    private void trainModel(File modelLocationToSave, String trainingSet, String validationSet) {
        FloatsDataSetIterator trainingIterator = DataUtils.loadDataset(trainingSet, BATCHSIZE);
        FloatsDataSetIterator validationIterator = DataUtils.loadDataset(validationSet, BATCHSIZE);




    }


    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Card> mulligan(GameContext context, Player player, List<Card> cards) {
        return null;
    }

    @Override
    public GameAction requestAction(GameContext context, Player player, List<GameAction> validActions) {
        return null;
    }
}
