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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|ExecutorCompletionService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|Future
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
name|long
name|count
init|=
name|getOperationCount
argument_list|()
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
name|fs
operator|.
name|mkdirs
argument_list|(
name|finalDir
argument_list|)
expr_stmt|;
name|int
name|testBufferSize
init|=
name|fs
operator|.
name|getConf
argument_list|()
operator|.
name|getInt
argument_list|(
name|ContractTestUtils
operator|.
name|IO_CHUNK_BUFFER_SIZE
argument_list|,
name|ContractTestUtils
operator|.
name|DEFAULT_IO_CHUNK_BUFFER_SIZE
argument_list|)
decl_stmt|;
comment|// use Executor to speed up file creation
name|ExecutorService
name|exec
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|16
argument_list|)
decl_stmt|;
specifier|final
name|ExecutorCompletionService
argument_list|<
name|Boolean
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|exec
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|ContractTestUtils
operator|.
name|dataset
argument_list|(
name|testBufferSize
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|String
name|fileName
init|=
literal|"foo-"
operator|+
name|i
decl_stmt|;
name|completionService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|IOException
block|{
name|ContractTestUtils
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|srcDir
argument_list|,
name|fileName
argument_list|)
argument_list|,
literal|false
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|srcDir
argument_list|,
name|fileName
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|Future
argument_list|<
name|Boolean
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|future
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"cannot create file"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while uploading file"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|exec
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|int
name|nSrcFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|srcDir
argument_list|)
operator|.
name|length
decl_stmt|;
name|fs
operator|.
name|rename
argument_list|(
name|srcDir
argument_list|,
name|finalDir
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
literal|"foo-"
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
literal|"foo-"
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
literal|"foo-"
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
literal|"foo-"
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
literal|"foo-"
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
literal|"foo-"
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
name|assertDeleted
argument_list|(
name|fs
argument_list|,
name|finalDir
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

