/*
 * First you have to add weka.jar to your project external libraries
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.Duration;
import java.util.*;

import weka.associations.*;

import weka.associations.AssociationRules;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Run {
    public static void main(String[] args) throws Exception {
        // Question 3
        double[] supArr = new double[]{0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13, 0.14, 0.15};
        ArrayList<Long> FPGrowthTimes = new ArrayList<>();
        ArrayList<Long> aprioriTimes = new ArrayList<>();
        System.out.println("Weka loaded.");

        //load dataset
        String dataset = "supermarket.arff";
        DataSource source = new DataSource(dataset);
        //get instance object
        Instances data = source.getDataSet();

        for (double sup : supArr) {
            String[] options = {"-N", "100000", "-C", "0.9", "-D", "0.01", "-M", "" + sup};
//        String[] options = {"-N", "100000", "-C", "0.9", "-D", "0.01", "-U", "0.16", "-M", "0.05"};

            Instant start = Instant.now();

            FPGrowth fpgrowth_model = new FPGrowth();
            fpgrowth_model.setOptions(options);
            fpgrowth_model.buildAssociations(data);
            AssociationRules ARS = fpgrowth_model.getAssociationRules();
            List<AssociationRule> ruleList = ARS.getRules();

            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
            System.out.println("FP-growth for min_sup: " + sup + " takes " + timeElapsed + "millis");
            FPGrowthTimes.add(timeElapsed);


            Instant start2 = Instant.now();

            Apriori apriori_model = new Apriori();
            apriori_model.setOptions(options);
            apriori_model.buildAssociations(data);
            AssociationRules ARS_ap = apriori_model.getAssociationRules();
            List<AssociationRule> ruleList_ap = ARS_ap.getRules();

            Instant finish2 = Instant.now();
            long timeElapsed2 = Duration.between(start2, finish2).toMillis();  //in millis
            System.out.println("Apriori for min_sup: " + sup + " takes " + timeElapsed2 + "millis");
            aprioriTimes.add(timeElapsed2);
        }


        File txtFile = new File("times.txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(txtFile));
            for (int i = 0; i < supArr.length; i++) {
                writer.write(supArr[i] + ", ");
            }
            writer.write("\n");
            for (int i = 0; i < FPGrowthTimes.size(); i++) {
                writer.write(FPGrowthTimes.get(i) + ", ");
            }
            writer.write("\n");
            for (int i = 0; i < aprioriTimes.size(); i++) {
                writer.write(aprioriTimes.get(i) + ", ");
            }
            writer.write("\n");
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
