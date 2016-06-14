begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.resource
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
name|resource
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|StringUtils
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
name|NodeId
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
name|Resource
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
name|ResourceOption
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
name|conf
operator|.
name|YarnConfiguration
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
name|ConverterUtils
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_class
DECL|class|DynamicResourceConfiguration
specifier|public
class|class
name|DynamicResourceConfiguration
extends|extends
name|Configuration
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DynamicResourceConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Private
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"yarn.resource.dynamic."
decl_stmt|;
annotation|@
name|Private
DECL|field|DOT
specifier|public
specifier|static
specifier|final
name|String
name|DOT
init|=
literal|"."
decl_stmt|;
annotation|@
name|Private
DECL|field|NODES
specifier|public
specifier|static
specifier|final
name|String
name|NODES
init|=
literal|"nodes"
decl_stmt|;
annotation|@
name|Private
DECL|field|VCORES
specifier|public
specifier|static
specifier|final
name|String
name|VCORES
init|=
literal|"vcores"
decl_stmt|;
annotation|@
name|Private
DECL|field|MEMORY
specifier|public
specifier|static
specifier|final
name|String
name|MEMORY
init|=
literal|"memory"
decl_stmt|;
annotation|@
name|Private
DECL|field|OVERCOMMIT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|OVERCOMMIT_TIMEOUT
init|=
literal|"overcommittimeout"
decl_stmt|;
DECL|method|DynamicResourceConfiguration ()
specifier|public
name|DynamicResourceConfiguration
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DynamicResourceConfiguration (Configuration configuration)
specifier|public
name|DynamicResourceConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|YarnConfiguration
operator|.
name|DR_CONFIGURATION_FILE
argument_list|)
expr_stmt|;
block|}
DECL|method|DynamicResourceConfiguration (Configuration configuration, InputStream drInputStream)
specifier|public
name|DynamicResourceConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|InputStream
name|drInputStream
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|drInputStream
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodePrefix (String node)
specifier|private
name|String
name|getNodePrefix
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|String
name|nodeName
init|=
name|PREFIX
operator|+
name|node
operator|+
name|DOT
decl_stmt|;
return|return
name|nodeName
return|;
block|}
DECL|method|getVcoresPerNode (String node)
specifier|public
name|int
name|getVcoresPerNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|int
name|vcoresPerNode
init|=
name|getInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|VCORES
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_VCORES
argument_list|)
decl_stmt|;
return|return
name|vcoresPerNode
return|;
block|}
DECL|method|setVcoresPerNode (String node, int vcores)
specifier|public
name|void
name|setVcoresPerNode
parameter_list|(
name|String
name|node
parameter_list|,
name|int
name|vcores
parameter_list|)
block|{
name|setInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|VCORES
argument_list|,
name|vcores
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"DRConf - setVcoresPerNode: nodePrefix="
operator|+
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
literal|", vcores="
operator|+
name|vcores
argument_list|)
expr_stmt|;
block|}
DECL|method|getMemoryPerNode (String node)
specifier|public
name|int
name|getMemoryPerNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|int
name|memoryPerNode
init|=
name|getInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|MEMORY
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_PMEM_MB
argument_list|)
decl_stmt|;
return|return
name|memoryPerNode
return|;
block|}
DECL|method|setMemoryPerNode (String node, int memory)
specifier|public
name|void
name|setMemoryPerNode
parameter_list|(
name|String
name|node
parameter_list|,
name|int
name|memory
parameter_list|)
block|{
name|setInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|MEMORY
argument_list|,
name|memory
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"DRConf - setMemoryPerNode: nodePrefix="
operator|+
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
literal|", memory="
operator|+
name|memory
argument_list|)
expr_stmt|;
block|}
DECL|method|getOverCommitTimeoutPerNode (String node)
specifier|public
name|int
name|getOverCommitTimeoutPerNode
parameter_list|(
name|String
name|node
parameter_list|)
block|{
name|int
name|overCommitTimeoutPerNode
init|=
name|getInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|OVERCOMMIT_TIMEOUT
argument_list|,
name|ResourceOption
operator|.
name|OVER_COMMIT_TIMEOUT_MILLIS_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|overCommitTimeoutPerNode
return|;
block|}
DECL|method|setOverCommitTimeoutPerNode (String node, int overCommitTimeout)
specifier|public
name|void
name|setOverCommitTimeoutPerNode
parameter_list|(
name|String
name|node
parameter_list|,
name|int
name|overCommitTimeout
parameter_list|)
block|{
name|setInt
argument_list|(
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
name|OVERCOMMIT_TIMEOUT
argument_list|,
name|overCommitTimeout
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"DRConf - setOverCommitTimeoutPerNode: nodePrefix="
operator|+
name|getNodePrefix
argument_list|(
name|node
argument_list|)
operator|+
literal|", overCommitTimeout="
operator|+
name|overCommitTimeout
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodes ()
specifier|public
name|String
index|[]
name|getNodes
parameter_list|()
block|{
name|String
index|[]
name|nodes
init|=
name|getStrings
argument_list|(
name|PREFIX
operator|+
name|NODES
argument_list|)
decl_stmt|;
return|return
name|nodes
return|;
block|}
DECL|method|setNodes (String[] nodes)
specifier|public
name|void
name|setNodes
parameter_list|(
name|String
index|[]
name|nodes
parameter_list|)
block|{
name|set
argument_list|(
name|PREFIX
operator|+
name|NODES
argument_list|,
name|StringUtils
operator|.
name|arrayToString
argument_list|(
name|nodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getNodeResourceMap ()
specifier|public
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
name|getNodeResourceMap
parameter_list|()
block|{
name|String
index|[]
name|nodes
init|=
name|getNodes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
name|resourceOptions
init|=
operator|new
name|HashMap
argument_list|<
name|NodeId
argument_list|,
name|ResourceOption
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|NodeId
name|nid
init|=
name|NodeId
operator|.
name|fromString
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|int
name|vcores
init|=
name|getVcoresPerNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|int
name|memory
init|=
name|getMemoryPerNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|int
name|overCommitTimeout
init|=
name|getOverCommitTimeoutPerNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|Resource
name|resource
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
decl_stmt|;
name|ResourceOption
name|resourceOption
init|=
name|ResourceOption
operator|.
name|newInstance
argument_list|(
name|resource
argument_list|,
name|overCommitTimeout
argument_list|)
decl_stmt|;
name|resourceOptions
operator|.
name|put
argument_list|(
name|nid
argument_list|,
name|resourceOption
argument_list|)
expr_stmt|;
block|}
return|return
name|resourceOptions
return|;
block|}
block|}
end_class

end_unit

