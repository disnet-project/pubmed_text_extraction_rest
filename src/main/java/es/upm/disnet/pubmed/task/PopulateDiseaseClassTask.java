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
import es.upm.disnet.pubmed.model.jpa.DiseaseClass;
import es.upm.disnet.pubmed.parser.DiseaseOntologyOBOParser;
import es.upm.disnet.pubmed.parser.GenericSemiStructuredTextParser.Record;
import es.upm.disnet.pubmed.parser.MeSHASCIIParser;
import es.upm.disnet.pubmed.service.DiseaseClassService;
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
public class PopulateDiseaseClassTask {

    private static final Logger logger = LoggerFactory.getLogger(PopulateDiseaseClassTask.class);

    private static final String DISEASE_CATEGORY_DO_ID = "DOID:4 ! disease";

    @Autowired
    private DiseaseClassService diseaseClassService;

    public void populateDiseaseClasses(String snapshot) {
        try {
            StopWatch watch = new StopWatch();

            logger.info("Populating diseas classes for snapshot {}.", snapshot);

            watch.start();

            Path doPath = Paths.get(
                    Thread.currentThread().getContextClassLoader().getResource("do/HumanDO.obo").toURI());

            DiseaseOntologyOBOParser diseaseOntologyOBOParser = new DiseaseOntologyOBOParser();

            final List<Record> doRecords = diseaseOntologyOBOParser.parse(doPath, StandardCharsets.UTF_8.name());

            // Populate Disease repository with diseases in DO that have MeSH terms

            List<Record> diseaseRecordList = doRecords.stream().filter(o -> o.hasPropertyValue("is_a", DISEASE_CATEGORY_DO_ID)).collect(Collectors.toList());

            addDiseaseClasses(doRecords, diseaseRecordList, 0, 0, snapshot);

            watch.stop();

            logger.info(
                    "Populating diseases finished in {} seconds. {} diseases were added",
                    watch.getTime(TimeUnit.SECONDS), diseaseClassService.countAllBySnapshot(snapshot));

        } catch (Exception e) {
            logger.error("Error while populating Disease table", e);
        }
    }

    private void addDiseaseClasses(List<Record> allRecords, List<Record> records, long level, long parentId, String snapshot) {
        for (Record record: records) {
            DiseaseClass diseaseClass = addDiseaseClass(record, level, parentId, snapshot);

            if (diseaseClassService.countAllByParentId(diseaseClass.getId()) > 0) {
                continue;
            }

            List<Record> newRecords = allRecords.stream().filter(o -> o.hasPropertyValue("is_a", record.getId())).collect(Collectors.toList());

            addDiseaseClasses(allRecords, newRecords, level +1, diseaseClass.getId(), snapshot);
        }
    }

    private DiseaseClass addDiseaseClass(Record doRecord, long level, long parentId, String snapshot) {
        DiseaseClass diseaseClass = diseaseClassService.getDiseaseClassByDoId(doRecord.getId());

        if (diseaseClass != null) {
            return diseaseClass;
        }

        diseaseClass = new DiseaseClass();

        diseaseClass.setDoId(doRecord.getId());
        diseaseClass.setDoName(doRecord.getPropertyValue("name"));
        diseaseClass.setLevel(level);
        diseaseClass.setParentId(parentId);
        diseaseClass.setSnapshot(snapshot);

        return diseaseClassService.addDiseaseClass(diseaseClass);

    }

    private Collection<String> combine(Collection<String> a, Collection<String> b) {
        return Stream.concat(a.stream(), b.stream()).collect(Collectors.toList());
    }

}
