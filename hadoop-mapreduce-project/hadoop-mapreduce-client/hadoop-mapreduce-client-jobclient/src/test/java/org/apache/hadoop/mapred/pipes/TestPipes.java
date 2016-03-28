begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|List
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|permission
operator|.
name|FsAction
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
name|permission
operator|.
name|FsPermission
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
name|mapred
operator|.
name|Counters
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
name|mapred
operator|.
name|FileInputFormat
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
name|mapred
operator|.
name|FileOutputFormat
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|MiniMRCluster
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
name|mapred
operator|.
name|RunningJob
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
name|mapred
operator|.
name|Utils
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
name|mapred
operator|.
name|Counters
operator|.
name|Counter
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
name|MapReduceTestUtil
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_class
annotation|@
name|Ignore
DECL|class|TestPipes
specifier|public
class|class
name|TestPipes
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestPipes
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|cppExamples
specifier|private
specifier|static
name|Path
name|cppExamples
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"install.c++.examples"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|wordCountSimple
specifier|static
name|Path
name|wordCountSimple
init|=
operator|new
name|Path
argument_list|(
name|cppExamples
argument_list|,
literal|"bin/wordcount-simple"
argument_list|)
decl_stmt|;
DECL|field|wordCountPart
specifier|static
name|Path
name|wordCountPart
init|=
operator|new
name|Path
argument_list|(
name|cppExamples
argument_list|,
literal|"bin/wordcount-part"
argument_list|)
decl_stmt|;
DECL|field|wordCountNoPipes
specifier|static
name|Path
name|wordCountNoPipes
init|=
operator|new
name|Path
argument_list|(
name|cppExamples
argument_list|,
literal|"bin/wordcount-nopipe"
argument_list|)
decl_stmt|;
DECL|field|nonPipedOutDir
specifier|static
name|Path
name|nonPipedOutDir
decl_stmt|;
DECL|method|cleanup (FileSystem fs, Path p)
specifier|static
name|void
name|cleanup
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"output not cleaned up"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testPipes ()
specifier|public
name|void
name|testPipes
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"compile.c++"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"compile.c++ is not defined, so skipping TestPipes"
argument_list|)
expr_stmt|;
return|return;
block|}
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
name|Path
name|inputPath
init|=
operator|new
name|Path
argument_list|(
literal|"testing/in"
argument_list|)
decl_stmt|;
name|Path
name|outputPath
init|=
operator|new
name|Path
argument_list|(
literal|"testing/out"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|numSlaves
init|=
literal|2
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|dfs
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numSlaves
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|numSlaves
argument_list|,
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writeInputFile
argument_list|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|inputPath
argument_list|)
expr_stmt|;
name|runProgram
argument_list|(
name|mr
argument_list|,
name|dfs
argument_list|,
name|wordCountSimple
argument_list|,
name|inputPath
argument_list|,
name|outputPath
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|,
name|twoSplitOutput
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|runProgram
argument_list|(
name|mr
argument_list|,
name|dfs
argument_list|,
name|wordCountSimple
argument_list|,
name|inputPath
argument_list|,
name|outputPath
argument_list|,
literal|3
argument_list|,
literal|0
argument_list|,
name|noSortOutput
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cleanup
argument_list|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|runProgram
argument_list|(
name|mr
argument_list|,
name|dfs
argument_list|,
name|wordCountPart
argument_list|,
name|inputPath
argument_list|,
name|outputPath
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|,
name|fixedPartitionOutput
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|runNonPipedProgram
argument_list|(
name|mr
argument_list|,
name|dfs
argument_list|,
name|wordCountNoPipes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mr
operator|.
name|waitUntilIdle
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|mr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|twoSplitOutput
specifier|final
specifier|static
name|String
index|[]
name|twoSplitOutput
init|=
operator|new
name|String
index|[]
block|{
literal|"`and\t1\na\t1\nand\t1\nbeginning\t1\nbook\t1\nbut\t1\nby\t1\n"
operator|+
literal|"conversation?'\t1\ndo:\t1\nhad\t2\nhaving\t1\nher\t2\nin\t1\nit\t1\n"
operator|+
literal|"it,\t1\nno\t1\nnothing\t1\nof\t3\non\t1\nonce\t1\nor\t3\npeeped\t1\n"
operator|+
literal|"pictures\t2\nthe\t3\nthought\t1\nto\t2\nuse\t1\nwas\t2\n"
block|,
literal|"Alice\t2\n`without\t1\nbank,\t1\nbook,'\t1\nconversations\t1\nget\t1\n"
operator|+
literal|"into\t1\nis\t1\nreading,\t1\nshe\t1\nsister\t2\nsitting\t1\ntired\t1\n"
operator|+
literal|"twice\t1\nvery\t1\nwhat\t1\n"
block|}
decl_stmt|;
DECL|field|noSortOutput
specifier|final
specifier|static
name|String
index|[]
name|noSortOutput
init|=
operator|new
name|String
index|[]
block|{
literal|"it,\t1\n`and\t1\nwhat\t1\nis\t1\nthe\t1\nuse\t1\nof\t1\na\t1\n"
operator|+
literal|"book,'\t1\nthought\t1\nAlice\t1\n`without\t1\npictures\t1\nor\t1\n"
operator|+
literal|"conversation?'\t1\n"
block|,
literal|"Alice\t1\nwas\t1\nbeginning\t1\nto\t1\nget\t1\nvery\t1\ntired\t1\n"
operator|+
literal|"of\t1\nsitting\t1\nby\t1\nher\t1\nsister\t1\non\t1\nthe\t1\nbank,\t1\n"
operator|+
literal|"and\t1\nof\t1\nhaving\t1\nnothing\t1\nto\t1\ndo:\t1\nonce\t1\n"
block|,
literal|"or\t1\ntwice\t1\nshe\t1\nhad\t1\npeeped\t1\ninto\t1\nthe\t1\nbook\t1\n"
operator|+
literal|"her\t1\nsister\t1\nwas\t1\nreading,\t1\nbut\t1\nit\t1\nhad\t1\nno\t1\n"
operator|+
literal|"pictures\t1\nor\t1\nconversations\t1\nin\t1\n"
block|}
decl_stmt|;
DECL|field|fixedPartitionOutput
specifier|final
specifier|static
name|String
index|[]
name|fixedPartitionOutput
init|=
operator|new
name|String
index|[]
block|{
literal|"Alice\t2\n`and\t1\n`without\t1\na\t1\nand\t1\nbank,\t1\nbeginning\t1\n"
operator|+
literal|"book\t1\nbook,'\t1\nbut\t1\nby\t1\nconversation?'\t1\nconversations\t1\n"
operator|+
literal|"do:\t1\nget\t1\nhad\t2\nhaving\t1\nher\t2\nin\t1\ninto\t1\nis\t1\n"
operator|+
literal|"it\t1\nit,\t1\nno\t1\nnothing\t1\nof\t3\non\t1\nonce\t1\nor\t3\n"
operator|+
literal|"peeped\t1\npictures\t2\nreading,\t1\nshe\t1\nsister\t2\nsitting\t1\n"
operator|+
literal|"the\t3\nthought\t1\ntired\t1\nto\t2\ntwice\t1\nuse\t1\n"
operator|+
literal|"very\t1\nwas\t2\nwhat\t1\n"
block|,
literal|""
block|}
decl_stmt|;
DECL|method|writeInputFile (FileSystem fs, Path dir)
specifier|static
name|void
name|writeInputFile
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
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|dir
argument_list|,
literal|"part0"
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"Alice was beginning to get very tired of sitting by her\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"sister on the bank, and of having nothing to do: once\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"or twice she had peeped into the book her sister was\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"reading, but it had no pictures or conversations in\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"it, `and what is the use of a book,' thought Alice\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"`without pictures or conversation?'\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|runProgram (MiniMRCluster mr, MiniDFSCluster dfs, Path program, Path inputPath, Path outputPath, int numMaps, int numReduces, String[] expectedResults, JobConf conf )
specifier|static
name|void
name|runProgram
parameter_list|(
name|MiniMRCluster
name|mr
parameter_list|,
name|MiniDFSCluster
name|dfs
parameter_list|,
name|Path
name|program
parameter_list|,
name|Path
name|inputPath
parameter_list|,
name|Path
name|outputPath
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|String
index|[]
name|expectedResults
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|wordExec
init|=
operator|new
name|Path
argument_list|(
literal|"testing/bin/application"
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|job
operator|=
name|mr
operator|.
name|createJobConf
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|job
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|setNumMapTasks
argument_list|(
name|numMaps
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|numReduces
argument_list|)
expr_stmt|;
block|{
name|FileSystem
name|fs
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|wordExec
operator|.
name|getParent
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|program
argument_list|,
name|wordExec
argument_list|)
expr_stmt|;
name|Submitter
operator|.
name|setExecutable
argument_list|(
name|job
argument_list|,
name|fs
operator|.
name|makeQualified
argument_list|(
name|wordExec
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Submitter
operator|.
name|setIsJavaRecordReader
argument_list|(
name|job
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Submitter
operator|.
name|setIsJavaRecordWriter
argument_list|(
name|job
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inputPath
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|RunningJob
name|rJob
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|numReduces
operator|==
literal|0
condition|)
block|{
name|rJob
operator|=
name|Submitter
operator|.
name|jobSubmit
argument_list|(
name|job
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|rJob
operator|.
name|isComplete
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ie
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
name|rJob
operator|=
name|Submitter
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"pipes job failed"
argument_list|,
name|rJob
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|Counters
name|counters
init|=
name|rJob
operator|.
name|getCounters
argument_list|()
decl_stmt|;
name|Counters
operator|.
name|Group
name|wordCountCounters
init|=
name|counters
operator|.
name|getGroup
argument_list|(
literal|"WORDCOUNT"
argument_list|)
decl_stmt|;
name|int
name|numCounters
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Counter
name|c
range|:
name|wordCountCounters
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|c
argument_list|)
expr_stmt|;
operator|++
name|numCounters
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"No counters found!"
argument_list|,
operator|(
name|numCounters
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
name|outputPath
argument_list|,
operator|new
name|Utils
operator|.
name|OutputFileUtils
operator|.
name|OutputFilesFilter
argument_list|()
argument_list|)
argument_list|)
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|p
argument_list|,
name|job
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"number of reduces is wrong"
argument_list|,
name|expectedResults
operator|.
name|length
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"pipes program "
operator|+
name|program
operator|+
literal|" output "
operator|+
name|i
operator|+
literal|" wrong"
argument_list|,
name|expectedResults
index|[
name|i
index|]
argument_list|,
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Run a map/reduce word count that does all of the map input and reduce    * output directly rather than sending it back up to Java.    * @param mr The mini mr cluster    * @param dfs the dfs cluster    * @param program the program to run    * @throws IOException    */
DECL|method|runNonPipedProgram (MiniMRCluster mr, MiniDFSCluster dfs, Path program, JobConf conf)
specifier|static
name|void
name|runNonPipedProgram
parameter_list|(
name|MiniMRCluster
name|mr
parameter_list|,
name|MiniDFSCluster
name|dfs
parameter_list|,
name|Path
name|program
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|JobConf
name|job
decl_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|job
operator|=
name|mr
operator|.
name|createJobConf
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|job
operator|=
operator|new
name|JobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|setInputFormat
argument_list|(
name|WordCountInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileSystem
name|local
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|Path
name|testDir
init|=
operator|new
name|Path
argument_list|(
literal|"file:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|,
literal|"pipes"
argument_list|)
decl_stmt|;
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"input"
argument_list|)
decl_stmt|;
name|nonPipedOutDir
operator|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"output"
argument_list|)
expr_stmt|;
name|Path
name|wordExec
init|=
operator|new
name|Path
argument_list|(
literal|"testing/bin/application"
argument_list|)
decl_stmt|;
name|Path
name|jobXml
init|=
operator|new
name|Path
argument_list|(
name|testDir
argument_list|,
literal|"job.xml"
argument_list|)
decl_stmt|;
block|{
name|FileSystem
name|fs
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|wordExec
operator|.
name|getParent
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|program
argument_list|,
name|wordExec
argument_list|)
expr_stmt|;
block|}
name|DataOutputStream
name|out
init|=
name|local
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part0"
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"i am a silly test\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"you are silly\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"i am a cat test\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"you is silly\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"i am a billy test\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hello are silly\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|local
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part1"
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"mall world things drink java\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hall silly cats drink java\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"all dogs bow wow\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"hello drink java\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|local
operator|.
name|delete
argument_list|(
name|nonPipedOutDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|local
operator|.
name|mkdirs
argument_list|(
name|nonPipedOutDir
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|=
name|local
operator|.
name|create
argument_list|(
name|jobXml
argument_list|)
expr_stmt|;
name|job
operator|.
name|writeXml
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"About to run: Submitter -conf "
operator|+
name|jobXml
operator|+
literal|" -input "
operator|+
name|inDir
operator|+
literal|" -output "
operator|+
name|nonPipedOutDir
operator|+
literal|" -program "
operator|+
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
name|wordExec
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|ret
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|Submitter
argument_list|()
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"-conf"
block|,
name|jobXml
operator|.
name|toString
argument_list|()
block|,
literal|"-input"
block|,
name|inDir
operator|.
name|toString
argument_list|()
block|,
literal|"-output"
block|,
name|nonPipedOutDir
operator|.
name|toString
argument_list|()
block|,
literal|"-program"
block|,
name|dfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
name|wordExec
argument_list|)
operator|.
name|toString
argument_list|()
block|,
literal|"-reduces"
block|,
literal|"2"
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"got exception: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

