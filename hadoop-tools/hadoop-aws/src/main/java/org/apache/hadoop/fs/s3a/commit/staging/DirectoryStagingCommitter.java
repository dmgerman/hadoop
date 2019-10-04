begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.staging
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|staging
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|PathExistsException
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
name|s3a
operator|.
name|commit
operator|.
name|InternalCommitterConstants
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
name|TaskAttemptContext
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
name|fs
operator|.
name|s3a
operator|.
name|commit
operator|.
name|CommitConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This commits to a directory.  * The conflict policy is  *<ul>  *<li>FAIL: fail the commit</li>  *<li>APPEND: add extra data to the destination.</li>  *<li>REPLACE: delete the destination directory in the job commit  *   (i.e. after and only if all tasks have succeeded.</li>  *</ul>  */
end_comment

begin_class
DECL|class|DirectoryStagingCommitter
specifier|public
class|class
name|DirectoryStagingCommitter
extends|extends
name|StagingCommitter
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
name|DirectoryStagingCommitter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Name: {@value}. */
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|COMMITTER_NAME_DIRECTORY
decl_stmt|;
DECL|method|DirectoryStagingCommitter (Path outputPath, TaskAttemptContext context)
specifier|public
name|DirectoryStagingCommitter
parameter_list|(
name|Path
name|outputPath
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|outputPath
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|setupJob (JobContext context)
specifier|public
name|void
name|setupJob
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|outputPath
init|=
name|getOutputPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|getDestFS
argument_list|()
decl_stmt|;
name|ConflictResolution
name|conflictResolution
init|=
name|getConflictResolutionMode
argument_list|(
name|context
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Conflict Resolution mode is {}"
argument_list|,
name|conflictResolution
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|outputPath
argument_list|)
decl_stmt|;
comment|// if it is not a directory, fail fast for all conflict options.
if|if
condition|(
operator|!
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PathExistsException
argument_list|(
name|outputPath
operator|.
name|toString
argument_list|()
argument_list|,
literal|"output path is not a directory: "
operator|+
name|InternalCommitterConstants
operator|.
name|E_DEST_EXISTS
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|conflictResolution
condition|)
block|{
case|case
name|FAIL
case|:
throw|throw
name|failDestinationExists
argument_list|(
name|outputPath
argument_list|,
literal|"Setting job as "
operator|+
name|getRole
argument_list|()
argument_list|)
throw|;
case|case
name|APPEND
case|:
case|case
name|REPLACE
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|"Destination directory exists; conflict policy permits this"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ignored
parameter_list|)
block|{
comment|// there is no destination path, hence, no conflict.
block|}
comment|// make the parent directory, which also triggers a recursive directory
comment|// creation operation
name|super
operator|.
name|setupJob
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pre-commit actions for a job.    * Here: look at the conflict resolution mode and choose    * an action based on the current policy.    * @param context job context    * @param pending pending commits    * @throws IOException any failure    */
annotation|@
name|Override
DECL|method|preCommitJob ( final JobContext context, final ActiveCommit pending)
specifier|public
name|void
name|preCommitJob
parameter_list|(
specifier|final
name|JobContext
name|context
parameter_list|,
specifier|final
name|ActiveCommit
name|pending
parameter_list|)
throws|throws
name|IOException
block|{
comment|// see if the files can be loaded.
name|super
operator|.
name|preCommitJob
argument_list|(
name|context
argument_list|,
name|pending
argument_list|)
expr_stmt|;
name|Path
name|outputPath
init|=
name|getOutputPath
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|getDestFS
argument_list|()
decl_stmt|;
name|Configuration
name|fsConf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|getConflictResolutionMode
argument_list|(
name|context
argument_list|,
name|fsConf
argument_list|)
condition|)
block|{
case|case
name|FAIL
case|:
comment|// this was checked in setupJob; temporary files may have been
comment|// created, so do not check again.
break|break;
case|case
name|APPEND
case|:
comment|// do nothing
break|break;
case|case
name|REPLACE
case|:
if|if
condition|(
name|fs
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|true
comment|/* recursive */
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"{}: removed output path to be replaced: {}"
argument_list|,
name|getRole
argument_list|()
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
name|getRole
argument_list|()
operator|+
literal|": unknown conflict resolution mode: "
operator|+
name|getConflictResolutionMode
argument_list|(
name|context
argument_list|,
name|fsConf
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

