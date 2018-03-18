/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.parser;

import es.upm.disnet.pubmed.retriever.RetrievalControl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PubMedWebArticleParser {

    private static final Logger logger = LoggerFactory.getLogger(PubMedWebArticleParser.class);

    public String getPubMedDocAbstractText(String url) throws IOException {
        try{
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .timeout(60*1000)
                    .get();

            Element abstractElement = doc.getElementsByTag("abstracttext").first();

            if (abstractElement != null) {
                return abstractElement.text();
            }
        }catch (Exception e){
            logger.error("Error to connect with Url ({}) for PubMed doc {}", url, e);
        }

        return "";
    }

}
