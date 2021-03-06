begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.common
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
name|scheduler
operator|.
name|common
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|placement
operator|.
name|AppPlacementAllocator
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
name|scheduler
operator|.
name|placement
operator|.
name|LocalityAppPlacementAllocator
import|;
end_import

begin_comment
comment|/**  * This class will keep all Scheduling env's names which will help in  * placement calculations.  */
end_comment

begin_class
DECL|class|ApplicationSchedulingConfig
specifier|public
class|class
name|ApplicationSchedulingConfig
block|{
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_APPLICATION_PLACEMENT_TYPE_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|ENV_APPLICATION_PLACEMENT_TYPE_CLASS
init|=
literal|"APPLICATION_PLACEMENT_TYPE_CLASS"
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
specifier|public
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|AppPlacementAllocator
argument_list|>
DECL|field|DEFAULT_APPLICATION_PLACEMENT_TYPE_CLASS
name|DEFAULT_APPLICATION_PLACEMENT_TYPE_CLASS
init|=
name|LocalityAppPlacementAllocator
operator|.
name|class
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|field|ENV_MULTI_NODE_SORTING_POLICY_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|ENV_MULTI_NODE_SORTING_POLICY_CLASS
init|=
literal|"MULTI_NODE_SORTING_POLICY_CLASS"
decl_stmt|;
block|}
end_class

end_unit

