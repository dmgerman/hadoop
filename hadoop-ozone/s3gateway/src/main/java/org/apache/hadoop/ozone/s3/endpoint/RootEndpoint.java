begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.endpoint
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
operator|.
name|endpoint
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
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
name|Path
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
name|core
operator|.
name|Response
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
name|core
operator|.
name|Response
operator|.
name|Status
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
name|time
operator|.
name|Instant
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
name|commontypes
operator|.
name|BucketMetadata
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
name|header
operator|.
name|AuthenticationHeaderParser
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
name|s3
operator|.
name|util
operator|.
name|OzoneS3Util
operator|.
name|getVolumeName
import|;
end_import

begin_comment
comment|/**  * Top level rest endpoint.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/"
argument_list|)
DECL|class|RootEndpoint
specifier|public
class|class
name|RootEndpoint
extends|extends
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
name|RootEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Rest endpoint to list all the buckets of the current user.    *    * See https://docs.aws.amazon.com/AmazonS3/latest/API/RESTServiceGET.html    * for more details.    */
annotation|@
name|GET
DECL|method|get ()
specifier|public
name|Response
name|get
parameter_list|()
throws|throws
name|OS3Exception
throws|,
name|IOException
block|{
name|OzoneVolume
name|volume
decl_stmt|;
name|ListBucketResponse
name|response
init|=
operator|new
name|ListBucketResponse
argument_list|()
decl_stmt|;
name|AuthenticationHeaderParser
name|authenticationHeaderParser
init|=
name|getAuthenticationHeaderParser
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|authenticationHeaderParser
operator|.
name|doesAuthenticationInfoExists
argument_list|()
condition|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|TEMPORARY_REDIRECT
argument_list|)
operator|.
name|header
argument_list|(
literal|"Location"
argument_list|,
literal|"/static/"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|String
name|volumeName
init|=
name|getVolumeName
argument_list|(
name|authenticationHeaderParser
operator|.
name|getAccessKeyID
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|OzoneBucket
argument_list|>
name|bucketIterator
init|=
name|listS3Buckets
argument_list|(
name|volumeName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|bucketIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|OzoneBucket
name|next
init|=
name|bucketIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|BucketMetadata
name|bucketMetadata
init|=
operator|new
name|BucketMetadata
argument_list|()
decl_stmt|;
name|bucketMetadata
operator|.
name|setName
argument_list|(
name|next
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|bucketMetadata
operator|.
name|setCreationDate
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|next
operator|.
name|getCreationTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|addBucket
argument_list|(
name|bucketMetadata
argument_list|)
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|ok
argument_list|(
name|response
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

