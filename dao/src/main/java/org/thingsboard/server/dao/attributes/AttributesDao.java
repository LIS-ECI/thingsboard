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

import com.google.common.util.concurrent.ListenableFuture;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.AttributeKvEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author Andrew Shvayka
 */
public interface AttributesDao {

    ListenableFuture<Optional<AttributeKvEntry>> find(EntityId entityId, String attributeType, String attributeKey);

    ListenableFuture<List<AttributeKvEntry>> find(EntityId entityId, String attributeType, Collection<String> attributeKey);

    ListenableFuture<List<AttributeKvEntry>> findAll(EntityId entityId, String attributeType);

    ListenableFuture<Void> save(EntityId entityId, String attributeType, AttributeKvEntry attribute);

    ListenableFuture<List<Void>> removeAll(EntityId entityId, String attributeType, List<String> keys);

    /**
     * get all the device data in an specific date of the landlot
     *
     * @param landlotId the landlot_id
     * @param date the timeseries date
     * @return saved landlot object
     */
    Map<String, TreeMap<Long,Double>> getHistoricalValues(String landlotId, long date);
}
