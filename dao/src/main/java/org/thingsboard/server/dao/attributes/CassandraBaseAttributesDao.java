/**
 * Copyright © 2016-2018 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.attributes;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.ParcelId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.parcel.Parcel;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.nosql.CassandraAbstractAsyncDao;
import org.thingsboard.server.dao.parcel.ParcelService;
import org.thingsboard.server.dao.timeseries.CassandraBaseTimeseriesDao;
import org.thingsboard.server.dao.util.NoSqlDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;
import static org.thingsboard.server.dao.model.ModelConstants.*;

/**
 * @author Andrew Shvayka
 */
@Component
@Slf4j
@NoSqlDao
public class CassandraBaseAttributesDao extends CassandraAbstractAsyncDao implements AttributesDao {

    private PreparedStatement saveStmt;

    @Autowired
    protected ParcelService parcelService;

    @PostConstruct
    public void init() {
        super.startExecutor();
    }

    @PreDestroy
    public void stop() {
        super.stopExecutor();
    }

    @Override
    public ListenableFuture<Optional<AttributeKvEntry>> find(EntityId entityId, String attributeType, String attributeKey) {
        Select.Where select = select().from(ATTRIBUTES_KV_CF)
                .where(eq(ENTITY_TYPE_COLUMN, entityId.getEntityType()))
                .and(eq(ENTITY_ID_COLUMN, entityId.getId()))
                .and(eq(ATTRIBUTE_TYPE_COLUMN, attributeType))
                .and(eq(ATTRIBUTE_KEY_COLUMN, attributeKey));
        log.trace("Generated query [{}] for entityId {} and key {}", select, entityId, attributeKey);
        return Futures.transform(executeAsyncRead(select), (Function<? super ResultSet, ? extends Optional<AttributeKvEntry>>) input ->
                        Optional.ofNullable(convertResultToAttributesKvEntry(attributeKey, input.one()))
                , readResultsProcessingExecutor);
    }

    @Override
    public ListenableFuture<List<AttributeKvEntry>> find(EntityId entityId, String attributeType, Collection<String> attributeKeys) {
        List<ListenableFuture<Optional<AttributeKvEntry>>> entries = new ArrayList<>();
        attributeKeys.forEach(attributeKey -> entries.add(find(entityId, attributeType, attributeKey)));
        return Futures.transform(Futures.allAsList(entries), (Function<List<Optional<AttributeKvEntry>>, ? extends List<AttributeKvEntry>>) input -> {
            List<AttributeKvEntry> result = new ArrayList<>();
            input.stream().filter(opt -> opt.isPresent()).forEach(opt -> result.add(opt.get()));
            return result;
        }, readResultsProcessingExecutor);
    }


    @Override
    public ListenableFuture<List<AttributeKvEntry>> findAll(EntityId entityId, String attributeType) {
        Select.Where select = select().from(ATTRIBUTES_KV_CF)
                .where(eq(ENTITY_TYPE_COLUMN, entityId.getEntityType()))
                .and(eq(ENTITY_ID_COLUMN, entityId.getId()))
                .and(eq(ATTRIBUTE_TYPE_COLUMN, attributeType));
        log.trace("Generated query [{}] for entityId {} and attributeType {}", select, entityId, attributeType);
        return Futures.transform(executeAsyncRead(select), (Function<? super ResultSet, ? extends List<AttributeKvEntry>>) input ->
                        convertResultToAttributesKvEntryList(input)
                , readResultsProcessingExecutor);
    }

    @Override
    public HashMap<String, String> getHistoricalValues(String parcelId, long date) {
        HashMap<String, String> data = new HashMap<>();
        long dayts = 86400000L;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try{
            String reportDate = dateFormat.format(date);
            Date parsedDate = dateFormat.parse(reportDate);

            Timestamp timestampmin = new java.sql.Timestamp(parsedDate.getTime());
            long tsTime1 = timestampmin.getTime();
            long tsTime2 = tsTime1 + dayts;

            //Obtener el parcel
            ParcelId parcelIdUUID = ParcelId.fromString(parcelId);
            Parcel parcel = parcelService.findParcelById(parcelIdUUID);
            List<UUID> devices= parcel.getDevices();
            //Por cada elemento del arreglo del parcel ir acumulando
            for (UUID device_id: devices){

                Select select = select(KEY_COLUMN,STRING_VALUE_COLUMN).from(TS_KV_CF).allowFiltering();
                Select.Where query= select.where();
                query.and(eq(ENTITY_ID_COLUMN,device_id));
                query.and(gte(TS_COLUMN,tsTime1));
                query.and(lte(TS_COLUMN,tsTime2));
                ResultSetFuture resp =executeAsyncRead(query);
                ResultSet resp2= resp.getUninterruptibly();
                List<Row> rows =resp2.all();
                HashMap<String,ArrayList<Integer>> temp = new HashMap<>();
                for (Row r:rows){
                    String key=r.getString(0);
                    Integer value=Integer.parseInt(r.getString(1));
                    if (temp.containsKey(key)){
                        ArrayList<Integer> arraytemp= temp.get(key);
                        arraytemp.add(value);
                        temp.put(key,arraytemp);
                    }

                    else{
                        ArrayList<Integer> arraytemp = new ArrayList<>();
                        arraytemp.add(value);
                        temp.put(key,arraytemp);
                    }
                }
                Set<String> keys = temp.keySet();
                for (String key : keys){
                    ArrayList<Integer> arraytemp= temp.get(key);
                    Long count=0L;
                    for (Integer a : arraytemp){
                        count+=a;
                    }
                    Double numcount =(double) count;
                    Double numcant =(double) arraytemp.size();
                    Double avg= numcount/numcant;
                    DecimalFormat df = new DecimalFormat("#.0000");
                    String savgdecimal = df.format(avg);
                    data.put(key,savgdecimal);
                }
            }
        }
        catch(Exception e) { //this generic but you can control another types of exception
            e.printStackTrace();
        }
        return data;
    }


    @Override
    public ListenableFuture<Void> save(EntityId entityId, String attributeType, AttributeKvEntry attribute) {
        BoundStatement stmt = getSaveStmt().bind();
        stmt.setString(0, entityId.getEntityType().name());
        stmt.setUUID(1, entityId.getId());
        stmt.setString(2, attributeType);
        stmt.setString(3, attribute.getKey());
        stmt.setLong(4, attribute.getLastUpdateTs());
        stmt.setString(5, attribute.getStrValue().orElse(null));
        Optional<Boolean> booleanValue = attribute.getBooleanValue();
        if (booleanValue.isPresent()) {
            stmt.setBool(6, booleanValue.get());
        } else {
            stmt.setToNull(6);
        }
        Optional<Long> longValue = attribute.getLongValue();
        if (longValue.isPresent()) {
            stmt.setLong(7, longValue.get());
        } else {
            stmt.setToNull(7);
        }
        Optional<Double> doubleValue = attribute.getDoubleValue();
        if (doubleValue.isPresent()) {
            stmt.setDouble(8, doubleValue.get());
        } else {
            stmt.setToNull(8);
        }
        log.trace("Generated save stmt [{}] for entityId {} and attributeType {} and attribute", stmt, entityId, attributeType, attribute);
        return getFuture(executeAsyncWrite(stmt), rs -> null);
    }

    @Override
    public ListenableFuture<List<Void>> removeAll(EntityId entityId, String attributeType, List<String> keys) {
        List<ListenableFuture<Void>> futures = keys
                .stream()
                .map(key -> delete(entityId, attributeType, key))
                .collect(Collectors.toList());
        return Futures.allAsList(futures);
    }

    private ListenableFuture<Void> delete(EntityId entityId, String attributeType, String key) {
        Statement delete = QueryBuilder.delete().all().from(ModelConstants.ATTRIBUTES_KV_CF)
                .where(eq(ENTITY_TYPE_COLUMN, entityId.getEntityType()))
                .and(eq(ENTITY_ID_COLUMN, entityId.getId()))
                .and(eq(ATTRIBUTE_TYPE_COLUMN, attributeType))
                .and(eq(ATTRIBUTE_KEY_COLUMN, key));
        log.debug("Remove request: {}", delete.toString());
        return getFuture(getSession().executeAsync(delete), rs -> null);
    }

    private PreparedStatement getSaveStmt() {
        if (saveStmt == null) {
            saveStmt = getSession().prepare("INSERT INTO " + ModelConstants.ATTRIBUTES_KV_CF +
                    "(" + ENTITY_TYPE_COLUMN +
                    "," + ENTITY_ID_COLUMN +
                    "," + ATTRIBUTE_TYPE_COLUMN +
                    "," + ATTRIBUTE_KEY_COLUMN +
                    "," + LAST_UPDATE_TS_COLUMN +
                    "," + ModelConstants.STRING_VALUE_COLUMN +
                    "," + ModelConstants.BOOLEAN_VALUE_COLUMN +
                    "," + ModelConstants.LONG_VALUE_COLUMN +
                    "," + ModelConstants.DOUBLE_VALUE_COLUMN +
                    ")" +
                    " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)");
        }
        return saveStmt;
    }

    private AttributeKvEntry convertResultToAttributesKvEntry(String key, Row row) {
        AttributeKvEntry attributeEntry = null;
        if (row != null) {
            long lastUpdateTs = row.get(LAST_UPDATE_TS_COLUMN, Long.class);
            attributeEntry = new BaseAttributeKvEntry(CassandraBaseTimeseriesDao.toKvEntry(row, key), lastUpdateTs);
        }
        return attributeEntry;
    }

    private List<AttributeKvEntry> convertResultToAttributesKvEntryList(ResultSet resultSet) {
        List<Row> rows = resultSet.all();
        List<AttributeKvEntry> entries = new ArrayList<>(rows.size());
        if (!rows.isEmpty()) {
            rows.forEach(row -> {
                String key = row.getString(ModelConstants.ATTRIBUTE_KEY_COLUMN);
                AttributeKvEntry kvEntry = convertResultToAttributesKvEntry(key, row);
                if (kvEntry != null) {
                    entries.add(kvEntry);
                }
            });
        }
        return entries;
    }
}
