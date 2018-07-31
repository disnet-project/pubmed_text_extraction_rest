package es.upm.disnet.pubmed.controller;

import es.upm.disnet.pubmed.common.util.Common;
import es.upm.disnet.pubmed.model.Request;
import es.upm.disnet.pubmed.model.Response;
import es.upm.disnet.pubmed.retriever.DiseaseRetrieval;
import es.upm.disnet.pubmed.service.ExtractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created by gerardo on 08/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className ExtractionController
 * @see
 */
@RestController
@RequestMapping("${my.service.rest.request.mapping.general.url}")
public class ExtractionController {

    @Autowired
    private ExtractService extractService;
    @Autowired
    private DiseaseRetrieval diseaseRetrieval;
    @Autowired
    private Common common;

    @RequestMapping(path =  {  "${my.service.rest.request.mapping.texts.path}" },
            method = RequestMethod.POST)
    public Response extract(@RequestBody @Valid Request request, HttpServletRequest httpRequest, Device device) throws Exception {
        Response response = new Response();

        extractService.extract(request);

        return response;
    }


    @RequestMapping(path =  {  "${my.service.rest.request.mapping.texts.json.path}" },
            method = RequestMethod.POST)
    public Response extractByJSON(@RequestBody @Valid Request request, HttpServletRequest httpRequest, Device device) throws Exception {
        Response response = new Response();
        return extractService.extractByJSON(request);
    }


    @RequestMapping(path = { "/test" }, //wikipedia extraction
            method = RequestMethod.GET)
    public String test() throws Exception {
        common.writeJSONFile("{\"test\":\"glg_test\"}", "2018-03-15", "_file_test");
        return "ESCRITO";
    }


    @RequestMapping(path =  {  "/diseaselistinfo" },
            method = RequestMethod.GET)
    public String diseaseListInfo(){
        diseaseRetrieval.retrieve("2018-06-07");
        return "disease list info";
    }
}
