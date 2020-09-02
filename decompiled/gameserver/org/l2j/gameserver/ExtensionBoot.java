// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.handler.ConditionHandler;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.handler.IVoicedCommandHandler;
import org.l2j.gameserver.handler.UserCommandHandler;
import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.handler.PunishmentHandler;
import org.l2j.gameserver.handler.IPunishmentHandler;
import org.l2j.gameserver.handler.ChatHandler;
import org.l2j.gameserver.handler.IChatHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.handler.ActionShiftHandler;
import org.l2j.gameserver.handler.IActionShiftHandler;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.handler.ActionHandler;
import java.util.ServiceLoader;
import org.l2j.gameserver.handler.IActionHandler;
import org.slf4j.Logger;

public class ExtensionBoot
{
    private static final Logger LOGGER;
    
    static void initializers() {
        final ServiceLoader<IActionHandler> load = ServiceLoader.load(IActionHandler.class);
        final ActionHandler instance = ActionHandler.getInstance();
        Objects.requireNonNull(instance);
        load.forEach(instance::registerHandler);
        final ServiceLoader<IActionShiftHandler> load2 = ServiceLoader.load(IActionShiftHandler.class);
        final ActionShiftHandler instance2 = ActionShiftHandler.getInstance();
        Objects.requireNonNull(instance2);
        load2.forEach(instance2::registerHandler);
        final ServiceLoader<IAdminCommandHandler> load3 = ServiceLoader.load(IAdminCommandHandler.class);
        final AdminCommandHandler instance3 = AdminCommandHandler.getInstance();
        Objects.requireNonNull(instance3);
        load3.forEach(instance3::registerHandler);
        final ServiceLoader<IBypassHandler> load4 = ServiceLoader.load(IBypassHandler.class);
        final BypassHandler instance4 = BypassHandler.getInstance();
        Objects.requireNonNull(instance4);
        load4.forEach(instance4::registerHandler);
        final ServiceLoader<IChatHandler> load5 = ServiceLoader.load(IChatHandler.class);
        final ChatHandler instance5 = ChatHandler.getInstance();
        Objects.requireNonNull(instance5);
        load5.forEach(instance5::registerHandler);
        final ServiceLoader<IPunishmentHandler> load6 = ServiceLoader.load(IPunishmentHandler.class);
        final PunishmentHandler instance6 = PunishmentHandler.getInstance();
        Objects.requireNonNull(instance6);
        load6.forEach(instance6::registerHandler);
        final ServiceLoader<IUserCommandHandler> load7 = ServiceLoader.load(IUserCommandHandler.class);
        final UserCommandHandler instance7 = UserCommandHandler.getInstance();
        Objects.requireNonNull(instance7);
        load7.forEach(instance7::registerHandler);
        final ServiceLoader<IVoicedCommandHandler> load8 = ServiceLoader.load(IVoicedCommandHandler.class);
        final VoicedCommandHandler instance8 = VoicedCommandHandler.getInstance();
        Objects.requireNonNull(instance8);
        load8.forEach(instance8::registerHandler);
        final ServiceLoader<IPlayerActionHandler> load9 = ServiceLoader.load(IPlayerActionHandler.class);
        final PlayerActionHandler instance9 = PlayerActionHandler.getInstance();
        Objects.requireNonNull(instance9);
        load9.forEach(instance9::registerHandler);
        final ServiceLoader<ConditionFactory> load10 = ServiceLoader.load(ConditionFactory.class);
        final ConditionHandler instance10 = ConditionHandler.getInstance();
        Objects.requireNonNull(instance10);
        load10.forEach(instance10::registerFactory);
        final ServiceLoader<IParseBoardHandler> load11 = ServiceLoader.load(IParseBoardHandler.class);
        final CommunityBoardHandler instance11 = CommunityBoardHandler.getInstance();
        Objects.requireNonNull(instance11);
        load11.forEach(instance11::registerHandler);
    }
    
    static void loaders() {
        ServiceLoader.load(Quest.class).forEach(q -> ExtensionBoot.LOGGER.debug("Quest {} Loaded", (Object)q));
        ServiceLoader.load(AbstractScript.class).forEach(s -> ExtensionBoot.LOGGER.debug("Script {} Loaded", (Object)s));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ExtensionBoot.class);
    }
}
