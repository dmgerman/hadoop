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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|security
operator|.
name|acl
operator|.
name|OzoneObjInfo
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
operator|.
name|StoreType
operator|.
name|OZONE
import|;
end_import

begin_comment
comment|/**  * Add acl handler for bucket.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"addacl"
argument_list|,
name|description
operator|=
literal|"Add a new Acl."
argument_list|)
DECL|class|AddAclBucketHandler
specifier|public
class|class
name|AddAclBucketHandler
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
name|CommandLine
operator|.
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--acl"
block|,
literal|"-a"
block|}
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|description
operator|=
literal|"new acl."
operator|+
literal|"r = READ,"
operator|+
literal|"w = WRITE,"
operator|+
literal|"c = CREATE,"
operator|+
literal|"d = DELETE,"
operator|+
literal|"l = LIST,"
operator|+
literal|"a = ALL,"
operator|+
literal|"n = NONE,"
operator|+
literal|"x = READ_AC,"
operator|+
literal|"y = WRITE_AC"
operator|+
literal|"Ex user:user1:rw or group:hadoop:rw"
argument_list|)
DECL|field|acl
specifier|private
name|String
name|acl
decl_stmt|;
annotation|@
name|CommandLine
operator|.
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--store"
block|,
literal|"-s"
block|}
argument_list|,
name|required
operator|=
literal|false
argument_list|,
name|description
operator|=
literal|"store type. i.e OZONE or S3"
argument_list|)
DECL|field|storeType
specifier|private
name|String
name|storeType
decl_stmt|;
comment|/**    * Executes the Client Calls.    */
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|acl
argument_list|,
literal|"New acl to be added not specified."
argument_list|)
expr_stmt|;
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
name|OzoneObj
name|obj
init|=
name|OzoneObjInfo
operator|.
name|Builder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setBucketName
argument_list|(
name|bucketName
argument_list|)
operator|.
name|setVolumeName
argument_list|(
name|volumeName
argument_list|)
operator|.
name|setResType
argument_list|(
name|OzoneObj
operator|.
name|ResourceType
operator|.
name|BUCKET
argument_list|)
operator|.
name|setStoreType
argument_list|(
name|storeType
operator|==
literal|null
condition|?
name|OZONE
else|:
name|OzoneObj
operator|.
name|StoreType
operator|.
name|valueOf
argument_list|(
name|storeType
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|boolean
name|result
init|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|addAcl
argument_list|(
name|obj
argument_list|,
name|OzoneAcl
operator|.
name|parseAcl
argument_list|(
name|acl
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s%n"
argument_list|,
literal|"Acl added successfully: "
operator|+
name|result
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

