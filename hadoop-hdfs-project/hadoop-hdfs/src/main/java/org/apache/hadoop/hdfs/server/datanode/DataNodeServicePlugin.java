begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|DatanodeRegistration
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
name|ServicePlugin
import|;
end_import

begin_comment
comment|/**  * Datanode specific service plugin with additional hooks.  */
end_comment

begin_interface
DECL|interface|DataNodeServicePlugin
specifier|public
interface|interface
name|DataNodeServicePlugin
extends|extends
name|ServicePlugin
block|{
comment|/**    * Extension point to modify the datanode id.    *    * @param dataNodeId    */
DECL|method|onDatanodeIdCreation (DatanodeID dataNodeId)
specifier|default
name|void
name|onDatanodeIdCreation
parameter_list|(
name|DatanodeID
name|dataNodeId
parameter_list|)
block|{
comment|//NOOP
block|}
comment|/**    * Extension point to modify the datanode id.    *    * @param dataNodeId    */
DECL|method|onDatanodeSuccessfulNamenodeRegisration ( DatanodeRegistration dataNodeId)
specifier|default
name|void
name|onDatanodeSuccessfulNamenodeRegisration
parameter_list|(
name|DatanodeRegistration
name|dataNodeId
parameter_list|)
block|{
comment|//NOOP
block|}
block|}
end_interface

end_unit

