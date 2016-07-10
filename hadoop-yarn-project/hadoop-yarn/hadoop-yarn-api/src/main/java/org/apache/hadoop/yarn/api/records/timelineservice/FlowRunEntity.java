begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timelineservice
package|package
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
name|timelineservice
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
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

begin_comment
comment|/**  * This entity represents a flow run.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FlowRunEntity
specifier|public
class|class
name|FlowRunEntity
extends|extends
name|HierarchicalTimelineEntity
block|{
DECL|field|USER_INFO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|USER_INFO_KEY
init|=
name|TimelineEntity
operator|.
name|SYSTEM_INFO_KEY_PREFIX
operator|+
literal|"USER"
decl_stmt|;
DECL|field|FLOW_NAME_INFO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FLOW_NAME_INFO_KEY
init|=
name|TimelineEntity
operator|.
name|SYSTEM_INFO_KEY_PREFIX
operator|+
literal|"FLOW_NAME"
decl_stmt|;
DECL|field|FLOW_VERSION_INFO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FLOW_VERSION_INFO_KEY
init|=
name|TimelineEntity
operator|.
name|SYSTEM_INFO_KEY_PREFIX
operator|+
literal|"FLOW_VERSION"
decl_stmt|;
DECL|field|FLOW_RUN_ID_INFO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|FLOW_RUN_ID_INFO_KEY
init|=
name|TimelineEntity
operator|.
name|SYSTEM_INFO_KEY_PREFIX
operator|+
literal|"FLOW_RUN_ID"
decl_stmt|;
DECL|field|FLOW_RUN_END_TIME
specifier|public
specifier|static
specifier|final
name|String
name|FLOW_RUN_END_TIME
init|=
name|TimelineEntity
operator|.
name|SYSTEM_INFO_KEY_PREFIX
operator|+
literal|"FLOW_RUN_END_TIME"
decl_stmt|;
DECL|method|FlowRunEntity ()
specifier|public
name|FlowRunEntity
parameter_list|()
block|{
name|super
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// set config to null
name|setConfigs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|FlowRunEntity (TimelineEntity entity)
specifier|public
name|FlowRunEntity
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|super
argument_list|(
name|entity
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|entity
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Incompatible entity type: "
operator|+
name|getId
argument_list|()
argument_list|)
throw|;
block|}
comment|// set config to null
name|setConfigs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"id"
argument_list|)
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
comment|//Flow id schema: user@flow_name(or id)/run_id
name|String
name|id
init|=
name|super
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|USER_INFO_KEY
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'@'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_NAME_INFO_KEY
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_RUN_ID_INFO_KEY
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|id
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|USER_INFO_KEY
argument_list|)
return|;
block|}
DECL|method|setUser (String user)
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|addInfo
argument_list|(
name|USER_INFO_KEY
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_NAME_INFO_KEY
argument_list|)
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|addInfo
argument_list|(
name|FLOW_NAME_INFO_KEY
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_VERSION_INFO_KEY
argument_list|)
return|;
block|}
DECL|method|setVersion (String version)
specifier|public
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|addInfo
argument_list|(
name|FLOW_VERSION_INFO_KEY
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
DECL|method|getRunId ()
specifier|public
name|long
name|getRunId
parameter_list|()
block|{
name|Object
name|runId
init|=
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_RUN_ID_INFO_KEY
argument_list|)
decl_stmt|;
return|return
name|runId
operator|==
literal|null
condition|?
literal|0L
else|:
operator|(
name|Long
operator|)
name|runId
return|;
block|}
DECL|method|setRunId (long runId)
specifier|public
name|void
name|setRunId
parameter_list|(
name|long
name|runId
parameter_list|)
block|{
name|addInfo
argument_list|(
name|FLOW_RUN_ID_INFO_KEY
argument_list|,
name|runId
argument_list|)
expr_stmt|;
block|}
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|getCreatedTime
argument_list|()
return|;
block|}
DECL|method|setStartTime (long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|startTime
parameter_list|)
block|{
name|setCreatedTime
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxEndTime ()
specifier|public
name|long
name|getMaxEndTime
parameter_list|()
block|{
name|Object
name|time
init|=
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
name|FLOW_RUN_END_TIME
argument_list|)
decl_stmt|;
return|return
name|time
operator|==
literal|null
condition|?
literal|0L
else|:
operator|(
name|Long
operator|)
name|time
return|;
block|}
DECL|method|setMaxEndTime (long endTime)
specifier|public
name|void
name|setMaxEndTime
parameter_list|(
name|long
name|endTime
parameter_list|)
block|{
name|addInfo
argument_list|(
name|FLOW_RUN_END_TIME
argument_list|,
name|endTime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

