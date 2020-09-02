// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

public interface IScriptingEngine
{
    String setProperty(final String key, final String value);
    
    IExecutionContext createExecutionContext();
    
    String getProperty(final String key);
    
    String getEngineName();
    
    String getEngineVersion();
    
    String getLanguageName();
    
    String getLanguageVersion();
    
    String[] getCommonFileExtensions();
}
