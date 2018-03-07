/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.event;

import es.upm.disnet.pubmed.experiment.PubMedDiseaseDistanceExperiment;
import es.upm.disnet.pubmed.model.jpa.Term;
import es.upm.disnet.pubmed.service.*;
import es.upm.disnet.pubmed.task.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    PopulateDiseaseTask populateDiseaseTask;
    @Autowired
    PopulateDiseaseClassTask populateDiseaseClassTask;
    @Autowired
    PopulatePhenotypeTask populatePhenotypeTask;
    @Autowired
    PopulatePubMedDocTask populatePubMedDocTask;
    @Autowired
    PopulatePubMedDocAbstractTask populatePubMedDocAbstractTask;
    @Autowired
    PopulatePubMedDocTermsTask populatePubMedDocTermsTask;
    @Autowired
    PopulateDisnetTermsTask populateDisnetTermsTask;
    @Autowired
    PubMedDiseaseDistanceExperiment pubMedDiseaseDistanceExperiment;

    @Autowired
    DiseaseService diseaseService;
    @Autowired
    DiseaseClassService diseaseClassService;
    @Autowired
    TermService termService;
    @Autowired
    PhenotypeService phenotypeService;
    @Autowired
    PubMedDocService pubMedDocService;

    @Value("${pubmed.snapshot:}")
    private String pubMedSnapshot;
    @Value("${disnet.snapshot:}")
    private String disnetSnapshot;

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        String snapshot = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

        pubMedSnapshot = StringUtils.isNotBlank(pubMedSnapshot) ? pubMedSnapshot : snapshot;

        if (diseaseClassService.countAllBySnapshot(pubMedSnapshot) == 0) {
            populateDiseaseClassTask.populateDiseaseClasses(pubMedSnapshot);
        }

        if (diseaseService.countAllBySnapshot(pubMedSnapshot) == 0) {
            populateDiseaseTask.populateDisease(pubMedSnapshot);
        }

        if (phenotypeService.countAllBySnapshot(pubMedSnapshot) == 0) {
            populatePhenotypeTask.populatePhenotype(pubMedSnapshot);
        }

        disnetSnapshot = StringUtils.isNotBlank(pubMedSnapshot) ? pubMedSnapshot : snapshot;

        if (termService.countAllBySource(Term.SOURCE_DISNET, disnetSnapshot) == 0) {
            populateDisnetTermsTask.populateDisnetDocTerms(pubMedSnapshot, disnetSnapshot);
        }

        if (pubMedDocService.countAllBySnapshot(pubMedSnapshot) == 0) {
            populatePubMedDocTask.populatePubMedDoc(pubMedSnapshot);

            populatePubMedDocAbstractTask.populatePubMedDocAbstract(pubMedSnapshot);

            populatePubMedDocTermsTask.populatePubMedDocTerms(pubMedSnapshot);
        }

        pubMedDiseaseDistanceExperiment.runExperiment(pubMedSnapshot, disnetSnapshot);
    }
}