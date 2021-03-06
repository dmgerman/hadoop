begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.scale
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
name|scale
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
name|Statistic
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Statistic
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test the performance of listing files/directories.  */
end_comment

begin_class
DECL|class|ITestS3ADirectoryPerformance
specifier|public
class|class
name|ITestS3ADirectoryPerformance
extends|extends
name|S3AScaleTestBase
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
name|ITestS3ADirectoryPerformance
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testListOperations ()
specifier|public
name|void
name|testListOperations
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Test recursive list operations"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|scaleTestDir
init|=
name|path
argument_list|(
literal|"testListOperations"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|listDir
init|=
operator|new
name|Path
argument_list|(
name|scaleTestDir
argument_list|,
literal|"lists"
argument_list|)
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// scale factor.
name|int
name|scale
init|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|KEY_DIRECTORY_COUNT
argument_list|,
name|DEFAULT_DIRECTORY_COUNT
argument_list|)
decl_stmt|;
name|int
name|width
init|=
name|scale
decl_stmt|;
name|int
name|depth
init|=
name|scale
decl_stmt|;
name|int
name|files
init|=
name|scale
decl_stmt|;
name|MetricDiff
name|metadataRequests
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|OBJECT_METADATA_REQUESTS
argument_list|)
decl_stmt|;
name|MetricDiff
name|listRequests
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|OBJECT_LIST_REQUESTS
argument_list|)
decl_stmt|;
name|MetricDiff
name|listContinueRequests
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|OBJECT_CONTINUE_LIST_REQUESTS
argument_list|)
decl_stmt|;
name|MetricDiff
name|listStatusCalls
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|INVOCATION_LIST_FILES
argument_list|)
decl_stmt|;
name|MetricDiff
name|getFileStatusCalls
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|INVOCATION_GET_FILE_STATUS
argument_list|)
decl_stmt|;
name|NanoTimer
name|createTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|TreeScanResults
name|created
init|=
name|createSubdirs
argument_list|(
name|fs
argument_list|,
name|listDir
argument_list|,
name|depth
argument_list|,
name|width
argument_list|,
name|files
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// add some empty directories
name|int
name|emptyDepth
init|=
literal|1
operator|*
name|scale
decl_stmt|;
name|int
name|emptyWidth
init|=
literal|3
operator|*
name|scale
decl_stmt|;
name|created
operator|.
name|add
argument_list|(
name|createSubdirs
argument_list|(
name|fs
argument_list|,
name|listDir
argument_list|,
name|emptyDepth
argument_list|,
name|emptyWidth
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"empty"
argument_list|,
literal|"f-"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|createTimer
operator|.
name|end
argument_list|(
literal|"Time to create %s"
argument_list|,
name|created
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per operation: {}"
argument_list|,
name|toHuman
argument_list|(
name|createTimer
operator|.
name|nanosPerOperation
argument_list|(
name|created
operator|.
name|totalCount
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|printThenReset
argument_list|(
name|LOG
argument_list|,
name|metadataRequests
argument_list|,
name|listRequests
argument_list|,
name|listContinueRequests
argument_list|,
name|listStatusCalls
argument_list|,
name|getFileStatusCalls
argument_list|)
expr_stmt|;
name|describe
argument_list|(
literal|"Listing files via treewalk"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Scan the directory via an explicit tree walk.
comment|// This is the baseline for any listing speedups.
name|NanoTimer
name|treeWalkTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|TreeScanResults
name|treewalkResults
init|=
name|treeWalk
argument_list|(
name|fs
argument_list|,
name|listDir
argument_list|)
decl_stmt|;
name|treeWalkTimer
operator|.
name|end
argument_list|(
literal|"List status via treewalk of %s"
argument_list|,
name|created
argument_list|)
expr_stmt|;
name|printThenReset
argument_list|(
name|LOG
argument_list|,
name|metadataRequests
argument_list|,
name|listRequests
argument_list|,
name|listContinueRequests
argument_list|,
name|listStatusCalls
argument_list|,
name|getFileStatusCalls
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Files found in listFiles(recursive=true) "
operator|+
literal|" created="
operator|+
name|created
operator|+
literal|" listed="
operator|+
name|treewalkResults
argument_list|,
name|created
operator|.
name|getFileCount
argument_list|()
argument_list|,
name|treewalkResults
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|describe
argument_list|(
literal|"Listing files via listFiles(recursive=true)"
argument_list|)
expr_stmt|;
comment|// listFiles() does the recursion internally
name|NanoTimer
name|listFilesRecursiveTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|TreeScanResults
name|listFilesResults
init|=
operator|new
name|TreeScanResults
argument_list|(
name|fs
operator|.
name|listFiles
argument_list|(
name|listDir
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|listFilesRecursiveTimer
operator|.
name|end
argument_list|(
literal|"listFiles(recursive=true) of %s"
argument_list|,
name|created
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Files found in listFiles(recursive=true) "
operator|+
literal|" created="
operator|+
name|created
operator|+
literal|" listed="
operator|+
name|listFilesResults
argument_list|,
name|created
operator|.
name|getFileCount
argument_list|()
argument_list|,
name|listFilesResults
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// only two list operations should have taken place
name|print
argument_list|(
name|LOG
argument_list|,
name|metadataRequests
argument_list|,
name|listRequests
argument_list|,
name|listContinueRequests
argument_list|,
name|listStatusCalls
argument_list|,
name|getFileStatusCalls
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|hasMetadataStore
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|listRequests
operator|.
name|toString
argument_list|()
argument_list|,
literal|2
argument_list|,
name|listRequests
operator|.
name|diff
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|reset
argument_list|(
name|metadataRequests
argument_list|,
name|listRequests
argument_list|,
name|listContinueRequests
argument_list|,
name|listStatusCalls
argument_list|,
name|getFileStatusCalls
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|describe
argument_list|(
literal|"deletion"
argument_list|)
expr_stmt|;
comment|// deletion at the end of the run
name|NanoTimer
name|deleteTimer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|listDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|deleteTimer
operator|.
name|end
argument_list|(
literal|"Deleting directory tree"
argument_list|)
expr_stmt|;
name|printThenReset
argument_list|(
name|LOG
argument_list|,
name|metadataRequests
argument_list|,
name|listRequests
argument_list|,
name|listContinueRequests
argument_list|,
name|listStatusCalls
argument_list|,
name|getFileStatusCalls
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTimeToStatEmptyDirectory ()
specifier|public
name|void
name|testTimeToStatEmptyDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Time to stat an empty directory"
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"empty"
argument_list|)
decl_stmt|;
name|getFileSystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|timeToStatPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeToStatNonEmptyDirectory ()
specifier|public
name|void
name|testTimeToStatNonEmptyDirectory
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Time to stat a non-empty directory"
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"dir"
argument_list|)
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|touch
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|path
argument_list|,
literal|"file"
argument_list|)
argument_list|)
expr_stmt|;
name|timeToStatPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeToStatFile ()
specifier|public
name|void
name|testTimeToStatFile
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Time to stat a simple file"
argument_list|)
expr_stmt|;
name|Path
name|path
init|=
name|path
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|timeToStatPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeToStatRoot ()
specifier|public
name|void
name|testTimeToStatRoot
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Time to stat the root path"
argument_list|)
expr_stmt|;
name|timeToStatPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|timeToStatPath (Path path)
specifier|private
name|void
name|timeToStatPath
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|describe
argument_list|(
literal|"Timing getFileStatus(\"%s\")"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|MetricDiff
name|metadataRequests
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|Statistic
operator|.
name|OBJECT_METADATA_REQUESTS
argument_list|)
decl_stmt|;
name|MetricDiff
name|listRequests
init|=
operator|new
name|MetricDiff
argument_list|(
name|fs
argument_list|,
name|Statistic
operator|.
name|OBJECT_LIST_REQUESTS
argument_list|)
decl_stmt|;
name|long
name|attempts
init|=
name|getOperationCount
argument_list|()
decl_stmt|;
name|NanoTimer
name|timer
init|=
operator|new
name|NanoTimer
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|l
init|=
literal|0
init|;
name|l
operator|<
name|attempts
condition|;
name|l
operator|++
control|)
block|{
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|end
argument_list|(
literal|"Time to execute %d getFileStatusCalls"
argument_list|,
name|attempts
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Time per call: {}"
argument_list|,
name|toHuman
argument_list|(
name|timer
operator|.
name|nanosPerOperation
argument_list|(
name|attempts
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"metadata: {}"
argument_list|,
name|metadataRequests
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"metadata per operation {}"
argument_list|,
name|metadataRequests
operator|.
name|diff
argument_list|()
operator|/
name|attempts
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"listObjects: {}"
argument_list|,
name|listRequests
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"listObjects: per operation {}"
argument_list|,
name|listRequests
operator|.
name|diff
argument_list|()
operator|/
name|attempts
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

