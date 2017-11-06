begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.registry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|registry
package|;
end_package

begin_comment
comment|/**  * These are constants unique to the Slider AM  */
end_comment

begin_class
DECL|class|CustomRegistryConstants
specifier|public
class|class
name|CustomRegistryConstants
block|{
DECL|field|MANAGEMENT_REST_API
specifier|public
specifier|static
specifier|final
name|String
name|MANAGEMENT_REST_API
init|=
literal|"classpath:org.apache.slider.management"
decl_stmt|;
DECL|field|REGISTRY_REST_API
specifier|public
specifier|static
specifier|final
name|String
name|REGISTRY_REST_API
init|=
literal|"classpath:org.apache.slider.registry"
decl_stmt|;
DECL|field|PUBLISHER_REST_API
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHER_REST_API
init|=
literal|"classpath:org.apache.slider.publisher"
decl_stmt|;
DECL|field|PUBLISHER_CONFIGURATIONS_API
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHER_CONFIGURATIONS_API
init|=
literal|"classpath:org.apache.slider.publisher.configurations"
decl_stmt|;
DECL|field|PUBLISHER_EXPORTS_API
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHER_EXPORTS_API
init|=
literal|"classpath:org.apache.slider.publisher.exports"
decl_stmt|;
DECL|field|PUBLISHER_DOCUMENTS_API
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHER_DOCUMENTS_API
init|=
literal|"classpath:org.apache.slider.publisher.documents"
decl_stmt|;
DECL|field|AGENT_SECURE_REST_API
specifier|public
specifier|static
specifier|final
name|String
name|AGENT_SECURE_REST_API
init|=
literal|"classpath:org.apache.slider.agents.secure"
decl_stmt|;
DECL|field|AGENT_ONEWAY_REST_API
specifier|public
specifier|static
specifier|final
name|String
name|AGENT_ONEWAY_REST_API
init|=
literal|"classpath:org.apache.slider.agents.oneway"
decl_stmt|;
DECL|field|AM_IPC_PROTOCOL
specifier|public
specifier|static
specifier|final
name|String
name|AM_IPC_PROTOCOL
init|=
literal|"classpath:org.apache.slider.appmaster.ipc"
decl_stmt|;
DECL|field|AM_REST_BASE
specifier|public
specifier|static
specifier|final
name|String
name|AM_REST_BASE
init|=
literal|"classpath:org.apache.slider.client.rest"
decl_stmt|;
DECL|field|WEB_UI
specifier|public
specifier|static
specifier|final
name|String
name|WEB_UI
init|=
literal|"http://"
decl_stmt|;
block|}
end_class

end_unit

