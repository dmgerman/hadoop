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

begin_class
DECL|class|TimelineCollectorContext
specifier|public
class|class
name|TimelineCollectorContext
block|{
DECL|field|clusterId
specifier|private
name|String
name|clusterId
decl_stmt|;
DECL|field|userId
specifier|private
name|String
name|userId
decl_stmt|;
DECL|field|flowName
specifier|private
name|String
name|flowName
decl_stmt|;
DECL|field|flowVersion
specifier|private
name|String
name|flowVersion
decl_stmt|;
DECL|field|flowRunId
specifier|private
name|long
name|flowRunId
decl_stmt|;
DECL|field|appId
specifier|private
name|String
name|appId
decl_stmt|;
DECL|method|TimelineCollectorContext ()
specifier|public
name|TimelineCollectorContext
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0L
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TimelineCollectorContext (String clusterId, String userId, String flowName, String flowVersion, long flowRunId, String appId)
specifier|public
name|TimelineCollectorContext
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|userId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|String
name|flowVersion
parameter_list|,
name|long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|flowName
operator|=
name|flowName
expr_stmt|;
name|this
operator|.
name|flowVersion
operator|=
name|flowVersion
expr_stmt|;
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
block|}
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
DECL|method|setClusterId (String clusterId)
specifier|public
name|void
name|setClusterId
parameter_list|(
name|String
name|clusterId
parameter_list|)
block|{
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
block|}
DECL|method|getUserId ()
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
DECL|method|setUserId (String userId)
specifier|public
name|void
name|setUserId
parameter_list|(
name|String
name|userId
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
DECL|method|getFlowName ()
specifier|public
name|String
name|getFlowName
parameter_list|()
block|{
return|return
name|flowName
return|;
block|}
DECL|method|setFlowName (String flowName)
specifier|public
name|void
name|setFlowName
parameter_list|(
name|String
name|flowName
parameter_list|)
block|{
name|this
operator|.
name|flowName
operator|=
name|flowName
expr_stmt|;
block|}
DECL|method|getFlowVersion ()
specifier|public
name|String
name|getFlowVersion
parameter_list|()
block|{
return|return
name|flowVersion
return|;
block|}
DECL|method|setFlowVersion (String flowVersion)
specifier|public
name|void
name|setFlowVersion
parameter_list|(
name|String
name|flowVersion
parameter_list|)
block|{
name|this
operator|.
name|flowVersion
operator|=
name|flowVersion
expr_stmt|;
block|}
DECL|method|getFlowRunId ()
specifier|public
name|long
name|getFlowRunId
parameter_list|()
block|{
return|return
name|flowRunId
return|;
block|}
DECL|method|setFlowRunId (long flowRunId)
specifier|public
name|void
name|setFlowRunId
parameter_list|(
name|long
name|flowRunId
parameter_list|)
block|{
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
block|}
DECL|method|getAppId ()
specifier|public
name|String
name|getAppId
parameter_list|()
block|{
return|return
name|appId
return|;
block|}
DECL|method|setAppId (String appId)
specifier|public
name|void
name|setAppId
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
block|}
block|}
end_class

end_unit

