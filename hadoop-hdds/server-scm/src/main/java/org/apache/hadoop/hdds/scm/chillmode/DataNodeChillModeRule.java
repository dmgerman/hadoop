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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|server
operator|.
name|events
operator|.
name|EventHandler
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
name|server
operator|.
name|events
operator|.
name|EventPublisher
import|;
end_import

begin_comment
comment|/**  * Class defining Chill mode exit criteria according to number of DataNodes  * registered with SCM.  */
end_comment

begin_class
DECL|class|DataNodeChillModeRule
specifier|public
class|class
name|DataNodeChillModeRule
implements|implements
name|ChillModeExitRule
argument_list|<
name|NodeRegistrationContainerReport
argument_list|>
implements|,
name|EventHandler
argument_list|<
name|NodeRegistrationContainerReport
argument_list|>
block|{
comment|// Min DataNodes required to exit chill mode.
DECL|field|requiredDns
specifier|private
name|int
name|requiredDns
decl_stmt|;
DECL|field|registeredDns
specifier|private
name|int
name|registeredDns
init|=
literal|0
decl_stmt|;
comment|// Set to track registered DataNodes.
DECL|field|registeredDnSet
specifier|private
name|HashSet
argument_list|<
name|UUID
argument_list|>
name|registeredDnSet
decl_stmt|;
DECL|field|chillModeManager
specifier|private
specifier|final
name|SCMChillModeManager
name|chillModeManager
decl_stmt|;
DECL|method|DataNodeChillModeRule (Configuration conf, SCMChillModeManager manager)
specifier|public
name|DataNodeChillModeRule
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|SCMChillModeManager
name|manager
parameter_list|)
block|{
name|requiredDns
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_MIN_DATANODE
argument_list|,
name|HddsConfigKeys
operator|.
name|HDDS_SCM_CHILLMODE_MIN_DATANODE_DEFAULT
argument_list|)
expr_stmt|;
name|registeredDnSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|requiredDns
operator|*
literal|2
argument_list|)
expr_stmt|;
name|chillModeManager
operator|=
name|manager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validate ()
specifier|public
name|boolean
name|validate
parameter_list|()
block|{
return|return
name|registeredDns
operator|>=
name|requiredDns
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRegisteredDataNodes ()
specifier|public
name|double
name|getRegisteredDataNodes
parameter_list|()
block|{
return|return
name|registeredDns
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
name|registeredDnSet
operator|.
name|add
argument_list|(
name|reportsProto
operator|.
name|getDatanodeDetails
argument_list|()
operator|.
name|getUuid
argument_list|()
argument_list|)
expr_stmt|;
name|registeredDns
operator|=
name|registeredDnSet
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (NodeRegistrationContainerReport nodeRegistrationContainerReport, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|NodeRegistrationContainerReport
name|nodeRegistrationContainerReport
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
comment|// TODO: when we have remove handlers, we can remove getInChillmode check
if|if
condition|(
name|chillModeManager
operator|.
name|getInChillMode
argument_list|()
condition|)
block|{
if|if
condition|(
name|validate
argument_list|()
condition|)
block|{
return|return;
block|}
name|process
argument_list|(
name|nodeRegistrationContainerReport
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
literal|"SCM in chill mode. {} DataNodes registered, {} required."
argument_list|,
name|registeredDns
argument_list|,
name|requiredDns
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validate
argument_list|()
condition|)
block|{
name|chillModeManager
operator|.
name|validateChillModeExitRules
argument_list|(
name|publisher
argument_list|)
expr_stmt|;
block|}
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
name|registeredDnSet
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

