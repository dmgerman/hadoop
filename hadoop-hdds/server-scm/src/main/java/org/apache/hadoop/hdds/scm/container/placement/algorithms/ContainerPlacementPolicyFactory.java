begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.placement.algorithms
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|hdds
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NetworkTopology
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
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_comment
comment|/**  * A factory to create container placement instance based on configuration  * property ozone.scm.container.placement.classname.  */
end_comment

begin_class
DECL|class|ContainerPlacementPolicyFactory
specifier|public
specifier|final
class|class
name|ContainerPlacementPolicyFactory
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
name|ContainerPlacementPolicyFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
DECL|field|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_DEFAULT
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_DEFAULT
init|=
name|SCMContainerPlacementRandom
operator|.
name|class
decl_stmt|;
DECL|method|ContainerPlacementPolicyFactory ()
specifier|private
name|ContainerPlacementPolicyFactory
parameter_list|()
block|{   }
DECL|method|getPolicy (Configuration conf, final NodeManager nodeManager, NetworkTopology clusterMap, final boolean fallback, SCMContainerPlacementMetrics metrics)
specifier|public
specifier|static
name|ContainerPlacementPolicy
name|getPolicy
parameter_list|(
name|Configuration
name|conf
parameter_list|,
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
name|NetworkTopology
name|clusterMap
parameter_list|,
specifier|final
name|boolean
name|fallback
parameter_list|,
name|SCMContainerPlacementMetrics
name|metrics
parameter_list|)
throws|throws
name|SCMException
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|placementClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
argument_list|,
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_DEFAULT
argument_list|,
name|ContainerPlacementPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|constructor
decl_stmt|;
try|try
block|{
name|constructor
operator|=
name|placementClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|,
name|NetworkTopology
operator|.
name|class
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
name|SCMContainerPlacementMetrics
operator|.
name|class
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Create container placement policy of type "
operator|+
name|placementClass
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to find constructor(NodeManager, Configuration, "
operator|+
literal|"NetworkTopology, boolean) for class "
operator|+
name|placementClass
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
name|msg
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_INIT_CONTAINER_PLACEMENT_POLICY
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|,
name|clusterMap
argument_list|,
name|fallback
argument_list|,
name|metrics
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to instantiate class "
operator|+
name|placementClass
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" for "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

