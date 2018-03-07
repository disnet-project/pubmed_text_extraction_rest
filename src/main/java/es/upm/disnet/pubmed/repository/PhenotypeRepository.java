/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.repository;

import es.upm.disnet.pubmed.model.jpa.Phenotype;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
public interface PhenotypeRepository extends JpaRepository<Phenotype, Long> {

    Phenotype findAllByMeSHUI(String meSHUI);

    List<Phenotype> findAllBySnapshot(String snapshot);

    long countBySnapshot(String snapshot);

}