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
import es.upm.disnet.pubmed.model.jpa.Term;
import es.upm.disnet.pubmed.model.rest.MetaMapTermsResponse;
import es.upm.disnet.pubmed.model.rest.TVPValidationResponse;
import es.upm.disnet.pubmed.retriever.MetaMapTermsRetrievalService;
import es.upm.disnet.pubmed.retriever.TVPValidationRetrievalService;
import es.upm.disnet.pubmed.service.DiseaseService;
import es.upm.disnet.pubmed.service.PubMedDocService;
import es.upm.disnet.pubmed.service.TermService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PopulatePubMedDocTermsTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulatePubMedDocTermsTask.class);

    @Autowired
    private PubMedDocService pubMedDocService;

    @Autowired
    private TermService termService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private MetaMapTermsRetrievalService metaMapTermsRetrievalService;

    @Autowired
    private TVPValidationRetrievalService tvpValidationRetrievalService;

    public void populatePubMedDocTerms(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating terms from PubMed for snapshot {}", snapshot);

            watch.start();

            List<Disease> diseases = diseaseService.getAllDiseasesBySnapshot(snapshot);

            for (Disease disease : diseases) {
                try {
                    addPubMedTerms(disease, snapshot);
                } catch (Exception e) {
                    logger.error("Error populating PubMed terms for disease {}", disease.getMeSHUI(), e);
                }
            }

            watch.stop();

            logger.info(
                    "Populating PubMed doc terms finished in {} minutes", watch.getTime(TimeUnit.MINUTES));

        } catch (Exception e) {
            logger.error("Error while populating PubMedDoc terms", e);
        }
    }

    //TODO: Use Multithread and Callable for parallel extraction
    private void addPubMedTerms(Disease disease, String snapshot) throws Exception {
        if (termService.countByDisease(disease, Term.SOURCE_PUBMED) > 0) {
            return;
        }

        String aggregatedAbstracts = pubMedDocService.getPubMedAggregatedAbstractText(disease);

        /*
        // Uncomment to limit the length of the aggregated abstract if terms extraction takes too long
        aggregatedAbstracts = aggregatedAbstracts.substring(0, Math.min(15000, aggregatedAbstracts.length()));
        */

        MetaMapTermsResponse metaMapTermsResponse = null;

        try {
            metaMapTermsResponse = metaMapTermsRetrievalService.getMetaMapTerms(
                    aggregatedAbstracts);
        } catch (Exception e) {
            logger.error("Error obtaining MetaMapTerms for disease {}", disease.getMeSHUI(), e);
        }

        if ((metaMapTermsResponse == null) ||
                (metaMapTermsResponse.getConcepts(0) == null) ||
                (metaMapTermsResponse.getConcepts(0).length == 0)) {

            logger.warn("No MetaMap terms found for disease {}", disease.getMeSHUI());

            return;
        }

        MetaMapTermsResponse.Concept[] metaMapTermConcepts = metaMapTermsResponse.getConcepts(0);

        TVPValidationResponse tvpValidationResponse = null;

        try {
            tvpValidationResponse = tvpValidationRetrievalService.getTVPValidation(
                    metaMapTermConcepts);
        } catch (Exception e) {
            logger.error("Error validating terms for disease {}", disease.getMeSHUI(), e);
        }

        if ((tvpValidationResponse == null) ||
                (tvpValidationResponse.getValidatedConcepts() == null) ||
                (tvpValidationResponse.getValidatedConcepts()).length == 0) {

            logger.warn("No validated terms found for disease {}", disease.getMeSHUI());

            return;
        }

        TVPValidationResponse.ValidatedConcept[] validatedConcepts =
                tvpValidationResponse.getValidatedConcepts();

        addValidatedPubMedTerms(metaMapTermConcepts, validatedConcepts, disease, snapshot);
    }

    private void addValidatedPubMedTerms(
            MetaMapTermsResponse.Concept[] metaMapTermConcepts,
            TVPValidationResponse.ValidatedConcept[] validatedConcepts, Disease disease, String pubmedSnapshot) {

        logger.info("Adding {} validatedTerms to disease {}", metaMapTermConcepts.length, disease.getMeSHUI());

        for (MetaMapTermsResponse.Concept concept : metaMapTermConcepts) {
            Term term = new Term(concept.getCui());

            if (termService.getAllTermsByDisease(disease, Term.SOURCE_PUBMED).contains(term)) {
                continue;
            }

            if (!isValidatedConcept(concept, validatedConcepts)) {
                continue;
            }

            term.setCui(concept.getCui());
            term.setName(concept.getName());
            term.setTypes(concept.getSemanticTypes());
            term.setSnapshot(pubmedSnapshot);
            term.setFrequency(Collections.frequency(Arrays.asList(metaMapTermConcepts), concept));
            term.setValidated(true);
            term.setSource(Term.SOURCE_PUBMED);
            term.setDisease(disease);

            termService.addTerm(term);
        }
    }

    private boolean isValidatedConcept(
            MetaMapTermsResponse.Concept concept, TVPValidationResponse.ValidatedConcept[] validatedConcepts) {

        for (TVPValidationResponse.ValidatedConcept validatedConcept : validatedConcepts) {
            if (validatedConcept.getConcept() != null &&
                    concept.getCui().equals(validatedConcept.getConcept().getCui())) {

                return validatedConcept.getHasMatches();
            }
        }

        return false;
    }


}
