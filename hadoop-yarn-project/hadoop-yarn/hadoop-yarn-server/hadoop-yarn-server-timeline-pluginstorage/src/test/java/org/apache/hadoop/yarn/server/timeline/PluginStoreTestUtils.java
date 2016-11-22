begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonInclude
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|util
operator|.
name|MinimalPrettyPrinter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|type
operator|.
name|TypeFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|module
operator|.
name|jaxb
operator|.
name|JaxbAnnotationIntrospector
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|fs
operator|.
name|FSDataOutputStream
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|security
operator|.
name|UserGroupInformation
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
name|timeline
operator|.
name|TimelineEntities
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
name|timeline
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
name|timeline
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
name|exceptions
operator|.
name|YarnException
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
name|timeline
operator|.
name|security
operator|.
name|TimelineACLsManager
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
name|util
operator|.
name|ArrayList
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
name|HashMap
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
name|List
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Utility methods related to the ATS v1.5 plugin storage tests.  */
end_comment

begin_class
DECL|class|PluginStoreTestUtils
specifier|public
class|class
name|PluginStoreTestUtils
block|{
comment|/**    * For a given file system, setup directories ready to test the plugin storage.    *    * @param fs a {@link FileSystem} object that the plugin storage will work with    * @return the dfsCluster ready to start plugin storage tests.    * @throws IOException    */
DECL|method|prepareFileSystemForPluginStore (FileSystem fs)
specifier|public
specifier|static
name|FileSystem
name|prepareFileSystemForPluginStore
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|activeDir
init|=
operator|new
name|Path
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_ACTIVE_DIR_DEFAULT
argument_list|)
decl_stmt|;
name|Path
name|doneDir
init|=
operator|new
name|Path
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_DONE_DIR_DEFAULT
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|activeDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|doneDir
argument_list|)
expr_stmt|;
return|return
name|fs
return|;
block|}
comment|/**    * Prepare configuration for plugin tests. This method will also add the mini    * DFS cluster's info to the configuration.    * Note: the test program needs to setup the reader plugin by itself.    *    * @param conf    * @param dfsCluster    * @return the modified configuration    */
DECL|method|prepareConfiguration (YarnConfiguration conf, MiniDFSCluster dfsCluster)
specifier|public
specifier|static
name|YarnConfiguration
name|prepareConfiguration
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|,
name|MiniDFSCluster
name|dfsCluster
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
name|dfsCluster
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_VERSION
argument_list|,
literal|1.5f
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_SCAN_INTERVAL_SECONDS
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_STORE
argument_list|,
name|EntityGroupFSTimelineStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|createLogFile (Path logPath, FileSystem fs)
specifier|static
name|FSDataOutputStream
name|createLogFile
parameter_list|(
name|Path
name|logPath
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|stream
decl_stmt|;
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|logPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
DECL|method|createObjectMapper ()
specifier|static
name|ObjectMapper
name|createObjectMapper
parameter_list|()
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|setAnnotationIntrospector
argument_list|(
operator|new
name|JaxbAnnotationIntrospector
argument_list|(
name|TypeFactory
operator|.
name|defaultInstance
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|setSerializationInclusion
argument_list|(
name|JsonInclude
operator|.
name|Include
operator|.
name|NON_NULL
argument_list|)
expr_stmt|;
return|return
name|mapper
return|;
block|}
comment|/**    * Create sample entities for testing    * @return two timeline entities in a {@link TimelineEntities} object    */
DECL|method|generateTestEntities ()
specifier|static
name|TimelineEntities
name|generateTestEntities
parameter_list|()
block|{
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFilters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|l1
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|l1
operator|.
name|add
argument_list|(
literal|"username"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|l2
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|l2
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|l3
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|l3
operator|.
name|add
argument_list|(
literal|"123abc"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Object
argument_list|>
name|l4
init|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|l4
operator|.
name|add
argument_list|(
operator|(
name|long
operator|)
name|Integer
operator|.
name|MAX_VALUE
operator|+
literal|1l
argument_list|)
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"user"
argument_list|,
name|l1
argument_list|)
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"appname"
argument_list|,
name|l2
argument_list|)
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"other"
argument_list|,
name|l3
argument_list|)
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"long"
argument_list|,
name|l4
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|secondaryFilters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|secondaryFilters
operator|.
name|put
argument_list|(
literal|"startTime"
argument_list|,
literal|123456
argument_list|)
expr_stmt|;
name|secondaryFilters
operator|.
name|put
argument_list|(
literal|"status"
argument_list|,
literal|"RUNNING"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo1
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|otherInfo1
operator|.
name|put
argument_list|(
literal|"info1"
argument_list|,
literal|"val1"
argument_list|)
expr_stmt|;
name|otherInfo1
operator|.
name|putAll
argument_list|(
name|secondaryFilters
argument_list|)
expr_stmt|;
name|String
name|entityId1
init|=
literal|"id_1"
decl_stmt|;
name|String
name|entityType1
init|=
literal|"type_1"
decl_stmt|;
name|String
name|entityId2
init|=
literal|"id_2"
decl_stmt|;
name|String
name|entityType2
init|=
literal|"type_2"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntities
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|relatedEntities
operator|.
name|put
argument_list|(
name|entityType2
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|entityId2
argument_list|)
argument_list|)
expr_stmt|;
name|TimelineEvent
name|ev3
init|=
name|createEvent
argument_list|(
literal|789l
argument_list|,
literal|"launch_event"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TimelineEvent
name|ev4
init|=
name|createEvent
argument_list|(
literal|0l
argument_list|,
literal|"init_event"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TimelineEvent
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<
name|TimelineEvent
argument_list|>
argument_list|()
decl_stmt|;
name|events
operator|.
name|add
argument_list|(
name|ev3
argument_list|)
expr_stmt|;
name|events
operator|.
name|add
argument_list|(
name|ev4
argument_list|)
expr_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|createEntity
argument_list|(
name|entityId2
argument_list|,
name|entityType2
argument_list|,
literal|456l
argument_list|,
name|events
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"domain_id_1"
argument_list|)
argument_list|)
expr_stmt|;
name|TimelineEvent
name|ev1
init|=
name|createEvent
argument_list|(
literal|123l
argument_list|,
literal|"start_event"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|createEntity
argument_list|(
name|entityId1
argument_list|,
name|entityType1
argument_list|,
literal|123l
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|ev1
argument_list|)
argument_list|,
name|relatedEntities
argument_list|,
name|primaryFilters
argument_list|,
name|otherInfo1
argument_list|,
literal|"domain_id_1"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|entities
return|;
block|}
DECL|method|verifyTestEntities (TimelineDataManager tdm)
specifier|static
name|void
name|verifyTestEntities
parameter_list|(
name|TimelineDataManager
name|tdm
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|TimelineEntity
name|entity1
init|=
name|tdm
operator|.
name|getEntity
argument_list|(
literal|"type_1"
argument_list|,
literal|"id_1"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|TimelineReader
operator|.
name|Field
operator|.
name|class
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineEntity
name|entity2
init|=
name|tdm
operator|.
name|getEntity
argument_list|(
literal|"type_2"
argument_list|,
literal|"id_2"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|TimelineReader
operator|.
name|Field
operator|.
name|class
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entity1
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|entity2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to read out entity 1"
argument_list|,
operator|(
name|Long
operator|)
literal|123l
argument_list|,
name|entity1
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Failed to read out entity 2"
argument_list|,
operator|(
name|Long
operator|)
literal|456l
argument_list|,
name|entity2
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a test entity    */
DECL|method|createEntity (String entityId, String entityType, Long startTime, List<TimelineEvent> events, Map<String, Set<String>> relatedEntities, Map<String, Set<Object>> primaryFilters, Map<String, Object> otherInfo, String domainId)
specifier|static
name|TimelineEntity
name|createEntity
parameter_list|(
name|String
name|entityId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|Long
name|startTime
parameter_list|,
name|List
argument_list|<
name|TimelineEvent
argument_list|>
name|events
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
name|relatedEntities
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFilters
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo
parameter_list|,
name|String
name|domainId
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setEntityId
argument_list|(
name|entityId
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
name|entityType
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEvents
argument_list|(
name|events
argument_list|)
expr_stmt|;
if|if
condition|(
name|relatedEntities
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|relatedEntities
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|v
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|entity
operator|.
name|addRelatedEntity
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|entity
operator|.
name|setRelatedEntities
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|entity
operator|.
name|setPrimaryFilters
argument_list|(
name|primaryFilters
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setOtherInfo
argument_list|(
name|otherInfo
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setDomainId
argument_list|(
name|domainId
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
comment|/**    * Create a test event    */
DECL|method|createEvent (long timestamp, String type, Map<String, Object> info)
specifier|static
name|TimelineEvent
name|createEvent
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|String
name|type
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
block|{
name|TimelineEvent
name|event
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event
operator|.
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|event
return|;
block|}
comment|/**    * Write timeline entities to a file system    * @param entities    * @param logPath    * @param fs    * @throws IOException    */
DECL|method|writeEntities (TimelineEntities entities, Path logPath, FileSystem fs)
specifier|static
name|void
name|writeEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|Path
name|logPath
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|outStream
init|=
name|createLogFile
argument_list|(
name|logPath
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|JsonGenerator
name|jsonGenerator
init|=
operator|new
name|JsonFactory
argument_list|()
operator|.
name|createGenerator
argument_list|(
name|outStream
argument_list|)
decl_stmt|;
name|jsonGenerator
operator|.
name|setPrettyPrinter
argument_list|(
operator|new
name|MinimalPrettyPrinter
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
name|ObjectMapper
name|objMapper
init|=
name|createObjectMapper
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|objMapper
operator|.
name|writeValue
argument_list|(
name|jsonGenerator
argument_list|,
name|entity
argument_list|)
expr_stmt|;
block|}
name|outStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getTdmWithStore (Configuration config, TimelineStore store)
specifier|static
name|TimelineDataManager
name|getTdmWithStore
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|TimelineStore
name|store
parameter_list|)
block|{
name|TimelineACLsManager
name|aclManager
init|=
operator|new
name|TimelineACLsManager
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|TimelineDataManager
name|tdm
init|=
operator|new
name|TimelineDataManager
argument_list|(
name|store
argument_list|,
name|aclManager
argument_list|)
decl_stmt|;
name|tdm
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|tdm
return|;
block|}
DECL|method|getTdmWithMemStore (Configuration config)
specifier|static
name|TimelineDataManager
name|getTdmWithMemStore
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|TimelineStore
name|store
init|=
operator|new
name|MemoryTimelineStore
argument_list|(
literal|"MemoryStore.test"
argument_list|)
decl_stmt|;
name|TimelineDataManager
name|tdm
init|=
name|getTdmWithStore
argument_list|(
name|config
argument_list|,
name|store
argument_list|)
decl_stmt|;
return|return
name|tdm
return|;
block|}
block|}
end_class

end_unit

