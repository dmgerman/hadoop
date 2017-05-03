begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell.volume
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
name|volume
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Executes List Volume call.  */
end_comment

begin_class
DECL|class|ListVolumeHandler
specifier|public
class|class
name|ListVolumeHandler
extends|extends
name|Handler
block|{
DECL|field|rootName
specifier|private
name|String
name|rootName
decl_stmt|;
DECL|field|userName
specifier|private
name|String
name|userName
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
name|LIST_VOLUME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"Incorrect call : listVolume is missing"
argument_list|)
throw|;
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
name|LIST_VOLUME
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
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|RUNAS
argument_list|)
condition|)
block|{
name|rootName
operator|=
literal|"hdfs"
expr_stmt|;
block|}
if|if
condition|(
operator|!
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
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"User name is needed in listVolume call."
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
name|client
operator|.
name|setEndPointURI
argument_list|(
name|ozoneURI
argument_list|)
expr_stmt|;
if|if
condition|(
name|rootName
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|setUserAuth
argument_list|(
name|rootName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|client
operator|.
name|setUserAuth
argument_list|(
name|userName
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|OzoneVolume
argument_list|>
name|volumes
init|=
name|client
operator|.
name|listVolumes
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|volumes
operator|!=
literal|null
condition|)
block|{
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
literal|"Found : %d volumes for user : %s %n"
argument_list|,
name|volumes
operator|.
name|size
argument_list|()
argument_list|,
name|userName
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|OzoneVolume
name|vol
range|:
name|volumes
control|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%s%n"
argument_list|,
name|JsonUtils
operator|.
name|toJsonStringWithDefaultPrettyPrinter
argument_list|(
name|vol
operator|.
name|getJsonString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

