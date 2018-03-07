/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.xmlparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author https://github.com/jl987-Jie
 * <p>
 * See https://github.com/wcmc-its/ReCiter-PubMed-Retrieval-Tool
 * A SAX handler for parsing the ESearch rest from PubMed.
 */
public class PubMedESearchHandler extends DefaultHandler {

    private final static Logger slf4jLogger = LoggerFactory.getLogger(PubMedESearchHandler.class);

    private String webEnv;
    private int count;
    private boolean bWebEnv;
    private boolean bCount;
    private int numCountEncounteredSoFar = 0;

    private StringBuilder chars = new StringBuilder();

    /**
     * Sends a rest to the NCBI web site to retrieve the webEnv.
     *
     * @param eSearchUrl example rest: http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax=1&usehistory=y&term=Kukafka%20R[au].
     * @return WebEnvHandler that contains the WebEnv data.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static PubMedESearchHandler executeESearchQuery(String eSearchUrl) {
        PubMedESearchHandler webEnvHandler = new PubMedESearchHandler();
        InputStream inputStream = null;
        try {
            inputStream = new URL(eSearchUrl).openStream();
        } catch (IOException e) {
            slf4jLogger.error("Error in executeESearchQuery", e);
        }
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(inputStream, webEnvHandler);
        } catch (Exception e) {
            slf4jLogger.error("Error in executeESearchQuery. url=[" + eSearchUrl + "]", e);
        }
        return webEnvHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        chars.setLength(0);

        if (qName.equalsIgnoreCase("WebEnv")) {
            bWebEnv = true;
        }
        if (qName.equalsIgnoreCase("Count") && numCountEncounteredSoFar == 0) {
            numCountEncounteredSoFar++;
            bCount = true;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (bWebEnv) {
            chars.append(ch, start, length);
        }
        if (bCount) {
            chars.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // WebEnv
        if (bWebEnv) {
            webEnv = chars.toString();
            bWebEnv = false;
        }

        // Count.
        if (bCount) {
            count = Integer.parseInt(chars.toString());
            bCount = false;
        }
    }

    public String getWebEnv() {
        return webEnv;
    }

    public int getCount() {
        return count;
    }
}
