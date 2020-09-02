// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.net.InetAddress;

class GUIDGenerator
{
    private static String MACHINE_DESCRIPTOR;
    
    public static String generate() {
        final StringBuffer id = new StringBuffer();
        encode(id, GUIDGenerator.MACHINE_DESCRIPTOR);
        encode(id, Runtime.getRuntime());
        encode(id, Thread.currentThread());
        encode(id, System.currentTimeMillis());
        encode(id, getRandomInt());
        return id.toString();
    }
    
    private static String getMachineDescriptor() {
        final StringBuffer descriptor = new StringBuffer();
        descriptor.append(System.getProperty("os.name"));
        descriptor.append("::");
        descriptor.append(System.getProperty("os.arch"));
        descriptor.append("::");
        descriptor.append(System.getProperty("os.version"));
        descriptor.append("::");
        descriptor.append(System.getProperty("user.name"));
        descriptor.append("::");
        final StringBuffer b = buildNetworkInterfaceDescriptor();
        if (b != null) {
            descriptor.append(b);
        }
        else {
            try {
                final InetAddress addr = InetAddress.getLocalHost();
                descriptor.append(addr.getHostAddress());
            }
            catch (UnknownHostException ex) {}
        }
        return descriptor.toString();
    }
    
    private static StringBuffer buildNetworkInterfaceDescriptor() {
        Enumeration<?> e1;
        try {
            e1 = NetworkInterface.getNetworkInterfaces();
        }
        catch (Throwable t) {
            return null;
        }
        final StringBuffer b = new StringBuffer();
        while (e1.hasMoreElements()) {
            final NetworkInterface ni = (NetworkInterface)e1.nextElement();
            final StringBuffer b2 = getMACAddressDescriptor(ni);
            final StringBuffer b3 = getInetAddressDescriptor(ni);
            final StringBuffer b4 = new StringBuffer();
            if (b2 != null) {
                b4.append(b2);
            }
            if (b3 != null) {
                if (b4.length() > 0) {
                    b4.append('=');
                }
                b4.append(b3);
            }
            if (b4.length() > 0) {
                if (b.length() > 0) {
                    b.append(';');
                }
                b.append(b4);
            }
        }
        return b;
    }
    
    private static StringBuffer getMACAddressDescriptor(final NetworkInterface ni) {
        byte[] haddr;
        try {
            haddr = ni.getHardwareAddress();
        }
        catch (Throwable t) {
            haddr = null;
        }
        final StringBuffer b = new StringBuffer();
        if (haddr != null) {
            for (final byte element : haddr) {
                if (b.length() > 0) {
                    b.append("-");
                }
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    b.append('0');
                }
                b.append(hex);
            }
        }
        return b;
    }
    
    private static StringBuffer getInetAddressDescriptor(final NetworkInterface ni) {
        final StringBuffer b = new StringBuffer();
        final Enumeration<?> e2 = ni.getInetAddresses();
        while (e2.hasMoreElements()) {
            final InetAddress addr = (InetAddress)e2.nextElement();
            if (b.length() > 0) {
                b.append(',');
            }
            b.append(addr.getHostAddress());
        }
        return b;
    }
    
    private static int getRandomInt() {
        return (int)Math.round(Math.random() * 2.147483647E9);
    }
    
    private static void encode(final StringBuffer b, final Object obj) {
        encode(b, obj.hashCode());
    }
    
    private static void encode(final StringBuffer b, final int value) {
        final String hex = Integer.toHexString(value);
        for (int hexSize = hex.length(), i = 8; i > hexSize; --i) {
            b.append('0');
        }
        b.append(hex);
    }
    
    private static void encode(final StringBuffer b, final long value) {
        final String hex = Long.toHexString(value);
        for (int hexSize = hex.length(), i = 16; i > hexSize; --i) {
            b.append('0');
        }
        b.append(hex);
    }
    
    static {
        GUIDGenerator.MACHINE_DESCRIPTOR = getMachineDescriptor();
    }
}
