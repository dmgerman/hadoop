begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|client
operator|.
name|protocol
operator|.
name|ClientProtocol
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
name|UserGroupInformation
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
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * ObjectStore class is responsible for the client operations that can be  * performed on Ozone Object Store.  */
end_comment

begin_class
DECL|class|ObjectStore
specifier|public
class|class
name|ObjectStore
block|{
comment|/**    * The proxy used for connecting to the cluster and perform    * client operations.    */
DECL|field|proxy
specifier|private
specifier|final
name|ClientProtocol
name|proxy
decl_stmt|;
comment|/**    * Cache size to be used for listVolume calls.    */
DECL|field|listCacheSize
specifier|private
name|int
name|listCacheSize
decl_stmt|;
comment|/**    * Creates an instance of ObjectStore.    * @param conf Configuration object.    * @param proxy ClientProtocol proxy.    */
DECL|method|ObjectStore (Configuration conf, ClientProtocol proxy)
specifier|public
name|ObjectStore
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ClientProtocol
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|this
operator|.
name|listCacheSize
operator|=
name|OzoneClientUtils
operator|.
name|getListCacheSize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates the volume with default values.    * @param volumeName Name of the volume to be created.    * @throws IOException    */
DECL|method|createVolume (String volumeName)
specifier|public
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates the volume.    * @param volumeName Name of the volume to be created.    * @param volumeArgs Volume properties.    * @throws IOException    */
DECL|method|createVolume (String volumeName, VolumeArgs volumeArgs)
specifier|public
name|void
name|createVolume
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|VolumeArgs
name|volumeArgs
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeArgs
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the volume information.    * @param volumeName Name of the volume.    * @return OzoneVolume    * @throws IOException    */
DECL|method|getVolume (String volumeName)
specifier|public
name|OzoneVolume
name|getVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|volume
init|=
name|proxy
operator|.
name|getVolumeDetails
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
return|return
name|volume
return|;
block|}
comment|/**    * Returns Iterator to iterate over all the volumes in object store.    * The result can be restricted using volume prefix, will return all    * volumes if volume prefix is null.    *    * @param volumePrefix Volume prefix to match    * @return {@code Iterator<OzoneVolume>}    */
DECL|method|listVolumes (String volumePrefix)
specifier|public
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|volumePrefix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|VolumeIterator
argument_list|(
name|volumePrefix
argument_list|)
return|;
block|}
comment|/**    * Returns Iterator to iterate over the List of volumes owned by a specific    * user. The result can be restricted using volume prefix, will return all    * volumes if volume prefix is null. If user is null, returns the volume of    * current user.    *    * @param user User Name    * @param volumePrefix Volume prefix to match    * @return {@code Iterator<OzoneVolume>}    */
DECL|method|listVolumes (String user, String volumePrefix)
specifier|public
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|volumePrefix
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|user
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|VolumeIterator
argument_list|(
name|user
argument_list|,
name|volumePrefix
argument_list|)
return|;
block|}
comment|/**    * Deletes the volume.    * @param volumeName Name of the volume.    * @throws IOException    */
DECL|method|deleteVolume (String volumeName)
specifier|public
name|void
name|deleteVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|deleteVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
comment|/**    * An Iterator to iterate over {@link OzoneVolume} list.    */
DECL|class|VolumeIterator
specifier|private
class|class
name|VolumeIterator
implements|implements
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
block|{
DECL|field|user
specifier|private
name|String
name|user
init|=
literal|null
decl_stmt|;
DECL|field|volPrefix
specifier|private
name|String
name|volPrefix
init|=
literal|null
decl_stmt|;
DECL|field|currentIterator
specifier|private
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|currentIterator
decl_stmt|;
DECL|field|currentValue
specifier|private
name|OzoneVolume
name|currentValue
decl_stmt|;
comment|/**      * Creates an Iterator to iterate over all volumes in the cluster,      * which matches the volume prefix.      * @param volPrefix prefix to match      */
DECL|method|VolumeIterator (String volPrefix)
name|VolumeIterator
parameter_list|(
name|String
name|volPrefix
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|volPrefix
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an Iterator to iterate over all volumes of the user,      * which matches volume prefix.      * @param user user name      * @param volPrefix volume prefix to match      */
DECL|method|VolumeIterator (String user, String volPrefix)
name|VolumeIterator
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|volPrefix
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|volPrefix
operator|=
name|volPrefix
expr_stmt|;
name|this
operator|.
name|currentValue
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|currentIterator
operator|=
name|getNextListOfVolumes
argument_list|(
literal|null
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|currentIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|currentIterator
operator|=
name|getNextListOfVolumes
argument_list|(
name|currentValue
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
return|return
name|currentIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|OzoneVolume
name|next
parameter_list|()
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
name|currentValue
operator|=
name|currentIterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|currentValue
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
comment|/**      * Returns the next set of volume list using proxy.      * @param prevVolume previous volume, this will be excluded from the result      * @return {@code List<OzoneVolume>}      */
DECL|method|getNextListOfVolumes (String prevVolume)
specifier|private
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|getNextListOfVolumes
parameter_list|(
name|String
name|prevVolume
parameter_list|)
block|{
try|try
block|{
comment|//if user is null, we do list of all volumes.
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
return|return
name|proxy
operator|.
name|listVolumes
argument_list|(
name|user
argument_list|,
name|volPrefix
argument_list|,
name|prevVolume
argument_list|,
name|listCacheSize
argument_list|)
return|;
block|}
return|return
name|proxy
operator|.
name|listVolumes
argument_list|(
name|volPrefix
argument_list|,
name|prevVolume
argument_list|,
name|listCacheSize
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

