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
name|hdfs
operator|.
name|protocol
operator|.
name|Block
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
name|BlockCollection
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
name|namenode
operator|.
name|NameNode
operator|.
name|OperationCategory
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
name|util
operator|.
name|RwLock
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
name|io
operator|.
name|erasurecode
operator|.
name|ECSchema
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
name|ipc
operator|.
name|StandbyException
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_comment
comment|/** Namesystem operations. */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|Namesystem
specifier|public
interface|interface
name|Namesystem
extends|extends
name|RwLock
extends|,
name|SafeMode
block|{
comment|/** Is this name system running? */
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
function_decl|;
comment|/** Check if the user has superuser privilege. */
DECL|method|checkSuperuserPrivilege ()
specifier|public
name|void
name|checkSuperuserPrivilege
parameter_list|()
throws|throws
name|AccessControlException
function_decl|;
comment|/** @return the block pool ID */
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
function_decl|;
DECL|method|isInStandbyState ()
specifier|public
name|boolean
name|isInStandbyState
parameter_list|()
function_decl|;
DECL|method|isGenStampInFuture (Block block)
specifier|public
name|boolean
name|isGenStampInFuture
parameter_list|(
name|Block
name|block
parameter_list|)
function_decl|;
DECL|method|adjustSafeModeBlockTotals (int deltaSafe, int deltaTotal)
specifier|public
name|void
name|adjustSafeModeBlockTotals
parameter_list|(
name|int
name|deltaSafe
parameter_list|,
name|int
name|deltaTotal
parameter_list|)
function_decl|;
DECL|method|checkOperation (OperationCategory read)
specifier|public
name|void
name|checkOperation
parameter_list|(
name|OperationCategory
name|read
parameter_list|)
throws|throws
name|StandbyException
function_decl|;
DECL|method|isInSnapshot (BlockCollection bc)
specifier|public
name|boolean
name|isInSnapshot
parameter_list|(
name|BlockCollection
name|bc
parameter_list|)
function_decl|;
comment|/**    * Gets the ECSchema for the specified path    *     * @param src    *          - path    * @return ECSchema    * @throws IOException    */
DECL|method|getECSchemaForPath (String src)
specifier|public
name|ECSchema
name|getECSchemaForPath
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

