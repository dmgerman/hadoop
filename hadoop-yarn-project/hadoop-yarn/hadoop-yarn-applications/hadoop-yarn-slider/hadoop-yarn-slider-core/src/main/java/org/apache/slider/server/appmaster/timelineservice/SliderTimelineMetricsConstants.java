begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.timelineservice
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
name|timelineservice
package|;
end_package

begin_comment
comment|/**  * Constants which are stored as key in ATS  */
end_comment

begin_class
DECL|class|SliderTimelineMetricsConstants
specifier|public
specifier|final
class|class
name|SliderTimelineMetricsConstants
block|{
DECL|field|URI
specifier|public
specifier|static
specifier|final
name|String
name|URI
init|=
literal|"URI"
decl_stmt|;
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"NAME"
decl_stmt|;
DECL|field|STATE
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"STATE"
decl_stmt|;
DECL|field|EXIT_STATUS_CODE
specifier|public
specifier|static
specifier|final
name|String
name|EXIT_STATUS_CODE
init|=
literal|"EXIT_STATUS_CODE"
decl_stmt|;
DECL|field|EXIT_REASON
specifier|public
specifier|static
specifier|final
name|String
name|EXIT_REASON
init|=
literal|"EXIT_REASON"
decl_stmt|;
DECL|field|DIAGNOSTICS_INFO
specifier|public
specifier|static
specifier|final
name|String
name|DIAGNOSTICS_INFO
init|=
literal|"DIAGNOSTICS_INFO"
decl_stmt|;
DECL|field|LAUNCH_TIME
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCH_TIME
init|=
literal|"LAUNCH_TIME"
decl_stmt|;
DECL|field|QUICK_LINKS
specifier|public
specifier|static
specifier|final
name|String
name|QUICK_LINKS
init|=
literal|"QUICK_LINKS"
decl_stmt|;
DECL|field|LAUNCH_COMMAND
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCH_COMMAND
init|=
literal|"LAUNCH_COMMAND"
decl_stmt|;
DECL|field|TOTAL_CONTAINERS
specifier|public
specifier|static
specifier|final
name|String
name|TOTAL_CONTAINERS
init|=
literal|"NUMBER_OF_CONTAINERS"
decl_stmt|;
DECL|field|RUNNING_CONTAINERS
specifier|public
specifier|static
specifier|final
name|String
name|RUNNING_CONTAINERS
init|=
literal|"NUMBER_OF_RUNNING_CONTAINERS"
decl_stmt|;
comment|/**    * Artifacts constants.    */
DECL|field|ARTIFACT_ID
specifier|public
specifier|static
specifier|final
name|String
name|ARTIFACT_ID
init|=
literal|"ARTIFACT_ID"
decl_stmt|;
DECL|field|ARTIFACT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|ARTIFACT_TYPE
init|=
literal|"ARTIFACT_TYPE"
decl_stmt|;
DECL|field|ARTIFACT_URI
specifier|public
specifier|static
specifier|final
name|String
name|ARTIFACT_URI
init|=
literal|"ARTIFACT_URI"
decl_stmt|;
comment|/**    * Resource constants.    */
DECL|field|RESOURCE_CPU
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_CPU
init|=
literal|"RESOURCE_CPU"
decl_stmt|;
DECL|field|RESOURCE_MEMORY
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_MEMORY
init|=
literal|"RESOURCE_MEMORY"
decl_stmt|;
DECL|field|RESOURCE_PROFILE
specifier|public
specifier|static
specifier|final
name|String
name|RESOURCE_PROFILE
init|=
literal|"RESOURCE_PROFILE"
decl_stmt|;
comment|/**    * component instance constants.    */
DECL|field|IP
specifier|public
specifier|static
specifier|final
name|String
name|IP
init|=
literal|"IP"
decl_stmt|;
DECL|field|HOSTNAME
specifier|public
specifier|static
specifier|final
name|String
name|HOSTNAME
init|=
literal|"HOSTNAME"
decl_stmt|;
DECL|field|BARE_HOST
specifier|public
specifier|static
specifier|final
name|String
name|BARE_HOST
init|=
literal|"BARE_HOST"
decl_stmt|;
DECL|field|COMPONENT_NAME
specifier|public
specifier|static
specifier|final
name|String
name|COMPONENT_NAME
init|=
literal|"COMPONENT_NAME"
decl_stmt|;
comment|/**    * component constants.    */
DECL|field|DEPENDENCIES
specifier|public
specifier|static
specifier|final
name|String
name|DEPENDENCIES
init|=
literal|"DEPENDENCIES"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"DESCRIPTION"
decl_stmt|;
DECL|field|UNIQUE_COMPONENT_SUPPORT
specifier|public
specifier|static
specifier|final
name|String
name|UNIQUE_COMPONENT_SUPPORT
init|=
literal|"UNIQUE_COMPONENT_SUPPORT"
decl_stmt|;
DECL|field|RUN_PRIVILEGED_CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|RUN_PRIVILEGED_CONTAINER
init|=
literal|"RUN_PRIVILEGED_CONTAINER"
decl_stmt|;
DECL|field|PLACEMENT_POLICY
specifier|public
specifier|static
specifier|final
name|String
name|PLACEMENT_POLICY
init|=
literal|"PLACEMENT_POLICY"
decl_stmt|;
block|}
end_class

end_unit

