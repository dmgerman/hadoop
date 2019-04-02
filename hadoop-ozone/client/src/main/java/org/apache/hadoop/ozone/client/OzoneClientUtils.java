begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|OzoneQuota
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
name|scm
operator|.
name|client
operator|.
name|HddsClientUtils
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerNotOpenException
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicies
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
name|io
operator|.
name|retry
operator|.
name|RetryPolicy
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
name|OzoneConsts
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
name|rest
operator|.
name|response
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|GroupMismatchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|protocol
operator|.
name|RaftRetryFailureException
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_comment
comment|/** A utility class for OzoneClient. */
end_comment

begin_class
DECL|class|OzoneClientUtils
specifier|public
specifier|final
class|class
name|OzoneClientUtils
block|{
DECL|method|OzoneClientUtils ()
specifier|private
name|OzoneClientUtils
parameter_list|()
block|{}
DECL|field|EXCEPTION_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|>
name|EXCEPTION_LIST
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
name|TimeoutException
operator|.
name|class
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|ContainerNotOpenException
operator|.
name|class
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|RaftRetryFailureException
operator|.
name|class
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|AlreadyClosedException
operator|.
name|class
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|GroupMismatchException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
comment|/**    * Returns a BucketInfo object constructed using fields of the input    * OzoneBucket object.    *    * @param bucket OzoneBucket instance from which BucketInfo object needs to    *               be created.    * @return BucketInfo instance    */
DECL|method|asBucketInfo (OzoneBucket bucket)
specifier|public
specifier|static
name|BucketInfo
name|asBucketInfo
parameter_list|(
name|OzoneBucket
name|bucket
parameter_list|)
block|{
name|BucketInfo
name|bucketInfo
init|=
operator|new
name|BucketInfo
argument_list|(
name|bucket
operator|.
name|getVolumeName
argument_list|()
argument_list|,
name|bucket
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|bucketInfo
operator|.
name|setCreatedOn
argument_list|(
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|bucket
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bucketInfo
operator|.
name|setStorageType
argument_list|(
name|bucket
operator|.
name|getStorageType
argument_list|()
argument_list|)
expr_stmt|;
name|bucketInfo
operator|.
name|setVersioning
argument_list|(
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|getVersioning
argument_list|(
name|bucket
operator|.
name|getVersioning
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bucketInfo
operator|.
name|setAcls
argument_list|(
name|bucket
operator|.
name|getAcls
argument_list|()
argument_list|)
expr_stmt|;
name|bucketInfo
operator|.
name|setEncryptionKeyName
argument_list|(
name|bucket
operator|.
name|getEncryptionKeyName
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|bucket
operator|.
name|getEncryptionKeyName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bucketInfo
return|;
block|}
comment|/**    * Returns a VolumeInfo object constructed using fields of the input    * OzoneVolume object.    *    * @param volume OzoneVolume instance from which VolumeInfo object needs to    *               be created.    * @return VolumeInfo instance    */
DECL|method|asVolumeInfo (OzoneVolume volume)
specifier|public
specifier|static
name|VolumeInfo
name|asVolumeInfo
parameter_list|(
name|OzoneVolume
name|volume
parameter_list|)
block|{
name|VolumeInfo
name|volumeInfo
init|=
operator|new
name|VolumeInfo
argument_list|(
name|volume
operator|.
name|getName
argument_list|()
argument_list|,
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|volume
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|,
name|volume
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
name|volumeInfo
operator|.
name|setQuota
argument_list|(
name|OzoneQuota
operator|.
name|getOzoneQuota
argument_list|(
name|volume
operator|.
name|getQuota
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|volumeInfo
operator|.
name|setOwner
argument_list|(
operator|new
name|VolumeOwner
argument_list|(
name|volume
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|volumeInfo
return|;
block|}
comment|/**    * Returns a KeyInfo object constructed using fields of the input    * OzoneKey object.    *    * @param key OzoneKey instance from which KeyInfo object needs to    *            be created.    * @return KeyInfo instance    */
DECL|method|asKeyInfo (OzoneKey key)
specifier|public
specifier|static
name|KeyInfo
name|asKeyInfo
parameter_list|(
name|OzoneKey
name|key
parameter_list|)
block|{
name|KeyInfo
name|keyInfo
init|=
operator|new
name|KeyInfo
argument_list|()
decl_stmt|;
name|keyInfo
operator|.
name|setKeyName
argument_list|(
name|key
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setCreatedOn
argument_list|(
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|key
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setModifiedOn
argument_list|(
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|key
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setSize
argument_list|(
name|key
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|keyInfo
return|;
block|}
comment|/**    * Returns a KeyInfoDetails object constructed using fields of the input    * OzoneKeyDetails object.    *    * @param key OzoneKeyDetails instance from which KeyInfo object needs to    *            be created.    * @return KeyInfoDetails instance    */
DECL|method|asKeyInfoDetails (OzoneKeyDetails key)
specifier|public
specifier|static
name|KeyInfoDetails
name|asKeyInfoDetails
parameter_list|(
name|OzoneKeyDetails
name|key
parameter_list|)
block|{
name|KeyInfoDetails
name|keyInfo
init|=
operator|new
name|KeyInfoDetails
argument_list|()
decl_stmt|;
name|keyInfo
operator|.
name|setKeyName
argument_list|(
name|key
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setCreatedOn
argument_list|(
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|key
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setModifiedOn
argument_list|(
name|HddsClientUtils
operator|.
name|formatDateTime
argument_list|(
name|key
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setSize
argument_list|(
name|key
operator|.
name|getDataSize
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|KeyLocation
argument_list|>
name|keyLocations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|key
operator|.
name|getOzoneKeyLocations
argument_list|()
operator|.
name|forEach
argument_list|(
parameter_list|(
name|a
parameter_list|)
lambda|->
name|keyLocations
operator|.
name|add
argument_list|(
operator|new
name|KeyLocation
argument_list|(
name|a
operator|.
name|getContainerID
argument_list|()
argument_list|,
name|a
operator|.
name|getLocalID
argument_list|()
argument_list|,
name|a
operator|.
name|getLength
argument_list|()
argument_list|,
name|a
operator|.
name|getOffset
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setKeyLocation
argument_list|(
name|keyLocations
argument_list|)
expr_stmt|;
name|keyInfo
operator|.
name|setFileEncryptionInfo
argument_list|(
name|key
operator|.
name|getFileEncryptionInfo
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|keyInfo
return|;
block|}
DECL|method|createRetryPolicy (int maxRetryCount)
specifier|public
specifier|static
name|RetryPolicy
name|createRetryPolicy
parameter_list|(
name|int
name|maxRetryCount
parameter_list|)
block|{
comment|// just retry without sleep
name|RetryPolicy
name|retryPolicy
init|=
name|RetryPolicies
operator|.
name|retryUpToMaximumCountWithFixedSleep
argument_list|(
name|maxRetryCount
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
return|return
name|retryPolicy
return|;
block|}
DECL|method|getExceptionList ()
specifier|public
specifier|static
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
argument_list|>
name|getExceptionList
parameter_list|()
block|{
return|return
name|EXCEPTION_LIST
return|;
block|}
block|}
end_class

end_unit

