begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|ozone
operator|.
name|om
operator|.
name|helpers
operator|.
name|OmDeleteVolumeResponse
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
name|om
operator|.
name|helpers
operator|.
name|OmVolumeArgs
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
name|om
operator|.
name|helpers
operator|.
name|OmVolumeOwnerChangeResponse
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|OzoneAclInfo
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|VolumeList
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
comment|/**  * OM volume manager interface.  */
end_comment

begin_interface
DECL|interface|VolumeManager
specifier|public
interface|interface
name|VolumeManager
extends|extends
name|IOzoneAcl
block|{
comment|/**    * Create a new volume.    * @param args - Volume args to create a volume    */
DECL|method|createVolume (OmVolumeArgs args)
name|VolumeList
name|createVolume
parameter_list|(
name|OmVolumeArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Create Volume changes to OM DB.    * @param omVolumeArgs    * @param volumeList    * @throws IOException    */
DECL|method|applyCreateVolume (OmVolumeArgs omVolumeArgs, VolumeList volumeList)
name|void
name|applyCreateVolume
parameter_list|(
name|OmVolumeArgs
name|omVolumeArgs
parameter_list|,
name|VolumeList
name|volumeList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Changes the owner of a volume.    *    * @param volume - Name of the volume.    * @param owner - Name of the owner.    * @throws IOException    */
DECL|method|setOwner (String volume, String owner)
name|OmVolumeOwnerChangeResponse
name|setOwner
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Set Owner changes to OM DB.    * @param oldOwner    * @param oldOwnerVolumeList    * @param newOwnerVolumeList    * @param newOwnerVolumeArgs    * @throws IOException    */
DECL|method|applySetOwner (String oldOwner, VolumeList oldOwnerVolumeList, VolumeList newOwnerVolumeList, OmVolumeArgs newOwnerVolumeArgs)
name|void
name|applySetOwner
parameter_list|(
name|String
name|oldOwner
parameter_list|,
name|VolumeList
name|oldOwnerVolumeList
parameter_list|,
name|VolumeList
name|newOwnerVolumeList
parameter_list|,
name|OmVolumeArgs
name|newOwnerVolumeArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Changes the Quota on a volume.    *    * @param volume - Name of the volume.    * @param quota - Quota in bytes.    * @throws IOException    */
DECL|method|setQuota (String volume, long quota)
name|OmVolumeArgs
name|setQuota
parameter_list|(
name|String
name|volume
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Set Quota changes to OM DB.    * @param omVolumeArgs    * @throws IOException    */
DECL|method|applySetQuota (OmVolumeArgs omVolumeArgs)
name|void
name|applySetQuota
parameter_list|(
name|OmVolumeArgs
name|omVolumeArgs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the volume information.    * @param volume - Volume name.    * @return VolumeArgs or exception is thrown.    * @throws IOException    */
DECL|method|getVolumeInfo (String volume)
name|OmVolumeArgs
name|getVolumeInfo
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing empty volume.    *    * @param volume - Name of the volume.    * @throws IOException    */
DECL|method|deleteVolume (String volume)
name|OmDeleteVolumeResponse
name|deleteVolume
parameter_list|(
name|String
name|volume
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Apply Delete Volume changes to OM DB.    * @param volume    * @param owner    * @param newVolumeList    * @throws IOException    */
DECL|method|applyDeleteVolume (String volume, String owner, VolumeList newVolumeList)
name|void
name|applyDeleteVolume
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|owner
parameter_list|,
name|VolumeList
name|newVolumeList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if the specified user with a role can access this volume.    *    * @param volume - volume    * @param userAcl - user acl which needs to be checked for access    * @return true if the user has access for the volume, false otherwise    * @throws IOException    */
DECL|method|checkVolumeAccess (String volume, OzoneAclInfo userAcl)
name|boolean
name|checkVolumeAccess
parameter_list|(
name|String
name|volume
parameter_list|,
name|OzoneAclInfo
name|userAcl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of volumes owned by a given user; if user is null,    * returns all volumes.    *    * @param userName    *   volume owner    * @param prefix    *   the volume prefix used to filter the listing result.    * @param startKey    *   the start volume name determines where to start listing from,    *   this key is excluded from the result.    * @param maxKeys    *   the maximum number of volumes to return.    * @return a list of {@link OmVolumeArgs}    * @throws IOException    */
DECL|method|listVolumes (String userName, String prefix, String startKey, int maxKeys)
name|List
argument_list|<
name|OmVolumeArgs
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|startKey
parameter_list|,
name|int
name|maxKeys
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

