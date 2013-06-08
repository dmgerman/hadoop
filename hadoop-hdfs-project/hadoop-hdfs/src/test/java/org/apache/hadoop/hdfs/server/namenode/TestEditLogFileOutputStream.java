begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Test the EditLogFileOutputStream  */
end_comment

begin_class
DECL|class|TestEditLogFileOutputStream
specifier|public
class|class
name|TestEditLogFileOutputStream
block|{
DECL|field|TEST_DIR
specifier|private
specifier|final
specifier|static
name|File
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|TEST_EDITS
specifier|private
specifier|static
specifier|final
name|File
name|TEST_EDITS
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testEditLogFileOutput.log"
argument_list|)
decl_stmt|;
DECL|field|MIN_PREALLOCATION_LENGTH
specifier|final
specifier|static
name|int
name|MIN_PREALLOCATION_LENGTH
init|=
name|EditLogFileOutputStream
operator|.
name|MIN_PREALLOCATION_LENGTH
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
static|static
block|{
comment|// No need to fsync for the purposes of tests. This makes
comment|// the tests run much faster.
name|EditLogFileOutputStream
operator|.
name|setShouldSkipFsyncForTesting
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|After
DECL|method|deleteEditsFile ()
specifier|public
name|void
name|deleteEditsFile
parameter_list|()
block|{
if|if
condition|(
name|TEST_EDITS
operator|.
name|exists
argument_list|()
condition|)
name|TEST_EDITS
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
DECL|method|flushAndCheckLength (EditLogFileOutputStream elos, long expectedLength)
specifier|static
name|void
name|flushAndCheckLength
parameter_list|(
name|EditLogFileOutputStream
name|elos
parameter_list|,
name|long
name|expectedLength
parameter_list|)
throws|throws
name|IOException
block|{
name|elos
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|elos
operator|.
name|flushAndSync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedLength
argument_list|,
name|elos
operator|.
name|getFile
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests writing to the EditLogFileOutputStream.  Due to preallocation, the    * length of the edit log will usually be longer than its valid contents.    */
annotation|@
name|Test
DECL|method|testRawWrites ()
specifier|public
name|void
name|testRawWrites
parameter_list|()
throws|throws
name|IOException
block|{
name|EditLogFileOutputStream
name|elos
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|conf
argument_list|,
name|TEST_EDITS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|small
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|8
block|,
literal|7
block|}
decl_stmt|;
name|elos
operator|.
name|create
argument_list|()
expr_stmt|;
comment|// The first (small) write we make extends the file by 1 MB due to
comment|// preallocation.
name|elos
operator|.
name|writeRaw
argument_list|(
name|small
argument_list|,
literal|0
argument_list|,
name|small
operator|.
name|length
argument_list|)
expr_stmt|;
name|flushAndCheckLength
argument_list|(
name|elos
argument_list|,
name|MIN_PREALLOCATION_LENGTH
argument_list|)
expr_stmt|;
comment|// The next small write we make goes into the area that was already
comment|// preallocated.
name|elos
operator|.
name|writeRaw
argument_list|(
name|small
argument_list|,
literal|0
argument_list|,
name|small
operator|.
name|length
argument_list|)
expr_stmt|;
name|flushAndCheckLength
argument_list|(
name|elos
argument_list|,
name|MIN_PREALLOCATION_LENGTH
argument_list|)
expr_stmt|;
comment|// Now we write enough bytes so that we exceed the minimum preallocated
comment|// length.
specifier|final
name|int
name|BIG_WRITE_LENGTH
init|=
literal|3
operator|*
name|MIN_PREALLOCATION_LENGTH
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|4096
index|]
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
name|buf
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|total
init|=
name|BIG_WRITE_LENGTH
decl_stmt|;
while|while
condition|(
name|total
operator|>
literal|0
condition|)
block|{
name|int
name|toWrite
init|=
operator|(
name|total
operator|>
name|buf
operator|.
name|length
operator|)
condition|?
name|buf
operator|.
name|length
else|:
name|total
decl_stmt|;
name|elos
operator|.
name|writeRaw
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toWrite
argument_list|)
expr_stmt|;
name|total
operator|-=
name|toWrite
expr_stmt|;
block|}
name|flushAndCheckLength
argument_list|(
name|elos
argument_list|,
literal|4
operator|*
name|MIN_PREALLOCATION_LENGTH
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|elos
operator|!=
literal|null
condition|)
name|elos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Tests EditLogFileOutputStream doesn't throw NullPointerException on    * close/abort sequence. See HDFS-2011.    */
annotation|@
name|Test
DECL|method|testEditLogFileOutputStreamCloseAbort ()
specifier|public
name|void
name|testEditLogFileOutputStreamCloseAbort
parameter_list|()
throws|throws
name|IOException
block|{
comment|// abort after a close should just ignore
name|EditLogFileOutputStream
name|editLogStream
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|conf
argument_list|,
name|TEST_EDITS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|editLogStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|editLogStream
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
comment|/**    * Tests EditLogFileOutputStream doesn't throw NullPointerException on    * close/close sequence. See HDFS-2011.    */
annotation|@
name|Test
DECL|method|testEditLogFileOutputStreamCloseClose ()
specifier|public
name|void
name|testEditLogFileOutputStreamCloseClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|// close after a close should result in an IOE
name|EditLogFileOutputStream
name|editLogStream
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|conf
argument_list|,
name|TEST_EDITS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|editLogStream
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|editLogStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|String
name|msg
init|=
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ioe
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
argument_list|,
name|msg
operator|.
name|contains
argument_list|(
literal|"Trying to use aborted output stream"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Tests EditLogFileOutputStream doesn't throw NullPointerException on being    * abort/abort sequence. See HDFS-2011.    */
annotation|@
name|Test
DECL|method|testEditLogFileOutputStreamAbortAbort ()
specifier|public
name|void
name|testEditLogFileOutputStreamAbortAbort
parameter_list|()
throws|throws
name|IOException
block|{
comment|// abort after a close should just ignore
name|EditLogFileOutputStream
name|editLogStream
init|=
operator|new
name|EditLogFileOutputStream
argument_list|(
name|conf
argument_list|,
name|TEST_EDITS
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|editLogStream
operator|.
name|abort
argument_list|()
expr_stmt|;
name|editLogStream
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

