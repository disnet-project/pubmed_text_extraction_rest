package es.upm.disnet.pubmed.service;

import es.upm.disnet.pubmed.common.util.TimeProvider;
import es.upm.disnet.pubmed.model.Response;
import es.upm.disnet.pubmed.model.document_structure.Source;
import es.upm.disnet.pubmed.retriever.DiseaseRetrieval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by gerardo on 08/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className ExtractService
 * @see
 */
@Service
public class ExtractService {

    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private DiseaseRetrieval diseaseRetrieval;


    public Response extract(String snapshot) throws Exception {
        Response response = new Response();
        List<Source> sourceList = null;

        String start = timeProvider.getTimestampFormat();
        String end = null;
        Date version = timeProvider.getSqlDate();

        diseaseRetrieval.retrieval(snapshot);

        return response;

    }


}
