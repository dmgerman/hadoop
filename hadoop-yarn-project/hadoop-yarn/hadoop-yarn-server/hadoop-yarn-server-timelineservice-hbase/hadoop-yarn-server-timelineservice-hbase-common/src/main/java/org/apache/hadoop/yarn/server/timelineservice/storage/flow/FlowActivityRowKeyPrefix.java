begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.flow
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
name|flow
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
comment|/**  * A prefix partial rowkey for flow activities.  */
end_comment

begin_class
DECL|class|FlowActivityRowKeyPrefix
specifier|public
class|class
name|FlowActivityRowKeyPrefix
extends|extends
name|FlowActivityRowKey
implements|implements
name|RowKeyPrefix
argument_list|<
name|FlowActivityRowKey
argument_list|>
block|{
comment|/**    * Constructs a row key prefix for the flow activity table as follows:    * {@code clusterId!dayTimestamp!}.    *    * @param clusterId Cluster Id.    * @param dayTs Start of the day timestamp.    */
DECL|method|FlowActivityRowKeyPrefix (String clusterId, Long dayTs)
specifier|public
name|FlowActivityRowKeyPrefix
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|Long
name|dayTs
parameter_list|)
block|{
name|super
argument_list|(
name|clusterId
argument_list|,
name|dayTs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a row key prefix for the flow activity table as follows:    * {@code clusterId!}.    *    * @param clusterId identifying the cluster    */
DECL|method|FlowActivityRowKeyPrefix (String clusterId)
specifier|public
name|FlowActivityRowKeyPrefix
parameter_list|(
name|String
name|clusterId
parameter_list|)
block|{
name|super
argument_list|(
name|clusterId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.application.    * RowKeyPrefix#getRowKeyPrefix()    */
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

