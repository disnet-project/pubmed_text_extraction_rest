/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.repository;

import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    Disease findByMeSHUI(String meSHUI);

    List<Disease> findAllBySnapshot(String snapshot);

    List<Disease> findAllByDisnet(boolean disnet);

    Disease findByPubMedDocs(PubMedDoc pubMedDoc);

    /*
    // Many to Many

    Iterable<Disease> findAllByPubMedDocs(PubMedDoc pubMedDoc);

    Page<Disease> findAllByPubMedDocs(PubMedDoc pubMedDoc, Pageable pageable);
    */

    long countBySnapshot(String snapshot);

}