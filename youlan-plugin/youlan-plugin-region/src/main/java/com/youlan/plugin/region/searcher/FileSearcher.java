package com.youlan.plugin.region.searcher;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

@Slf4j
public class FileSearcher extends AbstractSearcher {

    public FileSearcher(String dbPath) throws IOException {
        super(dbPath);
    }

    @Override
    public Searcher getSearcher() {
        try {
            return Searcher.newWithFileOnly(getDbPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {

    }
}
