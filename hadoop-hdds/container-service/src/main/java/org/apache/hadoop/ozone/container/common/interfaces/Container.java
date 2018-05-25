begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.interfaces
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|interfaces
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|StorageContainerException
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
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|ContainerData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_comment
comment|/**  * Interface for Container Operations.  */
end_comment

begin_interface
DECL|interface|Container
specifier|public
interface|interface
name|Container
block|{
comment|/**    * Creates a container.    *    * @throws StorageContainerException    */
DECL|method|create (ContainerData containerData)
name|void
name|create
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Deletes the container.    *    * @param forceDelete   - whether this container should be deleted forcibly.    * @throws StorageContainerException    */
DECL|method|delete (boolean forceDelete)
name|void
name|delete
parameter_list|(
name|boolean
name|forceDelete
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Update the container.    *    * @param forceUpdate if true, update container forcibly.    * @throws StorageContainerException    */
DECL|method|update (boolean forceUpdate)
name|void
name|update
parameter_list|(
name|boolean
name|forceUpdate
parameter_list|)
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Get metadata about the container.    *    * @return ContainerData - Container Data.    * @throws StorageContainerException    */
DECL|method|getContainerData ()
name|ContainerData
name|getContainerData
parameter_list|()
throws|throws
name|StorageContainerException
function_decl|;
comment|/**    * Closes a open container, if it is already closed or does not exist a    * StorageContainerException is thrown.    *    * @throws StorageContainerException    */
DECL|method|close ()
name|void
name|close
parameter_list|()
throws|throws
name|StorageContainerException
throws|,
name|NoSuchAlgorithmException
function_decl|;
block|}
end_interface

end_unit

