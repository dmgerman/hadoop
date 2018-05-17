begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
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
name|node
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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_comment
comment|/**  *  * This is the JMX management interface for node manager information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|SCMNodeStorageStatMXBean
specifier|public
interface|interface
name|SCMNodeStorageStatMXBean
block|{
comment|/**    * Get the capacity of the dataNode    * @param datanodeID Datanode Id    * @return long    */
DECL|method|getCapacity (UUID datanodeID)
name|long
name|getCapacity
parameter_list|(
name|UUID
name|datanodeID
parameter_list|)
function_decl|;
comment|/**    * Returns the remaining space of a Datanode.    * @param datanodeId Datanode Id    * @return long    */
DECL|method|getRemainingSpace (UUID datanodeId)
name|long
name|getRemainingSpace
parameter_list|(
name|UUID
name|datanodeId
parameter_list|)
function_decl|;
comment|/**    * Returns used space in bytes of a Datanode.    * @return long    */
DECL|method|getUsedSpace (UUID datanodeId)
name|long
name|getUsedSpace
parameter_list|(
name|UUID
name|datanodeId
parameter_list|)
function_decl|;
comment|/**    * Returns the total capacity of all dataNodes    * @return long    */
DECL|method|getTotalCapacity ()
name|long
name|getTotalCapacity
parameter_list|()
function_decl|;
comment|/**    * Returns the total Used Space in all Datanodes.    * @return long    */
DECL|method|getTotalSpaceUsed ()
name|long
name|getTotalSpaceUsed
parameter_list|()
function_decl|;
comment|/**    * Returns the total Remaining Space in all Datanodes.    * @return long    */
DECL|method|getTotalFreeSpace ()
name|long
name|getTotalFreeSpace
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

