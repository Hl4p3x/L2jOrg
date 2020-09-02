// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;

class StreamBridge
{
    static ArrayList<StreamBridge> traced;
    final StreamBridge myself;
    final InputStream in;
    final OutputStream out;
    private final Thread thread;
    
    public StreamBridge(final InputStream in, final OutputStream out) {
        this.myself = this;
        this.in = in;
        this.out = out;
        this.thread = new Thread(new Runner());
        synchronized (StreamBridge.traced) {
            StreamBridge.traced.add(this);
        }
    }
    
    public void start() {
        this.thread.start();
    }
    
    public void abort() {
        this.thread.interrupt();
        try {
            this.out.close();
        }
        catch (Throwable t) {}
        try {
            this.in.close();
        }
        catch (Throwable t2) {}
    }
    
    public void join() throws InterruptedException {
        this.thread.join();
    }
    
    public void join(final long millis) throws InterruptedException {
        this.thread.join(millis);
    }
    
    public void join(final long millis, final int nanos) throws IllegalArgumentException, InterruptedException {
        this.thread.join(millis, nanos);
    }
    
    public boolean isAlive() {
        return this.thread.isAlive();
    }
    
    static {
        StreamBridge.traced = new ArrayList<StreamBridge>();
    }
    
    private class Runner implements Runnable
    {
        public Runner() {
        }
        
        @Override
        public void run() {
            boolean skipout = false;
            while (true) {
                int b;
                try {
                    b = StreamBridge.this.in.read();
                }
                catch (IOException e) {
                    if (!Thread.interrupted()) {
                        e.printStackTrace();
                    }
                    break;
                }
                if (b == -1) {
                    break;
                }
                if (skipout) {
                    continue;
                }
                try {
                    StreamBridge.this.out.write(b);
                }
                catch (IOException e) {
                    if (!Thread.interrupted()) {
                        e.printStackTrace();
                    }
                    skipout = true;
                }
            }
            try {
                StreamBridge.this.out.close();
            }
            catch (Throwable t) {}
            try {
                StreamBridge.this.in.close();
            }
            catch (Throwable t2) {}
            synchronized (StreamBridge.traced) {
                StreamBridge.traced.remove(StreamBridge.this.myself);
            }
        }
    }
}
