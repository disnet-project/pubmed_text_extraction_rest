/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.service;

import es.upm.disnet.pubmed.model.jpa.Phenotype;
import es.upm.disnet.pubmed.repository.PhenotypeRepository;
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
public class PhenotypeService {

    @Autowired
    private PhenotypeRepository phenotypeRepository;

    public Phenotype getPhenotype(long phenotypeId) {
        return phenotypeRepository.findOne(phenotypeId);
    }

    public Phenotype getPhenotypeByMeSHUI(String meSHUI) {
        return phenotypeRepository.findAllByMeSHUI(meSHUI);
    }

    public Phenotype addPhenotype(Phenotype phenotype) {
        if (phenotype.getId() == 0) {
            phenotype.setCreateDate(new Date());
        }

        return phenotypeRepository.save(phenotype);
    }

    public List<Phenotype> getAllPhenotypes() {
        return phenotypeRepository.findAll();
    }

    public List<Phenotype> getAllPhenotypesBySnapshot(String snapshot) {
        return phenotypeRepository.findAllBySnapshot(snapshot);
    }

    public long countAll() {
        return phenotypeRepository.count();
    }

    public long countAllBySnapshot(String snapshot) {
        return phenotypeRepository.countBySnapshot(snapshot);
    }

    public Page<Phenotype> getAllPhenotypes(Pageable pageable) {
        return phenotypeRepository.findAll(pageable);
    }

}
