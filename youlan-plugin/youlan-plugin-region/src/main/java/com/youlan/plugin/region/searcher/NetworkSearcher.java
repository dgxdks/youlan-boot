package com.youlan.plugin.region.searcher;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.util.HashMap;

public class NetworkSearcher extends AbstractSearcher {
    public static final String SEARCHER_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    public NetworkSearcher(String dbPath) throws IOException {
        super(dbPath);
    }

    @Override
    public void init() throws IOException {

    }

    @Override
    public Searcher getSearcher() {
        return null;
    }

    @Override
    public String search(String ip) {
        //{"ip":"1.1.1.1","pro":"","proCode":"999999","city":"","cityCode":"0","region":"","regionCode":"0","addr":"
        //美国APNIC&CloudFlare公共DNS服务器","regionNames":"","err":"noprovince"}
        String response = HttpUtil.get(SEARCHER_URL, new HashMap<>() {{
            put("ip" , ip);
            put("json" , "true");
        }});
        JSONObject responseJson = JSONUtil.parseObj(response);
        String pro = responseJson.getStr("pro");
        String city = responseJson.getStr("city");
        return StrUtil.format("{} {}" , pro, city);
    }

    @Override
    public String search(long ip) {
        return search(Ipv4Util.longToIpv4(ip));
    }
}
