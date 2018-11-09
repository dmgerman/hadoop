begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.chillmode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|chillmode
package|;
end_package

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
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|hdds
operator|.
name|HddsConfigKeys
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerInfo
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|SCMDatanodeProtocolServer
operator|.
name|NodeRegistrationContainerReport
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
comment|/**  * Class defining Chill mode exit criteria for Containers.  */
end_comment

begin_class
DECL|class|ContainerChillModeRule
specifier|public
class|class
name|ContainerChillModeRule
implements|implements
name|ChillModeExitRule
argument_list|<
name|NodeRegistrationContainerReport
argument_list|>
block|{
comment|// Required cutoff % for containers with at least 1 reported replica.
DECL|field|chillModeCutoff
specifier|private
name|double
name|chillModeCutoff
decl_stmt|;
comment|// Containers read from scm db (excluding containers in ALLOCATED state).
DECL|field|containerMap
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerInfo
argument_list|>
name|containerMap
decl_stmt|;
DECL|field|maxContainer
specifier|private
name|double
name|maxContainer
decl_stmt|;
DECL|field|containerWithMinReplicas
specifier|private
name|AtomicLong
name|containerWithMinReplicas
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|chillModeManager
specifier|private
specifier|final
name|SCMChillModeManager
name|chillModeManager
decl_stmt|;
DECL|method|ContainerChillModeRule (Configuration conf, List<ContainerInfo> containers, SCMChillModeManager manager)
specifier|public
name|ContainerChillModeRule
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|List
argument_list|<
name|ContainerInfo
argument_list|>
name|containers
parameter_list|,
name|SCMChillModeManager
name|manager
parameter_list|)
block|{
name|chillModeCutoff
operator|=
name|conf
operator|.
name|getDouble
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_THRESHOLD_PCT_DEFAULT
argument_list|)
expr_stmt|;
name|chillModeManager
operator|=
name|manager
expr_stmt|;
name|containerMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
if|if
condition|(
name|containers
operator|!=
literal|null
condition|)
block|{
name|containers
operator|.
name|forEach
argument_list|(
name|c
lambda|->
block|{
comment|// TODO: There can be containers in OPEN state which were never
comment|// created by the client. We are not considering these containers for
comment|// now. These containers can be handled by tracking pipelines.
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|c
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|c
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|HddsProtos
operator|.
name|LifeCycleState
operator|.
name|OPEN
argument_list|)
condition|)
block|{
name|containerMap
operator|.
name|put
argument_list|(
name|c
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|maxContainer
operator|=
name|containerMap
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
if|if
condition|(
name|maxContainer
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|getCurrentContainerThreshold
argument_list|()
operator|>=
name|chillModeCutoff
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getCurrentContainerThreshold ()
specifier|public
name|double
name|getCurrentContainerThreshold
parameter_list|()
block|{
if|if
condition|(
name|maxContainer
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
operator|(
name|containerWithMinReplicas
operator|.
name|doubleValue
argument_list|()
operator|/
name|maxContainer
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|process (NodeRegistrationContainerReport reportsProto)
specifier|public
name|void
name|process
parameter_list|(
name|NodeRegistrationContainerReport
name|reportsProto
parameter_list|)
block|{
if|if
condition|(
name|maxContainer
operator|==
literal|0
condition|)
block|{
comment|// No container to check.
return|return;
block|}
name|reportsProto
operator|.
name|getReport
argument_list|()
operator|.
name|getReportsList
argument_list|()
operator|.
name|forEach
argument_list|(
name|c
lambda|->
block|{
if|if
condition|(
name|containerMap
operator|.
name|containsKey
argument_list|(
name|c
operator|.
name|getContainerID
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|containerMap
operator|.
name|remove
argument_list|(
name|c
operator|.
name|getContainerID
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|containerWithMinReplicas
operator|.
name|getAndAdd
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|chillModeManager
operator|.
name|getInChillMode
argument_list|()
condition|)
block|{
name|SCMChillModeManager
operator|.
name|getLogger
argument_list|()
operator|.
name|info
argument_list|(
literal|"SCM in chill mode. {} % containers have at least one"
operator|+
literal|" reported replica."
argument_list|,
operator|(
name|containerWithMinReplicas
operator|.
name|get
argument_list|()
operator|/
name|maxContainer
operator|)
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|containerMap
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

