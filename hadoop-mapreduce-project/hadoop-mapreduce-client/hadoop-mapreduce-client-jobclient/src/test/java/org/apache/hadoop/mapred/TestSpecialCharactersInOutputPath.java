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
name|mapred
operator|.
name|lib
operator|.
name|IdentityMapper
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
name|lib
operator|.
name|IdentityReducer
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
name|Progressable
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
name|net
operator|.
name|URI
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
name|fail
import|;
end_import

begin_comment
comment|/**  * A JUnit test to test that jobs' output filenames are not HTML-encoded (cf HADOOP-1795).  */
end_comment

begin_class
DECL|class|TestSpecialCharactersInOutputPath
specifier|public
class|class
name|TestSpecialCharactersInOutputPath
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
name|TestSpecialCharactersInOutputPath
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|OUTPUT_FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_FILENAME
init|=
literal|"result[0]"
decl_stmt|;
DECL|method|launchJob (URI fileSys, JobConf conf, int numMaps, int numReduces)
specifier|public
specifier|static
name|boolean
name|launchJob
parameter_list|(
name|URI
name|fileSys
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReduces
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testing/input"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testing/output"
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|fileSys
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|fs
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
name|fs
operator|.
name|mkdirs
argument_list|(
name|inDir
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't create "
operator|+
name|inDir
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// generate an input file
name|DataOutputStream
name|file
init|=
name|fs
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
literal|"foo foo2 foo3"
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// use WordCount example
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|fileSys
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setJobName
argument_list|(
literal|"foo"
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
name|conf
operator|.
name|setOutputFormat
argument_list|(
name|SpecialTextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|IdentityMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|IdentityReducer
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
comment|// run job and wait for completion
name|RunningJob
name|runningJob
init|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|isComplete
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|runningJob
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Output folder not found!"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/testing/output/"
operator|+
name|OUTPUT_FILENAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// This NPE should no more happens
name|fail
argument_list|(
literal|"A NPE should not have happened."
argument_list|)
expr_stmt|;
block|}
comment|// return job result
name|LOG
operator|.
name|info
argument_list|(
literal|"job is complete: "
operator|+
name|runningJob
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|runningJob
operator|.
name|isSuccessful
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Test
DECL|method|testJobWithDFS ()
specifier|public
name|void
name|testJobWithDFS
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|namenode
init|=
literal|null
decl_stmt|;
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
specifier|final
name|int
name|jobTrackerPort
init|=
literal|60050
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
name|namenode
operator|=
name|fileSys
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|taskTrackers
argument_list|,
name|namenode
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|boolean
name|result
decl_stmt|;
name|result
operator|=
name|launchJob
argument_list|(
name|fileSys
operator|.
name|getUri
argument_list|()
argument_list|,
name|jobConf
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
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
comment|/** generates output filenames with special characters */
DECL|class|SpecialTextOutputFormat
specifier|static
class|class
name|SpecialTextOutputFormat
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|TextOutputFormat
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getRecordWriter (FileSystem ignored, JobConf job, String name, Progressable progress)
specifier|public
name|RecordWriter
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|getRecordWriter
parameter_list|(
name|FileSystem
name|ignored
parameter_list|,
name|JobConf
name|job
parameter_list|,
name|String
name|name
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getRecordWriter
argument_list|(
name|ignored
argument_list|,
name|job
argument_list|,
name|OUTPUT_FILENAME
argument_list|,
name|progress
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

