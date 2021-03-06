begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
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
name|sharedcachemanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|YarnApplicationState
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|exceptions
operator|.
name|ApplicationNotFoundException
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * An implementation of AppChecker that queries the resource manager remotely to  * determine whether the app is running.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RemoteAppChecker
specifier|public
class|class
name|RemoteAppChecker
extends|extends
name|AppChecker
block|{
DECL|field|ACTIVE_STATES
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|YarnApplicationState
argument_list|>
name|ACTIVE_STATES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|YarnApplicationState
operator|.
name|NEW
argument_list|,
name|YarnApplicationState
operator|.
name|ACCEPTED
argument_list|,
name|YarnApplicationState
operator|.
name|NEW_SAVING
argument_list|,
name|YarnApplicationState
operator|.
name|SUBMITTED
argument_list|,
name|YarnApplicationState
operator|.
name|RUNNING
argument_list|)
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|YarnClient
name|client
decl_stmt|;
DECL|method|RemoteAppChecker ()
specifier|public
name|RemoteAppChecker
parameter_list|()
block|{
name|this
argument_list|(
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RemoteAppChecker (YarnClient client)
name|RemoteAppChecker
parameter_list|(
name|YarnClient
name|client
parameter_list|)
block|{
name|super
argument_list|(
literal|"RemoteAppChecker"
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|addService
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Private
DECL|method|isApplicationActive (ApplicationId id)
specifier|public
name|boolean
name|isApplicationActive
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|YarnException
block|{
name|ApplicationReport
name|report
init|=
literal|null
decl_stmt|;
try|try
block|{
name|report
operator|=
name|client
operator|.
name|getApplicationReport
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ApplicationNotFoundException
name|e
parameter_list|)
block|{
comment|// the app does not exist
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|report
operator|==
literal|null
condition|)
block|{
comment|// the app does not exist
return|return
literal|false
return|;
block|}
return|return
name|ACTIVE_STATES
operator|.
name|contains
argument_list|(
name|report
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Private
DECL|method|getActiveApplications ()
specifier|public
name|Collection
argument_list|<
name|ApplicationId
argument_list|>
name|getActiveApplications
parameter_list|()
throws|throws
name|YarnException
block|{
try|try
block|{
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|activeApps
init|=
operator|new
name|ArrayList
argument_list|<
name|ApplicationId
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|apps
init|=
name|client
operator|.
name|getApplications
argument_list|(
name|ACTIVE_STATES
argument_list|)
decl_stmt|;
for|for
control|(
name|ApplicationReport
name|app
range|:
name|apps
control|)
block|{
name|activeApps
operator|.
name|add
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|activeApps
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

