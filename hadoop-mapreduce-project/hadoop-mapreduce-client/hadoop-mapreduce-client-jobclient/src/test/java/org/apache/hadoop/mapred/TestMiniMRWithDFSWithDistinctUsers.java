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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
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
name|Assert
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
comment|/**  * A JUnit test to test Mini Map-Reduce Cluster with Mini-DFS.  */
end_comment

begin_class
DECL|class|TestMiniMRWithDFSWithDistinctUsers
specifier|public
class|class
name|TestMiniMRWithDFSWithDistinctUsers
block|{
DECL|field|DFS_UGI
specifier|static
specifier|final
name|UserGroupInformation
name|DFS_UGI
init|=
name|createUGI
argument_list|(
literal|"dfs"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|field|ALICE_UGI
specifier|static
specifier|final
name|UserGroupInformation
name|ALICE_UGI
init|=
name|createUGI
argument_list|(
literal|"alice"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|BOB_UGI
specifier|static
specifier|final
name|UserGroupInformation
name|BOB_UGI
init|=
name|createUGI
argument_list|(
literal|"bob"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|mr
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
DECL|field|dfs
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|createUGI (String name, boolean issuper)
specifier|static
name|UserGroupInformation
name|createUGI
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|issuper
parameter_list|)
block|{
name|String
name|group
init|=
name|issuper
condition|?
literal|"supergroup"
else|:
name|name
decl_stmt|;
return|return
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|name
argument_list|,
operator|new
name|String
index|[]
block|{
name|group
block|}
argument_list|)
return|;
block|}
DECL|method|mkdir (FileSystem fs, String dir, String user, String group, short mode)
specifier|static
name|void
name|mkdir
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dir
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|,
name|short
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|p
init|=
operator|new
name|Path
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|p
argument_list|,
operator|new
name|FsPermission
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setOwner
argument_list|(
name|p
argument_list|,
name|user
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
comment|// runs a sample job as a user (ugi)
DECL|method|runJobAsUser (final JobConf job, UserGroupInformation ugi)
name|void
name|runJobAsUser
parameter_list|(
specifier|final
name|JobConf
name|job
parameter_list|,
name|UserGroupInformation
name|ugi
parameter_list|)
throws|throws
name|Exception
block|{
name|RunningJob
name|rj
init|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|RunningJob
argument_list|>
argument_list|()
block|{
specifier|public
name|RunningJob
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|JobClient
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|rj
operator|.
name|waitForCompletion
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"SUCCEEDED"
argument_list|,
name|JobStatus
operator|.
name|getJobRunState
argument_list|(
name|rj
operator|.
name|getJobState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|dfs
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
literal|4
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|DFS_UGI
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Home directories for users
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/user"
argument_list|,
literal|"nobody"
argument_list|,
literal|"nogroup"
argument_list|,
operator|(
name|short
operator|)
literal|01777
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/user/alice"
argument_list|,
literal|"alice"
argument_list|,
literal|"nogroup"
argument_list|,
operator|(
name|short
operator|)
literal|0755
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/user/bob"
argument_list|,
literal|"bob"
argument_list|,
literal|"nogroup"
argument_list|,
operator|(
name|short
operator|)
literal|0755
argument_list|)
expr_stmt|;
comment|// staging directory root with sticky bit
name|UserGroupInformation
name|MR_UGI
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
decl_stmt|;
name|mkdir
argument_list|(
name|fs
argument_list|,
literal|"/staging"
argument_list|,
name|MR_UGI
operator|.
name|getShortUserName
argument_list|()
argument_list|,
literal|"nogroup"
argument_list|,
operator|(
name|short
operator|)
literal|01777
argument_list|)
expr_stmt|;
name|JobConf
name|mrConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|mrConf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_STAGING_AREA_ROOT
argument_list|,
literal|"/staging"
argument_list|)
expr_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|4
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
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|MR_UGI
argument_list|,
name|mrConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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
name|Test
DECL|method|testDistinctUsers ()
specifier|public
name|void
name|testDistinctUsers
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job1
init|=
name|mr
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
name|String
name|input
init|=
literal|"The quick brown fox\nhas many silly\n"
operator|+
literal|"red fox sox\n"
decl_stmt|;
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testing/distinct/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"/user/alice/output"
argument_list|)
decl_stmt|;
name|TestMiniMRClasspath
operator|.
name|configureWordCount
argument_list|(
name|fs
argument_list|,
name|job1
argument_list|,
name|input
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|runJobAsUser
argument_list|(
name|job1
argument_list|,
name|ALICE_UGI
argument_list|)
expr_stmt|;
name|JobConf
name|job2
init|=
name|mr
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
name|Path
name|inDir2
init|=
operator|new
name|Path
argument_list|(
literal|"/testing/distinct/input2"
argument_list|)
decl_stmt|;
name|Path
name|outDir2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/bob/output2"
argument_list|)
decl_stmt|;
name|TestMiniMRClasspath
operator|.
name|configureWordCount
argument_list|(
name|fs
argument_list|,
name|job2
argument_list|,
name|input
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|inDir2
argument_list|,
name|outDir2
argument_list|)
expr_stmt|;
name|runJobAsUser
argument_list|(
name|job2
argument_list|,
name|BOB_UGI
argument_list|)
expr_stmt|;
block|}
comment|/**    * Regression test for MAPREDUCE-2327. Verifies that, even if a map    * task makes lots of spills (more than fit in the spill index cache)    * that it will succeed.    */
annotation|@
name|Test
DECL|method|testMultipleSpills ()
specifier|public
name|void
name|testMultipleSpills
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job1
init|=
name|mr
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
comment|// Make sure it spills twice
name|job1
operator|.
name|setFloat
argument_list|(
name|MRJobConfig
operator|.
name|MAP_SORT_SPILL_PERCENT
argument_list|,
literal|0.0001f
argument_list|)
expr_stmt|;
name|job1
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|IO_SORT_MB
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Make sure the spill records don't fit in index cache
name|job1
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|INDEX_CACHE_MEMORY_LIMIT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|String
name|input
init|=
literal|"The quick brown fox\nhas many silly\n"
operator|+
literal|"red fox sox\n"
decl_stmt|;
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
literal|"/testing/distinct/input"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
literal|"/user/alice/output"
argument_list|)
decl_stmt|;
name|TestMiniMRClasspath
operator|.
name|configureWordCount
argument_list|(
name|fs
argument_list|,
name|job1
argument_list|,
name|input
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|runJobAsUser
argument_list|(
name|job1
argument_list|,
name|ALICE_UGI
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

