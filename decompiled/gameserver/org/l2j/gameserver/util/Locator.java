// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import java.util.Locale;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public final class Locator
{
    private Locator() {
    }
    
    public static File getClassSource(final Class<?> c) {
        final String classResource = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, c.getName().replace('.', '/'));
        return getResourceSource(c.getClassLoader(), classResource);
    }
    
    public static File getResourceSource(ClassLoader c, final String resource) {
        if (c == null) {
            c = Locator.class.getClassLoader();
        }
        URL url = null;
        if (c == null) {
            url = ClassLoader.getSystemResource(resource);
        }
        else {
            url = c.getResource(resource);
        }
        if (url != null) {
            final String u = url.toString();
            if (u.startsWith("jar:file:")) {
                final int pling = u.indexOf("!");
                final String jarName = u.substring(4, pling);
                return new File(fromURI(jarName));
            }
            if (u.startsWith("file:")) {
                final int tail = u.indexOf(resource);
                final String dirName = u.substring(0, tail);
                return new File(fromURI(dirName));
            }
        }
        return null;
    }
    
    public static String fromURI(String uri) {
        URL url = null;
        try {
            url = new URL(uri);
        }
        catch (MalformedURLException ex) {}
        if (url == null || !"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Can only handle valid file: URIs");
        }
        final StringBuffer buf = new StringBuffer(url.getHost());
        if (buf.length() > 0) {
            buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
        }
        final String file = url.getFile();
        final int queryPos = file.indexOf(63);
        buf.append((queryPos < 0) ? file : file.substring(0, queryPos));
        uri = buf.toString().replace('/', File.separatorChar);
        if (File.pathSeparatorChar == ';' && uri.startsWith("\\") && uri.length() > 2 && Character.isLetter(uri.charAt(1)) && uri.lastIndexOf(58) > -1) {
            uri = uri.substring(1);
        }
        final String path = decodeUri(uri);
        return path;
    }
    
    private static String decodeUri(final String uri) {
        if (uri.indexOf(37) == -1) {
            return uri;
        }
        final StringBuffer sb = new StringBuffer();
        final CharacterIterator iter = new StringCharacterIterator(uri);
        for (char c = iter.first(); c != '\uffff'; c = iter.next()) {
            if (c == '%') {
                final char c2 = iter.next();
                if (c2 != '\uffff') {
                    final int i1 = Character.digit(c2, 16);
                    final char c3 = iter.next();
                    if (c3 != '\uffff') {
                        final int i2 = Character.digit(c3, 16);
                        sb.append((char)((i1 << 4) + i2));
                    }
                }
            }
            else {
                sb.append(c);
            }
        }
        final String path = sb.toString();
        return path;
    }
    
    public static File getToolsJar() {
        boolean toolsJarAvailable = false;
        try {
            Class.forName("com.sun.tools.javac.Main");
            toolsJarAvailable = true;
        }
        catch (Exception e) {
            try {
                Class.forName("sun.tools.javac.Main");
                toolsJarAvailable = true;
            }
            catch (Exception ex) {}
        }
        if (toolsJarAvailable) {
            return null;
        }
        String javaHome = System.getProperty("java.home");
        if (javaHome.toLowerCase(Locale.US).endsWith("jre")) {
            javaHome = javaHome.substring(0, javaHome.length() - 4);
        }
        final File toolsJar = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, javaHome));
        if (!toolsJar.exists()) {
            System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, toolsJar.getPath()));
            return null;
        }
        return toolsJar;
    }
    
    public static URL[] getLocationURLs(final File location) throws MalformedURLException {
        return getLocationURLs(location, new String[] { ".jar" });
    }
    
    public static URL[] getLocationURLs(final File location, final String[] extensions) throws MalformedURLException {
        URL[] urls = new URL[0];
        if (!location.exists()) {
            return urls;
        }
        if (!location.isDirectory()) {
            urls = new URL[] { null };
            final String path = location.getPath();
            for (final String extension : extensions) {
                if (path.toLowerCase().endsWith(extension)) {
                    urls[0] = location.toURI().toURL();
                    break;
                }
            }
            return urls;
        }
        final int length2;
        int k = 0;
        String extension2;
        final File[] matches = location.listFiles((dir, name) -> {
            length2 = extensions.length;
            while (k < length2) {
                extension2 = extensions[k];
                if (name.toLowerCase().endsWith(extension2)) {
                    return true;
                }
                else {
                    ++k;
                }
            }
            return false;
        });
        urls = new URL[matches.length];
        for (int i = 0; i < matches.length; ++i) {
            urls[i] = matches[i].toURI().toURL();
        }
        return urls;
    }
}
