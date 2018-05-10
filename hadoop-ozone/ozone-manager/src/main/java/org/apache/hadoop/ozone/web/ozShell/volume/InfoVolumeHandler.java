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
name|rest
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

begin_comment
comment|/**  * Executes volume Info calls.  */
end_comment

begin_class
DECL|class|InfoVolumeHandler
specifier|public
class|class
name|InfoVolumeHandler
extends|extends
name|Handler
block|{
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
comment|/**    * Executes volume Info.    *    * @param cmd - CommandLine    *    * @throws IOException    * @throws OzoneException    * @throws URISyntaxException    */
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
name|INFO_VOLUME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"Incorrect call : infoVolume is missing"
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
name|INFO_VOLUME
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
name|ozoneURI
operator|.
name|getPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"Volume name is required to get info of a volume"
argument_list|)
throw|;
block|}
comment|// we need to skip the slash in the URI path
name|volumeName
operator|=
name|ozoneURI
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|OzoneClientUtils
operator|.
name|asVolumeInfo
argument_list|(
name|vol
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

