begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container.placement.algorithms
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
name|hdsl
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A ContainerPlacementPolicy support choosing datanodes to build replication  * pipeline with specified constraints.  */
end_comment

begin_interface
DECL|interface|ContainerPlacementPolicy
specifier|public
interface|interface
name|ContainerPlacementPolicy
block|{
comment|/**    * Given the replication factor and size required, return set of datanodes    * that satisfy the nodes and size requirement.    * @param nodesRequired - number of datanodes required.    * @param sizeRequired - size required for the container or block.    * @return list of datanodes chosen.    * @throws IOException    */
DECL|method|chooseDatanodes (int nodesRequired, long sizeRequired)
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|chooseDatanodes
parameter_list|(
name|int
name|nodesRequired
parameter_list|,
name|long
name|sizeRequired
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

