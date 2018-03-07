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
import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import es.upm.disnet.pubmed.service.DiseaseService;
import es.upm.disnet.pubmed.service.PubMedDocService;
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
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private PubMedDocService pubMedDocService;

    @RequestMapping("/diseases")
    public Page<Disease> getAllDiseases(Pageable pageable) {
        return diseaseService.getAllDiseases(pageable);
    }

    @RequestMapping("/diseases/{diseaseId}")
    public Disease getDisease(@PathVariable Long diseaseId) {
        Disease disease = diseaseService.getDisease(diseaseId);

        if (disease == null) {
            throw new ResourceNotFoundException("Disease not found");
        }

        return disease;
    }

    @RequestMapping("/diseases/{diseaseId}/pubmeddocs")
    public Page<PubMedDoc> getDiseasePubMedDocs(@PathVariable Long diseaseId, Pageable pageable) {
        Disease disease = diseaseService.getDisease(diseaseId);

        if (disease == null) {
            throw new ResourceNotFoundException("Disease not found");
        }

        return pubMedDocService.getAllPubMedDocsByDisease(disease, pageable);
    }
}