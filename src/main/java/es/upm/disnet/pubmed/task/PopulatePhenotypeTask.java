/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.task;


import es.upm.disnet.pubmed.model.jpa.Phenotype;
import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser.Record;
import es.upm.disnet.pubmed.parser.MeSHASCIIParser;
import es.upm.disnet.pubmed.parser.PhenotypeOntologyOBOParser;
import es.upm.disnet.pubmed.service.PhenotypeService;
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
public class PopulatePhenotypeTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulatePhenotypeTask.class);
    // View https://meshb.nlm.nih.gov/treeView
    private final static List<String> INCLUDED_MESH_MN = Arrays.asList(new String[]{"C23"});

    @Autowired
    private PhenotypeService phenotypeService;

    public void populatePhenotype(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating phenotypes for snapshot {}.", snapshot);

            watch.start();

            // Obtain MeSH records

            Path meshPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("mesh/d2018.bin").toURI());

            MeSHASCIIParser meSHASCIIParser = new MeSHASCIIParser();

            final List<Record> meshRecords = meSHASCIIParser.parse(meshPath, StandardCharsets.UTF_8.name());

            // Obtain PO records

            Path poPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("po/hp.obo").toURI());

            PhenotypeOntologyOBOParser phenotypeOntologyOBOParser = new PhenotypeOntologyOBOParser();

            final List<Record> poRecords = phenotypeOntologyOBOParser.parse(poPath, StandardCharsets.UTF_8.name());

            // Populate Phenotype repository with phenotypes in PO that have MeSH terms

            List<Record> testList = poRecords.stream().filter(o -> o.hasPropertyValue("xref", "MSH")).collect(Collectors.toList());

            testList.size();

            //TODO: extract to constants

            poRecords.stream().filter(o -> o.hasPropertyValue("xref", "MSH:D")).forEach(
                    o -> {
                        String meshUI = o.getPropertyValue("xref", "MSH:");

                        Optional<Record> optMeshRecord = meshRecords.stream().filter(m -> m.getId().equals(meshUI)).findFirst();

                        if (!optMeshRecord.isPresent()) {
                            logger.error("Mesh Record not found for UI {}", meshUI);

                            return;
                        }

                        try {
                            addPhenotype(o, optMeshRecord.get(), snapshot);
                        } catch (Exception e) {
                            logger.error("Error adding phenotype {}", meshUI, e);
                        }
                    }
            );

            watch.stop();

            logger.info(
                    "Populating phenotypes finished in {} seconds. {} phenotypes were added",
                    watch.getTime(TimeUnit.SECONDS), phenotypeService.countAll());

        } catch (Exception e) {
            logger.error("Error while populating Phenotype table", e);
        }
    }

    private void addPhenotype(Record poRecord, Record meshRecord, String snapshot) {
        Phenotype phenotype = new Phenotype();

        logger.trace("Adding phenotype with MESH UI {} from PO Id {}", meshRecord.getId(), poRecord.getId());

        phenotype.setPoId(poRecord.getId());
        phenotype.setPoName(poRecord.getPropertyValue("name"));
        phenotype.setPoDef(poRecord.getPropertyValue("def"));
        phenotype.setPoIsA(poRecord.getPropertyValue("is_a"));
        phenotype.setSnomedct(poRecord.getPropertyValues("xref", "SNOMEDCT_US:"));
        phenotype.setUmlsCUI(poRecord.getPropertyValues("xref", "UMLS:"));

        // PO terms might point to the same MeSHUI. In that case, PO terms are merged by MeSHUI.

        Phenotype existingPhenotype = phenotypeService.getPhenotypeByMeSHUI(meshRecord.getId());

        if (existingPhenotype != null) {
            logger.trace("Merging data with existing phenotype {}", phenotype.getId());

            existingPhenotype.setPoId((combine(existingPhenotype.getPoIsAAsCollection(), phenotype.getPoIsAAsCollection())));
            existingPhenotype.setPoName(combine(existingPhenotype.getPoNameAsCollection(), phenotype.getPoNameAsCollection()));
            existingPhenotype.setPoDef(combine(existingPhenotype.getPoDefAsCollection(), phenotype.getPoDefAsCollection()));
            existingPhenotype.setPoIsA(combine(existingPhenotype.getPoIsAAsCollection(), phenotype.getPoIsAAsCollection()));
            existingPhenotype.setSnomedct(combine(existingPhenotype.getSnomedctAsCollection(), phenotype.getSnomedctAsCollection()));
            existingPhenotype.setUmlsCUI(combine(existingPhenotype.getUmlsCUIAsCollection(), phenotype.getUmlsCUIAsCollection()));

            phenotypeService.addPhenotype(existingPhenotype);
        } else {
            phenotype.setSnapshot(snapshot);
            phenotype.setMeSHUI(meshRecord.getId());
            phenotype.setMeshMH(meshRecord.getPropertyValue("MH"));

            // Filter excluded MeSH descriptors
            List<String> meshMNList =
                    meshRecord.getPropertyValues("MN")
                            .stream()
                            .filter(o -> INCLUDED_MESH_MN.contains(o.split("\\.")[0]))
                            .collect(Collectors.toList());

            if (meshMNList.isEmpty()) {
                return;
            }

            phenotype.setMeSHMN(meshMNList);

            phenotypeService.addPhenotype(phenotype);
        }
    }

    private Collection<String> combine(Collection<String> a, Collection<String> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

}
