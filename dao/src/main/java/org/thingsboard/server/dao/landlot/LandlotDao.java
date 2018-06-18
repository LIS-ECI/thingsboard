/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.landlot;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.*;

import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.model.nosql.LandlotEntity;

/**
 *
 * @German Lopez
 */
public interface LandlotDao extends Dao<Landlot> {

    /**
     * Save or update landlot object
     *
     * @param landlot the landlot object
     * @return saved landlot object
     */
    Landlot save(Landlot landlot);



    /**
     * Find landlots by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of landlot objects
     */
    List<Landlot> findLandlotsByTenantId(UUID tenantId, TextPageLink pageLink);

    /**
     * Find landlots by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of landlot objects
     */
    List<Landlot> findLandlotsByTenantIdAndType(UUID tenantId, String type, TextPageLink pageLink);

    /**
     * Find landlots by tenantId and landlots Ids.
     *
     * @param tenantId the tenantId
     * @param landlotIds the landlot Ids
     * @return the list of landlot objects
     */
    ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> landlotIds);

    /**
     * Find landlots by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of landlot objects
     */
    List<Landlot> findLandlotsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, TextPageLink pageLink);

    /**
     * Find landlots by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of landlot objects
     */
    List<Landlot> findLandlotsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, TextPageLink pageLink);

    /**
     * Find landlots by tenantId, customerId and landlots Ids.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param landlotIds the landlot Ids
     * @return the list of landlot objects
     */
    ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> landlotIds);

    /**
     * Find landlots by tenantId and landlot name.
     *
     * @param tenantId the tenantId
     * @param name the landlot name
     * @return the optional landlot object
     */
    Optional<Landlot> findLandlotsByTenantIdAndName(UUID tenantId, String name);

    /**
     * Find tenants landlot types.
     *
     * @return the list of tenant landlot type objects
     */
    ListenableFuture<List<EntitySubtype>> findTenantLandlotTypesAsync(UUID tenantId);
    
    /**
     * Find all landlots by farm id
     * @param farmId
     * @return 
     */
    ListenableFuture<List<LandlotEntity>> findLandlotsByFarmId(String farmId);
    
    /**
     * Return all landlots
     * @return 
     */
    ListenableFuture<List<LandlotEntity>> allLandlots();




}