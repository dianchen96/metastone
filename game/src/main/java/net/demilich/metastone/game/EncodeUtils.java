package net.demilich.metastone.game;


import net.demilich.metastone.game.actions.ActionType;
import net.demilich.metastone.game.actions.GameAction;
import net.demilich.metastone.game.cards.Card;
import net.demilich.metastone.game.cards.CardCollection;
import net.demilich.metastone.game.entities.Entity;
import net.demilich.metastone.game.entities.heroes.Hero;
import net.demilich.metastone.game.entities.minions.Minion;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.util.*;


public class EncodeUtils {

    private static final int MAX_DECK_SIZE = 30;
    private static final int MAX_BOARD_SIZE = 7;


    public static FloatBuffer encodeCardList(CardCollection cardList, CardCollection originDeck) {
        Map<String, Integer> indexMap = new HashMap<>();
        float[] tmp = new float[MAX_DECK_SIZE];
        FloatBuffer feature = FloatBuffer.allocate(tmp.length);
        for (Card card : cardList) {
            if (originDeck.containsCard(card)) {
                String cardID = card.getCardId();
                int startIndex = indexMap.containsKey(cardID) ? indexMap.get(cardID)+1 : 0;
                int index = originDeck.getIndex(startIndex, card);
                tmp[index] = 1.0f;
                indexMap.put(cardID, index);
            }
        }

        feature.put(tmp);
        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeMinions(List<Minion> minions, CardCollection originDeck) {
        FloatBuffer feature = FloatBuffer.allocate(MAX_BOARD_SIZE * (MAX_DECK_SIZE + 2));
        int minionIndex = 0;
        for (Minion minion : minions) {
            if (originDeck.containsCard(minion.getSourceCard())) {
                int index = originDeck.getIndex(minion.getSourceCard());
                feature.put(toFloatBuffer(index, MAX_DECK_SIZE));
            } else {
                feature.put(new float[MAX_DECK_SIZE]);
            }

            feature.put(new float[]{(float) minion.getAttack(), (float) minion.getHp()});

            minionIndex++;
        }
        while (minionIndex < MAX_BOARD_SIZE) {
            feature.put(new float[MAX_DECK_SIZE + 2]);
            minionIndex++;
        }

        feature.position(0);

        return feature;
    }

    public static FloatBuffer encodeHero(Hero hero) {
        FloatBuffer feature = FloatBuffer.allocate(3);
        feature.put((float) hero.getEffectiveHp());
        feature.put((float) hero.getAttack());
        if (hero.getWeapon() != null) {
            feature.put((float) hero.getWeapon().getDurability());
        } else {
            feature.put(0.0f);
        }

        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeSecrets(Set<String> secretIDs, CardCollection originDeck) {
        FloatBuffer feature = FloatBuffer.allocate(MAX_DECK_SIZE);
        float[] temp = new float[MAX_DECK_SIZE];
        for (String secretID : secretIDs) {
            if (originDeck.contains(secretID)) {
                int index = originDeck.getIndex(secretID);
                temp[index] = 1.0f;
            }
        }
        feature.put(temp);
        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeOpponentMinions(List<Minion> minions) {
        FloatBuffer feature = FloatBuffer.allocate(MAX_BOARD_SIZE * 2);
        int minionIndex = 0;
        for (Minion minion : minions) {
            feature.put(new float[]{(float) minion.getAttack(), (float) minion.getHp()});
            minionIndex++;
        }
        while (minionIndex < MAX_BOARD_SIZE) {
            feature.put(new float[]{0.0f, 0.0f});
            minionIndex++;
        }

        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeOthers(GameContext context, int playerID) {
        FloatBuffer feature = FloatBuffer.allocate(5);

        /* Get turn num */
        feature.put((float) context.getTurn());

        /* Get opponent card num */
        feature.put((float) context.getPlayer(1 - playerID).getHand().getCount());

        /* Get opponent deck num */
        feature.put((float) context.getPlayer(1 - playerID).getDeck().getCount());

        /* Get opponent secret num */
        feature.put((float) context.getPlayer(1 - playerID).getSecrets().size());

        /* Get remaining mana */
        feature.put((float) context.getPlayer(playerID).getMana());

        feature.position(0);
        return feature;

    }

    public static FloatBuffer encodeActionType(ActionType actionType) {
        List<ActionType> encoding = new ArrayList<ActionType>();
        encoding.add(ActionType.SUMMON);
        encoding.add(ActionType.SPELL);
        encoding.add(ActionType.PHYSICAL_ATTACK);
        encoding.add(ActionType.HERO_POWER);
        encoding.add(ActionType.END_TURN);

        FloatBuffer feature = toFloatBuffer(encoding.indexOf(actionType), encoding.size());
        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeSource(Card card, CardCollection originDeck) {
        if (!originDeck.containsCard(card)) {
            FloatBuffer feature = FloatBuffer.allocate(30);
            for (int i = 0; i < MAX_DECK_SIZE; i++) {
                feature.put(0.0f);
            }

            feature.position(0);
            return feature;
        }

        int index = originDeck.getIndex(card);
        FloatBuffer feature = toFloatBuffer(index, MAX_DECK_SIZE);
        feature.position(0);
        return feature;
    }

    public static FloatBuffer encodeTarget(Entity entity) {
        FloatBuffer feature = FloatBuffer.allocate(1);
        if (entity instanceof Hero) {
            feature.put(1.0f);
        } else {
            feature.put(0.0f);
        }
        feature.position(0);
        return feature;
    }


    private static FloatBuffer toFloatBuffer(int N, int size) {
        FloatBuffer feature = FloatBuffer.allocate(size);
        float[] temp = new float[size];
        temp[N] = 1.0f;
        feature.put(temp);
        feature.position(0);
        return feature;
    }

    private static int binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;

        int b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }
}
