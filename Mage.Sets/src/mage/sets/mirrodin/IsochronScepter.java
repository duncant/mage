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
package mage.sets.mirrodin;

import java.util.UUID;

import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.filter.Filter;
import mage.filter.FilterCard;
import mage.filter.predicate.mageobject.CardTypePredicate;
import mage.filter.predicate.mageobject.ConvertedManaCostPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetCard;
import mage.util.CardUtil;

/**
 *
 * @author LevelX2
 */
public class IsochronScepter extends CardImpl {

    public IsochronScepter(UUID ownerId) {
        super(ownerId, 188, "Isochron Scepter", Rarity.UNCOMMON, new CardType[]{CardType.ARTIFACT}, "{2}");
        this.expansionSetCode = "MRD";

        // Imprint - When Isochron Scepter enters the battlefield, you may exile an instant card with converted mana cost 2 or less from your hand.
        this.addAbility(new EntersBattlefieldTriggeredAbility(new IsochronScepterImprintEffect(), true, "<i>Imprint - </i>"));

        // {2}, {tap}: You may copy the exiled card. If you do, you may cast the copy without paying its mana cost.
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new IsochronScepterCopyEffect(), new GenericManaCost(2));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);

    }

    public IsochronScepter(final IsochronScepter card) {
        super(card);
    }

    @Override
    public IsochronScepter copy() {
        return new IsochronScepter(this);
    }
}

class IsochronScepterImprintEffect extends OneShotEffect {

    private static final FilterCard filter = new FilterCard("instant card with converted mana cost 2 or less from your hand");
    static  {
        filter.add(new CardTypePredicate(CardType.INSTANT));
        filter.add(new ConvertedManaCostPredicate(Filter.ComparisonType.LessThan, 3));
    }

    public IsochronScepterImprintEffect() {
        super(Outcome.Benefit);
        staticText = "you may exile an instant card with converted mana cost 2 or less from your hand";
    }

    public IsochronScepterImprintEffect(IsochronScepterImprintEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (controller != null) {
            if (controller.getHand().size() > 0) {
                TargetCard target = new TargetCard(Zone.HAND, filter);
                if (target.canChoose(source.getSourceId(), source.getControllerId(), game)
                        && controller.choose(Outcome.Benefit, controller.getHand(), target, game)) {
                    Card card = controller.getHand().get(target.getFirstTarget(), game);
                    if (card != null) {
                        controller.moveCardToExileWithInfo(card, source.getSourceId(), sourcePermanent.getLogName() +" (Imprint)", source.getSourceId(), game, Zone.HAND);
                        Permanent permanent = game.getPermanent(source.getSourceId());
                        if (permanent != null) {
                            permanent.imprint(card.getId(), game);
                            permanent.addInfo("imprint", CardUtil.addToolTipMarkTags("[Imprinted card - " + card.getLogName() + "]"));
                        }
                    }
                }
            }
            return true;
        }
        return false;
        
    }

    @Override
    public IsochronScepterImprintEffect copy() {
        return new IsochronScepterImprintEffect(this);
    }

}

class IsochronScepterCopyEffect extends OneShotEffect {

    public IsochronScepterCopyEffect() {
        super(Outcome.Copy);
        this.staticText = "You may copy the exiled card. If you do, you may cast the copy without paying its mana cost";
    }

    public IsochronScepterCopyEffect(final IsochronScepterCopyEffect effect) {
        super(effect);
    }

    @Override
    public IsochronScepterCopyEffect copy() {
        return new IsochronScepterCopyEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Permanent scepter = game.getPermanentOrLKIBattlefield(source.getSourceId());
            if (scepter != null && scepter.getImprinted() != null && !scepter.getImprinted().isEmpty()) {
                Card imprintedInstant = game.getCard(scepter.getImprinted().get(0));
                if (imprintedInstant != null && game.getState().getZone(imprintedInstant.getId()).equals(Zone.EXILED)) {
                    if (controller.chooseUse(outcome, new StringBuilder("Create a copy of ").append(imprintedInstant.getName()).append("?").toString(), game)) {
                        Card copiedCard = game.copyCard(imprintedInstant, source, source.getControllerId());
                        if (copiedCard != null) {
                            game.getExile().add(source.getSourceId(), "",copiedCard);
                            game.getState().setZone(copiedCard.getId(), Zone.EXILED);
                            if (controller.chooseUse(outcome, "Cast the copied card without paying mana cost?", game)) {
                                controller.cast(copiedCard.getSpellAbility(), game, true);
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
