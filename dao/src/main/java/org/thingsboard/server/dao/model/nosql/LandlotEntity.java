/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.model.nosql;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.thingsboard.server.common.data.crop.Crop;
import org.thingsboard.server.common.data.farm.Area;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.landlot.GroundFeatures;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.model.SearchTextEntity;
import org.thingsboard.server.dao.model.type.JsonCodec;

import static org.thingsboard.server.dao.model.ModelConstants.*;

/**
 *
 * @author German Lopez
 */
@Table(name = LANDLOT_COLUMN_FAMILY_NAME)
@EqualsAndHashCode
@ToString
public final class LandlotEntity implements SearchTextEntity<Landlot> {


    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    @PartitionKey(value = 0)
    @Column(name = ID_PROPERTY)
    private UUID id;

    @PartitionKey(value = 1)
    @Column(name = LANDLOT_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @PartitionKey(value = 2)
    @Column(name = LANDLOT_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @PartitionKey(value = 3)
    @Column(name = LANDLOT_TYPE_PROPERTY)
    private String type;

    @Column(name = LANDLOT_NAME_PROPERTY)
    private String name;
    
    @Column(name = LANDLOT_FARMID_PROPERTY)
    private String farmId;

    @Column(name = SEARCH_TEXT_PROPERTY)
    private String searchText;

    @com.datastax.driver.mapping.annotations.Column(name = LANDLOT_ADDITIONAL_INFO_PROPERTY, codec = JsonCodec.class)
    private JsonNode additionalInfo;

    @Column(name = LANDLOT_CROP)
    private String crop;

    @Column(name = LANDLOT_CROPS_HISTORY)
    private String cropsHistory;

    @Column(name = LANDLOT_TOTAL_AREA)
    private String totalArea;

    @Column(name = GROUND_FEATURES)
    private String groundFeatures;

    @Column(name = LANDLOT_DEVICES)
    private String devices;



    public LandlotEntity() {
        super();
    }

    public LandlotEntity(Landlot landlot) {
        if (landlot.getId() != null) {
            this.id = landlot.getId().getId();
        }
        if (landlot.getTenantId() != null) {
            this.tenantId = landlot.getTenantId().getId();
        }
        if (landlot.getCustomerId() != null) {
            this.customerId = landlot.getCustomerId().getId();
        }
        this.name = landlot.getName();
        this.type = landlot.getType();
        this.farmId = landlot.getFarmId();
        this.additionalInfo = landlot.getAdditionalInfo();
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.crop = mapper.writeValueAsString(landlot.getCrop());
            this.cropsHistory = mapper.writeValueAsString(landlot.getCropsHistory());
            this.totalArea = mapper.writeValueAsString(landlot.getTotalArea());
            this.groundFeatures = mapper.writeValueAsString(landlot.getGroundFeatures());
            this.setDevices(mapper.writeValueAsString(landlot.getDevices()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(JsonNode additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    public String getSearchTextSource() {
        return getName();
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public Landlot toData() {
        Landlot landlot = new Landlot(new LandlotId(id));
        landlot.setCreatedTime(UUIDs.unixTimestamp(id));
        if (tenantId != null) {
            landlot.setTenantId(new TenantId(tenantId));
        }
        if (customerId != null) {
            landlot.setCustomerId(new CustomerId(customerId));
        }
        landlot.setName(name);
        landlot.setType(getType());
        landlot.setFarmId(farmId);
        landlot.setAdditionalInfo(additionalInfo);
        try {
            ObjectMapper mapper = new ObjectMapper();
            landlot.setCrop(mapper.readValue(crop, Crop.class));
            landlot.setCropsHistory(mapper.readValue(cropsHistory, mapper.getTypeFactory().constructParametricType(List.class, Crop.class)));
            landlot.setTotalArea(mapper.readValue(totalArea, Area.class));
            landlot.setGroundFeatures(mapper.readValue(groundFeatures, GroundFeatures.class));
            landlot.setDevices(mapper.readValue(getDevices(), mapper.getTypeFactory().constructParametricType(List.class, UUID.class)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return landlot;
    }


    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getCropsHistory() {
        return cropsHistory;
    }

    public void setCropsHistory(String cropsHistory) {
        this.cropsHistory = cropsHistory;
    }

    public String getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(String totalArea) {
        this.totalArea = totalArea;
    }

    public String getGroundFeatures() {
        return groundFeatures;
    }

    public void setGroundFeatures(String groundFeatures) {
        this.groundFeatures = groundFeatures;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }
}
