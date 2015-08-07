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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|HashSet
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
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|csv
operator|.
name|CSVFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|csv
operator|.
name|CSVParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|csv
operator|.
name|CSVRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|service
operator|.
name|AbstractService
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
name|TimelineEvent
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
name|TimelineMetric
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
name|conf
operator|.
name|YarnConfiguration
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
name|webapp
operator|.
name|YarnJacksonJaxbJsonProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonGenerationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|JsonMappingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  *  File System based implementation for TimelineReader.  */
end_comment

begin_class
DECL|class|FileSystemTimelineReaderImpl
specifier|public
class|class
name|FileSystemTimelineReaderImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|FileSystemTimelineReaderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rootPath
specifier|private
name|String
name|rootPath
decl_stmt|;
DECL|field|ENTITIES_DIR
specifier|private
specifier|static
specifier|final
name|String
name|ENTITIES_DIR
init|=
literal|"entities"
decl_stmt|;
comment|/** Default extension for output files. */
DECL|field|TIMELINE_SERVICE_STORAGE_EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|TIMELINE_SERVICE_STORAGE_EXTENSION
init|=
literal|".thist"
decl_stmt|;
annotation|@
name|VisibleForTesting
comment|/** Default extension for output files. */
DECL|field|APP_FLOW_MAPPING_FILE
specifier|static
specifier|final
name|String
name|APP_FLOW_MAPPING_FILE
init|=
literal|"app_flow_mapping.csv"
decl_stmt|;
annotation|@
name|VisibleForTesting
comment|/** Config param for timeline service file system storage root. */
DECL|field|TIMELINE_SERVICE_STORAGE_DIR_ROOT
specifier|static
specifier|final
name|String
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
init|=
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_PREFIX
operator|+
literal|"fs-writer.root-dir"
decl_stmt|;
annotation|@
name|VisibleForTesting
comment|/** Default value for storage location on local disk. */
DECL|field|DEFAULT_TIMELINE_SERVICE_STORAGE_DIR_ROOT
specifier|static
specifier|final
name|String
name|DEFAULT_TIMELINE_SERVICE_STORAGE_DIR_ROOT
init|=
literal|"/tmp/timeline_service_data"
decl_stmt|;
DECL|field|csvFormat
specifier|private
specifier|final
name|CSVFormat
name|csvFormat
init|=
name|CSVFormat
operator|.
name|DEFAULT
operator|.
name|withHeader
argument_list|(
literal|"APP"
argument_list|,
literal|"USER"
argument_list|,
literal|"FLOW"
argument_list|,
literal|"FLOWRUN"
argument_list|)
decl_stmt|;
DECL|method|FileSystemTimelineReaderImpl ()
specifier|public
name|FileSystemTimelineReaderImpl
parameter_list|()
block|{
name|super
argument_list|(
name|FileSystemTimelineReaderImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRootPath ()
name|String
name|getRootPath
parameter_list|()
block|{
return|return
name|rootPath
return|;
block|}
DECL|field|mapper
specifier|private
specifier|static
name|ObjectMapper
name|mapper
decl_stmt|;
static|static
block|{
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|YarnJacksonJaxbJsonProvider
operator|.
name|configObjectMapper
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deserialize a POJO object from a JSON string.    * @param clazz    *      class to be desirialized    *    * @param jsonString    *    json string to deserialize    * @return TimelineEntity object    * @throws IOException    * @throws JsonMappingException    * @throws JsonGenerationException    */
DECL|method|getTimelineRecordFromJSON ( String jsonString, Class<T> clazz)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getTimelineRecordFromJSON
parameter_list|(
name|String
name|jsonString
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|JsonGenerationException
throws|,
name|JsonMappingException
throws|,
name|IOException
block|{
return|return
name|mapper
operator|.
name|readValue
argument_list|(
name|jsonString
argument_list|,
name|clazz
argument_list|)
return|;
block|}
DECL|method|fillFields (TimelineEntity finalEntity, TimelineEntity real, EnumSet<Field> fields)
specifier|private
specifier|static
name|void
name|fillFields
parameter_list|(
name|TimelineEntity
name|finalEntity
parameter_list|,
name|TimelineEntity
name|real
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
argument_list|)
condition|)
block|{
name|fields
operator|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Field
name|field
range|:
name|fields
control|)
block|{
switch|switch
condition|(
name|field
condition|)
block|{
case|case
name|CONFIGS
case|:
name|finalEntity
operator|.
name|setConfigs
argument_list|(
name|real
operator|.
name|getConfigs
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|METRICS
case|:
name|finalEntity
operator|.
name|setMetrics
argument_list|(
name|real
operator|.
name|getMetrics
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|INFO
case|:
name|finalEntity
operator|.
name|setInfo
argument_list|(
name|real
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|IS_RELATED_TO
case|:
name|finalEntity
operator|.
name|setIsRelatedToEntities
argument_list|(
name|real
operator|.
name|getIsRelatedToEntities
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|RELATES_TO
case|:
name|finalEntity
operator|.
name|setIsRelatedToEntities
argument_list|(
name|real
operator|.
name|getIsRelatedToEntities
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|EVENTS
case|:
name|finalEntity
operator|.
name|setEvents
argument_list|(
name|real
operator|.
name|getEvents
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
continue|continue;
block|}
block|}
block|}
DECL|method|getFlowRunPath (String userId, String clusterId, String flowId, Long flowRunId, String appId)
specifier|private
name|String
name|getFlowRunPath
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
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|userId
operator|!=
literal|null
operator|&&
name|flowId
operator|!=
literal|null
operator|&&
name|flowRunId
operator|!=
literal|null
condition|)
block|{
return|return
name|userId
operator|+
literal|"/"
operator|+
name|flowId
operator|+
literal|"/"
operator|+
name|flowRunId
return|;
block|}
if|if
condition|(
name|clusterId
operator|==
literal|null
operator|||
name|appId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to get flow info"
argument_list|)
throw|;
block|}
name|String
name|appFlowMappingFile
init|=
name|rootPath
operator|+
literal|"/"
operator|+
name|ENTITIES_DIR
operator|+
literal|"/"
operator|+
name|clusterId
operator|+
literal|"/"
operator|+
name|APP_FLOW_MAPPING_FILE
decl_stmt|;
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|appFlowMappingFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
init|;
name|CSVParser
name|parser
operator|=
operator|new
name|CSVParser
argument_list|(
name|reader
argument_list|,
name|csvFormat
argument_list|)
init|)
block|{
for|for
control|(
name|CSVRecord
name|record
range|:
name|parser
operator|.
name|getRecords
argument_list|()
control|)
block|{
if|if
condition|(
name|record
operator|.
name|size
argument_list|()
operator|<
literal|4
condition|)
block|{
continue|continue;
block|}
name|String
name|applicationId
init|=
name|record
operator|.
name|get
argument_list|(
literal|"APP"
argument_list|)
decl_stmt|;
if|if
condition|(
name|applicationId
operator|!=
literal|null
operator|&&
operator|!
name|applicationId
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|applicationId
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|appId
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
name|record
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
operator|+
literal|"/"
operator|+
name|record
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
operator|+
literal|"/"
operator|+
name|record
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to get flow info"
argument_list|)
throw|;
block|}
DECL|method|createEntityToBeReturned (TimelineEntity entity, EnumSet<Field> fieldsToRetrieve)
specifier|private
specifier|static
name|TimelineEntity
name|createEntityToBeReturned
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|)
block|{
name|TimelineEntity
name|entityToBeReturned
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entityToBeReturned
operator|.
name|setIdentifier
argument_list|(
name|entity
operator|.
name|getIdentifier
argument_list|()
argument_list|)
expr_stmt|;
name|entityToBeReturned
operator|.
name|setCreatedTime
argument_list|(
name|entity
operator|.
name|getCreatedTime
argument_list|()
argument_list|)
expr_stmt|;
name|entityToBeReturned
operator|.
name|setModifiedTime
argument_list|(
name|entity
operator|.
name|getModifiedTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldsToRetrieve
operator|!=
literal|null
condition|)
block|{
name|fillFields
argument_list|(
name|entityToBeReturned
argument_list|,
name|entity
argument_list|,
name|fieldsToRetrieve
argument_list|)
expr_stmt|;
block|}
return|return
name|entityToBeReturned
return|;
block|}
DECL|method|isTimeInRange (Long time, Long timeBegin, Long timeEnd)
specifier|private
specifier|static
name|boolean
name|isTimeInRange
parameter_list|(
name|Long
name|time
parameter_list|,
name|Long
name|timeBegin
parameter_list|,
name|Long
name|timeEnd
parameter_list|)
block|{
return|return
operator|(
name|time
operator|>=
name|timeBegin
operator|)
operator|&&
operator|(
name|time
operator|<=
name|timeEnd
operator|)
return|;
block|}
DECL|method|mergeEntities (TimelineEntity entity1, TimelineEntity entity2)
specifier|private
specifier|static
name|void
name|mergeEntities
parameter_list|(
name|TimelineEntity
name|entity1
parameter_list|,
name|TimelineEntity
name|entity2
parameter_list|)
block|{
comment|// Ideally created time wont change except in the case of issue from client.
if|if
condition|(
name|entity2
operator|.
name|getCreatedTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|entity1
operator|.
name|setCreatedTime
argument_list|(
name|entity2
operator|.
name|getCreatedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entity2
operator|.
name|getModifiedTime
argument_list|()
operator|>
literal|0
condition|)
block|{
name|entity1
operator|.
name|setModifiedTime
argument_list|(
name|entity2
operator|.
name|getModifiedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configEntry
range|:
name|entity2
operator|.
name|getConfigs
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entity1
operator|.
name|addConfig
argument_list|(
name|configEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|configEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|infoEntry
range|:
name|entity2
operator|.
name|getInfo
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entity1
operator|.
name|addInfo
argument_list|(
name|infoEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|infoEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|isRelatedToEntry
range|:
name|entity2
operator|.
name|getIsRelatedToEntities
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|type
init|=
name|isRelatedToEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|entityId
range|:
name|isRelatedToEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|entity1
operator|.
name|addIsRelatedToEntity
argument_list|(
name|type
argument_list|,
name|entityId
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatesToEntry
range|:
name|entity2
operator|.
name|getRelatesToEntities
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|type
init|=
name|relatesToEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|entityId
range|:
name|relatesToEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|entity1
operator|.
name|addRelatesToEntity
argument_list|(
name|type
argument_list|,
name|entityId
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|TimelineEvent
name|event
range|:
name|entity2
operator|.
name|getEvents
argument_list|()
control|)
block|{
name|entity1
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TimelineMetric
name|metric2
range|:
name|entity2
operator|.
name|getMetrics
argument_list|()
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|TimelineMetric
name|metric1
range|:
name|entity1
operator|.
name|getMetrics
argument_list|()
control|)
block|{
if|if
condition|(
name|metric1
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|metric2
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|metric1
operator|.
name|addValues
argument_list|(
name|metric2
operator|.
name|getValues
argument_list|()
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|entity1
operator|.
name|addMetric
argument_list|(
name|metric2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|readEntityFromFile (BufferedReader reader)
specifier|private
specifier|static
name|TimelineEntity
name|readEntityFromFile
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|TimelineEntity
name|entity
init|=
name|getTimelineRecordFromJSON
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|,
name|TimelineEntity
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|entityStr
init|=
literal|""
decl_stmt|;
while|while
condition|(
operator|(
name|entityStr
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|entityStr
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|TimelineEntity
name|anotherEntity
init|=
name|getTimelineRecordFromJSON
argument_list|(
name|entityStr
argument_list|,
name|TimelineEntity
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entity
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|anotherEntity
operator|.
name|getId
argument_list|()
argument_list|)
operator|||
operator|!
name|entity
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|anotherEntity
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|mergeEntities
argument_list|(
name|entity
argument_list|,
name|anotherEntity
argument_list|)
expr_stmt|;
block|}
return|return
name|entity
return|;
block|}
DECL|method|getEntities (File dir, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Long modifiedTimeBegin, Long modifiedTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, EnumSet<Field> fieldsToRetrieve)
specifier|private
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|getEntities
parameter_list|(
name|File
name|dir
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
throws|throws
name|IOException
block|{
if|if
condition|(
name|limit
operator|==
literal|null
operator|||
name|limit
operator|<=
literal|0
condition|)
block|{
name|limit
operator|=
name|DEFAULT_LIMIT
expr_stmt|;
block|}
if|if
condition|(
name|createdTimeBegin
operator|==
literal|null
operator|||
name|createdTimeBegin
operator|<=
literal|0
condition|)
block|{
name|createdTimeBegin
operator|=
literal|0L
expr_stmt|;
block|}
if|if
condition|(
name|createdTimeEnd
operator|==
literal|null
operator|||
name|createdTimeEnd
operator|<=
literal|0
condition|)
block|{
name|createdTimeEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|modifiedTimeBegin
operator|==
literal|null
operator|||
name|modifiedTimeBegin
operator|<=
literal|0
condition|)
block|{
name|modifiedTimeBegin
operator|=
literal|0L
expr_stmt|;
block|}
if|if
condition|(
name|modifiedTimeEnd
operator|==
literal|null
operator|||
name|modifiedTimeEnd
operator|<=
literal|0
condition|)
block|{
name|modifiedTimeEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
comment|// First sort the selected entities based on created/start time.
name|Map
argument_list|<
name|Long
argument_list|,
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
argument_list|>
name|sortedEntities
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|(
operator|new
name|Comparator
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Long
name|l1
parameter_list|,
name|Long
name|l2
parameter_list|)
block|{
return|return
name|l2
operator|.
name|compareTo
argument_list|(
name|l1
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|entityFile
range|:
name|dir
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entityFile
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
name|TIMELINE_SERVICE_STORAGE_EXTENSION
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|entityFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|TimelineEntity
name|entity
init|=
name|readEntityFromFile
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entity
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|entityType
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|isTimeInRange
argument_list|(
name|entity
operator|.
name|getCreatedTime
argument_list|()
argument_list|,
name|createdTimeBegin
argument_list|,
name|createdTimeEnd
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|isTimeInRange
argument_list|(
name|entity
operator|.
name|getModifiedTime
argument_list|()
argument_list|,
name|modifiedTimeBegin
argument_list|,
name|modifiedTimeEnd
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|relatesTo
operator|!=
literal|null
operator|&&
operator|!
name|relatesTo
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|isRelatedTo
operator|!=
literal|null
operator|&&
operator|!
name|isRelatedTo
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|infoFilters
operator|!=
literal|null
operator|&&
operator|!
name|infoFilters
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|configFilters
operator|!=
literal|null
operator|&&
operator|!
name|configFilters
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|metricFilters
operator|!=
literal|null
operator|&&
operator|!
name|metricFilters
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
if|if
condition|(
name|eventFilters
operator|!=
literal|null
operator|&&
operator|!
name|eventFilters
operator|.
name|isEmpty
argument_list|()
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
continue|continue;
block|}
name|TimelineEntity
name|entityToBeReturned
init|=
name|createEntityToBeReturned
argument_list|(
name|entity
argument_list|,
name|fieldsToRetrieve
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entitiesCreatedAtSameTime
init|=
name|sortedEntities
operator|.
name|get
argument_list|(
name|entityToBeReturned
operator|.
name|getCreatedTime
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entitiesCreatedAtSameTime
operator|==
literal|null
condition|)
block|{
name|entitiesCreatedAtSameTime
operator|=
operator|new
name|HashSet
argument_list|<
name|TimelineEntity
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|entitiesCreatedAtSameTime
operator|.
name|add
argument_list|(
name|entityToBeReturned
argument_list|)
expr_stmt|;
name|sortedEntities
operator|.
name|put
argument_list|(
name|entityToBeReturned
operator|.
name|getCreatedTime
argument_list|()
argument_list|,
name|entitiesCreatedAtSameTime
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entities
init|=
operator|new
name|HashSet
argument_list|<
name|TimelineEntity
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|entitiesAdded
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|entitySet
range|:
name|sortedEntities
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entitySet
control|)
block|{
name|entities
operator|.
name|add
argument_list|(
name|entity
argument_list|)
expr_stmt|;
operator|++
name|entitiesAdded
expr_stmt|;
if|if
condition|(
name|entitiesAdded
operator|>=
name|limit
condition|)
block|{
return|return
name|entities
return|;
block|}
block|}
block|}
return|return
name|entities
return|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|rootPath
operator|=
name|conf
operator|.
name|get
argument_list|(
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|DEFAULT_TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntity (String userId, String clusterId, String flowId, Long flowRunId, String appId, String entityType, String entityId, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|TimelineEntity
name|getEntity
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
throws|throws
name|IOException
block|{
name|String
name|flowRunPath
init|=
name|getFlowRunPath
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
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|rootPath
argument_list|,
name|ENTITIES_DIR
argument_list|)
argument_list|,
name|clusterId
operator|+
literal|"/"
operator|+
name|flowRunPath
operator|+
literal|"/"
operator|+
name|appId
operator|+
literal|"/"
operator|+
name|entityType
argument_list|)
decl_stmt|;
name|File
name|entityFile
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|entityId
operator|+
name|TIMELINE_SERVICE_STORAGE_EXTENSION
argument_list|)
decl_stmt|;
try|try
init|(
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|entityFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|TimelineEntity
name|entity
init|=
name|readEntityFromFile
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
name|createEntityToBeReturned
argument_list|(
name|entity
argument_list|,
name|fieldsToRetrieve
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEntities (String userId, String clusterId, String flowId, Long flowRunId, String appId, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Long modifiedTimeBegin, Long modifiedTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|getEntities
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
throws|throws
name|IOException
block|{
name|String
name|flowRunPath
init|=
name|getFlowRunPath
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
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|rootPath
argument_list|,
name|ENTITIES_DIR
argument_list|)
argument_list|,
name|clusterId
operator|+
literal|"/"
operator|+
name|flowRunPath
operator|+
literal|"/"
operator|+
name|appId
operator|+
literal|"/"
operator|+
name|entityType
argument_list|)
decl_stmt|;
return|return
name|getEntities
argument_list|(
name|dir
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
return|;
block|}
block|}
end_class

end_unit

