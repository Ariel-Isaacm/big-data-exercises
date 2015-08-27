package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ariel Isaac Machado on 18/08/15.
 */

public class MovieRecommender {
    private CustomFileDataModel model;
    private int lines;
    private BiMap<String, String> users;
    private BiMap<String, String> products;
    private UserBasedRecommender recommender;

    public MovieRecommender(String path) throws IOException, TasteException {
      
            model = new CustomFileDataModel(new File(path));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(.1, similarity, model);
            recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        lines = model.getCount();
        users = model.getUsers();
        products = model.getProducts();
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


    public List<String> getRecommendationsForUser(String userId) throws TasteException {
        List<RecommendedItem> recommendations = null;
            recommendations = recommender.recommend(Long.parseLong(users.get(userId)), 100000);
        List<String> recomendationsForUser = new ArrayList();
        for (RecommendedItem item : recommendations) {
            recomendationsForUser.add(products.inverse().get(item.getItemID() + ""));
        }
        return recomendationsForUser;
    }

}



