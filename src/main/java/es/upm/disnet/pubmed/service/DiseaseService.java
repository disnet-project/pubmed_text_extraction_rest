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
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
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
public class DiseaseService {

    @Autowired
    private DiseaseRepository diseaseRepository;

    public Disease getDisease(long diseaseId) {
        return diseaseRepository.findOne(diseaseId);
    }

    public Disease getDiseaseByMeSHUI(String meSHUI) {
        return diseaseRepository.findByMeSHUI(meSHUI);
    }

    public Disease addDisease(Disease disease) {
        if (disease.getId() == 0) {
            disease.setCreateDate(new Date());
        }

        return diseaseRepository.save(disease);
    }

    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    public List<Disease> getAllDiseasesInDisnet() {
        return diseaseRepository.findAllByDisnet(true);
    }

    public List<Disease> getAllDiseasesBySnapshot(String snapshot) {
        return diseaseRepository.findAllBySnapshot(snapshot);
    }

    public long countAll() {
        return diseaseRepository.count();
    }

    public long countAllBySnapshot(String snapshot) {
        return diseaseRepository.countBySnapshot(snapshot);
    }

    public Page<Disease> getAllDiseases(Pageable pageable) {
        return diseaseRepository.findAll(pageable);
    }

    public Disease getDiseasesByPubMedDoc(PubMedDoc pubMedDoc) {
        return diseaseRepository.findByPubMedDocs(pubMedDoc);
    }

    /*
    // Many to Many

    public Iterable<Disease> getAllDiseasesByPubMedDoc(PubMedDoc pubMedDoc) {
        return diseaseRepository.findAllByPubMedDocs(pubMedDoc);
    }

    public Page<Disease> getAllDiseasesByPubMedDoc(PubMedDoc pubMedDoc, Pageable pageable) {
        return diseaseRepository.findAllByPubMedDocs(pubMedDoc, pageable);
    }
    */

}
