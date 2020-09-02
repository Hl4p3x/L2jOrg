// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.tasks.player;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.slf4j.Logger;

public final class IllegalPlayerActionTask implements Runnable
{
    private static final Logger LOGGER;
    private final String _message;
    private final IllegalActionPunishmentType _punishment;
    private final Player _actor;
    
    public IllegalPlayerActionTask(final Player actor, final String message, final IllegalActionPunishmentType punishment) {
        this._message = message;
        this._punishment = punishment;
        this._actor = actor;
        switch (punishment) {
            case KICK: {
                this._actor.sendMessage("You will be kicked for illegal action, GM informed.");
                break;
            }
            case KICKBAN: {
                if (!this._actor.isGM()) {
                    this._actor.setAccessLevel(-1, false, true);
                    this._actor.setAccountAccesslevel(-1);
                }
                this._actor.sendMessage("You are banned for illegal action, GM informed.");
                break;
            }
            case JAIL: {
                this._actor.sendMessage("Illegal action performed!");
                this._actor.sendMessage("You will be teleported to GM Consultation Service area and jailed.");
                break;
            }
        }
    }
    
    @Override
    public void run() {
        IllegalPlayerActionTask.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/enums/IllegalActionPunishmentType;)Ljava/lang/String;, this._message, this._actor, this._punishment));
        if (!this._actor.isGM()) {
            switch (this._punishment) {
                case BROADCAST: {}
                case KICK: {
                    Disconnection.of(this._actor).defaultSequence(false);
                    break;
                }
                case KICKBAN: {
                    PunishmentManager.getInstance().startPunishment(new PunishmentTask(this._actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.BAN, System.currentTimeMillis() + Config.DEFAULT_PUNISH_PARAM * 1000, this._message, this.getClass().getSimpleName()));
                    break;
                }
                case JAIL: {
                    PunishmentManager.getInstance().startPunishment(new PunishmentTask(this._actor.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + Config.DEFAULT_PUNISH_PARAM * 1000, this._message, this.getClass().getSimpleName()));
                    break;
                }
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger("audit");
    }
}
