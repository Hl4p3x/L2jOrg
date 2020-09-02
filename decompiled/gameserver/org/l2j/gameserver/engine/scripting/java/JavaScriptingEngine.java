// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.scripting.java;

import java.util.Arrays;
import javax.lang.model.SourceVersion;
import org.l2j.gameserver.engine.scripting.IExecutionContext;
import java.util.Objects;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import org.l2j.gameserver.engine.scripting.AbstractScriptingEngine;

public final class JavaScriptingEngine extends AbstractScriptingEngine
{
    private volatile JavaCompiler compiler;
    
    public JavaScriptingEngine() {
        super("Java Engine", System.getProperty("java.specification.version"), new String[] { "java" });
    }
    
    private void determineCompilerOrThrow() {
        this.compiler = ToolProvider.getSystemJavaCompiler();
        if (Objects.isNull(this.compiler)) {
            throw new IllegalStateException("No JavaCompiler service installed!");
        }
    }
    
    private synchronized void ensureCompilerOrThrow() {
        if (Objects.isNull(this.compiler)) {
            this.determineCompilerOrThrow();
        }
    }
    
    JavaCompiler getCompiler() {
        return this.compiler;
    }
    
    @Override
    public IExecutionContext createExecutionContext() {
        this.ensureCompilerOrThrow();
        return new JavaExecutionContext(this);
    }
    
    @Override
    public String getLanguageName() {
        return "Java";
    }
    
    @Override
    public String getLanguageVersion() {
        this.ensureCompilerOrThrow();
        return Arrays.deepToString(this.compiler.getSourceVersions().toArray(SourceVersion[]::new)).replace("RELEASE_", "");
    }
}
