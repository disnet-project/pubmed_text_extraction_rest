package es.upm.disnet.pubmed.service;

import es.upm.disnet.pubmed.common.util.TimeProvider;
import es.upm.disnet.pubmed.enums.ApiErrorEnum;
import es.upm.disnet.pubmed.enums.StatusHttpEnum;
import es.upm.disnet.pubmed.model.Request;
import es.upm.disnet.pubmed.model.Response;
import es.upm.disnet.pubmed.model.document_structure.Source;
import es.upm.disnet.pubmed.retriever.RetrievalControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private RetrievalControl retrievalControl;


    public Response extract(Request request) throws Exception {
        Response response = new Response();
        Source source = new Source();

        String start = timeProvider.getTimestampFormat();
        Date version = timeProvider.getSqlDate();

        try {
            source = retrievalControl.retrieve(request);
            if (source != null) {
                response.setResponseCode(StatusHttpEnum.OK.getClave());
                response.setResponseMessage(StatusHttpEnum.OK.getDescripcion());
            } else {
                response.setResponseCode(ApiErrorEnum.RESOURCES_NOT_FOUND.getKey());
                response.setResponseMessage(ApiErrorEnum.RESOURCES_NOT_FOUND.getDescription());
            }
        }catch (Exception e){
            response.setResponseCode(ApiErrorEnum.INTERNAL_SERVER_ERROR.getKey());
            response.setResponseMessage(ApiErrorEnum.INTERNAL_SERVER_ERROR.getDescription());
        }
        String end = timeProvider.getTimestampFormat();

        response.setSource(source);
        response.setStart_time(start);
        response.setEnd_time(end);

        System.out.println("Inicio:" + start + " | Termino: " + end);

        return response;

    }


}
