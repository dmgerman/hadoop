begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.reader
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
operator|.
name|reader
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|hbase
operator|.
name|client
operator|.
name|ResultScanner
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
name|Scan
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
name|filter
operator|.
name|FilterList
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
name|filter
operator|.
name|FirstKeyOnlyFilter
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
name|filter
operator|.
name|KeyOnlyFilter
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
name|filter
operator|.
name|PageFilter
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
name|server
operator|.
name|timelineservice
operator|.
name|reader
operator|.
name|TimelineReaderContext
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
name|HBaseTimelineStorageUtils
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
name|Separator
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
name|entity
operator|.
name|EntityRowKey
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
name|entity
operator|.
name|EntityRowKeyPrefix
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
name|entity
operator|.
name|EntityTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Arrays
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
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * Timeline entity reader for listing all available entity types given one  * reader context. Right now only supports listing all entity types within one  * YARN application.  */
end_comment

begin_class
DECL|class|EntityTypeReader
specifier|public
specifier|final
class|class
name|EntityTypeReader
extends|extends
name|AbstractTimelineStorageReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EntityTypeReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ENTITY_TABLE
specifier|private
specifier|static
specifier|final
name|EntityTable
name|ENTITY_TABLE
init|=
operator|new
name|EntityTable
argument_list|()
decl_stmt|;
DECL|method|EntityTypeReader (TimelineReaderContext context)
specifier|public
name|EntityTypeReader
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads a set of timeline entity types from the HBase storage for the given    * context.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @return a set of<cite>TimelineEntity</cite> objects, with only type field    *         set.    * @throws IOException if any exception is encountered while reading entities.    */
DECL|method|readEntityTypes (Configuration hbaseConf, Connection conn)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|readEntityTypes
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
name|validateParams
argument_list|()
expr_stmt|;
name|augmentParams
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|TimelineReaderContext
name|context
init|=
name|getContext
argument_list|()
decl_stmt|;
name|EntityRowKeyPrefix
name|prefix
init|=
operator|new
name|EntityRowKeyPrefix
argument_list|(
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowRunId
argument_list|()
argument_list|,
name|context
operator|.
name|getAppId
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|currRowKey
init|=
name|prefix
operator|.
name|getRowKeyPrefix
argument_list|()
decl_stmt|;
name|byte
index|[]
name|nextRowKey
init|=
name|prefix
operator|.
name|getRowKeyPrefix
argument_list|()
decl_stmt|;
name|nextRowKey
index|[
name|nextRowKey
operator|.
name|length
operator|-
literal|1
index|]
operator|++
expr_stmt|;
name|FilterList
name|typeFilterList
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
name|typeFilterList
operator|.
name|addFilter
argument_list|(
operator|new
name|FirstKeyOnlyFilter
argument_list|()
argument_list|)
expr_stmt|;
name|typeFilterList
operator|.
name|addFilter
argument_list|(
operator|new
name|KeyOnlyFilter
argument_list|()
argument_list|)
expr_stmt|;
name|typeFilterList
operator|.
name|addFilter
argument_list|(
operator|new
name|PageFilter
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"FilterList created for scan is - {}"
argument_list|,
name|typeFilterList
argument_list|)
expr_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
init|(
name|ResultScanner
name|results
init|=
name|getResult
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|typeFilterList
argument_list|,
name|currRowKey
argument_list|,
name|nextRowKey
argument_list|)
init|)
block|{
name|TimelineEntity
name|entity
init|=
name|parseEntityForType
argument_list|(
name|results
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
break|break;
block|}
operator|++
name|counter
expr_stmt|;
if|if
condition|(
operator|!
name|types
operator|.
name|add
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to add type "
operator|+
name|entity
operator|.
name|getType
argument_list|()
operator|+
literal|" to the result set because there is a duplicated copy. "
argument_list|)
expr_stmt|;
block|}
name|String
name|currType
init|=
name|entity
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Current row key: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|currRowKey
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"New entity type discovered: "
operator|+
name|currType
argument_list|)
expr_stmt|;
block|}
name|currRowKey
operator|=
name|getNextRowKey
argument_list|(
name|prefix
operator|.
name|getRowKeyPrefix
argument_list|()
argument_list|,
name|currType
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Scanned {} records for {} types"
argument_list|,
name|counter
argument_list|,
name|types
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|types
return|;
block|}
annotation|@
name|Override
DECL|method|validateParams ()
specifier|protected
name|void
name|validateParams
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
argument_list|,
literal|"context shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getClusterId
argument_list|()
argument_list|,
literal|"clusterId shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getAppId
argument_list|()
argument_list|,
literal|"appId shouldn't be null"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Gets the possibly next row key prefix given current prefix and type.    *    * @param currRowKeyPrefix The current prefix that contains user, cluster,    *                         flow, run, and application id.    * @param entityType Current entity type.    * @return A new prefix for the possibly immediately next row key.    */
DECL|method|getNextRowKey (byte[] currRowKeyPrefix, String entityType)
specifier|private
specifier|static
name|byte
index|[]
name|getNextRowKey
parameter_list|(
name|byte
index|[]
name|currRowKeyPrefix
parameter_list|,
name|String
name|entityType
parameter_list|)
block|{
if|if
condition|(
name|currRowKeyPrefix
operator|==
literal|null
operator|||
name|entityType
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|byte
index|[]
name|entityTypeEncoded
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|Separator
operator|.
name|encode
argument_list|(
name|entityType
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|,
name|Separator
operator|.
name|TAB
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
argument_list|)
argument_list|,
name|Separator
operator|.
name|EMPTY_BYTES
argument_list|)
decl_stmt|;
name|byte
index|[]
name|currRowKey
init|=
operator|new
name|byte
index|[
name|currRowKeyPrefix
operator|.
name|length
operator|+
name|entityTypeEncoded
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|currRowKeyPrefix
argument_list|,
literal|0
argument_list|,
name|currRowKey
argument_list|,
literal|0
argument_list|,
name|currRowKeyPrefix
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|entityTypeEncoded
argument_list|,
literal|0
argument_list|,
name|currRowKey
argument_list|,
name|currRowKeyPrefix
operator|.
name|length
argument_list|,
name|entityTypeEncoded
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|HBaseTimelineStorageUtils
operator|.
name|calculateTheClosestNextRowKeyForPrefix
argument_list|(
name|currRowKey
argument_list|)
return|;
block|}
DECL|method|getResult (Configuration hbaseConf, Connection conn, FilterList filterList, byte[] startPrefix, byte[] endPrefix)
specifier|private
name|ResultScanner
name|getResult
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|FilterList
name|filterList
parameter_list|,
name|byte
index|[]
name|startPrefix
parameter_list|,
name|byte
index|[]
name|endPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|Scan
name|scan
init|=
operator|new
name|Scan
argument_list|(
name|startPrefix
argument_list|,
name|endPrefix
argument_list|)
decl_stmt|;
name|scan
operator|.
name|setFilter
argument_list|(
name|filterList
argument_list|)
expr_stmt|;
name|scan
operator|.
name|setSmall
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|ENTITY_TABLE
operator|.
name|getResultScanner
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|scan
argument_list|)
return|;
block|}
DECL|method|parseEntityForType (Result result)
specifier|private
name|TimelineEntity
name|parseEntityForType
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
name|EntityRowKey
name|newRowKey
init|=
name|EntityRowKey
operator|.
name|parseRowKey
argument_list|(
name|result
operator|.
name|getRow
argument_list|()
argument_list|)
decl_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|newRowKey
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
block|}
end_class

end_unit

