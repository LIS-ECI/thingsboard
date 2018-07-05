/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.common.data.landlot;

import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.Polygon;
import org.thingsboard.server.common.data.SearchTextBasedWithAdditionalInfo;
import org.thingsboard.server.common.data.crop.Crop;
import org.thingsboard.server.common.data.farm.Area;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author German Lopez
 */
@EqualsAndHashCode(callSuper = true)
public class Landlot extends SearchTextBasedWithAdditionalInfo<LandlotId> implements HasName {

    private static final long serialVersionUID = 2807343040519543363L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String name;
    private String type;
    private String farmId;
    private Polygon location;
    private Crop crop;
    private List<Crop> cropsHistory;
    private Area totalArea;
    private GroundFeatures groundFeatures;
    private List<UUID> devices;


    public Landlot() {
        super();
    }

    public Landlot(LandlotId id) {
        super(id);
    }
    
    public Landlot(Landlot landlot){
        super(landlot);
        this.tenantId = landlot.getTenantId();
        this.customerId = landlot.getCustomerId();
        this.name = landlot.getName();
        this.type = landlot.getType();
        this.farmId = landlot.getFarmId();
        this.setCrop(landlot.getCrop());
        this.cropsHistory = landlot.getCropsHistory();
        this.totalArea = landlot.getTotalArea();
        this.groundFeatures = landlot.getGroundFeatures();
        this.devices=landlot.getDevices();
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @Override
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

    @Override
    public String getSearchText() {
        return getName();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Landlot [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", name=");
        builder.append(name);
        builder.append(", type=");
        builder.append(type);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append(", farmId=");
        builder.append(farmId);
        builder.append("]");
        return builder.toString();
    }

    /**
     * @return the farmId
     */
    public String getFarmId() {
        return farmId;
    }

    /**
     * @param farmId the farmId to set
     */
    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    /**
     * @return the location
     */
    public Polygon getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Polygon location) {
        this.location = location;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
    }

    public List<Crop> getCropsHistory() {
        return cropsHistory;
    }

    public void setCropsHistory(List<Crop> cropsHistory) {
        this.cropsHistory = cropsHistory;
    }

    public Area getTotalArea() {
        return totalArea;
    }

    public void setTotalArea(Area totalArea) {
        this.totalArea = totalArea;
    }

    public GroundFeatures getGroundFeatures() {
        return groundFeatures;
    }

    public void setGroundFeatures(GroundFeatures groundFeatures) {
        this.groundFeatures = groundFeatures;
    }

    public List<UUID> getDevices() {
        return devices;
    }

    public void setDevices(List<UUID> devices) {
        this.devices = devices;
    }
}
