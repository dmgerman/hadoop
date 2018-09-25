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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationFactor
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
name|client
operator|.
name|ReplicationType
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
name|io
operator|.
name|OzoneInputStream
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
name|io
operator|.
name|OzoneOutputStream
import|;
end_import

begin_comment
comment|/**  * In-memory ozone bucket for testing.  */
end_comment

begin_class
DECL|class|OzoneBucketStub
specifier|public
class|class
name|OzoneBucketStub
extends|extends
name|OzoneBucket
block|{
DECL|field|keyDetails
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|OzoneKeyDetails
argument_list|>
name|keyDetails
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|keyContents
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|keyContents
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Constructs OzoneBucket instance.    *    * @param volumeName   Name of the volume the bucket belongs to.    * @param bucketName   Name of the bucket.    * @param acls         ACLs associated with the bucket.    * @param storageType  StorageType of the bucket.    * @param versioning   versioning status of the bucket.    * @param creationTime creation time of the bucket.    */
DECL|method|OzoneBucketStub ( String volumeName, String bucketName, List<OzoneAcl> acls, StorageType storageType, Boolean versioning, long creationTime)
specifier|public
name|OzoneBucketStub
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|Boolean
name|versioning
parameter_list|,
name|long
name|creationTime
parameter_list|)
block|{
name|super
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|acls
argument_list|,
name|storageType
argument_list|,
name|versioning
argument_list|,
name|creationTime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createKey (String key, long size)
specifier|public
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createKey
argument_list|(
name|key
argument_list|,
name|size
argument_list|,
name|ReplicationType
operator|.
name|STAND_ALONE
argument_list|,
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createKey (String key, long size, ReplicationType type, ReplicationFactor factor)
specifier|public
name|OzoneOutputStream
name|createKey
parameter_list|(
name|String
name|key
parameter_list|,
name|long
name|size
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
operator|(
name|int
operator|)
name|size
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|keyContents
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|keyDetails
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|new
name|OzoneKeyDetails
argument_list|(
name|getVolumeName
argument_list|()
argument_list|,
name|getName
argument_list|()
argument_list|,
name|key
argument_list|,
name|size
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|OzoneOutputStream
argument_list|(
name|byteArrayOutputStream
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readKey (String key)
specifier|public
name|OzoneInputStream
name|readKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|OzoneInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|keyContents
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey (String key)
specifier|public
name|OzoneKeyDetails
name|getKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|keyDetails
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listKeys (String keyPrefix)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneKey
argument_list|>
name|listKeys
parameter_list|(
name|String
name|keyPrefix
parameter_list|)
block|{
return|return
name|keyDetails
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|key
lambda|->
name|key
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|keyPrefix
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
DECL|method|listKeys (String keyPrefix, String prevKey)
specifier|public
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneKey
argument_list|>
name|listKeys
parameter_list|(
name|String
name|keyPrefix
parameter_list|,
name|String
name|prevKey
parameter_list|)
block|{
return|return
name|keyDetails
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|key
lambda|->
name|key
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|prevKey
argument_list|)
operator|>
literal|0
argument_list|)
operator|.
name|filter
argument_list|(
name|key
lambda|->
name|key
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|keyPrefix
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
DECL|method|deleteKey (String key)
specifier|public
name|void
name|deleteKey
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|keyDetails
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameKey (String fromKeyName, String toKeyName)
specifier|public
name|void
name|renameKey
parameter_list|(
name|String
name|fromKeyName
parameter_list|,
name|String
name|toKeyName
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

