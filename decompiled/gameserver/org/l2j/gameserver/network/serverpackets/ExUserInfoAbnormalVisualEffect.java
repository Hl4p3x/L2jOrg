// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExUserInfoAbnormalVisualEffect extends ServerPacket
{
    private final Player _activeChar;
    
    public ExUserInfoAbnormalVisualEffect(final Player cha) {
        this._activeChar = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_INFO_ABNORMAL_VISUAL_EFFECT);
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._activeChar.getTransformationId());
        final Set<AbnormalVisualEffect> abnormalVisualEffects = this._activeChar.getEffectList().getCurrentAbnormalVisualEffects();
        final boolean isInvisible = this._activeChar.isInvisible();
        this.writeInt(abnormalVisualEffects.size() + (isInvisible ? 1 : 0));
        for (final AbnormalVisualEffect abnormalVisualEffect : abnormalVisualEffects) {
            this.writeShort((short)abnormalVisualEffect.getClientId());
        }
        if (isInvisible) {
            this.writeShort((short)AbnormalVisualEffect.STEALTH.getClientId());
        }
    }
}
