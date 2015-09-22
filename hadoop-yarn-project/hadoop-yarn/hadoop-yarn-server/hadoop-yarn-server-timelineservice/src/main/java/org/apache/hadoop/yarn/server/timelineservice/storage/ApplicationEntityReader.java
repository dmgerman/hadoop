begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hbase
operator|.
name|client
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hbase
operator|.
name|client
operator|.
name|Get
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hbase
operator|.
name|client
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntityType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|TimelineReader
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|application
operator|.
name|ApplicationColumn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|application
operator|.
name|ApplicationColumnPrefix
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|application
operator|.
name|ApplicationRowKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|application
operator|.
name|ApplicationTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|BaseTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|TimelineReaderUtils
import|;
end_import

begin_comment
comment|/**  * Timeline entity reader for application entities that are stored in the  * application table.  */
end_comment

begin_class
DECL|class|ApplicationEntityReader
class|class
name|ApplicationEntityReader
extends|extends
name|GenericEntityReader
block|{
DECL|field|APPLICATION_TABLE
specifier|private
specifier|static
specifier|final
name|ApplicationTable
name|APPLICATION_TABLE
init|=
operator|new
name|ApplicationTable
argument_list|()
decl_stmt|;
DECL|method|ApplicationEntityReader (String userId, String clusterId, String flowId, Long flowRunId, String appId, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Long modifiedTimeBegin, Long modifiedTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|ApplicationEntityReader
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|flowId
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|Long
name|limit
parameter_list|,
name|Long
name|createdTimeBegin
parameter_list|,
name|Long
name|createdTimeEnd
parameter_list|,
name|Long
name|modifiedTimeBegin
parameter_list|,
name|Long
name|modifiedTimeEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatesTo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|isRelatedTo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|infoFilters
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configFilters
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricFilters
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|eventFilters
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|)
block|{
name|super
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowId
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|limit
argument_list|,
name|createdTimeBegin
argument_list|,
name|createdTimeEnd
argument_list|,
name|modifiedTimeBegin
argument_list|,
name|modifiedTimeEnd
argument_list|,
name|relatesTo
argument_list|,
name|isRelatedTo
argument_list|,
name|infoFilters
argument_list|,
name|configFilters
argument_list|,
name|metricFilters
argument_list|,
name|eventFilters
argument_list|,
name|fieldsToRetrieve
argument_list|)
expr_stmt|;
block|}
DECL|method|ApplicationEntityReader (String userId, String clusterId, String flowId, Long flowRunId, String appId, String entityType, String entityId, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|ApplicationEntityReader
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|flowId
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|String
name|entityId
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|)
block|{
name|super
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowId
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|entityId
argument_list|,
name|fieldsToRetrieve
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uses the {@link ApplicationTable}.    */
DECL|method|getTable ()
specifier|protected
name|BaseTable
argument_list|<
name|?
argument_list|>
name|getTable
parameter_list|()
block|{
return|return
name|APPLICATION_TABLE
return|;
block|}
annotation|@
name|Override
DECL|method|getResult (Configuration hbaseConf, Connection conn)
specifier|protected
name|Result
name|getResult
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|rowKey
init|=
name|ApplicationRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowId
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|Get
name|get
init|=
operator|new
name|Get
argument_list|(
name|rowKey
argument_list|)
decl_stmt|;
name|get
operator|.
name|setMaxVersions
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
return|return
name|table
operator|.
name|getResult
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|get
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getResults (Configuration hbaseConf, Connection conn)
specifier|protected
name|Iterable
argument_list|<
name|Result
argument_list|>
name|getResults
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
comment|// If getEntities() is called for an application, there can be at most
comment|// one entity. If the entity passes the filter, it is returned. Otherwise,
comment|// an empty set is returned.
name|byte
index|[]
name|rowKey
init|=
name|ApplicationRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowId
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|Get
name|get
init|=
operator|new
name|Get
argument_list|(
name|rowKey
argument_list|)
decl_stmt|;
name|get
operator|.
name|setMaxVersions
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|Result
name|result
init|=
name|table
operator|.
name|getResult
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|get
argument_list|)
decl_stmt|;
name|TimelineEntity
name|entity
init|=
name|parseEntity
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Result
argument_list|>
name|set
decl_stmt|;
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|set
operator|=
name|Collections
operator|.
name|singleton
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|parseEntity (Result result)
specifier|protected
name|TimelineEntity
name|parseEntity
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|entityId
init|=
name|ApplicationColumn
operator|.
name|ID
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|entityId
argument_list|)
expr_stmt|;
comment|// fetch created time
name|Number
name|createdTime
init|=
operator|(
name|Number
operator|)
name|ApplicationColumn
operator|.
name|CREATED_TIME
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
name|createdTime
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|singleEntityRead
operator|&&
operator|(
name|entity
operator|.
name|getCreatedTime
argument_list|()
operator|<
name|createdTimeBegin
operator|||
name|entity
operator|.
name|getCreatedTime
argument_list|()
operator|>
name|createdTimeEnd
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// fetch modified time
name|Number
name|modifiedTime
init|=
operator|(
name|Number
operator|)
name|ApplicationColumn
operator|.
name|MODIFIED_TIME
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|entity
operator|.
name|setModifiedTime
argument_list|(
name|modifiedTime
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|singleEntityRead
operator|&&
operator|(
name|entity
operator|.
name|getModifiedTime
argument_list|()
operator|<
name|modifiedTimeBegin
operator|||
name|entity
operator|.
name|getModifiedTime
argument_list|()
operator|>
name|modifiedTimeEnd
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// fetch is related to entities
name|boolean
name|checkIsRelatedTo
init|=
name|isRelatedTo
operator|!=
literal|null
operator|&&
name|isRelatedTo
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|IS_RELATED_TO
argument_list|)
operator|||
name|checkIsRelatedTo
condition|)
block|{
name|readRelationship
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
name|ApplicationColumnPrefix
operator|.
name|IS_RELATED_TO
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkIsRelatedTo
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchRelations
argument_list|(
name|entity
operator|.
name|getIsRelatedToEntities
argument_list|()
argument_list|,
name|isRelatedTo
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|IS_RELATED_TO
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// fetch relates to entities
name|boolean
name|checkRelatesTo
init|=
name|relatesTo
operator|!=
literal|null
operator|&&
name|relatesTo
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|RELATES_TO
argument_list|)
operator|||
name|checkRelatesTo
condition|)
block|{
name|readRelationship
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
name|ApplicationColumnPrefix
operator|.
name|RELATES_TO
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkRelatesTo
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchRelations
argument_list|(
name|entity
operator|.
name|getRelatesToEntities
argument_list|()
argument_list|,
name|relatesTo
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|RELATES_TO
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// fetch info
name|boolean
name|checkInfo
init|=
name|infoFilters
operator|!=
literal|null
operator|&&
name|infoFilters
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|INFO
argument_list|)
operator|||
name|checkInfo
condition|)
block|{
name|readKeyValuePairs
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
name|ApplicationColumnPrefix
operator|.
name|INFO
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkInfo
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchFilters
argument_list|(
name|entity
operator|.
name|getInfo
argument_list|()
argument_list|,
name|infoFilters
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|INFO
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getInfo
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// fetch configs
name|boolean
name|checkConfigs
init|=
name|configFilters
operator|!=
literal|null
operator|&&
name|configFilters
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|CONFIGS
argument_list|)
operator|||
name|checkConfigs
condition|)
block|{
name|readKeyValuePairs
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
name|ApplicationColumnPrefix
operator|.
name|CONFIG
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkConfigs
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchFilters
argument_list|(
name|entity
operator|.
name|getConfigs
argument_list|()
argument_list|,
name|configFilters
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|CONFIGS
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getConfigs
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// fetch events
name|boolean
name|checkEvents
init|=
name|eventFilters
operator|!=
literal|null
operator|&&
name|eventFilters
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|EVENTS
argument_list|)
operator|||
name|checkEvents
condition|)
block|{
name|readEvents
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkEvents
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchEventFilters
argument_list|(
name|entity
operator|.
name|getEvents
argument_list|()
argument_list|,
name|eventFilters
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|EVENTS
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getEvents
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|// fetch metrics
name|boolean
name|checkMetrics
init|=
name|metricFilters
operator|!=
literal|null
operator|&&
name|metricFilters
operator|.
name|size
argument_list|()
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|METRICS
argument_list|)
operator|||
name|checkMetrics
condition|)
block|{
name|readMetrics
argument_list|(
name|entity
argument_list|,
name|result
argument_list|,
name|ApplicationColumnPrefix
operator|.
name|METRIC
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkMetrics
operator|&&
operator|!
name|TimelineReaderUtils
operator|.
name|matchMetricFilters
argument_list|(
name|entity
operator|.
name|getMetrics
argument_list|()
argument_list|,
name|metricFilters
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
operator|&&
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|METRICS
argument_list|)
condition|)
block|{
name|entity
operator|.
name|getMetrics
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|entity
return|;
block|}
block|}
end_class

end_unit

