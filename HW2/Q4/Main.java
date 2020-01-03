/*
 * First you have to add weka.jar to your project external libraries
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import weka.associations.*;

import weka.associations.AssociationRules;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Main {
    public static void main(String[] args) throws Exception {
        // Question 4:
        System.out.println("Weka loaded.");
        //load dataset
        String dataset = "supermarket.arff";
        DataSource source = new DataSource(dataset);
        //get instance object
        Instances data = source.getDataSet();

        String[] options = {"-N", "10000000", "-C", "0.0", "-D", "0.01", "-M", "0.1"};
        FPGrowth fpgrowth_model = new FPGrowth();
        fpgrowth_model.setOptions(options);
        fpgrowth_model.buildAssociations(data);
        AssociationRules ARS = fpgrowth_model.getAssociationRules();
        List<AssociationRule> ruleList = ARS.getRules();

//        ArrayList<Collection<BinaryItem>> frequentPatterns = new ArrayList<Collection<BinaryItem>>();
        HashMap<Collection<BinaryItem>, Integer> frequentPatterns = new HashMap<Collection<BinaryItem>, Integer>();

        for (int i = 0; i < ruleList.size(); i++) {

            AssociationRule AR = ruleList.get(i);

            Collection premise = AR.getPremise();
            int premiseSupport = AR.getPremiseSupport();

            Collection consequence = AR.getConsequence();
            int consequenceSupport = AR.getConsequenceSupport();

            int totalSupport = AR.getTotalSupport();
            Collection<BinaryItem> frequentPattern = new HashSet<BinaryItem>();
            Collection<BinaryItem> premiseFrequentPattern = new HashSet<BinaryItem>();
            Collection<BinaryItem> consequenceFrequentPattern = new HashSet<BinaryItem>();

            Iterator iterator = premise.iterator();
            while (iterator.hasNext()) {
                frequentPattern.add((BinaryItem) iterator.next());
            }

            iterator = consequence.iterator();
            while (iterator.hasNext()) {
                frequentPattern.add((BinaryItem) iterator.next());
            }

            frequentPatterns.put(frequentPattern, totalSupport);


            iterator = premise.iterator();
            while (iterator.hasNext()) {
                premiseFrequentPattern.add((BinaryItem) iterator.next());
            }
            if (premiseFrequentPattern.size() > 1) {
                frequentPatterns.put(premiseFrequentPattern, premiseSupport);
            }

            iterator = consequence.iterator();
            while (iterator.hasNext()) {
                consequenceFrequentPattern.add((BinaryItem) iterator.next());
            }
            if (consequenceFrequentPattern.size() > 1) {
                frequentPatterns.put(consequenceFrequentPattern, consequenceSupport);
            }
        }

        System.out.println("Number of frequent patterns: " + frequentPatterns.size());


        HashMap<Collection<BinaryItem>, Integer> closedPatterns = new HashMap<Collection<BinaryItem>, Integer>();
        for (Collection<BinaryItem> pattern1 : frequentPatterns.keySet()) {
            boolean flag = true;
            for (Collection<BinaryItem> pattern2 : frequentPatterns.keySet()) {
                if (!pattern1.equals(pattern2)) {
                    if (pattern2.containsAll(pattern1) && frequentPatterns.get(pattern2) >= frequentPatterns.get(pattern1))
                        flag = false;
                }
            }
            if (flag) {
                closedPatterns.put(pattern1, frequentPatterns.get(pattern1));
            }
        }
        System.out.println("Number of closed patterns: " + closedPatterns.size());
        fileWriter("closedPattern.txt", closedPatterns);

        HashMap<Collection<BinaryItem>, Integer> maxPatterns = new HashMap<Collection<BinaryItem>, Integer>();
        for (Collection<BinaryItem> pattern1 : frequentPatterns.keySet()) {
            boolean flag = true;
            for (Collection<BinaryItem> pattern2 : frequentPatterns.keySet()) {
                if (!pattern1.equals(pattern2)) {
                    if (pattern2.containsAll(pattern1))
                        flag = false;
                }
            }
            if (flag) {
                maxPatterns.put(pattern1, frequentPatterns.get(pattern1));
            }
        }
        System.out.println("Number of max patterns: " + maxPatterns.size());
        fileWriter("maxPattern.txt", maxPatterns);
    }

    public static void fileWriter(String pathName, HashMap<Collection<BinaryItem>, Integer> patterns) {
        File txtFile = new File(pathName);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(txtFile));
            Iterator hmIterator = patterns.entrySet().iterator();
            while (hmIterator.hasNext()) {
                Map.Entry mapElement = (Map.Entry) hmIterator.next();
                int sup = ((int) mapElement.getValue());
                writer.write(mapElement.getKey() + ", " + sup + "\n");
//                System.out.println("frequent pattern: " + mapElement.getKey() + ", sup: " + sup);
            }
            writer.flush();
        } catch (IOException err) {
            System.err.println(err);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException err) {
                System.err.println(err);
            }
        }
    }

}
