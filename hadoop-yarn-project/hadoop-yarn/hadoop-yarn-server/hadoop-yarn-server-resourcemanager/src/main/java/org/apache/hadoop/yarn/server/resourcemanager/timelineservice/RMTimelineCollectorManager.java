begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.timelineservice
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
name|timelineservice
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
name|InterfaceAudience
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|resourcemanager
operator|.
name|RMContext
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollector
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
name|timelineservice
operator|.
name|collector
operator|.
name|TimelineCollectorManager
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
comment|/**  * This class extends TimelineCollectorManager to provide RM specific  * implementations.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RMTimelineCollectorManager
specifier|public
class|class
name|RMTimelineCollectorManager
extends|extends
name|TimelineCollectorManager
block|{
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|RMTimelineCollectorManager (RMContext rmContext)
specifier|public
name|RMTimelineCollectorManager
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|super
argument_list|(
name|RMTimelineCollectorManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postPut (ApplicationId appId, TimelineCollector collector)
specifier|public
name|void
name|postPut
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|TimelineCollector
name|collector
parameter_list|)
block|{
name|RMApp
name|app
init|=
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Unable to get the timeline collector context info for a "
operator|+
literal|"non-existing app "
operator|+
name|appId
argument_list|)
throw|;
block|}
name|String
name|userId
init|=
name|app
operator|.
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|!=
literal|null
operator|&&
operator|!
name|userId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setUserId
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|tag
range|:
name|app
operator|.
name|getApplicationTags
argument_list|()
control|)
block|{
name|String
index|[]
name|parts
init|=
name|tag
operator|.
name|split
argument_list|(
literal|":"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|!=
literal|2
operator|||
name|parts
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
switch|switch
condition|(
name|parts
index|[
literal|0
index|]
operator|.
name|toUpperCase
argument_list|()
condition|)
block|{
case|case
name|TimelineUtils
operator|.
name|FLOW_NAME_TAG_PREFIX
case|:
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowName
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|TimelineUtils
operator|.
name|FLOW_VERSION_TAG_PREFIX
case|:
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowVersion
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
break|break;
case|case
name|TimelineUtils
operator|.
name|FLOW_RUN_ID_TAG_PREFIX
case|:
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
operator|.
name|setFlowRunId
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

