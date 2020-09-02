// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

import java.util.Map;
import java.nio.file.Path;

public interface IExecutionContext
{
    String setProperty(final String key, final String value);
    
    Map<Path, Throwable> executeScripts(final Iterable<Path> sourcePaths) throws Exception;
    
    Map.Entry<Path, Throwable> executeScript(final Path sourcePath) throws Exception;
    
    String getProperty(final String key);
    
    Path getCurrentExecutingScript();
    
    IScriptingEngine getScriptingEngine();
}
