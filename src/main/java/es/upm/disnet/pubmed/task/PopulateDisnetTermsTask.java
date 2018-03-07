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
import es.upm.disnet.pubmed.model.jpa.Term;
import es.upm.disnet.pubmed.model.rest.DisnetResponse;
import es.upm.disnet.pubmed.retriever.DisnetRetrievalService;
import es.upm.disnet.pubmed.service.DiseaseService;
import es.upm.disnet.pubmed.service.TermService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PopulateDisnetTermsTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulateDisnetTermsTask.class);

    @Autowired
    private TermService termService;

    @Autowired
    private DisnetRetrievalService disnetRetrievalService;

    @Autowired
    private DiseaseService diseaseService;

    public void populateDisnetDocTerms(String pubMedSnapshot, String disnetSnapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating terms from Disnet snapshot {} for PubMedsnapshot {}", disnetSnapshot, pubMedSnapshot);

            watch.start();

            List<Disease> diseases = diseaseService.getAllDiseasesBySnapshot(pubMedSnapshot);

            for (Disease disease : diseases) {
                try {
                    addDisnetTerms(disease, disnetSnapshot);
                } catch (Exception e) {
                    logger.error("Error populating terms for disease {}", disease.getMeSHUI(), e);
                }
            }

            watch.stop();

            logger.info(
                    "Populating Disnet terms finished in {} minutes", watch.getTime(TimeUnit.MINUTES));

        } catch (Exception e) {
            logger.error("Error while populating Disnet terms", e);
        }
    }

    private void addDisnetTerms(Disease disease, String disnetSnapshot) throws Exception {
        if (termService.countByDisease(disease, Term.SOURCE_DISNET) > 0) {
            return;
        }

        DisnetResponse.Concept[] disnetTermConcepts = null;

        try {
            disnetTermConcepts = disnetRetrievalService.getDisnetTerms(
                    DisnetRetrievalService.DISEASE_CODE_MESH, disease.getMeSHUI(), disnetSnapshot);
        } catch (Exception e) {
            logger.error(
                    "Error obtaining DisnetTerms for disease with MeSHUI {}", disease.getMeSHUI(), e);
        }

        if ((disnetTermConcepts == null) || (disnetTermConcepts.length == 0)) {
            try {
                disnetTermConcepts = disnetRetrievalService.getDisnetTerms(
                        DisnetRetrievalService.DISEASE_CODE_OMIM, disease.getOmim(), disnetSnapshot);
            } catch (Exception e) {
                logger.error(
                        "Error obtaining DisnetTerms for disease with OMIM {}", disease.getOmim(), e);
            }
        }

        if ((disnetTermConcepts == null) || (disnetTermConcepts.length == 0)) {
            try {
                disnetTermConcepts = disnetRetrievalService.getDisnetTerms(
                        DisnetRetrievalService.DISEASE_CODE_ICD10, disease.getIcd10CM(), disnetSnapshot);
            } catch (Exception e) {
                logger.error(
                        "Error obtaining DisnetTerms for disease with ICD-10 {}", disease.getIcd10CM(), e);
            }
        }

        if ((disnetTermConcepts == null) || (disnetTermConcepts.length == 0)) {
            logger.error("No DisNET terms found for disease {}", disease.getMeSHUI());

            disease.setDisnet(false);
        } else {
            addDisnetTerms(disnetTermConcepts, disease, disnetSnapshot);

            disease.setDisnet(true);
        }

        diseaseService.addDisease(disease);
    }

    private void addDisnetTerms(
            DisnetResponse.Concept[] disnetTermConcepts, Disease disease, String snapshot) {

        logger.info("Adding {} validatedTerms to disease {}", disnetTermConcepts.length, disease.getMeSHUI());

        for (DisnetResponse.Concept concept : disnetTermConcepts) {
            Term term = new Term(concept.getCui());

            if (termService.getAllTermsByDisease(disease, Term.SOURCE_DISNET).contains(term)) {
                continue;
            }

            term.setCui(concept.getCui());
            term.setName(concept.getName());
            term.setTypes(concept.getSemanticTypes());
            term.setSnapshot(snapshot);
            term.setFrequency(concept.getDetectionInformation().getTimesFoundInTexts());
            term.setValidated(true);
            term.setSource(Term.SOURCE_DISNET);
            term.setDisease(disease);

            termService.addTerm(term);
        }
    }
}
