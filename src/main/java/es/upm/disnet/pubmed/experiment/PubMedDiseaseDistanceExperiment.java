/*
 * Copyright (c) 2018. Eduardo P. Garcia del Valle.
 *
 * If other author is present on the file, the copyrights are shared or of the mentioned author.
 *
 * Unless required by applicable law or agreed to in writing, software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package es.upm.disnet.pubmed.experiment;

import com.google.common.collect.Multiset;
import es.upm.disnet.pubmed.model.jpa.Disease;
import es.upm.disnet.pubmed.model.jpa.DiseaseClass;
import es.upm.disnet.pubmed.model.jpa.PubMedDoc;
import es.upm.disnet.pubmed.model.jpa.Term;
import es.upm.disnet.pubmed.model.rest.DisnetResponse;
import es.upm.disnet.pubmed.model.rest.MetaMapTermsResponse;
import es.upm.disnet.pubmed.model.rest.TVPValidationResponse;
import es.upm.disnet.pubmed.parser.PubMedWebArticleParser;
import es.upm.disnet.pubmed.retriever.DisnetRetrievalService;
import es.upm.disnet.pubmed.retriever.MetaMapTermsRetrievalService;
import es.upm.disnet.pubmed.retriever.TVPValidationRetrievalService;
import es.upm.disnet.pubmed.service.DiseaseClassService;
import es.upm.disnet.pubmed.service.DiseaseService;
import es.upm.disnet.pubmed.service.PubMedDocService;
import es.upm.disnet.pubmed.service.TermService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.inference.TTest;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.metrics.GeneralizedJaccard;
import org.simmetrics.metrics.SimonWhite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Eduardo P. Garcia del Valle
 */
@Component
public class PubMedDiseaseDistanceExperiment {

    private static final Logger logger = LoggerFactory.getLogger(PubMedDiseaseDistanceExperiment.class);

    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private DiseaseClassService diseaseClassService;
    @Autowired
    private PubMedWebArticleParser pubMedWebArticleParser;
    @Autowired
    private PubMedDocService pubMedDocService;
    @Autowired
    private MetaMapTermsRetrievalService metaMapTermsRetrievalService;
    @Autowired
    private DisnetRetrievalService disnetRetrievalService;
    @Autowired
    private TVPValidationRetrievalService tvpValidationRetrievalService;
    @Autowired
    private TermService termService;

    private final static int MAX_COMBINATIONS = 20;
    private final static int MAX_SET_SIZE = -1;
    private final static int MIN_TERM_COUNT = 5;
    private final static long DISEASE_CLASS_LEVEL = 2;
    private final static int TEST_ITERATIONS = 1;
    private final static double ALPHA = 0.01;

    public void runExperiment(String pubMedSnapshot, String disnetSnapshot) {

        List<Disease> allDiseases = diseaseService.getAllDiseasesInDisnet();

        // Exploratory Analysis of Datasets

        /*
        for (String source : new String[]{Term.SOURCE_PUBMED, Term.SOURCE_DISNET}) {
            double average = getValidatedTermAverage(allDiseases, source);

            logger.info("Term average for source {} = {} ", source, average);
        }

        logger.info("PubMed average abstract length {} ", getPubMedAbstractLengthAverage(allDiseases));
        logger.info("PubMed average aggregated abstract length {} ", getPubMedAbstractAggregatedLengthAverage(allDiseases));
        */

        getPubMedDocInfoFile(allDiseases);

        List<Disease> diseases = allDiseases
                .stream()
                .filter(
                        disease -> ((termService.countByDisease(disease, Term.SOURCE_DISNET) >= MIN_TERM_COUNT) && (termService.countByDisease(disease, Term.SOURCE_PUBMED) >= MIN_TERM_COUNT)))
                .collect(Collectors.toList());

        Map<String, List<Disease>> sameDiseaseClasses = getSameDOClassDiseases(diseases, DISEASE_CLASS_LEVEL);

        logger.info("Disease classes count: {}", sameDiseaseClasses.size());

        List<Disease[]> allSameDOClassDiseasePairs = new ArrayList<>();

        for (Map.Entry<String, List<Disease>> entry : sameDiseaseClasses.entrySet()) {
            allSameDOClassDiseasePairs.addAll(getPairCombinations(entry.getValue(), -1));
        }

        logger.info("All same class disease combinations: {}", allSameDOClassDiseasePairs.size());

        getTermsDistributionFile(diseases);
        getTermsDistributionFileByDiseaseClass(sameDiseaseClasses);
        getTermsFile();

        List<Pvalue> pValues = new ArrayList();

        for (int i= 0; i<TEST_ITERATIONS; i++) {
            logger.info("Running {}", i);

            List<Disease[]> similarDiseasePairs = getSameDOClassDiseasePairs(allSameDOClassDiseasePairs, MAX_COMBINATIONS, MAX_SET_SIZE);
            List<Disease[]> randomDiseasePairs = getRandomDiseasePairs(diseases, similarDiseasePairs.size());

            writeResultsFile();

            for (String source : new String[]{Term.SOURCE_PUBMED, Term.SOURCE_DISNET}) {
                String[] sources = new String[]{source, source};

                // P-Values

                double[] cosineSimilaritiesSimilar = getCosineSimilarities(similarDiseasePairs, sources, false);
                double[] cosineSimilaritiesRandom = getCosineSimilarities(randomDiseasePairs, sources, false);

                double pValueCosineSimilarity = getPValue(cosineSimilaritiesSimilar, cosineSimilaritiesRandom);

                pValues.add(new Pvalue(pValueCosineSimilarity, source, Pvalue.COSINE));

                double[] jaccardSimilaritiesSimilar = getJaccardSimilarities(similarDiseasePairs, sources, false);
                double[] jaccardSimilaritiesRandom = getJaccardSimilarities(randomDiseasePairs, sources, false);

                double pValueJaccardSimilarity = getPValue(jaccardSimilaritiesSimilar, jaccardSimilaritiesRandom);

                pValues.add(new Pvalue(pValueJaccardSimilarity, source, Pvalue.JACCARD));

                double[] simonWhiteSimilaritiesSimilar = getSimonWhiteSimilarities(similarDiseasePairs, sources, false);
                double[] simonWhiteSimilaritiesRandom = getSimonWhiteSimilarities(randomDiseasePairs, sources, false);

                double pValueSimonWhiteSimilarity = getPValue(simonWhiteSimilaritiesSimilar, simonWhiteSimilaritiesRandom);

                pValues.add(new Pvalue(pValueSimonWhiteSimilarity, source, Pvalue.DICE));
            }

            List<Disease[]> sameDiseasePairs = geSameDiseasePairs(diseases, similarDiseasePairs.size());

            String[] sources = new String[]{Term.SOURCE_PUBMED, Term.SOURCE_DISNET};

            // P-Values

            double[] cosineSimilaritiesSame = getCosineSimilarities(sameDiseasePairs, sources, false);
            double[] cosineSimilaritiesRandom = getCosineSimilarities(randomDiseasePairs, sources, false);

            double pValueCosineSimilarity = getPValue(cosineSimilaritiesSame, cosineSimilaritiesRandom);

            pValues.add(new Pvalue(pValueCosineSimilarity, Term.SOURCE_ALL, Pvalue.COSINE));

            double[] jaccardSimilaritiesSame = getJaccardSimilarities(sameDiseasePairs, sources, false);
            double[] jaccardSimilaritiesRandom = getJaccardSimilarities(randomDiseasePairs, sources, false);

            double pValueJaccardSimilarity = getPValue(jaccardSimilaritiesSame, jaccardSimilaritiesRandom);

            pValues.add(new Pvalue(pValueJaccardSimilarity, Term.SOURCE_ALL, Pvalue.JACCARD));

            double[] simonWhiteSimilaritiesSame = getSimonWhiteSimilarities(sameDiseasePairs, sources, false);
            double[] simonWhiteSimilaritiesRandom = getSimonWhiteSimilarities(randomDiseasePairs, sources, false);

            double pValueSimonWhiteSimilarity = getPValue(simonWhiteSimilaritiesSame, simonWhiteSimilaritiesRandom);

            pValues.add(new Pvalue(pValueSimonWhiteSimilarity, Term.SOURCE_ALL, Pvalue.DICE));
        }

        for (String source : new String[]{Term.SOURCE_PUBMED, Term.SOURCE_DISNET, Term.SOURCE_ALL}) {
            List<Pvalue> sourcePValues = pValues.stream().filter(pvalue -> pvalue.getSource().equals(source)).collect(Collectors.toList());

            double[] cosinePvalues = ArrayUtils.toPrimitive(sourcePValues.stream().filter(pvalue -> pvalue.getMethod().equals(Pvalue.COSINE)).map(pvalue -> pvalue.getValue()).toArray(Double[]::new));

            BenjaminiHochbergFDR benjaminiHochbergFDR = new BenjaminiHochbergFDR(cosinePvalues);

            benjaminiHochbergFDR.calculate();

            logger.info("{} Adjusted P-Value for Cosine: {}", source, benjaminiHochbergFDR.getAdjustedValue(ALPHA));

            double[] jaccardPvalues = ArrayUtils.toPrimitive(sourcePValues.stream().filter(pvalue -> pvalue.getMethod().equals(Pvalue.JACCARD)).map(pvalue -> pvalue.getValue()).toArray(Double[]::new));

            benjaminiHochbergFDR = new BenjaminiHochbergFDR(jaccardPvalues);

            benjaminiHochbergFDR.calculate();

            logger.info("{} Adjusted P-Value for Jaccard: {}", source, benjaminiHochbergFDR.getAdjustedValue(ALPHA));

            double[] dicePvalues = ArrayUtils.toPrimitive(sourcePValues.stream().filter(pvalue -> pvalue.getMethod().equals(Pvalue.DICE)).map(pvalue -> pvalue.getValue()).toArray(Double[]::new));

            benjaminiHochbergFDR = new BenjaminiHochbergFDR(dicePvalues);

            benjaminiHochbergFDR.calculate();

            logger.info("{} Adjusted P-Value for Dice: {}", source, benjaminiHochbergFDR.getAdjustedValue(ALPHA));
        }
    }

    public class Pvalue {
        public final static String COSINE = "COSINE";
        public final static String JACCARD = "JACCARD";
        public final static String DICE = "DICE";

        public Pvalue(double value, String source, String method) {
            this.value = value;
            this.source = source;
            this.method = method;
        }

        public double getValue() {
            return value;
        }

        public String getSource() {
            return source;
        }

        public String getMethod() {
            return method;
        }

        private double value;
        private String source;
        private String method;
    }

    private double[] getCosineSimilarities(List<Disease[]> diseases, String[] sources, boolean expandFrequency) {
        return ArrayUtils.toPrimitive(
                diseases.stream().map(dp -> computeCosineSimilarity(dp, sources, expandFrequency)).toArray(Double[]::new));
    }

    private double[] getJaccardSimilarities(List<Disease[]> diseases, String[] sources, boolean expandFrequency) {
        return ArrayUtils.toPrimitive(
                diseases.stream().map(dp -> computeJaccardSimilarity(dp, sources, expandFrequency)).toArray(Double[]::new));
    }

    private double[] getSimonWhiteSimilarities(List<Disease[]> diseases, String[] sources, boolean expandFrequency) {
        return ArrayUtils.toPrimitive(
                diseases.stream().map(dp -> computeSimonWhiteSimilarity(dp, sources, expandFrequency)).toArray(Double[]::new));
    }

    private double computeCosineSimilarity(Disease[] diseasePair, String[] sources, boolean expandFrequency) {
        Multiset<String> cuis0 = termService.getAllTermCuisByDisease(diseasePair[0], sources[0], expandFrequency);
        Multiset<String> cuis1 = termService.getAllTermCuisByDisease(diseasePair[1], sources[1], expandFrequency);

        CosineSimilarity cosineSimilarity = new org.simmetrics.metrics.CosineSimilarity();

        float similarity = cosineSimilarity.compare(cuis0, cuis1);

        writeToResultsFile("Cosine",sources, diseasePair, similarity);

        return similarity;
    }

    private double computeJaccardSimilarity(Disease[] diseasePair, String[] sources, boolean expandFrequency) {
        Multiset<String> cuis0 = termService.getAllTermCuisByDisease(diseasePair[0], sources[0], expandFrequency);
        Multiset<String> cuis1 = termService.getAllTermCuisByDisease(diseasePair[1], sources[1], expandFrequency);

        // Jaccard
        GeneralizedJaccard generalizedJaccard = new org.simmetrics.metrics.GeneralizedJaccard<>();

        float similarity = generalizedJaccard.compare(cuis0, cuis1);

        writeToResultsFile("Jaccard",sources, diseasePair, similarity);

        return similarity;
    }

    private double computeSimonWhiteSimilarity(Disease[] diseasePair, String[] sources, boolean expandFrequency) {
        Multiset<String> cuis0 = termService.getAllTermCuisByDisease(diseasePair[0], sources[0], expandFrequency);
        Multiset<String> cuis1 = termService.getAllTermCuisByDisease(diseasePair[1], sources[1], expandFrequency);

        // Simon White = Dice
        SimonWhite simonWhite = new org.simmetrics.metrics.SimonWhite<>();

        float similarity = simonWhite.compare(cuis0, cuis1);

        writeToResultsFile("Dice", sources, diseasePair, similarity);

        return similarity;
    }

    private double getPValue(double[] observations0, double[] observations1) {
        TTest tTest = new TTest();

        return tTest.tTest(observations0, observations1);
    }

    private Map<String, List<Disease>> getSameDOClassDiseases(List<Disease> diseases, long classLevel) {
        Map<String, List<Disease>> diseaseClasses = new HashMap();

        for (Disease disease :  diseases) {
            Collection<String> doIsAAsCollection = disease.getDoIsAIdAsCollection();

            for (String doIsA : doIsAAsCollection) {
                DiseaseClass parentDiseaseClass = diseaseClassService.getParentDiseaseClass(doIsA, classLevel);

                if (parentDiseaseClass == null) {
                    continue;
                }

                List<Disease> doDiseases = diseaseClasses.get(parentDiseaseClass.getDoId());

                if (doDiseases == null) {
                    doDiseases = new ArrayList();

                    diseaseClasses.put(parentDiseaseClass.getDoId(), doDiseases);
                }

                doDiseases.add(disease);
            }
        }

        return diseaseClasses;
    }

    private List<Disease[]> getSameDOClassDiseasePairs(List<Disease[]> allSameDOClassDiseasePairs, int maxCombinations, int size) {
        size = (size > 0) ? Math.min(allSameDOClassDiseasePairs.size(), size) : allSameDOClassDiseasePairs.size();

        List<Disease[]> sameDOClassDiseasePairs = new ArrayList<>();

        // Set seed for reproducibility
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int index = rand.nextInt(allSameDOClassDiseasePairs.size());

            sameDOClassDiseasePairs.add(allSameDOClassDiseasePairs.get(index));
        }

        return sameDOClassDiseasePairs;
    }


    private List<Disease[]> getRandomDiseasePairs(List<Disease> diseases, int size) {
        List<Disease[]> randomDiseasePairs = new ArrayList<>();

        // Set seed for reproducibility
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int index0 = rand.nextInt(diseases.size());
            int index1 = rand.nextInt(diseases.size());

            randomDiseasePairs.add(new Disease[]{diseases.get(index0), diseases.get(index1)});
        }

        return randomDiseasePairs;
    }

    private List<Disease[]> geSameDiseasePairs(List<Disease> diseases, int size) {
        List<Disease[]> randomDiseasePairs = new ArrayList<>();

        // Set seed for reproducibility
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            int index = rand.nextInt(diseases.size());

            randomDiseasePairs.add(new Disease[]{diseases.get(index), diseases.get(index)});
        }

        return randomDiseasePairs;
    }

    private List<Disease[]> getPairCombinations(List<Disease> diseases, int maxCombinations) {
        if (diseases.size() < 2) {
            return Collections.emptyList();
        }

        List<Disease[]> combinations = new ArrayList();

        outerlop:
        for (int i=0; i<diseases.size(); i++) {
            for (int j=i+1; j<diseases.size(); j++) {
                combinations.add(new Disease[] {diseases.get(i), diseases.get(j)});

                if (maxCombinations > 0 && combinations.size() >= maxCombinations) {
                    break outerlop;
                }
            }
        }

        return combinations;
    }

    private double getValidatedTermAverage(List<Disease> diseases, String source) {
        return diseases
                .stream()
                .mapToDouble(disease-> termService.getAllTermsByDisease(disease, source, true).size())
                .average()
                .getAsDouble();
    }

    private double getPubMedAbstractLengthAverage(List<Disease> diseases) {
        return diseases
                .stream()
                .mapToDouble(
                        disease-> pubMedDocService.getAllPubMedDocsByDisease(disease)
                                .stream()
                                .mapToDouble(pubMedDoc -> pubMedDoc.getAbstractText() != null ? pubMedDoc.getAbstractText().length() : 0)
                                .average()
                                .orElse(0))
                .average()
                .getAsDouble();
    }

    private double getPubMedAbstractAggregatedLengthAverage(List<Disease> diseases) {
        return diseases
                .stream()
                .mapToDouble(
                        disease-> pubMedDocService.getPubMedAggregatedAbstractText(disease).length())
                .average()
                .getAsDouble();
    }

    private void getTermsFile() {
        StringBuilder sb = new StringBuilder();

        sb.append("Disease Name");
        sb.append('|');
        sb.append("MeSH UI");
        sb.append('|');
        sb.append("DO Class");
        sb.append('|');
        sb.append("Term Name");
        sb.append('|');
        sb.append("Term CUI");
        sb.append('|');
        sb.append("Term Frequency");
        sb.append('|');
        sb.append("Term Source");
        sb.append('|');
        sb.append("Term Type");
        sb.append('\n');

        try (PrintWriter pw = new PrintWriter(new File("terms.csv"))) {
            for (Disease disease : diseaseService.getAllDiseasesInDisnet()) {
                for (Term term : termService.getAllTermsByDisease(disease)) {
                    if (!term.isValidated()) {
                        continue;
                    }

                    sb.append(disease.getDoName());
                    sb.append('|');
                    sb.append(disease.getMeSHUI());
                    sb.append('|');
                    sb.append(disease.getDoIsA());
                    sb.append('|');
                    sb.append(term.getName());
                    sb.append('|');
                    sb.append(term.getCui());
                    sb.append('|');
                    sb.append(term.getFrequency());
                    sb.append('|');
                    sb.append(term.getSource());
                    sb.append('|');
                    sb.append(term.getTypes());
                    sb.append('\n');
                }
            }

            pw.write(sb.toString());
        }
        catch(FileNotFoundException fnfe) {
        }
    }

    private void getTermsDistributionFile(List<Disease> diseases) {
        StringBuilder sb = new StringBuilder();

        sb.append("Disease Name");
        sb.append('|');
        sb.append("DO Class");
        sb.append('|');
        sb.append("Term Source");
        sb.append('|');
        sb.append("Validated Term Count");
        sb.append('\n');

        try (PrintWriter pw = new PrintWriter(new File("terms_distribution.csv"))) {
            for (Disease disease : diseases) {
                for (String source : new String[]{Term.SOURCE_DISNET, Term.SOURCE_PUBMED}){
                    List<Term> allTermsByDisease = termService.getAllTermsByDisease(disease, source, true);

                    sb.append(disease.getDoName());
                    sb.append('|');
                    sb.append(disease.getDoIsA());
                    sb.append('|');
                    sb.append(source);
                    sb.append('|');
                    sb.append(allTermsByDisease.size());
                    sb.append('\n');
                }
            }
            pw.write(sb.toString());
        }
        catch(FileNotFoundException fnfe) {
        }
    }

    private void getTermsDistributionFileByDiseaseClass(Map<String, List<Disease>> sameDiseaseClasses) {
        StringBuilder sb = new StringBuilder();

        sb.append("DO Class");
        sb.append('|');
        sb.append("Term Source");
        sb.append('|');
        sb.append("Validated Term Count");
        sb.append('\n');

        try (PrintWriter pw = new PrintWriter(new File("terms_distribution_classes.csv"))) {
            for (Map.Entry<String, List<Disease>> entry : sameDiseaseClasses.entrySet()) {
                DiseaseClass diseaseClass = diseaseClassService.getDiseaseClassByDoId(entry.getKey());

                for (String source : new String[]{Term.SOURCE_DISNET, Term.SOURCE_PUBMED}) {
                    sb.append(diseaseClass.getDoName() + "(" + diseaseClass.getDoId() + ")");
                    sb.append('|');
                    sb.append(source);
                    sb.append('|');
                    sb.append(getValidatedTermAverage(entry.getValue(), source));
                    sb.append('\n');
                }

            }
            pw.write(sb.toString());
        }
        catch(FileNotFoundException fnfe) {
        }
    }

    private void getPubMedDocInfoFile(List<Disease> diseases) {
        StringBuilder sb = new StringBuilder();

        sb.append("Document title");
        sb.append('\t');
        sb.append("Document DOI");
        sb.append('\t');
        sb.append("PubMed URL");
        sb.append('\t');
        sb.append("Disease");
        sb.append('\t');
        sb.append("Disease MeSH UI");
        sb.append('\n');

        try (PrintWriter pw = new PrintWriter(new File("pubMed_docs.csv"))) {
            for (Disease disease : diseases) {
                for (PubMedDoc pubMedDoc : pubMedDocService.getAllPubMedDocsByDisease(disease)){
                    sb.append(pubMedDoc.getTitleText());
                    sb.append('\t');
                    sb.append(pubMedDoc.getDoi());
                    sb.append('\t');
                    sb.append(pubMedDoc.getPmArticleURL());
                    sb.append('\t');
                    sb.append(disease.getDoName());
                    sb.append('\t');
                    sb.append(disease.getMeSHUI());
                    sb.append('\n');
                }
            }
            pw.write(sb.toString());
        }
        catch(FileNotFoundException fnfe) {
        }
    }

    private void writeResultsFile() {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(
                new File("results.csv"),
                false))) {

            StringBuilder sb = new StringBuilder();

            sb.append("Disease 0 Class");
            sb.append('|');
            sb.append("Disease 0 MeshUI");
            sb.append('|');
            sb.append("Disease 0 Name");
            sb.append('|');
            sb.append("Disease 1 Class");
            sb.append('|');
            sb.append("Disease 1 MeshUI");
            sb.append('|');
            sb.append("Disease 1 Name");
            sb.append('|');
            sb.append("Source");
            sb.append('|');
            sb.append("Method");
            sb.append('|');
            sb.append("Similarity");
            sb.append('\n');

            pw.print(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeToResultsFile(String method, String[] sources, Disease[] diseasePair, double similarity) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(
                new File("results.csv"),
                true))) {

            StringBuilder sb = new StringBuilder();

            sb.append(diseasePair[0].getDoIsA());
            sb.append('|');
            sb.append(diseasePair[0].getMeSHUI());
            sb.append('|');
            sb.append(diseasePair[0].getDoName());
            sb.append('|');
            sb.append(diseasePair[1].getDoIsA());
            sb.append('|');
            sb.append(diseasePair[1].getMeSHUI());
            sb.append('|');
            sb.append(diseasePair[1].getDoName());
            sb.append('|');
            sb.append(sources[0].equals(sources[1]) ? sources[0] : "Both");
            sb.append('|');
            sb.append(method);
            sb.append('|');
            sb.append(similarity);
            sb.append('\n');

            pw.print(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
