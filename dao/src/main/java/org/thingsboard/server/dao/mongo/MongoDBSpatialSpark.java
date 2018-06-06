package org.thingsboard.server.dao.mongo;

import com.google.common.util.concurrent.ListenableFuture;
import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.DeleteResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.thingsboard.server.common.data.SparkDevice;
import org.thingsboard.server.dao.Dao;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


public class MongoDBSpatialSpark extends MongoConnectionPOJO<SparkDevice> implements DaoMongo<SparkDevice> {

    @Override
    public MongoCollection<SparkDevice> getCollectionDependClass() {
        return this.getMongoDatabase().getCollection("SparkDevice", SparkDevice.class);
    }

    @Override
    public List<SparkDevice> find() {
        MongoCollection<SparkDevice> sparkCollection = getCollectionDependClass();
        List<SparkDevice> resultSet = new CopyOnWriteArrayList<>();
        sparkCollection.find().forEach((Block<SparkDevice>) sparkDevice -> {
            resultSet.add(sparkDevice);
        });
        return resultSet;
    }

    @Override
    public SparkDevice findById(String id) {
        return getCollectionDependClass().find(eq("_id", id)).first();
    }

    @Override
    public SparkDevice save(SparkDevice t) {
        try {
            getCollectionDependClass().insertOne(t);
            return t;
        } catch (MongoWriteException ex) {
            System.out.println("No fue posible agregar el spark device");
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        DeleteResult deleteResult = getCollectionDependClass().deleteMany(eq("_id", id));
        return deleteResult.getDeletedCount() >= 1;
    }



    public List<SparkDevice> getSparkDevicesByParcelId(@PathVariable("parcelId") String strParcelId){
        List<SparkDevice> resultSet = new CopyOnWriteArrayList<>();
        FindIterable<SparkDevice> devices=this.getCollectionDependClass().find(eq("idParcel", strParcelId));
        devices.forEach((Block<SparkDevice>) device -> {
            resultSet.add(device);
        });
        return resultSet;


    }

}