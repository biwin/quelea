/*
 * This file is part of Quelea, free projection software for churches.
 * 
 * Copyright (C) 2012 Michael Berry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.quelea.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.quelea.displayable.Song;
import org.quelea.utils.LoggerUtils;

/**
 * The search index of songs.
 *
 * @author Michael
 */
public class SongSearchIndex implements SearchIndex<Song> {

    private static final Logger LOGGER = LoggerUtils.getLogger();
    private Analyzer analyzer;
    private Directory index;
    private Map<Integer, Song> songs;

    /**
     * Create a new empty search index.
     */
    public SongSearchIndex() {
        songs = new HashMap<>();
        analyzer = new StandardAnalyzer(Version.LUCENE_35, new HashSet<String>());
        index = new RAMDirectory();
    }

    /**
     * Add a song to the index.
     *
     * @param song the song to add.
     */
    @Override
    public void add(Song song) {
        List<Song> songList = new ArrayList<>();
        songList.add(song);
        addAll(songList);
    }

    /**
     * Add a number of songs to the index. This is much more efficient than
     * calling addSong() repeatedly because it just uses one writer rather than
     * opening and closing one for each individual operation.
     *
     * @param songList the song list to add.
     */
    @Override
    public void addAll(Collection<? extends Song> songList) {
        try (IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(Version.LUCENE_35, analyzer))) {
            for(Song song : songList) {
                Document doc = new Document();
                doc.add(new Field("title", song.getTitle(), Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("lyrics", song.getLyrics(false, false), Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("number", Integer.toString(song.getID()), Field.Store.YES, Field.Index.ANALYZED));
                writer.addDocument(doc);
                songs.put(song.getID(), song);
                LOGGER.log(Level.FINE, "Added song to index: {0}", song.getTitle());
            }
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Couldn't add value to index", ex);
        }
    }

    /**
     * Remove the given song from the index.
     *
     * @param song the song to remove.
     */
    @Override
    public void remove(Song song) {
        try (IndexReader reader = IndexReader.open(index, false)) {
            reader.deleteDocuments(new Term("number", Integer.toString(song.getID())));
        }
        catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Couldn't remove value from index", ex);
        }
    }

    /**
     * Update the given song in the index.
     *
     * @param song the song to update.
     */
    @Override
    public void update(Song song) {
        remove(song);
        add(song);
    }

    /**
     * Search for songs that match the given filter.
     *
     * @param queryString the query to use to search.
     * @param type TITLE or BODY, depending on what to search in. BODY is
     * equivalent to the lyrics, TITLE the title.
     * @return an array of songs that match the filter.
     */
    @Override
    public Song[] filter(String queryString, FilterType type) {
        String sanctifyQueryString = SearchIndexUtils.makeLuceneQuery(queryString);
        if(songs.isEmpty() || sanctifyQueryString.isEmpty()) {
            return songs.values().toArray(new Song[songs.size()]);
        }
        String typeStr;
        if(type == FilterType.BODY) {
            typeStr = "lyrics";
        }
        else if(type == FilterType.TITLE) {
            typeStr = "title";
        }
        else {
            LOGGER.log(Level.SEVERE, "Unknown type: {0}", type);
            return new Song[0];
        }
        List<Song> ret;
        try (IndexSearcher searcher = new IndexSearcher(IndexReader.open(index))) {
            Query q = new ComplexPhraseQueryParser(Version.LUCENE_35, typeStr, analyzer).parse(sanctifyQueryString);
            TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            ret = new ArrayList<>();
            for(int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                Song song = songs.get(Integer.parseInt(d.get("number")));
                ret.add(song);
            }
            if(type == FilterType.BODY) {
                for(Song song : filter(queryString, FilterType.TITLE)) {
                    ret.remove(song);
                }
            }
            return ret.toArray(new Song[ret.size()]);
        }
        catch (ParseException | IOException ex) {
            LOGGER.log(Level.WARNING, "Invalid query string: " + sanctifyQueryString, ex);
            return new Song[0];
        }
    }

    /**
     * Remove everything from this index.
     */
    @Override
    public void clear() {
        SearchIndexUtils.clearIndex(index);
    }

}