package net.demilich.metastone.game.behaviour.learning;

import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.datasets.iterator.FloatsDataSetIterator;
import org.fusesource.lmdbjni.Database;
import org.fusesource.lmdbjni.Entry;
import org.fusesource.lmdbjni.Env;
import org.fusesource.lmdbjni.Transaction;

import javax.xml.crypto.Data;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by dianchen on 17-1-16.
 */
public class DataUtils {



    public static FloatsDataSetIterator loadDataset(String datasetName, int batchSize) {
        Env env = new Env(TrainingConfig.DATA_LOCATION + datasetName);
        return new FloatsDataSetIterator(new StateActionIterable(env), batchSize);
    }
}

class StateActionIterable implements Iterable<Pair<float[], float[]>> {

    private final Iterator<Pair<float[], float[]>> iterator;

    public StateActionIterable(Env env) {
        this.iterator = new StateActionIterator(env);
    }

    @Override
    public Iterator<Pair<float[], float[]>> iterator() {
        return iterator;
    }
}

class StateActionIterator implements Iterator<Pair<float[], float[]>> {

    private final Iterator<Entry> delegate;
    private final Transaction tx;
    private final Database db;

    public StateActionIterator(Env env) {
        db = env.openDatabase();
        tx = env.createReadTransaction();
        this.delegate = db.iterate(tx);
    }

    @Override
    public boolean hasNext() {
        if (delegate.hasNext()) {
            return true;
        } else {
            tx.abort();
            return false;
        }
    }

    @Override
    public Pair<float[], float[]> next() {
        Entry entry = delegate.next();
        byte[] pair = entry.getValue();
        float[] state = new float[TrainingConfig.STATE_SIZE];
        float[] action = new float[TrainingConfig.ACTION_SIZE];

        ByteArrayInputStream bas = new ByteArrayInputStream(pair);
        DataInputStream ds = new DataInputStream(bas);

        for (int i = 0; i < state.length; i++) {
            try {
                state[i] = ds.readFloat();
            } catch (IOException e) {
                System.out.println("Error when iterating dataset");
                e.printStackTrace();
            }
        }

        for (int i = 0; i < action.length; i++) {
            try {
                action[i] = ds.readFloat();
            } catch (IOException e) {
                System.out.println("Error when iterating dataset");
                e.printStackTrace();
            }
        }

        return new Pair<>(state, action);
    }

}


