/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.controller;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.landlot.LandlotSearchQuery;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.nosql.LandlotEntity;
import org.thingsboard.server.exception.ThingsboardErrorCode;
import org.thingsboard.server.exception.ThingsboardException;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.xml.soap.Text;

/**
 *
 * @author German Lopez
 */
@RestController
@RequestMapping("/api")
public class LandlotController extends BaseController {

    public static final String LANDLOT_ID = "landlotId";

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/{landlotId}", method = RequestMethod.GET)
    @ResponseBody
    public Landlot getLandlotById(@PathVariable(LANDLOT_ID) String strLandlotId) throws ThingsboardException {
        checkParameter(LANDLOT_ID, strLandlotId);
        try {
            LandlotId landlotId = new LandlotId(toUUID(strLandlotId));
            return checkLandlotId(landlotId);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/historical/{landlotId}/{minDate}/{maxDate}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, TreeMap<Long,Double>> getHistoricalValues(@PathVariable("landlotId") String landlotId, @PathVariable("minDate") String minDate, @PathVariable("maxDate") String maxDate) throws ThingsboardException {
        checkParameter("landlotId", landlotId);
        checkParameter("minDate", minDate);
        checkParameter("maxDate", maxDate);
        return landlotService.getHistoricalValues(landlotId, Long.parseLong(minDate), Long.parseLong(maxDate));
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/image/{landlotId}/{date}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody
    List<Image> getImages(@PathVariable("landlotId") String landlotId, @PathVariable("date") long date) throws ThingsboardException {
        try {
            return mongoService.getMongodbimage().downloadMapsFile(landlotId,date);

        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/files/{startDate}/{finishDate}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody List<Long> getFilesDates(@PathVariable("startDate") long startDate,@PathVariable("finishDate") long finishDate) throws ThingsboardException {
        System.out.println("Entró al controlador con las fechas: "+startDate+" "+finishDate);
        try {
            return mongoService.getMongodbimage().datesOfFilesLandlot(startDate,finishDate);

        } catch (Exception e) {
            throw handleException(e);
        }

    }
    
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot", method = RequestMethod.POST)
    @ResponseBody
    public Landlot saveLandlot(@RequestBody Landlot landlot) throws ThingsboardException {
        try {
            landlot.setTenantId(getCurrentUser().getTenantId());
            if (getCurrentUser().getAuthority() == Authority.CUSTOMER_USER) {
                if (landlot.getId() == null || landlot.getId().isNullUid()
                        || landlot.getCustomerId() == null || landlot.getCustomerId().isNullUid()) {
                    throw new ThingsboardException("You don't have permission to perform this operation!",
                            ThingsboardErrorCode.PERMISSION_DENIED);
                } else {
                    checkCustomerId(landlot.getCustomerId());
                }
            }
            Landlot savedLandlot = checkNotNull(landlotService.saveLandlot(landlot));
            SpatialLandlot spatialLandlot = new SpatialLandlot(savedLandlot.getId().getId().toString(), savedLandlot.getFarmId(), landlot.getLocation());
            if (landlot.getLocation() != null) {
                if (mongoService.getMongodbFarm().checkCropInFarm(landlot.getLocation(), landlot.getFarmId())) {
                    mongoService.getMongodblandlot().save(spatialLandlot);
                } else {
                    throw new IncorrectParameterException("Polygon not contains in farm!");
                }
            } else {
                mongoService.getMongodblandlot().save(spatialLandlot);
            }
            return savedLandlot;

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.LANDLOT), landlot,
                    null, landlot.getId() == null ? ActionType.ADDED : ActionType.UPDATED, e);
            throw handleException(e);
        }
    }
    
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/tag", method = RequestMethod.POST)
    @ResponseBody
    public TagLandlot saveTagLandlot(@RequestBody TagLandlot tagLandlot) throws ThingsboardException {
        if(tagLandlot != null){
            return mongoService.getMongodbTag().save(tagLandlot);
        }else{
            throw new ThingsboardException("The landlot tag is null",ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/landlot/{landlotId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteLandlot(@PathVariable(LANDLOT_ID) String strLandlotId) throws ThingsboardException {
        checkParameter(LANDLOT_ID, strLandlotId);
        try {
            LandlotId landlotId = new LandlotId(toUUID(strLandlotId));
            Landlot landlot = checkLandlotId(landlotId);
            landlotService.deleteLandlot(landlotId);
            mongoService.getMongodblandlot().removeById(strLandlotId);
            logEntityAction(landlotId, landlot,
                    landlot.getCustomerId(),
                    ActionType.DELETED, null, strLandlotId);

        } catch (Exception e) {
            logEntityAction(emptyId(EntityType.LANDLOT),
                    null,
                    null,
                    ActionType.DELETED, e, strLandlotId);
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/{customerId}/landlot/{landlotId}", method = RequestMethod.POST)
    @ResponseBody
    public Landlot assignLandlotToCustomer(@PathVariable("customerId") String strCustomerId,
            @PathVariable(LANDLOT_ID) String strLandlotId) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        checkParameter(LANDLOT_ID, strLandlotId);
        try {
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            Customer customer = checkCustomerId(customerId);

            LandlotId landlotId = new LandlotId(toUUID(strLandlotId));
            checkLandlotId(landlotId);

            Landlot savedLandlot = checkNotNull(landlotService.assignLandlotToCustomer(landlotId, customerId));

            logEntityAction(landlotId, savedLandlot,
                    savedLandlot.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strLandlotId, strCustomerId, customer.getName());

            return savedLandlot;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.LANDLOT), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strLandlotId, strCustomerId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/landlot/{landlotId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Landlot unassignLandlotFromCustomer(@PathVariable(LANDLOT_ID) String strLandlotId) throws ThingsboardException {
        checkParameter(LANDLOT_ID, strLandlotId);
        try {
            LandlotId landlotId = new LandlotId(toUUID(strLandlotId));
            Landlot landlot = checkLandlotId(landlotId);
            if (landlot.getCustomerId() == null || landlot.getCustomerId().getId().equals(ModelConstants.NULL_UUID)) {
                throw new IncorrectParameterException("Landlot isn't assigned to any customer!");
            }

            Customer customer = checkCustomerId(landlot.getCustomerId());

            Landlot savedLandlot = checkNotNull(landlotService.unassignLandlotFromCustomer(landlotId));

            logEntityAction(landlotId, landlot,
                    landlot.getCustomerId(),
                    ActionType.UNASSIGNED_FROM_CUSTOMER, null, strLandlotId, customer.getId().toString(), customer.getName());

            return savedLandlot;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.LANDLOT), null,
                    null,
                    ActionType.UNASSIGNED_FROM_CUSTOMER, e, strLandlotId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/customer/public/landlot/{landlotId}", method = RequestMethod.POST)
    @ResponseBody
    public Landlot assignLandlotToPublicCustomer(@PathVariable(LANDLOT_ID) String strLandlotId) throws ThingsboardException {
        checkParameter(LANDLOT_ID, strLandlotId);
        try {
            LandlotId landlotId = new LandlotId(toUUID(strLandlotId));
            Landlot landlot = checkLandlotId(landlotId);
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(landlot.getTenantId());
            Landlot savedLandlot = checkNotNull(landlotService.assignLandlotToCustomer(landlotId, publicCustomer.getId()));

            logEntityAction(landlotId, savedLandlot,
                    savedLandlot.getCustomerId(),
                    ActionType.ASSIGNED_TO_CUSTOMER, null, strLandlotId, publicCustomer.getId().toString(), publicCustomer.getName());

            return savedLandlot;
        } catch (Exception e) {

            logEntityAction(emptyId(EntityType.LANDLOT), null,
                    null,
                    ActionType.ASSIGNED_TO_CUSTOMER, e, strLandlotId);

            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/landlots", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Landlot> getTenantLandlots(
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length() > 0) {
                TextPageData<Landlot> landlots = landlotService.findLandlotsByTenantIdAndType(tenantId, type, pageLink);
                for (Landlot p : landlots.getData()) {
                    p.setLocation(mongoService.getMongodblandlot().findById(p.getId().getId().toString()).getPolygons());
                }
                return checkNotNull(landlots);
            } else {
                TextPageData<Landlot> landlots = landlotService.findLandlotsByTenantId(tenantId, pageLink);
                for (Landlot p : landlots.getData()) {
                    p.setLocation(mongoService.getMongodblandlot().findById(p.getId().getId().toString()).getPolygons());
                }
                return checkNotNull(landlots);
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    @RequestMapping(value = "/tenant/landlots", params = {"landlotName"}, method = RequestMethod.GET)
    @ResponseBody
    public Landlot getTenantLandlot(
            @RequestParam String landlotName) throws ThingsboardException {
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            return checkNotNull(landlotService.findLandlotByTenantIdAndName(tenantId, landlotName));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/customer/{customerId}/landlots", params = {"limit"}, method = RequestMethod.GET)
    @ResponseBody
    public TextPageData<Landlot> getCustomerLandlots(
            @PathVariable("customerId") String strCustomerId,
            @RequestParam int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String textSearch,
            @RequestParam(required = false) String idOffset,
            @RequestParam(required = false) String textOffset) throws ThingsboardException {
        checkParameter("customerId", strCustomerId);
        try {
            TenantId tenantId = getCurrentUser().getTenantId();
            CustomerId customerId = new CustomerId(toUUID(strCustomerId));
            checkCustomerId(customerId);
            TextPageLink pageLink = createPageLink(limit, textSearch, idOffset, textOffset);
            if (type != null && type.trim().length() > 0) {
                return checkNotNull(landlotService.findLandlotsByTenantIdAndCustomerIdAndType(tenantId, customerId, type, pageLink));
            } else {
                return checkNotNull(landlotService.findLandlotsByTenantIdAndCustomerId(tenantId, customerId, pageLink));
            }
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlots", params = {"landlotIds"}, method = RequestMethod.GET)
    @ResponseBody
    public List<Landlot> getLandlotsByIds(
            @RequestParam("landlotIds") String[] strLandlotIds) throws ThingsboardException {
        checkArrayParameter("landlotIds", strLandlotIds);
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            CustomerId customerId = user.getCustomerId();
            List<LandlotId> landlotIds = new ArrayList<>();
            for (String strLandlotId : strLandlotIds) {
                landlotIds.add(new LandlotId(toUUID(strLandlotId)));
            }
            ListenableFuture<List<Landlot>> landlots;
            if (customerId == null || customerId.isNullUid()) {
                landlots = landlotService.findLandlotsByTenantIdAndIdsAsync(tenantId, landlotIds);
            } else {
                landlots = landlotService.findLandlotsByTenantIdCustomerIdAndIdsAsync(tenantId, customerId, landlotIds);
            }
            return checkNotNull(landlots.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlots", method = RequestMethod.POST)
    @ResponseBody
    public List<Landlot> findByQuery(@RequestBody LandlotSearchQuery query) throws ThingsboardException {
        checkNotNull(query);
        checkNotNull(query.getParameters());
        checkNotNull(query.getLandlotTypes());
        checkEntityId(query.getParameters().getEntityId());
        try {
            List<Landlot> landlots = checkNotNull(landlotService.findLandlotsByQuery(query).get());
            landlots = landlots.stream().filter(landlot -> {
                try {
                    checkLandlot(landlot);
                    return true;
                } catch (ThingsboardException e) {
                    return false;
                }
            }).collect(Collectors.toList());
            return landlots;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/landlot/types", method = RequestMethod.GET)
    @ResponseBody
    public List<EntitySubtype> getLandlotTypes() throws ThingsboardException {
        try {
            SecurityUser user = getCurrentUser();
            TenantId tenantId = user.getTenantId();
            ListenableFuture<List<EntitySubtype>> landlotTypes = landlotService.findLandlotTypesByTenantId(tenantId);
            return checkNotNull(landlotTypes.get());
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/Alllandlots", method = RequestMethod.GET)
    @ResponseBody
    public List<Landlot> getAllLandlots() throws ThingsboardException {
        try {
            List<LandlotEntity> landlotTypes = landlotService.allLandlots().get();
            List<Landlot> landlots = new ArrayList<>();
            for (LandlotEntity fe : landlotTypes) {
                SpatialLandlot sp = mongoService.getMongodblandlot().findById(fe.getId().toString());
                Landlot p = fe.toData();
                p.setLocation(sp.getPolygons());
                landlots.add(p);
            }
            return landlots;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

}
