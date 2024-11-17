package it.uniroma3.lucene.searchEngineLucene.listener;

import it.uniroma3.lucene.searchEngineLucene.service.Indexer;
import it.uniroma3.lucene.searchEngineLucene.service.utils.PropUtil;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.io.IOException;
import java.nio.file.Paths;
@WebListener
public class SearcherInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            if(Boolean.parseBoolean(PropUtil.getProperty("indexing.enabled"))) {
                Indexer.index();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to index", e);
        }

        try {
            Directory indexDirectory = FSDirectory.open(Paths.get(PropUtil.getProperty("lucene.index.path")));
            IndexReader reader = DirectoryReader.open(indexDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(reader);
            sce.getServletContext().setAttribute("indexSearcher", indexSearcher);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize indexSearcher", e);
        }
    }

}