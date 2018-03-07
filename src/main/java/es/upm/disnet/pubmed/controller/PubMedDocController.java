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
public class PubMedDocController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private PubMedDocService pubMedDocService;

    @RequestMapping("/pubmeddocs")
    public Page<PubMedDoc> getAllPubmedDocs(Pageable pageable) {
        return pubMedDocService.getAllPubMedDocs(pageable);
    }

    @RequestMapping("/pubmeddocs/{pubMedDocId}")
    public PubMedDoc getPubMedDoc(@PathVariable Long pubMedDocId) {
        PubMedDoc pubMedDoc = pubMedDocService.getPubMedDoc(pubMedDocId);

        if (pubMedDoc == null) {
            throw new ResourceNotFoundException("PubMed Doc not found");
        }

        return pubMedDoc;
    }

    @RequestMapping("/pubmeddocs/{pubMedDocId}/disease")
    public Disease getPubMedDocDisease(@PathVariable Long pubMedDocId) {
        PubMedDoc pubMedDoc = pubMedDocService.getPubMedDoc(pubMedDocId);

        if (pubMedDoc == null) {
            throw new ResourceNotFoundException("PubMed Doc not found");
        }

        return diseaseService.getDiseasesByPubMedDoc(pubMedDoc);
    }

    /*
    // Many to Many

    @RequestMapping("/pubmeddocs/{pubMedDocId}/diseases")
    public Page<Disease> getPubMedDocDiseases(@PathVariable Long pubMedDocId, Pageable pageable) {
        PubMedDoc pubMedDoc = pubMedDocService.getPubMedDoc(pubMedDocId);

        if (pubMedDoc == null) {
            throw new ResourceNotFoundException("PubMed Doc not found");
        }

        return diseaseService.getAllDiseasesByPubMedDoc(pubMedDoc, pageable);
    }
    */
}