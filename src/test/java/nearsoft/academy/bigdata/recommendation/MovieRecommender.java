package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ariel Isaac Machado on 18/08/15.
 */

public class MovieRecommender {
    private DataModel model;
    private UserSimilarity similarity;
    private UserNeighborhood neighborhood;
    private UserBasedRecommender recommender;
    private List<RecommendedItem> recommendations;
    private int lineas;
    private BiMap<String, String> users;
    private BiMap<String, String> products;

    public MovieRecommender(String path) {
        try {

            users = HashBiMap.create();
            products = HashBiMap.create();

            filterFile();

            LineNumberReader lnr = new LineNumberReader(new FileReader(new File("dataSet.txt")));
            lnr.skip(Long.MAX_VALUE);
            lineas = lnr.getLineNumber();
            lnr.close();

            model = new FileDataModel(new File("dataSet.txt"));
            similarity = new PearsonCorrelationSimilarity(model);

            neighborhood = new ThresholdUserNeighborhood(.1, similarity, model);
            recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public int getTotalReviews() {
        return lineas;
    }

    public int getTotalProducts() throws TasteException {
        return model.getNumItems();
    }

    public int getTotalUsers() throws TasteException {
        return model.getNumUsers();
    }


    public List<String> getRecommendationsForUser(String userId) {
        try {

            recommendations = recommender.recommend(Long.parseLong(users.get(userId)), 100000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> recomendaciones = new ArrayList();
        for (RecommendedItem item : recommendations) {
            recomendaciones.add(products.inverse().get(item.getItemID() + ""));
        }
        return recomendaciones;
    }

    public void filterFile() throws IOException {

        InputStream fileStream = new FileInputStream("movies.txt.gz");
        InputStream gzipStream = new GZIPInputStream(fileStream);
        Reader decoder = new InputStreamReader(gzipStream);
        BufferedReader br = new BufferedReader(decoder);
        BufferedWriter bw = new BufferedWriter(new FileWriter("dataSet.txt"));

        String line;
        String aux[] = {"", ""};
        System.out.println("Procesando Datos crudos");
        while ((line = br.readLine()) != null) {

            if (line.contains("product/productId:")) {
                aux[1] = line.substring(line.lastIndexOf(' ') + 1);
                if (!products.containsKey(aux[1])) {
                    products.put(aux[1], products.size() + 1 + "");
                }
            } else if (line.contains("review/userId:")) {

                aux[0] = line.substring(line.lastIndexOf(' ') + 1);
                if (!users.containsKey(aux[0])) {
                    users.put(aux[0], users.size() + 1 + "");
                }
            } else if (line.contains("review/score:")) {
                bw.write(users.get(aux[0]) + "," + products.get(aux[1]) + "," + line.substring(line.lastIndexOf(' ') + 1) + "\n");

            }
        }
        br.close();
        bw.close();


    }


}



