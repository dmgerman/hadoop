begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.subapplication
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
name|subapplication
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
name|RowKeyPrefix
import|;
end_import

begin_comment
comment|/**  * Represents a partial rowkey without the entityId or without entityType and  * entityId for the sub application table.  *  */
end_comment

begin_class
DECL|class|SubApplicationRowKeyPrefix
specifier|public
class|class
name|SubApplicationRowKeyPrefix
extends|extends
name|SubApplicationRowKey
implements|implements
name|RowKeyPrefix
argument_list|<
name|SubApplicationRowKey
argument_list|>
block|{
comment|/**    * Creates a prefix which generates the following rowKeyPrefixes for the sub    * application table:    * {@code subAppUserId!clusterId!entityType!entityPrefix!userId}.    *    * @param subAppUserId    *          identifying the subApp User    * @param clusterId    *          identifying the cluster    * @param entityType    *          which entity type    * @param entityIdPrefix    *          for entityId    * @param entityId    *          for an entity    * @param userId    *          for the user who runs the AM    *    * subAppUserId is usually the doAsUser.    * userId is the yarn user that the AM runs as.    *    */
DECL|method|SubApplicationRowKeyPrefix (String subAppUserId, String clusterId, String entityType, Long entityIdPrefix, String entityId, String userId)
specifier|public
name|SubApplicationRowKeyPrefix
parameter_list|(
name|String
name|subAppUserId
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|Long
name|entityIdPrefix
parameter_list|,
name|String
name|entityId
parameter_list|,
name|String
name|userId
parameter_list|)
block|{
name|super
argument_list|(
name|subAppUserId
argument_list|,
name|clusterId
argument_list|,
name|entityType
argument_list|,
name|entityIdPrefix
argument_list|,
name|entityId
argument_list|,
name|userId
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.hadoop.yarn.server.timelineservice.storage.subapplication.    * RowKeyPrefix#getRowKeyPrefix()    */
DECL|method|getRowKeyPrefix ()
specifier|public
name|byte
index|[]
name|getRowKeyPrefix
parameter_list|()
block|{
return|return
name|super
operator|.
name|getRowKey
argument_list|()
return|;
block|}
block|}
end_class

end_unit

