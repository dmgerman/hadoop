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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Test case to run a MapReduce job.  *<p/>  * It runs a 2 node cluster Hadoop with a 2 node DFS.  *<p/>  * The JobConf to use must be obtained via the creatJobConf() method.  *<p/>  * It creates a temporary directory -accessible via getTestRootDir()-  * for both input and output.  *<p/>  * The input directory is accesible via getInputDir() and the output  * directory via getOutputDir()  *<p/>  * The DFS filesystem is formated before the testcase starts and after it ends.  */
end_comment

begin_class
DECL|class|ClusterMapReduceTestCase
specifier|public
specifier|abstract
class|class
name|ClusterMapReduceTestCase
extends|extends
name|TestCase
block|{
DECL|field|dfsCluster
specifier|private
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|mrCluster
specifier|private
name|MiniMRCluster
name|mrCluster
init|=
literal|null
decl_stmt|;
comment|/**    * Creates Hadoop Cluster and DFS before a test case is run.    *    * @throws Exception    */
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|startCluster
argument_list|(
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Starts the cluster within a testcase.    *<p/>    * Note that the cluster is already started when the testcase method    * is invoked. This method is useful if as part of the testcase the    * cluster has to be shutdown and restarted again.    *<p/>    * If the cluster is already running this method does nothing.    *    * @param reformatDFS indicates if DFS has to be reformated    * @param props configuration properties to inject to the mini cluster    * @throws Exception if the cluster could not be started    */
DECL|method|startCluster (boolean reformatDFS, Properties props)
specifier|protected
specifier|synchronized
name|void
name|startCluster
parameter_list|(
name|boolean
name|reformatDFS
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|dfsCluster
operator|==
literal|null
condition|)
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|props
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|props
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
name|reformatDFS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ConfigurableMiniMRCluster
operator|.
name|setConfiguration
argument_list|(
name|props
argument_list|)
expr_stmt|;
comment|//noinspection deprecation
name|mrCluster
operator|=
operator|new
name|ConfigurableMiniMRCluster
argument_list|(
literal|2
argument_list|,
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
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ConfigurableMiniMRCluster
specifier|private
specifier|static
class|class
name|ConfigurableMiniMRCluster
extends|extends
name|MiniMRCluster
block|{
DECL|field|config
specifier|private
specifier|static
name|Properties
name|config
decl_stmt|;
DECL|method|setConfiguration (Properties props)
specifier|public
specifier|static
name|void
name|setConfiguration
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
name|config
operator|=
name|props
expr_stmt|;
block|}
DECL|method|ConfigurableMiniMRCluster (int numTaskTrackers, String namenode, int numDir, JobConf conf)
specifier|public
name|ConfigurableMiniMRCluster
parameter_list|(
name|int
name|numTaskTrackers
parameter_list|,
name|String
name|namenode
parameter_list|,
name|int
name|numDir
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|numTaskTrackers
argument_list|,
name|namenode
argument_list|,
name|numDir
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|createJobConf ()
specifier|public
name|JobConf
name|createJobConf
parameter_list|()
block|{
name|JobConf
name|conf
init|=
name|super
operator|.
name|createJobConf
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|config
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|conf
operator|.
name|set
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|conf
return|;
block|}
block|}
comment|/**    * Stops the cluster within a testcase.    *<p/>    * Note that the cluster is already started when the testcase method    * is invoked. This method is useful if as part of the testcase the    * cluster has to be shutdown.    *<p/>    * If the cluster is already stopped this method does nothing.    *    * @throws Exception if the cluster could not be stopped    */
DECL|method|stopCluster ()
specifier|protected
name|void
name|stopCluster
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Destroys Hadoop Cluster and DFS after a test case is run.    *    * @throws Exception    */
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|stopCluster
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a preconfigured Filesystem instance for test cases to read and    * write files to it.    *<p/>    * TestCases should use this Filesystem instance.    *    * @return the filesystem used by Hadoop.    * @throws IOException     */
DECL|method|getFileSystem ()
specifier|protected
name|FileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
DECL|method|getMRCluster ()
specifier|protected
name|MiniMRCluster
name|getMRCluster
parameter_list|()
block|{
return|return
name|mrCluster
return|;
block|}
comment|/**    * Returns the path to the root directory for the testcase.    *    * @return path to the root directory for the testcase.    */
DECL|method|getTestRootDir ()
specifier|protected
name|Path
name|getTestRootDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getParent
argument_list|()
return|;
block|}
comment|/**    * Returns a path to the input directory for the testcase.    *    * @return path to the input directory for the tescase.    */
DECL|method|getInputDir ()
specifier|protected
name|Path
name|getInputDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"input"
argument_list|)
return|;
block|}
comment|/**    * Returns a path to the output directory for the testcase.    *    * @return path to the output directory for the tescase.    */
DECL|method|getOutputDir ()
specifier|protected
name|Path
name|getOutputDir
parameter_list|()
block|{
return|return
operator|new
name|Path
argument_list|(
literal|"output"
argument_list|)
return|;
block|}
comment|/**    * Returns a job configuration preconfigured to run against the Hadoop    * managed by the testcase.    *    * @return configuration that works on the testcase Hadoop instance    */
DECL|method|createJobConf ()
specifier|protected
name|JobConf
name|createJobConf
parameter_list|()
block|{
return|return
name|mrCluster
operator|.
name|createJobConf
argument_list|()
return|;
block|}
block|}
end_class

end_unit

