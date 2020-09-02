// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

public class ProcessTask extends Task
{
    private String[] command;
    private String[] envs;
    private File directory;
    private File stdinFile;
    private File stdoutFile;
    private File stderrFile;
    
    public ProcessTask(final String[] command, final String[] envs, final File directory) {
        this.stdinFile = null;
        this.stdoutFile = null;
        this.stderrFile = null;
        this.command = command;
        this.envs = envs;
        this.directory = directory;
    }
    
    public ProcessTask(final String[] command, final String[] envs) {
        this(command, envs, null);
    }
    
    public ProcessTask(final String[] command) {
        this(command, null, null);
    }
    
    public ProcessTask(final String command) {
        this(new String[] { command }, null, null);
    }
    
    private static String listStrings(final String[] arr) {
        if (arr == null) {
            return "null";
        }
        final StringBuffer b = new StringBuffer();
        b.append('[');
        for (int i = 0; i < arr.length; ++i) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(arr[i]);
        }
        b.append(']');
        return b.toString();
    }
    
    @Override
    public boolean canBeStopped() {
        return true;
    }
    
    public String[] getCommand() {
        return this.command;
    }
    
    public void setCommand(final String[] command) {
        this.command = command;
    }
    
    public String[] getEnvs() {
        return this.envs;
    }
    
    public void setEnvs(final String[] envs) {
        this.envs = envs;
    }
    
    public File getDirectory() {
        return this.directory;
    }
    
    public void setDirectory(final File directory) {
        this.directory = directory;
    }
    
    public File getStdinFile() {
        return this.stdinFile;
    }
    
    public void setStdinFile(final File stdinFile) {
        this.stdinFile = stdinFile;
    }
    
    public File getStdoutFile() {
        return this.stdoutFile;
    }
    
    public void setStdoutFile(final File stdoutFile) {
        this.stdoutFile = stdoutFile;
    }
    
    public File getStderrFile() {
        return this.stderrFile;
    }
    
    public void setStderrFile(final File stderrFile) {
        this.stderrFile = stderrFile;
    }
    
    @Override
    public void execute(final TaskExecutionContext context) throws RuntimeException {
        Process p;
        try {
            p = this.exec();
        }
        catch (IOException e) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()), (Throwable)e);
        }
        final InputStream in = this.buildInputStream(this.stdinFile);
        final OutputStream out = this.buildOutputStream(this.stdoutFile);
        final OutputStream err = this.buildOutputStream(this.stderrFile);
        if (in != null) {
            final StreamBridge b = new StreamBridge(in, p.getOutputStream());
            b.start();
        }
        if (out != null) {
            final StreamBridge b = new StreamBridge(p.getInputStream(), out);
            b.start();
        }
        if (err != null) {
            final StreamBridge b = new StreamBridge(p.getErrorStream(), err);
            b.start();
        }
        int r;
        try {
            r = p.waitFor();
        }
        catch (InterruptedException e2) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()));
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Throwable t) {}
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (Throwable t2) {}
            }
            if (err != null) {
                try {
                    err.close();
                }
                catch (Throwable t3) {}
            }
            p.destroy();
        }
        if (r != 0) {
            throw new RuntimeException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.toString(), r));
        }
    }
    
    private Process exec() throws IOException {
        final Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec(this.command, this.envs, this.directory);
        }
        catch (NoSuchMethodError e) {
            p = rt.exec(this.command, this.envs);
        }
        return p;
    }
    
    private InputStream buildInputStream(final File file) {
        if (file != null) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    private OutputStream buildOutputStream(final File file) {
        if (file != null) {
            try {
                return new FileOutputStream(file);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer b = new StringBuffer();
        b.append("Task[");
        b.append("cmd=");
        b.append(listStrings(this.command));
        b.append(", env=");
        b.append(listStrings(this.envs));
        b.append(", ");
        b.append("dir=");
        b.append(this.directory);
        b.append("]");
        return b.toString();
    }
}
