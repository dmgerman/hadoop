begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.pipelines
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|pipelines
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
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeID
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationType
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|algorithms
operator|.
name|SCMContainerPlacementRandom
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
name|ozone
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
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|ratis
operator|.
name|RatisManagerImpl
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
name|ozone
operator|.
name|scm
operator|.
name|pipelines
operator|.
name|standalone
operator|.
name|StandaloneManagerImpl
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|io
operator|.
name|IOException
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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Sends the request to the right pipeline manager.  */
end_comment

begin_class
DECL|class|PipelineSelector
specifier|public
class|class
name|PipelineSelector
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
name|PipelineSelector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|placementPolicy
specifier|private
specifier|final
name|ContainerPlacementPolicy
name|placementPolicy
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|ratisManager
specifier|private
specifier|final
name|RatisManagerImpl
name|ratisManager
decl_stmt|;
DECL|field|standaloneManager
specifier|private
specifier|final
name|StandaloneManagerImpl
name|standaloneManager
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
comment|/**    * Constructs a pipeline Selector.    * @param nodeManager - node manager    * @param conf - Ozone Config    */
DECL|method|PipelineSelector (NodeManager nodeManager, Configuration conf)
specifier|public
name|PipelineSelector
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|placementPolicy
operator|=
name|createContainerPlacementPolicy
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerSize
operator|=
name|OzoneConsts
operator|.
name|GB
operator|*
name|this
operator|.
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|standaloneManager
operator|=
operator|new
name|StandaloneManagerImpl
argument_list|(
name|this
operator|.
name|nodeManager
argument_list|,
name|placementPolicy
argument_list|,
name|containerSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|ratisManager
operator|=
operator|new
name|RatisManagerImpl
argument_list|(
name|this
operator|.
name|nodeManager
argument_list|,
name|placementPolicy
argument_list|,
name|containerSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create pluggable container placement policy implementation instance.    *    * @param nodeManager - SCM node manager.    * @param conf - configuration.    * @return SCM container placement policy implementation instance.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createContainerPlacementPolicy ( final NodeManager nodeManager, final Configuration conf)
specifier|private
specifier|static
name|ContainerPlacementPolicy
name|createContainerPlacementPolicy
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|implClass
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
operator|)
name|conf
operator|.
name|getClass
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
argument_list|,
name|SCMContainerPlacementRandom
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|ctor
init|=
name|implClass
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
argument_list|)
decl_stmt|;
return|return
name|ctor
operator|.
name|newInstance
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|implClass
operator|.
name|getName
argument_list|()
operator|+
literal|" could not be constructed."
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unhandled exception occurred, Placement policy will not be "
operator|+
literal|"functional."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to load "
operator|+
literal|"ContainerPlacementPolicy"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Return the pipeline manager from the replication type.    * @param replicationType - Replication Type Enum.    * @return pipeline Manager.    * @throws IllegalArgumentException    */
DECL|method|getPipelineManager (ReplicationType replicationType)
specifier|private
name|PipelineManager
name|getPipelineManager
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
switch|switch
condition|(
name|replicationType
condition|)
block|{
case|case
name|RATIS
case|:
return|return
name|this
operator|.
name|ratisManager
return|;
case|case
name|STAND_ALONE
case|:
return|return
name|this
operator|.
name|standaloneManager
return|;
case|case
name|CHAINED
case|:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not implemented yet"
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected enum found. Does not"
operator|+
literal|" know how to handle "
operator|+
name|replicationType
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * This function is called by the Container Manager while allocating a new    * container. The client specifies what kind of replication pipeline is needed    * and based on the replication type in the request appropriate Interface is    * invoked.    *    */
DECL|method|getReplicationPipeline (ReplicationType replicationType, OzoneProtos.ReplicationFactor replicationFactor, String containerName)
specifier|public
name|Pipeline
name|getReplicationPipeline
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|OzoneProtos
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineManager
name|manager
init|=
name|getPipelineManager
argument_list|(
name|replicationType
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|manager
argument_list|,
literal|"Found invalid pipeline manager"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting replication pipeline for {} : Replication {}"
argument_list|,
name|containerName
argument_list|,
name|replicationFactor
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|manager
operator|.
name|getPipeline
argument_list|(
name|containerName
argument_list|,
name|replicationFactor
argument_list|)
return|;
block|}
comment|/**    * Creates a pipeline from a specified set of Nodes.    */
DECL|method|createPipeline (ReplicationType replicationType, String pipelineID, List<DatanodeID> datanodes)
specifier|public
name|void
name|createPipeline
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineManager
name|manager
init|=
name|getPipelineManager
argument_list|(
name|replicationType
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|manager
argument_list|,
literal|"Found invalid pipeline manager"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating a pipeline: {} with nodes:{}"
argument_list|,
name|pipelineID
argument_list|,
name|datanodes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DatanodeID
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|createPipeline
argument_list|(
name|pipelineID
argument_list|,
name|datanodes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Close the  pipeline with the given clusterId.    */
DECL|method|closePipeline (ReplicationType replicationType, String pipelineID)
specifier|public
name|void
name|closePipeline
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineManager
name|manager
init|=
name|getPipelineManager
argument_list|(
name|replicationType
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|manager
argument_list|,
literal|"Found invalid pipeline manager"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing pipeline. pipelineID: {}"
argument_list|,
name|pipelineID
argument_list|)
expr_stmt|;
name|manager
operator|.
name|closePipeline
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
comment|/**    * list members in the pipeline .    */
DECL|method|getDatanodes (ReplicationType replicationType, String pipelineID)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getDatanodes
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|String
name|pipelineID
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineManager
name|manager
init|=
name|getPipelineManager
argument_list|(
name|replicationType
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|manager
argument_list|,
literal|"Found invalid pipeline manager"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting data nodes from pipeline : {}"
argument_list|,
name|pipelineID
argument_list|)
expr_stmt|;
return|return
name|manager
operator|.
name|getMembers
argument_list|(
name|pipelineID
argument_list|)
return|;
block|}
comment|/**    * Update the datanodes in the list of the pipeline.    */
DECL|method|updateDatanodes (ReplicationType replicationType, String pipelineID, List<DatanodeID> newDatanodes)
specifier|public
name|void
name|updateDatanodes
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|,
name|String
name|pipelineID
parameter_list|,
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|newDatanodes
parameter_list|)
throws|throws
name|IOException
block|{
name|PipelineManager
name|manager
init|=
name|getPipelineManager
argument_list|(
name|replicationType
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|manager
argument_list|,
literal|"Found invalid pipeline manager"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Updating pipeline: {} with new nodes:{}"
argument_list|,
name|pipelineID
argument_list|,
name|newDatanodes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DatanodeID
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|updatePipeline
argument_list|(
name|pipelineID
argument_list|,
name|newDatanodes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

