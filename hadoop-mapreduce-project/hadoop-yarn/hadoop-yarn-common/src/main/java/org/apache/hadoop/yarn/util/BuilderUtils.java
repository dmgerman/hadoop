begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|ApplicationAttemptId
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
name|ApplicationReport
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
name|ApplicationState
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
name|Container
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
name|ContainerState
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
name|ContainerStatus
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
name|LocalResource
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
name|LocalResourceType
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
name|LocalResourceVisibility
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
name|NodeId
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
name|Priority
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
name|Resource
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
name|ResourceRequest
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

begin_comment
comment|/**  * Builder utilities to construct various objects.  *  */
end_comment

begin_class
DECL|class|BuilderUtils
specifier|public
class|class
name|BuilderUtils
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
DECL|class|ApplicationIdComparator
specifier|public
specifier|static
class|class
name|ApplicationIdComparator
implements|implements
name|Comparator
argument_list|<
name|ApplicationId
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (ApplicationId a1, ApplicationId a2)
specifier|public
name|int
name|compare
parameter_list|(
name|ApplicationId
name|a1
parameter_list|,
name|ApplicationId
name|a2
parameter_list|)
block|{
return|return
name|a1
operator|.
name|compareTo
argument_list|(
name|a2
argument_list|)
return|;
block|}
block|}
DECL|class|ContainerIdComparator
specifier|public
specifier|static
class|class
name|ContainerIdComparator
implements|implements
name|java
operator|.
name|util
operator|.
name|Comparator
argument_list|<
name|ContainerId
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (ContainerId c1, ContainerId c2)
specifier|public
name|int
name|compare
parameter_list|(
name|ContainerId
name|c1
parameter_list|,
name|ContainerId
name|c2
parameter_list|)
block|{
return|return
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
return|;
block|}
block|}
DECL|class|ResourceRequestComparator
specifier|public
specifier|static
class|class
name|ResourceRequestComparator
implements|implements
name|java
operator|.
name|util
operator|.
name|Comparator
argument_list|<
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
name|ResourceRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (org.apache.hadoop.yarn.api.records.ResourceRequest r1, org.apache.hadoop.yarn.api.records.ResourceRequest r2)
specifier|public
name|int
name|compare
parameter_list|(
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
name|ResourceRequest
name|r1
parameter_list|,
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
name|ResourceRequest
name|r2
parameter_list|)
block|{
comment|// Compare priority, host and capability
name|int
name|ret
init|=
name|r1
operator|.
name|getPriority
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|getPriority
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|String
name|h1
init|=
name|r1
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|String
name|h2
init|=
name|r2
operator|.
name|getHostName
argument_list|()
decl_stmt|;
name|ret
operator|=
name|h1
operator|.
name|compareTo
argument_list|(
name|h2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
name|r1
operator|.
name|getCapability
argument_list|()
operator|.
name|compareTo
argument_list|(
name|r2
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
DECL|method|newLocalResource (RecordFactory recordFactory, URI uri, LocalResourceType type, LocalResourceVisibility visibility, long size, long timestamp)
specifier|public
specifier|static
name|LocalResource
name|newLocalResource
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|URI
name|uri
parameter_list|,
name|LocalResourceType
name|type
parameter_list|,
name|LocalResourceVisibility
name|visibility
parameter_list|,
name|long
name|size
parameter_list|,
name|long
name|timestamp
parameter_list|)
block|{
name|LocalResource
name|resource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setResource
argument_list|(
name|ConverterUtils
operator|.
name|getYarnUrlFromURI
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setVisibility
argument_list|(
name|visibility
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setTimestamp
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
return|return
name|resource
return|;
block|}
DECL|method|newApplicationId (RecordFactory recordFactory, long clustertimestamp, CharSequence id)
specifier|public
specifier|static
name|ApplicationId
name|newApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|long
name|clustertimestamp
parameter_list|,
name|CharSequence
name|id
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|applicationId
operator|.
name|setClusterTimestamp
argument_list|(
name|clustertimestamp
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|newApplicationId (RecordFactory recordFactory, long clusterTimeStamp, int id)
specifier|public
specifier|static
name|ApplicationId
name|newApplicationId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|long
name|clusterTimeStamp
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|applicationId
operator|.
name|setClusterTimestamp
argument_list|(
name|clusterTimeStamp
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|newApplicationId (long clusterTimeStamp, int id)
specifier|public
specifier|static
name|ApplicationId
name|newApplicationId
parameter_list|(
name|long
name|clusterTimeStamp
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|applicationId
operator|.
name|setClusterTimestamp
argument_list|(
name|clusterTimeStamp
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|convert (long clustertimestamp, CharSequence id)
specifier|public
specifier|static
name|ApplicationId
name|convert
parameter_list|(
name|long
name|clustertimestamp
parameter_list|,
name|CharSequence
name|id
parameter_list|)
block|{
name|ApplicationId
name|applicationId
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationId
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|applicationId
operator|.
name|setClusterTimestamp
argument_list|(
name|clustertimestamp
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|newContainerId (RecordFactory recordFactory, ApplicationId appId, ApplicationAttemptId appAttemptId, int containerId)
specifier|public
specifier|static
name|ContainerId
name|newContainerId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|int
name|containerId
parameter_list|)
block|{
name|ContainerId
name|id
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|id
operator|.
name|setAppId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|newContainerId (RecordFactory recordFactory, ApplicationAttemptId appAttemptId, int containerId)
specifier|public
specifier|static
name|ContainerId
name|newContainerId
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|int
name|containerId
parameter_list|)
block|{
name|ContainerId
name|id
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ContainerId
operator|.
name|class
argument_list|)
decl_stmt|;
name|id
operator|.
name|setAppAttemptId
argument_list|(
name|appAttemptId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setAppId
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
name|id
operator|.
name|setId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|clone (Container c)
specifier|public
specifier|static
name|Container
name|clone
parameter_list|(
name|Container
name|c
parameter_list|)
block|{
name|Container
name|container
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|container
operator|.
name|setId
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setContainerToken
argument_list|(
name|c
operator|.
name|getContainerToken
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
name|c
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeHttpAddress
argument_list|(
name|c
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setResource
argument_list|(
name|c
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setState
argument_list|(
name|c
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
DECL|method|newContainer (RecordFactory recordFactory, ApplicationAttemptId appAttemptId, int containerId, NodeId nodeId, String nodeHttpAddress, Resource resource)
specifier|public
specifier|static
name|Container
name|newContainer
parameter_list|(
name|RecordFactory
name|recordFactory
parameter_list|,
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|int
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|nodeHttpAddress
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|ContainerId
name|containerID
init|=
name|newContainerId
argument_list|(
name|recordFactory
argument_list|,
name|appAttemptId
argument_list|,
name|containerId
argument_list|)
decl_stmt|;
return|return
name|newContainer
argument_list|(
name|containerID
argument_list|,
name|nodeId
argument_list|,
name|nodeHttpAddress
argument_list|,
name|resource
argument_list|)
return|;
block|}
DECL|method|newContainer (ContainerId containerId, NodeId nodeId, String nodeHttpAddress, Resource resource)
specifier|public
specifier|static
name|Container
name|newContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|nodeHttpAddress
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|Container
name|container
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|Container
operator|.
name|class
argument_list|)
decl_stmt|;
name|container
operator|.
name|setId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|container
operator|.
name|setNodeHttpAddress
argument_list|(
name|nodeHttpAddress
argument_list|)
expr_stmt|;
name|container
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|container
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|ContainerStatus
name|containerStatus
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerStatus
operator|.
name|setContainerId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|containerStatus
operator|.
name|setState
argument_list|(
name|ContainerState
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|container
operator|.
name|setContainerStatus
argument_list|(
name|containerStatus
argument_list|)
expr_stmt|;
return|return
name|container
return|;
block|}
DECL|method|newResourceRequest (Priority priority, String hostName, Resource capability, int numContainers)
specifier|public
specifier|static
name|ResourceRequest
name|newResourceRequest
parameter_list|(
name|Priority
name|priority
parameter_list|,
name|String
name|hostName
parameter_list|,
name|Resource
name|capability
parameter_list|,
name|int
name|numContainers
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|request
operator|.
name|setHostName
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|capability
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNumContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|newResourceRequest (ResourceRequest r)
specifier|public
specifier|static
name|ResourceRequest
name|newResourceRequest
parameter_list|(
name|ResourceRequest
name|r
parameter_list|)
block|{
name|ResourceRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setPriority
argument_list|(
name|r
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setHostName
argument_list|(
name|r
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setCapability
argument_list|(
name|r
operator|.
name|getCapability
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNumContainers
argument_list|(
name|r
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|newApplicationReport ( ApplicationId applicationId, String user, String queue, String name, String host, int rpcPort, String clientToken, ApplicationState state, String diagnostics, String url, long startTime)
specifier|public
specifier|static
name|ApplicationReport
name|newApplicationReport
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|queue
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|host
parameter_list|,
name|int
name|rpcPort
parameter_list|,
name|String
name|clientToken
parameter_list|,
name|ApplicationState
name|state
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|String
name|url
parameter_list|,
name|long
name|startTime
parameter_list|)
block|{
name|ApplicationReport
name|report
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ApplicationReport
operator|.
name|class
argument_list|)
decl_stmt|;
name|report
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|report
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|report
operator|.
name|setQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|report
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|report
operator|.
name|setHost
argument_list|(
name|host
argument_list|)
expr_stmt|;
name|report
operator|.
name|setRpcPort
argument_list|(
name|rpcPort
argument_list|)
expr_stmt|;
name|report
operator|.
name|setClientToken
argument_list|(
name|clientToken
argument_list|)
expr_stmt|;
name|report
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|report
operator|.
name|setDiagnostics
argument_list|(
name|diagnostics
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTrackingUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|report
operator|.
name|setStartTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
block|}
end_class

end_unit

