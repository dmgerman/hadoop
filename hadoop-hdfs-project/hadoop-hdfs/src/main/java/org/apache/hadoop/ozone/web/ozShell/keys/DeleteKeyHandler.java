begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.keys
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
name|keys
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|web
operator|.
name|client
operator|.
name|OzoneRestClientException
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
name|exceptions
operator|.
name|OzoneException
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  * Executes Delete Key.  */
end_comment

begin_class
DECL|class|DeleteKeyHandler
specifier|public
class|class
name|DeleteKeyHandler
extends|extends
name|Handler
block|{
DECL|field|userName
specifier|private
name|String
name|userName
decl_stmt|;
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|keyName
specifier|private
name|String
name|keyName
decl_stmt|;
comment|/**    * Executes the Client Calls.    *    * @param cmd - CommandLine    * @throws IOException    * @throws OzoneException    * @throws URISyntaxException    */
annotation|@
name|Override
DECL|method|execute (CommandLine cmd)
specifier|protected
name|void
name|execute
parameter_list|(
name|CommandLine
name|cmd
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|DELETE_KEY
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Incorrect call : deleteKey is missing"
argument_list|)
throw|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|USER
argument_list|)
condition|)
block|{
name|userName
operator|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|Shell
operator|.
name|USER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userName
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
expr_stmt|;
block|}
name|String
name|ozoneURIString
init|=
name|cmd
operator|.
name|getOptionValue
argument_list|(
name|Shell
operator|.
name|DELETE_KEY
argument_list|)
decl_stmt|;
name|URI
name|ozoneURI
init|=
name|verifyURI
argument_list|(
name|ozoneURIString
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
literal|3
condition|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"volume/bucket/key name required in deleteKey"
argument_list|)
throw|;
block|}
name|volumeName
operator|=
name|path
operator|.
name|getName
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|bucketName
operator|=
name|path
operator|.
name|getName
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|keyName
operator|=
name|path
operator|.
name|getName
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|VERBOSE
argument_list|)
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
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"Key Name : %s%n"
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|setEndPointURI
argument_list|(
name|ozoneURI
argument_list|)
expr_stmt|;
name|client
operator|.
name|setUserAuth
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|vol
init|=
name|client
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|OzoneBucket
name|bucket
init|=
name|vol
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
decl_stmt|;
name|bucket
operator|.
name|deleteKey
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

