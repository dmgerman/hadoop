begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
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
name|FileContext
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
name|UnsupportedFileSystemException
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
name|AccessControlException
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|MiniYARNCluster
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|LocalDirsHandlerService
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|NodeManager
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
import|;
end_import

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
name|FileNotFoundException
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
name|Iterator
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Verify if NodeManager's in-memory good local dirs list and good log dirs list  * get updated properly when disks(nm-local-dirs and nm-log-dirs) fail. Also  * verify if the overall health status of the node gets updated properly when  * specified percentage of disks fail.  */
end_comment

begin_class
DECL|class|TestDiskFailures
specifier|public
class|class
name|TestDiskFailures
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
name|TestDiskFailures
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DISK_HEALTH_CHECK_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|DISK_HEALTH_CHECK_INTERVAL
init|=
literal|1000
decl_stmt|;
comment|//1 sec
DECL|field|localFS
specifier|private
specifier|static
name|FileContext
name|localFS
init|=
literal|null
decl_stmt|;
DECL|field|testDir
specifier|private
specifier|static
specifier|final
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
name|TestDiskFailures
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
DECL|field|localFSDirBase
specifier|private
specifier|static
specifier|final
name|File
name|localFSDirBase
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
name|TestDiskFailures
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-localDir"
argument_list|)
decl_stmt|;
DECL|field|numLocalDirs
specifier|private
specifier|static
specifier|final
name|int
name|numLocalDirs
init|=
literal|4
decl_stmt|;
DECL|field|numLogDirs
specifier|private
specifier|static
specifier|final
name|int
name|numLogDirs
init|=
literal|4
decl_stmt|;
DECL|field|yarnCluster
specifier|private
specifier|static
name|MiniYARNCluster
name|yarnCluster
decl_stmt|;
DECL|field|dirsHandler
name|LocalDirsHandlerService
name|dirsHandler
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IOException
block|{
name|localFS
operator|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
expr_stmt|;
name|localFS
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|localFSDirBase
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localFSDirBase
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Do not start cluster here
block|}
annotation|@
name|AfterClass
DECL|method|teardown ()
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
name|yarnCluster
operator|=
literal|null
expr_stmt|;
block|}
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|localFSDirBase
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make local-dirs fail/inaccessible and verify if NodeManager can    * recognize the disk failures properly and can update the list of    * local-dirs accordingly with good disks. Also verify the overall    * health status of the node.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testLocalDirsFailures ()
specifier|public
name|void
name|testLocalDirsFailures
parameter_list|()
throws|throws
name|IOException
block|{
name|testDirsFailures
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make log-dirs fail/inaccessible and verify if NodeManager can    * recognize the disk failures properly and can update the list of    * log-dirs accordingly with good disks. Also verify the overall health    * status of the node.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testLogDirsFailures ()
specifier|public
name|void
name|testLogDirsFailures
parameter_list|()
throws|throws
name|IOException
block|{
name|testDirsFailures
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make a local and log directory inaccessible during initialization    * and verify those bad directories are recognized and removed from    * the list of available local and log directories.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testDirFailuresOnStartup ()
specifier|public
name|void
name|testDirFailuresOnStartup
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|localDir1
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"localDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|localDir2
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"localDir2"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|logDir1
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"logDir1"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|logDir2
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"logDir2"
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
argument_list|,
name|localDir1
operator|+
literal|","
operator|+
name|localDir2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
argument_list|,
name|logDir1
operator|+
literal|","
operator|+
name|logDir2
argument_list|)
expr_stmt|;
name|prepareDirToFail
argument_list|(
name|localDir1
argument_list|)
expr_stmt|;
name|prepareDirToFail
argument_list|(
name|logDir2
argument_list|)
expr_stmt|;
name|LocalDirsHandlerService
name|dirSvc
init|=
operator|new
name|LocalDirsHandlerService
argument_list|()
decl_stmt|;
name|dirSvc
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|localDirs
init|=
name|dirSvc
operator|.
name|getLocalDirs
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|localDirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
name|localDir2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|localDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|logDirs
init|=
name|dirSvc
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|logDirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Path
argument_list|(
name|logDir1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|logDirs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDirsFailures (boolean localORLogDirs)
specifier|private
name|void
name|testDirsFailures
parameter_list|(
name|boolean
name|localORLogDirs
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dirType
init|=
name|localORLogDirs
condition|?
literal|"local"
else|:
literal|"log"
decl_stmt|;
name|String
name|dirsProperty
init|=
name|localORLogDirs
condition|?
name|YarnConfiguration
operator|.
name|NM_LOCAL_DIRS
else|:
name|YarnConfiguration
operator|.
name|NM_LOG_DIRS
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// set disk health check interval to a small value (say 1 sec).
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|NM_DISK_HEALTH_CHECK_INTERVAL_MS
argument_list|,
name|DISK_HEALTH_CHECK_INTERVAL
argument_list|)
expr_stmt|;
comment|// If 2 out of the total 4 local-dirs fail OR if 2 Out of the total 4
comment|// log-dirs fail, then the node's health status should become unhealthy.
name|conf
operator|.
name|setFloat
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MIN_HEALTHY_DISKS_FRACTION
argument_list|,
literal|0.60F
argument_list|)
expr_stmt|;
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|localFSDirBase
argument_list|)
expr_stmt|;
name|localFSDirBase
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting up YARN cluster"
argument_list|)
expr_stmt|;
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestDiskFailures
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numLocalDirs
argument_list|,
name|numLogDirs
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|NodeManager
name|nm
init|=
name|yarnCluster
operator|.
name|getNodeManager
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Configured nm-"
operator|+
name|dirType
operator|+
literal|"-dirs="
operator|+
name|nm
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|dirsProperty
argument_list|)
argument_list|)
expr_stmt|;
name|dirsHandler
operator|=
name|nm
operator|.
name|getNodeHealthChecker
argument_list|()
operator|.
name|getDiskHandler
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|localORLogDirs
condition|?
name|dirsHandler
operator|.
name|getLocalDirs
argument_list|()
else|:
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|String
index|[]
name|dirs
init|=
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Number of nm-"
operator|+
name|dirType
operator|+
literal|"-dirs is wrong."
argument_list|,
name|numLocalDirs
argument_list|,
name|dirs
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|expectedDirs
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|list
argument_list|)
decl_stmt|;
comment|// validate the health of disks initially
name|verifyDisksHealth
argument_list|(
name|localORLogDirs
argument_list|,
name|expectedDirs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Make 1 nm-local-dir fail and verify if "the nodemanager can identify
comment|// the disk failure(s) and can update the list of good nm-local-dirs.
name|prepareDirToFail
argument_list|(
name|dirs
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|expectedDirs
operator|=
name|dirs
index|[
literal|0
index|]
operator|+
literal|","
operator|+
name|dirs
index|[
literal|1
index|]
operator|+
literal|","
operator|+
name|dirs
index|[
literal|3
index|]
expr_stmt|;
name|verifyDisksHealth
argument_list|(
name|localORLogDirs
argument_list|,
name|expectedDirs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Now, make 1 more nm-local-dir/nm-log-dir fail and verify if "the
comment|// nodemanager can identify the disk failures and can update the list of
comment|// good nm-local-dirs/nm-log-dirs and can update the overall health status
comment|// of the node to unhealthy".
name|prepareDirToFail
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|expectedDirs
operator|=
name|dirs
index|[
literal|1
index|]
operator|+
literal|","
operator|+
name|dirs
index|[
literal|3
index|]
expr_stmt|;
name|verifyDisksHealth
argument_list|(
name|localORLogDirs
argument_list|,
name|expectedDirs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// Fail the remaining 2 local-dirs/log-dirs and verify if NM remains with
comment|// empty list of local-dirs/log-dirs and the overall health status is
comment|// unhealthy.
name|prepareDirToFail
argument_list|(
name|dirs
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|prepareDirToFail
argument_list|(
name|dirs
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|expectedDirs
operator|=
literal|""
expr_stmt|;
name|verifyDisksHealth
argument_list|(
name|localORLogDirs
argument_list|,
name|expectedDirs
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for the NodeManger to go for the disk-health-check at least once.    */
DECL|method|waitForDiskHealthCheck ()
specifier|private
name|void
name|waitForDiskHealthCheck
parameter_list|()
block|{
name|long
name|lastDisksCheckTime
init|=
name|dirsHandler
operator|.
name|getLastDisksCheckTime
argument_list|()
decl_stmt|;
name|long
name|time
init|=
name|lastDisksCheckTime
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
operator|&&
operator|(
name|time
operator|<=
name|lastDisksCheckTime
operator|)
condition|;
name|i
operator|++
control|)
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted while waiting for NodeManager's disk health check."
argument_list|)
expr_stmt|;
block|}
name|time
operator|=
name|dirsHandler
operator|.
name|getLastDisksCheckTime
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Verify if the NodeManager could identify disk failures.    * @param localORLogDirs<em>true</em> represent nm-local-dirs and<em>false    *</em> means nm-log-dirs    * @param expectedDirs expected nm-local-dirs/nm-log-dirs as a string    * @param isHealthy<em>true</em> if the overall node should be healthy    */
DECL|method|verifyDisksHealth (boolean localORLogDirs, String expectedDirs, boolean isHealthy)
specifier|private
name|void
name|verifyDisksHealth
parameter_list|(
name|boolean
name|localORLogDirs
parameter_list|,
name|String
name|expectedDirs
parameter_list|,
name|boolean
name|isHealthy
parameter_list|)
block|{
comment|// Wait for the NodeManager to identify disk failures.
name|waitForDiskHealthCheck
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|localORLogDirs
condition|?
name|dirsHandler
operator|.
name|getLocalDirs
argument_list|()
else|:
name|dirsHandler
operator|.
name|getLogDirs
argument_list|()
decl_stmt|;
name|String
name|seenDirs
init|=
name|StringUtils
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|list
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ExpectedDirs="
operator|+
name|expectedDirs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"SeenDirs="
operator|+
name|seenDirs
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"NodeManager could not identify disk failure."
argument_list|,
name|expectedDirs
operator|.
name|equals
argument_list|(
name|seenDirs
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Node's health in terms of disks is wrong"
argument_list|,
name|isHealthy
argument_list|,
name|dirsHandler
operator|.
name|areDisksHealthy
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Iterator
argument_list|<
name|RMNode
argument_list|>
name|iter
init|=
name|yarnCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getIsNodeHealthy
argument_list|()
operator|==
name|isHealthy
condition|)
block|{
break|break;
block|}
comment|// wait for the node health info to go to RM
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
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Interrupted while waiting for NM->RM heartbeat."
argument_list|)
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|RMNode
argument_list|>
name|iter
init|=
name|yarnCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"RM is not updated with the health status of a node"
argument_list|,
name|isHealthy
argument_list|,
name|iter
operator|.
name|next
argument_list|()
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getIsNodeHealthy
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Prepare directory for a failure: Replace the given directory on the    * local FileSystem with a regular file with the same name.    * This would cause failure of creation of directory in DiskChecker.checkDir()    * with the same name.    * @param dir the directory to be failed    * @throws IOException     */
DECL|method|prepareDirToFail (String dir)
specifier|private
name|void
name|prepareDirToFail
parameter_list|(
name|String
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Prepared "
operator|+
name|dir
operator|+
literal|" to fail."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

