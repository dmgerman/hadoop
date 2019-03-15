begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.s3
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
name|s3
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
name|conf
operator|.
name|OzoneConfiguration
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
name|security
operator|.
name|UserGroupInformation
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConfigKeys
operator|.
name|OZONE_SECURITY_ENABLED_KEY
import|;
end_import

begin_comment
comment|/**  * Executes getsecret calls.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"getsecret"
argument_list|,
name|description
operator|=
literal|"Returns s3 secret for current user"
argument_list|)
DECL|class|GetS3SecretHandler
specifier|public
class|class
name|GetS3SecretHandler
extends|extends
name|Handler
block|{
DECL|field|OZONE_GETS3SECRET_ERROR
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_GETS3SECRET_ERROR
init|=
literal|"This command is not"
operator|+
literal|" supported in unsecure clusters."
decl_stmt|;
comment|/**    * Executes getS3Secret.    */
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
name|OzoneConfiguration
name|ozoneConfiguration
init|=
name|createOzoneConfiguration
argument_list|()
decl_stmt|;
name|OzoneClient
name|client
init|=
operator|new
name|OzoneAddress
argument_list|()
operator|.
name|createClient
argument_list|(
name|ozoneConfiguration
argument_list|)
decl_stmt|;
comment|// getS3Secret works only with secured clusters
if|if
condition|(
name|ozoneConfiguration
operator|.
name|getBoolean
argument_list|(
name|OZONE_SECURITY_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|getS3Secret
argument_list|(
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// log a warning message for unsecured cluster
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|OZONE_GETS3SECRET_ERROR
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

