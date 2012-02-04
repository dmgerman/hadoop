begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

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
name|FileInputStream
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|fs
operator|.
name|RawLocalFileSystem
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
name|LongWritable
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
name|MapFile
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
name|NullWritable
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|TestFileOutputCommitter
specifier|public
class|class
name|TestFileOutputCommitter
extends|extends
name|TestCase
block|{
DECL|field|outDir
specifier|private
specifier|static
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|,
literal|"output"
argument_list|)
decl_stmt|;
comment|// A random task attempt id for testing.
DECL|field|attempt
specifier|private
specifier|static
name|String
name|attempt
init|=
literal|"attempt_200707121733_0001_m_000000_0"
decl_stmt|;
DECL|field|partFile
specifier|private
specifier|static
name|String
name|partFile
init|=
literal|"part-00000"
decl_stmt|;
DECL|field|taskID
specifier|private
specifier|static
name|TaskAttemptID
name|taskID
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|attempt
argument_list|)
decl_stmt|;
DECL|field|key1
specifier|private
name|Text
name|key1
init|=
operator|new
name|Text
argument_list|(
literal|"key1"
argument_list|)
decl_stmt|;
DECL|field|key2
specifier|private
name|Text
name|key2
init|=
operator|new
name|Text
argument_list|(
literal|"key2"
argument_list|)
decl_stmt|;
DECL|field|val1
specifier|private
name|Text
name|val1
init|=
operator|new
name|Text
argument_list|(
literal|"val1"
argument_list|)
decl_stmt|;
DECL|field|val2
specifier|private
name|Text
name|val2
init|=
operator|new
name|Text
argument_list|(
literal|"val2"
argument_list|)
decl_stmt|;
DECL|method|writeOutput (RecordWriter theRecordWriter, TaskAttemptContext context)
specifier|private
name|void
name|writeOutput
parameter_list|(
name|RecordWriter
name|theRecordWriter
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|NullWritable
name|nullWritable
init|=
name|NullWritable
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
name|val1
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|nullWritable
argument_list|,
name|val2
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|nullWritable
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
name|key2
argument_list|,
name|val2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|theRecordWriter
operator|.
name|close
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeMapFileOutput (RecordWriter theRecordWriter, TaskAttemptContext context)
specifier|private
name|void
name|writeMapFileOutput
parameter_list|(
name|RecordWriter
name|theRecordWriter
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|int
name|key
init|=
literal|0
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|key
operator|=
name|i
expr_stmt|;
name|Text
name|val
init|=
operator|(
name|i
operator|%
literal|2
operator|==
literal|1
operator|)
condition|?
name|val1
else|:
name|val2
decl_stmt|;
name|theRecordWriter
operator|.
name|write
argument_list|(
operator|new
name|LongWritable
argument_list|(
name|key
argument_list|)
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|theRecordWriter
operator|.
name|close
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRecovery ()
specifier|public
name|void
name|testRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRConstants
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
comment|// setup
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|setupTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
comment|// write output
name|TextOutputFormat
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|partFile
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writeOutput
argument_list|(
name|theRecordWriter
argument_list|,
name|tContext
argument_list|)
expr_stmt|;
comment|// do commit
name|committer
operator|.
name|commitTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
name|Path
name|jobTempDir1
init|=
name|committer
operator|.
name|getCommittedTaskPath
argument_list|(
name|tContext
argument_list|)
decl_stmt|;
name|File
name|jtd1
init|=
operator|new
name|File
argument_list|(
name|jobTempDir1
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|jtd1
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|validateContent
argument_list|(
name|jobTempDir1
argument_list|)
expr_stmt|;
comment|//now while running the second app attempt,
comment|//recover the task output from first attempt
name|JobConf
name|conf2
init|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|setInt
argument_list|(
name|MRConstants
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|JobContext
name|jContext2
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf2
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext2
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf2
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer2
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
name|committer2
operator|.
name|setupJob
argument_list|(
name|jContext2
argument_list|)
expr_stmt|;
name|Path
name|jobTempDir2
init|=
name|committer2
operator|.
name|getCommittedTaskPath
argument_list|(
name|tContext2
argument_list|)
decl_stmt|;
name|committer2
operator|.
name|recoverTask
argument_list|(
name|tContext2
argument_list|)
expr_stmt|;
name|File
name|jtd2
init|=
operator|new
name|File
argument_list|(
name|jobTempDir2
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|jtd2
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|validateContent
argument_list|(
name|jobTempDir2
argument_list|)
expr_stmt|;
name|committer2
operator|.
name|commitJob
argument_list|(
name|jContext2
argument_list|)
expr_stmt|;
name|validateContent
argument_list|(
name|outDir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|validateContent (Path dir)
specifier|private
name|void
name|validateContent
parameter_list|(
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|fdir
init|=
operator|new
name|File
argument_list|(
name|dir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|expectedFile
init|=
operator|new
name|File
argument_list|(
name|fdir
argument_list|,
name|partFile
argument_list|)
decl_stmt|;
name|StringBuffer
name|expectedOutput
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key1
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|expectedOutput
operator|.
name|append
argument_list|(
name|key2
argument_list|)
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|val2
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|slurp
argument_list|(
name|expectedFile
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|output
argument_list|,
name|expectedOutput
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|validateMapFileOutputContent ( FileSystem fs, Path dir)
specifier|private
name|void
name|validateMapFileOutputContent
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// map output is a directory with index and data files
name|Path
name|expectedMapDir
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
name|partFile
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|expectedMapDir
argument_list|)
operator|.
name|isDirectory
argument_list|()
operator|)
assert|;
name|FileStatus
index|[]
name|files
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|expectedMapDir
argument_list|)
decl_stmt|;
name|int
name|fileCount
init|=
literal|0
decl_stmt|;
name|boolean
name|dataFileFound
init|=
literal|false
decl_stmt|;
name|boolean
name|indexFileFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FileStatus
name|f
range|:
name|files
control|)
block|{
if|if
condition|(
name|f
operator|.
name|isFile
argument_list|()
condition|)
block|{
operator|++
name|fileCount
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MapFile
operator|.
name|INDEX_FILE_NAME
argument_list|)
condition|)
block|{
name|indexFileFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|MapFile
operator|.
name|DATA_FILE_NAME
argument_list|)
condition|)
block|{
name|dataFileFound
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
assert|assert
operator|(
name|fileCount
operator|>
literal|0
operator|)
assert|;
assert|assert
operator|(
name|dataFileFound
operator|&&
name|indexFileFound
operator|)
assert|;
block|}
DECL|method|testCommitter ()
specifier|public
name|void
name|testCommitter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
comment|// setup
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|setupTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
comment|// write output
name|TextOutputFormat
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|partFile
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writeOutput
argument_list|(
name|theRecordWriter
argument_list|,
name|tContext
argument_list|)
expr_stmt|;
comment|// do commit
name|committer
operator|.
name|commitTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
comment|// validate output
name|validateContent
argument_list|(
name|outDir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapFileOutputCommitter ()
specifier|public
name|void
name|testMapFileOutputCommitter
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
comment|// setup
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|setupTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
comment|// write output
name|MapFileOutputFormat
name|theOutputFormat
init|=
operator|new
name|MapFileOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|partFile
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writeMapFileOutput
argument_list|(
name|theRecordWriter
argument_list|,
name|tContext
argument_list|)
expr_stmt|;
comment|// do commit
name|committer
operator|.
name|commitTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|commitJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
comment|// validate output
name|validateMapFileOutputContent
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAbort ()
specifier|public
name|void
name|testAbort
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
comment|// do setup
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|setupTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
comment|// write output
name|TextOutputFormat
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|partFile
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writeOutput
argument_list|(
name|theRecordWriter
argument_list|,
name|tContext
argument_list|)
expr_stmt|;
comment|// do abort
name|committer
operator|.
name|abortTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
name|File
name|out
init|=
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|workPath
init|=
name|committer
operator|.
name|getWorkPath
argument_list|(
name|tContext
argument_list|,
name|outDir
argument_list|)
decl_stmt|;
name|File
name|wp
init|=
operator|new
name|File
argument_list|(
name|workPath
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|expectedFile
init|=
operator|new
name|File
argument_list|(
name|wp
argument_list|,
name|partFile
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"task temp dir still exists"
argument_list|,
name|expectedFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|committer
operator|.
name|abortJob
argument_list|(
name|jContext
argument_list|,
name|JobStatus
operator|.
name|State
operator|.
name|FAILED
argument_list|)
expr_stmt|;
name|expectedFile
operator|=
operator|new
name|File
argument_list|(
name|out
argument_list|,
name|FileOutputCommitter
operator|.
name|TEMP_DIR_NAME
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"job temp dir still exists"
argument_list|,
name|expectedFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Output directory not empty"
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|listFiles
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeFileSystem
specifier|public
specifier|static
class|class
name|FakeFileSystem
extends|extends
name|RawLocalFileSystem
block|{
DECL|method|FakeFileSystem ()
specifier|public
name|FakeFileSystem
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
return|return
name|URI
operator|.
name|create
argument_list|(
literal|"faildel:///"
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path p, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|p
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"fake delete failed"
argument_list|)
throw|;
block|}
block|}
DECL|method|testFailAbort ()
specifier|public
name|void
name|testFailAbort
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystem
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"faildel:///"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
literal|"fs.faildel.impl"
argument_list|,
name|FakeFileSystem
operator|.
name|class
argument_list|,
name|FileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobContext
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|attempt
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRConstants
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|JobContext
name|jContext
init|=
operator|new
name|JobContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
operator|.
name|getJobID
argument_list|()
argument_list|)
decl_stmt|;
name|TaskAttemptContext
name|tContext
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|taskID
argument_list|)
decl_stmt|;
name|FileOutputCommitter
name|committer
init|=
operator|new
name|FileOutputCommitter
argument_list|()
decl_stmt|;
comment|// do setup
name|committer
operator|.
name|setupJob
argument_list|(
name|jContext
argument_list|)
expr_stmt|;
name|committer
operator|.
name|setupTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
comment|// write output
name|File
name|jobTmpDir
init|=
operator|new
name|File
argument_list|(
operator|new
name|Path
argument_list|(
name|outDir
argument_list|,
name|FileOutputCommitter
operator|.
name|TEMP_DIR_NAME
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|conf
operator|.
name|getInt
argument_list|(
name|MRConstants
operator|.
name|APPLICATION_ATTEMPT_ID
argument_list|,
literal|0
argument_list|)
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
name|FileOutputCommitter
operator|.
name|TEMP_DIR_NAME
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|taskTmpDir
init|=
operator|new
name|File
argument_list|(
name|jobTmpDir
argument_list|,
literal|"_"
operator|+
name|taskID
argument_list|)
decl_stmt|;
name|File
name|expectedFile
init|=
operator|new
name|File
argument_list|(
name|taskTmpDir
argument_list|,
name|partFile
argument_list|)
decl_stmt|;
name|TextOutputFormat
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|theOutputFormat
init|=
operator|new
name|TextOutputFormat
argument_list|()
decl_stmt|;
name|RecordWriter
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|theRecordWriter
init|=
name|theOutputFormat
operator|.
name|getRecordWriter
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|expectedFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writeOutput
argument_list|(
name|theRecordWriter
argument_list|,
name|tContext
argument_list|)
expr_stmt|;
comment|// do abort
name|Throwable
name|th
init|=
literal|null
decl_stmt|;
try|try
block|{
name|committer
operator|.
name|abortTask
argument_list|(
name|tContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|th
operator|=
name|ie
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"fake delete failed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|expectedFile
operator|+
literal|" does not exists"
argument_list|,
name|expectedFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|th
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|committer
operator|.
name|abortJob
argument_list|(
name|jContext
argument_list|,
name|JobStatus
operator|.
name|State
operator|.
name|FAILED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|th
operator|=
name|ie
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|th
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|instanceof
name|IOException
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|th
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"fake delete failed"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"job temp dir does not exists"
argument_list|,
name|jobTmpDir
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
operator|new
name|File
argument_list|(
name|outDir
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|slurp (File f)
specifier|public
specifier|static
name|String
name|slurp
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|f
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|String
name|contents
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|contents
operator|=
operator|new
name|String
argument_list|(
name|buf
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|contents
return|;
block|}
block|}
end_class

end_unit

