package com.youlan.plugin.region.searcher;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

@Slf4j
@Getter
public class VectorIndexSearcher extends AbstractSearcher {
    private byte[] vIndex;

    public VectorIndexSearcher(String dbPath) throws IOException {
        super(dbPath);
    }

    @Override
    public Searcher getSearcher() {
        try {
            return Searcher.newWithVectorIndex(getDbPath(), vIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() throws IOException {
        vIndex = Searcher.loadVectorIndexFromFile(getDbPath());
    }
}
