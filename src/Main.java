/* Fundamental Functions */
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/* Clustering */
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.Cobweb;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.clustering.FarthestFirst;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.clustering.KMeans;

/* Scoring */
import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.BICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;

public class Main
{
    public static void main(String[] args)
    {
        String pathStr = "iris/iris.data";//Path to iris dataset
        try
        {
            //Load the Iris dataset from file
            Dataset data = FileHandler.loadDataset(new File(pathStr), 4, ",");

            //Display some information about the dataset
            System.out.println("Dataset loaded successfully!");
            System.out.printf("Number of instances: %d\n", data.size());
            System.out.printf("Number of attributes: %d\n\n", data.noAttributes());

            //Choose clustering algorithms
            Clusterer kmeans = new KMeans();//Kmeans clustering
            Clusterer cob = new Cobweb();//Cobweb clustering
            Clusterer farfirst = new FarthestFirst();//Farthest-First clustering

            //Time measure
            long[] tTime = new long[3];

            long s;
            long e;

            Dataset[][] allClusters = new Dataset[3][];

            //Apply clustering algorithms and measure time
            s = System.nanoTime();
            allClusters[0] = kmeans.cluster(data);
            e = System.nanoTime();
            tTime[0] = e - s;

            s = System.nanoTime();
            allClusters[1] = cob.cluster(data);
            e = System.nanoTime();
            tTime[1] = e - s;

            s = System.nanoTime();
            allClusters[2] = farfirst.cluster(data);
            e = System.nanoTime();
            tTime[2] = e - s;

            //Scoring
            ClusterEvaluation aic = new AICScore();
            ClusterEvaluation bic = new BICScore();
            ClusterEvaluation sumSquared = new SumOfSquaredErrors();
            ClusterEvaluation sumPair = new SumOfAveragePairwiseSimilarities();

            double[] aicScore = new double[3];
            double[] bicScore = new double[3];
            double[] sumSqScore = new double[3];
            double[] sumPairScore = new double[3];

            for (int i = 0; i < 3; i++)
            {
                aicScore[i] = aic.score(allClusters[i]);
                bicScore[i] = bic.score(allClusters[i]);
                sumSqScore[i] = sumSquared.score(allClusters[i]);
                sumPairScore[i] = sumPair.score(allClusters[i]);
            }

            //Print the output of each algorithm
            System.out.println("~ Kmeans clusters ~");
            printClusters(allClusters[0]);
            System.out.println("~ Cobweb clusters ~");
            printClusters(allClusters[1]);
            System.out.println("~ Farthest-First clusters ~");
            printClusters(allClusters[2]);

            //Final tallies
            for (int i = 0; i < 3; i++)
            {
                String title;
                if (i == 0)
                    title = "Kmeans";
                else if (i == 1)
                    title = "Cobweb";
                else
                    title = "Farthest-First";

                System.out.println("~ " + title + " ~");
                System.out.printf("Cluster Count: %d\n", allClusters[i].length);
                printDataPointsPerCluster(allClusters[i]);
                System.out.printf("AIC Score: %.2f\n", aicScore[i]);
                System.out.printf("BIC Score: %.2f\n", bicScore[i]);
                System.out.printf("Sum of Squared Errors: %.2f\n", sumSqScore[i]);
                System.out.printf("Pairwise Similarities: %.2f\n", sumPairScore[i]);
                System.out.printf("Time (ms): %d\n", tTime[i]/100000);
                System.out.println();
            }
        }
        catch (Exception e)
        {
            System.out.println("ERROR: " + e.getMessage());
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    //Method to print clusters
    private static void printClusters(Dataset[] clusters)
    {
        for (int i = 0; i < clusters.length; i++)
        {
            System.out.println("Cluster " + (i + 1) + ":");
            for (int j = 0; j < clusters[i].size(); j++)
                System.out.println(clusters[i].get(j));
            System.out.println();
        }
    }

    //Method to print the number of data points per cluster
    private static void printDataPointsPerCluster(Dataset[] clusters)
    {
        //Initialize a HashMap to store the count of data points per cluster
        Map<Integer, Integer> clusterCounts = new HashMap<>();

        //Iterate over the clusters and count the number of data points in each cluster
        for (int i = 0; i < clusters.length; i++)
        {
            int clusterSize = clusters[i].size();
            clusterCounts.put(i, clusterSize);
        }

        //Print the number of data points per cluster
        System.out.println("Number of data points per cluster:");
        for (Map.Entry<Integer, Integer> entry : clusterCounts.entrySet())
            System.out.println("Cluster #" + (entry.getKey() + 1) + " --> " + entry.getValue() + " data points");
    }
}