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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|*
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
name|tools
operator|.
name|DistCpOptions
operator|.
name|FileAttribute
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The OptionsParser parses out the command-line options passed to DistCp,  * and interprets those specific to DistCp, to create an Options object.  */
end_comment

begin_class
DECL|class|OptionsParser
specifier|public
class|class
name|OptionsParser
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
name|OptionsParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|cliOptions
specifier|private
specifier|static
specifier|final
name|Options
name|cliOptions
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|DistCpOptionSwitch
name|option
range|:
name|DistCpOptionSwitch
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding option "
operator|+
name|option
operator|.
name|getOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|cliOptions
operator|.
name|addOption
argument_list|(
name|option
operator|.
name|getOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CustomParser
specifier|private
specifier|static
class|class
name|CustomParser
extends|extends
name|GnuParser
block|{
annotation|@
name|Override
DECL|method|flatten (Options options, String[] arguments, boolean stopAtNonOption)
specifier|protected
name|String
index|[]
name|flatten
parameter_list|(
name|Options
name|options
parameter_list|,
name|String
index|[]
name|arguments
parameter_list|,
name|boolean
name|stopAtNonOption
parameter_list|)
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|arguments
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
if|if
condition|(
name|arguments
index|[
name|index
index|]
operator|.
name|equals
argument_list|(
literal|"-"
operator|+
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|arguments
index|[
name|index
index|]
operator|=
literal|"-prbugpc"
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|flatten
argument_list|(
name|options
argument_list|,
name|arguments
argument_list|,
name|stopAtNonOption
argument_list|)
return|;
block|}
block|}
comment|/**    * The parse method parses the command-line options, and creates    * a corresponding Options object.    * @param args Command-line arguments (excluding the options consumed    *              by the GenericOptionsParser).    * @return The Options object, corresponding to the specified command-line.    * @throws IllegalArgumentException: Thrown if the parse fails.    */
DECL|method|parse (String args[])
specifier|public
specifier|static
name|DistCpOptions
name|parse
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|CommandLineParser
name|parser
init|=
operator|new
name|CustomParser
argument_list|()
decl_stmt|;
name|CommandLine
name|command
decl_stmt|;
try|try
block|{
name|command
operator|=
name|parser
operator|.
name|parse
argument_list|(
name|cliOptions
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to parse arguments. "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|args
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|DistCpOptions
name|option
decl_stmt|;
name|Path
name|targetPath
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|sourcePaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|leftOverArgs
index|[]
init|=
name|command
operator|.
name|getArgs
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftOverArgs
operator|==
literal|null
operator|||
name|leftOverArgs
operator|.
name|length
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Target path not specified"
argument_list|)
throw|;
block|}
comment|//Last Argument is the target path
name|targetPath
operator|=
operator|new
name|Path
argument_list|(
name|leftOverArgs
index|[
name|leftOverArgs
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
comment|//Copy any source paths in the arguments to the list
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|leftOverArgs
operator|.
name|length
operator|-
literal|1
condition|;
name|index
operator|++
control|)
block|{
name|sourcePaths
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|leftOverArgs
index|[
name|index
index|]
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* If command has source file listing, use it else, fall back on source paths in args        If both are present, throw exception and bail */
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SOURCE_FILE_LISTING
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|sourcePaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Both source file listing and source paths present"
argument_list|)
throw|;
block|}
name|option
operator|=
operator|new
name|DistCpOptions
argument_list|(
operator|new
name|Path
argument_list|(
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|SOURCE_FILE_LISTING
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|sourcePaths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Neither source file listing nor source paths present"
argument_list|)
throw|;
block|}
name|option
operator|=
operator|new
name|DistCpOptions
argument_list|(
name|sourcePaths
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
comment|//Process all the other option switches and set options appropriately
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|IGNORE_FAILURES
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setIgnoreFailures
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|ATOMIC_COMMIT
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setAtomicCommit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|WORK_PATH
operator|.
name|getSwitch
argument_list|()
argument_list|)
operator|&&
name|option
operator|.
name|shouldAtomicCommit
argument_list|()
condition|)
block|{
name|String
name|workPath
init|=
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|WORK_PATH
operator|.
name|getSwitch
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|workPath
operator|!=
literal|null
operator|&&
operator|!
name|workPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|option
operator|.
name|setAtomicWorkPath
argument_list|(
operator|new
name|Path
argument_list|(
name|workPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|WORK_PATH
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"-tmp work-path can only be specified along with -atomic"
argument_list|)
throw|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|LOG_PATH
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setLogPath
argument_list|(
operator|new
name|Path
argument_list|(
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|LOG_PATH
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SYNC_FOLDERS
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setSyncFolder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|OVERWRITE
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setOverwrite
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|DELETE_MISSING
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setDeleteMissing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SKIP_CRC
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setSkipCRC
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|BLOCKING
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|BANDWIDTH
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|Integer
name|mapBandwidth
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|BANDWIDTH
operator|.
name|getSwitch
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mapBandwidth
operator|.
name|intValue
argument_list|()
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bandwidth specified is not positive: "
operator|+
name|mapBandwidth
argument_list|)
throw|;
block|}
name|option
operator|.
name|setMapBandwidth
argument_list|(
name|mapBandwidth
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bandwidth specified is invalid: "
operator|+
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|BANDWIDTH
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SSL_CONF
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setSslConfigurationFile
argument_list|(
name|command
operator|.
name|getOptionValue
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SSL_CONF
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|MAX_MAPS
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|Integer
name|maps
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|MAX_MAPS
operator|.
name|getSwitch
argument_list|()
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|option
operator|.
name|setMaxMaps
argument_list|(
name|maps
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Number of maps is invalid: "
operator|+
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|MAX_MAPS
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|COPY_STRATEGY
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|option
operator|.
name|setCopyStrategy
argument_list|(
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|COPY_STRATEGY
operator|.
name|getSwitch
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|attributes
init|=
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|PRESERVE_STATUS
operator|.
name|getSwitch
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|attributes
operator|==
literal|null
operator|||
name|attributes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|FileAttribute
name|attribute
range|:
name|FileAttribute
operator|.
name|values
argument_list|()
control|)
block|{
name|option
operator|.
name|preserve
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|attributes
operator|.
name|length
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|option
operator|.
name|preserve
argument_list|(
name|FileAttribute
operator|.
name|getAttribute
argument_list|(
name|attributes
operator|.
name|charAt
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|FILE_LIMIT
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|fileLimitString
init|=
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|FILE_LIMIT
operator|.
name|getSwitch
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|fileLimitString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"File-limit is invalid: "
operator|+
name|fileLimitString
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
name|DistCpOptionSwitch
operator|.
name|FILE_LIMIT
operator|.
name|getSwitch
argument_list|()
operator|+
literal|" is a deprecated"
operator|+
literal|" option. Ignoring."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|command
operator|.
name|hasOption
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SIZE_LIMIT
operator|.
name|getSwitch
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|sizeLimitString
init|=
name|getVal
argument_list|(
name|command
argument_list|,
name|DistCpOptionSwitch
operator|.
name|SIZE_LIMIT
operator|.
name|getSwitch
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Long
operator|.
name|parseLong
argument_list|(
name|sizeLimitString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Size-limit is invalid: "
operator|+
name|sizeLimitString
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
name|DistCpOptionSwitch
operator|.
name|SIZE_LIMIT
operator|.
name|getSwitch
argument_list|()
operator|+
literal|" is a deprecated"
operator|+
literal|" option. Ignoring."
argument_list|)
expr_stmt|;
block|}
return|return
name|option
return|;
block|}
DECL|method|getVal (CommandLine command, String swtch)
specifier|private
specifier|static
name|String
name|getVal
parameter_list|(
name|CommandLine
name|command
parameter_list|,
name|String
name|swtch
parameter_list|)
block|{
name|String
name|optionValue
init|=
name|command
operator|.
name|getOptionValue
argument_list|(
name|swtch
argument_list|)
decl_stmt|;
if|if
condition|(
name|optionValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|optionValue
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
DECL|method|usage ()
specifier|public
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|"distcp OPTIONS [source_path...]<target_path>\n\nOPTIONS"
argument_list|,
name|cliOptions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

