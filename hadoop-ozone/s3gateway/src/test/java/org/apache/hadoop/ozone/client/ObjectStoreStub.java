begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  *  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|exceptions
operator|.
name|OMException
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_ALREADY_EXISTS
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_EMPTY
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|BUCKET_NOT_FOUND
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|S3_BUCKET_NOT_FOUND
import|;
end_import

begin_import
import|import static
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
name|exceptions
operator|.
name|OMException
operator|.
name|ResultCodes
operator|.
name|VOLUME_NOT_FOUND
import|;
end_import

begin_comment
comment|/**  * ObjectStore implementation with in-memory state.  */
end_comment

begin_class
DECL|class|ObjectStoreStub
specifier|public
class|class
name|ObjectStoreStub
extends|extends
name|ObjectStore
block|{
DECL|method|ObjectStoreStub ()
specifier|public
name|ObjectStoreStub
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|field|volumes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneVolumeStub
argument_list|>
name|volumes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|bucketVolumeMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bucketVolumeMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|bucketEmptyStatus
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|bucketEmptyStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|userBuckets
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|OzoneBucket
argument_list|>
argument_list|>
name|userBuckets
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
name|createVolume
argument_list|(
name|volumeName
argument_list|,
name|VolumeArgs
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAdmin
argument_list|(
literal|"root"
argument_list|)
operator|.
name|setOwner
argument_list|(
literal|"root"
argument_list|)
operator|.
name|setQuota
argument_list|(
literal|""
operator|+
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|setAcls
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|OzoneVolumeStub
name|volume
init|=
operator|new
name|OzoneVolumeStub
argument_list|(
name|volumeName
argument_list|,
name|volumeArgs
operator|.
name|getAdmin
argument_list|()
argument_list|,
name|volumeArgs
operator|.
name|getOwner
argument_list|()
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|volumeArgs
operator|.
name|getQuota
argument_list|()
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|volumeArgs
operator|.
name|getAcls
argument_list|()
argument_list|)
decl_stmt|;
name|volumes
operator|.
name|put
argument_list|(
name|volumeName
argument_list|,
name|volume
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|volumes
operator|.
name|containsKey
argument_list|(
name|volumeName
argument_list|)
condition|)
block|{
return|return
name|volumes
operator|.
name|get
argument_list|(
name|volumeName
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|VOLUME_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listVolumes (String volumePrefix)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
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
name|volumes
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|volumePrefix
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listVolumes (String volumePrefix, String prevVolume)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneVolume
argument_list|>
name|listVolumes
parameter_list|(
name|String
name|volumePrefix
parameter_list|,
name|String
name|prevVolume
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|volumes
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|prevVolume
argument_list|)
operator|>
literal|0
argument_list|)
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|volumePrefix
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listVolumesByUser (String user, String volumePrefix, String prevVolume)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneVolume
argument_list|>
name|listVolumesByUser
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|volumePrefix
parameter_list|,
name|String
name|prevVolume
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|volumes
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getOwner
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|prevVolume
argument_list|)
operator|<
literal|0
argument_list|)
operator|.
name|filter
argument_list|(
name|volume
lambda|->
name|volume
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|volumePrefix
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|volumes
operator|.
name|remove
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createS3Bucket (String userName, String s3BucketName)
specifier|public
name|void
name|createS3Bucket
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|volumeName
init|=
literal|"s3"
operator|+
name|userName
decl_stmt|;
if|if
condition|(
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|bucketVolumeMap
operator|.
name|put
argument_list|(
name|s3BucketName
argument_list|,
name|volumeName
operator|+
literal|"/"
operator|+
name|s3BucketName
argument_list|)
expr_stmt|;
name|bucketEmptyStatus
operator|.
name|put
argument_list|(
name|s3BucketName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|volumes
operator|.
name|get
argument_list|(
name|volumeName
argument_list|)
operator|.
name|createBucket
argument_list|(
name|s3BucketName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|BUCKET_ALREADY_EXISTS
argument_list|)
throw|;
block|}
if|if
condition|(
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|==
literal|null
condition|)
block|{
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|ozoneBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ozoneBuckets
operator|.
name|add
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
name|volumeName
argument_list|)
operator|.
name|getBucket
argument_list|(
name|s3BucketName
argument_list|)
argument_list|)
expr_stmt|;
name|userBuckets
operator|.
name|put
argument_list|(
name|userName
argument_list|,
name|ozoneBuckets
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|.
name|add
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
name|volumeName
argument_list|)
operator|.
name|getBucket
argument_list|(
name|s3BucketName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|listS3Buckets (String userName, String bucketPrefix)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneBucket
argument_list|>
name|listS3Buckets
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|)
block|{
if|if
condition|(
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|OzoneBucket
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|.
name|parallelStream
argument_list|()
operator|.
name|filter
argument_list|(
name|ozoneBucket
lambda|->
block|{
if|if
condition|(
name|bucketPrefix
operator|!=
literal|null
condition|)
block|{
return|return
name|ozoneBucket
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|bucketPrefix
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
DECL|method|listS3Buckets (String userName, String bucketPrefix, String prevBucket)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneBucket
argument_list|>
name|listS3Buckets
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|String
name|prevBucket
parameter_list|)
block|{
if|if
condition|(
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|OzoneBucket
argument_list|>
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
else|else
block|{
comment|//Sort buckets lexicographically
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|.
name|sort
argument_list|(
parameter_list|(
name|bucket1
parameter_list|,
name|bucket2
parameter_list|)
lambda|->
block|{
name|int
name|compare
init|=
name|bucket1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|bucket2
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|userBuckets
operator|.
name|get
argument_list|(
name|userName
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|ozoneBucket
lambda|->
block|{
if|if
condition|(
name|prevBucket
operator|!=
literal|null
condition|)
block|{
return|return
name|ozoneBucket
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|prevBucket
argument_list|)
operator|>
literal|0
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
operator|.
name|filter
argument_list|(
name|ozoneBucket
lambda|->
block|{
if|if
condition|(
name|bucketPrefix
operator|!=
literal|null
condition|)
block|{
return|return
name|ozoneBucket
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|bucketPrefix
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteS3Bucket (String s3BucketName)
specifier|public
name|void
name|deleteS3Bucket
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucketVolumeMap
operator|.
name|containsKey
argument_list|(
name|s3BucketName
argument_list|)
condition|)
block|{
if|if
condition|(
name|bucketEmptyStatus
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
condition|)
block|{
name|bucketVolumeMap
operator|.
name|remove
argument_list|(
name|s3BucketName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|BUCKET_NOT_EMPTY
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getOzoneBucketMapping (String s3BucketName)
specifier|public
name|String
name|getOzoneBucketMapping
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|S3_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StringSplitter"
argument_list|)
DECL|method|getOzoneVolumeName (String s3BucketName)
specifier|public
name|String
name|getOzoneVolumeName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|S3_BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"StringSplitter"
argument_list|)
DECL|method|getOzoneBucketName (String s3BucketName)
specifier|public
name|String
name|getOzoneBucketName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OMException
argument_list|(
literal|""
argument_list|,
name|BUCKET_NOT_FOUND
argument_list|)
throw|;
block|}
return|return
name|bucketVolumeMap
operator|.
name|get
argument_list|(
name|s3BucketName
argument_list|)
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
index|[
literal|1
index|]
return|;
block|}
DECL|method|setBucketEmptyStatus (String bucketName, boolean status)
specifier|public
name|void
name|setBucketEmptyStatus
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|boolean
name|status
parameter_list|)
block|{
name|bucketEmptyStatus
operator|.
name|put
argument_list|(
name|bucketName
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

