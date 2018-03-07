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
import es.upm.disnet.pubmed.model.jpa.Term;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findAllByDisease(Disease disease);

    List<Term> findAllByDiseaseAndSource(Disease disease, String source);

    List<Term> findAllByDiseaseAndSourceAndValidated(Disease disease, String source, boolean validated);

    Page<Term> findAllByDisease(Disease disease, Pageable pageable);

    long countByDisease(Disease disease);

    long countByDiseaseAndSource(Disease disease, String source);

    long countBySourceAndSnapshot(String source, String snapshot);
}