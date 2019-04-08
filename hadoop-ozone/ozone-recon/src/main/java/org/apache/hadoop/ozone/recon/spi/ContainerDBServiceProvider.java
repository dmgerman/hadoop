begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.spi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|spi
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|ozone
operator|.
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerKeyPrefix
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
name|ozone
operator|.
name|recon
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerMetadata
import|;
end_import

begin_comment
comment|/**  * The Recon Container DB Service interface.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|ContainerDBServiceProvider
specifier|public
interface|interface
name|ContainerDBServiceProvider
block|{
comment|/**    * Create new container DB and bulk Store the container to Key prefix    * mapping.    * @param containerKeyPrefixCounts Map of containerId, key-prefix tuple to    *                                 key count.    */
DECL|method|initNewContainerDB (Map<ContainerKeyPrefix, Integer> containerKeyPrefixCounts)
name|void
name|initNewContainerDB
parameter_list|(
name|Map
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|containerKeyPrefixCounts
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Store the container to Key prefix mapping into the Recon Container DB.    *    * @param containerKeyPrefix the containerId, key-prefix tuple.    * @param count              Count of Keys with that prefix.    */
DECL|method|storeContainerKeyMapping (ContainerKeyPrefix containerKeyPrefix, Integer count)
name|void
name|storeContainerKeyMapping
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|,
name|Integer
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the stored key prefix count for the given containerId, key prefix.    *    * @param containerKeyPrefix the containerId, key-prefix tuple.    * @return count of keys with that prefix.    */
DECL|method|getCountForForContainerKeyPrefix ( ContainerKeyPrefix containerKeyPrefix)
name|Integer
name|getCountForForContainerKeyPrefix
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the stored key prefixes for the given containerId.    *    * @param containerId the given containerId.    * @return Map of Key prefix -> count.    */
DECL|method|getKeyPrefixesForContainer (long containerId)
name|Map
argument_list|<
name|ContainerKeyPrefix
argument_list|,
name|Integer
argument_list|>
name|getKeyPrefixesForContainer
parameter_list|(
name|long
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a Map of containerID, containerMetadata of all Containers.    *    * @return Map of containerID -> containerMetadata.    * @throws IOException    */
DECL|method|getContainers ()
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerMetadata
argument_list|>
name|getContainers
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

