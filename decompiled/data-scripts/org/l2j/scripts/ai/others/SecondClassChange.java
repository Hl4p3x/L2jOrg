// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others;

import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.classchange.ExRequestClassChangeUi;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import org.l2j.scripts.ai.AbstractNpcAI;

public class SecondClassChange extends AbstractNpcAI
{
    private static final int LEVEL_REQUIREMENT = 40;
    
    @RegisterEvent(EventType.ON_PLAYER_LEVEL_CHANGED)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLevelChanged(final OnPlayerLevelChanged event) {
        if (Config.DISABLE_TUTORIAL) {
            return;
        }
        final Player player = event.getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getLevel() < 40 || !CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, player.getClassId().getId())) {
            return;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(final OnPlayerLogin event) {
        if (Config.DISABLE_TUTORIAL) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (player.getLevel() < 40 || !CategoryManager.getInstance().isInCategory(CategoryType.SECOND_CLASS_GROUP, player.getClassId().getId())) {
            return;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)ExRequestClassChangeUi.STATIC_PACKET });
    }
    
    public static void main(final String[] args) {
        new SecondClassChange();
    }
}
