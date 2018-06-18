/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import org.thingsboard.server.common.data.SpatialLandlot;
import org.thingsboard.server.common.data.SpatialDevice;
import org.thingsboard.server.common.data.SpatialFarm;

import java.util.List;

/**
 *
 * @author Carlos Ramirez
 */
public interface SpatialIndexes {
    
    public SpatialFarm findFarmsByDeviceId(String device_id) throws MongoDBException;
    
    public SpatialLandlot findLandlotsByDeviceId(String device_id) throws MongoDBException;
    
    public SpatialDevice getCoordenatesByDeviceId(String device_id) throws MongoDBException;

    public String getTokenByIdLandlotTopic(String idLandlot, String topic) throws MongoDBException;
}
