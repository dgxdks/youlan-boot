package com.youlan.plugin.region.searcher;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

@Slf4j
@Getter
public abstract class AbstractSearcher {
    private final String dbPath;

    public AbstractSearcher(String dbPath) throws IOException {
        this.dbPath = dbPath;
        this.init();
    }

    public abstract void init() throws IOException;

    public abstract Searcher getSearcher();

    public String search(String ip) {
        try {
            return getSearcher().search(ip);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return StrUtil.EMPTY;
        }
    }

    public String search(long ip) {
        try {
            return getSearcher().search(ip);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return StrUtil.EMPTY;
        }
    }
}
