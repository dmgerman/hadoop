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
name|fs
operator|.
name|StorageType
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

begin_comment
comment|/**  * Ozone volume with in-memory state for testing.  */
end_comment

begin_class
DECL|class|OzoneVolumeStub
specifier|public
class|class
name|OzoneVolumeStub
extends|extends
name|OzoneVolume
block|{
DECL|field|buckets
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneBucketStub
argument_list|>
name|buckets
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|OzoneVolumeStub (String name, String admin, String owner, long quotaInBytes, long creationTime, List<OzoneAcl> acls)
specifier|public
name|OzoneVolumeStub
parameter_list|(
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
name|super
argument_list|(
name|name
argument_list|,
name|admin
argument_list|,
name|owner
argument_list|,
name|quotaInBytes
argument_list|,
name|creationTime
argument_list|,
name|acls
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|createBucket
argument_list|(
name|bucketName
argument_list|,
operator|new
name|BucketArgs
operator|.
name|Builder
argument_list|()
operator|.
name|setStorageType
argument_list|(
name|StorageType
operator|.
name|DEFAULT
argument_list|)
operator|.
name|setVersioning
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|buckets
operator|.
name|put
argument_list|(
name|bucketName
argument_list|,
operator|new
name|OzoneBucketStub
argument_list|(
name|getName
argument_list|()
argument_list|,
name|bucketName
argument_list|,
name|bucketArgs
operator|.
name|getAcls
argument_list|()
argument_list|,
name|bucketArgs
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|bucketArgs
operator|.
name|getVersioning
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|buckets
operator|.
name|containsKey
argument_list|(
name|bucketName
argument_list|)
condition|)
block|{
return|return
name|buckets
operator|.
name|get
argument_list|(
name|bucketName
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"BUCKET_NOT_FOUND"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listBuckets (String bucketPrefix)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneBucket
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|bucketPrefix
parameter_list|)
block|{
return|return
name|buckets
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|bucket
lambda|->
name|bucket
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|bucketPrefix
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
DECL|method|listBuckets (String bucketPrefix, String prevBucket)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneBucket
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|bucketPrefix
parameter_list|,
name|String
name|prevBucket
parameter_list|)
block|{
return|return
name|buckets
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|bucket
lambda|->
name|bucket
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
argument_list|)
operator|.
name|filter
argument_list|(
name|bucket
lambda|->
name|bucket
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|bucketPrefix
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
if|if
condition|(
name|buckets
operator|.
name|containsKey
argument_list|(
name|bucketName
argument_list|)
condition|)
block|{
name|buckets
operator|.
name|remove
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"BUCKET_NOT_FOUND"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

