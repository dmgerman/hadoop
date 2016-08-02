begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.management
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
name|management
package|;
end_package

begin_comment
comment|/**  * Constants used in slider for metrics registration and lookup  */
end_comment

begin_class
DECL|class|MetricsConstants
specifier|public
class|class
name|MetricsConstants
block|{
comment|/**    * {@value}    */
DECL|field|CONTAINERS_OUTSTANDING_REQUESTS
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_OUTSTANDING_REQUESTS
init|=
literal|"containers.outstanding-requests"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|CONTAINERS_STARTED
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_STARTED
init|=
literal|"containers.started"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|CONTAINERS_SURPLUS
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_SURPLUS
init|=
literal|"containers.surplus"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|CONTAINERS_COMPLETED
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_COMPLETED
init|=
literal|"containers.completed"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|CONTAINERS_FAILED
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_FAILED
init|=
literal|"containers.failed"
decl_stmt|;
comment|/**    * {@value}    */
DECL|field|CONTAINERS_START_FAILED
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINERS_START_FAILED
init|=
literal|"containers.start-failed"
decl_stmt|;
DECL|field|PREFIX_SLIDER_ROLES
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_SLIDER_ROLES
init|=
literal|"slider.roles."
decl_stmt|;
block|}
end_class

end_unit

