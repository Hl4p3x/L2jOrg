// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.mission;

import java.util.function.Consumer;
import java.util.ServiceLoader;
import java.util.Objects;
import java.util.HashMap;
import java.util.function.Function;
import java.util.Map;

public final class MissionEngine
{
    private final Map<String, Function<MissionDataHolder, AbstractMissionHandler>> handlerFactories;
    
    private MissionEngine() {
        this.handlerFactories = new HashMap<String, Function<MissionDataHolder, AbstractMissionHandler>>();
    }
    
    public void registerHandler(final MissionHandlerFactory factory) {
        final Map<String, Function<MissionDataHolder, AbstractMissionHandler>> handlerFactories = this.handlerFactories;
        final String handlerName = factory.handlerName();
        Objects.requireNonNull(factory);
        handlerFactories.put(handlerName, factory::create);
    }
    
    public Function<MissionDataHolder, AbstractMissionHandler> getHandler(final String name) {
        return this.handlerFactories.get(name);
    }
    
    public int size() {
        return this.handlerFactories.size();
    }
    
    public static void init() {
        final ServiceLoader<MissionHandlerFactory> load = ServiceLoader.load(MissionHandlerFactory.class);
        final MissionEngine instance = getInstance();
        Objects.requireNonNull(instance);
        load.forEach(instance::registerHandler);
        MissionData.init();
    }
    
    public static MissionEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final MissionEngine INSTANCE;
        
        static {
            INSTANCE = new MissionEngine();
        }
    }
}
