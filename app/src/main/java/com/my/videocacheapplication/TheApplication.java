package com.my.videocacheapplication;

import android.app.Application;

import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.headers.HeaderInjector;

import java.util.HashMap;
import java.util.Map;


public class TheApplication extends Application {


    private static TheApplication theApplication;

    private static HttpProxyCacheServer httpProxyCacheServer;


    @Override
    public void onCreate() {
        super.onCreate();
        theApplication = this;
    }


    public static synchronized HttpProxyCacheServer getProxy() {
        if (httpProxyCacheServer == null) {
            httpProxyCacheServer = new HttpProxyCacheServer.Builder(theApplication).headerInjector(new UserAgentHeadersInjector())
                    .maxCacheSize(1024 * 1024 * 1024)// 1 Gb for cache
                    .singleFileBandwidth(600)//单位KB,https需要1.5倍左右
                    .build();
        }
        return httpProxyCacheServer;
    }


    public static TheApplication getTheApplication() {
        return theApplication;
    }

    public static class UserAgentHeadersInjector implements HeaderInjector {

        @Override
        public Map addHeaders(String url) {
            Map<String, String> headerParams = new HashMap<String, String>();
            headerParams.put("referer","ossassets");
            return headerParams;
        }
    }
}
