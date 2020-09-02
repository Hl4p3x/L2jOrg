// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestExMagicSkillUseGround extends ClientPacket
{
    private static final Logger LOGGER;
    private int _x;
    private int _y;
    private int _z;
    private int _skillId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;
    
    public void readImpl() {
        this._x = this.readInt();
        this._y = this.readInt();
        this._z = this.readInt();
        this._skillId = this.readInt();
        this._ctrlPressed = (this.readInt() != 0);
        this._shiftPressed = (this.readByte() != 0);
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final int level = activeChar.getSkillLevel(this._skillId);
        if (level <= 0) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(this._skillId, level);
        if (skill != null) {
            activeChar.setCurrentSkillWorldPosition(new Location(this._x, this._y, this._z));
            activeChar.setHeading(MathUtil.calculateHeadingFrom(activeChar.getX(), activeChar.getY(), this._x, this._y));
            Broadcast.toKnownPlayers(activeChar, new ValidateLocation(activeChar));
            activeChar.useMagic(skill, null, this._ctrlPressed, this._shiftPressed);
        }
        else {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            RequestExMagicSkillUseGround.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this._skillId, level));
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExMagicSkillUseGround.class);
    }
}
