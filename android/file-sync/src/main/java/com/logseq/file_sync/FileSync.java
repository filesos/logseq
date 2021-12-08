package com.logseq.file_sync;

public class FileSync {
    static {
        System.loadLibrary("filesync");
    }

    public static native void watch(final String path);

    public static native void close();
    public static native String ping();
}
