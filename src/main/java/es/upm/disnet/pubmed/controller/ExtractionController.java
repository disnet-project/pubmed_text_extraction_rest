package es.upm.disnet.pubmed.controller;

import es.upm.disnet.pubmed.common.util.Common;
import es.upm.disnet.pubmed.constants.Constants;
import es.upm.disnet.pubmed.model.Request;
import es.upm.disnet.pubmed.model.Response;
import es.upm.disnet.pubmed.model.document_structure.Disease;
import es.upm.disnet.pubmed.model.document_structure.Doc;
import es.upm.disnet.pubmed.model.document_structure.code.Code;
import es.upm.disnet.pubmed.retriever.DiseaseRetrieval;
import es.upm.disnet.pubmed.retriever.RetrievalControl;
import es.upm.disnet.pubmed.service.ExtractService;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.FileWriter;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(ExtractionController.class);

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

        List<Disease> diseaseList = diseaseRetrieval.retrieve("2018-06-07");
        System.out.println("Disease Retrieved: " + diseaseList.size());
//        int diseaseCount = 1, documentCount = 0;
//        for (Disease disease: diseaseList) {
//            logger.info("Disease ("+diseaseCount+") | ID: " + disease.getMeSHUI() + " | Name: " + disease.getMeSHMH());
//            for (Code code: disease.getCodes()) {
//                logger.info("   Her codes (" + code.getId() + ") | ID: " + code.getId() + " | CODE: " + code.getCode() + " | UniqueId: " + code.getUniqueId());
//            }
//            diseaseCount++;
//        }
        return "disease list info";
    }

    @RequestMapping(path =  {  "/retrievalInfo" },
            method = RequestMethod.GET,
            params = {"snapshot", "json"})
    public String retrievalInfo(
            @RequestParam(value = "snapshot") @Valid @NotBlank @NotNull @NotEmpty String snapshot,
            @RequestParam(value = "json", required = false, defaultValue = "true") boolean json
    ){
        try {
            String path = Constants.RETRIEVAL_HISTORY_FOLDER + "test" + Constants.DOT + Constants.TEXT;
            FileWriter fileWriter = new FileWriter(path);


            Request request = new Request(snapshot, json);
            Response response = extractService.extractByJSON(request);

            logger.info("Exist: " + response.getSource().getDocumentCount() + " documents");
            for (Doc document: response.getSource().getDocuments()) {
//                logger.info("");
//                System.out.print(document.getDisease().getId() + "| MeSH | " + document.getDisease().getMeSHUI() + " | DiseaseName | " + document.getDisease().getName() + " |" + document.getDisease().getMeSHMH() + "|");
                for (Code code: document.getCodeList()) {
                    System.out.println(document.getDisease().getId() + "| MeSH | " + document.getDisease().getMeSHUI() + " | DiseaseName | " + document.getDisease().getName() + " |" + document.getDisease().getMeSHMH() + "|" + "|Code| " + code.getCode() + " | " + code.getResource().getName());
                    fileWriter.write(document.getDisease().getId() + "| MeSH | " + document.getDisease().getMeSHUI() + " | DiseaseName | " + document.getDisease().getName() + " |" + document.getDisease().getMeSHMH() + "|" + "|Code| " + code.getCode() + " | " + code.getResource().getName() + "\n");
                }
            }
            fileWriter.close();

        }catch (Exception e){
            logger.error("Error", e);
        }
        return "done!";
    }
}
