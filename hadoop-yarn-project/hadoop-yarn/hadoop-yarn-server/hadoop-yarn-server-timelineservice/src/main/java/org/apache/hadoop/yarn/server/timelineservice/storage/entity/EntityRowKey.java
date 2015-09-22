begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.entity
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
name|entity
package|;
end_package

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
name|common
operator|.
name|TimelineWriterUtils
import|;
end_import

begin_comment
comment|/**  * Represents a rowkey for the entity table.  */
end_comment

begin_class
DECL|class|EntityRowKey
specifier|public
class|class
name|EntityRowKey
block|{
DECL|field|clusterId
specifier|private
specifier|final
name|String
name|clusterId
decl_stmt|;
DECL|field|userId
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
DECL|field|flowId
specifier|private
specifier|final
name|String
name|flowId
decl_stmt|;
DECL|field|flowRunId
specifier|private
specifier|final
name|long
name|flowRunId
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|String
name|appId
decl_stmt|;
DECL|field|entityType
specifier|private
specifier|final
name|String
name|entityType
decl_stmt|;
DECL|field|entityId
specifier|private
specifier|final
name|String
name|entityId
decl_stmt|;
DECL|method|EntityRowKey (String clusterId, String userId, String flowId, long flowRunId, String appId, String entityType, String entityId)
specifier|public
name|EntityRowKey
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowId
parameter_list|,
name|long
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
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|flowId
operator|=
name|flowId
expr_stmt|;
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
name|this
operator|.
name|entityId
operator|=
name|entityId
expr_stmt|;
block|}
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
DECL|method|getUserId ()
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
DECL|method|getFlowId ()
specifier|public
name|String
name|getFlowId
parameter_list|()
block|{
return|return
name|flowId
return|;
block|}
DECL|method|getFlowRunId ()
specifier|public
name|long
name|getFlowRunId
parameter_list|()
block|{
return|return
name|flowRunId
return|;
block|}
DECL|method|getAppId ()
specifier|public
name|String
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|getEntityType ()
specifier|public
name|String
name|getEntityType
parameter_list|()
block|{
return|return
name|entityType
return|;
block|}
DECL|method|getEntityId ()
specifier|public
name|String
name|getEntityId
parameter_list|()
block|{
return|return
name|entityId
return|;
block|}
comment|/**    * Constructs a row key prefix for the entity table as follows:    * {@code userName!clusterId!flowId!flowRunId!AppId}    *    * @param clusterId    * @param userId    * @param flowId    * @param flowRunId    * @param appId    * @return byte array with the row key prefix    */
DECL|method|getRowKeyPrefix (String clusterId, String userId, String flowId, Long flowRunId, String appId)
specifier|public
specifier|static
name|byte
index|[]
name|getRowKeyPrefix
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
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
block|{
name|byte
index|[]
name|first
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowId
argument_list|)
argument_list|)
decl_stmt|;
comment|// Note that flowRunId is a long, so we can't encode them all at the same
comment|// time.
name|byte
index|[]
name|second
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|TimelineWriterUtils
operator|.
name|invert
argument_list|(
name|flowRunId
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|third
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|appId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|third
argument_list|)
return|;
block|}
comment|/**    * Constructs a row key prefix for the entity table as follows:    * {@code userName!clusterId!flowId!flowRunId!AppId!entityType!}    *    * @param clusterId    * @param userId    * @param flowId    * @param flowRunId    * @param appId    * @param entityType    * @return byte array with the row key prefix    */
DECL|method|getRowKeyPrefix (String clusterId, String userId, String flowId, Long flowRunId, String appId, String entityType)
specifier|public
specifier|static
name|byte
index|[]
name|getRowKeyPrefix
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
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
parameter_list|)
block|{
name|byte
index|[]
name|first
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowId
argument_list|)
argument_list|)
decl_stmt|;
comment|// Note that flowRunId is a long, so we can't encode them all at the same
comment|// time.
name|byte
index|[]
name|second
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|TimelineWriterUtils
operator|.
name|invert
argument_list|(
name|flowRunId
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|third
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|appId
argument_list|,
name|entityType
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|third
argument_list|)
return|;
block|}
comment|/**    * Constructs a row key for the entity table as follows:    * {@code userName!clusterId!flowId!flowRunId!AppId!entityType!entityId}    *    * @param clusterId    * @param userId    * @param flowId    * @param flowRunId    * @param appId    * @param entityType    * @param entityId    * @return byte array with the row key    */
DECL|method|getRowKey (String clusterId, String userId, String flowId, Long flowRunId, String appId, String entityType, String entityId)
specifier|public
specifier|static
name|byte
index|[]
name|getRowKey
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
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
parameter_list|)
block|{
name|byte
index|[]
name|first
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowId
argument_list|)
argument_list|)
decl_stmt|;
comment|// Note that flowRunId is a long, so we can't encode them all at the same
comment|// time.
name|byte
index|[]
name|second
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|TimelineWriterUtils
operator|.
name|invert
argument_list|(
name|flowRunId
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|third
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|appId
argument_list|,
name|entityType
argument_list|,
name|entityId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|first
argument_list|,
name|second
argument_list|,
name|third
argument_list|)
return|;
block|}
comment|/**    * Given the raw row key as bytes, returns the row key as an object.    */
DECL|method|parseRowKey (byte[] rowKey)
specifier|public
specifier|static
name|EntityRowKey
name|parseRowKey
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|rowKeyComponents
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|rowKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowKeyComponents
operator|.
name|length
operator|<
literal|7
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the row key is not valid for "
operator|+
literal|"an entity"
argument_list|)
throw|;
block|}
name|String
name|userId
init|=
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
name|rowKeyComponents
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|clusterId
init|=
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
name|rowKeyComponents
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|flowId
init|=
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
name|rowKeyComponents
index|[
literal|2
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|flowRunId
init|=
name|TimelineWriterUtils
operator|.
name|invert
argument_list|(
name|Bytes
operator|.
name|toLong
argument_list|(
name|rowKeyComponents
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|appId
init|=
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
name|rowKeyComponents
index|[
literal|4
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|entityType
init|=
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
name|rowKeyComponents
index|[
literal|5
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|entityId
init|=
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
name|rowKeyComponents
index|[
literal|6
index|]
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EntityRowKey
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
argument_list|,
name|entityType
argument_list|,
name|entityId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

