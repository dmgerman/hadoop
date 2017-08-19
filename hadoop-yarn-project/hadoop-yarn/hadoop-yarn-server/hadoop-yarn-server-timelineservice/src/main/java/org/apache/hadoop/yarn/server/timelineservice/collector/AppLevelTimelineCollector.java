begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|collector
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
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
name|security
operator|.
name|UserGroupInformation
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
name|Token
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Service that handles writes to the timeline service and writes them to the  * backing storage for a given YARN application.  *  * App-related lifecycle management is handled by this service.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppLevelTimelineCollector
specifier|public
class|class
name|AppLevelTimelineCollector
extends|extends
name|TimelineCollector
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TimelineCollector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|appUser
specifier|private
specifier|final
name|String
name|appUser
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|TimelineCollectorContext
name|context
decl_stmt|;
DECL|field|currentUser
specifier|private
name|UserGroupInformation
name|currentUser
decl_stmt|;
DECL|field|delegationTokenForApp
specifier|private
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|delegationTokenForApp
decl_stmt|;
DECL|field|tokenMaxDate
specifier|private
name|long
name|tokenMaxDate
init|=
literal|0
decl_stmt|;
DECL|field|tokenRenewer
specifier|private
name|String
name|tokenRenewer
decl_stmt|;
DECL|field|renewalOrRegenerationFuture
specifier|private
name|Future
argument_list|<
name|?
argument_list|>
name|renewalOrRegenerationFuture
decl_stmt|;
DECL|method|AppLevelTimelineCollector (ApplicationId appId)
specifier|public
name|AppLevelTimelineCollector
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|this
argument_list|(
name|appId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|AppLevelTimelineCollector (ApplicationId appId, String user)
specifier|public
name|AppLevelTimelineCollector
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|super
argument_list|(
name|AppLevelTimelineCollector
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" - "
operator|+
name|appId
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|appId
argument_list|,
literal|"AppId shouldn't be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|appUser
operator|=
name|user
expr_stmt|;
name|context
operator|=
operator|new
name|TimelineCollectorContext
argument_list|()
expr_stmt|;
block|}
DECL|method|getCurrentUser ()
specifier|public
name|UserGroupInformation
name|getCurrentUser
parameter_list|()
block|{
return|return
name|currentUser
return|;
block|}
DECL|method|getAppUser ()
specifier|public
name|String
name|getAppUser
parameter_list|()
block|{
return|return
name|appUser
return|;
block|}
DECL|method|setDelegationTokenAndFutureForApp ( Token<TimelineDelegationTokenIdentifier> token, Future<?> appRenewalOrRegenerationFuture, long tknMaxDate, String renewer)
name|void
name|setDelegationTokenAndFutureForApp
parameter_list|(
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|token
parameter_list|,
name|Future
argument_list|<
name|?
argument_list|>
name|appRenewalOrRegenerationFuture
parameter_list|,
name|long
name|tknMaxDate
parameter_list|,
name|String
name|renewer
parameter_list|)
block|{
name|this
operator|.
name|delegationTokenForApp
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|tokenMaxDate
operator|=
name|tknMaxDate
expr_stmt|;
name|this
operator|.
name|tokenRenewer
operator|=
name|renewer
expr_stmt|;
name|this
operator|.
name|renewalOrRegenerationFuture
operator|=
name|appRenewalOrRegenerationFuture
expr_stmt|;
block|}
DECL|method|setRenewalOrRegenerationFutureForApp ( Future<?> appRenewalOrRegenerationFuture)
name|void
name|setRenewalOrRegenerationFutureForApp
parameter_list|(
name|Future
argument_list|<
name|?
argument_list|>
name|appRenewalOrRegenerationFuture
parameter_list|)
block|{
name|this
operator|.
name|renewalOrRegenerationFuture
operator|=
name|appRenewalOrRegenerationFuture
expr_stmt|;
block|}
DECL|method|cancelRenewalOrRegenerationFutureForApp ()
name|void
name|cancelRenewalOrRegenerationFutureForApp
parameter_list|()
block|{
if|if
condition|(
name|renewalOrRegenerationFuture
operator|!=
literal|null
operator|&&
operator|!
name|renewalOrRegenerationFuture
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|renewalOrRegenerationFuture
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getAppDelegationTokenMaxDate ()
name|long
name|getAppDelegationTokenMaxDate
parameter_list|()
block|{
return|return
name|tokenMaxDate
return|;
block|}
DECL|method|getAppDelegationTokenRenewer ()
name|String
name|getAppDelegationTokenRenewer
parameter_list|()
block|{
return|return
name|tokenRenewer
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDelegationTokenForApp ()
specifier|public
name|Token
argument_list|<
name|TimelineDelegationTokenIdentifier
argument_list|>
name|getDelegationTokenForApp
parameter_list|()
block|{
return|return
name|this
operator|.
name|delegationTokenForApp
return|;
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
name|context
operator|.
name|setClusterId
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_CLUSTER_ID
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the default values, which will be updated with an RPC call to get the
comment|// context info from NM.
comment|// Current user usually is not the app user, but keep this field non-null
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|context
operator|.
name|setUserId
argument_list|(
name|currentUser
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setAppId
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
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
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
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
name|cancelRenewalOrRegenerationFutureForApp
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimelineEntityContext ()
specifier|public
name|TimelineCollectorContext
name|getTimelineEntityContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
block|}
end_class

end_unit

