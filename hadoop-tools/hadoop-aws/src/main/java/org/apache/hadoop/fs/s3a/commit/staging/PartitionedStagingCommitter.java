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
name|IOException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|s3a
operator|.
name|commit
operator|.
name|PathCommitException
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
name|Tasks
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
name|files
operator|.
name|PendingSet
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
name|files
operator|.
name|SinglePendingCommit
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|DurationInfo
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
name|COMMITTER_NAME_PARTITIONED
import|;
end_import

begin_comment
comment|/**  * Partitioned committer.  * This writes data to specific "partition" subdirectories, applying  * conflict resolution on a partition-by-partition basis. The existence  * and state of any parallel partitions for which there is no are output  * files are not considered in the conflict resolution.  *  * The conflict policy is  *<ul>  *<li>FAIL: fail the commit if any of the partitions have data.</li>  *<li>APPEND: add extra data to the destination partitions.</li>  *<li>REPLACE: delete the destination partition in the job commit  *   (i.e. after and only if all tasks have succeeded.</li>  *</ul>  * To determine the paths, the precommit process actually has to read  * in all source files, independently of the final commit phase.  * This is inefficient, though some parallelization here helps.  */
end_comment

begin_class
DECL|class|PartitionedStagingCommitter
specifier|public
class|class
name|PartitionedStagingCommitter
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
name|PartitionedStagingCommitter
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
name|COMMITTER_NAME_PARTITIONED
decl_stmt|;
DECL|method|PartitionedStagingCommitter (Path outputPath, TaskAttemptContext context)
specifier|public
name|PartitionedStagingCommitter
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
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"PartitionedStagingCommitter{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|commitTaskInternal (TaskAttemptContext context, List<? extends FileStatus> taskOutput)
specifier|protected
name|int
name|commitTaskInternal
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|FileStatus
argument_list|>
name|taskOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|attemptPath
init|=
name|getTaskAttemptPath
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|partitions
init|=
name|Paths
operator|.
name|getPartitions
argument_list|(
name|attemptPath
argument_list|,
name|taskOutput
argument_list|)
decl_stmt|;
comment|// enforce conflict resolution, but only if the mode is FAIL. for APPEND,
comment|// it doesn't matter that the partitions are already there, and for REPLACE,
comment|// deletion should be done during job commit.
name|FileSystem
name|fs
init|=
name|getDestFS
argument_list|()
decl_stmt|;
if|if
condition|(
name|getConflictResolutionMode
argument_list|(
name|context
argument_list|,
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
operator|==
name|ConflictResolution
operator|.
name|FAIL
condition|)
block|{
for|for
control|(
name|String
name|partition
range|:
name|partitions
control|)
block|{
comment|// getFinalPath adds the UUID to the file name. this needs the parent.
name|Path
name|partitionPath
init|=
name|getFinalPath
argument_list|(
name|partition
operator|+
literal|"/file"
argument_list|,
name|context
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|partitionPath
argument_list|)
condition|)
block|{
throw|throw
name|failDestinationExists
argument_list|(
name|partitionPath
argument_list|,
literal|"Committing task "
operator|+
name|context
operator|.
name|getTaskAttemptID
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|commitTaskInternal
argument_list|(
name|context
argument_list|,
name|taskOutput
argument_list|)
return|;
block|}
comment|/**    * All    * Job-side conflict resolution.    * The partition path conflict resolution actions are:    *<ol>    *<li>FAIL: assume checking has taken place earlier; no more checks.</li>    *<li>APPEND: allowed.; no need to check.</li>    *<li>REPLACE deletes all existing partitions.</li>    *</ol>    * @param context job context    * @param pending the pending operations    * @throws IOException any failure    */
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
name|FileSystem
name|fs
init|=
name|getDestFS
argument_list|()
decl_stmt|;
comment|// enforce conflict resolution
name|Configuration
name|fsConf
init|=
name|fs
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|boolean
name|shouldPrecheckPendingFiles
init|=
literal|true
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
comment|// FAIL checking is done on the task side, so this does nothing
break|break;
case|case
name|APPEND
case|:
comment|// no check is needed because the output may exist for appending
break|break;
case|case
name|REPLACE
case|:
comment|// identify and replace the destination partitions
name|replacePartitions
argument_list|(
name|context
argument_list|,
name|pending
argument_list|)
expr_stmt|;
comment|// and so there is no need to do another check.
name|shouldPrecheckPendingFiles
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|PathCommitException
argument_list|(
literal|""
argument_list|,
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
if|if
condition|(
name|shouldPrecheckPendingFiles
condition|)
block|{
name|precommitCheckPendingFiles
argument_list|(
name|context
argument_list|,
name|pending
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Identify all partitions which need to be replaced and then delete them.    * The original implementation relied on all the pending commits to be    * loaded so could simply enumerate them.    * This iteration does not do that; it has to reload all the files    * to build the set, after which it initiates the delete process.    * This is done in parallel.    *<pre>    *   Set<Path> partitions = pending.stream()    *     .map(Path::getParent)    *     .collect(Collectors.toCollection(Sets::newLinkedHashSet));    *   for (Path partitionPath : partitions) {    *     LOG.debug("{}: removing partition path to be replaced: " +    *     getRole(), partitionPath);    *     fs.delete(partitionPath, true);    *   }    *</pre>    *    * @param context job context    * @param pending the pending operations    * @throws IOException any failure    */
DECL|method|replacePartitions ( final JobContext context, final ActiveCommit pending)
specifier|private
name|void
name|replacePartitions
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
name|Map
argument_list|<
name|Path
argument_list|,
name|String
argument_list|>
name|partitions
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|FileSystem
name|sourceFS
init|=
name|pending
operator|.
name|getSourceFS
argument_list|()
decl_stmt|;
name|ExecutorService
name|pool
init|=
name|buildThreadPool
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Replacing partitions"
argument_list|)
init|)
block|{
comment|// the parent directories are saved to a concurrent hash map.
comment|// for a marginal optimisation, the previous parent is tracked, so
comment|// if a task writes many files to the same dir, the synchronized map
comment|// is updated only once.
name|Tasks
operator|.
name|foreach
argument_list|(
name|pending
operator|.
name|getSourceFiles
argument_list|()
argument_list|)
operator|.
name|stopOnFailure
argument_list|()
operator|.
name|suppressExceptions
argument_list|(
literal|false
argument_list|)
operator|.
name|executeWith
argument_list|(
name|pool
argument_list|)
operator|.
name|run
argument_list|(
name|path
lambda|->
block|{
name|PendingSet
name|pendingSet
init|=
name|PendingSet
operator|.
name|load
argument_list|(
name|sourceFS
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|Path
name|lastParent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SinglePendingCommit
name|commit
range|:
name|pendingSet
operator|.
name|getCommits
argument_list|()
control|)
block|{
name|Path
name|parent
init|=
name|commit
operator|.
name|destinationPath
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
operator|!
name|parent
operator|.
name|equals
argument_list|(
name|lastParent
argument_list|)
condition|)
block|{
name|partitions
operator|.
name|put
argument_list|(
name|parent
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|lastParent
operator|=
name|parent
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// now do the deletes
name|FileSystem
name|fs
init|=
name|getDestFS
argument_list|()
decl_stmt|;
name|Tasks
operator|.
name|foreach
argument_list|(
name|partitions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|stopOnFailure
argument_list|()
operator|.
name|suppressExceptions
argument_list|(
literal|false
argument_list|)
operator|.
name|executeWith
argument_list|(
name|pool
argument_list|)
operator|.
name|run
argument_list|(
name|partitionPath
lambda|->
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}: removing partition path to be replaced: "
operator|+
name|getRole
argument_list|()
argument_list|,
name|partitionPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|partitionPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

