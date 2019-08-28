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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
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
name|client
operator|.
name|BucketArgs
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
name|ObjectPrinter
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
name|OzoneAddress
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
name|Option
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
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--bucketkey"
block|,
literal|"-k"
block|}
argument_list|,
name|description
operator|=
literal|"bucket encryption key name"
argument_list|)
DECL|field|bekName
specifier|private
name|String
name|bekName
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
name|OzoneAddress
name|address
init|=
operator|new
name|OzoneAddress
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|address
operator|.
name|ensureBucketAddress
argument_list|()
expr_stmt|;
name|OzoneClient
name|client
init|=
name|address
operator|.
name|createClient
argument_list|(
name|createOzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|volumeName
init|=
name|address
operator|.
name|getVolumeName
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|address
operator|.
name|getBucketName
argument_list|()
decl_stmt|;
name|BucketArgs
operator|.
name|Builder
name|bb
init|=
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
decl_stmt|;
if|if
condition|(
name|bekName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|bekName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|bb
operator|.
name|setBucketEncryptionKey
argument_list|(
name|bekName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket encryption key name must "
operator|+
literal|"be specified to enable bucket encryption!"
argument_list|)
throw|;
block|}
block|}
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
if|if
condition|(
name|bekName
operator|!=
literal|null
condition|)
block|{
name|bb
operator|.
name|setBucketEncryptionKey
argument_list|(
name|bekName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Bucket Encryption enabled with Key Name: %s%n"
argument_list|,
name|bekName
argument_list|)
expr_stmt|;
block|}
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
argument_list|,
name|bb
operator|.
name|build
argument_list|()
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
name|ObjectPrinter
operator|.
name|printObjectAsJson
argument_list|(
name|bucket
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

