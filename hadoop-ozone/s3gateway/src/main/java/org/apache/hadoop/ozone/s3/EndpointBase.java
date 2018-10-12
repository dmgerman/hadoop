begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|NotFoundException
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
name|OzoneBucket
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
name|OzoneClient
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
name|OzoneVolume
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
name|s3
operator|.
name|exception
operator|.
name|OS3Exception
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
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
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
name|s3
operator|.
name|exception
operator|.
name|S3ErrorTable
operator|.
name|Resource
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
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Basic helpers for all the REST endpoints.  */
end_comment

begin_class
DECL|class|EndpointBase
specifier|public
class|class
name|EndpointBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EndpointBase
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Inject
DECL|field|client
specifier|private
name|OzoneClient
name|client
decl_stmt|;
DECL|method|getBucket (String volumeName, String bucketName)
specifier|protected
name|OzoneBucket
name|getBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getVolume
argument_list|(
name|volumeName
argument_list|)
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
return|;
block|}
DECL|method|getBucket (OzoneVolume volume, String bucketName)
specifier|protected
name|OzoneBucket
name|getBucket
parameter_list|(
name|OzoneVolume
name|volume
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|OzoneBucket
name|bucket
decl_stmt|;
try|try
block|{
name|bucket
operator|=
name|volume
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error occurred is {}"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOT_FOUND"
argument_list|)
condition|)
block|{
name|OS3Exception
name|oex
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_BUCKET
argument_list|,
name|Resource
operator|.
name|BUCKET
argument_list|)
decl_stmt|;
throw|throw
name|oex
throw|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
return|return
name|bucket
return|;
block|}
DECL|method|getVolume (String volumeName)
specifier|protected
name|OzoneVolume
name|getVolume
parameter_list|(
name|String
name|volumeName
parameter_list|)
throws|throws
name|IOException
block|{
name|OzoneVolume
name|volume
init|=
literal|null
decl_stmt|;
try|try
block|{
name|volume
operator|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"NOT_FOUND"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Volume "
operator|+
name|volumeName
operator|+
literal|" is not found"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
return|return
name|volume
return|;
block|}
comment|/**    * Create an S3Bucket, and also it creates mapping needed to access via    * ozone and S3.    * @param userName    * @param bucketName    * @return location of the S3Bucket.    * @throws IOException    */
DECL|method|createS3Bucket (String userName, String bucketName)
specifier|protected
name|String
name|createS3Bucket
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|createS3Bucket
argument_list|(
name|userName
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"createS3Bucket error:"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ALREADY_EXISTS"
argument_list|)
condition|)
block|{
comment|// S3 does not return error for bucket already exists, it just
comment|// returns the location.
throw|throw
name|ex
throw|;
block|}
block|}
comment|// Not required to call as bucketname is same, but calling now in future
comment|// if mapping changes we get right location.
name|String
name|location
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getOzoneBucketName
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
return|return
literal|"/"
operator|+
name|location
return|;
block|}
comment|/**    * Deletes an s3 bucket and removes mapping of Ozone volume/bucket.    * @param s3BucketName - S3 Bucket Name.    * @throws  IOException in case the bucket cannot be deleted.    */
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
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|deleteS3Bucket
argument_list|(
name|s3BucketName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the Ozone Namespace for the S3Bucket. It will return the    * OzoneVolume/OzoneBucketName.    * @param s3BucketName  - S3 Bucket Name.    * @return String - The Ozone canonical name for this s3 bucket. This    * string is useful for mounting an OzoneFS.    * @throws IOException - Error is throw if the s3bucket does not exist.    */
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
return|return
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getOzoneBucketMapping
argument_list|(
name|s3BucketName
argument_list|)
return|;
block|}
comment|/**    * Returns the corresponding Ozone volume given an S3 Bucket.    * @param s3BucketName - S3Bucket Name.    * @return String - Ozone Volume name.    * @throws IOException - Throws if the s3Bucket does not exist.    */
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
return|return
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getOzoneVolumeName
argument_list|(
name|s3BucketName
argument_list|)
return|;
block|}
comment|/**    * Returns the corresponding Ozone bucket name for the given S3 bucket.    * @param s3BucketName - S3Bucket Name.    * @return String - Ozone bucket Name.    * @throws IOException - Throws if the s3bucket does not exist.    */
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
return|return
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getOzoneBucketName
argument_list|(
name|s3BucketName
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setClient (OzoneClient ozoneClient)
specifier|public
name|void
name|setClient
parameter_list|(
name|OzoneClient
name|ozoneClient
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|ozoneClient
expr_stmt|;
block|}
block|}
end_class

end_unit

