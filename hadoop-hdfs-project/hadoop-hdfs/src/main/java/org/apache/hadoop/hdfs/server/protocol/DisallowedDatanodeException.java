begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
package|package
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
name|protocol
operator|.
name|DatanodeID
import|;
end_import

begin_comment
comment|/**  * This exception is thrown when a datanode tries to register or communicate  * with the namenode when it does not appear on the list of included nodes,   * or has been specifically excluded.  *   */
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
DECL|class|DisallowedDatanodeException
specifier|public
class|class
name|DisallowedDatanodeException
extends|extends
name|IOException
block|{
comment|/** for java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|DisallowedDatanodeException (DatanodeID nodeID, String reason)
specifier|public
name|DisallowedDatanodeException
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
literal|"Datanode denied communication with namenode because "
operator|+
name|reason
operator|+
literal|": "
operator|+
name|nodeID
argument_list|)
expr_stmt|;
block|}
DECL|method|DisallowedDatanodeException (DatanodeID nodeID)
specifier|public
name|DisallowedDatanodeException
parameter_list|(
name|DatanodeID
name|nodeID
parameter_list|)
block|{
name|this
argument_list|(
name|nodeID
argument_list|,
literal|"the host is not in the include-list"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

