package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Ariel Isaac Machado on 18/08/15.
 */

public class MovieRecommender {
    private DataModel model;
    private int lines;
    private BiMap<String, String> users;
    private BiMap<String, String> products;
    private UserBasedRecommender recommender;
    private Logger LOGGER;
    public MovieRecommender(String path) {
        try {
            LOGGER = Logger.getLogger("System's Logger");
            model = new CustomFileDataModel(new File(path));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);

            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(.1, similarity, model);
            recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            lines = ((CustomFileDataModel) model).getCount();
            users = ((CustomFileDataModel) model).getUsers();
            products = ((CustomFileDataModel) model).getProducts();
        } catch (Exception e) {
            LOGGER.info("An exception has occured creating the model\n" + e);
        }
    }


    public int getTotalReviews() {
        return lines;
    }

    public int getTotalProducts() throws TasteException {
        return model.getNumItems();
    }

    public int getTotalUsers() throws TasteException {
        return model.getNumUsers();
    }


    public List<String> getRecommendationsForUser(String userId) {
        List<RecommendedItem> recommendations = null;
        try {
            recommendations = recommender.recommend(Long.parseLong(users.get(userId)), 100000);

        } catch (Exception e) {
            LOGGER.info("An exception has occurred getting recomendations \n" + e);
        }
        List<String> recomendaciones = new ArrayList();
        for (RecommendedItem item : recommendations) {
            recomendaciones.add(products.inverse().get(item.getItemID() + ""));
        }
        return recomendaciones;
    }

}



