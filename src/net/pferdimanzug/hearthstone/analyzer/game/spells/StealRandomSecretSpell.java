package net.pferdimanzug.hearthstone.analyzer.game.spells;

import java.util.ArrayList;
import java.util.List;

import net.pferdimanzug.hearthstone.analyzer.game.GameContext;
import net.pferdimanzug.hearthstone.analyzer.game.Player;
import net.pferdimanzug.hearthstone.analyzer.game.entities.Entity;
import net.pferdimanzug.hearthstone.analyzer.game.spells.desc.SpellDesc;
import net.pferdimanzug.hearthstone.analyzer.game.spells.trigger.IGameEventListener;
import net.pferdimanzug.hearthstone.analyzer.game.spells.trigger.secrets.Secret;
import net.pferdimanzug.hearthstone.analyzer.game.targeting.EntityReference;

public class StealRandomSecretSpell extends Spell {

	public static SpellDesc create() {
		SpellDesc desc = new SpellDesc(StealRandomSecretSpell.class);
		desc.setTarget(EntityReference.NONE);
		return desc;
	}

	@Override
	protected void onCast(GameContext context, Player player, SpellDesc desc, Entity target) {
		Player opponent = context.getOpponent(player);
		List<IGameEventListener> secrets = context.getLogic().getSecrets(opponent);
		
		if (secrets.isEmpty()) {
			return;
		}
		
		// try to steal a secret which we do not own yet
		List<Secret> validSecrets = new ArrayList<>();
		for (IGameEventListener trigger : secrets) {
			Secret secret = (Secret) trigger;
			if (!player.getSecrets().contains(secret.getSource().getTypeId())) {
				validSecrets.add(secret);
			}
		}
		
		if (!validSecrets.isEmpty()) {
			Secret secret = validSecrets.get(context.getLogic().random(validSecrets.size()));
			secret.setHost(player.getHero());
			secret.setOwner(player.getId());
			player.getSecrets().add(secret.getSource().getTypeId());
			opponent.getSecrets().remove((Integer) secret.getSource().getTypeId());	
		} else {
			// no valid secret to steal; instead destroy one for the opponent at least
			Secret secret = (Secret) secrets.get(context.getLogic().random(secrets.size()));
			context.removeTrigger(secret);
			opponent.getSecrets().remove((Integer) secret.getSource().getTypeId());
		}
		
	}

}
