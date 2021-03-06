begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|impl
operator|.
name|ITestPartialRenamesDeletes
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
name|impl
operator|.
name|ITestPartialRenamesDeletes
operator|.
name|createFiles
import|;
end_import

begin_comment
comment|/**  * Test some scalable operations related to file renaming and deletion.  */
end_comment

begin_class
DECL|class|ITestS3ADeleteManyFiles
specifier|public
class|class
name|ITestS3ADeleteManyFiles
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
name|ITestS3ADeleteManyFiles
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
name|ITestPartialRenamesDeletes
operator|.
name|PREFIX
decl_stmt|;
comment|/**    * CAUTION: If this test starts failing, please make sure that the    * {@link org.apache.hadoop.fs.s3a.Constants#MAX_THREADS} configuration is not    * set too low. Alternatively, consider reducing the    *<code>scale.test.operation.count</code> parameter in    *<code>getOperationCount()</code>.    *    * @see #getOperationCount()    */
annotation|@
name|Test
DECL|method|testBulkRenameAndDelete ()
specifier|public
name|void
name|testBulkRenameAndDelete
parameter_list|()
throws|throws
name|Throwable
block|{
specifier|final
name|Path
name|scaleTestDir
init|=
name|path
argument_list|(
literal|"testBulkRenameAndDelete"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|srcDir
init|=
operator|new
name|Path
argument_list|(
name|scaleTestDir
argument_list|,
literal|"src"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|finalDir
init|=
operator|new
name|Path
argument_list|(
name|scaleTestDir
argument_list|,
literal|"final"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|KEY_FILE_COUNT
argument_list|,
name|DEFAULT_FILE_COUNT
argument_list|)
decl_stmt|;
specifier|final
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
name|ContractTestUtils
operator|.
name|rm
argument_list|(
name|fs
argument_list|,
name|scaleTestDir
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|srcDir
argument_list|)
expr_stmt|;
name|createFiles
argument_list|(
name|fs
argument_list|,
name|srcDir
argument_list|,
literal|1
argument_list|,
name|count
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|statuses
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|srcDir
argument_list|)
decl_stmt|;
name|int
name|nSrcFiles
init|=
name|statuses
operator|.
name|length
decl_stmt|;
name|long
name|sourceSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileStatus
name|status
range|:
name|statuses
control|)
block|{
name|sourceSize
operator|+=
name|status
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Source file Count"
argument_list|,
name|count
argument_list|,
name|nSrcFiles
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|renameTimer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
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
literal|"Rename %s to %s"
argument_list|,
name|srcDir
argument_list|,
name|finalDir
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
literal|"Rename failed"
argument_list|,
name|fs
operator|.
name|rename
argument_list|(
name|srcDir
argument_list|,
name|finalDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|renameTimer
operator|.
name|end
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Effective rename bandwidth {} MB/s"
argument_list|,
name|renameTimer
operator|.
name|bandwidthDescription
argument_list|(
name|sourceSize
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Time to rename a file: %,03f milliseconds"
argument_list|,
operator|(
name|renameTimer
operator|.
name|nanosPerOperation
argument_list|(
name|count
argument_list|)
operator|*
literal|1.0f
operator|)
operator|/
literal|1.0e6
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nSrcFiles
argument_list|,
name|fs
operator|.
name|listStatus
argument_list|(
name|finalDir
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"not deleted after rename"
argument_list|,
operator|new
name|Path
argument_list|(
name|srcDir
argument_list|,
name|PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"not deleted after rename"
argument_list|,
operator|new
name|Path
argument_list|(
name|srcDir
argument_list|,
name|PREFIX
operator|+
name|count
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathDoesNotExist
argument_list|(
name|fs
argument_list|,
literal|"not deleted after rename"
argument_list|,
operator|new
name|Path
argument_list|(
name|srcDir
argument_list|,
name|PREFIX
operator|+
operator|(
name|count
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"not renamed to dest dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|finalDir
argument_list|,
name|PREFIX
operator|+
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"not renamed to dest dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|finalDir
argument_list|,
name|PREFIX
operator|+
name|count
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|assertPathExists
argument_list|(
name|fs
argument_list|,
literal|"not renamed to dest dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|finalDir
argument_list|,
name|PREFIX
operator|+
operator|(
name|count
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|NanoTimer
name|deleteTimer
init|=
operator|new
name|ContractTestUtils
operator|.
name|NanoTimer
argument_list|()
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
literal|"Delete subtree %s"
argument_list|,
name|finalDir
argument_list|)
init|)
block|{
name|assertDeleted
argument_list|(
name|finalDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|deleteTimer
operator|.
name|end
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Time to delete an object %,03f milliseconds"
argument_list|,
operator|(
name|deleteTimer
operator|.
name|nanosPerOperation
argument_list|(
name|count
argument_list|)
operator|*
literal|1.0f
operator|)
operator|/
literal|1.0e6
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

