begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
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
name|io
operator|.
name|InterruptedIOException
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
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|AmazonS3
import|;
end_import

begin_import
import|import
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|StringUtils
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
name|AbstractS3ATestBase
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
name|FailureInjectionPolicy
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
name|InconsistentAmazonS3Client
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
name|WriteOperationHelper
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
name|SuccessData
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
name|RecordWriter
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
name|mapreduce
operator|.
name|TypeConverter
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
name|task
operator|.
name|TaskAttemptContextImpl
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|v2
operator|.
name|util
operator|.
name|MRBuilderUtils
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
name|Constants
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
name|MultipartTestUtils
operator|.
name|listMultipartUploads
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
comment|/**  * Base test suite for committer operations.  *  * By default, these tests enable the inconsistent committer, with  * a delay of {@link #CONSISTENCY_DELAY}; they may also have throttling  * enabled/disabled.  *  *<b>Important:</b> all filesystem probes will have to wait for  * the FS inconsistency delays and handle things like throttle exceptions,  * or disable throttling and fault injection before the probe.  *  */
end_comment

begin_class
DECL|class|AbstractCommitITest
specifier|public
specifier|abstract
class|class
name|AbstractCommitITest
extends|extends
name|AbstractS3ATestBase
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
name|AbstractCommitITest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CONSISTENCY_DELAY
specifier|protected
specifier|static
specifier|final
name|int
name|CONSISTENCY_DELAY
init|=
literal|500
decl_stmt|;
DECL|field|CONSISTENCY_PROBE_INTERVAL
specifier|protected
specifier|static
specifier|final
name|int
name|CONSISTENCY_PROBE_INTERVAL
init|=
literal|500
decl_stmt|;
DECL|field|CONSISTENCY_WAIT
specifier|protected
specifier|static
specifier|final
name|int
name|CONSISTENCY_WAIT
init|=
name|CONSISTENCY_DELAY
operator|*
literal|2
decl_stmt|;
DECL|field|inconsistentClient
specifier|private
name|InconsistentAmazonS3Client
name|inconsistentClient
decl_stmt|;
comment|/**    * Should the inconsistent S3A client be used?    * Default value: true.    * @return true for inconsistent listing    */
DECL|method|useInconsistentClient ()
specifier|public
name|boolean
name|useInconsistentClient
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * switch to an inconsistent path if in inconsistent mode.    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|path (String filepath)
specifier|protected
name|Path
name|path
parameter_list|(
name|String
name|filepath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|useInconsistentClient
argument_list|()
condition|?
name|super
operator|.
name|path
argument_list|(
name|FailureInjectionPolicy
operator|.
name|DEFAULT_DELAY_KEY_SUBSTRING
operator|+
literal|"/"
operator|+
name|filepath
argument_list|)
else|:
name|super
operator|.
name|path
argument_list|(
name|filepath
argument_list|)
return|;
block|}
comment|/**    * Creates a configuration for commit operations: commit is enabled in the FS    * and output is multipart to on-heap arrays.    * @return a configuration to use when creating an FS.    */
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|getTestBucketName
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|removeBucketOverrides
argument_list|(
name|bucketName
argument_list|,
name|conf
argument_list|,
name|MAGIC_COMMITTER_ENABLED
argument_list|,
name|S3A_COMMITTER_FACTORY_KEY
argument_list|,
name|FS_S3A_COMMITTER_NAME
argument_list|,
name|FS_S3A_COMMITTER_STAGING_CONFLICT_MODE
argument_list|,
name|FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES
argument_list|,
name|FAST_UPLOAD_BUFFER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|MAGIC_COMMITTER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|MIN_MULTIPART_THRESHOLD
argument_list|,
name|MULTIPART_MIN_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MULTIPART_SIZE
argument_list|,
name|MULTIPART_MIN_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FAST_UPLOAD_BUFFER
argument_list|,
name|FAST_UPLOAD_BUFFER_ARRAY
argument_list|)
expr_stmt|;
if|if
condition|(
name|useInconsistentClient
argument_list|()
condition|)
block|{
name|enableInconsistentS3Client
argument_list|(
name|conf
argument_list|,
name|CONSISTENCY_DELAY
argument_list|)
expr_stmt|;
block|}
return|return
name|conf
return|;
block|}
comment|/**    * Get the log; can be overridden for test case log.    * @return a log.    */
DECL|method|log ()
specifier|public
name|Logger
name|log
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
comment|/***    * Bind to the named committer.    *    * @param conf configuration    * @param factory factory name    * @param committerName committer; set if non null/empty    */
DECL|method|bindCommitter (Configuration conf, String factory, String committerName)
specifier|protected
name|void
name|bindCommitter
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|factory
parameter_list|,
name|String
name|committerName
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|S3A_COMMITTER_FACTORY_KEY
argument_list|,
name|factory
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|committerName
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_NAME
argument_list|,
name|committerName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Clean up a directory.    * Waits for consistency if needed    * @param dir directory    * @param conf configuration    * @throws IOException failure    */
DECL|method|rmdir (Path dir, Configuration conf)
specifier|public
name|void
name|rmdir
parameter_list|(
name|Path
name|dir
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|describe
argument_list|(
literal|"deleting %s"
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|dir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|waitForConsistency
argument_list|()
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|waitForConsistency
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Setup will use inconsistent client if {@link #useInconsistentClient()}    * is true.    * @throws Exception failure.    */
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
if|if
condition|(
name|useInconsistentClient
argument_list|()
condition|)
block|{
name|AmazonS3
name|client
init|=
name|getFileSystem
argument_list|()
operator|.
name|getAmazonS3ClientForTesting
argument_list|(
literal|"fault injection"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"AWS client is not inconsistent, even though the test requirees it "
operator|+
name|client
argument_list|,
name|client
operator|instanceof
name|InconsistentAmazonS3Client
argument_list|)
expr_stmt|;
name|inconsistentClient
operator|=
operator|(
name|InconsistentAmazonS3Client
operator|)
name|client
expr_stmt|;
block|}
block|}
comment|/**    * Create a random Job ID using the fork ID as part of the number.    * @return fork ID string in a format parseable by Jobs    * @throws Exception failure    */
DECL|method|randomJobId ()
specifier|public
specifier|static
name|String
name|randomJobId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testUniqueForkId
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|TEST_UNIQUE_FORK_ID
argument_list|,
literal|"0001"
argument_list|)
decl_stmt|;
name|int
name|l
init|=
name|testUniqueForkId
operator|.
name|length
argument_list|()
decl_stmt|;
name|String
name|trailingDigits
init|=
name|testUniqueForkId
operator|.
name|substring
argument_list|(
name|l
operator|-
literal|4
argument_list|,
name|l
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|digitValue
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|trailingDigits
argument_list|)
decl_stmt|;
return|return
name|String
operator|.
name|format
argument_list|(
literal|"20070712%04d_%04d"
argument_list|,
call|(
name|long
call|)
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|1000
argument_list|)
argument_list|,
name|digitValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Failed to parse "
operator|+
name|trailingDigits
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Teardown waits for the consistency delay and resets failure count, so    * FS is stable, before the superclass teardown is called. This    * should clean things up better.    * @throws Exception failure.    */
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"teardown"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"AbstractCommitITest::teardown"
argument_list|)
expr_stmt|;
name|waitForConsistency
argument_list|()
expr_stmt|;
comment|// make sure there are no failures any more
name|resetFailures
argument_list|()
expr_stmt|;
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Wait a multiple of the inconsistency delay for things to stabilize;    * no-op if the consistent client is used.    * @throws InterruptedIOException if the sleep is interrupted    */
DECL|method|waitForConsistency ()
specifier|protected
name|void
name|waitForConsistency
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
if|if
condition|(
name|useInconsistentClient
argument_list|()
operator|&&
name|inconsistentClient
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2
operator|*
name|inconsistentClient
operator|.
name|getDelayKeyMsec
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
call|(
name|InterruptedIOException
call|)
argument_list|(
operator|new
name|InterruptedIOException
argument_list|(
literal|"while waiting for consistency: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Set the throttling factor on requests.    * @param p probability of a throttling occurring: 0-1.0    */
DECL|method|setThrottling (float p)
specifier|protected
name|void
name|setThrottling
parameter_list|(
name|float
name|p
parameter_list|)
block|{
if|if
condition|(
name|inconsistentClient
operator|!=
literal|null
condition|)
block|{
name|inconsistentClient
operator|.
name|setThrottleProbability
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set the throttling factor on requests and number of calls to throttle.    * @param p probability of a throttling occurring: 0-1.0    * @param limit limit to number of calls which fail    */
DECL|method|setThrottling (float p, int limit)
specifier|protected
name|void
name|setThrottling
parameter_list|(
name|float
name|p
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|inconsistentClient
operator|!=
literal|null
condition|)
block|{
name|inconsistentClient
operator|.
name|setThrottleProbability
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|setFailureLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
comment|/**    * Turn off throttling.    */
DECL|method|resetFailures ()
specifier|protected
name|void
name|resetFailures
parameter_list|()
block|{
if|if
condition|(
name|inconsistentClient
operator|!=
literal|null
condition|)
block|{
name|setThrottling
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set failure limit.    * @param limit limit to number of calls which fail    */
DECL|method|setFailureLimit (int limit)
specifier|private
name|void
name|setFailureLimit
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
if|if
condition|(
name|inconsistentClient
operator|!=
literal|null
condition|)
block|{
name|inconsistentClient
operator|.
name|setFailureLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Abort all multipart uploads under a path.    * @param path path for uploads to abort; may be null    * @return a count of aborts    * @throws IOException trouble.    */
DECL|method|abortMultipartUploadsUnderPath (Path path)
specifier|protected
name|int
name|abortMultipartUploadsUnderPath
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
operator|&&
name|path
operator|!=
literal|null
condition|)
block|{
name|String
name|key
init|=
name|fs
operator|.
name|pathToKey
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|WriteOperationHelper
name|writeOps
init|=
name|fs
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
name|int
name|count
init|=
name|writeOps
operator|.
name|abortMultipartUploadsUnderPath
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
name|log
argument_list|()
operator|.
name|info
argument_list|(
literal|"Multipart uploads deleted: {}"
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**    * Assert that there *are* pending MPUs.    * @param path path to look under    * @throws IOException IO failure    */
DECL|method|assertMultipartUploadsPending (Path path)
specifier|protected
name|void
name|assertMultipartUploadsPending
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
literal|"No multipart uploads in progress under "
operator|+
name|path
argument_list|,
name|countMultipartUploads
argument_list|(
name|path
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that there *are no* pending MPUs; assertion failure will include    * the list of pending writes.    * @param path path to look under    * @throws IOException IO failure    */
DECL|method|assertNoMultipartUploadsPending (Path path)
specifier|protected
name|void
name|assertNoMultipartUploadsPending
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|uploads
init|=
name|listMultipartUploads
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|pathToPrefix
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uploads
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|result
init|=
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
decl_stmt|;
name|fail
argument_list|(
literal|"Multipart uploads in progress under "
operator|+
name|path
operator|+
literal|" \n"
operator|+
name|result
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Count the number of MPUs under a path.    * @param path path to scan    * @return count    * @throws IOException IO failure    */
DECL|method|countMultipartUploads (Path path)
specifier|protected
name|int
name|countMultipartUploads
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|countMultipartUploads
argument_list|(
name|pathToPrefix
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Count the number of MPUs under a prefix; also logs them.    * @param prefix prefix to scan    * @return count    * @throws IOException IO failure    */
DECL|method|countMultipartUploads (String prefix)
specifier|protected
name|int
name|countMultipartUploads
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|listMultipartUploads
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|prefix
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Map from a path to a prefix.    * @param path path    * @return the key    */
DECL|method|pathToPrefix (Path path)
specifier|private
name|String
name|pathToPrefix
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|==
literal|null
condition|?
literal|""
else|:
name|getFileSystem
argument_list|()
operator|.
name|pathToKey
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Verify that the specified dir has the {@code _SUCCESS} marker    * and that it can be loaded.    * The contents will be logged and returned.    * @param dir directory to scan    * @return the loaded success data    * @throws IOException IO Failure    */
DECL|method|verifySuccessMarker (Path dir)
specifier|protected
name|SuccessData
name|verifySuccessMarker
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|validateSuccessFile
argument_list|(
name|dir
argument_list|,
literal|""
argument_list|,
name|getFileSystem
argument_list|()
argument_list|,
literal|"query"
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Read a UTF-8 file.    * @param path path to read    * @return string value    * @throws IOException IO failure    */
DECL|method|readFile (Path path)
specifier|protected
name|String
name|readFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContractTestUtils
operator|.
name|readUTF8
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Assert that the given dir does not have the {@code _SUCCESS} marker.    * @param dir dir to scan    * @throws IOException IO Failure    */
DECL|method|assertSuccessMarkerDoesNotExist (Path dir)
specifier|protected
name|void
name|assertSuccessMarkerDoesNotExist
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|assertPathDoesNotExist
argument_list|(
literal|"Success marker"
argument_list|,
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|_SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Closeable which can be used to safely close writers in    * a try-with-resources block..    */
DECL|class|CloseWriter
specifier|protected
specifier|static
class|class
name|CloseWriter
implements|implements
name|AutoCloseable
block|{
DECL|field|writer
specifier|private
specifier|final
name|RecordWriter
name|writer
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|TaskAttemptContext
name|context
decl_stmt|;
DECL|method|CloseWriter (RecordWriter writer, TaskAttemptContext context)
specifier|public
name|CloseWriter
parameter_list|(
name|RecordWriter
name|writer
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"When closing {} on context {}"
argument_list|,
name|writer
argument_list|,
name|context
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a task attempt for a Job. This is based on the code    * run in the MR AM, creating a task (0) for the job, then a task    * attempt (0).    * @param jobId job ID    * @param jContext job context    * @return the task attempt.    */
DECL|method|taskAttemptForJob (JobId jobId, JobContext jContext)
specifier|public
specifier|static
name|TaskAttemptContext
name|taskAttemptForJob
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|JobContext
name|jContext
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
name|taskID
init|=
name|MRBuilderUtils
operator|.
name|newTaskId
argument_list|(
name|jobId
argument_list|,
literal|0
argument_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
operator|.
name|MAP
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|MRBuilderUtils
operator|.
name|newTaskAttemptId
argument_list|(
name|taskID
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|jContext
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|attemptID
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Load in the success data marker: this guarantees that an S3A    * committer was used.    * @param outputPath path of job    * @param committerName name of committer to match, or ""    * @param fs filesystem    * @param origin origin (e.g. "teragen" for messages)    * @param minimumFileCount minimum number of files to have been created    * @return the success data    * @throws IOException IO failure    */
DECL|method|validateSuccessFile (final Path outputPath, final String committerName, final S3AFileSystem fs, final String origin, final int minimumFileCount)
specifier|public
specifier|static
name|SuccessData
name|validateSuccessFile
parameter_list|(
specifier|final
name|Path
name|outputPath
parameter_list|,
specifier|final
name|String
name|committerName
parameter_list|,
specifier|final
name|S3AFileSystem
name|fs
parameter_list|,
specifier|final
name|String
name|origin
parameter_list|,
specifier|final
name|int
name|minimumFileCount
parameter_list|)
throws|throws
name|IOException
block|{
name|SuccessData
name|successData
init|=
name|loadSuccessFile
argument_list|(
name|fs
argument_list|,
name|outputPath
argument_list|,
name|origin
argument_list|)
decl_stmt|;
name|String
name|commitDetails
init|=
name|successData
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Committer name "
operator|+
name|committerName
operator|+
literal|"\n{}"
argument_list|,
name|commitDetails
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Committer statistics: \n{}"
argument_list|,
name|successData
operator|.
name|dumpMetrics
argument_list|(
literal|"  "
argument_list|,
literal|" = "
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Diagnostics\n{}"
argument_list|,
name|successData
operator|.
name|dumpDiagnostics
argument_list|(
literal|"  "
argument_list|,
literal|" = "
argument_list|,
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|committerName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong committer in "
operator|+
name|commitDetails
argument_list|,
name|committerName
argument_list|,
name|successData
operator|.
name|getCommitter
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assertions
operator|.
name|assertThat
argument_list|(
name|successData
operator|.
name|getFilenames
argument_list|()
argument_list|)
operator|.
name|describedAs
argument_list|(
literal|"Files committed"
argument_list|)
operator|.
name|hasSizeGreaterThanOrEqualTo
argument_list|(
name|minimumFileCount
argument_list|)
expr_stmt|;
return|return
name|successData
return|;
block|}
comment|/**    * Load a success file; fail if the file is empty/nonexistent.    * @param fs filesystem    * @param outputPath directory containing the success file.    * @param origin origin of the file    * @return the loaded file.    * @throws IOException failure to find/load the file    * @throws AssertionError file is 0-bytes long,    */
DECL|method|loadSuccessFile (final S3AFileSystem fs, final Path outputPath, final String origin)
specifier|public
specifier|static
name|SuccessData
name|loadSuccessFile
parameter_list|(
specifier|final
name|S3AFileSystem
name|fs
parameter_list|,
specifier|final
name|Path
name|outputPath
parameter_list|,
specifier|final
name|String
name|origin
parameter_list|)
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"Output directory "
operator|+
name|outputPath
operator|+
literal|" from "
operator|+
name|origin
operator|+
literal|" not found: Job may not have executed"
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|Path
name|success
init|=
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|_SUCCESS
argument_list|)
decl_stmt|;
name|FileStatus
name|status
init|=
name|ContractTestUtils
operator|.
name|verifyPathExists
argument_list|(
name|fs
argument_list|,
literal|"job completion marker "
operator|+
name|success
operator|+
literal|" from "
operator|+
name|origin
operator|+
literal|" not found: Job may have failed"
argument_list|,
name|success
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"_SUCCESS outout from "
operator|+
name|origin
operator|+
literal|" is not a file "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"0 byte success file "
operator|+
name|success
operator|+
literal|" from "
operator|+
name|origin
operator|+
literal|"; an S3A committer was not used"
argument_list|,
name|status
operator|.
name|getLen
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading committer success file {}"
argument_list|,
name|success
argument_list|)
expr_stmt|;
return|return
name|SuccessData
operator|.
name|load
argument_list|(
name|fs
argument_list|,
name|success
argument_list|)
return|;
block|}
block|}
end_class

end_unit

