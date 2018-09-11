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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|rest
operator|.
name|response
operator|.
name|VolumeInfo
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"list"
argument_list|,
name|aliases
operator|=
literal|"ls"
argument_list|,
name|description
operator|=
literal|"List the volumes of a given user"
argument_list|)
DECL|class|ListVolumeHandler
specifier|public
class|class
name|ListVolumeHandler
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
name|OZONE_VOLUME_URI_DESCRIPTION
argument_list|,
name|defaultValue
operator|=
literal|"/"
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
literal|"--length"
block|,
literal|"-l"
block|}
argument_list|,
name|description
operator|=
literal|"Limit of the max results"
argument_list|,
name|defaultValue
operator|=
literal|"100"
argument_list|)
DECL|field|maxVolumes
specifier|private
name|int
name|maxVolumes
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--start"
block|,
literal|"-s"
block|}
argument_list|,
name|description
operator|=
literal|"The first volume to start the listing"
argument_list|)
DECL|field|startVolume
specifier|private
name|String
name|startVolume
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--prefix"
block|,
literal|"-p"
block|}
argument_list|,
name|description
operator|=
literal|"Prefix to filter the volumes"
argument_list|)
DECL|field|prefix
specifier|private
name|String
name|prefix
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"--user"
block|,
literal|"-u"
block|}
argument_list|,
name|description
operator|=
literal|"Owner of the volumes to list."
argument_list|)
DECL|field|userName
specifier|private
name|String
name|userName
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
name|URI
name|ozoneURI
init|=
name|verifyURI
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|ozoneURI
operator|.
name|getPath
argument_list|()
argument_list|)
operator|&&
operator|!
name|ozoneURI
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|OzoneClientException
argument_list|(
literal|"Invalid URI: "
operator|+
name|ozoneURI
operator|+
literal|" . Specified path not used."
operator|+
name|ozoneURI
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|userName
operator|==
literal|null
condition|)
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
if|if
condition|(
name|maxVolumes
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the length should be a positive number"
argument_list|)
throw|;
block|}
name|Iterator
argument_list|<
name|OzoneVolume
argument_list|>
name|volumeIterator
decl_stmt|;
if|if
condition|(
name|userName
operator|!=
literal|null
condition|)
block|{
name|volumeIterator
operator|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|listVolumesByUser
argument_list|(
name|userName
argument_list|,
name|prefix
argument_list|,
name|startVolume
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|volumeIterator
operator|=
name|client
operator|.
name|getObjectStore
argument_list|()
operator|.
name|listVolumes
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumeInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|maxVolumes
operator|>
literal|0
operator|&&
name|volumeIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|VolumeInfo
name|volume
init|=
name|OzoneClientUtils
operator|.
name|asVolumeInfo
argument_list|(
name|volumeIterator
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|volumeInfos
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|maxVolumes
operator|-=
literal|1
expr_stmt|;
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
literal|"Found : %d volumes for user : %s "
argument_list|,
name|volumeInfos
operator|.
name|size
argument_list|()
argument_list|,
name|userName
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|JsonUtils
operator|.
name|toJsonStringWithDefaultPrettyPrinter
argument_list|(
name|JsonUtils
operator|.
name|toJsonString
argument_list|(
name|volumeInfos
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

