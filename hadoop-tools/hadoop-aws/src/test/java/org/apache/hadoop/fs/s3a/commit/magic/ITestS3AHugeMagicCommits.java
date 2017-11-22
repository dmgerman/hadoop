begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit.magic
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
name|magic
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
name|stream
operator|.
name|Collectors
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
name|commons
operator|.
name|lang3
operator|.
name|tuple
operator|.
name|Pair
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
name|LocatedFileStatus
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
name|contract
operator|.
name|ContractTestUtils
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
name|Constants
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
name|CommitConstants
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
name|CommitOperations
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
name|CommitUtils
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
name|fs
operator|.
name|s3a
operator|.
name|scale
operator|.
name|AbstractSTestS3AHugeFiles
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
name|S3ATestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Write a huge file via the magic commit mechanism,  * commit it and verify that it is there. This is needed to  * verify that the pending-upload mechanism works with multipart files  * of more than one part.  *  * This is a scale test.  */
end_comment

begin_class
DECL|class|ITestS3AHugeMagicCommits
specifier|public
class|class
name|ITestS3AHugeMagicCommits
extends|extends
name|AbstractSTestS3AHugeFiles
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
name|ITestS3AHugeMagicCommits
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|magicDir
specifier|private
name|Path
name|magicDir
decl_stmt|;
DECL|field|jobDir
specifier|private
name|Path
name|jobDir
decl_stmt|;
comment|/** file used as the destination for the write;    *  it is never actually created. */
DECL|field|magicOutputFile
specifier|private
name|Path
name|magicOutputFile
decl_stmt|;
comment|/** The file with the JSON data about the commit. */
DECL|field|pendingDataFile
specifier|private
name|Path
name|pendingDataFile
decl_stmt|;
comment|/**    * Use fast upload on disk.    * @return the upload buffer mechanism.    */
DECL|method|getBlockOutputBufferName ()
specifier|protected
name|String
name|getBlockOutputBufferName
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|FAST_UPLOAD_BUFFER_DISK
return|;
block|}
comment|/**    * The suite name; required to be unique.    * @return the test suite name    */
annotation|@
name|Override
DECL|method|getTestSuiteName ()
specifier|public
name|String
name|getTestSuiteName
parameter_list|()
block|{
return|return
literal|"ITestS3AHugeMagicCommits"
return|;
block|}
comment|/**    * Create the scale IO conf with the committer enabled.    * @return the configuration to use for the test FS.    */
annotation|@
name|Override
DECL|method|createScaleConfiguration ()
specifier|protected
name|Configuration
name|createScaleConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createScaleConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MAGIC_COMMITTER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|CommitUtils
operator|.
name|verifyIsMagicCommitFS
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
comment|// set up the paths for the commit operation
name|Path
name|finalDirectory
init|=
operator|new
name|Path
argument_list|(
name|getScaleTestDir
argument_list|()
argument_list|,
literal|"commit"
argument_list|)
decl_stmt|;
name|magicDir
operator|=
operator|new
name|Path
argument_list|(
name|finalDirectory
argument_list|,
name|MAGIC
argument_list|)
expr_stmt|;
name|jobDir
operator|=
operator|new
name|Path
argument_list|(
name|magicDir
argument_list|,
literal|"job_001"
argument_list|)
expr_stmt|;
name|String
name|filename
init|=
literal|"commit.bin"
decl_stmt|;
name|setHugefile
argument_list|(
operator|new
name|Path
argument_list|(
name|finalDirectory
argument_list|,
name|filename
argument_list|)
argument_list|)
expr_stmt|;
name|magicOutputFile
operator|=
operator|new
name|Path
argument_list|(
name|jobDir
argument_list|,
name|filename
argument_list|)
expr_stmt|;
name|pendingDataFile
operator|=
operator|new
name|Path
argument_list|(
name|jobDir
argument_list|,
name|filename
operator|+
name|PENDING_SUFFIX
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the path to the commit metadata file, not that of the huge file.    * @return a file in the job dir    */
annotation|@
name|Override
DECL|method|getPathOfFileToCreate ()
specifier|protected
name|Path
name|getPathOfFileToCreate
parameter_list|()
block|{
return|return
name|magicOutputFile
return|;
block|}
annotation|@
name|Override
DECL|method|test_030_postCreationAssertions ()
specifier|public
name|void
name|test_030_postCreationAssertions
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Committing file"
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"final file exists"
argument_list|,
name|getHugefile
argument_list|()
argument_list|)
expr_stmt|;
name|assertPathExists
argument_list|(
literal|"No pending file"
argument_list|,
name|pendingDataFile
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// as a 0-byte marker is created, there is a file at the end path,
comment|// it just MUST be 0-bytes long
name|FileStatus
name|status
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|magicOutputFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Non empty marker file "
operator|+
name|status
argument_list|,
literal|0
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|timer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
decl_stmt|;
name|CommitOperations
name|operations
init|=
operator|new
name|CommitOperations
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|Path
name|destDir
init|=
name|getHugefile
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertPathExists
argument_list|(
literal|"Magic dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|destDir
argument_list|,
name|CommitConstants
operator|.
name|MAGIC
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|destDirKey
init|=
name|fs
operator|.
name|pathToKey
argument_list|(
name|destDir
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|uploads
init|=
name|listMultipartUploads
argument_list|(
name|fs
argument_list|,
name|destDirKey
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Pending uploads: "
operator|+
name|uploads
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|,
name|uploads
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"jobDir"
argument_list|,
name|jobDir
argument_list|)
expr_stmt|;
name|Pair
argument_list|<
name|PendingSet
argument_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|LocatedFileStatus
argument_list|,
name|IOException
argument_list|>
argument_list|>
argument_list|>
name|results
init|=
name|operations
operator|.
name|loadSinglePendingCommits
argument_list|(
name|jobDir
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|SinglePendingCommit
name|singlePendingCommit
range|:
name|results
operator|.
name|getKey
argument_list|()
operator|.
name|getCommits
argument_list|()
control|)
block|{
name|operations
operator|.
name|commitOrFail
argument_list|(
name|singlePendingCommit
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|end
argument_list|(
literal|"time to commit %s"
argument_list|,
name|pendingDataFile
argument_list|)
expr_stmt|;
comment|// upload is no longer pending
name|uploads
operator|=
name|listMultipartUploads
argument_list|(
name|fs
argument_list|,
name|destDirKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pending uploads"
operator|+
name|uploads
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
argument_list|,
literal|0
argument_list|,
name|operations
operator|.
name|listPendingUploadsUnderPath
argument_list|(
name|destDir
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// at this point, the huge file exists, so the normal assertions
comment|// on that file must be valid. Verify.
name|super
operator|.
name|test_030_postCreationAssertions
argument_list|()
expr_stmt|;
block|}
DECL|method|skipQuietly (String text)
specifier|private
name|void
name|skipQuietly
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|describe
argument_list|(
literal|"Skipping: %s"
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|test_040_PositionedReadHugeFile ()
specifier|public
name|void
name|test_040_PositionedReadHugeFile
parameter_list|()
block|{
name|skipQuietly
argument_list|(
literal|"test_040_PositionedReadHugeFile"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|test_050_readHugeFile ()
specifier|public
name|void
name|test_050_readHugeFile
parameter_list|()
block|{
name|skipQuietly
argument_list|(
literal|"readHugeFile"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|test_100_renameHugeFile ()
specifier|public
name|void
name|test_100_renameHugeFile
parameter_list|()
block|{
name|skipQuietly
argument_list|(
literal|"renameHugeFile"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|test_800_DeleteHugeFiles ()
specifier|public
name|void
name|test_800_DeleteHugeFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|getFileSystem
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|getFileSystem
argument_list|()
operator|.
name|abortOutstandingMultipartUploads
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception while purging old uploads"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|super
operator|.
name|test_800_DeleteHugeFiles
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|ContractTestUtils
operator|.
name|rm
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|magicDir
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

