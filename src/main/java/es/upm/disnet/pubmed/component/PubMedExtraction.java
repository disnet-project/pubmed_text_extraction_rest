package es.upm.disnet.pubmed.component;
import es.upm.disnet.pubmed.common.util.Common;
import es.upm.disnet.pubmed.common.util.TimeProvider;
import es.upm.disnet.pubmed.model.document_structure.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gerardo on 08/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className PubMedExtraction
 * @see
 */
@Component
public class PubMedExtraction {

    @Autowired
    private Common common;
    @Autowired
    private TimeProvider date;


    public List<Source> extract(Date snapshot) throws Exception {
        List<Source> sourceList;


        sourceList = new ArrayList<>();


        return sourceList;
    }


}
