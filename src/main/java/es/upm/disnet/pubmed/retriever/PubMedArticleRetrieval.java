/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.retriever;

import es.upm.disnet.pubmed.callable.PubMedUriParserCallable;
import es.upm.disnet.pubmed.querybuilder.PubmedXmlQuery;
import es.upm.disnet.pubmed.xmlparser.PubMedEFetchHandler;
import es.upm.disnet.pubmed.xmlparser.PubMedESearchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import reciter.model.pubmed.PubMedArticle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author https://github.com/jl987-Jie
 * <p>
 * See https://github.com/wcmc-its/ReCiter-PubMed-Retrieval-Tool
 */
@Component
public class PubMedArticleRetrieval {

    @Value("${my.service.test.retieval.doc.number}")
    public Integer NUMBER_OF_ARTICLES;

    private final static Logger slf4jLogger = LoggerFactory.getLogger(PubMedArticleRetrieval.class);

    /**
     * Initializes and starts threads that handles the retrieve process. Partition the number of articles
     * into manageable pieces and ask each thread to handle one partition.
     */
    public List<PubMedArticle> retrieve(String pubMedQuery, int numberOfArticles) throws IOException {

        int numberOfPubmedArticles = getNumberOfPubMedArticles(pubMedQuery);

        List<PubMedArticle> pubMedArticles = new ArrayList<>();

        // Changes to original code to handle large results
        //if (numberOfPubmedArticles <= 2000) {
        ExecutorService executor = Executors.newWorkStealingPool();

        // Get the count (number of publications for this rest).
        PubmedXmlQuery pubmedXmlQuery = new PubmedXmlQuery();
        pubmedXmlQuery.setTerm(pubMedQuery);

        slf4jLogger.info("retMax=[" + pubmedXmlQuery.getRetMax() + "], pubMedQuery=[" + pubMedQuery + "], "
                + "numberOfPubmedArticles=[" + numberOfPubmedArticles + "].");

        // Retrieve the publications retMax records at one time and store to disk.
        int currentRetStart = 0;

        List<Callable<List<PubMedArticle>>> callables = new ArrayList<>();

        // Use the retstart value to iteratively fetch all XMLs.
        while (numberOfPubmedArticles > 0) {
            // Get webenv value.
            pubmedXmlQuery.setRetStart(currentRetStart);
            String eSearchUrl = pubmedXmlQuery.buildESearchQuery();

            pubmedXmlQuery.setWevEnv(PubMedESearchHandler.executeESearchQuery(eSearchUrl).getWebEnv());

            // Use the webenv value to retrieve xml.
            String eFetchUrl = pubmedXmlQuery.buildEFetchQuery();
            slf4jLogger.info("eFetchUrl=[" + eFetchUrl + "].");

            callables.add(new PubMedUriParserCallable(new PubMedEFetchHandler(), eFetchUrl));

            // Update the retstart value.
            currentRetStart += pubmedXmlQuery.getRetMax();
            pubmedXmlQuery.setRetStart(currentRetStart);
            numberOfPubmedArticles -= pubmedXmlQuery.getRetMax();
        }

        try {
            executor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            slf4jLogger.error("Unsable to retrieve result using future get.");
                            throw new IllegalStateException(e);
                        }
                    }).forEach(pubMedArticles::addAll);
        } catch (InterruptedException e) {
            slf4jLogger.error("Unable to invoke callable.", e);
        }
        //} else {
        //    throw new IOException("Number of PubMed Articles retrieved " + numberOfPubmedArticles + " exceeded the threshold level 2000");
        //}
        //return pubMedArticles.subList(0, Math.min(pubMedArticles.size(), 100));
        if (numberOfArticles == 0)//Recupera todos
            return pubMedArticles.subList( 0, pubMedArticles.size() );
        else {
            NUMBER_OF_ARTICLES = numberOfArticles;
            return pubMedArticles.subList(0, Math.min(pubMedArticles.size(), NUMBER_OF_ARTICLES));
        }
        //return pubMedArticles.subList( 0, pubMedArticles.size() );
    }

    protected int getNumberOfPubMedArticles(String query) throws IOException {
        PubmedXmlQuery pubmedXmlQuery = new PubmedXmlQuery(query);
        pubmedXmlQuery.setRetMax(1);
        String fullUrl = pubmedXmlQuery.buildESearchQuery(); // build eSearch rest.
        slf4jLogger.info("ESearch Query=[" + fullUrl + "]");

        PubMedESearchHandler pubmedESearchHandler = new PubMedESearchHandler();
        InputStream esearchStream = null;
        try {
            //esearchStream = new URL(fullUrl).openStream();
            esearchStream = tryAgainURLObtain(fullUrl);
        }catch (Exception e3) {
            slf4jLogger.error("Error URL.openStream query=[" + query + "], full url=[" + fullUrl + "]", e3);
        }

        try {
            SAXParserFactory.newInstance().newSAXParser().parse(esearchStream, pubmedESearchHandler);
        } catch (SAXException | ParserConfigurationException e) {
            slf4jLogger.error("Error parsing XML file for rest=[" + query + "], full url=[" + fullUrl + "]", e);
        }
        return pubmedESearchHandler.getCount();
    }


    protected InputStream tryAgainURLObtain(String url){
        InputStream esearchStream = null;
        while (true){
            try {
                esearchStream = new URL(url).openStream();
                break;
            }catch (MalformedURLException e) {
                slf4jLogger.error("Error in tryAgainURLObtain MalformedURLException URL.openStream full url=[" + url + "]", e);

            } catch (UnknownHostException e2) {
                slf4jLogger.error("Error in tryAgainURLObtain UnknownHostException URL.openStream full url=[" + url + "]", e2);
            }catch (IOException e3) {
                slf4jLogger.error("Error in tryAgainURLObtain URL.openStream url=[" + url + "]", e3);
                //e2.printStackTrace();
            }
        }
        return esearchStream;
    }

}
