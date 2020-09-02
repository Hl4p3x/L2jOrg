// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

public class CronParser
{
    private CronParser() {
    }
    
    public static TaskTable parse(final File file) throws IOException {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return parse(stream);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable t) {}
            }
        }
    }
    
    public static TaskTable parse(final URL url) throws IOException {
        InputStream stream = null;
        try {
            stream = url.openStream();
            return parse(stream);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (Throwable t) {}
            }
        }
    }
    
    public static TaskTable parse(final InputStream stream) throws IOException {
        return parse(new InputStreamReader(stream, "UTF-8"));
    }
    
    public static TaskTable parse(final Reader reader) throws IOException {
        final TaskTable table = new TaskTable();
        final BufferedReader bufferedReader = new BufferedReader(reader);
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    parseLine(table, line);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            reader.close();
        }
        return table;
    }
    
    public static void parseLine(final TaskTable table, String line) throws Exception {
        line = line.trim();
        if (line.isEmpty() || line.charAt(0) == '#') {
            return;
        }
        int size = line.length();
        String pattern = null;
        for (int i = size; i >= 0; --i) {
            final String aux = line.substring(0, i);
            if (SchedulingPattern.validate(aux)) {
                pattern = aux;
                break;
            }
        }
        if (pattern == null) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
        }
        line = line.substring(pattern.length());
        size = line.length();
        final ArrayList<String> splitted = new ArrayList<String>();
        StringBuffer current = null;
        boolean quotes = false;
        for (int j = 0; j < size; ++j) {
            final char c = line.charAt(j);
            if (current == null) {
                if (c == '\"') {
                    current = new StringBuffer();
                    quotes = true;
                }
                else if (c > ' ') {
                    current = new StringBuffer();
                    current.append(c);
                    quotes = false;
                }
            }
            else {
                boolean closeCurrent;
                if (quotes) {
                    closeCurrent = (c == '\"');
                }
                else {
                    closeCurrent = (c <= ' ');
                }
                if (closeCurrent) {
                    if (current.length() > 0) {
                        String str = current.toString();
                        if (quotes) {
                            str = escape(str);
                        }
                        splitted.add(str);
                    }
                    current = null;
                }
                else {
                    current.append(c);
                }
            }
        }
        if (current != null && current.length() > 0) {
            String str2 = current.toString();
            if (quotes) {
                str2 = escape(str2);
            }
            splitted.add(str2);
            current = null;
        }
        size = splitted.size();
        int status = 0;
        String dirString = null;
        File stdinFile = null;
        File stdoutFile = null;
        File stderrFile = null;
        final ArrayList<String> envsList = new ArrayList<String>();
        String command = null;
        final ArrayList<String> argsList = new ArrayList<String>();
        for (int k = 0; k < size; ++k) {
            final String tk = splitted.get(k);
            if (status == 0) {
                if (tk.startsWith("ENV:")) {
                    envsList.add(tk.substring(4));
                    continue;
                }
                if (tk.startsWith("DIR:")) {
                    dirString = tk.substring(4);
                    continue;
                }
                if (tk.startsWith("IN:")) {
                    stdinFile = new File(tk.substring(3));
                    continue;
                }
                if (tk.startsWith("OUT:")) {
                    stdoutFile = new File(tk.substring(4));
                    continue;
                }
                if (tk.startsWith("ERR:")) {
                    stderrFile = new File(tk.substring(4));
                    continue;
                }
                status = 1;
            }
            if (status == 1) {
                if (command == null) {
                    command = tk;
                }
                else {
                    argsList.add(tk);
                }
            }
        }
        if (command == null) {
            throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
        }
        Task task;
        if (command.startsWith("java:")) {
            String className = command.substring(5);
            if (className.isEmpty()) {
                throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
            }
            final int sep = className.indexOf(35);
            String methodName;
            if (sep == -1) {
                methodName = "main";
            }
            else {
                methodName = className.substring(sep + 1);
                className = className.substring(0, sep);
                if (methodName.isEmpty()) {
                    throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line));
                }
            }
            final String[] args = new String[argsList.size()];
            for (int l = 0; l < argsList.size(); ++l) {
                args[l] = argsList.get(l);
            }
            task = new StaticMethodTask(className, methodName, args);
        }
        else {
            final String[] cmdarray = new String[1 + argsList.size()];
            cmdarray[0] = command;
            for (int m = 0; m < argsList.size(); ++m) {
                cmdarray[m + 1] = argsList.get(m);
            }
            String[] envs = null;
            size = envsList.size();
            if (size > 0) {
                envs = new String[size];
                for (int i2 = 0; i2 < size; ++i2) {
                    envs[i2] = envsList.get(i2);
                }
            }
            File dir = null;
            if (dirString != null) {
                dir = new File(dirString);
                if (!dir.exists() || !dir.isDirectory()) {
                    throw new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, line), (Throwable)new FileNotFoundException(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, dirString)));
                }
            }
            final ProcessTask process = new ProcessTask(cmdarray, envs, dir);
            if (stdinFile != null) {
                process.setStdinFile(stdinFile);
            }
            if (stdoutFile != null) {
                process.setStdoutFile(stdoutFile);
            }
            if (stderrFile != null) {
                process.setStderrFile(stderrFile);
            }
            task = process;
        }
        table.add(new SchedulingPattern(pattern), task);
    }
    
    private static String escape(final String str) {
        final int size = str.length();
        final StringBuffer b = new StringBuffer();
        for (int i = 0; i < size; ++i) {
            int skip = 0;
            final char c = str.charAt(i);
            if (c == '\\' && i < size - 1) {
                final char d = str.charAt(i + 1);
                if (d == '\"') {
                    b.append('\"');
                    skip = 2;
                }
                else if (d == '\\') {
                    b.append('\\');
                    skip = 2;
                }
                else if (d == '/') {
                    b.append('/');
                    skip = 2;
                }
                else if (d == 'b') {
                    b.append('\b');
                    skip = 2;
                }
                else if (d == 'f') {
                    b.append('\f');
                    skip = 2;
                }
                else if (d == 'n') {
                    b.append('\n');
                    skip = 2;
                }
                else if (d == 'r') {
                    b.append('\r');
                    skip = 2;
                }
                else if (d == 't') {
                    b.append('\t');
                    skip = 2;
                }
                else if (d == 'u' && i < size - 5) {
                    final String hex = str.substring(i + 2, i + 6);
                    try {
                        final int code = Integer.parseInt(hex, 16);
                        if (code >= 0) {
                            b.append((char)code);
                            skip = 6;
                        }
                    }
                    catch (NumberFormatException ex) {}
                }
            }
            if (skip == 0) {
                b.append(c);
            }
            else {
                i += skip - 1;
            }
        }
        return b.toString();
    }
}
