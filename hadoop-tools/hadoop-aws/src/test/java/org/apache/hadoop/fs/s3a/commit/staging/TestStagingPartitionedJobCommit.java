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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|UUID
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|MockS3AFileSystem
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
name|S3AFileSystem
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|staging
operator|.
name|StagingTestBase
operator|.
name|*
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
comment|/** Mocking test of partitioned committer. */
end_comment

begin_class
DECL|class|TestStagingPartitionedJobCommit
specifier|public
class|class
name|TestStagingPartitionedJobCommit
extends|extends
name|StagingTestBase
operator|.
name|JobCommitterTest
argument_list|<
name|PartitionedStagingCommitter
argument_list|>
block|{
annotation|@
name|Override
DECL|method|setupJob ()
specifier|public
name|void
name|setupJob
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setupJob
argument_list|()
expr_stmt|;
name|getWrapperFS
argument_list|()
operator|.
name|setLogEvents
argument_list|(
name|MockS3AFileSystem
operator|.
name|LOG_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newJobCommitter ()
name|PartitionedStagingCommitter
name|newJobCommitter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|PartitionedStagingCommitterForTesting
argument_list|(
name|createTaskAttemptForJob
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Subclass of the Partitioned Staging committer used in the test cases.    */
DECL|class|PartitionedStagingCommitterForTesting
specifier|private
specifier|static
specifier|final
class|class
name|PartitionedStagingCommitterForTesting
extends|extends
name|PartitionedCommitterForTesting
block|{
DECL|field|aborted
specifier|private
name|boolean
name|aborted
init|=
literal|false
decl_stmt|;
DECL|method|PartitionedStagingCommitterForTesting (TaskAttemptContext context)
specifier|private
name|PartitionedStagingCommitterForTesting
parameter_list|(
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|OUTPUT_PATH
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|listPendingUploadsToCommit ( JobContext context)
specifier|protected
name|List
argument_list|<
name|SinglePendingCommit
argument_list|>
name|listPendingUploadsToCommit
parameter_list|(
name|JobContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|SinglePendingCommit
argument_list|>
name|pending
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dateint
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"20161115"
argument_list|,
literal|"20161116"
argument_list|)
control|)
block|{
for|for
control|(
name|String
name|hour
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|"13"
argument_list|,
literal|"14"
argument_list|)
control|)
block|{
name|String
name|key
init|=
name|OUTPUT_PREFIX
operator|+
literal|"/dateint="
operator|+
name|dateint
operator|+
literal|"/hour="
operator|+
name|hour
operator|+
literal|"/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|".parquet"
decl_stmt|;
name|SinglePendingCommit
name|commit
init|=
operator|new
name|SinglePendingCommit
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setBucket
argument_list|(
name|BUCKET
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setDestinationKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setUri
argument_list|(
literal|"s3a://"
operator|+
name|BUCKET
operator|+
literal|"/"
operator|+
name|key
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setUploadId
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|etags
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|etags
operator|.
name|add
argument_list|(
literal|"tag1"
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setEtags
argument_list|(
name|etags
argument_list|)
expr_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pending
return|;
block|}
annotation|@
name|Override
DECL|method|abortJobInternal (JobContext context, boolean suppressExceptions)
specifier|protected
name|void
name|abortJobInternal
parameter_list|(
name|JobContext
name|context
parameter_list|,
name|boolean
name|suppressExceptions
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|aborted
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|abortJobInternal
argument_list|(
name|context
argument_list|,
name|suppressExceptions
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testDefaultFailAndAppend ()
specifier|public
name|void
name|testDefaultFailAndAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockS3
init|=
name|getMockS3A
argument_list|()
decl_stmt|;
comment|// both fail and append don't check. fail is enforced at the task level.
for|for
control|(
name|String
name|mode
range|:
name|Arrays
operator|.
name|asList
argument_list|(
literal|null
argument_list|,
name|CONFLICT_MODE_FAIL
argument_list|,
name|CONFLICT_MODE_APPEND
argument_list|)
control|)
block|{
if|if
condition|(
name|mode
operator|!=
literal|null
condition|)
block|{
name|getJob
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getJob
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|unset
argument_list|(
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|)
expr_stmt|;
block|}
name|PartitionedStagingCommitter
name|committer
init|=
name|newJobCommitter
argument_list|()
decl_stmt|;
comment|// no directories exist
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
comment|// parent and peer directories exist
name|reset
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116"
argument_list|,
literal|"dateint=20161116/hour=10"
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
comment|// a leaf directory exists.
comment|// NOTE: this is not checked during job commit, the commit succeeds.
name|reset
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=14"
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBadConflictMode ()
specifier|public
name|void
name|testBadConflictMode
parameter_list|()
throws|throws
name|Throwable
block|{
name|getJob
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|,
literal|"merge"
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
literal|"MERGE"
argument_list|,
literal|"committer conflict"
argument_list|,
name|this
operator|::
name|newJobCommitter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplace ()
specifier|public
name|void
name|testReplace
parameter_list|()
throws|throws
name|Exception
block|{
name|S3AFileSystem
name|mockS3
init|=
name|getMockS3A
argument_list|()
decl_stmt|;
name|getJob
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|,
name|CONFLICT_MODE_REPLACE
argument_list|)
expr_stmt|;
name|PartitionedStagingCommitter
name|committer
init|=
name|newJobCommitter
argument_list|()
decl_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyReplaceCommitActions
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
comment|// parent and peer directories exist
name|reset
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115"
argument_list|,
literal|"dateint=20161115/hour=12"
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyReplaceCommitActions
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
comment|// partition directories exist and should be removed
name|reset
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=12"
argument_list|,
literal|"dateint=20161115/hour=13"
argument_list|)
expr_stmt|;
name|canDelete
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=13"
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=13"
argument_list|)
expr_stmt|;
name|verifyReplaceCommitActions
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
comment|// partition directories exist and should be removed
name|reset
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=13"
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
name|canDelete
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=13"
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
expr_stmt|;
name|verifyReplaceCommitActions
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=13"
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the actions which replace does, essentially: delete the parent    * partitions.    * @param mockS3 s3 mock    */
DECL|method|verifyReplaceCommitActions (FileSystem mockS3)
specifier|protected
name|void
name|verifyReplaceCommitActions
parameter_list|(
name|FileSystem
name|mockS3
parameter_list|)
throws|throws
name|IOException
block|{
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=13"
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161115/hour=14"
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=13"
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplaceWithDeleteFailure ()
specifier|public
name|void
name|testReplaceWithDeleteFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystem
name|mockS3
init|=
name|getMockS3A
argument_list|()
decl_stmt|;
name|getJob
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|,
name|CONFLICT_MODE_REPLACE
argument_list|)
expr_stmt|;
specifier|final
name|PartitionedStagingCommitter
name|committer
init|=
name|newJobCommitter
argument_list|()
decl_stmt|;
name|pathsExist
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mockS3
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|OUTPUT_PATH
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|PathCommitException
argument_list|(
literal|"fake"
argument_list|,
literal|"Fake IOException for delete"
argument_list|)
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|PathCommitException
operator|.
name|class
argument_list|,
literal|"Fake IOException for delete"
argument_list|,
literal|"Should throw the fake IOException"
argument_list|,
parameter_list|()
lambda|->
name|committer
operator|.
name|commitJob
argument_list|(
name|getJob
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|verifyReplaceCommitActions
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
name|verifyDeleted
argument_list|(
name|mockS3
argument_list|,
literal|"dateint=20161116/hour=14"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have aborted"
argument_list|,
operator|(
operator|(
name|PartitionedStagingCommitterForTesting
operator|)
name|committer
operator|)
operator|.
name|aborted
argument_list|)
expr_stmt|;
name|verifyCompletion
argument_list|(
name|mockS3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

