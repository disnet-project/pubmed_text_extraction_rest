/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.service;

import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.DiseaseClass;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import es.upm.disnet.pubmed.repository.DiseaseClassRepository;
import es.upm.disnet.pubmed.repository.DiseaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Service
public class DiseaseClassService {

    @Autowired
    private DiseaseClassRepository diseaseClassRepository;

    public DiseaseClass addDiseaseClass(DiseaseClass diseaseClass) {
        if (diseaseClass.getId() == 0) {
            diseaseClass.setCreateDate(new Date());
        }

        return diseaseClassRepository.save(diseaseClass);
    }

    public DiseaseClass getDiseaseClass(long diseaseClassId) {
        return diseaseClassRepository.findOne(diseaseClassId);
    }

    public DiseaseClass getDiseaseClassByDoId(String doId) {
        return diseaseClassRepository.findByDoId(doId);
    }

    public List<DiseaseClass> getAllDiseaseClasses() {
        return diseaseClassRepository.findAll();
    }

    public long countAllBySnapshot(String snapshot) {
        return diseaseClassRepository.countBySnapshot(snapshot);
    }

    public long countAllByParentId(long parentId) {
        return diseaseClassRepository.countByParentId(parentId);
    }

    public DiseaseClass getParentDiseaseClass(String doId, long parentLevel) {
        DiseaseClass diseaseClass = getDiseaseClassByDoId(doId);

        if (diseaseClass == null || diseaseClass.getLevel() < parentLevel) {
            return null;
        }

        if (diseaseClass.getLevel() == parentLevel) {
            return diseaseClass;
        }

        while(true) {
            DiseaseClass parentDiseaseClass = getDiseaseClass(diseaseClass.getParentId());

            if (parentDiseaseClass.getLevel() == parentLevel) {
                return parentDiseaseClass;
            }

            diseaseClass = parentDiseaseClass;
        }
    }
}
