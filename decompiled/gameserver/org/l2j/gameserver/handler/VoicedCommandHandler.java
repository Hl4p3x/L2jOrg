// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

public class VoicedCommandHandler implements IHandler<IVoicedCommandHandler, String>
{
    private final Map<String, IVoicedCommandHandler> _datatable;
    
    private VoicedCommandHandler() {
        this._datatable = new HashMap<String, IVoicedCommandHandler>();
    }
    
    @Override
    public void registerHandler(final IVoicedCommandHandler handler) {
        for (final String id : handler.getVoicedCommandList()) {
            this._datatable.put(id, handler);
        }
    }
    
    @Override
    public synchronized void removeHandler(final IVoicedCommandHandler handler) {
        for (final String id : handler.getVoicedCommandList()) {
            this._datatable.remove(id);
        }
    }
    
    @Override
    public IVoicedCommandHandler getHandler(final String voicedCommand) {
        return this._datatable.get(voicedCommand.contains(" ") ? voicedCommand.substring(0, voicedCommand.indexOf(" ")) : voicedCommand);
    }
    
    @Override
    public int size() {
        return this._datatable.size();
    }
    
    public static VoicedCommandHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final VoicedCommandHandler INSTANCE;
        
        static {
            INSTANCE = new VoicedCommandHandler();
        }
    }
}
