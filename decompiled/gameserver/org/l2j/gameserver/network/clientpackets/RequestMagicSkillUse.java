// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestMagicSkillUse extends ClientPacket
{
    private static final Logger LOGGER;
    private int _magicId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;
    
    public void readImpl() {
        this._magicId = this.readInt();
        this._ctrlPressed = (this.readInt() != 0);
        this._shiftPressed = (this.readByte() != 0);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        Skill skill = player.getKnownSkill(this._magicId);
        if (skill == null) {
            if (this._magicId != CommonSkill.HAIR_ACCESSORY_SET.getId() && (this._magicId <= 1565 || this._magicId >= 1570)) {
                player.sendPacket(ActionFailed.STATIC_PACKET);
                if (this._magicId > 0) {
                    RequestMagicSkillUse.LOGGER.warn("Skill Id {} not found in player: {}", (Object)this._magicId, (Object)player);
                }
                return;
            }
            skill = SkillEngine.getInstance().getSkill(this._magicId, 1);
        }
        if (skill.isBlockActionUseSkill()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        player.onActionRequest();
        player.useMagic(skill, null, this._ctrlPressed, this._shiftPressed);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestMagicSkillUse.class);
    }
}
