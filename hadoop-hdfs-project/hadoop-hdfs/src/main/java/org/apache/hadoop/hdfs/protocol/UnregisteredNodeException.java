begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

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
name|classification
operator|.
name|InterfaceStability
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
name|server
operator|.
name|protocol
operator|.
name|JournalInfo
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
name|server
operator|.
name|protocol
operator|.
name|NodeRegistration
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when a node that has not previously   * registered is trying to access the name node.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|UnregisteredNodeException
specifier|public
class|class
name|UnregisteredNodeException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5620209396945970810L
decl_stmt|;
DECL|method|UnregisteredNodeException (JournalInfo info)
specifier|public
name|UnregisteredNodeException
parameter_list|(
name|JournalInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
literal|"Unregistered server: "
operator|+
name|info
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|UnregisteredNodeException (NodeRegistration nodeReg)
specifier|public
name|UnregisteredNodeException
parameter_list|(
name|NodeRegistration
name|nodeReg
parameter_list|)
block|{
name|super
argument_list|(
literal|"Unregistered server: "
operator|+
name|nodeReg
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * The exception is thrown if a different data-node claims the same    * storage id as the existing one.    *      * @param nodeID unregistered data-node    * @param storedNode data-node stored in the system with this storage id    */
DECL|method|UnregisteredNodeException (DatanodeID nodeID, DatanodeInfo storedNode)
specifier|public
name|UnregisteredNodeException
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|DatanodeInfo
name|storedNode
parameter_list|)
block|{
name|super
argument_list|(
literal|"Data node "
operator|+
name|nodeID
operator|+
literal|" is attempting to report storage ID "
operator|+
name|nodeID
operator|.
name|getDatanodeUuid
argument_list|()
operator|+
literal|". Node "
operator|+
name|storedNode
operator|+
literal|" is expected to serve this storage."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

