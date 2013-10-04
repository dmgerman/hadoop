begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
name|nodemanager
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
name|server
operator|.
name|nodemanager
operator|.
name|ContainerManagerEvent
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
name|nodemanager
operator|.
name|ContainerManagerEventType
import|;
end_import

begin_class
DECL|class|CMgrCompletedAppsEvent
specifier|public
class|class
name|CMgrCompletedAppsEvent
extends|extends
name|ContainerManagerEvent
block|{
DECL|field|appsToCleanup
specifier|private
specifier|final
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|appsToCleanup
decl_stmt|;
DECL|field|reason
specifier|private
specifier|final
name|Reason
name|reason
decl_stmt|;
DECL|method|CMgrCompletedAppsEvent (List<ApplicationId> appsToCleanup, Reason reason)
specifier|public
name|CMgrCompletedAppsEvent
parameter_list|(
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|appsToCleanup
parameter_list|,
name|Reason
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|ContainerManagerEventType
operator|.
name|FINISH_APPS
argument_list|)
expr_stmt|;
name|this
operator|.
name|appsToCleanup
operator|=
name|appsToCleanup
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
block|}
DECL|method|getAppsToCleanup ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getAppsToCleanup
parameter_list|()
block|{
return|return
name|this
operator|.
name|appsToCleanup
return|;
block|}
DECL|method|getReason ()
specifier|public
name|Reason
name|getReason
parameter_list|()
block|{
return|return
name|reason
return|;
block|}
DECL|enum|Reason
specifier|public
specifier|static
enum|enum
name|Reason
block|{
comment|/**      * Application is killed as NodeManager is shut down      */
DECL|enumConstant|ON_SHUTDOWN
name|ON_SHUTDOWN
block|,
comment|/**      * Application is killed by ResourceManager      */
DECL|enumConstant|BY_RESOURCEMANAGER
name|BY_RESOURCEMANAGER
block|}
block|}
end_class

end_unit

