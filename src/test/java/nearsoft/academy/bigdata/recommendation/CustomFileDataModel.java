package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.common.iterator.FileLineIterator;

import java.io.File;
import java.io.IOException;

/**
 * Created by fernando on 21/08/15.
 */
public class CustomFileDataModel extends FileDataModel {
    private BiMap<String, String> users;
    private BiMap<String, String> products;


    public CustomFileDataModel(File DataFile) throws IOException {
        super(DataFile);

    }

    @Override
    protected void processFile(FileLineIterator dataOrUpdateFileIterator, FastByIDMap<?> data, FastByIDMap<FastByIDMap<Long>> timestamps, boolean fromPriorData) {
        users = HashBiMap.create();
        products = HashBiMap.create();
        System.out.println("Reading file info...");
        int count = 0;
        String aux[] = {"", ""};
        while (dataOrUpdateFileIterator.hasNext()) {
            String line = dataOrUpdateFileIterator.next();
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
                processLine(users.get(aux[0]) + "," + products.get(aux[1]) + "," + line.substring(line.lastIndexOf(' ') + 1), data, timestamps, fromPriorData);
                if (++count % 1000000 == 0) {

                    System.out.println("Processed " + count + " lines");

                }


            }
        }
        System.out.println("Read lines: " + count);

    }


}


