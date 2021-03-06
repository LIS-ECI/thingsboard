/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import com.mongodb.Block;
import org.thingsboard.server.common.data.SparkDevice;
import org.thingsboard.server.common.data.SpatialLandlot;
import org.thingsboard.server.common.data.SpatialDevice;
import org.thingsboard.server.common.data.SpatialFarm;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.List;

/**
 *
 * @author Carlos Ramirez
 */
public class MongoDBSpatial extends MongoConnection implements SpatialIndexes {

    private final MongoDBSpatialLandlot mongodblandlot;
    private final MongoDBSpatialFarm mongodbFarm;
    private final MongoDBSpatialDevice mongodbDevice;
    private final MongoDBSpatialSpark mongodbspark;
    private final MongoDbImage mongodbimage;
    private final MongoDBTagLandlot mongodbTag;

    public MongoDBSpatial() {
        mongodblandlot = new MongoDBSpatialLandlot();
        mongodbFarm = new MongoDBSpatialFarm();
        mongodbDevice = new MongoDBSpatialDevice();
        mongodbspark = new MongoDBSpatialSpark();
        mongodbimage = new MongoDbImage(mongodblandlot);
        mongodbTag = new MongoDBTagLandlot(mongodbimage);
    }

    public MongoDBSpatialLandlot getMongodblandlot() {
        return mongodblandlot;
    }

    public MongoDBSpatialFarm getMongodbFarm() {
        return mongodbFarm;
    }

    public MongoDBSpatialDevice getMongodbDevice() {
        return mongodbDevice;
    }
    public MongoDBSpatialSpark getMongodbspark() {
        return mongodbspark;
    }
    
    public MongoDbImage getMongodbimage() { 
        return mongodbimage; 
    }

    public MongoDBTagLandlot getMongodbTag() {
        return mongodbTag;
    }

    @Override
    public SpatialFarm findFarmsByDeviceId(String device_id) throws MongoDBException {
        try {
            SpatialDevice sdt = mongodbDevice.findById(device_id);
            SpatialLandlot sct = mongodblandlot.findById(sdt.getDevice_Landlot_FK());
            return mongodbFarm.findById(sct.getLandlot_Farm_FK());
        } catch (NullPointerException ex) {
            throw new MongoDBException("It wasn´t posible to load the farm associated with device!!");
        }
    }

    @Override
    public SpatialLandlot findLandlotsByDeviceId(String device_id) throws MongoDBException {
        try {
            SpatialDevice sdt = mongodbDevice.findById(device_id);
            return mongodblandlot.findById(sdt.getDevice_Landlot_FK());
        } catch (NullPointerException ex) {
            throw new MongoDBException("It wasn´t posible to load the landlot associated with device!!");
        }
    }

    @Override
    public SpatialDevice getCoordenatesByDeviceId(String device_id) throws MongoDBException {
        return mongodbDevice.findById(device_id);
    }

    @Override
    public String getTokenByIdLandlotTopic(String idLandlot, String topic) throws MongoDBException {
        StringBuilder token = new StringBuilder();
        System.out.println("get token: idLandlot:"+idLandlot+" topic: "+topic);
        mongodbspark.getCollectionDependClass().find(and(eq("idLandlot",idLandlot),eq("topic",topic))).forEach((Block<SparkDevice>) sparkDevice -> {
            token.append(sparkDevice.getId());
        });
        return token.toString();
    }



}
