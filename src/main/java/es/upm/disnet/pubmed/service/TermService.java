/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.service;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.Term;
import es.upm.disnet.pubmed.repository.TermRepository;
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
public class TermService {

    @Autowired
    private TermRepository termRepository;

    public Term getTerm(long termId) {
        return termRepository.findOne(termId);
    }

    public Term addTerm(Term term) {
        if (term.getId() == 0) {
            term.setCreateDate(new Date());
        }

        return termRepository.save(term);
    }

    public long countByDisease(Disease disease) {
        return termRepository.countByDisease(disease);
    }

    public long countByDisease(Disease disease, String source) {
        return termRepository.countByDiseaseAndSource(disease, source);
    }

    public long countAllBySource(String source, String snapshot) {
        return termRepository.countBySourceAndSnapshot(source, snapshot);
    }

    //TODO: Include snapshot as filter
    public List<Term> getAllTermsByDisease(Disease disease) {
        return termRepository.findAllByDisease(disease);
    }

    public List<Term> getAllTermsByDisease(Disease disease, String source) {
        return termRepository.findAllByDiseaseAndSource(disease, source);
    }

    public List<Term> getAllTermsByDisease(Disease disease, String source, boolean validated) {
        return termRepository.findAllByDiseaseAndSourceAndValidated(disease, source, validated);
    }

    //TODO: Include snapshot as filter
    public Multiset<String> getAllTermCuisByDisease(Disease disease, String source, boolean expandFrequency) {
        List<Term> terms = getAllTermsByDisease(disease, source, true);

        Multiset<String> multiset = HashMultiset.create();

        if (expandFrequency) {
            terms.forEach(t -> multiset.add(t.getCui(), t.getFrequency()));
        } else {
            terms.forEach(t -> multiset.add(t.getCui(), 1));
        }

        return multiset;
    }

    public Page<Term> getAllTermsByDisease(Disease disease, Pageable pageable) {
        return termRepository.findAllByDisease(disease, pageable);
    }

    public void delete(Term term) {
        termRepository.delete(term);
    }

}
