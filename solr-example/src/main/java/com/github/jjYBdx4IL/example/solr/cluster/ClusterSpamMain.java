package com.github.jjYBdx4IL.example.solr.cluster;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ClusterSpamMain {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterSpamMain.class);

    SolrClient solr = null;
    static final int ZK_PORT = 2181;
    static final String COLLECTION = "gettingstarted";
    String[] words = null;
    Random r = new Random(0);

    public static void main(String[] args) {
        try {
            new ClusterSpamMain().run();
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
        LOG.info("done");
        System.exit(0);
    }

    public void run() throws SolrServerException, IOException {
        String zkHostString = "localhost:" + ZK_PORT + ",localhost:" + (ZK_PORT + 1) + ",localhost:"
            + (ZK_PORT + 2);
        CloudSolrClient cloudSolr = new CloudSolrClient.Builder().withZkHost(zkHostString).build();
        cloudSolr.setDefaultCollection(COLLECTION);
        cloudSolr.setParser(new XMLResponseParser());
        solr = cloudSolr;

        try (InputStream is = getWordsFileInputStream()) {
            String wordsContent = IOUtils.toString(is, "UTF-8");
            words = wordsContent.split("\\r?\\n");
        }

        LOG.info("read " + words.length + " words");

        solr.deleteByQuery("*");
        solr.commit();

        for (int i = 1; i >= 0; i++) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", "" + i);
            document.addField("title", getText(1 + r.nextInt(10)));
            document.addField("content", getText(3 + r.nextInt(2000)));
            solr.add(document);

            if (i % 100 == 0) {
                LOG.info("" + i);
            }
        }
        
        // no need for commit because we configured an autocommit max delay
    }

    private InputStream getWordsFileInputStream() throws FileNotFoundException {
        File f = new File(getClass().getResource("/simplelogger.properties").toExternalForm().substring(5));
        f = f.getParentFile().getParentFile();
        f = new File(f, "words.txt");
        return new FileInputStream(f);
    }

    public String getText(int numWords) {
        StringBuilder sb = new StringBuilder(numWords * 5);
        for (int i = 0; i < numWords; i++) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(words[r.nextInt(words.length)]);
        }
        return sb.toString();
    }
}
