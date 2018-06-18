/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.dao.landlot;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Statement;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static org.thingsboard.server.dao.model.ModelConstants.*;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Result;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntitySubtype;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.LandlotId;
import org.thingsboard.server.common.data.landlot.Landlot;
import org.thingsboard.server.common.data.page.TextPageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.EntitySubtypeEntity;
import org.thingsboard.server.dao.model.nosql.LandlotEntity;
import org.thingsboard.server.dao.nosql.CassandraAbstractSearchTextDao;
import org.thingsboard.server.dao.util.NoSqlDao;

/**
 *
 * @author German Lopez
 */
@Component
@Slf4j
@NoSqlDao
public class CassandraLandlotDao extends CassandraAbstractSearchTextDao<LandlotEntity, Landlot> implements LandlotDao {

    @Override
    protected Class<LandlotEntity> getColumnFamilyClass() {
        return LandlotEntity.class;
    }

    @Override
    protected String getColumnFamilyName() {
        return LANDLOT_COLUMN_FAMILY_NAME;
    }

    @Override
    public Landlot save(Landlot domain) {
        Landlot savedLandlot = super.save(domain);
        EntitySubtype entitySubtype = new EntitySubtype(savedLandlot.getTenantId(), EntityType.LANDLOT, savedLandlot.getType());
        EntitySubtypeEntity entitySubtypeEntity = new EntitySubtypeEntity(entitySubtype);
        Statement saveStatement = cluster.getMapper(EntitySubtypeEntity.class).saveQuery(entitySubtypeEntity);
        executeWrite(saveStatement);
        return savedLandlot;
    }

    @Override
    public List<Landlot> findLandlotsByTenantId(UUID tenantId, TextPageLink pageLink) {
        log.debug("Try to find landlots by tenantId [{}] and pageLink [{}]", tenantId, pageLink);
        List<LandlotEntity> landlotEntities = findPageWithTextSearch(LANDLOT_BY_TENANT_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Collections.singletonList(eq(LANDLOT_TENANT_ID_PROPERTY, tenantId)), pageLink);

        log.trace("Found landlots [{}] by tenantId [{}] and pageLink [{}]", landlotEntities, tenantId, pageLink);
        return DaoUtil.convertDataList(landlotEntities);
    }

    @Override
    public List<Landlot> findLandlotsByTenantIdAndType(UUID tenantId, String type, TextPageLink pageLink) {
        log.debug("Try to find landlots by tenantId [{}], type [{}] and pageLink [{}]", tenantId, type, pageLink);
        List<LandlotEntity> landlotEntities = findPageWithTextSearch(LANDLOT_BY_TENANT_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(LANDLOT_TYPE_PROPERTY, type),
                        eq(LANDLOT_TENANT_ID_PROPERTY, tenantId)), pageLink);
        log.trace("Found landlots [{}] by tenantId [{}], type [{}] and pageLink [{}]", landlotEntities, tenantId, type, pageLink);
        return DaoUtil.convertDataList(landlotEntities);
    }

    public ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndIdsAsync(UUID tenantId, List<UUID> landlotIds) {
        log.debug("Try to find landlots by tenantId [{}] and landlot Ids [{}]", tenantId, landlotIds);
        Select select = select().from(getColumnFamilyName());
        Select.Where query = select.where();
        query.and(eq(LANDLOT_TENANT_ID_PROPERTY, tenantId));
        query.and(in(ID_PROPERTY, landlotIds));
        return findListByStatementAsync(query);
    }

    @Override
    public List<Landlot> findLandlotsByTenantIdAndCustomerId(UUID tenantId, UUID customerId, TextPageLink pageLink) {
        log.debug("Try to find landlots by tenantId [{}], customerId[{}] and pageLink [{}]", tenantId, customerId, pageLink);
        List<LandlotEntity> landlotEntities = findPageWithTextSearch(LANDLOT_BY_CUSTOMER_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(LANDLOT_CUSTOMER_ID_PROPERTY, customerId),
                        eq(LANDLOT_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found landlots [{}] by tenantId [{}], customerId [{}] and pageLink [{}]", landlotEntities, tenantId, customerId, pageLink);
        return DaoUtil.convertDataList(landlotEntities);
    }

    @Override
    public List<Landlot> findLandlotsByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, TextPageLink pageLink) {
        log.debug("Try to find landlots by tenantId [{}], customerId [{}], type [{}] and pageLink [{}]", tenantId, customerId, type, pageLink);
        List<LandlotEntity> landlotEntities = findPageWithTextSearch(LANDLOT_BY_CUSTOMER_BY_TYPE_AND_SEARCH_TEXT_COLUMN_FAMILY_NAME,
                Arrays.asList(eq(LANDLOT_TYPE_PROPERTY, type),
                        eq(LANDLOT_CUSTOMER_ID_PROPERTY, customerId),
                        eq(LANDLOT_TENANT_ID_PROPERTY, tenantId)),
                pageLink);

        log.trace("Found landlots [{}] by tenantId [{}], customerId [{}], type [{}] and pageLink [{}]", landlotEntities, tenantId, customerId, type, pageLink);
        return DaoUtil.convertDataList(landlotEntities);
    }

    @Override
    public ListenableFuture<List<Landlot>> findLandlotsByTenantIdAndCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> landlotIds) {
        log.debug("Try to find landlots by tenantId [{}], customerId [{}] and landlot Ids [{}]", tenantId, customerId, landlotIds);
        Select select = select().from(getColumnFamilyName());
        Select.Where query = select.where();
        query.and(eq(LANDLOT_TENANT_ID_PROPERTY, tenantId));
        query.and(eq(LANDLOT_CUSTOMER_ID_PROPERTY, customerId));
        query.and(in(ID_PROPERTY, landlotIds));
        return findListByStatementAsync(query);
    }

    @Override
    public Optional<Landlot> findLandlotsByTenantIdAndName(UUID tenantId, String landlotName) {
        Select select = select().from(LANDLOT_BY_TENANT_AND_NAME_VIEW_NAME);
        Select.Where query = select.where();
        query.and(eq(LANDLOT_TENANT_ID_PROPERTY, tenantId));
        query.and(eq(LANDLOT_NAME_PROPERTY, landlotName));
        LandlotEntity landlotEntity = (LandlotEntity) findOneByStatement(query);
        return Optional.ofNullable(DaoUtil.getData(landlotEntity));
    }

    @Override
    public ListenableFuture<List<EntitySubtype>> findTenantLandlotTypesAsync(UUID tenantId) {
        Select select = select().from(ENTITY_SUBTYPE_COLUMN_FAMILY_NAME);
        Select.Where query = select.where();
        query.and(eq(ENTITY_SUBTYPE_TENANT_ID_PROPERTY, tenantId));
        query.and(eq(ENTITY_SUBTYPE_ENTITY_TYPE_PROPERTY, EntityType.LANDLOT));
        query.setConsistencyLevel(cluster.getDefaultReadConsistencyLevel());
        ResultSetFuture resultSetFuture = getSession().executeAsync(query);
        return Futures.transform(resultSetFuture, new Function<ResultSet, List<EntitySubtype>>() {
            @Nullable
            @Override
            public List<EntitySubtype> apply(@Nullable ResultSet resultSet) {
                Result<EntitySubtypeEntity> result = cluster.getMapper(EntitySubtypeEntity.class).map(resultSet);
                if (result != null) {
                    List<EntitySubtype> entitySubtypes = new ArrayList<>();
                    result.all().forEach((entitySubtypeEntity) ->
                            entitySubtypes.add(entitySubtypeEntity.toEntitySubtype())
                    );
                    return entitySubtypes;
                } else {
                    return Collections.emptyList();
                }
            }
        });
    }
    
    @Override
    public ListenableFuture<List<LandlotEntity>> findLandlotsByFarmId(String farmId) {
        Select select = select().from(LANDLOT_COLUMN_FAMILY_NAME);
        Select.Where query = select.where();
        query.and(eq(LANDLOT_COLUMN_FAMILY_NAME, farmId));
        ResultSetFuture resultSetFuture = getSession().executeAsync(query);
        return Futures.transform(resultSetFuture, new Function<ResultSet, List<LandlotEntity>>() {
            @Nullable
            @Override
            public List<LandlotEntity> apply(@Nullable ResultSet resultSet) {
                Result<LandlotEntity> result = cluster.getMapper(LandlotEntity.class).map(resultSet);
                if (result != null) {
                    List<LandlotEntity> landlots = new ArrayList<>();
                    result.all().forEach((landlot) ->
                            landlots.add(landlot)
                    );
                    return landlots;
                } else {
                    return Collections.emptyList();
                }
            }
        });
    }
    
    @Override
    public ListenableFuture<List<LandlotEntity>> allLandlots() {
        Select select = select().from(ALL_LANDLOTS);
        Select.Where query = select.where();
        ResultSetFuture resultSetFuture = getSession().executeAsync(query);
        return Futures.transform(resultSetFuture, new Function<ResultSet, List<LandlotEntity>>() {
            @Nullable
            @Override
            public List<LandlotEntity> apply(@Nullable ResultSet resultSet) {
                Result<LandlotEntity> result = cluster.getMapper(LandlotEntity.class).map(resultSet);
                if (result != null) {
                    List<LandlotEntity> landlots = new ArrayList<>();
                    result.all().forEach((landlot) ->
                            landlots.add(landlot)
                    );
                    return landlots;
                } else {
                    return Collections.emptyList();
                }
            }
        });
    }


}



