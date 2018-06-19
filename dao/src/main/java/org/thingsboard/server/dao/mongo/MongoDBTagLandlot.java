/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import com.mongodb.Block;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.result.DeleteResult;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.thingsboard.server.common.data.TagLandlot;

/**
 *
 * @author carlos
 */
public class MongoDBTagLandlot extends MongoConnectionPOJO<TagLandlot> implements DaoMongo<TagLandlot>{

    @Override
    public MongoCollection<TagLandlot> getCollectionDependClass() {
        return this.getMongoDatabase().getCollection("TagLandlot", TagLandlot.class);
    }

    @Override
    public List<TagLandlot> find() {
        MongoCollection<TagLandlot> tagCollection = getCollectionDependClass();
        List<TagLandlot> resultSet = new CopyOnWriteArrayList<>();
        tagCollection.find().forEach((Block<TagLandlot>) tag -> {
            resultSet.add(tag);
        });
        return resultSet;
    }

    @Override
    public TagLandlot findById(String id) {
        return getCollectionDependClass().find(eq("_id", id)).first();
    }

    @Override
    public TagLandlot save(TagLandlot t) {
        try {
            getCollectionDependClass().insertOne(t);
            return t;
        } catch (MongoWriteException ex) {
            System.out.println("No fue posible agregar la etiqueta del landlot");
        }
        return null;
    }

    @Override
    public boolean removeById(String id) {
        DeleteResult deleteResult = getCollectionDependClass().deleteMany(eq("_id", id));
        return deleteResult.getDeletedCount() >= 1;
    }
    
}
