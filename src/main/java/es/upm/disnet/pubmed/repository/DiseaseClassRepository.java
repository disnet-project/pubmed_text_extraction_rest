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
import es.upm.disnet.pubmed.model.jpa.DiseaseClass;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
public interface DiseaseClassRepository extends JpaRepository<DiseaseClass, Long> {

    DiseaseClass findByDoId(String doId);

    long countBySnapshot(String snapshot);

    long countByParentId(long parentId);

}