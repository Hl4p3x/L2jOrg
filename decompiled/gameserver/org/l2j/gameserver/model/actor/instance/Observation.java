// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Npc;

public final class Observation extends Npc
{
    public Observation(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2ObservationInstance);
    }
    
    @Override
    public void showChatWindow(final Player player, final int val) {
        String filename;
        if (MathUtil.isInsideRadius2D(this, -79884, 86529, 50) || MathUtil.isInsideRadius2D(this, -78858, 111358, 50) || MathUtil.isInsideRadius2D(this, -76973, 87136, 50) || MathUtil.isInsideRadius2D(this, -75850, 111968, 50)) {
            if (val == 0) {
                filename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId());
            }
            else {
                filename = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getId(), val);
            }
        }
        else if (val == 0) {
            filename = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getId());
        }
        else {
            filename = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getId(), val);
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        player.sendPacket(html);
    }
}
