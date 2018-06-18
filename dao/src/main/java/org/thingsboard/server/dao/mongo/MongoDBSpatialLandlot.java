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
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.thingsboard.server.common.data.Point;
import org.thingsboard.server.common.data.SpatialLandlot;

/**
 *
 * @author Carlos Ramirez
 */
public class MongoDBSpatialLandlot extends MongoConnectionPOJO<SpatialLandlot> implements DaoMongo<SpatialLandlot> {

    public MongoDBSpatialLandlot(){
        getCollectionDependClass().createIndex(Indexes.geo2dsphere("polygons"));
    }

    public SpatialLandlot findNearestLandlot(Point p){
        SpatialLandlot nearSp = null;
        getCollectionDependClass().createIndex(Indexes.geo2dsphere("polygons"));
        Document doc = new Document("polygons",new Document("$near",new Document("$geometry",new Document("type", "Point").append("coordinates", p.getCoordinates()))));
        nearSp = getCollectionDependClass().find(doc).first();
        return nearSp;
    }

    @Override
    public MongoCollection<SpatialLandlot> getCollectionDependClass() {
        return this.getMongoDatabase().getCollection("Landlots", SpatialLandlot.class);
    }

    @Override
    public List<SpatialLandlot> find() {
        MongoCollection<SpatialLandlot> farmCollection = getCollectionDependClass();
        List<SpatialLandlot> resultSet = new CopyOnWriteArrayList<>();
        farmCollection.find().forEach((Block<SpatialLandlot>) landlot -> {
            resultSet.add(landlot);
        });
        return resultSet;
    }

    @Override
    public SpatialLandlot findById(String id) {
        return getCollectionDependClass().find(eq("_id", id)).first();
    }
    
    public boolean checkDeviceInLandlot(Point point, String farmId) {
        List<SpatialLandlot> resultSet = new CopyOnWriteArrayList<>();
        getCollectionDependClass().find(Filters.geoIntersects("polygons", new Document("type", "Point").append("coordinates", point.getCoordinates()))).forEach((Block<SpatialLandlot>) crop -> {
            if(crop.getId().equals(farmId)){
                resultSet.add(crop);
            }
        });
        for (SpatialLandlot spatialCrop : resultSet) {
            if(spatialCrop.getId().equals(farmId)){
                return true;
            }
        }
        return false;
    }

    @Override
    //Revisar que poligono del lote este contenido en el poligono de la finca
    public SpatialLandlot save(SpatialLandlot t){
        try{
            if(this.findById(t.getId()) == null){
                getCollectionDependClass().insertOne(t);
            }else{
                getCollectionDependClass().updateOne(eq("_id",t.getId()),new Document("$set", new Document("polygons", t.getPolygons())));
            }
            return t;
        }catch(MongoWriteException ex){
            System.out.println("No fue posible agregar el landlot");
            
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        DeleteResult deleteResult = getCollectionDependClass().deleteMany((Bson) eq("_id", id));
        return deleteResult.wasAcknowledged();
    }

}
