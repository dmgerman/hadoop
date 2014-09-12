begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.metrics
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
name|metrics
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|AppAttemptMetricsConstants
specifier|public
class|class
name|AppAttemptMetricsConstants
block|{
DECL|field|ENTITY_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|ENTITY_TYPE
init|=
literal|"YARN_APPLICATION_ATTEMPT"
decl_stmt|;
DECL|field|REGISTERED_EVENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|REGISTERED_EVENT_TYPE
init|=
literal|"YARN_APPLICATION_ATTEMPT_REGISTERED"
decl_stmt|;
DECL|field|FINISHED_EVENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|FINISHED_EVENT_TYPE
init|=
literal|"YARN_APPLICATION_ATTEMPT_FINISHED"
decl_stmt|;
DECL|field|PARENT_PRIMARY_FILTER
specifier|public
specifier|static
specifier|final
name|String
name|PARENT_PRIMARY_FILTER
init|=
literal|"YARN_APPLICATION_ATTEMPT_PARENT"
decl_stmt|;
DECL|field|TRACKING_URL_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|TRACKING_URL_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_TRACKING_URL"
decl_stmt|;
DECL|field|ORIGINAL_TRACKING_URL_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|ORIGINAL_TRACKING_URL_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_ORIGINAL_TRACKING_URL"
decl_stmt|;
DECL|field|HOST_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|HOST_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_HOST"
decl_stmt|;
DECL|field|RPC_PORT_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|RPC_PORT_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_RPC_PORT"
decl_stmt|;
DECL|field|MASTER_CONTAINER_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|MASTER_CONTAINER_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_MASTER_CONTAINER"
decl_stmt|;
DECL|field|DIAGNOSTICS_INFO_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|DIAGNOSTICS_INFO_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_DIAGNOSTICS_INFO"
decl_stmt|;
DECL|field|FINAL_STATUS_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|FINAL_STATUS_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_FINAL_STATUS"
decl_stmt|;
DECL|field|STATE_EVENT_INFO
specifier|public
specifier|static
specifier|final
name|String
name|STATE_EVENT_INFO
init|=
literal|"YARN_APPLICATION_ATTEMPT_STATE"
decl_stmt|;
block|}
end_class

end_unit

