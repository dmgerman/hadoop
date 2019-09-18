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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|utils
operator|.
name|db
operator|.
name|TableIterator
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
comment|/**    * Store the containerID -> no. of keys count into the container DB store.    *    * @param containerID the containerID.    * @param count count of the keys within the given containerID.    * @throws IOException    */
DECL|method|storeContainerKeyCount (Long containerID, Long count)
name|void
name|storeContainerKeyCount
parameter_list|(
name|Long
name|containerID
parameter_list|,
name|Long
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Store the total count of containers into the container DB store.    *    * @param count count of the containers present in the system.    */
DECL|method|storeContainerCount (Long count)
name|void
name|storeContainerCount
parameter_list|(
name|Long
name|count
parameter_list|)
function_decl|;
comment|/**    * Get the stored key prefix count for the given containerID, key prefix.    *    * @param containerKeyPrefix the containerID, key-prefix tuple.    * @return count of keys with that prefix.    */
DECL|method|getCountForContainerKeyPrefix ( ContainerKeyPrefix containerKeyPrefix)
name|Integer
name|getCountForContainerKeyPrefix
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the total count of keys within the given containerID.    *    * @param containerID the given containerId.    * @return count of keys within the given containerID.    * @throws IOException    */
DECL|method|getKeyCountForContainer (Long containerID)
name|long
name|getKeyCountForContainer
parameter_list|(
name|Long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get if a containerID exists or not.    *    * @param containerID the given containerID.    * @return if the given ContainerID exists or not.    * @throws IOException    */
DECL|method|doesContainerExists (Long containerID)
name|boolean
name|doesContainerExists
parameter_list|(
name|Long
name|containerID
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the stored key prefixes for the given containerId.    *    * @param containerId the given containerId.    * @return Map of Key prefix -> count.    */
DECL|method|getKeyPrefixesForContainer ( long containerId)
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
comment|/**    * Get the stored key prefixes for the given containerId starting    * after the given keyPrefix.    *    * @param containerId the given containerId.    * @param prevKeyPrefix the key prefix to seek to and start scanning.    * @return Map of Key prefix -> count.    */
DECL|method|getKeyPrefixesForContainer ( long containerId, String prevKeyPrefix)
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
parameter_list|,
name|String
name|prevKeyPrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a Map of containerID, containerMetadata of Containers only for the    * given limit. If the limit is -1 or any integer<0, then return all    * the containers without any limit.    *    * @param limit the no. of containers to fetch.    * @param prevContainer containerID after which the results are returned.    * @return Map of containerID -> containerMetadata.    * @throws IOException    */
DECL|method|getContainers (int limit, long prevContainer)
name|Map
argument_list|<
name|Long
argument_list|,
name|ContainerMetadata
argument_list|>
name|getContainers
parameter_list|(
name|int
name|limit
parameter_list|,
name|long
name|prevContainer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete an entry in the container DB.    *    * @param containerKeyPrefix container key prefix to be deleted.    * @throws IOException exception.    */
DECL|method|deleteContainerMapping (ContainerKeyPrefix containerKeyPrefix)
name|void
name|deleteContainerMapping
parameter_list|(
name|ContainerKeyPrefix
name|containerKeyPrefix
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get iterator to the entire container DB.    * @return TableIterator    */
DECL|method|getContainerTableIterator ()
name|TableIterator
name|getContainerTableIterator
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the total count of containers present in the system.    *    * @return total count of containers.    * @throws IOException    */
DECL|method|getCountForContainers ()
name|long
name|getCountForContainers
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Increment the total count for containers in the system by the given count.    *    * @param count no. of new containers to add to containers total count.    */
DECL|method|incrementContainerCountBy (long count)
name|void
name|incrementContainerCountBy
parameter_list|(
name|long
name|count
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

