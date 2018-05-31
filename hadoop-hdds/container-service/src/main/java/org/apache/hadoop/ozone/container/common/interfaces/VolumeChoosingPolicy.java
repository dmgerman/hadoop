begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
operator|.
name|VolumeInfo
import|;
end_import

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
name|List
import|;
end_import

begin_comment
comment|/**  * This interface specifies the policy for choosing volumes to store replicas.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|VolumeChoosingPolicy
specifier|public
interface|interface
name|VolumeChoosingPolicy
block|{
comment|/**    * Choose a volume to place a container,    * given a list of volumes and the max container size sought for storage.    *    * The implementations of this interface must be thread-safe.    *    * @param volumes - a list of available volumes.    * @param maxContainerSize - the maximum size of the container for which a    *                         volume is sought.    * @return the chosen volume.    * @throws IOException when disks are unavailable or are full.    */
DECL|method|chooseVolume (List<VolumeInfo> volumes, long maxContainerSize)
name|VolumeInfo
name|chooseVolume
parameter_list|(
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumes
parameter_list|,
name|long
name|maxContainerSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

