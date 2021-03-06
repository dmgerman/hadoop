begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
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
name|IOException
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|SleepJob
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
name|mapreduce
operator|.
name|Job
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
name|JobStatus
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
name|v2
operator|.
name|MiniMRYarnCluster
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

begin_class
DECL|class|TestMRAMWithNonNormalizedCapabilities
specifier|public
class|class
name|TestMRAMWithNonNormalizedCapabilities
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
name|TestMRAMWithNonNormalizedCapabilities
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
DECL|field|mrCluster
specifier|protected
specifier|static
name|MiniMRYarnCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestMRAMWithNonNormalizedCapabilities
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|localFs
operator|.
name|getUri
argument_list|()
argument_list|,
name|localFs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|APP_JAR
specifier|static
name|Path
name|APP_JAR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"MRAppJar.jar"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|==
literal|null
condition|)
block|{
name|mrCluster
operator|=
operator|new
name|MiniMRYarnCluster
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|mrCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Copy MRAppJar and make it private. TODO: FIXME. This is a hack to
comment|// workaround the absent public discache.
name|localFs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
argument_list|,
name|APP_JAR
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|APP_JAR
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * To ensure nothing broken after we removed normalization     * from the MRAM side    * @throws Exception    */
annotation|@
name|Test
DECL|method|testJobWithNonNormalizedCapabilities ()
specifier|public
name|void
name|testJobWithNonNormalizedCapabilities
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|mrCluster
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"mapreduce.map.memory.mb"
argument_list|,
literal|700
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setInt
argument_list|(
literal|"mapred.reduce.memory.mb"
argument_list|,
literal|1500
argument_list|)
expr_stmt|;
name|SleepJob
name|sleepJob
init|=
operator|new
name|SleepJob
argument_list|()
decl_stmt|;
name|sleepJob
operator|.
name|setConf
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|sleepJob
operator|.
name|createJob
argument_list|(
literal|3
argument_list|,
literal|2
argument_list|,
literal|1000
argument_list|,
literal|1
argument_list|,
literal|500
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|SleepJob
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|APP_JAR
argument_list|)
expr_stmt|;
comment|// The AppMaster jar itself.
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
name|boolean
name|completed
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Job should be completed"
argument_list|,
name|completed
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job should be finished successfully"
argument_list|,
name|JobStatus
operator|.
name|State
operator|.
name|SUCCEEDED
argument_list|,
name|job
operator|.
name|getJobState
argument_list|()
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
block|{
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

