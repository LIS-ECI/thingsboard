/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Carlos Ramirez
 */
@Configuration
public abstract class MongoConnection {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private GridFSBucket gridFSBucket;
    
    private String host;
    private String port;
    private String database;    

    @Autowired
    private ServerProperties serverProperties;

    public MongoConnection() {
        Properties prop = new Properties();
        try{
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
            prop.load(inputStream);
            host = prop.getProperty("mongodb.host");
            port = prop.getProperty("mongodb.port");
            database = prop.getProperty("mongodb.database");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public MongoClient getSession() {
        if (mongoClient == null) {
            //mongoClient = new MongoClient(new MongoClientURI(serverProperties.getMongoURI()));
            mongoClient = new MongoClient(new MongoClientURI("mongodb://"+host+":"+port));
        }
        return mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        if (mongoDatabase == null) {
            org.bson.codecs.configuration.CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            //mongoDatabase = getSession().getDatabase(serverProperties.getMongoDB());
            mongoDatabase = getSession().getDatabase(database).withCodecRegistry(pojoCodecRegistry);
        }
        return mongoDatabase;
    }

    public MongoDatabase getMongoDatabaseByName(String databaseName) {
        if (mongoDatabase == null) {
            org.bson.codecs.configuration.CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            mongoDatabase = getSession().getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
        }
        return mongoDatabase;
    }

    public List<String> getListCollectionsNames() {
        List<String> collectionsNames = new ArrayList<>();
        for (String name : mongoDatabase.listCollectionNames()) {
            collectionsNames.add(name);
        }
        return collectionsNames;
    }

    public void dropCollection(String nameCollection) throws MongoDBException {
        if (getListCollectionsNames().contains(nameCollection)) {
            MongoCollection<org.bson.Document> collection = mongoDatabase.getCollection(nameCollection);
            collection.drop();
        } else {
            throw new MongoDBException("Collection not exist!!");
        }
    }

    public GridFSBucket getGridFSDatabase(){
        if(gridFSBucket == null){
            gridFSBucket = GridFSBuckets.create(getMongoDatabase());
        }
        return gridFSBucket;
    }

}
