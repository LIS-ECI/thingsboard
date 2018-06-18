/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.landlot;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.landlot.LandlotSearchQuery;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.FarmId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.TextPageData;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntitySearchDirection;
import static org.thingsboard.server.dao.DaoUtil.toUUIDs;

import org.thingsboard.server.dao.attributes.AttributesDao;
import org.thingsboard.server.dao.customer.CustomerDao;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.exception.DataValidationException;
import static org.thingsboard.server.dao.model.ModelConstants.NULL_UUID;
import org.thingsboard.server.dao.model.nosql.LandlotEntity;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validateIds;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
import static org.thingsboard.server.dao.service.Validator.validateString;
import org.thingsboard.server.dao.tenant.TenantDao;

/**
 *
 * @author German Lopez
 */
@Service
@Slf4j
public class BaseLandlotService extends AbstractEntityService implements LandlotService {

    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";
    public static final String INCORRECT_PAGE_LINK = "Incorrect page link ";
    public static final String INCORRECT_CUSTOMER_ID = "Incorrect customerId ";
    public static final String INCORRECT_LANDLOT_ID = "Incorrect landlotId ";
    @Autowired
    private LandlotDao landlotDao;

    @Autowired
    private AttributesDao attributesDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private CustomerDao customerDao;

    @Override
    public Landlot findLandlotById(LandlotId landlotId) {
        log.trace("Executing findLandlotById [{}]", landlotId);
        validateId(landlotId, INCORRECT_LANDLOT_ID + landlotId);
        return landlotDao.findById(landlotId.getId());
    }

    private List<Date> getDaysBetweenDates(Date startDate, Date endDate){
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        while(calendar.getTime().before(endDate)){
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private void putInformationHistoricalValue(Map<String, TreeMap<Long,Double>> dataComplete, String key, Map<Long, Double> dataToAdd){
        if(!dataComplete.containsKey(key)){
            dataComplete.put(key, new TreeMap<>());
        }
        for(Map.Entry<Long, Double> entry : dataToAdd.entrySet()){
            dataComplete.get(key).put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<String, TreeMap<Long,Double>> getHistoricalValues(String landlotId, long minDate, long maxDate) {
        log.trace("Executing getHistoricalValues [{}]", landlotId);
        List<Date> daysList = getDaysBetweenDates(new Date(minDate), new Date(maxDate));
        Map<String, TreeMap<Long,Double>> dataComplete = new HashMap<>();
        for(Date date : daysList){
            Map<String, TreeMap<Long, Double>> dayValuesReturned = attributesDao.getHistoricalValues(landlotId,date.getTime());
            for(Map.Entry<String, TreeMap<Long, Double>> entry : dayValuesReturned.entrySet()){
                putInformationHistoricalValue(dataComplete,entry.getKey(),entry.getValue());
            }
        }
        Map<String, TreeMap<Long, Double>> lastDayValuesReturned = attributesDao.getHistoricalValues(landlotId,maxDate);
        for(Map.Entry<String, TreeMap<Long, Double>> entry : lastDayValuesReturned.entrySet()){
            putInformationHistoricalValue(dataComplete,entry.getKey(),entry.getValue());
        }
        log.trace("Result getHistoricalValues [{}]", dataComplete);
        return dataComplete;
    }

    @Override
    public ListenableFuture<Landlot> findLandlotByIdAsync(LandlotId landlotId) {
        log.trace("Executing findLandlotById [{}]", landlotId);
        validateId(landlotId, INCORRECT_LANDLOT_ID + landlotId);
        return landlotDao.findByIdAsync(landlotId.getId());
    }

    @Override
    public Optional<Landlot> findLandlotByTenantIdAndName(TenantId tenantId, String name) {
        log.trace("Executing findLandlotByTenantIdAndName [{}][{}]", tenantId, name);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return landlotDao.findLandlotsByTenantIdAndName(tenantId.getId(), name);
    }

    @Override
    public Landlot saveLandlot(Landlot landlot) {
        log.trace("Executing saveLandlot [{}]", landlot);
        landlotValidator.validate(landlot);
        return landlotDao.save(landlot);
    }

    @Override
    public Landlot assignLandlotToCustomer(LandlotId landlotId, CustomerId customerId) {
        Landlot landlot = findLandlotById(landlotId);
        landlot.setCustomerId(customerId);
        return saveLandlot(landlot);
    }

    @Override
    public Landlot unassignLandlotFromCustomer(LandlotId landlotId) {
        Landlot landlot = findLandlotById(landlotId);
        landlot.setCustomerId(null);
        return saveLandlot(landlot);
    }

    @Override
    public void deleteLandlot(LandlotId landlotId) {
        log.trace("Executing deleteLandlot [{}]", landlotId);
        validateId(landlotId, INCORRECT_LANDLOT_ID + landlotId);
        deleteEntityRelations(landlotId);
        landlotDao.removeById(landlotId.getId());
    }

    @Override
    public TextPageData<Landlot> findLandlotsByTenantId(TenantId tenantId, TextPageLink pageLink) {
        log.trace("Executing findLandlotsByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink, INCORRECT_PAGE_LINK + pageLink);
        List<Landlot> landlots = landlotDao.findLandlotsByTenantId(tenantId.getId(), pageLink);
        return new TextPageData<>(landlots, pageLink);
    }

    @Override
    public TextPageData<Landlot> findLandlotsByTenantIdAndType(TenantId tenantId, String type, TextPageLink pageLink) {
        log.trace("Executing findLandlotsByTenantIdAndType, tenantId [{}], type [{}], pageLink [{}]", tenantId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink, INCORRECT_PAGE_LINK + pageLink);
        List<Landlot> landlots = landlotDao.findLandlotsByTenantIdAndType(tenantId.getId(), type, pageLink);
        return new TextPageData<>(landlots, pageLink);
    }

    @Override
    public ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndIdsAsync(TenantId tenantId, List<LandlotId> landlotIds) {
        log.trace("Executing findLandlotsByTenantIdAndIdsAsync, tenantId [{}], landlotIds [{}]", tenantId, landlotIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateIds(landlotIds, "Incorrect landlotIds " + landlotIds);
        return landlotDao.findLandlotsByTenantIdAndIdsAsync(tenantId.getId(), toUUIDs(landlotIds));
    }

    @Override
    public void deleteLandlotsByTenantId(TenantId tenantId) {
        log.trace("Executing deleteLandlotsByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantLandlotsRemover.removeEntities(tenantId);
    }

    @Override
    public TextPageData<Landlot> findLandlotsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, TextPageLink pageLink) {
        log.trace("Executing findLandlotsByTenantIdAndCustomerId, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validatePageLink(pageLink, INCORRECT_PAGE_LINK + pageLink);
        List<Landlot> landlots = landlotDao.findLandlotsByTenantIdAndCustomerId(tenantId.getId(), customerId.getId(), pageLink);
        return new TextPageData<>(landlots, pageLink);
    }

    @Override
    public TextPageData<Landlot> findLandlotsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, TextPageLink pageLink) {
        log.trace("Executing findLandlotsByTenantIdAndCustomerIdAndType, tenantId [{}], customerId [{}], type [{}], pageLink [{}]", tenantId, customerId, type, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateString(type, "Incorrect type " + type);
        validatePageLink(pageLink, INCORRECT_PAGE_LINK + pageLink);
        List<Landlot> landlots = landlotDao.findLandlotsByTenantIdAndCustomerIdAndType(tenantId.getId(), customerId.getId(), type, pageLink);
        return new TextPageData<>(landlots, pageLink);
    }

    @Override
    public ListenableFuture<List<Landlot>> findLandlotsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<LandlotId> landlotIds) {
        log.trace("Executing findLandlotsByTenantIdAndCustomerIdAndIdsAsync, tenantId [{}], customerId [{}], landlotIds [{}]", tenantId, customerId, landlotIds);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        validateIds(landlotIds, "Incorrect landlotIds " + landlotIds);
        return landlotDao.findLandlotsByTenantIdAndCustomerIdAndIdsAsync(tenantId.getId(), customerId.getId(), toUUIDs(landlotIds));
    }

    @Override
    public void unassignCustomerLandlots(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing unassignCustomerLandlots, tenantId [{}], customerId [{}]", tenantId, customerId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, INCORRECT_CUSTOMER_ID + customerId);
        new CustomerLandlotsUnassigner(tenantId).removeEntities(customerId);
    }

    @Override
    public ListenableFuture<List<Landlot>> findLandlotsByQuery(LandlotSearchQuery query) {
        ListenableFuture<List<EntityRelation>> relations = relationService.findByQuery(query.toEntitySearchQuery());
        ListenableFuture<List<Landlot>> landlots = Futures.transform(relations, (AsyncFunction<List<EntityRelation>, List<Landlot>>) relations1 -> {
            EntitySearchDirection direction = query.toEntitySearchQuery().getParameters().getDirection();
            List<ListenableFuture<Landlot>> futures = new ArrayList<>();
            for (EntityRelation relation : relations1) {
                EntityId entityId = direction == EntitySearchDirection.FROM ? relation.getTo() : relation.getFrom();
                if (entityId.getEntityType() == EntityType.LANDLOT) {
                    futures.add(findLandlotByIdAsync(new LandlotId(entityId.getId())));
                }
            }
            return Futures.successfulAsList(futures);
        });
        landlots = Futures.transform(landlots, (Function<List<Landlot>, List<Landlot>>)landlotList ->
            landlotList == null ? Collections.emptyList() : landlotList.stream().filter(landlot -> query.getLandlotTypes().contains(landlot.getType())).collect(Collectors.toList())
        );
        return landlots;
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findLandlotTypesByTenantId(TenantId tenantId) {
        log.trace("Executing findLandlotTypesByTenantId, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        ListenableFuture<List<EntitySubtype>> tenantLandlotTypes = landlotDao.findTenantLandlotTypesAsync(tenantId.getId());
        return Futures.transform(tenantLandlotTypes,
                (Function<List<EntitySubtype>, List<EntitySubtype>>) landlotTypes -> {
                    landlotTypes.sort(Comparator.comparing(EntitySubtype::getType));
                    return landlotTypes;
                });
    }

    private DataValidator<Landlot> landlotValidator =
            new DataValidator<Landlot>() {

                @Override
                protected void validateCreate(Landlot landlot) {
                    landlotDao.findLandlotsByTenantIdAndName(landlot.getTenantId().getId(), landlot.getName()).ifPresent(
                            d -> {
                                throw new DataValidationException("Landlot with such name already exists!");
                            }
                    );
                }

                @Override
                protected void validateUpdate(Landlot landlot) {
                    landlotDao.findLandlotsByTenantIdAndName(landlot.getTenantId().getId(), landlot.getName()).ifPresent(
                            d -> {
                                if (!d.getId().equals(landlot.getId())) {
                                    throw new DataValidationException("Landlot with such name already exists!");
                                }
                            }
                    );
                }

                @Override
                protected void validateDataImpl(Landlot landlot) {
                    landlot.setType("Landlot");
                    if (StringUtils.isEmpty(landlot.getType())) {
                        throw new DataValidationException("Landlot type should be specified!");
                    }
                    if (StringUtils.isEmpty(landlot.getName())) {
                        throw new DataValidationException("Landlot name should be specified!");
                    }
                    if (landlot.getTenantId() == null) {
                        throw new DataValidationException("Landlot should be assigned to tenant!");
                    } else {
                        Tenant tenant = tenantDao.findById(landlot.getTenantId().getId());
                        if (tenant == null) {
                            throw new DataValidationException("Landlot is referencing to non-existent tenant!");
                        }
                    }
                    if (landlot.getCustomerId() == null) {
                        landlot.setCustomerId(new CustomerId(NULL_UUID));
                    } else if (!landlot.getCustomerId().getId().equals(NULL_UUID)) {
                        Customer customer = customerDao.findById(landlot.getCustomerId().getId());
                        if (customer == null) {
                            throw new DataValidationException("Can't assign landlot to non-existent customer!");
                        }
                        if (!customer.getTenantId().equals(landlot.getTenantId())) {
                            throw new DataValidationException("Can't assign landlot to customer from different tenant!");
                        }
                    }
                }
            };

    private PaginatedRemover<TenantId, Landlot> tenantLandlotsRemover =
            new PaginatedRemover<TenantId, Landlot>() {

                @Override
                protected List<Landlot> findEntities(TenantId id, TextPageLink pageLink) {
                    return landlotDao.findLandlotsByTenantId(id.getId(), pageLink);
                }

                @Override
                protected void removeEntity(Landlot entity) {
                    deleteLandlot(new LandlotId(entity.getId().getId()));
                }
            };

    @Override
    public ListenableFuture<List<LandlotEntity>> findLandlotsByFarmId(String farmId) {
        return landlotDao.findLandlotsByFarmId(farmId);
    }

    @Override
    public ListenableFuture<List<LandlotEntity>> allLandlots() {
        return landlotDao.allLandlots();
    }

    class CustomerLandlotsUnassigner extends PaginatedRemover<CustomerId, Landlot> {

        private TenantId tenantId;

        CustomerLandlotsUnassigner(TenantId tenantId) {
            this.tenantId = tenantId;
        }

        @Override
        protected List<Landlot> findEntities(CustomerId id, TextPageLink pageLink) {
            return landlotDao.findLandlotsByTenantIdAndCustomerId(tenantId.getId(), id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(Landlot entity) {
            unassignLandlotFromCustomer(new LandlotId(entity.getId().getId()));
        }
    }
}
