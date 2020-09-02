// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.TimeInterpreter;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class AdminCommandHandler implements IHandler<IAdminCommandHandler, String>
{
    private static final Logger LOGGER;
    private final Map<String, IAdminCommandHandler> _datatable;
    
    private AdminCommandHandler() {
        this._datatable = new HashMap<String, IAdminCommandHandler>();
    }
    
    @Override
    public void registerHandler(final IAdminCommandHandler handler) {
        for (final String id : handler.getAdminCommandList()) {
            this._datatable.put(id, handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IAdminCommandHandler handler) {
        for (final String id : handler.getAdminCommandList()) {
            this._datatable.remove(id);
        }
    }
    
    @Override
    public IAdminCommandHandler getHandler(final String adminCommand) {
        String command = adminCommand;
        if (adminCommand.contains(" ")) {
            command = adminCommand.substring(0, adminCommand.indexOf(" "));
        }
        return this._datatable.get(command);
    }
    
    public void useAdminCommand(final Player player, final String fullCommand, final boolean useConfirm) {
        final String command = fullCommand.split(" ")[0];
        final String commandNoPrefix = command.substring(6);
        if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel())) {
            AdminCommandHandler.LOGGER.warn("Player {} tried to use admin command '{}', without proper access level!", (Object)player.getName(), (Object)command);
            return;
        }
        final IAdminCommandHandler handler = this.getHandler(command);
        if (Objects.isNull(handler)) {
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandNoPrefix));
            AdminCommandHandler.LOGGER.warn("No handler registered for admin command '{}'", (Object)command);
            return;
        }
        if (useConfirm && AdminData.getInstance().requireConfirm(command)) {
            player.setAdminConfirmCmd(fullCommand);
            final ConfirmDlg dlg = new ConfirmDlg(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, commandNoPrefix));
            player.addAction(PlayerAction.ADMIN_COMMAND);
            player.sendPacket(dlg);
        }
        else {
            final long begin;
            WorldObject target;
            final IAdminCommandHandler adminCommandHandler;
            long runtime;
            ThreadPool.execute(() -> {
                begin = System.currentTimeMillis();
                try {
                    if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).auditGM()) {
                        target = player.getTarget();
                        GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), player.getObjectId()), fullCommand, (target != null) ? target.getName() : "no-target");
                    }
                    adminCommandHandler.useAdminCommand(fullCommand, player);
                }
                catch (RuntimeException e) {
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, fullCommand, e.toString()));
                    AdminCommandHandler.LOGGER.warn("Exception during execution of {}", (Object)fullCommand, (Object)e);
                }
                finally {
                    runtime = System.currentTimeMillis() - begin;
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, fullCommand, TimeInterpreter.consolidateMillis(runtime)));
                }
            });
        }
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static AdminCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminCommandHandler.class);
    }
    
    private static class Singleton
    {
        private static final AdminCommandHandler INSTANCE;
        
        static {
            INSTANCE = new AdminCommandHandler();
        }
    }
}
