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
import es.upm.disnet.pubmed.repository.PubMedDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Service
public class PubMedDocService {

    @Autowired
    private PubMedDocRepository pubmedDocRepository;

    public PubMedDoc addPubMedDoc(PubMedDoc pubMedDoc) {
        if (pubMedDoc.getId() == 0) {
            pubMedDoc.setCreateDate(new Date());
        }

        return pubmedDocRepository.save(pubMedDoc);
    }

    public PubMedDoc getPubMedDoc(long pubMedDocId) {
        return pubmedDocRepository.findOne(pubMedDocId);
    }

    public PubMedDoc getPubMedDocByPmID(String pmID) {
        return pubmedDocRepository.findAllByPmID(pmID);
    }

    public List<PubMedDoc> getAllPubMedDocs() {
        return pubmedDocRepository.findAll();
    }

    public List<PubMedDoc> getAllPubMedDocsBySnapshot(String snapshot) {
        return pubmedDocRepository.findAllBySnapshot(snapshot);
    }

    public Page<PubMedDoc> getPubMedDocsBySnapshot(String snapshot, Pageable pageable) {
        return pubmedDocRepository.findBySnapshot(snapshot, pageable);
    }

    public Page<PubMedDoc> getAllPubMedDocs(Pageable pageable) {
        return pubmedDocRepository.findAll(pageable);
    }

    public long countAll() {
        return pubmedDocRepository.count();
    }

    public long countByPmID(String pmId) {
        return pubmedDocRepository.countByPmID(pmId);
    }

    public long countAllBySnapshot(String snapshot) {
        return pubmedDocRepository.countBySnapshot(snapshot);
    }

    public List<PubMedDoc> getAllPubMedDocsByDisease(Disease disease) {
        return pubmedDocRepository.findAllByDisease(disease);
    }

    public Page<PubMedDoc> getAllPubMedDocsByDisease(Disease disease, Pageable pageable) {
        return pubmedDocRepository.findAllByDisease(disease, pageable);
    }

    public String getPubMedAggregatedAbstractText(Disease disease) {
        List<PubMedDoc> pubMedDocs = getAllPubMedDocsByDisease(disease);

        return pubMedDocs
                .stream()
                .filter(p -> p.getAbstractText() != null)
                .map(p -> p.getAbstractText())
                .collect(Collectors.joining(" "));
    }

}
