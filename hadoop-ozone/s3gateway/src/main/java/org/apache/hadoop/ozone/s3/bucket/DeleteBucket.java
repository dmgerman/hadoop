begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3.bucket
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
name|bucket
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
name|DELETE
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
name|PathParam
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
name|Produces
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
name|MediaType
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
name|s3
operator|.
name|EndpointBase
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
name|http
operator|.
name|HttpStatus
import|;
end_import

begin_comment
comment|/**  * Delete a bucket.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/{bucket}"
argument_list|)
DECL|class|DeleteBucket
specifier|public
class|class
name|DeleteBucket
extends|extends
name|EndpointBase
block|{
annotation|@
name|DELETE
annotation|@
name|Produces
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
DECL|method|delete (@athParamR) String bucketName)
specifier|public
name|Response
name|delete
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"bucket"
argument_list|)
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
throws|,
name|OS3Exception
block|{
try|try
block|{
name|deleteS3Bucket
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
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"BUCKET_NOT_EMPTY"
argument_list|)
condition|)
block|{
name|OS3Exception
name|os3Exception
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|BUCKET_NOT_EMPTY
argument_list|,
name|S3ErrorTable
operator|.
name|Resource
operator|.
name|BUCKET
argument_list|)
decl_stmt|;
throw|throw
name|os3Exception
throw|;
block|}
elseif|else
if|if
condition|(
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"BUCKET_NOT_FOUND"
argument_list|)
condition|)
block|{
name|OS3Exception
name|os3Exception
init|=
name|S3ErrorTable
operator|.
name|newError
argument_list|(
name|S3ErrorTable
operator|.
name|NO_SUCH_BUCKET
argument_list|,
name|S3ErrorTable
operator|.
name|Resource
operator|.
name|BUCKET
argument_list|)
decl_stmt|;
throw|throw
name|os3Exception
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
name|Response
operator|.
name|status
argument_list|(
name|HttpStatus
operator|.
name|SC_NO_CONTENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

