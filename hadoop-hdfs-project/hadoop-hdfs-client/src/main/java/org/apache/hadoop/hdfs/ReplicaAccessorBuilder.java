begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * The public API for creating a new ReplicaAccessor.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ReplicaAccessorBuilder
specifier|public
specifier|abstract
class|class
name|ReplicaAccessorBuilder
block|{
comment|/**    * Set the file name which is being opened.  Provided for debugging purposes.    */
DECL|method|setFileName (String fileName)
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
name|setFileName
parameter_list|(
name|String
name|fileName
parameter_list|)
function_decl|;
comment|/** Set the block ID and block pool ID which are being opened. */
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
DECL|method|setBlock (long blockId, String blockPoolId)
name|setBlock
parameter_list|(
name|long
name|blockId
parameter_list|,
name|String
name|blockPoolId
parameter_list|)
function_decl|;
comment|/**    * Set whether checksums must be verified.  Checksums should be skipped if    * the user has disabled checksum verification in the configuration.  Users    * may wish to do this if their software does checksum verification at a    * higher level than HDFS.    */
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
DECL|method|setVerifyChecksum (boolean verifyChecksum)
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
function_decl|;
comment|/** Set the name of the HDFS client.  Provided for debugging purposes. */
DECL|method|setClientName (String clientName)
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
name|setClientName
parameter_list|(
name|String
name|clientName
parameter_list|)
function_decl|;
comment|/**    * Set whether short-circuit is enabled.  Short-circuit may be disabled if    * the user has set dfs.client.read.shortcircuit to false, or if the block    * being read is under construction.  The fact that this bit is enabled does    * not mean that the user has permission to do short-circuit reads or to    * access the replica-- that must be checked separately by the    * ReplicaAccessorBuilder implementation.    */
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
DECL|method|setAllowShortCircuitReads (boolean allowShortCircuit)
name|setAllowShortCircuitReads
parameter_list|(
name|boolean
name|allowShortCircuit
parameter_list|)
function_decl|;
comment|/**    * Set the length of the replica which is visible to this client.  If bytes    * are added later, they will not be visible to the ReplicaAccessor we are    * building.  In order to see more of the replica, the client must re-open    * this HDFS file.  The visible length provides an upper bound, but not a    * lower one.  If the replica is deleted or truncated, fewer bytes may be    * visible than specified here.    */
DECL|method|setVisibleLength (long visibleLength)
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
name|setVisibleLength
parameter_list|(
name|long
name|visibleLength
parameter_list|)
function_decl|;
comment|/**    * Set the configuration to use.  ReplicaAccessorBuilder subclasses should    * define their own configuration prefix.  For example, the foobar plugin    * could look for configuration keys like foo.bar.parameter1,    * foo.bar.parameter2.    */
DECL|method|setConfiguration (Configuration conf)
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
name|setConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
function_decl|;
comment|/**    * Set the block access token to use.    */
DECL|method|setBlockAccessToken (byte[] token)
specifier|public
specifier|abstract
name|ReplicaAccessorBuilder
name|setBlockAccessToken
parameter_list|(
name|byte
index|[]
name|token
parameter_list|)
function_decl|;
comment|/**    * Build a new ReplicaAccessor.    *    * The implementation must perform any necessary access checks before    * constructing the ReplicaAccessor.  If there is a hardware-level or    * network-level setup operation that could fail, it should be done here.  If    * the implementation returns a ReplicaAccessor, we will assume that it works    * and not attempt to construct a normal BlockReader.    *    * If the ReplicaAccessor could not be built, implementations may wish to log    * a message at TRACE level indicating why.    *    * @return    null if the ReplicaAccessor could not be built; the    *                ReplicaAccessor otherwise.    */
DECL|method|build ()
specifier|public
specifier|abstract
name|ReplicaAccessor
name|build
parameter_list|()
function_decl|;
block|}
end_class

end_unit

