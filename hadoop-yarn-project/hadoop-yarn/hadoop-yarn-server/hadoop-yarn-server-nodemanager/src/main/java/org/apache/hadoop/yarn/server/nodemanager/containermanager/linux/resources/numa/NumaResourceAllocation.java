begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.resources.numa
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|resources
operator|.
name|numa
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * NumaResourceAllocation contains Memory nodes and CPU nodes assigned to a  * container.  */
end_comment

begin_class
DECL|class|NumaResourceAllocation
specifier|public
class|class
name|NumaResourceAllocation
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6339719798446595123L
decl_stmt|;
DECL|field|nodeVsMemory
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|nodeVsMemory
decl_stmt|;
DECL|field|nodeVsCpus
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodeVsCpus
decl_stmt|;
DECL|method|NumaResourceAllocation ()
specifier|public
name|NumaResourceAllocation
parameter_list|()
block|{
name|nodeVsMemory
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|nodeVsCpus
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|NumaResourceAllocation (String memNodeId, long memory, String cpuNodeId, int cpus)
specifier|public
name|NumaResourceAllocation
parameter_list|(
name|String
name|memNodeId
parameter_list|,
name|long
name|memory
parameter_list|,
name|String
name|cpuNodeId
parameter_list|,
name|int
name|cpus
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|nodeVsMemory
operator|.
name|put
argument_list|(
name|memNodeId
argument_list|,
name|memory
argument_list|)
expr_stmt|;
name|nodeVsCpus
operator|.
name|put
argument_list|(
name|cpuNodeId
argument_list|,
name|cpus
argument_list|)
expr_stmt|;
block|}
DECL|method|addMemoryNode (String memNodeId, long memory)
specifier|public
name|void
name|addMemoryNode
parameter_list|(
name|String
name|memNodeId
parameter_list|,
name|long
name|memory
parameter_list|)
block|{
name|nodeVsMemory
operator|.
name|put
argument_list|(
name|memNodeId
argument_list|,
name|memory
argument_list|)
expr_stmt|;
block|}
DECL|method|addCpuNode (String cpuNodeId, int cpus)
specifier|public
name|void
name|addCpuNode
parameter_list|(
name|String
name|cpuNodeId
parameter_list|,
name|int
name|cpus
parameter_list|)
block|{
name|nodeVsCpus
operator|.
name|put
argument_list|(
name|cpuNodeId
argument_list|,
name|cpus
argument_list|)
expr_stmt|;
block|}
DECL|method|getMemNodes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getMemNodes
parameter_list|()
block|{
return|return
name|nodeVsMemory
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|getCpuNodes ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getCpuNodes
parameter_list|()
block|{
return|return
name|nodeVsCpus
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|getNodeVsMemory ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getNodeVsMemory
parameter_list|()
block|{
return|return
name|nodeVsMemory
return|;
block|}
DECL|method|getNodeVsCpus ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getNodeVsCpus
parameter_list|()
block|{
return|return
name|nodeVsCpus
return|;
block|}
block|}
end_class

end_unit

