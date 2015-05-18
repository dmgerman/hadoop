begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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

begin_comment
comment|/**  * A storage policy specifies the placement of block replicas on specific  * storage types.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|BlockStoragePolicySpi
specifier|public
interface|interface
name|BlockStoragePolicySpi
block|{
comment|/**    * Return the name of the storage policy. Policies are uniquely    * identified by name.    *    * @return the name of the storage policy.    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Return the preferred storage types associated with this policy. These    * storage types are used sequentially for successive block replicas.    *    * @return preferred storage types used for placing block replicas.    */
DECL|method|getStorageTypes ()
name|StorageType
index|[]
name|getStorageTypes
parameter_list|()
function_decl|;
comment|/**    * Get the fallback storage types for creating new block replicas. Fallback    * storage types are used if the preferred storage types are not available.    *    * @return fallback storage types for new block replicas..    */
DECL|method|getCreationFallbacks ()
name|StorageType
index|[]
name|getCreationFallbacks
parameter_list|()
function_decl|;
comment|/**    * Get the fallback storage types for replicating existing block replicas.    * Fallback storage types are used if the preferred storage types are not    * available.    *    * @return fallback storage types for replicating existing block replicas.    */
DECL|method|getReplicationFallbacks ()
name|StorageType
index|[]
name|getReplicationFallbacks
parameter_list|()
function_decl|;
comment|/**    * Returns true if the policy is inherit-only and cannot be changed for    * an existing file.    *    * @return true if the policy is inherit-only.    */
DECL|method|isCopyOnCreateFile ()
name|boolean
name|isCopyOnCreateFile
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

