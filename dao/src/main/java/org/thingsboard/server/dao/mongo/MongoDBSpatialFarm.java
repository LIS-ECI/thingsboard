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
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bson.Document;
import org.thingsboard.server.common.data.Polygon;
import org.thingsboard.server.common.data.SpatialFarm;

/**
 *
 * @author Carlos Ramirez
 */
public class MongoDBSpatialFarm extends MongoConnectionPOJO<SpatialFarm> implements DaoMongo<SpatialFarm> {

    @Override
    public MongoCollection<SpatialFarm> getCollectionDependClass() {
        return this.getMongoDatabase().getCollection("Farms", SpatialFarm.class);
    }

    @Override
    public List<SpatialFarm> find() {
        MongoCollection<SpatialFarm> deviceCollection = getCollectionDependClass();
        List<SpatialFarm> resultSet = new CopyOnWriteArrayList<>();
        deviceCollection.find().forEach((Block<SpatialFarm>) farm -> {
            resultSet.add(farm);
        });
        return resultSet;
    }

    @Override
    public SpatialFarm findById(String id) {
        return getCollectionDependClass().find(eq("_id", id)).first();
    }
    
    public boolean checkCropInFarm(Polygon polygon, String farmId) {
        List<SpatialFarm> resultSet = new CopyOnWriteArrayList<>();
        getCollectionDependClass().find(Filters.geoIntersects("polygons", new Document("type", "Polygon").append("coordinates", polygon.getCoordinates()))).forEach((Block<SpatialFarm>) farm -> {
            if(farm.getId().equals(farmId)){
                resultSet.add(farm);
            }
        });
        for (SpatialFarm spatialFarm : resultSet) {
            if(spatialFarm.getId().equals(farmId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public SpatialFarm save(SpatialFarm t) {
        try {
            getCollectionDependClass().insertOne(t);
            return t;
        } catch (MongoWriteException ex) {
            System.out.println("No fue posible agregar la farm");
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        DeleteResult deleteResult = getCollectionDependClass().deleteMany(eq("_id", id));
        return deleteResult.getDeletedCount() >= 1;
    }

}
