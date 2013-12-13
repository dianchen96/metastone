package net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.neutral;

import net.pferdimanzug.hearthstone.analyzer.game.cards.MinionCard;
import net.pferdimanzug.hearthstone.analyzer.game.cards.Rarity;
import net.pferdimanzug.hearthstone.analyzer.game.heroes.HeroClass;
import net.pferdimanzug.hearthstone.analyzer.game.minions.Minion;

public class BoulderfistOgre extends MinionCard {

	public BoulderfistOgre() {
		super("Boulderfist Ogre", Rarity.FREE, HeroClass.ANY, 6);
	}

	@Override
	public Minion summon() {
		return createMinion(6, 7);
	}

}
