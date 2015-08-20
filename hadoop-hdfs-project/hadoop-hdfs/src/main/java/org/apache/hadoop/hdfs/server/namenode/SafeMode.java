begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
operator|.
name|BlockInfo
import|;
end_import

begin_comment
comment|/** SafeMode related operations. */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|SafeMode
specifier|public
interface|interface
name|SafeMode
block|{
comment|/**    * Check safe mode conditions.    * If the corresponding conditions are satisfied,    * trigger the system to enter/leave safe mode.    */
DECL|method|checkSafeMode ()
specifier|public
name|void
name|checkSafeMode
parameter_list|()
function_decl|;
comment|/** Is the system in safe mode? */
DECL|method|isInSafeMode ()
specifier|public
name|boolean
name|isInSafeMode
parameter_list|()
function_decl|;
comment|/**    * Is the system in startup safe mode, i.e. the system is starting up with    * safe mode turned on automatically?    */
DECL|method|isInStartupSafeMode ()
specifier|public
name|boolean
name|isInStartupSafeMode
parameter_list|()
function_decl|;
comment|/** Check whether replication queues are being populated. */
DECL|method|isPopulatingReplQueues ()
specifier|public
name|boolean
name|isPopulatingReplQueues
parameter_list|()
function_decl|;
comment|/**    * Increment number of blocks that reached minimal replication.    * @param replication current replication    * @param storedBlock current stored Block    */
DECL|method|incrementSafeBlockCount (int replication, BlockInfo storedBlock)
specifier|public
name|void
name|incrementSafeBlockCount
parameter_list|(
name|int
name|replication
parameter_list|,
name|BlockInfo
name|storedBlock
parameter_list|)
function_decl|;
comment|/** Decrement number of blocks that reached minimal replication. */
DECL|method|decrementSafeBlockCount (BlockInfo b)
specifier|public
name|void
name|decrementSafeBlockCount
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

