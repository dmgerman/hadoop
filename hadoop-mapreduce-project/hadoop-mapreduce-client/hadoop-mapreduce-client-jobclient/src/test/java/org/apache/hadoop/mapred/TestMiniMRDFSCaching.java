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
name|mapred
operator|.
name|MRCaching
operator|.
name|TestResult
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
name|assertTrue
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

begin_comment
comment|/**  * A JUnit test to test caching with DFS  *   */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|TestMiniMRDFSCaching
specifier|public
class|class
name|TestMiniMRDFSCaching
block|{
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
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
name|MiniDFSCluster
name|dfs
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
name|JobConf
name|conf
init|=
operator|new
name|JobConf
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
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|2
argument_list|,
name|fileSys
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|MRCaching
operator|.
name|setupCache
argument_list|(
literal|"/cachedir"
argument_list|,
name|fileSys
argument_list|)
expr_stmt|;
comment|// run the wordcount example with caching
name|TestResult
name|ret
init|=
name|MRCaching
operator|.
name|launchMRCache
argument_list|(
literal|"/testing/wc/input"
argument_list|,
literal|"/testing/wc/output"
argument_list|,
literal|"/cachedir"
argument_list|,
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
literal|"The quick brown fox\nhas many silly\n"
operator|+
literal|"red fox sox\n"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Archives not matching"
argument_list|,
name|ret
operator|.
name|isOutputOk
argument_list|)
expr_stmt|;
comment|// launch MR cache with symlinks
name|ret
operator|=
name|MRCaching
operator|.
name|launchMRCache
argument_list|(
literal|"/testing/wc/input"
argument_list|,
literal|"/testing/wc/output"
argument_list|,
literal|"/cachedir"
argument_list|,
name|mr
operator|.
name|createJobConf
argument_list|()
argument_list|,
literal|"The quick brown fox\nhas many silly\n"
operator|+
literal|"red fox sox\n"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Archives not matching"
argument_list|,
name|ret
operator|.
name|isOutputOk
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
block|{
name|fileSys
operator|.
name|close
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

