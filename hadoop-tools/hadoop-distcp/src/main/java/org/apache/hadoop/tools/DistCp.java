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
name|util
operator|.
name|Random
import|;
end_import

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
name|Preconditions
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|FileUtil
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Cluster
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|JobContext
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
name|mapreduce
operator|.
name|JobSubmissionFiles
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
name|CopyListing
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
name|hadoop
operator|.
name|tools
operator|.
name|mapred
operator|.
name|CopyMapper
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
name|mapred
operator|.
name|CopyOutputFormat
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
name|util
operator|.
name|DistCpUtils
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
name|ShutdownHookManager
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

begin_comment
comment|/**  * DistCp is the main driver-class for DistCpV2.  * For command-line use, DistCp::main() orchestrates the parsing of command-line  * parameters and the launch of the DistCp job.  * For programmatic use, a DistCp object can be constructed by specifying  * options (in a DistCpOptions object), and DistCp::execute() may be used to  * launch the copy-job. DistCp may alternatively be sub-classed to fine-tune  * behaviour.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DistCp
specifier|public
class|class
name|DistCp
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|/**    * Priority of the shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DistCp
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|field|context
name|DistCpContext
name|context
decl_stmt|;
DECL|field|metaFolder
specifier|private
name|Path
name|metaFolder
decl_stmt|;
DECL|field|PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"_distcp"
decl_stmt|;
DECL|field|WIP_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|WIP_PREFIX
init|=
literal|"._WIP_"
decl_stmt|;
DECL|field|DISTCP_DEFAULT_XML
specifier|private
specifier|static
specifier|final
name|String
name|DISTCP_DEFAULT_XML
init|=
literal|"distcp-default.xml"
decl_stmt|;
DECL|field|DISTCP_SITE_XML
specifier|private
specifier|static
specifier|final
name|String
name|DISTCP_SITE_XML
init|=
literal|"distcp-site.xml"
decl_stmt|;
DECL|field|rand
specifier|static
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|submitted
specifier|private
name|boolean
name|submitted
decl_stmt|;
DECL|field|jobFS
specifier|private
name|FileSystem
name|jobFS
decl_stmt|;
DECL|method|prepareFileListing (Job job)
specifier|private
name|void
name|prepareFileListing
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|context
operator|.
name|shouldUseSnapshotDiff
argument_list|()
condition|)
block|{
comment|// When "-diff" or "-rdiff" is passed, do sync() first, then
comment|// create copyListing based on snapshot diff.
name|DistCpSync
name|distCpSync
init|=
operator|new
name|DistCpSync
argument_list|(
name|context
argument_list|,
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|distCpSync
operator|.
name|sync
argument_list|()
condition|)
block|{
name|createInputFileListingWithDiff
argument_list|(
name|job
argument_list|,
name|distCpSync
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"DistCp sync failed, input options: "
operator|+
name|context
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// When no "-diff" or "-rdiff" is passed, create copyListing
comment|// in regular way.
name|createInputFileListing
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Public Constructor. Creates DistCp object with specified input-parameters.    * (E.g. source-paths, target-location, etc.)    * @param configuration configuration against which the Copy-mapper must run    * @param inputOptions Immutable options    * @throws Exception    */
DECL|method|DistCp (Configuration configuration, DistCpOptions inputOptions)
specifier|public
name|DistCp
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|DistCpOptions
name|inputOptions
parameter_list|)
throws|throws
name|Exception
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|config
operator|.
name|addResource
argument_list|(
name|DISTCP_DEFAULT_XML
argument_list|)
expr_stmt|;
name|config
operator|.
name|addResource
argument_list|(
name|DISTCP_SITE_XML
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputOptions
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|context
operator|=
operator|new
name|DistCpContext
argument_list|(
name|inputOptions
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|metaFolder
operator|=
name|createMetaFolderPath
argument_list|()
expr_stmt|;
block|}
comment|/**    * To be used with the ToolRunner. Not for public consumption.    */
annotation|@
name|VisibleForTesting
DECL|method|DistCp ()
name|DistCp
parameter_list|()
block|{}
comment|/**    * Implementation of Tool::run(). Orchestrates the copy of source file(s)    * to target location, by:    *  1. Creating a list of files to be copied to target.    *  2. Launching a Map-only job to copy the files. (Delegates to execute().)    * @param argv List of arguments passed to DistCp, from the ToolRunner.    * @return On success, it returns 0. Else, -1.    */
annotation|@
name|Override
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|OptionsParser
operator|.
name|usage
argument_list|()
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|INVALID_ARGUMENT
return|;
block|}
try|try
block|{
name|context
operator|=
operator|new
name|DistCpContext
argument_list|(
name|OptionsParser
operator|.
name|parse
argument_list|(
name|argv
argument_list|)
argument_list|)
expr_stmt|;
name|checkSplitLargeFile
argument_list|()
expr_stmt|;
name|setTargetPathExists
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Input Options: "
operator|+
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid arguments: "
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
literal|"Invalid arguments: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|OptionsParser
operator|.
name|usage
argument_list|()
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|INVALID_ARGUMENT
return|;
block|}
try|try
block|{
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidInputException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid input: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|INVALID_ARGUMENT
return|;
block|}
catch|catch
parameter_list|(
name|DuplicateFileException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Duplicate files in input path: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|DUPLICATE_INPUT
return|;
block|}
catch|catch
parameter_list|(
name|AclsNotSupportedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"ACLs not supported on at least one file system: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|ACLS_NOT_SUPPORTED
return|;
block|}
catch|catch
parameter_list|(
name|XAttrsNotSupportedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"XAttrs not supported on at least one file system: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|XATTRS_NOT_SUPPORTED
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception encountered "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|DistCpConstants
operator|.
name|UNKNOWN_ERROR
return|;
block|}
return|return
name|DistCpConstants
operator|.
name|SUCCESS
return|;
block|}
comment|/**    * Implements the core-execution. Creates the file-list for copy,    * and launches the Hadoop-job, to do the copy.    * @return Job handle    * @throws Exception    */
DECL|method|execute ()
specifier|public
name|Job
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|context
operator|!=
literal|null
argument_list|,
literal|"The DistCpContext should have been created before running DistCp!"
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|createAndSubmitJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|shouldBlock
argument_list|()
condition|)
block|{
name|waitForJobCompletion
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
return|return
name|job
return|;
block|}
comment|/**    * Create and submit the mapreduce job.    * @return The mapreduce job object that has been submitted    */
DECL|method|createAndSubmitJob ()
specifier|public
name|Job
name|createAndSubmitJob
parameter_list|()
throws|throws
name|Exception
block|{
assert|assert
name|context
operator|!=
literal|null
assert|;
assert|assert
name|getConf
argument_list|()
operator|!=
literal|null
assert|;
name|Job
name|job
init|=
literal|null
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|//Don't cleanup while we are setting up.
name|metaFolder
operator|=
name|createMetaFolderPath
argument_list|()
expr_stmt|;
name|jobFS
operator|=
name|metaFolder
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|=
name|createJob
argument_list|()
expr_stmt|;
block|}
name|prepareFileListing
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
name|submitted
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|submitted
condition|)
block|{
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|jobID
init|=
name|job
operator|.
name|getJobID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_DISTCP_JOB_ID
argument_list|,
name|jobID
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DistCp job-id: "
operator|+
name|jobID
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
comment|/**    * Wait for the given job to complete.    * @param job the given mapreduce job that has already been submitted    */
DECL|method|waitForJobCompletion (Job job)
specifier|public
name|void
name|waitForJobCompletion
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|Exception
block|{
assert|assert
name|job
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"DistCp failure: Job "
operator|+
name|job
operator|.
name|getJobID
argument_list|()
operator|+
literal|" has failed: "
operator|+
name|job
operator|.
name|getStatus
argument_list|()
operator|.
name|getFailureInfo
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Set targetPathExists in both inputOptions and job config,    * for the benefit of CopyCommitter    */
DECL|method|setTargetPathExists ()
specifier|private
name|void
name|setTargetPathExists
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|target
init|=
name|context
operator|.
name|getTargetPath
argument_list|()
decl_stmt|;
name|FileSystem
name|targetFS
init|=
name|target
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|targetExists
init|=
name|targetFS
operator|.
name|exists
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|context
operator|.
name|setTargetPathExists
argument_list|(
name|targetExists
argument_list|)
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_TARGET_PATH_EXISTS
argument_list|,
name|targetExists
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check splitting large files is supported and populate configs.    */
DECL|method|checkSplitLargeFile ()
specifier|private
name|void
name|checkSplitLargeFile
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|splitLargeFile
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|Path
name|target
init|=
name|context
operator|.
name|getTargetPath
argument_list|()
decl_stmt|;
specifier|final
name|FileSystem
name|targetFS
init|=
name|target
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Path
index|[]
name|src
init|=
literal|null
decl_stmt|;
name|Path
name|tgt
init|=
literal|null
decl_stmt|;
name|targetFS
operator|.
name|concat
argument_list|(
name|tgt
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|DistCpOptionSwitch
operator|.
name|BLOCKS_PER_CHUNK
operator|.
name|getSwitch
argument_list|()
operator|+
literal|" is not supported since the target file system doesn't"
operator|+
literal|" support concat."
argument_list|,
name|use
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Ignore other exception
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Set "
operator|+
name|DistCpConstants
operator|.
name|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
operator|+
literal|" to false since "
operator|+
name|DistCpOptionSwitch
operator|.
name|BLOCKS_PER_CHUNK
operator|.
name|getSwitch
argument_list|()
operator|+
literal|" is passed."
argument_list|)
expr_stmt|;
name|getConf
argument_list|()
operator|.
name|setBoolean
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_SIMPLE_LISTING_RANDOMIZE_FILES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create Job object for submitting it, with all the configuration    *    * @return Reference to job object.    * @throws IOException - Exception if any    */
DECL|method|createJob ()
specifier|private
name|Job
name|createJob
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jobName
init|=
literal|"distcp"
decl_stmt|;
name|String
name|userChosenName
init|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|JobContext
operator|.
name|JOB_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|userChosenName
operator|!=
literal|null
condition|)
name|jobName
operator|+=
literal|": "
operator|+
name|userChosenName
expr_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
name|jobName
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|DistCpUtils
operator|.
name|getStrategy
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|CopyMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|configureOutputFormat
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|CopyMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|CopyOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|MAP_SPECULATIVE
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|NUM_MAPS
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getMaxMaps
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|appendToConf
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
comment|/**    * Setup output format appropriately    *    * @param job - Job handle    * @throws IOException - Exception if any    */
DECL|method|configureOutputFormat (Job job)
specifier|private
name|void
name|configureOutputFormat
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Configuration
name|configuration
init|=
name|job
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Path
name|targetPath
init|=
name|context
operator|.
name|getTargetPath
argument_list|()
decl_stmt|;
name|FileSystem
name|targetFS
init|=
name|targetPath
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
name|targetPath
operator|=
name|targetPath
operator|.
name|makeQualified
argument_list|(
name|targetFS
operator|.
name|getUri
argument_list|()
argument_list|,
name|targetFS
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|shouldPreserve
argument_list|(
name|DistCpOptions
operator|.
name|FileAttribute
operator|.
name|ACL
argument_list|)
condition|)
block|{
name|DistCpUtils
operator|.
name|checkFileSystemAclSupport
argument_list|(
name|targetFS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|shouldPreserve
argument_list|(
name|DistCpOptions
operator|.
name|FileAttribute
operator|.
name|XATTR
argument_list|)
condition|)
block|{
name|DistCpUtils
operator|.
name|checkFileSystemXAttrSupport
argument_list|(
name|targetFS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|shouldAtomicCommit
argument_list|()
condition|)
block|{
name|Path
name|workDir
init|=
name|context
operator|.
name|getAtomicWorkPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|workDir
operator|==
literal|null
condition|)
block|{
name|workDir
operator|=
name|targetPath
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|workDir
operator|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
name|WIP_PREFIX
operator|+
name|targetPath
operator|.
name|getName
argument_list|()
operator|+
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
name|workFS
init|=
name|workDir
operator|.
name|getFileSystem
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|FileUtil
operator|.
name|compareFs
argument_list|(
name|targetFS
argument_list|,
name|workFS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Work path "
operator|+
name|workDir
operator|+
literal|" and target path "
operator|+
name|targetPath
operator|+
literal|" are in different file system"
argument_list|)
throw|;
block|}
name|CopyOutputFormat
operator|.
name|setWorkingDirectory
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CopyOutputFormat
operator|.
name|setWorkingDirectory
argument_list|(
name|job
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
name|CopyOutputFormat
operator|.
name|setCommitDirectory
argument_list|(
name|job
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
name|Path
name|logPath
init|=
name|context
operator|.
name|getLogPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|logPath
operator|==
literal|null
condition|)
block|{
name|logPath
operator|=
operator|new
name|Path
argument_list|(
name|metaFolder
argument_list|,
literal|"_logs"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DistCp job log path: "
operator|+
name|logPath
argument_list|)
expr_stmt|;
block|}
name|CopyOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|logPath
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create input listing by invoking an appropriate copy listing    * implementation. Also add delegation tokens for each path    * to job's credential store    *    * @param job - Handle to job    * @return Returns the path where the copy listing is created    * @throws IOException - If any    */
DECL|method|createInputFileListing (Job job)
specifier|protected
name|Path
name|createInputFileListing
parameter_list|(
name|Job
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|fileListingPath
init|=
name|getFileListingPath
argument_list|()
decl_stmt|;
name|CopyListing
name|copyListing
init|=
name|CopyListing
operator|.
name|getCopyListing
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|job
operator|.
name|getCredentials
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|copyListing
operator|.
name|buildListing
argument_list|(
name|fileListingPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|fileListingPath
return|;
block|}
comment|/**    * Create input listing based on snapshot diff report.    * @param job - Handle to job    * @param distCpSync the class wraps the snapshot diff report    * @return Returns the path where the copy listing is created    * @throws IOException - If any    */
DECL|method|createInputFileListingWithDiff (Job job, DistCpSync distCpSync)
specifier|private
name|Path
name|createInputFileListingWithDiff
parameter_list|(
name|Job
name|job
parameter_list|,
name|DistCpSync
name|distCpSync
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|fileListingPath
init|=
name|getFileListingPath
argument_list|()
decl_stmt|;
name|CopyListing
name|copyListing
init|=
operator|new
name|SimpleCopyListing
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|job
operator|.
name|getCredentials
argument_list|()
argument_list|,
name|distCpSync
argument_list|)
decl_stmt|;
name|copyListing
operator|.
name|buildListing
argument_list|(
name|fileListingPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|fileListingPath
return|;
block|}
comment|/**    * Get default name of the copy listing file. Use the meta folder    * to create the copy listing file    *    * @return - Path where the copy listing file has to be saved    * @throws IOException - Exception if any    */
DECL|method|getFileListingPath ()
specifier|protected
name|Path
name|getFileListingPath
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|fileListPathStr
init|=
name|metaFolder
operator|+
literal|"/fileList.seq"
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|fileListPathStr
argument_list|)
decl_stmt|;
return|return
operator|new
name|Path
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Create a default working folder for the job, under the    * job staging directory    *    * @return Returns the working folder information    * @throws Exception - Exception if any    */
DECL|method|createMetaFolderPath ()
specifier|private
name|Path
name|createMetaFolderPath
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|configuration
init|=
name|getConf
argument_list|()
decl_stmt|;
name|Path
name|stagingDir
init|=
name|JobSubmissionFiles
operator|.
name|getStagingDir
argument_list|(
operator|new
name|Cluster
argument_list|(
name|configuration
argument_list|)
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|Path
name|metaFolderPath
init|=
operator|new
name|Path
argument_list|(
name|stagingDir
argument_list|,
name|PREFIX
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Meta folder location: "
operator|+
name|metaFolderPath
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|set
argument_list|(
name|DistCpConstants
operator|.
name|CONF_LABEL_META_FOLDER
argument_list|,
name|metaFolderPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|metaFolderPath
return|;
block|}
comment|/**    * Returns the context.    *    * @return the context    */
DECL|method|getContext ()
specifier|protected
name|DistCpContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
comment|/**    * Main function of the DistCp program. Parses the input arguments (via OptionsParser),    * and invokes the DistCp::run() method, via the ToolRunner.    * @param argv Command-line arguments sent to DistCp.    */
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
name|int
name|exitCode
decl_stmt|;
try|try
block|{
name|DistCp
name|distCp
init|=
operator|new
name|DistCp
argument_list|()
decl_stmt|;
name|Cleanup
name|CLEANUP
init|=
operator|new
name|Cleanup
argument_list|(
name|distCp
argument_list|)
decl_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
name|CLEANUP
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|exitCode
operator|=
name|ToolRunner
operator|.
name|run
argument_list|(
name|getDefaultConf
argument_list|()
argument_list|,
name|distCp
argument_list|,
name|argv
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
name|error
argument_list|(
literal|"Couldn't complete DistCp operation: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|exitCode
operator|=
name|DistCpConstants
operator|.
name|UNKNOWN_ERROR
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
block|}
comment|/**    * Loads properties from distcp-default.xml into configuration    * object    * @return Configuration which includes properties from distcp-default.xml    *         and distcp-site.xml    */
DECL|method|getDefaultConf ()
specifier|private
specifier|static
name|Configuration
name|getDefaultConf
parameter_list|()
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|addResource
argument_list|(
name|DISTCP_DEFAULT_XML
argument_list|)
expr_stmt|;
name|config
operator|.
name|addResource
argument_list|(
name|DISTCP_SITE_XML
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
DECL|method|cleanup ()
specifier|private
specifier|synchronized
name|void
name|cleanup
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|metaFolder
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|jobFS
operator|!=
literal|null
condition|)
block|{
name|jobFS
operator|.
name|delete
argument_list|(
name|metaFolder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|metaFolder
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to cleanup meta folder: "
operator|+
name|metaFolder
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isSubmitted ()
specifier|private
name|boolean
name|isSubmitted
parameter_list|()
block|{
return|return
name|submitted
return|;
block|}
DECL|class|Cleanup
specifier|private
specifier|static
class|class
name|Cleanup
implements|implements
name|Runnable
block|{
DECL|field|distCp
specifier|private
specifier|final
name|DistCp
name|distCp
decl_stmt|;
DECL|method|Cleanup (DistCp distCp)
name|Cleanup
parameter_list|(
name|DistCp
name|distCp
parameter_list|)
block|{
name|this
operator|.
name|distCp
operator|=
name|distCp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|distCp
operator|.
name|isSubmitted
argument_list|()
condition|)
return|return;
name|distCp
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

