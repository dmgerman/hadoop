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
name|io
operator|.
name|IntWritable
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
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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

begin_comment
comment|/**  * A JUnit test to test Job System Directory with Mini-DFS.  */
end_comment

begin_class
DECL|class|TestJobSysDirWithDFS
specifier|public
class|class
name|TestJobSysDirWithDFS
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
name|TestJobSysDirWithDFS
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NUM_MAPS
specifier|static
specifier|final
name|int
name|NUM_MAPS
init|=
literal|10
decl_stmt|;
DECL|field|NUM_SAMPLES
specifier|static
specifier|final
name|int
name|NUM_SAMPLES
init|=
literal|100000
decl_stmt|;
DECL|class|TestResult
specifier|public
specifier|static
class|class
name|TestResult
block|{
DECL|field|output
specifier|public
name|String
name|output
decl_stmt|;
DECL|field|job
specifier|public
name|RunningJob
name|job
decl_stmt|;
DECL|method|TestResult (RunningJob job, String output)
name|TestResult
parameter_list|(
name|RunningJob
name|job
parameter_list|,
name|String
name|output
parameter_list|)
block|{
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
block|}
block|}
DECL|method|launchWordCount (JobConf conf, Path inDir, Path outDir, String input, int numMaps, int numReduces, String sysDir)
specifier|public
specifier|static
name|TestResult
name|launchWordCount
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|Path
name|inDir
parameter_list|,
name|Path
name|outDir
parameter_list|,
name|String
name|input
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|String
name|sysDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|inFs
init|=
name|inDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|outFs
init|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|outFs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|inFs
operator|.
name|mkdirs
argument_list|(
name|inDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|inDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|{
name|DataOutputStream
name|file
init|=
name|inFs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part-0"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setJobName
argument_list|(
literal|"wordcount"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInputFormat
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the keys are words (strings)
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the values are counts (ints)
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|WordCount
operator|.
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCombinerClass
argument_list|(
name|WordCount
operator|.
name|Reduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|WordCount
operator|.
name|Reduce
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
name|inDir
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
name|conf
operator|.
name|setNumMapTasks
argument_list|(
name|numMaps
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
name|numReduces
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_SYSTEM_DIR
argument_list|,
literal|"/tmp/subru/mapred/system"
argument_list|)
expr_stmt|;
name|JobClient
name|jobClient
init|=
operator|new
name|JobClient
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|RunningJob
name|job
init|=
name|jobClient
operator|.
name|runJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Checking that the Job Client system dir is not used
name|assertFalse
argument_list|(
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JTConfig
operator|.
name|JT_SYSTEM_DIR
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check if the Job Tracker system dir is propogated to client
name|assertFalse
argument_list|(
name|sysDir
operator|.
name|contains
argument_list|(
literal|"/tmp/subru/mapred/system"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sysDir
operator|.
name|contains
argument_list|(
literal|"custom"
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|TestResult
argument_list|(
name|job
argument_list|,
name|MapReduceTestUtil
operator|.
name|readOutput
argument_list|(
name|outDir
argument_list|,
name|conf
argument_list|)
argument_list|)
return|;
block|}
DECL|method|runWordCount (MiniMRCluster mr, JobConf jobConf, String sysDir)
specifier|static
name|void
name|runWordCount
parameter_list|(
name|MiniMRCluster
name|mr
parameter_list|,
name|JobConf
name|jobConf
parameter_list|,
name|String
name|sysDir
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"runWordCount"
argument_list|)
expr_stmt|;
comment|// Run a word count example
comment|// Keeping tasks that match this pattern
name|TestResult
name|result
decl_stmt|;
specifier|final
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"./wc/input"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"./wc/output"
argument_list|)
decl_stmt|;
name|result
operator|=
name|launchWordCount
argument_list|(
name|jobConf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|"The quick brown fox\nhas many silly\n"
operator|+
literal|"red fox sox\n"
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
name|sysDir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The\t1\nbrown\t1\nfox\t2\nhas\t1\nmany\t1\n"
operator|+
literal|"quick\t1\nred\t1\nsilly\t1\nsox\t1\n"
argument_list|,
name|result
operator|.
name|output
argument_list|)
expr_stmt|;
comment|// Checking if the Job ran successfully in spite of different system dir config
comment|//  between Job Client& Job Tracker
name|assertTrue
argument_list|(
name|result
operator|.
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithDFS ()
specifier|public
name|void
name|testWithDFS
parameter_list|()
throws|throws
name|IOException
block|{
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
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|taskTrackers
init|=
literal|4
decl_stmt|;
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
name|JTConfig
operator|.
name|JT_SYSTEM_DIR
argument_list|,
literal|"/tmp/custom/mapred/system"
argument_list|)
expr_stmt|;
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
literal|4
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|taskTrackers
argument_list|,
name|fileSys
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|runWordCount
argument_list|(
name|mr
argument_list|,
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"mapred.system.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

