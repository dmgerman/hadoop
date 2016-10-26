begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|mapred
operator|.
name|MiniMRClientCluster
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
name|MiniMRClientClusterFactory
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacitySchedulerConfiguration
operator|.
name|PREFIX
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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_class
DECL|class|GridmixTestUtils
specifier|public
class|class
name|GridmixTestUtils
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
name|GridmixTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEST
specifier|static
specifier|final
name|Path
name|DEST
init|=
operator|new
name|Path
argument_list|(
literal|"/gridmix"
argument_list|)
decl_stmt|;
DECL|field|dfs
specifier|static
name|FileSystem
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|dfsCluster
specifier|static
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|mrvl
specifier|static
name|MiniMRClientCluster
name|mrvl
init|=
literal|null
decl_stmt|;
DECL|field|GRIDMIX_USE_QUEUE_IN_TRACE
specifier|protected
specifier|static
specifier|final
name|String
name|GRIDMIX_USE_QUEUE_IN_TRACE
init|=
literal|"gridmix.job-submission.use-queue-in-trace"
decl_stmt|;
DECL|field|GRIDMIX_DEFAULT_QUEUE
specifier|protected
specifier|static
specifier|final
name|String
name|GRIDMIX_DEFAULT_QUEUE
init|=
literal|"gridmix.job-submission.default-queue"
decl_stmt|;
DECL|method|initCluster (Class<?> caller)
specifier|public
specifier|static
name|void
name|initCluster
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|caller
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//    conf.set("mapred.queue.names", "default,q1,q2");
name|conf
operator|.
name|set
argument_list|(
literal|"mapred.queue.names"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"root.queues"
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|PREFIX
operator|+
literal|"root.default.capacity"
argument_list|,
literal|"100.0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|GRIDMIX_USE_QUEUE_IN_TRACE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|GRIDMIX_DEFAULT_QUEUE
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
name|dfsCluster
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
literal|1
argument_list|)
operator|.
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// MiniDFSCluster(conf, 3, true, null);
name|dfs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JTConfig
operator|.
name|JT_RETIREJOBS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|mrvl
operator|=
name|MiniMRClientClusterFactory
operator|.
name|create
argument_list|(
name|caller
argument_list|,
literal|2
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|=
name|mrvl
operator|.
name|getConfig
argument_list|()
expr_stmt|;
name|String
index|[]
name|files
init|=
name|conf
operator|.
name|getStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILES
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|timestamps
init|=
operator|new
name|String
index|[
name|files
operator|.
name|length
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|timestamps
index|[
name|i
index|]
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setStrings
argument_list|(
name|MRJobConfig
operator|.
name|CACHE_FILE_TIMESTAMPS
argument_list|,
name|timestamps
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdownCluster ()
specifier|public
specifier|static
name|void
name|shutdownCluster
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|mrvl
operator|!=
literal|null
condition|)
block|{
name|mrvl
operator|.
name|stop
argument_list|()
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
block|}
block|}
comment|/**    * Methods to generate the home directory for dummy users.    *     * @param conf    */
DECL|method|createHomeAndStagingDirectory (String user, Configuration conf)
specifier|public
specifier|static
name|void
name|createHomeAndStagingDirectory
parameter_list|(
name|String
name|user
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
name|FileSystem
name|fs
init|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|path
init|=
literal|"/user/"
operator|+
name|user
decl_stmt|;
name|Path
name|homeDirectory
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|homeDirectory
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Home directory : "
operator|+
name|homeDirectory
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|homeDirectory
argument_list|)
expr_stmt|;
name|changePermission
argument_list|(
name|user
argument_list|,
name|homeDirectory
argument_list|,
name|fs
argument_list|)
expr_stmt|;
block|}
name|changePermission
argument_list|(
name|user
argument_list|,
name|homeDirectory
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|Path
name|stagingArea
init|=
operator|new
name|Path
argument_list|(
name|conf
operator|.
name|get
argument_list|(
literal|"mapreduce.jobtracker.staging.root.dir"
argument_list|,
literal|"/tmp/hadoop/mapred/staging"
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Staging root directory : "
operator|+
name|stagingArea
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|stagingArea
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|stagingArea
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|changePermission (String user, Path homeDirectory, FileSystem fs)
specifier|static
name|void
name|changePermission
parameter_list|(
name|String
name|user
parameter_list|,
name|Path
name|homeDirectory
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|fs
operator|.
name|setOwner
argument_list|(
name|homeDirectory
argument_list|,
name|user
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

