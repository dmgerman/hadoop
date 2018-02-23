begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
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
name|annotations
operator|.
name|VisibleForTesting
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
name|CommandLineParser
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
name|GnuParser
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
name|commons
operator|.
name|cli
operator|.
name|ParseException
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|PathFilter
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
name|fs
operator|.
name|permission
operator|.
name|FsAction
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|mapred
operator|.
name|JobConf
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_comment
comment|/**  * This is a child program designed to be used by the {@link HadoopArchiveLogs}  * tool via the Distributed Shell.  It's not meant to be run directly.  */
end_comment

begin_class
DECL|class|HadoopArchiveLogsRunner
specifier|public
class|class
name|HadoopArchiveLogsRunner
implements|implements
name|Tool
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HadoopArchiveLogsRunner
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|APP_ID_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|APP_ID_OPTION
init|=
literal|"appId"
decl_stmt|;
DECL|field|USER_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|USER_OPTION
init|=
literal|"user"
decl_stmt|;
DECL|field|WORKING_DIR_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|WORKING_DIR_OPTION
init|=
literal|"workingDir"
decl_stmt|;
DECL|field|REMOTE_ROOT_LOG_DIR_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|REMOTE_ROOT_LOG_DIR_OPTION
init|=
literal|"remoteRootLogDir"
decl_stmt|;
DECL|field|SUFFIX_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|SUFFIX_OPTION
init|=
literal|"suffix"
decl_stmt|;
DECL|field|NO_PROXY_OPTION
specifier|private
specifier|static
specifier|final
name|String
name|NO_PROXY_OPTION
init|=
literal|"noProxy"
decl_stmt|;
DECL|field|appId
specifier|private
name|String
name|appId
decl_stmt|;
DECL|field|user
specifier|private
name|String
name|user
decl_stmt|;
DECL|field|workingDir
specifier|private
name|String
name|workingDir
decl_stmt|;
DECL|field|remoteLogDir
specifier|private
name|String
name|remoteLogDir
decl_stmt|;
DECL|field|suffix
specifier|private
name|String
name|suffix
decl_stmt|;
DECL|field|proxy
specifier|private
name|boolean
name|proxy
decl_stmt|;
DECL|field|conf
specifier|private
name|JobConf
name|conf
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|hadoopArchives
name|HadoopArchives
name|hadoopArchives
decl_stmt|;
DECL|field|HAR_DIR_PERM
specifier|private
specifier|static
specifier|final
name|FsPermission
name|HAR_DIR_PERM
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|READ_EXECUTE
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
DECL|field|HAR_INNER_FILES_PERM
specifier|private
specifier|static
specifier|final
name|FsPermission
name|HAR_INNER_FILES_PERM
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|NONE
argument_list|)
decl_stmt|;
DECL|method|HadoopArchiveLogsRunner (Configuration conf)
specifier|public
name|HadoopArchiveLogsRunner
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|hadoopArchives
operator|=
operator|new
name|HadoopArchives
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|HadoopArchiveLogsRunner
operator|.
name|class
argument_list|)
decl_stmt|;
name|HadoopArchiveLogsRunner
name|halr
init|=
operator|new
name|HadoopArchiveLogsRunner
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
try|try
block|{
name|ret
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|halr
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|s
init|=
name|e
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
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
name|ret
argument_list|)
expr_stmt|;
block|}
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
name|handleOpts
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Integer
name|exitCode
init|=
literal|1
decl_stmt|;
name|UserGroupInformation
name|loginUser
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
comment|// If we're running as the user, then no need to impersonate
comment|// (which might fail if user is not a proxyuser for themselves)
comment|// Also if !proxy is set
if|if
condition|(
operator|!
name|proxy
operator|||
name|loginUser
operator|.
name|getShortUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Running as "
operator|+
name|user
argument_list|)
expr_stmt|;
name|exitCode
operator|=
name|runInternal
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise impersonate user.  If we're not allowed to, then this will
comment|// fail with an Exception
name|LOG
operator|.
name|info
argument_list|(
literal|"Running as "
operator|+
name|loginUser
operator|.
name|getShortUserName
argument_list|()
operator|+
literal|" but will "
operator|+
literal|"impersonate "
operator|+
name|user
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|proxyUser
init|=
name|UserGroupInformation
operator|.
name|createProxyUser
argument_list|(
name|user
argument_list|,
name|loginUser
argument_list|)
decl_stmt|;
name|exitCode
operator|=
name|proxyUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|runInternal
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|exitCode
return|;
block|}
DECL|method|runInternal ()
specifier|private
name|int
name|runInternal
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|remoteAppLogDir
init|=
name|remoteLogDir
operator|+
name|File
operator|.
name|separator
operator|+
name|user
operator|+
name|File
operator|.
name|separator
operator|+
name|suffix
operator|+
name|File
operator|.
name|separator
operator|+
name|appId
decl_stmt|;
comment|// Run 'hadoop archives' command in local mode
name|conf
operator|.
name|set
argument_list|(
literal|"mapreduce.framework.name"
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
comment|// Set the umask so we get 640 files and 750 dirs
name|conf
operator|.
name|set
argument_list|(
literal|"fs.permissions.umask-mode"
argument_list|,
literal|"027"
argument_list|)
expr_stmt|;
name|String
name|harName
init|=
name|appId
operator|+
literal|".har"
decl_stmt|;
name|String
index|[]
name|haArgs
init|=
block|{
literal|"-archiveName"
block|,
name|harName
block|,
literal|"-p"
block|,
name|remoteAppLogDir
block|,
literal|"*"
block|,
name|workingDir
block|}
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Executing 'hadoop archives'"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|haArg
range|:
name|haArgs
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
operator|.
name|append
argument_list|(
name|haArg
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|exitCode
init|=
name|hadoopArchives
operator|.
name|run
argument_list|(
name|haArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|exitCode
operator|!=
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to create archives for "
operator|+
name|appId
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
comment|// Move har file to correct location and delete original logs
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Path
name|harPath
init|=
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
name|harName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|harPath
argument_list|)
operator|||
name|fs
operator|.
name|listStatus
argument_list|(
name|harPath
argument_list|)
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The created archive \""
operator|+
name|harName
operator|+
literal|"\" is missing or empty."
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|Path
name|harDest
init|=
operator|new
name|Path
argument_list|(
name|remoteAppLogDir
argument_list|,
name|harName
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Moving har to original location"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|harPath
argument_list|,
name|harDest
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting original logs"
argument_list|)
expr_stmt|;
for|for
control|(
name|FileStatus
name|original
range|:
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|remoteAppLogDir
argument_list|)
argument_list|,
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|!
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".har"
argument_list|)
return|;
block|}
block|}
argument_list|)
control|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|original
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|handleOpts (String[] args)
specifier|private
name|void
name|handleOpts
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
block|{
name|Options
name|opts
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|Option
name|appIdOpt
init|=
operator|new
name|Option
argument_list|(
name|APP_ID_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Application ID"
argument_list|)
decl_stmt|;
name|appIdOpt
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Option
name|userOpt
init|=
operator|new
name|Option
argument_list|(
name|USER_OPTION
argument_list|,
literal|true
argument_list|,
literal|"User"
argument_list|)
decl_stmt|;
name|userOpt
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Option
name|workingDirOpt
init|=
operator|new
name|Option
argument_list|(
name|WORKING_DIR_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Working Directory"
argument_list|)
decl_stmt|;
name|workingDirOpt
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Option
name|remoteLogDirOpt
init|=
operator|new
name|Option
argument_list|(
name|REMOTE_ROOT_LOG_DIR_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Remote Root Log Directory"
argument_list|)
decl_stmt|;
name|remoteLogDirOpt
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Option
name|suffixOpt
init|=
operator|new
name|Option
argument_list|(
name|SUFFIX_OPTION
argument_list|,
literal|true
argument_list|,
literal|"Suffix"
argument_list|)
decl_stmt|;
name|suffixOpt
operator|.
name|setRequired
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Option
name|useProxyOpt
init|=
operator|new
name|Option
argument_list|(
name|NO_PROXY_OPTION
argument_list|,
literal|false
argument_list|,
literal|"Use Proxy"
argument_list|)
decl_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|appIdOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|userOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|workingDirOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|remoteLogDirOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|suffixOpt
argument_list|)
expr_stmt|;
name|opts
operator|.
name|addOption
argument_list|(
name|useProxyOpt
argument_list|)
expr_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|commandLine
init|=
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|appId
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|APP_ID_OPTION
argument_list|)
expr_stmt|;
name|user
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|USER_OPTION
argument_list|)
expr_stmt|;
name|workingDir
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|WORKING_DIR_OPTION
argument_list|)
expr_stmt|;
name|remoteLogDir
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|REMOTE_ROOT_LOG_DIR_OPTION
argument_list|)
expr_stmt|;
name|suffix
operator|=
name|commandLine
operator|.
name|getOptionValue
argument_list|(
name|SUFFIX_OPTION
argument_list|)
expr_stmt|;
name|proxy
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|commandLine
operator|.
name|hasOption
argument_list|(
name|NO_PROXY_OPTION
argument_list|)
condition|)
block|{
name|proxy
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|instanceof
name|JobConf
condition|)
block|{
name|this
operator|.
name|conf
operator|=
operator|(
name|JobConf
operator|)
name|conf
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|,
name|HadoopArchiveLogsRunner
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
block|}
end_class

end_unit

