begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.bucket
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|ozShell
operator|.
name|bucket
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|OzoneClientException
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
name|OzoneClientUtils
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
name|web
operator|.
name|ozShell
operator|.
name|Handler
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
name|web
operator|.
name|ozShell
operator|.
name|Shell
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
name|web
operator|.
name|utils
operator|.
name|JsonUtils
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Parameters
import|;
end_import

begin_comment
comment|/**  * create bucket handler.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"create"
argument_list|,
name|description
operator|=
literal|"creates a bucket in a given volume"
argument_list|)
DECL|class|CreateBucketHandler
specifier|public
class|class
name|CreateBucketHandler
extends|extends
name|Handler
block|{
annotation|@
name|Parameters
argument_list|(
name|arity
operator|=
literal|"1..1"
argument_list|,
name|description
operator|=
name|Shell
operator|.
name|OZONE_BUCKET_URI_DESCRIPTION
argument_list|)
DECL|field|uri
specifier|private
name|String
name|uri
decl_stmt|;
comment|/**    * Executes create bucket.    */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|ozoneURI
init|=
name|verifyURI
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
name|ozoneURI
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|getNameCount
argument_list|()
operator|<
literal|2
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"volume and bucket name required in createBucket"
argument_list|)
throw|;
block|}
name|String
name|volumeName
init|=
name|path
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|path
operator|.
name|getName
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|isVerbose
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Volume Name : %s%n"
argument_list|,
name|volumeName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Bucket Name : %s%n"
argument_list|,
name|bucketName
argument_list|)
expr_stmt|;
block|}
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
if|if
condition|(
name|isVerbose
argument_list|()
condition|)
block|{
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
name|JsonUtils
operator|.
name|toJsonStringWithDefaultPrettyPrinter
argument_list|(
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|OzoneClientUtils
operator|.
name|asBucketInfo
argument_list|(
name|bucket
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

