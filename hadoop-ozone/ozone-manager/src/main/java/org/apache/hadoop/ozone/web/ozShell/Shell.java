begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.ozShell
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
name|cli
operator|.
name|GenericCli
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
name|hdds
operator|.
name|cli
operator|.
name|HddsVersionProvider
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
name|bucket
operator|.
name|CreateBucketHandler
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
name|bucket
operator|.
name|DeleteBucketHandler
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
name|bucket
operator|.
name|InfoBucketHandler
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
name|bucket
operator|.
name|ListBucketHandler
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
name|bucket
operator|.
name|UpdateBucketHandler
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
name|keys
operator|.
name|DeleteKeyHandler
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
name|keys
operator|.
name|GetKeyHandler
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
name|keys
operator|.
name|InfoKeyHandler
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
name|keys
operator|.
name|ListKeyHandler
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
name|keys
operator|.
name|PutKeyHandler
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
name|volume
operator|.
name|CreateVolumeHandler
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
name|volume
operator|.
name|DeleteVolumeHandler
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
name|volume
operator|.
name|InfoVolumeHandler
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
name|volume
operator|.
name|ListVolumeHandler
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
name|volume
operator|.
name|UpdateVolumeHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|/**  * Ozone user interface commands.  *  * This class uses dispatch method to make calls  * to appropriate handlers that execute the ozone functions.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|name
operator|=
literal|"ozone oz"
argument_list|,
name|description
operator|=
literal|"Client for the Ozone object store"
argument_list|,
name|subcommands
operator|=
block|{
name|InfoVolumeHandler
operator|.
name|class
block|,
name|ListVolumeHandler
operator|.
name|class
block|,
name|CreateVolumeHandler
operator|.
name|class
block|,
name|UpdateVolumeHandler
operator|.
name|class
block|,
name|DeleteVolumeHandler
operator|.
name|class
block|,
name|InfoBucketHandler
operator|.
name|class
block|,
name|ListBucketHandler
operator|.
name|class
block|,
name|CreateBucketHandler
operator|.
name|class
block|,
name|UpdateBucketHandler
operator|.
name|class
block|,
name|DeleteBucketHandler
operator|.
name|class
block|,
name|InfoKeyHandler
operator|.
name|class
block|,
name|ListKeyHandler
operator|.
name|class
block|,
name|PutKeyHandler
operator|.
name|class
block|,
name|GetKeyHandler
operator|.
name|class
block|,
name|DeleteKeyHandler
operator|.
name|class
block|}
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|)
DECL|class|Shell
specifier|public
class|class
name|Shell
extends|extends
name|GenericCli
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Shell
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OZONE_URI_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_URI_DESCRIPTION
init|=
literal|"Ozone URI could start "
operator|+
literal|"with o3:// or http(s):// or without prefix. REST protocol will "
operator|+
literal|"be used for http(s), RPC otherwise. URI may contain the host and port "
operator|+
literal|"of the SCM server. Both are optional. "
operator|+
literal|"If they are not specified it will be identified from "
operator|+
literal|"the config files."
decl_stmt|;
DECL|field|OZONE_VOLUME_URI_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_VOLUME_URI_DESCRIPTION
init|=
literal|"URI of the volume.\n"
operator|+
name|OZONE_URI_DESCRIPTION
decl_stmt|;
DECL|field|OZONE_BUCKET_URI_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_BUCKET_URI_DESCRIPTION
init|=
literal|"URI of the volume/bucket.\n"
operator|+
name|OZONE_URI_DESCRIPTION
decl_stmt|;
DECL|field|OZONE_KEY_URI_DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|OZONE_KEY_URI_DESCRIPTION
init|=
literal|"URI of the volume/bucket/key.\n"
operator|+
name|OZONE_URI_DESCRIPTION
decl_stmt|;
comment|// General options
DECL|field|DEFAULT_OZONE_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_OZONE_PORT
init|=
literal|50070
decl_stmt|;
comment|/**    * Main for the ozShell Command handling.    *    * @param argv - System Args Strings[]    * @throws Exception    */
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|Shell
argument_list|()
operator|.
name|run
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

