begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
name|common
package|;
end_package

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
name|assertTrue
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
name|util
operator|.
name|Bytes
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
name|ApplicationId
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
name|ApplicationRowKeyPrefix
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
name|apptoflow
operator|.
name|AppToFlowRowKey
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
name|flow
operator|.
name|FlowActivityRowKey
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
name|flow
operator|.
name|FlowActivityRowKeyPrefix
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
name|flow
operator|.
name|FlowRunRowKey
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
name|subapplication
operator|.
name|SubApplicationRowKey
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
name|domain
operator|.
name|DomainRowKey
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Class to test the row key structures for various tables.  *  */
end_comment

begin_class
DECL|class|TestRowKeys
specifier|public
class|class
name|TestRowKeys
block|{
DECL|field|QUALIFIER_SEP
specifier|private
specifier|final
specifier|static
name|String
name|QUALIFIER_SEP
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|getValue
argument_list|()
decl_stmt|;
DECL|field|QUALIFIER_SEP_BYTES
specifier|private
specifier|final
specifier|static
name|byte
index|[]
name|QUALIFIER_SEP_BYTES
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|QUALIFIER_SEP
argument_list|)
decl_stmt|;
DECL|field|CLUSTER
specifier|private
specifier|final
specifier|static
name|String
name|CLUSTER
init|=
literal|"cl"
operator|+
name|QUALIFIER_SEP
operator|+
literal|"uster"
decl_stmt|;
DECL|field|USER
specifier|private
specifier|final
specifier|static
name|String
name|USER
init|=
name|QUALIFIER_SEP
operator|+
literal|"user"
decl_stmt|;
DECL|field|SUB_APP_USER
specifier|private
specifier|final
specifier|static
name|String
name|SUB_APP_USER
init|=
name|QUALIFIER_SEP
operator|+
literal|"subAppUser"
decl_stmt|;
DECL|field|FLOW_NAME
specifier|private
specifier|final
specifier|static
name|String
name|FLOW_NAME
init|=
literal|"dummy_"
operator|+
name|QUALIFIER_SEP
operator|+
literal|"flow"
operator|+
name|QUALIFIER_SEP
decl_stmt|;
DECL|field|FLOW_RUN_ID
specifier|private
specifier|final
specifier|static
name|Long
name|FLOW_RUN_ID
decl_stmt|;
DECL|field|APPLICATION_ID
specifier|private
specifier|final
specifier|static
name|String
name|APPLICATION_ID
decl_stmt|;
static|static
block|{
name|long
name|runid
init|=
name|Long
operator|.
name|MAX_VALUE
operator|-
literal|900L
decl_stmt|;
name|byte
index|[]
name|longMaxByteArr
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|byte
index|[]
name|byteArr
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|runid
argument_list|)
decl_stmt|;
name|int
name|sepByteLen
init|=
name|QUALIFIER_SEP_BYTES
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|sepByteLen
operator|<=
name|byteArr
operator|.
name|length
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sepByteLen
condition|;
name|i
operator|++
control|)
block|{
name|byteArr
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|longMaxByteArr
index|[
name|i
index|]
operator|-
name|QUALIFIER_SEP_BYTES
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|FLOW_RUN_ID
operator|=
name|Bytes
operator|.
name|toLong
argument_list|(
name|byteArr
argument_list|)
expr_stmt|;
name|long
name|clusterTs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|byteArr
operator|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|clusterTs
argument_list|)
expr_stmt|;
if|if
condition|(
name|sepByteLen
operator|<=
name|byteArr
operator|.
name|length
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sepByteLen
condition|;
name|i
operator|++
control|)
block|{
name|byteArr
index|[
name|byteArr
operator|.
name|length
operator|-
name|sepByteLen
operator|+
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|longMaxByteArr
index|[
name|byteArr
operator|.
name|length
operator|-
name|sepByteLen
operator|+
name|i
index|]
operator|-
name|QUALIFIER_SEP_BYTES
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|clusterTs
operator|=
name|Bytes
operator|.
name|toLong
argument_list|(
name|byteArr
argument_list|)
expr_stmt|;
name|int
name|seqId
init|=
literal|222
decl_stmt|;
name|APPLICATION_ID
operator|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|clusterTs
argument_list|,
name|seqId
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyRowPrefixBytes (byte[] byteRowKeyPrefix)
specifier|private
specifier|static
name|void
name|verifyRowPrefixBytes
parameter_list|(
name|byte
index|[]
name|byteRowKeyPrefix
parameter_list|)
block|{
name|int
name|sepLen
init|=
name|QUALIFIER_SEP_BYTES
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sepLen
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"Row key prefix not encoded properly."
argument_list|,
name|byteRowKeyPrefix
index|[
name|byteRowKeyPrefix
operator|.
name|length
operator|-
name|sepLen
operator|+
name|i
index|]
operator|==
name|QUALIFIER_SEP_BYTES
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testApplicationRowKey ()
specifier|public
name|void
name|testApplicationRowKey
parameter_list|()
block|{
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|ApplicationRowKey
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|,
name|APPLICATION_ID
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|ApplicationRowKey
name|rowKey
init|=
name|ApplicationRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|USER
argument_list|,
name|rowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|rowKey
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_RUN_ID
argument_list|,
name|rowKey
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|rowKey
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKeyPrefix
init|=
operator|new
name|ApplicationRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|4
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_RUN_ID
argument_list|,
operator|(
name|Long
operator|)
name|LongConverter
operator|.
name|invertLong
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|3
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
name|byteRowKeyPrefix
operator|=
operator|new
name|ApplicationRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
expr_stmt|;
name|splits
operator|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|3
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests the converters indirectly through the public methods of the    * corresponding rowkey.    */
annotation|@
name|Test
DECL|method|testAppToFlowRowKey ()
specifier|public
name|void
name|testAppToFlowRowKey
parameter_list|()
block|{
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|AppToFlowRowKey
argument_list|(
name|APPLICATION_ID
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|AppToFlowRowKey
name|rowKey
init|=
name|AppToFlowRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|rowKey
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntityRowKey ()
specifier|public
name|void
name|testEntityRowKey
parameter_list|()
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
name|setId
argument_list|(
literal|"!ent!ity!!id!"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
literal|"entity!Type"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setIdPrefix
argument_list|(
literal|54321
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|EntityRowKey
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|,
name|APPLICATION_ID
argument_list|,
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
name|entity
operator|.
name|getIdPrefix
argument_list|()
argument_list|,
name|entity
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|EntityRowKey
name|rowKey
init|=
name|EntityRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|USER
argument_list|,
name|rowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|rowKey
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_RUN_ID
argument_list|,
name|rowKey
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|rowKey
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getIdPrefix
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityIdPrefix
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKeyPrefix
init|=
operator|new
name|EntityRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|,
name|APPLICATION_ID
argument_list|,
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|AppIdKeyConverter
operator|.
name|getKeySize
argument_list|()
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
operator|new
name|AppIdKeyConverter
argument_list|()
operator|.
name|decode
argument_list|(
name|splits
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|5
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
name|byteRowKeyPrefix
operator|=
operator|new
name|EntityRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|,
name|APPLICATION_ID
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
expr_stmt|;
name|splits
operator|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|AppIdKeyConverter
operator|.
name|getKeySize
argument_list|()
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|5
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|AppIdKeyConverter
name|appIdKeyConverter
init|=
operator|new
name|AppIdKeyConverter
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|APPLICATION_ID
argument_list|,
name|appIdKeyConverter
operator|.
name|decode
argument_list|(
name|splits
index|[
literal|4
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlowActivityRowKey ()
specifier|public
name|void
name|testFlowActivityRowKey
parameter_list|()
block|{
name|Long
name|ts
init|=
literal|1459900830000L
decl_stmt|;
name|Long
name|dayTimestamp
init|=
name|HBaseTimelineSchemaUtils
operator|.
name|getTopOfTheDayTimestamp
argument_list|(
name|ts
argument_list|)
decl_stmt|;
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|FlowActivityRowKey
argument_list|(
name|CLUSTER
argument_list|,
name|ts
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|FlowActivityRowKey
name|rowKey
init|=
name|FlowActivityRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dayTimestamp
argument_list|,
name|rowKey
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|USER
argument_list|,
name|rowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|rowKey
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKeyPrefix
init|=
operator|new
name|FlowActivityRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|1
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
name|byteRowKeyPrefix
operator|=
operator|new
name|FlowActivityRowKeyPrefix
argument_list|(
name|CLUSTER
argument_list|,
name|ts
argument_list|)
operator|.
name|getRowKeyPrefix
argument_list|()
expr_stmt|;
name|splits
operator|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Bytes
operator|.
name|SIZEOF_LONG
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|2
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ts
argument_list|,
operator|(
name|Long
operator|)
name|LongConverter
operator|.
name|invertLong
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|splits
index|[
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFlowRunRowKey ()
specifier|public
name|void
name|testFlowRunRowKey
parameter_list|()
block|{
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|FlowRunRowKey
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
name|FLOW_RUN_ID
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|FlowRunRowKey
name|rowKey
init|=
name|FlowRunRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|USER
argument_list|,
name|rowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|rowKey
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_RUN_ID
argument_list|,
name|rowKey
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKeyPrefix
init|=
operator|new
name|FlowRunRowKey
argument_list|(
name|CLUSTER
argument_list|,
name|USER
argument_list|,
name|FLOW_NAME
argument_list|,
literal|null
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|byteRowKeyPrefix
argument_list|,
operator|new
name|int
index|[]
block|{
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|,
name|Separator
operator|.
name|VARIABLE_SIZE
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|splits
index|[
literal|3
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FLOW_NAME
argument_list|,
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|decode
argument_list|(
name|Bytes
operator|.
name|toString
argument_list|(
name|splits
index|[
literal|2
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|verifyRowPrefixBytes
argument_list|(
name|byteRowKeyPrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubAppRowKey ()
specifier|public
name|void
name|testSubAppRowKey
parameter_list|()
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
name|setId
argument_list|(
literal|"entity1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
literal|"DAG"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setIdPrefix
argument_list|(
literal|54321
argument_list|)
expr_stmt|;
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|SubApplicationRowKey
argument_list|(
name|SUB_APP_USER
argument_list|,
name|CLUSTER
argument_list|,
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
name|entity
operator|.
name|getIdPrefix
argument_list|()
argument_list|,
name|entity
operator|.
name|getId
argument_list|()
argument_list|,
name|USER
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|SubApplicationRowKey
name|rowKey
init|=
name|SubApplicationRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|CLUSTER
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SUB_APP_USER
argument_list|,
name|rowKey
operator|.
name|getSubAppUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getIdPrefix
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityIdPrefix
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entity
operator|.
name|getId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|USER
argument_list|,
name|rowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDomainRowKey ()
specifier|public
name|void
name|testDomainRowKey
parameter_list|()
block|{
name|String
name|clusterId
init|=
literal|"cluster1@dc1"
decl_stmt|;
name|String
name|domainId
init|=
literal|"helloworld"
decl_stmt|;
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|DomainRowKey
argument_list|(
name|clusterId
argument_list|,
name|domainId
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|DomainRowKey
name|rowKey
init|=
name|DomainRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|clusterId
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|domainId
argument_list|,
name|rowKey
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|rowKeyStr
init|=
name|rowKey
operator|.
name|getRowKeyAsString
argument_list|()
decl_stmt|;
name|DomainRowKey
name|drk
init|=
name|DomainRowKey
operator|.
name|parseRowKeyFromString
argument_list|(
name|rowKeyStr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|drk
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|drk
operator|.
name|getDomainId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDomainRowKeySpecialChars ()
specifier|public
name|void
name|testDomainRowKeySpecialChars
parameter_list|()
block|{
name|String
name|clusterId
init|=
literal|"cluster1!temp!dc1"
decl_stmt|;
name|String
name|domainId
init|=
literal|"hello=world"
decl_stmt|;
name|byte
index|[]
name|byteRowKey
init|=
operator|new
name|DomainRowKey
argument_list|(
name|clusterId
argument_list|,
name|domainId
argument_list|)
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|DomainRowKey
name|rowKey
init|=
name|DomainRowKey
operator|.
name|parseRowKey
argument_list|(
name|byteRowKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|clusterId
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|domainId
argument_list|,
name|rowKey
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|rowKeyStr
init|=
name|rowKey
operator|.
name|getRowKeyAsString
argument_list|()
decl_stmt|;
name|DomainRowKey
name|drk
init|=
name|DomainRowKey
operator|.
name|parseRowKeyFromString
argument_list|(
name|rowKeyStr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|drk
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|drk
operator|.
name|getDomainId
argument_list|()
argument_list|,
name|rowKey
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

