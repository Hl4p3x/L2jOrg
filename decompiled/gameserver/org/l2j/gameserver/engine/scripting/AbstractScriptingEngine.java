// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting;

import java.util.Arrays;
import java.util.HashMap;
import org.l2j.commons.util.Util;
import java.util.Map;

public abstract class AbstractScriptingEngine implements IScriptingEngine
{
    private final String name;
    private final String version;
    private final String[] fileExtension;
    private final Map<String, String> properties;
    
    protected AbstractScriptingEngine(final String engineName, final String engineVersion, final String... commonFileExtensions) {
        if (Util.isNullOrEmpty((CharSequence)engineName) || Util.isNullOrEmpty((CharSequence)engineVersion) || commonFileExtensions == null || commonFileExtensions.length == 0) {
            throw new IllegalArgumentException();
        }
        this.name = engineName;
        this.version = engineVersion;
        this.fileExtension = commonFileExtensions;
        this.properties = new HashMap<String, String>();
    }
    
    @Override
    public final String setProperty(final String key, final String value) {
        return this.properties.put(key, value);
    }
    
    @Override
    public final String getProperty(final String key) {
        return this.properties.get(key);
    }
    
    @Override
    public final String getEngineName() {
        return this.name;
    }
    
    @Override
    public final String getEngineVersion() {
        return this.version;
    }
    
    @Override
    public final String[] getCommonFileExtensions() {
        return Arrays.copyOf(this.fileExtension, this.fileExtension.length);
    }
}
