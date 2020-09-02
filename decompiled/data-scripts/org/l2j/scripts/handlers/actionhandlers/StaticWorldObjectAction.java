// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class StaticWorldObjectAction implements IActionHandler
{
    public boolean action(final Player activeChar, final WorldObject target, final boolean interact) {
        final StaticWorldObject staticObject = (StaticWorldObject)target;
        if (staticObject.getType() < 0) {
            StaticWorldObjectAction.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, staticObject.getId()));
        }
        if (activeChar.getTarget() != staticObject) {
            activeChar.setTarget((WorldObject)staticObject);
        }
        else if (interact) {
            if (!MathUtil.isInsideRadius2D((ILocational)activeChar, (ILocational)staticObject, 250)) {
                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, new Object[] { staticObject });
            }
            else if (staticObject.getType() == 2) {
                final String filename = (staticObject.getId() == 24230101) ? "data/html/signboards/tomb_of_crystalgolem.htm" : "data/html/signboards/pvp_signboard.htm";
                final String content = HtmCache.getInstance().getHtm(activeChar, filename);
                final NpcHtmlMessage html = new NpcHtmlMessage(staticObject.getObjectId());
                if (content == null) {
                    html.setHtml(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
                }
                else {
                    html.setHtml(content);
                }
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
            }
            else if (staticObject.getType() == 0) {
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)staticObject.getMap() });
            }
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2StaticObjectInstance;
    }
}
