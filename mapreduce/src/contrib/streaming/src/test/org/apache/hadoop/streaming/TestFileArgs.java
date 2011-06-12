begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
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
name|net
operator|.
name|URI
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

begin_comment
comment|/**  * This class tests that the '-file' argument to streaming results  * in files being unpacked in the job working directory.  */
end_comment

begin_class
DECL|class|TestFileArgs
specifier|public
class|class
name|TestFileArgs
extends|extends
name|TestStreaming
block|{
DECL|field|dfs
specifier|private
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|mr
specifier|private
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
DECL|field|fileSys
specifier|private
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
DECL|field|strJobTracker
specifier|private
name|String
name|strJobTracker
init|=
literal|null
decl_stmt|;
DECL|field|strNamenode
specifier|private
name|String
name|strNamenode
init|=
literal|null
decl_stmt|;
DECL|field|namenode
specifier|private
name|String
name|namenode
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|EXPECTED_OUTPUT
specifier|private
specifier|static
specifier|final
name|String
name|EXPECTED_OUTPUT
init|=
literal|"job.jar\t\nsidefile\t\ntmp\t\n"
decl_stmt|;
DECL|field|LS_PATH
specifier|private
specifier|static
specifier|final
name|String
name|LS_PATH
init|=
literal|"/bin/ls"
decl_stmt|;
DECL|method|TestFileArgs ()
specifier|public
name|TestFileArgs
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set up mini cluster
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|dfs
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
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
name|getAuthority
argument_list|()
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|1
argument_list|,
name|namenode
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|strJobTracker
operator|=
name|JTConfig
operator|.
name|JT_IPC_ADDRESS
operator|+
literal|"=localhost:"
operator|+
name|mr
operator|.
name|getJobTrackerPort
argument_list|()
expr_stmt|;
name|strNamenode
operator|=
literal|"fs.default.name=hdfs://"
operator|+
name|namenode
expr_stmt|;
name|map
operator|=
name|LS_PATH
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
literal|"hdfs://"
operator|+
name|namenode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Set up side file
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|DataOutputStream
name|dos
init|=
name|localFs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
literal|"sidefile"
argument_list|)
argument_list|)
decl_stmt|;
name|dos
operator|.
name|write
argument_list|(
literal|"hello world\n"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|dos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Since ls doesn't read stdin, we don't want to write anything
comment|// to it, or else we risk Broken Pipe exceptions.
name|input
operator|=
literal|""
expr_stmt|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
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
block|}
annotation|@
name|Override
DECL|method|getExpectedOutput ()
specifier|protected
name|String
name|getExpectedOutput
parameter_list|()
block|{
return|return
name|EXPECTED_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|protected
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|genArgs ()
specifier|protected
name|String
index|[]
name|genArgs
parameter_list|()
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-file"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
operator|new
name|java
operator|.
name|io
operator|.
name|File
argument_list|(
literal|"sidefile"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-numReduceTasks"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|strNamenode
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|strJobTracker
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|genArgs
argument_list|()
return|;
block|}
block|}
end_class

end_unit

