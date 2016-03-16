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
name|commons
operator|.
name|cli
operator|.
name|BasicParser
import|;
end_import

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
name|commons
operator|.
name|cli
operator|.
name|HelpFormatter
import|;
end_import

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
name|Option
import|;
end_import

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
name|Options
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
name|conf
operator|.
name|Configuration
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
name|conf
operator|.
name|Configured
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
name|util
operator|.
name|Tool
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
name|util
operator|.
name|ToolRunner
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
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * Ozone user interface commands.  *  * This class uses dispatch method to make calls  * to appropriate handlers that execute the ozone functions.  */
end_comment

begin_class
DECL|class|Shell
specifier|public
class|class
name|Shell
extends|extends
name|Configured
implements|implements
name|Tool
block|{
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
DECL|field|VERBOSE
specifier|public
specifier|static
specifier|final
name|String
name|VERBOSE
init|=
literal|"v"
decl_stmt|;
comment|// volume related command line arguments
DECL|field|RUNAS
specifier|public
specifier|static
specifier|final
name|String
name|RUNAS
init|=
literal|"root"
decl_stmt|;
DECL|field|USER
specifier|public
specifier|static
specifier|final
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
DECL|field|OWNER
specifier|public
specifier|static
specifier|final
name|String
name|OWNER
init|=
literal|"owner"
decl_stmt|;
DECL|field|QUOTA
specifier|public
specifier|static
specifier|final
name|String
name|QUOTA
init|=
literal|"quota"
decl_stmt|;
DECL|field|CREATE_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_VOLUME
init|=
literal|"createVolume"
decl_stmt|;
DECL|field|UPDATE_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_VOLUME
init|=
literal|"updateVolume"
decl_stmt|;
DECL|field|DELETE_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_VOLUME
init|=
literal|"deleteVolume"
decl_stmt|;
DECL|field|LIST_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|LIST_VOLUME
init|=
literal|"listVolume"
decl_stmt|;
DECL|field|INFO_VOLUME
specifier|public
specifier|static
specifier|final
name|String
name|INFO_VOLUME
init|=
literal|"infoVolume"
decl_stmt|;
comment|// bucket related command line arguments
DECL|field|CREATE_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|CREATE_BUCKET
init|=
literal|"createBucket"
decl_stmt|;
DECL|field|UPDATE_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|UPDATE_BUCKET
init|=
literal|"updateBucket"
decl_stmt|;
DECL|field|DELETE_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|DELETE_BUCKET
init|=
literal|"deleteBucket"
decl_stmt|;
DECL|field|LIST_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|LIST_BUCKET
init|=
literal|"listBucket"
decl_stmt|;
DECL|field|INFO_BUCKET
specifier|public
specifier|static
specifier|final
name|String
name|INFO_BUCKET
init|=
literal|"infoBucket"
decl_stmt|;
DECL|field|ADD_ACLS
specifier|public
specifier|static
specifier|final
name|String
name|ADD_ACLS
init|=
literal|"addAcl"
decl_stmt|;
DECL|field|REMOVE_ACLS
specifier|public
specifier|static
specifier|final
name|String
name|REMOVE_ACLS
init|=
literal|"removeAcl"
decl_stmt|;
comment|/**    * Execute the command with the given arguments.    *    * @param args command specific arguments.    *    * @return exit code.    *    * @throws Exception    */
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Options
name|opts
init|=
name|getOpts
argument_list|()
decl_stmt|;
name|CommandLine
name|cmd
init|=
name|parseArgs
argument_list|(
name|args
argument_list|,
name|opts
argument_list|)
decl_stmt|;
return|return
name|dispatch
argument_list|(
name|cmd
argument_list|,
name|opts
argument_list|)
return|;
block|}
comment|/**    * Construct an ozShell.    */
DECL|method|Shell ()
specifier|public
name|Shell
parameter_list|()
block|{   }
comment|/**    * returns the Command Line Options.    *    * @return Options    */
DECL|method|getOpts ()
specifier|private
name|Options
name|getOpts
parameter_list|()
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|addVolumeCommands
argument_list|(
name|opts
argument_list|)
expr_stmt|;
name|addBucketCommands
argument_list|(
name|opts
argument_list|)
expr_stmt|;
return|return
name|opts
return|;
block|}
comment|/**    * This function parses all command line arguments    * and returns the appropriate values.    *    * @param argv - Argv from main    *    * @return CommandLine    */
DECL|method|parseArgs (String[] argv, Options opts)
specifier|private
name|CommandLine
name|parseArgs
parameter_list|(
name|String
index|[]
name|argv
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
block|{
name|BasicParser
name|parser
init|=
operator|new
name|BasicParser
argument_list|()
decl_stmt|;
return|return
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|argv
argument_list|)
return|;
block|}
comment|/**    * All volume related commands are added in this function for the command    * parser.    *    * @param options - Command Options class.    */
DECL|method|addVolumeCommands (Options options)
specifier|private
name|void
name|addVolumeCommands
parameter_list|(
name|Options
name|options
parameter_list|)
block|{
name|Option
name|verbose
init|=
operator|new
name|Option
argument_list|(
name|VERBOSE
argument_list|,
literal|false
argument_list|,
literal|"verbose information output."
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|verbose
argument_list|)
expr_stmt|;
name|Option
name|runas
init|=
operator|new
name|Option
argument_list|(
name|RUNAS
argument_list|,
literal|false
argument_list|,
literal|"Run the command as \"hdfs\" user"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|runas
argument_list|)
expr_stmt|;
name|Option
name|userName
init|=
operator|new
name|Option
argument_list|(
name|USER
argument_list|,
literal|true
argument_list|,
literal|"Name of the user in volume management "
operator|+
literal|"functions"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|Option
name|quota
init|=
operator|new
name|Option
argument_list|(
name|QUOTA
argument_list|,
literal|true
argument_list|,
literal|"Quota for the volume. E.g. 10TB"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|quota
argument_list|)
expr_stmt|;
name|Option
name|createVolume
init|=
operator|new
name|Option
argument_list|(
name|CREATE_VOLUME
argument_list|,
literal|true
argument_list|,
literal|"creates a volume"
operator|+
literal|"for the specified user.\n \t For example : hdfs oz  -createVolume "
operator|+
literal|"<volumeURI> -root -user<userName>\n"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|createVolume
argument_list|)
expr_stmt|;
name|Option
name|deleteVolume
init|=
operator|new
name|Option
argument_list|(
name|DELETE_VOLUME
argument_list|,
literal|true
argument_list|,
literal|"deletes a volume"
operator|+
literal|"if it is empty.\n \t For example : hdfs oz -deleteVolume<volumeURI>"
operator|+
literal|" -root \n"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|deleteVolume
argument_list|)
expr_stmt|;
name|Option
name|listVolume
init|=
operator|new
name|Option
argument_list|(
name|LIST_VOLUME
argument_list|,
literal|true
argument_list|,
literal|"List the volumes of a given user.\n"
operator|+
literal|"For example : hdfs oz -listVolume<ozoneURI>"
operator|+
literal|"-user<username> -root or hdfs oz "
operator|+
literal|"-listVolume"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|listVolume
argument_list|)
expr_stmt|;
name|Option
name|updateVolume
init|=
operator|new
name|Option
argument_list|(
name|UPDATE_VOLUME
argument_list|,
literal|true
argument_list|,
literal|"updates an existing volume.\n"
operator|+
literal|"\t For example : hdfs oz "
operator|+
literal|"-updateVolume<volumeURI> -quota "
operator|+
literal|"100TB\n"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|updateVolume
argument_list|)
expr_stmt|;
name|Option
name|infoVolume
init|=
operator|new
name|Option
argument_list|(
name|INFO_VOLUME
argument_list|,
literal|true
argument_list|,
literal|"returns information about a specific "
operator|+
literal|"volume."
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|infoVolume
argument_list|)
expr_stmt|;
block|}
comment|/**    * All bucket related commands for ozone.    *    * @param opts - Options    */
DECL|method|addBucketCommands (Options opts)
specifier|private
name|void
name|addBucketCommands
parameter_list|(
name|Options
name|opts
parameter_list|)
block|{
name|Option
name|createBucket
init|=
operator|new
name|Option
argument_list|(
name|CREATE_BUCKET
argument_list|,
literal|true
argument_list|,
literal|"creates a bucket in a given volume.\n"
operator|+
literal|"\t For example : hdfs oz "
operator|+
literal|"-createBucket "
operator|+
literal|"<volumeName/bucketName>"
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|createBucket
argument_list|)
expr_stmt|;
name|Option
name|infoBucket
init|=
operator|new
name|Option
argument_list|(
name|INFO_BUCKET
argument_list|,
literal|true
argument_list|,
literal|"returns information about a bucket."
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|infoBucket
argument_list|)
expr_stmt|;
name|Option
name|deleteBucket
init|=
operator|new
name|Option
argument_list|(
name|DELETE_BUCKET
argument_list|,
literal|true
argument_list|,
literal|"deletes an empty bucket."
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|deleteBucket
argument_list|)
expr_stmt|;
name|Option
name|listBucket
init|=
operator|new
name|Option
argument_list|(
name|LIST_BUCKET
argument_list|,
literal|true
argument_list|,
literal|"Lists the buckets in a volume."
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|listBucket
argument_list|)
expr_stmt|;
block|}
comment|/**    * Main for the ozShell Command handling.    *    * @param argv - System Args Strings[]    *    * @throws Exception    */
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
name|Shell
name|shell
init|=
operator|new
name|Shell
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|shell
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
try|try
block|{
name|res
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|shell
argument_list|,
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dispatches calls to the right command Handler classes.    *    * @param cmd - CommandLine    *    * @throws IOException    * @throws OzoneException    * @throws URISyntaxException    */
DECL|method|dispatch (CommandLine cmd, Options opts)
specifier|private
name|int
name|dispatch
parameter_list|(
name|CommandLine
name|cmd
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
throws|,
name|URISyntaxException
block|{
name|Handler
name|handler
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|eightyColumn
init|=
literal|80
decl_stmt|;
try|try
block|{
comment|// volume functions
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|CREATE_VOLUME
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|CreateVolumeHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|DELETE_VOLUME
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|DeleteVolumeHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
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
name|handler
operator|=
operator|new
name|ListVolumeHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|UPDATE_VOLUME
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|UpdateVolumeHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
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
name|handler
operator|=
operator|new
name|InfoVolumeHandler
argument_list|()
expr_stmt|;
block|}
comment|// bucket functions
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|CREATE_BUCKET
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|CreateBucketHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|DELETE_BUCKET
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|DeleteBucketHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|INFO_BUCKET
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|InfoBucketHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cmd
operator|.
name|hasOption
argument_list|(
name|Shell
operator|.
name|LIST_BUCKET
argument_list|)
condition|)
block|{
name|handler
operator|=
operator|new
name|ListBucketHandler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|handler
operator|.
name|execute
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
else|else
block|{
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
name|eightyColumn
argument_list|,
literal|"hdfs oz -command uri [args]"
argument_list|,
literal|"Ozone Commands"
argument_list|,
name|opts
argument_list|,
literal|"Please correct your command and try again."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OzoneException
decl||
name|URISyntaxException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|printf
argument_list|(
literal|"Command Failed : %s%n"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

