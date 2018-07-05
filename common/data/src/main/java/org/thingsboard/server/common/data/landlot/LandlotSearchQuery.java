/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingsboard.server.common.data.landlot;

import java.util.Collections;
import java.util.List;
import lombok.Data;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.relation.EntityRelation;
import org.thingsboard.server.common.data.relation.EntityRelationsQuery;
import org.thingsboard.server.common.data.relation.EntityTypeFilter;
import org.thingsboard.server.common.data.relation.RelationsSearchParameters;

/**
 *
 * @German Lopez
 */
@Data
public class LandlotSearchQuery {

    private RelationsSearchParameters parameters;
    private String relationType;
    private List<String> landlotTypes;

    public EntityRelationsQuery toEntitySearchQuery() {
        EntityRelationsQuery query = new EntityRelationsQuery();
        query.setParameters(parameters);
        query.setFilters(
                Collections.singletonList(new EntityTypeFilter(relationType == null ? EntityRelation.CONTAINS_TYPE : relationType,
                        Collections.singletonList(EntityType.LANDLOT))));
        return query;
    }

}