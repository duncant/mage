/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.guildpact;

import java.util.UUID;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.MageInt;
import mage.cards.CardImpl;

import mage.abilities.Ability;
import mage.abilities.SpellAbility;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.FilterSpell;
import mage.filter.FilterPermanent;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.Spell;
import mage.target.Target;
import mage.abilities.Modes;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.cards.Card;

import mage.filter.FilterObject;
import mage.target.TargetObject;
import mage.MageObject;
import mage.game.Controllable;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author duncancmt
 */
public class InkTreaderNephilim extends CardImpl {

    public InkTreaderNephilim(UUID ownerId) {
        super(ownerId, 117, "Ink-Treader Nephilim", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{R}{G}{W}{U}");
        this.expansionSetCode = "GPT";
        this.subtype.add("Nephilim");
        this.color.setRed(true);
        this.color.setGreen(true);
        this.color.setWhite(true);
        this.color.setBlue(true);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Whenever a player casts an instant or sorcery spell, if that spell targets only Ink-Treader Nephilim, copy the spell for each other creature that spell could target. Each copy targets a different one of those creatures.
        this.addAbility(new InkTreaderNephilimTriggeredAbility());
    }

    public InkTreaderNephilim(final InkTreaderNephilim card) {
        super(card);
    }

    @Override
    public InkTreaderNephilim copy() {
        return new InkTreaderNephilim(this);
    }
}

class InkTreaderNephilimTriggeredAbility extends TriggeredAbilityImpl {

    private static final FilterSpell filter = new FilterSpell();

    static {
        filter.add(Predicates.or(
                new CardTypePredicate(CardType.INSTANT),
                new CardTypePredicate(CardType.SORCERY)));
    }

    InkTreaderNephilimTriggeredAbility() {
        super(Zone.BATTLEFIELD, new InkTreaderNephilimEffect(), false);
    }

    InkTreaderNephilimTriggeredAbility(final InkTreaderNephilimTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public InkTreaderNephilimTriggeredAbility copy() {
        return new InkTreaderNephilimTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event.getType() == GameEvent.EventType.SPELL_CAST) {
            Spell spell = game.getStack().getSpell(event.getTargetId());
            if (spell != null &&
                spell.getCardType().contains(CardType.INSTANT) || spell.getCardType().contains(CardType.SORCERY)){
                for (Effect effect : getEffects()) {
                    effect.setValue("TriggeringSpell", spell);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkInterveningIfClause(Game game) {
        Spell spell = (Spell) getEffects().get(0).getValue("TriggeringSpell");
        if (spell != null) {
            boolean allTargetsInkTreaderNephilim = true;
            boolean atLeastOneTargetsInkTreaderNephilim = false;
            for (SpellAbility sa: spell.getSpellAbilities()){
                Modes modes = sa.getModes();
                for (UUID mode : modes.getSelectedModes()) {
                    for (Target targetInstance : modes.get(mode).getTargets()) {
                        for (UUID target : targetInstance.getTargets()) {
                            allTargetsInkTreaderNephilim &= target.equals(sourceId);
                            atLeastOneTargetsInkTreaderNephilim |= target.equals(sourceId);
                        }
                    }
                }
            }
            if (allTargetsInkTreaderNephilim && atLeastOneTargetsInkTreaderNephilim) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever a player casts an instant or sorcery spell, if that spell targets only Ink-Treader Nephilim, copy the spell for each other creature that spell could target. Each copy targets a different one of those creatures.";
    }
}

class InkTreaderNephilimEffect extends OneShotEffect {

    private static final FilterPermanent filter = new FilterPermanent();

    static {
        filter.add(new CardTypePredicate(CardType.CREATURE));
    }

    public InkTreaderNephilimEffect() {
        super(Outcome.Copy);
    }

    public InkTreaderNephilimEffect(final InkTreaderNephilimEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Spell spell = (Spell) getValue("TriggeringSpell");
        if (spell != null) {
            Set<Spell> copies = new HashSet<>();
            UUID controller = source.getControllerId();
            for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, controller, source.getSourceId(), game)) {
                Spell copy = spell.copySpell();                
                copy.setControllerId(controller);
                if (permanent.getId().equals(source.getSourceId())) {
                    continue; // copy only for other creatures
                }
                boolean legal = true;
                for (SpellAbility sa : copy.getSpellAbilities()) {
                    Modes modes = sa.getModes();
                    for (UUID mode : modes.getSelectedModes()) {
                        for (Target targetInstance : modes.get(mode).getTargets()) {
                            legal &= targetInstance.canTarget(permanent.getId(), sa, game);
                        }
                    }
                }
                if (legal) {
                    for (SpellAbility sa : copy.getSpellAbilities()) {
                        Modes modes = sa.getModes();
                        for (UUID mode : modes.getSelectedModes()) {
                            for (Target targetInstance : modes.get(mode).getTargets()) {
                                int numTargets = targetInstance.getNumberOfTargets();
                                targetInstance.clearChosen();
                                while (numTargets > 0) {
                                    targetInstance.add(permanent.getId(), game);
                                    numTargets--;
                                }
                            }
                        }
                    }
                    copies.add(copy);
                    game.getState().setZone(copy.getId(), Zone.PICK);
                }
            }
            TargetPickObject<Spell> target = new TargetPickObject(copies.size(),
                                                                  new FilterSpell("Copy to put on the stack"),
                                                                  copies);
            target.setNotTarget(true);
            Map<UUID, Spell> copyMap = new HashMap<>();
            for (Spell s : copies) {
                copyMap.put(s.getId(), s);
            }
            if (target.canChoose(source.getSourceId(), controller, game)) {
                game.getPlayer(controller).choose(Outcome.Neutral, target, source.getId(), game);
                for (UUID chosenCopyId : target.getTargets()) {
                    if (copyMap.containsKey(chosenCopyId)) {
                        Spell chosenCopy = copyMap.get(chosenCopyId);
                        chosenCopy.moveToZone(Zone.STACK, source.getSourceId(), game, true);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public InkTreaderNephilimEffect copy() {
        return new InkTreaderNephilimEffect(this);
    }

}


class TargetPickObject<E extends Controllable> extends TargetObject {

    protected FilterObject filter;
    protected Map<UUID, E> picks;

    // All constructors take a Set picks as an argument because we cannot
    // enumerate the objects in the "picks" zone. Any object that is not a
    // member of the Set picks cannot be a target, regardless of its zone.
    public TargetPickObject(Set<E> picks) {
        this(1, 1, new FilterObject(""), picks);
    }

    public TargetPickObject(FilterObject filter, Set<E> picks) {
        this(1, 1, filter, picks);
    }

    public TargetPickObject(int numTargets, FilterObject filter, Set<E> picks) {
        this(numTargets, numTargets, filter, picks);
    }

    public TargetPickObject(int minNumTargets, int maxNumTargets, FilterObject filter, Set<E> picks) {
        this.minNumberOfTargets = minNumTargets;
        this.maxNumberOfTargets = maxNumTargets;
        this.zone = Zone.PICK;
        this.filter = filter;
        this.targetName = filter.getMessage();
        this.picks = new HashMap<>();
        for (E pickObject : picks) {
            this.picks.put(pickObject.getId(), pickObject);
        }
    }

    public TargetPickObject(final TargetPickObject target) {
        super(target);
        this.filter = target.filter.copy();
    }

    @Override
    public FilterObject getFilter() {
        return filter;
    }

    @Override
    public boolean canTarget(UUID id, Ability source, Game game) {
        if (id != null && picks.containsKey(id) && game.getState().getZone(id).match(zone)) {
            E pickObject = picks.get(id);
            return filter.match(pickObject, game);
        }
        return false;
    }

    @Override
    public boolean canChoose(UUID sourceId, UUID sourceControllerId, Game game) {
        return canChoose(sourceControllerId, game);
    }

    @Override
    public boolean canChoose(UUID sourceControllerId, Game game) {
        int count = 0;
        for (UUID pickObjectId : picks.keySet()) {
            E pickObject = picks.get(pickObjectId);
            if (game.getPlayer(sourceControllerId).getInRange().contains(pickObject.getControllerId()) &&
                game.getState().getZone(pickObjectId).match(zone) &&
                filter.match(pickObject, game)) {
                count++;
                if (count >= this.minNumberOfTargets) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<UUID> possibleTargets(UUID sourceId, UUID sourceControllerId, Game game) {
        return possibleTargets(sourceControllerId, game);
    }

    @Override
    public Set<UUID> possibleTargets(UUID sourceControllerId, Game game) {
        Set<UUID> possibleTargets = new HashSet<>();
        for (UUID pickObjectId : picks.keySet()) {
            E pickObject = picks.get(pickObjectId);
            if (game.getPlayer(sourceControllerId).getInRange().contains(pickObject.getControllerId()) &&
                game.getState().getZone(pickObjectId).match(zone) &&
                filter.match(pickObject, game)) {
                possibleTargets.add(pickObject.getId());
            }
        }
        return possibleTargets;
    }

    @Override
    public TargetPickObject copy() {
        return new TargetPickObject(this);
    }

}
        
