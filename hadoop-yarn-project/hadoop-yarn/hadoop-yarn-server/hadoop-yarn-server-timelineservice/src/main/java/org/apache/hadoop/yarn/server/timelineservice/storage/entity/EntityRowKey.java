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
DECL|field|flowName
specifier|private
specifier|final
name|String
name|flowName
decl_stmt|;
DECL|field|flowRunId
specifier|private
specifier|final
name|Long
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
DECL|method|EntityRowKey (String clusterId, String userId, String flowName, Long flowRunId, String appId, String entityType, String entityId)
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
name|flowName
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
name|flowName
operator|=
name|flowName
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
DECL|method|getFlowName ()
specifier|public
name|String
name|getFlowName
parameter_list|()
block|{
return|return
name|flowName
return|;
block|}
DECL|method|getFlowRunId ()
specifier|public
name|Long
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
comment|/**    * Constructs a row key prefix for the entity table as follows:    * {@code userName!clusterId!flowName!flowRunId!AppId}.    *    * @param clusterId Context cluster id.    * @param userId User name.    * @param flowName Flow name.    * @param flowRunId Run Id for the flow.    * @param appId Application Id.    * @return byte array with the row key prefix.    */
DECL|method|getRowKeyPrefix (String clusterId, String userId, String flowName, Long flowRunId, String appId)
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
name|flowName
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
return|return
name|EntityRowKeyConverter
operator|.
name|getInstance
argument_list|()
operator|.
name|encode
argument_list|(
operator|new
name|EntityRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Constructs a row key prefix for the entity table as follows:    * {@code userName!clusterId!flowName!flowRunId!AppId!entityType!}.    * Typically used while querying multiple entities of a particular entity    * type.    *    * @param clusterId Context cluster id.    * @param userId User name.    * @param flowName Flow name.    * @param flowRunId Run Id for the flow.    * @param appId Application Id.    * @param entityType Entity type.    * @return byte array with the row key prefix.    */
DECL|method|getRowKeyPrefix (String clusterId, String userId, String flowName, Long flowRunId, String appId, String entityType)
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
name|flowName
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
return|return
name|EntityRowKeyConverter
operator|.
name|getInstance
argument_list|()
operator|.
name|encode
argument_list|(
operator|new
name|EntityRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Constructs a row key for the entity table as follows:    * {@code userName!clusterId!flowName!flowRunId!AppId!entityType!entityId}.    * Typically used while querying a specific entity.    *    * @param clusterId Context cluster id.    * @param userId User name.    * @param flowName Flow name.    * @param flowRunId Run Id for the flow.    * @param appId Application Id.    * @param entityType Entity type.    * @param entityId Entity Id.    * @return byte array with the row key.    */
DECL|method|getRowKey (String clusterId, String userId, String flowName, Long flowRunId, String appId, String entityType, String entityId)
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
name|flowName
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
return|return
name|EntityRowKeyConverter
operator|.
name|getInstance
argument_list|()
operator|.
name|encode
argument_list|(
operator|new
name|EntityRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|entityId
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Given the raw row key as bytes, returns the row key as an object.    *    * @param rowKey byte representation of row key.    * @return An<cite>EntityRowKey</cite> object.    */
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
return|return
name|EntityRowKeyConverter
operator|.
name|getInstance
argument_list|()
operator|.
name|decode
argument_list|(
name|rowKey
argument_list|)
return|;
block|}
block|}
end_class

end_unit

