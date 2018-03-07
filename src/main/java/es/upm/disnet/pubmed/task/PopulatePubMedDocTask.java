/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.task;

import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import es.upm.disnet.pubmed.retriever.PubMedArticleRetrievalService;
import es.upm.disnet.pubmed.service.DiseaseService;
import es.upm.disnet.pubmed.service.PubMedDocService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reciter.model.pubmed.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PopulatePubMedDocTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulatePubMedDocTask.class);
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private PubMedDocService pubMedDocService;

    @Autowired
    private PubMedArticleRetrievalService pubMedArticleRetrievalService;

    public void populatePubMedDoc(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating PubMed docs for snapshot {}.", snapshot);

            watch.start();

            List<Disease> diseases = diseaseService.getAllDiseasesInDisnet();

            for (Disease disease : diseases) {
                if (pubMedDocService.getAllPubMedDocsByDisease(disease).size() > 0) {
                    continue;
                }

                logger.info("populating docs for id " + disease.getId());

                String pubMedMeshTermQuery =
                        URLEncoder.encode(getPubMedMeshTermQuery(disease.getMeshMH()), StandardCharsets.UTF_8.name());

                try {
                    List<PubMedArticle> pubMedArticles = pubMedArticleRetrievalService.retrieve(pubMedMeshTermQuery);

                    logger.debug(
                            "{} PubMed articles retrieved for disease with MESH UI {} ({})",
                            pubMedArticles.size(), disease.getMeSHUI(), disease.getId());

                    pubMedArticles.stream().forEach(p -> {
                        try {
                            addPubMedDoc(p, disease, snapshot);
                        } catch (Exception e) {
                            logger.error(
                                    "Error adding document {} for disease {}", p, disease.getMeSHUI(), e);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Error in rest {}", pubMedMeshTermQuery, e);
                }
            }

            watch.stop();

            logger.info(
                    "Populating PubMed docs finished in {} minutes. {} PubMed docs were added",
                    watch.getTime(TimeUnit.MINUTES), pubMedDocService.countAll());

        } catch (Exception e) {
            logger.error("Error while populating PubMedDoc table", e);
        }
    }

    private void addPubMedDoc(PubMedArticle pubMedArticle, Disease disease, String snapshot) {
        MedlineCitation medlineCitation = pubMedArticle.getMedlinecitation();

        if (medlineCitation == null) {
            return;
        }

        String pmId = String.valueOf(medlineCitation.getMedlinecitationpmid().getPmid());

        logger.trace("Adding PubMed doc with PM ID {} for Disease with MESH UI {}({})",
                pmId, disease.getMeSHUI(), disease.getId());

        /*
        //Many to Many: Merge diseases in existing PubMed docs

        PubMedDoc pubMedDoc = pubMedDocService.getPubMedDocByPmID(pmId);

        if (pubMedDoc != null) {
            Set<Disease> diseases = pubMedDoc.getDiseases();

            diseases.add(disease);

            pubMedDoc.setDiseases(diseases);

            pubMedDocService.addPubMedDoc(pubMedDoc);

            return;
        }
        */

        PubMedDoc pubMedDoc = new PubMedDoc();

        pubMedDoc.setPmID(pmId);
        pubMedDoc.setSnapshot(snapshot);

        MedlineCitationArticle medlineCitationArticle = medlineCitation.getArticle();

        if (medlineCitationArticle == null) {
            return;
        }

        if ((pubMedArticle.getPubmeddata() != null) && (pubMedArticle.getPubmeddata().getArticleIdList() != null)) {
            pubMedDoc.setPmcID(pubMedArticle.getPubmeddata().getArticleIdList().getPmc());
        }

        pubMedDoc.setTitleText(medlineCitationArticle.getArticletitle());

        if (medlineCitationArticle.getElocationid() != null) {
            pubMedDoc.setDoi(medlineCitationArticle.getElocationid().getElocationid());
        }

        if (medlineCitation.getMeshheadinglist() == null) {
            return;
        }

        pubMedDoc.setMeshTerms(
                medlineCitation.getMeshheadinglist()
                        .stream()
                        .map(o -> o.getDescriptorname().getDescriptorname())
                        .collect(Collectors.toList()));

        List<MedlineCitationArticleAuthor> authorList = medlineCitationArticle.getAuthorlist();

        if (authorList != null) {
            pubMedDoc.setAuthor(
                    authorList
                            .stream()
                            .map(o -> o.getLastname() + "," + o.getForename())
                            .collect(Collectors.toList()));
        }

        MedlineCitationKeywordList medlineCitationKeywordlist = medlineCitation.getKeywordlist();

        if (medlineCitationKeywordlist != null) {
            pubMedDoc.setKeyWords(
                    medlineCitationKeywordlist.getKeywordlist()
                            .stream()
                            .map(o -> o.getKeyword())
                            .collect(Collectors.toList()));
        }

        pubMedDoc.setDisease(disease);

        /*
        // Many to Many

        Set<Disease> diseases = pubMedDoc.getDiseases();

        diseases.add(disease);

        pubMedDoc.setDiseases(diseases);
        */

        pubMedDocService.addPubMedDoc(pubMedDoc);
    }

    private String getPubMedMeshTermQuery(String meshTerm) {
        // To search a MeSH heading as a major topic and turn off the automatic EXPLODE
        // See https://www.nlm.nih.gov/bsd/disted/pubmedtutorial/020_720.html

        return String.format("%s[majr:noexp]", meshTerm);
    }

}
