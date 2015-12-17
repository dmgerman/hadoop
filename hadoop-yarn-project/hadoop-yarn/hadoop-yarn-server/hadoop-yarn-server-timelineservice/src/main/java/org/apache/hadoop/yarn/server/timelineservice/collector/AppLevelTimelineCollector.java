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
name|util
operator|.
name|timeline
operator|.
name|TimelineUtils
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
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|TimelineCollectorContext
name|context
decl_stmt|;
DECL|method|AppLevelTimelineCollector (ApplicationId appId)
specifier|public
name|AppLevelTimelineCollector
parameter_list|(
name|ApplicationId
name|appId
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
name|context
operator|=
operator|new
name|TimelineCollectorContext
argument_list|()
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
name|context
operator|.
name|setUserId
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Use app ID to generate a default flow name for orphan app
name|context
operator|.
name|setFlowName
argument_list|(
name|TimelineUtils
operator|.
name|generateDefaultFlowNameBasedOnAppId
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the flow version to string 1 if it's an orphan app
name|context
operator|.
name|setFlowVersion
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
comment|// Set the flow run ID to 1 if it's an orphan app
name|context
operator|.
name|setFlowRunId
argument_list|(
literal|1L
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

