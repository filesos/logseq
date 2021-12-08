package com.logseq.app;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.logseq.file_sync.FileSync;

@CapacitorPlugin(name = "GraphFileSync")
public class GraphFileSync extends Plugin {

    @PluginMethod()
    public void watch(PluginCall call) {
        String path = call.getString("path");
        FileSync.watch(path);
        JSObject ret = new JSObject();
        ret.put("path", "watched");
        call.resolve(ret);
    }

    @PluginMethod()
    public void close(PluginCall call) {
        FileSync.close();
        JSObject ret = new JSObject();
        ret.put("value", "closed watcher");
        call.resolve(ret);
    }

    @PluginMethod()
    public void ping(PluginCall call) {
        String res = FileSync.ping();
        JSObject ret = new JSObject();
        ret.put("value", res);
        call.resolve(ret);
    }
}
