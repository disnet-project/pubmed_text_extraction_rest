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
import es.upm.disnet.pubmed.parser.DiseaseOntologyOBOParser;
import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser.Record;
import es.upm.disnet.pubmed.parser.MeSHASCIIParser;
import es.upm.disnet.pubmed.service.DiseaseService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PopulateDiseaseTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulateDiseaseTask.class);
    // View https://meshb.nlm.nih.gov/treeView
    private final static List<String> EXCLUDED_MESH_MN = Arrays.asList(new String[]{"C21", "C22", "C23", "C24", "C25", "C26"});
    @Autowired
    private DiseaseService diseaseService;

    public void populateDisease(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating diseases for snapshot {}.", snapshot);

            watch.start();

            // Obtain MeSH records

            Path meshPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("mesh/d2018.bin").toURI());

            MeSHASCIIParser meSHASCIIParser = new MeSHASCIIParser();

            final List<Record> meshRecords = meSHASCIIParser.parse(meshPath, StandardCharsets.UTF_8.name());

            // Obtain DO records

            Path doPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("do/HumanDO.obo").toURI());

            DiseaseOntologyOBOParser diseaseOntologyOBOParser = new DiseaseOntologyOBOParser();

            final List<Record> doRecords = diseaseOntologyOBOParser.parse(doPath, StandardCharsets.UTF_8.name());

            // Populate Disease repository with diseases in DO that have MeSH terms

            List<Record> testList = doRecords.stream().filter(o -> o.hasPropertyValue("xref", "MESH")).collect(Collectors.toList());

            testList.size();

            //TODO: extract to constants

            doRecords.stream().filter(o -> o.hasPropertyValue("xref", "MESH:D")).forEach(
                    o -> {
                        String meshUI = o.getPropertyValue("xref", "MESH:");

                        Optional<Record> optMeshRecord = meshRecords.stream().filter(m -> m.getId().equals(meshUI)).findFirst();

                        if (!optMeshRecord.isPresent()) {
                            logger.error("Mesh Record not found for UI {}", meshUI);

                            return;
                        }

                        try {
                            addDisease(o, optMeshRecord.get(), snapshot);
                        } catch (Exception e) {
                            logger.error("Error adding disease {}", meshUI, e);
                        }
                    }
            );

            watch.stop();

            logger.info(
                    "Populating diseases finished in {} seconds. {} diseases were added",
                    watch.getTime(TimeUnit.SECONDS), diseaseService.countAll());

        } catch (Exception e) {
            logger.error("Error while populating Disease table", e);
        }
    }

    private void addDisease(Record doRecord, Record meshRecord, String snapshot) {
        Disease disease = new Disease();

        logger.trace("Adding disease with MESH UI {} from DO Id {}", meshRecord.getId(), doRecord.getId());

        disease.setDoId(doRecord.getId());
        disease.setDoName(doRecord.getPropertyValue("name"));
        disease.setDoDef(doRecord.getPropertyValue("def"));
        disease.setIcd10CM(doRecord.getPropertyValues("xref", "ICD10CM:"));
        disease.setDoIsA(doRecord.getPropertyValue("is_a"));
        disease.setOmim(doRecord.getPropertyValues("xref", "OMIM:"));
        disease.setSnomedct(doRecord.getPropertyValues("xref", "SNOMEDCT_US_2016_03_01:"));
        disease.setUmlsCUI(doRecord.getPropertyValues("xref", "UMLS_CUI:"));

        // DO terms might point to the same MeSHUI. In that case, DO terms are merged by MeSHUI.

        Disease existingDisease = diseaseService.getDiseaseByMeSHUI(meshRecord.getId());

        if (existingDisease != null) {
            logger.trace("Merging data with existing disease {}", disease.getId());

            existingDisease.setDoId((combine(existingDisease.getDoIsAAsCollection(), disease.getDoIsAAsCollection())));
            existingDisease.setDoName(combine(existingDisease.getDoNameAsCollection(), disease.getDoNameAsCollection()));
            existingDisease.setDoDef(combine(existingDisease.getDoDefAsCollection(), disease.getDoDefAsCollection()));
            existingDisease.setIcd10CM(combine(existingDisease.getIcd10CMAsCollection(), disease.getIcd10CMAsCollection()));
            existingDisease.setDoIsA(combine(existingDisease.getDoIsAAsCollection(), disease.getDoIsAAsCollection()));
            existingDisease.setOmim(combine(existingDisease.getOmimAsCollection(), disease.getOmimAsCollection()));
            existingDisease.setSnomedct(combine(existingDisease.getSnomedctAsCollection(), disease.getSnomedctAsCollection()));
            existingDisease.setUmlsCUI(combine(existingDisease.getUmlsCUIAsCollection(), disease.getUmlsCUIAsCollection()));

            diseaseService.addDisease(existingDisease);
        } else {
            disease.setSnapshot(snapshot);
            disease.setMeSHUI(meshRecord.getId());
            disease.setMeshMH(meshRecord.getPropertyValue("MH"));

            // Filter excluded MeSH descriptors
            List<String> meshMNList =
                    meshRecord.getPropertyValues("MN")
                            .stream()
                            .filter(o -> !EXCLUDED_MESH_MN.contains(o.split("\\.")[0]))
                            .collect(Collectors.toList());

            if (meshMNList.isEmpty()) {
                return;
            }

            disease.setMeSHMN(meshMNList);

            diseaseService.addDisease(disease);
        }
    }

    private Collection<String> combine(Collection<String> a, Collection<String> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

}
