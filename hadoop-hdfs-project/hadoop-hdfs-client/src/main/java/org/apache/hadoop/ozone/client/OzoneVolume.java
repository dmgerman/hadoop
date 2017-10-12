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
name|OzoneAcl
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
comment|/**  * A class that encapsulates OzoneVolume.  */
end_comment

begin_class
DECL|class|OzoneVolume
specifier|public
class|class
name|OzoneVolume
block|{
comment|/**    * The proxy used for connecting to the cluster and perform    * client operations.    */
DECL|field|proxy
specifier|private
specifier|final
name|ClientProtocol
name|proxy
decl_stmt|;
comment|/**    * Name of the Volume.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Admin Name of the Volume.    */
DECL|field|admin
specifier|private
name|String
name|admin
decl_stmt|;
comment|/**    * Owner of the Volume.    */
DECL|field|owner
specifier|private
name|String
name|owner
decl_stmt|;
comment|/**    * Quota allocated for the Volume.    */
DECL|field|quotaInBytes
specifier|private
name|long
name|quotaInBytes
decl_stmt|;
comment|/**    * Creation time of the volume.    */
DECL|field|creationTime
specifier|private
name|long
name|creationTime
decl_stmt|;
comment|/**    * Volume ACLs.    */
DECL|field|acls
specifier|private
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
decl_stmt|;
DECL|field|listCacheSize
specifier|private
name|int
name|listCacheSize
decl_stmt|;
comment|/**    * Constructs OzoneVolume instance.    * @param conf Configuration object.    * @param proxy ClientProtocol proxy.    * @param name Name of the volume.    * @param admin Volume admin.    * @param owner Volume owner.    * @param quotaInBytes Volume quota in bytes.    * @param creationTime creation time of the volume    * @param acls ACLs associated with the volume.    */
DECL|method|OzoneVolume (Configuration conf, ClientProtocol proxy, String name, String admin, String owner, long quotaInBytes, long creationTime, List<OzoneAcl> acls)
specifier|public
name|OzoneVolume
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ClientProtocol
name|proxy
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|admin
parameter_list|,
name|String
name|owner
parameter_list|,
name|long
name|quotaInBytes
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
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
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|admin
operator|=
name|admin
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|quotaInBytes
operator|=
name|quotaInBytes
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|acls
operator|=
name|acls
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
comment|/**    * Returns Volume name.    *    * @return volumeName    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Returns Volume's admin name.    *    * @return adminName    */
DECL|method|getAdmin ()
specifier|public
name|String
name|getAdmin
parameter_list|()
block|{
return|return
name|admin
return|;
block|}
comment|/**    * Returns Volume's owner name.    *    * @return ownerName    */
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**    * Returns Quota allocated for the Volume in bytes.    *    * @return quotaInBytes    */
DECL|method|getQuota ()
specifier|public
name|long
name|getQuota
parameter_list|()
block|{
return|return
name|quotaInBytes
return|;
block|}
comment|/**    * Returns creation time of the volume.    *    * @return creation time.    */
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/**    * Returns OzoneAcl list associated with the Volume.    *    * @return aclMap    */
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|acls
return|;
block|}
comment|/**    * Sets/Changes the owner of this Volume.    * @param owner new owner    * @throws IOException    */
DECL|method|setOwner (String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setVolumeOwner
argument_list|(
name|name
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
comment|/**    * Sets/Changes the quota of this Volume.    * @param quota new quota    * @throws IOException    */
DECL|method|setQuota (OzoneQuota quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|OzoneQuota
name|quota
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|quota
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|setVolumeQuota
argument_list|(
name|name
argument_list|,
name|quota
argument_list|)
expr_stmt|;
name|this
operator|.
name|quotaInBytes
operator|=
name|quota
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a new Bucket in this Volume, with default values.    * @param bucketName Name of the Bucket    * @throws IOException    */
DECL|method|createBucket (String bucketName)
specifier|public
name|void
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|createBucket
argument_list|(
name|name
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new Bucket in this Volume, with properties set in bucketArgs.    * @param bucketName Name of the Bucket    * @param bucketArgs Properties to be set    * @throws IOException    */
DECL|method|createBucket (String bucketName, BucketArgs bucketArgs)
specifier|public
name|void
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|BucketArgs
name|bucketArgs
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketArgs
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|createBucket
argument_list|(
name|name
argument_list|,
name|bucketName
argument_list|,
name|bucketArgs
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the Bucket from this Volume.    * @param bucketName Name of the Bucket    * @return OzoneBucket    * @throws IOException    */
DECL|method|getBucket (String bucketName)
specifier|public
name|OzoneBucket
name|getBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|OzoneBucket
name|bucket
init|=
name|proxy
operator|.
name|getBucketDetails
argument_list|(
name|name
argument_list|,
name|bucketName
argument_list|)
decl_stmt|;
return|return
name|bucket
return|;
block|}
comment|/**    * Returns Iterator to iterate over all buckets in the volume.    * The result can be restricted using bucket prefix, will return all    * buckets if bucket prefix is null.    *    * @param bucketPrefix Bucket prefix to match    * @return {@code Iterator<OzoneBucket>}    */
DECL|method|listBuckets (String bucketPrefix)
specifier|public
name|Iterator
argument_list|<
name|OzoneBucket
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|bucketPrefix
parameter_list|)
block|{
return|return
operator|new
name|BucketIterator
argument_list|(
name|bucketPrefix
argument_list|)
return|;
block|}
comment|/**    * Deletes the Bucket from this Volume.    * @param bucketName Name of the Bucket    * @throws IOException    */
DECL|method|deleteBucket (String bucketName)
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proxy
argument_list|,
literal|"Client proxy is not set."
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|OzoneClientUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|deleteBucket
argument_list|(
name|name
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
comment|/**    * An Iterator to iterate over {@link OzoneBucket} list.    */
DECL|class|BucketIterator
specifier|private
class|class
name|BucketIterator
implements|implements
name|Iterator
argument_list|<
name|OzoneBucket
argument_list|>
block|{
DECL|field|bucketPrefix
specifier|private
name|String
name|bucketPrefix
init|=
literal|null
decl_stmt|;
DECL|field|currentIterator
specifier|private
name|Iterator
argument_list|<
name|OzoneBucket
argument_list|>
name|currentIterator
decl_stmt|;
DECL|field|currentValue
specifier|private
name|OzoneBucket
name|currentValue
decl_stmt|;
comment|/**      * Creates an Iterator to iterate over all buckets in the volume,      * which matches volume prefix.      * @param bucketPrefix      */
DECL|method|BucketIterator (String bucketPrefix)
name|BucketIterator
parameter_list|(
name|String
name|bucketPrefix
parameter_list|)
block|{
name|this
operator|.
name|bucketPrefix
operator|=
name|bucketPrefix
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
name|getNextListOfBuckets
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
name|getNextListOfBuckets
argument_list|(
name|currentValue
operator|!=
literal|null
condition|?
name|currentValue
operator|.
name|getName
argument_list|()
else|:
literal|null
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
name|OzoneBucket
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
comment|/**      * Gets the next set of bucket list using proxy.      * @param prevBucket      * @return {@code List<OzoneVolume>}      */
DECL|method|getNextListOfBuckets (String prevBucket)
specifier|private
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|getNextListOfBuckets
parameter_list|(
name|String
name|prevBucket
parameter_list|)
block|{
try|try
block|{
return|return
name|proxy
operator|.
name|listBuckets
argument_list|(
name|name
argument_list|,
name|bucketPrefix
argument_list|,
name|prevBucket
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

