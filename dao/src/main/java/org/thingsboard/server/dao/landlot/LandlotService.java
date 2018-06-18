/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.landlot;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.landlot.LandlotSearchQuery;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.FarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.model.nosql.LandlotEntity;

/**
 *
 * @author German Lopez
 */
public interface LandlotService {

    Landlot findLandlotById(LandlotId landlotId);

    Map<String, TreeMap<Long,Double>> getHistoricalValues(String landlotId, long minDate, long maxDate);

    ListenableFuture<Landlot> findLandlotByIdAsync(LandlotId landlotId);

    Optional<Landlot> findLandlotByTenantIdAndName(TenantId tenantId, String name);

    Landlot saveLandlot(Landlot landlot);

    Landlot assignLandlotToCustomer(LandlotId landlotId, CustomerId customerId);

    Landlot unassignLandlotFromCustomer(LandlotId landlotId);

    void deleteLandlot(LandlotId landlotId);

    TextPageData<Landlot> findLandlotsByTenantId(TenantId tenantId, TextPageLink pageLink);

    TextPageData<Landlot> findLandlotsByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink);

    ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndIdsAsync(TenantId tenantId, List<LandlotId> landlotIds);

    void deleteLandlotsByTenantId(TenantId tenantId);

    TextPageData<Landlot> findLandlotsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink);

    TextPageData<Landlot> findLandlotsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink);

    ListenableFuture<List<Landlot>> findLandlotsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<LandlotId> landlotIds);

    void unassignCustomerLandlots(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Landlot>> findLandlotsByQuery(LandlotSearchQuery query);

    ListenableFuture<List<EntitySubtype>> findLandlotTypesByTenantId(TenantId tenantId);
    
    ListenableFuture<List<LandlotEntity>> findLandlotsByFarmId(String farmId);
    
    ListenableFuture<List<LandlotEntity>> allLandlots();
}