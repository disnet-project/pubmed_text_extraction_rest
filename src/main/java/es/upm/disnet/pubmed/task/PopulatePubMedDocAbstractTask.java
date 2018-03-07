/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.task;

import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import es.upm.disnet.pubmed.parser.PubMedWebArticleParser;
import es.upm.disnet.pubmed.service.PubMedDocService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PopulatePubMedDocAbstractTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulatePubMedDocAbstractTask.class);

    @Autowired
    private PubMedDocService pubMedDocService;

    @Autowired
    private PubMedWebArticleParser pubMedWebArticleParser;

    public void populatePubMedDocAbstract(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating PubMed doc abstracts for snapshot {}.", snapshot);

            watch.start();

            final int pageLimit = 1000;
            int pageNumber = 0;

            Page<PubMedDoc> page = pubMedDocService.getPubMedDocsBySnapshot(
                    snapshot, new PageRequest(pageNumber, pageLimit));

            while (page.hasNext()) {
                addPubMedDocsAbstractText(page.getContent());

                page = pubMedDocService.getPubMedDocsBySnapshot(snapshot, (new PageRequest(++pageNumber, pageLimit)));

                logger.info("Page {}", pageNumber);
            }

            // process last page
            addPubMedDocsAbstractText(page.getContent());

            watch.stop();

            logger.info(
                    "Populating PubMed doc abstracts finished in {} minutes", watch.getTime(TimeUnit.MINUTES));

        } catch (Exception e) {
            logger.error("Error while populating PubMedDoc abstracts", e);
        }

        /*
        // Alternative for update

        ScrollableResults userResults = session.createCriteria(User.class)
                .add(ge("id", 5L))
                .addOrder(asc("id"))
                .setFirstResult(1) // = offset
                .setMaxResults(10) // = limit
                .setFetchSize(1)
                .scroll(ScrollMode.FORWARD_ONLY);
        while (userResults.next()) {
            User user = (User) userResults.get(0);
            System.out.println(user);
        }
        System.out.println("List finished.");


        StatelessSession session = sessionFactory.openStatelessSession();
        Transaction tx = session.beginTransaction();

        ScrollableResults customers = session.getNamedQuery("GetCustomers")
                .scroll(ScrollMode.FORWARD_ONLY);
        while ( customers.next() ) {
            Customer customer = (Customer) customers.get(0);
            customer.updateStuff(...);
            session.update(customer);
        }

        tx.commit();
        session.close();
        */
    }

    private void addPubMedDocsAbstractText(List<PubMedDoc> pubMedDocs) {
        for (PubMedDoc pubMedDoc : pubMedDocs) {
            if (pubMedDoc.getAbstractText() != null) {
                return;
            }

            try {
                logger.debug(
                        "Retrieving abstract for PubMed doc {} in url {})",
                        pubMedDoc.getPmID(), pubMedDoc.getPmArticleURL());

                String abstractText = pubMedWebArticleParser.getPubMedDocAbstractText(pubMedDoc.getPmArticleURL());

                logger.debug("Abstract retrieved {})", abstractText);

                pubMedDoc.setAbstractText(abstractText);

                pubMedDocService.addPubMedDoc(pubMedDoc);
            } catch (Exception e) {
                logger.error("Error populating abstract for PubMed doc  {}", pubMedDoc.getPmID(), e);
            }
        }
    }

}
