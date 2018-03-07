/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.controller;

import es.upm.disnet.pubmed.exception.ResourceNotFoundException;
import es.upm.disnet.pubmed.model.jpa.Phenotype;
import es.upm.disnet.pubmed.service.PhenotypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Eduardo P. Garcia del Valle
 */
@RestController
public class PhenotypeController {

    @Autowired
    private PhenotypeService phenotypeService;

    @RequestMapping("/phenotypes")
    public Page<Phenotype> getAllPhenotypes(Pageable pageable) {
        return phenotypeService.getAllPhenotypes(pageable);
    }

    @RequestMapping("/phenotypes/{phenotypeId}")
    public Phenotype getPhenotype(@PathVariable Long phenotypeId) {
        Phenotype phenotype = phenotypeService.getPhenotype(phenotypeId);

        if (phenotype == null) {
            throw new ResourceNotFoundException("Phenotype not found");
        }

        return phenotype;
    }

}