begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline.security
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
name|timeline
operator|.
name|security
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|AbstractDelegationTokenSecretManager
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
name|service
operator|.
name|AbstractService
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
name|conf
operator|.
name|YarnConfiguration
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
name|security
operator|.
name|client
operator|.
name|TimelineDelegationTokenIdentifier
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of delegation token manager service for different  * versions of timeline service.  */
end_comment

begin_class
DECL|class|TimelineDelgationTokenSecretManagerService
specifier|public
specifier|abstract
class|class
name|TimelineDelgationTokenSecretManagerService
extends|extends
name|AbstractService
block|{
DECL|method|TimelineDelgationTokenSecretManagerService (String name)
specifier|public
name|TimelineDelgationTokenSecretManagerService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|field|delegationTokenRemovalScanInterval
specifier|private
specifier|static
name|long
name|delegationTokenRemovalScanInterval
init|=
literal|3600000L
decl_stmt|;
specifier|private
name|AbstractDelegationTokenSecretManager
DECL|field|secretManager
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|secretManager
init|=
literal|null
decl_stmt|;
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
name|long
name|secretKeyInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_DELEGATION_KEY_UPDATE_INTERVAL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_DELEGATION_KEY_UPDATE_INTERVAL
argument_list|)
decl_stmt|;
name|long
name|tokenMaxLifetime
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_DELEGATION_TOKEN_MAX_LIFETIME
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_DELEGATION_TOKEN_MAX_LIFETIME
argument_list|)
decl_stmt|;
name|long
name|tokenRenewInterval
init|=
name|conf
operator|.
name|getLong
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_DELEGATION_TOKEN_RENEW_INTERVAL
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_TIMELINE_DELEGATION_TOKEN_RENEW_INTERVAL
argument_list|)
decl_stmt|;
name|secretManager
operator|=
name|createTimelineDelegationTokenSecretManager
argument_list|(
name|secretKeyInterval
argument_list|,
name|tokenMaxLifetime
argument_list|,
name|tokenRenewInterval
argument_list|,
name|delegationTokenRemovalScanInterval
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|AbstractDelegationTokenSecretManager
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
DECL|method|createTimelineDelegationTokenSecretManager (long secretKeyInterval, long tokenMaxLifetime, long tokenRenewInterval, long tokenRemovalScanInterval)
name|createTimelineDelegationTokenSecretManager
parameter_list|(
name|long
name|secretKeyInterval
parameter_list|,
name|long
name|tokenMaxLifetime
parameter_list|,
name|long
name|tokenRenewInterval
parameter_list|,
name|long
name|tokenRemovalScanInterval
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|secretManager
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|secretManager
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AbstractDelegationTokenSecretManager
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
DECL|method|getTimelineDelegationTokenSecretManager ()
name|getTimelineDelegationTokenSecretManager
parameter_list|()
block|{
return|return
name|secretManager
return|;
block|}
block|}
end_class

end_unit

