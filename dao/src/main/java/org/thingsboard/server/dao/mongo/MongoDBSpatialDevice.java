/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.thingsboard.server.common.data.SparkDevice;
import org.thingsboard.server.common.data.SpatialDevice;
import org.thingsboard.server.dao.Dao;

/**
 *
 * @author Carlos Ramirez
 */
public class MongoDBSpatialDevice extends MongoConnectionPOJO<SpatialDevice> implements DaoMongo<SpatialDevice> {

    @Override
    public MongoCollection<SpatialDevice> getCollectionDependClass() {
        return this.getMongoDatabase().getCollection("Devices", SpatialDevice.class);
    }

    @Override
    public List<SpatialDevice> find() {
        MongoCollection<SpatialDevice> deviceCollection = getCollectionDependClass();
        List<SpatialDevice> resultSet = new CopyOnWriteArrayList<>();
        deviceCollection.find().forEach((Block<SpatialDevice>) device -> {
            resultSet.add(device);
        });
        return resultSet;
    }



    @Override
    public SpatialDevice findById(String id) {
        return getCollectionDependClass().find(eq("_id", id)).first();
    }

    @Override
    public SpatialDevice save(SpatialDevice t) {
        try {
            getCollectionDependClass().insertOne(t);
            return t;
        } catch (MongoWriteException ex) {
            System.out.println("No fue posible agregar el device");
        }
        return null;
    }

    public SparkDevice saveSparkDevice(SparkDevice t){
        try{
            this.getMongoDatabase().getCollection("SparkDevice", SparkDevice.class).insertOne(t);
            return t;
        }catch (MongoWriteException ex){
            System.out.println("No fue posible agregar el spark device");
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        DeleteResult deleteResult = getCollectionDependClass().deleteMany(eq("_id", id));
        return deleteResult.getDeletedCount() >= 1;
    }

}
