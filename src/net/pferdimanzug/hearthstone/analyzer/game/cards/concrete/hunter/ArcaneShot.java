package net.pferdimanzug.hearthstone.analyzer.game.cards.concrete.hunter;

import net.pferdimanzug.hearthstone.analyzer.game.GameContext;
import net.pferdimanzug.hearthstone.analyzer.game.Player;
import net.pferdimanzug.hearthstone.analyzer.game.actions.ActionType;
import net.pferdimanzug.hearthstone.analyzer.game.actions.PlayCardAction;
import net.pferdimanzug.hearthstone.analyzer.game.actions.TargetRequirement;
import net.pferdimanzug.hearthstone.analyzer.game.cards.EffectHint;
import net.pferdimanzug.hearthstone.analyzer.game.cards.Rarity;
import net.pferdimanzug.hearthstone.analyzer.game.cards.spells.DamageSpellCard;
import net.pferdimanzug.hearthstone.analyzer.game.heroes.HeroClass;

public class ArcaneShot extends DamageSpellCard {

	public ArcaneShot() {
		super("Arcane Shot", Rarity.FREE, HeroClass.HUNTER, 1, 2);
	}

	@Override
	public PlayCardAction play() {
		return new PlayCardAction(this) {
			{
				setTargetRequirement(TargetRequirement.ANY);
				setActionType(ActionType.SPELL);
				setEffectHint(EffectHint.NEGATIVE);
			}
			
			@Override
			protected void cast(GameContext context, Player player) {
				context.getLogic().damage(getTarget(), getDamage());
			}
		};
	}

}
