begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.utils
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
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|Collection
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
name|ContainerId
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
name|impl
operator|.
name|pb
operator|.
name|ProtoUtils
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|proto
operator|.
name|YarnServerCommonServiceProtos
operator|.
name|SystemCredentialsForAppsProto
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
name|api
operator|.
name|protocolrecords
operator|.
name|NodeHeartbeatResponse
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
name|api
operator|.
name|records
operator|.
name|MasterKey
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
name|api
operator|.
name|records
operator|.
name|NodeAction
import|;
end_import

begin_comment
comment|/**  * Server Builder utilities to construct various objects.  *  */
end_comment

begin_class
DECL|class|YarnServerBuilderUtils
specifier|public
class|class
name|YarnServerBuilderUtils
block|{
DECL|field|recordFactory
specifier|private
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|method|newNodeHeartbeatResponse ( NodeAction action, String diagnosticsMessage)
specifier|public
specifier|static
name|NodeHeartbeatResponse
name|newNodeHeartbeatResponse
parameter_list|(
name|NodeAction
name|action
parameter_list|,
name|String
name|diagnosticsMessage
parameter_list|)
block|{
name|NodeHeartbeatResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNodeAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDiagnosticsMessage
argument_list|(
name|diagnosticsMessage
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|newNodeHeartbeatResponse (int responseId, NodeAction action, List<ContainerId> containersToCleanUp, List<ApplicationId> applicationsToCleanUp, MasterKey containerTokenMasterKey, MasterKey nmTokenMasterKey, long nextHeartbeatInterval)
specifier|public
specifier|static
name|NodeHeartbeatResponse
name|newNodeHeartbeatResponse
parameter_list|(
name|int
name|responseId
parameter_list|,
name|NodeAction
name|action
parameter_list|,
name|List
argument_list|<
name|ContainerId
argument_list|>
name|containersToCleanUp
parameter_list|,
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|applicationsToCleanUp
parameter_list|,
name|MasterKey
name|containerTokenMasterKey
parameter_list|,
name|MasterKey
name|nmTokenMasterKey
parameter_list|,
name|long
name|nextHeartbeatInterval
parameter_list|)
block|{
name|NodeHeartbeatResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|NodeHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setResponseId
argument_list|(
name|responseId
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNodeAction
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContainerTokenMasterKey
argument_list|(
name|containerTokenMasterKey
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNMTokenMasterKey
argument_list|(
name|nmTokenMasterKey
argument_list|)
expr_stmt|;
name|response
operator|.
name|setNextHeartBeatInterval
argument_list|(
name|nextHeartbeatInterval
argument_list|)
expr_stmt|;
if|if
condition|(
name|containersToCleanUp
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|addAllContainersToCleanup
argument_list|(
name|containersToCleanUp
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|applicationsToCleanUp
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|addAllApplicationsToCleanup
argument_list|(
name|applicationsToCleanUp
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Build SystemCredentialsForAppsProto objects.    *    * @param applicationId Application ID    * @param credentials HDFS Tokens    * @return systemCredentialsForAppsProto SystemCredentialsForAppsProto    */
DECL|method|newSystemCredentialsForAppsProto ( ApplicationId applicationId, ByteBuffer credentials)
specifier|public
specifier|static
name|SystemCredentialsForAppsProto
name|newSystemCredentialsForAppsProto
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|ByteBuffer
name|credentials
parameter_list|)
block|{
name|SystemCredentialsForAppsProto
name|systemCredentialsForAppsProto
init|=
name|SystemCredentialsForAppsProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAppId
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|applicationId
argument_list|)
argument_list|)
operator|.
name|setCredentialsForApp
argument_list|(
name|ProtoUtils
operator|.
name|BYTE_STRING_INTERNER
operator|.
name|intern
argument_list|(
name|ProtoUtils
operator|.
name|convertToProtoFormat
argument_list|(
name|credentials
operator|.
name|duplicate
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|systemCredentialsForAppsProto
return|;
block|}
comment|/**    * Convert Collection of SystemCredentialsForAppsProto proto objects to a Map    * of ApplicationId to ByteBuffer.    *    * @param systemCredentials List of SystemCredentialsForAppsProto proto    *          objects    * @return systemCredentialsForApps Map of Application Id to ByteBuffer    */
DECL|method|convertFromProtoFormat ( Collection<SystemCredentialsForAppsProto> systemCredentials)
specifier|public
specifier|static
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|convertFromProtoFormat
parameter_list|(
name|Collection
argument_list|<
name|SystemCredentialsForAppsProto
argument_list|>
name|systemCredentials
parameter_list|)
block|{
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentialsForApps
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|(
name|systemCredentials
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SystemCredentialsForAppsProto
name|proto
range|:
name|systemCredentials
control|)
block|{
name|systemCredentialsForApps
operator|.
name|put
argument_list|(
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|proto
operator|.
name|getAppId
argument_list|()
argument_list|)
argument_list|,
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|proto
operator|.
name|getCredentialsForApp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|systemCredentialsForApps
return|;
block|}
comment|/**    * Convert Map of Application Id to ByteBuffer to Collection of    * SystemCredentialsForAppsProto proto objects.    *    * @param systemCredentialsForApps Map of Application Id to ByteBuffer    * @return systemCredentials List of SystemCredentialsForAppsProto proto    *         objects    */
DECL|method|convertToProtoFormat ( Map<ApplicationId, ByteBuffer> systemCredentialsForApps)
specifier|public
specifier|static
name|List
argument_list|<
name|SystemCredentialsForAppsProto
argument_list|>
name|convertToProtoFormat
parameter_list|(
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|systemCredentialsForApps
parameter_list|)
block|{
name|List
argument_list|<
name|SystemCredentialsForAppsProto
argument_list|>
name|systemCredentials
init|=
operator|new
name|ArrayList
argument_list|<
name|SystemCredentialsForAppsProto
argument_list|>
argument_list|(
name|systemCredentialsForApps
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|ByteBuffer
argument_list|>
name|entry
range|:
name|systemCredentialsForApps
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SystemCredentialsForAppsProto
name|proto
init|=
name|newSystemCredentialsForAppsProto
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|systemCredentials
operator|.
name|add
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
return|return
name|systemCredentials
return|;
block|}
block|}
end_class

end_unit

