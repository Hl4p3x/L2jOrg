// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.EnumMap;
import java.util.Map;
import org.l2j.gameserver.enums.ChatType;

public class ChatHandler implements IHandler<IChatHandler, ChatType>
{
    private final Map<ChatType, IChatHandler> _datatable;
    
    private ChatHandler() {
        this._datatable = new EnumMap<ChatType, IChatHandler>(ChatType.class);
    }
    
    @Override
    public void registerHandler(final IChatHandler handler) {
        for (final ChatType type : handler.getChatTypeList()) {
            this._datatable.put(type, handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IChatHandler handler) {
        for (final ChatType type : handler.getChatTypeList()) {
            this._datatable.remove(type);
        }
    }
    
    @Override
    public IChatHandler getHandler(final ChatType chatType) {
        return this._datatable.get(chatType);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static ChatHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final ChatHandler INSTANCE;
        
        static {
            INSTANCE = new ChatHandler();
        }
    }
}
