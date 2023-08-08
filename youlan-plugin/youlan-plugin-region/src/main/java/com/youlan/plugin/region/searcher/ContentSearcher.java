package com.youlan.plugin.region.searcher;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

@Slf4j
@Getter
public class ContentSearcher extends AbstractSearcher {
    private byte[] cBuff;
    private Searcher contentSearch;

    public ContentSearcher(String dbPath) throws IOException {
        super(dbPath);
    }

    @Override
    public void init() throws IOException {
        cBuff = Searcher.loadContentFromFile(getDbPath());
        contentSearch = Searcher.newWithBuffer(cBuff);
    }

    @Override
    public Searcher getSearcher() {
        return contentSearch;
    }
}
