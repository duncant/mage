/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.abilities.keyword;

import mage.abilities.Ability;
import mage.abilities.common.DiesTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.ReturnSourceFromGraveyardToBattlefieldEffect;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.target.targetpointer.FixedTarget;



public class PersistAbility extends DiesTriggeredAbility {

    public PersistAbility() {
        super(new PersistEffect());
        this.addEffect(new ReturnSourceFromGraveyardToBattlefieldEffect(false, true));
    }

    public PersistAbility(final PersistAbility ability) {
        super(ability);
    }

    @Override
    public PersistAbility copy() {
        return new PersistAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (super.checkTrigger(event, game)) {
            Permanent p = (Permanent) game.getLastKnownInformation(event.getTargetId(), Zone.BATTLEFIELD);
            if (p.getCounters().getCount(CounterType.M1M1) == 0) {
                game.getState().setValue("persist" + getSourceId().toString(), new FixedTarget(p.getId()));
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Persist <i>(When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)</i>";
    }
}

class PersistEffect extends OneShotEffect {

    public PersistEffect() {
        super(Outcome.Benefit);
        this.staticText = "";
    }

    public PersistEffect(final PersistEffect effect) {
        super(effect);
    }

    @Override
    public PersistEffect copy() {
        return new PersistEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        game.addEffect(new PersistReplacementEffect(), source);
        return true;
    }
}

class PersistReplacementEffect extends ReplacementEffectImpl {

    PersistReplacementEffect() {
        super(Duration.OneUse, Outcome.UnboostCreature, false);
        selfScope = true;
        staticText = "return it to the battlefield under its owner's control with a -1/-1 counter on it";
    }

    PersistReplacementEffect(final PersistReplacementEffect effect) {
        super(effect);
    }

    @Override
    public PersistReplacementEffect copy() {
        return new PersistReplacementEffect(this);
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        Permanent permanent = game.getPermanent(event.getTargetId());
        if (permanent != null) {
            permanent.addCounters(CounterType.M1M1.createInstance(), game);
        }
        used = true;
        return false;
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.ENTERS_THE_BATTLEFIELD;
    }

    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        if (event.getTargetId().equals(source.getSourceId())) {
            Object fixedTarget = game.getState().getValue("persist" + source.getSourceId().toString());
            if (fixedTarget instanceof FixedTarget && ((FixedTarget) fixedTarget).getFirst(game, source).equals(source.getSourceId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return false;
    }
}
