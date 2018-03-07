/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.callable;

import es.upm.disnet.pubmed.xmlparser.PubMedEFetchHandler;
import org.xml.sax.SAXException;
import reciter.model.pubmed.PubMedArticle;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * @author https://github.com/jl987-Jie
 * <p>
 * See https://github.com/wcmc-its/ReCiter-PubMed-Retrieval-Tool
 */
public class PubMedUriParserCallable implements Callable<List<PubMedArticle>> {

    private final PubMedEFetchHandler xmlHandler;
    private final String uri;

    public PubMedUriParserCallable(PubMedEFetchHandler xmlHandler, String uri) {
        this.xmlHandler = xmlHandler;
        this.uri = uri;
    }

    public List<PubMedArticle> parse(String uri) throws ParserConfigurationException, SAXException, IOException {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.parse(uri, xmlHandler);
        return xmlHandler.getPubmedArticles();
    }

    public List<PubMedArticle> call() throws Exception {
        return parse(uri);
    }
}
