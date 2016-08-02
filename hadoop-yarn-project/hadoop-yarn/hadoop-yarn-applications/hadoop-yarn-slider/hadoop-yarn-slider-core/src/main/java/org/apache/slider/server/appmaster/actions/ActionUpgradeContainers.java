begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.actions
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
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
name|List
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|CollectionUtils
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
name|FinalApplicationStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|SliderAppMaster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|AppState
import|;
end_import

begin_class
DECL|class|ActionUpgradeContainers
specifier|public
class|class
name|ActionUpgradeContainers
extends|extends
name|AsyncAction
block|{
DECL|field|exitCode
specifier|private
name|int
name|exitCode
decl_stmt|;
DECL|field|finalApplicationStatus
specifier|private
name|FinalApplicationStatus
name|finalApplicationStatus
decl_stmt|;
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|field|containers
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|containers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|components
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|components
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ActionUpgradeContainers (String name, long delay, TimeUnit timeUnit, int exitCode, FinalApplicationStatus finalApplicationStatus, List<String> containers, List<String> components, String message)
specifier|public
name|ActionUpgradeContainers
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|,
name|int
name|exitCode
parameter_list|,
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|containers
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|components
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|delay
argument_list|,
name|timeUnit
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
name|this
operator|.
name|finalApplicationStatus
operator|=
name|finalApplicationStatus
expr_stmt|;
name|this
operator|.
name|containers
operator|.
name|addAll
argument_list|(
name|containers
argument_list|)
expr_stmt|;
name|this
operator|.
name|components
operator|.
name|addAll
argument_list|(
name|components
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute (SliderAppMaster appMaster, QueueAccess queueService, AppState appState)
specifier|public
name|void
name|execute
parameter_list|(
name|SliderAppMaster
name|appMaster
parameter_list|,
name|QueueAccess
name|queueService
parameter_list|,
name|AppState
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|CollectionUtils
operator|.
name|isNotEmpty
argument_list|(
name|this
operator|.
name|containers
argument_list|)
operator|||
name|CollectionUtils
operator|.
name|isNotEmpty
argument_list|(
name|this
operator|.
name|components
argument_list|)
condition|)
block|{
name|SliderAppMaster
operator|.
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"SliderAppMaster.upgradeContainers: {}"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|appMaster
operator|.
name|onUpgradeContainers
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
DECL|method|setExitCode (int exitCode)
specifier|public
name|void
name|setExitCode
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
DECL|method|getFinalApplicationStatus ()
specifier|public
name|FinalApplicationStatus
name|getFinalApplicationStatus
parameter_list|()
block|{
return|return
name|finalApplicationStatus
return|;
block|}
DECL|method|setFinalApplicationStatus ( FinalApplicationStatus finalApplicationStatus)
specifier|public
name|void
name|setFinalApplicationStatus
parameter_list|(
name|FinalApplicationStatus
name|finalApplicationStatus
parameter_list|)
block|{
name|this
operator|.
name|finalApplicationStatus
operator|=
name|finalApplicationStatus
expr_stmt|;
block|}
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
DECL|method|setMessage (String message)
specifier|public
name|void
name|setMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|getContainers ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getContainers
parameter_list|()
block|{
return|return
name|containers
return|;
block|}
DECL|method|setContainers (Set<String> containers)
specifier|public
name|void
name|setContainers
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|containers
parameter_list|)
block|{
name|this
operator|.
name|containers
operator|=
name|containers
expr_stmt|;
block|}
DECL|method|getComponents ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getComponents
parameter_list|()
block|{
return|return
name|components
return|;
block|}
DECL|method|setComponents (Set<String> components)
specifier|public
name|void
name|setComponents
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|components
parameter_list|)
block|{
name|this
operator|.
name|components
operator|=
name|components
expr_stmt|;
block|}
block|}
end_class

end_unit

