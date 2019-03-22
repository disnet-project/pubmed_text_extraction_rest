package es.upm.disnet.pubmed.retriever;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.upm.disnet.pubmed.common.util.Common;
import es.upm.disnet.pubmed.common.util.TimeProvider;
import es.upm.disnet.pubmed.constants.Constants;
import es.upm.disnet.pubmed.model.Request;
import es.upm.disnet.pubmed.model.document_structure.*;
import es.upm.disnet.pubmed.model.document_structure.Disease;
import es.upm.disnet.pubmed.model.document_structure.PubMedDoc;
import es.upm.disnet.pubmed.model.document_structure.text.Paragraph;
import es.upm.disnet.pubmed.model.document_structure.text.Text;
import es.upm.disnet.pubmed.parser.PubMedWebArticleParser;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reciter.model.pubmed.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by gerardo on 13/03/2018.
 *
 * @author Gerardo Lagunes G. ${EMAIL}
 * @version ${<VERSION>}
 * @project pubmed_text_extraction_rest
 * @className RetrievalControl
 * @see
 */
@Component
public class RetrievalControl {

    private static final Logger logger = LoggerFactory.getLogger(RetrievalControl.class);

    @Autowired
    private DiseaseRetrieval diseaseRetrieval;
    @Autowired
    private PubMedArticleRetrieval pubMedArticleRetrieval;
    @Autowired
    private PubMedWebArticleParser pubMedWebArticleParser;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private Common common;


    /**
     * @param request
     * @return
     * @throws IOException
     */
    public Source retrieve(Request request) throws IOException {
        Source source = new Source(Constants.SOURCE_PUBMED_CODE, Constants.SOURCE_PUBMED);

        try {
            StopWatch watch = new StopWatch();

            logger.info("Retrieving Diseases and PubMed docs for snapshot {}.", request.getSnapshot());

            TreeMap<String, String> sectionMap = new TreeMap<>();
            sectionMap.put(Constants.ABSTRACT_SECTION, Constants.ABSTRACT_SECTION);
            List<Doc> docList = new ArrayList<>();

            watch.start();
            //Recupera las enfermedades (descriptores MeSH) usando:
            // d2018.bin
            // obteniendo más información de la HumanDO.obo
            List<Disease> diseaseList = diseaseRetrieval.retrieve(request.getSnapshot());
            System.out.println("Disease Retrieved: " + diseaseList.size());
            if (request.isJson()) {
                try {
                    logger.info("Saving initiated disease list (MeSH terms) ");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    common.writeJSONFile(gson.toJson(diseaseList), request.getSnapshot(), Constants.RETRIEVAL_DISEASE_LIST_FILE_NAME);
                    logger.info("Saving of finished disease list (MeSH terms) ");
                } catch (Exception e) {
                    logger.error("Error while save json {} ", request.getSnapshot() + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
                }
            }

            int diseaseCount = 1, documentCount = 0;
            //<editor-fold desc="DiseaseList">
            for (Disease disease: diseaseList) {
                Doc document = new Doc();
                List<PubMedDoc> pubMedDocs = new ArrayList<>();
                disease.setId(diseaseCount);

                logger.info("("+diseaseCount+") Retrieving docs for id " + disease.getMeSHUI() + " | " + disease.getMeSHMH());

                String pubMedMeshTermQuery =
                        URLEncoder.encode(getPubMedMeshTermQuery(disease.getMeSHMH()), StandardCharsets.UTF_8.name());

                //<editor-fold desc="try retrieved pubmed docs">
                try {
                    List<PubMedArticle> pubMedArticles = pubMedArticleRetrieval.retrieve(pubMedMeshTermQuery, request.getNumOfArticles());

                    logger.debug(
                            "{} PubMed articles retrieved for disease with MESH UI {} ({})",
                            pubMedArticles.size(), disease.getMeSHUI(), disease.getId());
                    logger.info(
                            "{} PubMed articles retrieved for disease with MESH UI {} ({})",
                            pubMedArticles.size(), disease.getMeSHUI(), disease.getId());

                    final int[] count = {1};
                    pubMedArticles.stream().forEach(p -> {
                        try {
                            addPubMedDoc(p, disease, pubMedDocs, request.getSnapshot(), count[0]);
                            count[0]++;
                        } catch (Exception e) {
                            logger.error(
                                    "Error adding document {} for disease {}", p, disease.getMeSHUI(), e);
                        }
                    });
                } catch (Exception e) {
                    logger.error("Error in rest {}", pubMedMeshTermQuery, e);
                }
                //</editor-fold>

                document.setId(diseaseCount);
                document.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(request.getSnapshot()));
                document.setDisease(disease);

                document.setSectionList( createAbstractSection(pubMedDocs) );
                document.setSectionCount(document.getSectionList().size());

                document.setCodeList(disease.getCodes());
                document.setCodeCount(document.getCodeList().size());

                document.setPaperList(pubMedDocs);
                document.setPaperCount(document.getPaperList().size());

                documentCount += document.getPaperCount();

                //System.out.println("Document("+document.getId()+"): " + document.toString());

                docList.add(document);
                //if (diseaseCount==1) break;
                //Guardar enfermedad con sus artículos encontrados
                if (request.isJson()) {
                    try {
                        logger.info("Saving initiated document (Disease and PubMed articles) ");
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        common.writeJSONFile(gson.toJson(document), request.getSnapshot(), Constants.RETRIEVAL_DOCUMENT_FILE_NAME + "_" + document.getDisease().getMeSHUI());
                        logger.info("Saving of finished document (Disease and PubMed articles) ");
                    } catch (Exception e) {
                        logger.error("Error while save json {} ", request.getSnapshot() + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
                    }
                }

                diseaseCount++;
            }
            //</editor-fold>
            watch.stop();

            logger.info(
                    "Retrieving PubMed docs finished in {} minutes. {} PubMed docs were added",
                    watch.getTime(TimeUnit.MINUTES), documentCount);

            source.setDocuments(docList);
            source.setDocumentCount(source.getDocuments().size());
            source.setSectionMap(sectionMap);

        } catch (Exception e) {
            logger.error("Error while retrieving Disease and their PubMedDoc from PubMed source", e);
        }

//        if (request.isJson()) {
//            try {
//                logger.info("Saving initiated PubMed texts");
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                common.writeJSONFile(gson.toJson(source), request.getSnapshot(), Constants.RETRIEVAL_FILE_NAME);
//                logger.info("Saving of finished PubMed texts");
//            } catch (Exception e) {
//                logger.error("Error while save json {} ", request.getSnapshot() + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
//            }
//        }

        return source;

    }


    public Source getMoreCodes(Request request){
        Source source = null;
        try {
            //source = common.readPubMedRetrievalJSON(request.getSnapshot());
            //System.out.println("source: "+source);
            // obteniendo más información de la HumanDO.obo
            List<Disease> diseaseList = diseaseRetrieval.retrieve(request.getSnapshot());
        } catch (Exception e) {
            logger.error("Error while save json {} ", request.getSnapshot() + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
        }
        return source;

    }


    /**
     * @param request
     * @return
     * @throws IOException
     */
    public Source retrieveByJSON(Request request) throws Exception {
        Source source = null;
        try {
            source = common.readPubMedRetrievalJSON(request.getSnapshot());
            //System.out.println("source: "+source);
        } catch (Exception e) {
            logger.error("Error while save json {} ", request.getSnapshot() + Constants.RETRIEVAL_FILE_NAME + Constants.DOT_JSON);
        }
        return source;
    }


    /**
     * @param pubMedDocs
     * @return
     */
    private List<Section> createAbstractSection(List<PubMedDoc> pubMedDocs){
        List<Section> sections = new ArrayList<>();
        Section section = new Section();
        section.setId(1);
        section.setDescription(Constants.ABSTRACT_SECTION);
        section.setName(Constants.ABSTRACT_SECTION);
        section.setTextList( createSectionTextsByPubMedDocs(pubMedDocs) );
        section.setTextCount(section.getTextList().size());

        sections.add(section);
        return sections;
    }


    /**
     * @param pubMedDocs
     * @return
     */
    private List<Text> createSectionTextsByPubMedDocs(List<PubMedDoc> pubMedDocs){
        List<Text> texts = new ArrayList<>();
        int count = 1;
        for (PubMedDoc document: pubMedDocs) {
            if (!common.isEmpty( document.getAbstractText() ) ){
                Paragraph abstractText = new Paragraph(count, count, document.getPmID(), document.getAbstractText());
                document.setAbstractText("idText: " + count);
                texts.add(abstractText);
                count++;
            }
        }

        return texts;
    }


    /**
     * @param pubMedArticle
     * @param disease
     * @param pubMedDocs
     * @param snapshot
     * @param count
     */
    private void addPubMedDoc(PubMedArticle pubMedArticle, Disease disease, List<PubMedDoc> pubMedDocs, String snapshot, int count) {
        MedlineCitation medlineCitation = pubMedArticle.getMedlinecitation();

        if (medlineCitation == null) {
            return;
        }

        String pmId = String.valueOf(medlineCitation.getMedlinecitationpmid().getPmid());

        logger.trace("Adding PubMed doc({}) with PM ID {} for Disease with MESH UI {}({})",
                count, pmId, disease.getMeSHUI(), disease.getId());
        logger.info("Adding PubMed doc({}) with PM ID {} for Disease with MESH UI {}({})",
                count, pmId, disease.getMeSHUI(), disease.getId());

        PubMedDoc pubMedDoc = new PubMedDoc();

        pubMedDoc.setId(count);
        pubMedDoc.setPmID(pmId);
        pubMedDoc.setSnapshot(snapshot);

        MedlineCitationArticle medlineCitationArticle = medlineCitation.getArticle();

        if (medlineCitationArticle == null) {
            return;
        }

        if ((pubMedArticle.getPubmeddata() != null) && (pubMedArticle.getPubmeddata().getArticleIdList() != null)) {
            pubMedDoc.setPmcID(pubMedArticle.getPubmeddata().getArticleIdList().getPmc());
        }

        pubMedDoc.setTitleText(medlineCitationArticle.getArticletitle());

        if (medlineCitationArticle.getElocationid() != null) {
            pubMedDoc.setDoi(medlineCitationArticle.getElocationid().getElocationid());
        }

        if (medlineCitation.getMeshheadinglist() == null) {
            return;
        }

        pubMedDoc.setMeshTerms(
                medlineCitation.getMeshheadinglist()
                        .stream()
                        .map(o -> o.getDescriptorname().getDescriptorname())
                        .collect(Collectors.toList()));

        List<MedlineCitationArticleAuthor> authorList = medlineCitationArticle.getAuthorlist();

        if (authorList != null) {
            pubMedDoc.setAuthor(
                    authorList
                            .stream()
                            .map(o -> o.getLastname() + "," + o.getForename())
                            .collect(Collectors.toList()));
        }

        MedlineCitationKeywordList medlineCitationKeywordlist = medlineCitation.getKeywordlist();

        if (medlineCitationKeywordlist != null) {
            pubMedDoc.setKeyWords(
                    medlineCitationKeywordlist.getKeywordlist()
                            .stream()
                            .map(o -> o.getKeyword())
                            .collect(Collectors.toList()));
        }

        pubMedDoc.setLink();
        pubMedDoc.setFreeTextlink();
        pubMedDoc.setHasFreeText();
        pubMedDoc.setDisease(disease);

        addPubMedDocsAbstractText(pubMedDoc);

        pubMedDocs.add(pubMedDoc);

    }


    /**
     * @param pubMedDoc
     */
    private void addPubMedDocsAbstractText(PubMedDoc pubMedDoc) {
        if (pubMedDoc != null){
            if (pubMedDoc.getAbstractText() != null) {
                return;
            }

            try {
                logger.debug(
                        "Retrieving abstract for PubMed doc {} in url {})",
                        pubMedDoc.getPmID(), pubMedDoc.getPmArticleURL());
                logger.info(
                        "Retrieving abstract for PubMed doc {} in url {})",
                        pubMedDoc.getPmID(), pubMedDoc.getPmArticleURL());

                String abstractText = pubMedWebArticleParser.getPubMedDocAbstractText(pubMedDoc.getPmArticleURL());

                logger.debug("Abstract retrieved {})", abstractText);
                //logger.info("Abstract retrieved {})", abstractText);

                pubMedDoc.setAbstractText(abstractText);
            } catch (Exception e) {
                logger.error("(3) Error retrieving abstract for PubMed doc  {}", pubMedDoc.getPmID(), e);
            }
        }
    }


    /**
     * @param meshTerm
     * @return
     */
    private String getPubMedMeshTermQuery(String meshTerm) {
        // To search a MeSH heading as a major topic and turn off the automatic EXPLODE
        // See https://www.nlm.nih.gov/bsd/disted/pubmedtutorial/020_720.html

        return String.format("%s[majr:noexp]", meshTerm);
    }






}
