begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|resourcemanager
operator|.
name|recovery
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|ApplicationAttemptStateDataPBImpl
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
name|ApplicationStateDataPBImpl
import|;
end_import

begin_class
annotation|@
name|Unstable
DECL|class|NullRMStateStore
specifier|public
class|class
name|NullRMStateStore
extends|extends
name|RMStateStore
block|{
annotation|@
name|Override
DECL|method|initInternal (Configuration conf)
specifier|protected
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|closeInternal ()
specifier|protected
name|void
name|closeInternal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|loadState ()
specifier|public
name|RMState
name|loadState
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot load state from null store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeApplicationState (String appId, ApplicationStateDataPBImpl appStateData)
specifier|protected
name|void
name|storeApplicationState
parameter_list|(
name|String
name|appId
parameter_list|,
name|ApplicationStateDataPBImpl
name|appStateData
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeApplicationAttemptState (String attemptId, ApplicationAttemptStateDataPBImpl attemptStateData)
specifier|protected
name|void
name|storeApplicationAttemptState
parameter_list|(
name|String
name|attemptId
parameter_list|,
name|ApplicationAttemptStateDataPBImpl
name|attemptStateData
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeApplicationState (ApplicationState appState)
specifier|protected
name|void
name|removeApplicationState
parameter_list|(
name|ApplicationState
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

