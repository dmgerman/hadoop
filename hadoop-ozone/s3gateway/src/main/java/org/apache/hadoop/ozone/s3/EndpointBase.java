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

begin_comment
comment|/**  * Basic helpers for all the REST endpoints.  */
end_comment

begin_class
DECL|class|EndpointBase
specifier|public
class|class
name|EndpointBase
block|{
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
name|IOException
block|{
name|OzoneBucket
name|bucket
init|=
literal|null
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
literal|"Bucket"
operator|+
name|bucketName
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

