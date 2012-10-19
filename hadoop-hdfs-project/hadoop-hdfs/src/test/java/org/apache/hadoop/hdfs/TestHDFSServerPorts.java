begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|Util
operator|.
name|fileAsURI
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
name|java
operator|.
name|net
operator|.
name|UnknownHostException
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|server
operator|.
name|namenode
operator|.
name|BackupNode
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|net
operator|.
name|DNS
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
comment|/**  * This test checks correctness of port usage by hdfs components:  * NameNode, DataNode, SecondaryNamenode and BackupNode.  *   * The correct behavior is:<br>   * - when a specific port is provided the server must either start on that port   * or fail by throwing {@link java.net.BindException}.<br>  * - if the port = 0 (ephemeral) then the server should choose   * a free port and start on it.  */
end_comment

begin_class
DECL|class|TestHDFSServerPorts
specifier|public
class|class
name|TestHDFSServerPorts
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestHDFSServerPorts
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// reset default 0.0.0.0 addresses in order to avoid IPv6 problem
DECL|field|THIS_HOST
specifier|static
specifier|final
name|String
name|THIS_HOST
init|=
name|getFullHostName
argument_list|()
operator|+
literal|":0"
decl_stmt|;
static|static
block|{
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|field|config
name|Configuration
name|config
decl_stmt|;
DECL|field|hdfsDir
name|File
name|hdfsDir
decl_stmt|;
comment|/**    * Attempt to determine the fully qualified domain name for this host     * to compare during testing.    *     * This is necessary because in order for the BackupNode test to correctly     * work, the namenode must have its http server started with the fully     * qualified address, as this is the one the backupnode will attempt to start    * on as well.    *     * @return Fully qualified hostname, or 127.0.0.1 if can't determine    */
DECL|method|getFullHostName ()
specifier|public
specifier|static
name|String
name|getFullHostName
parameter_list|()
block|{
try|try
block|{
return|return
name|DNS
operator|.
name|getDefaultHost
argument_list|(
literal|"default"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnknownHostException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to determine hostname.  May interfere with obtaining "
operator|+
literal|"valid test results."
argument_list|)
expr_stmt|;
return|return
literal|"127.0.0.1"
return|;
block|}
block|}
comment|/**    * Get base directory these tests should run in.    */
DECL|method|getTestingDir ()
specifier|private
name|String
name|getTestingDir
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
return|;
block|}
DECL|method|startNameNode ()
specifier|public
name|NameNode
name|startNameNode
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|startNameNode
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Start the namenode.    */
DECL|method|startNameNode (boolean withService)
specifier|public
name|NameNode
name|startNameNode
parameter_list|(
name|boolean
name|withService
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dataDir
init|=
name|getTestingDir
argument_list|()
decl_stmt|;
name|hdfsDir
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"dfs"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hdfsDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|hdfsDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete hdfs directory '"
operator|+
name|hdfsDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|config
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name1"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|config
argument_list|,
literal|"hdfs://"
operator|+
name|THIS_HOST
argument_list|)
expr_stmt|;
if|if
condition|(
name|withService
condition|)
block|{
name|NameNode
operator|.
name|setServiceAddress
argument_list|(
name|config
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
block|}
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
comment|// NameNode will modify config with the ports it bound to
return|return
name|NameNode
operator|.
name|createNameNode
argument_list|(
name|args
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Start the BackupNode    */
DECL|method|startBackupNode (Configuration conf)
specifier|public
name|BackupNode
name|startBackupNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dataDir
init|=
name|getTestingDir
argument_list|()
decl_stmt|;
comment|// Set up testing environment directories
name|hdfsDir
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"backupNode"
argument_list|)
expr_stmt|;
if|if
condition|(
name|hdfsDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|hdfsDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not delete hdfs directory '"
operator|+
name|hdfsDir
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|File
name|currDir
init|=
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name2"
argument_list|)
decl_stmt|;
name|File
name|currDir2
init|=
operator|new
name|File
argument_list|(
name|currDir
argument_list|,
literal|"current"
argument_list|)
decl_stmt|;
name|File
name|currDir3
init|=
operator|new
name|File
argument_list|(
name|currDir
argument_list|,
literal|"image"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|currDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|currDir2
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|currDir3
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name2"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"${"
operator|+
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// Start BackupNode
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
name|StartupOption
operator|.
name|BACKUP
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
name|BackupNode
name|bu
init|=
operator|(
name|BackupNode
operator|)
name|NameNode
operator|.
name|createNameNode
argument_list|(
name|args
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|bu
return|;
block|}
comment|/**    * Start the datanode.    */
DECL|method|startDataNode (int index, Configuration config)
specifier|public
name|DataNode
name|startDataNode
parameter_list|(
name|int
name|index
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|dataDir
init|=
name|getTestingDir
argument_list|()
decl_stmt|;
name|File
name|dataNodeDir
init|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
literal|"data-"
operator|+
name|index
argument_list|)
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
name|dataNodeDir
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{}
decl_stmt|;
comment|// NameNode will modify config with the ports it bound to
return|return
name|DataNode
operator|.
name|createDataNode
argument_list|(
name|args
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Stop the datanode.    */
DECL|method|stopDataNode (DataNode dn)
specifier|public
name|void
name|stopDataNode
parameter_list|(
name|DataNode
name|dn
parameter_list|)
block|{
if|if
condition|(
name|dn
operator|!=
literal|null
condition|)
block|{
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|stopNameNode (NameNode nn)
specifier|public
name|void
name|stopNameNode
parameter_list|(
name|NameNode
name|nn
parameter_list|)
block|{
if|if
condition|(
name|nn
operator|!=
literal|null
condition|)
block|{
name|nn
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
block|{
return|return
name|this
operator|.
name|config
return|;
block|}
comment|/**    * Check whether the namenode can be started.    */
DECL|method|canStartNameNode (Configuration conf)
specifier|private
name|boolean
name|canStartNameNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|NameNode
name|nn2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn2
operator|=
name|NameNode
operator|.
name|createNameNode
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|java
operator|.
name|net
operator|.
name|BindException
condition|)
return|return
literal|false
return|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|stopNameNode
argument_list|(
name|nn2
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Check whether the datanode can be started.    */
DECL|method|canStartDataNode (Configuration conf)
specifier|private
name|boolean
name|canStartDataNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|DataNode
name|dn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dn
operator|=
name|DataNode
operator|.
name|createDataNode
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|java
operator|.
name|net
operator|.
name|BindException
condition|)
return|return
literal|false
return|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|dn
operator|!=
literal|null
condition|)
name|dn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Check whether the secondary name-node can be started.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|canStartSecondaryNode (Configuration conf)
specifier|private
name|boolean
name|canStartSecondaryNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Using full name allows us not to have to add deprecation tag to
comment|// entire source file.
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|SecondaryNameNode
name|sn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sn
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|SecondaryNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|java
operator|.
name|net
operator|.
name|BindException
condition|)
return|return
literal|false
return|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|sn
operator|!=
literal|null
condition|)
name|sn
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Check whether the BackupNode can be started.    */
DECL|method|canStartBackupNode (Configuration conf)
specifier|private
name|boolean
name|canStartBackupNode
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|BackupNode
name|bn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|bn
operator|=
name|startBackupNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|java
operator|.
name|net
operator|.
name|BindException
condition|)
return|return
literal|false
return|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|bn
operator|!=
literal|null
condition|)
name|bn
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testNameNodePorts ()
specifier|public
name|void
name|testNameNodePorts
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestNameNodePorts
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|runTestNameNodePorts
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify namenode port usage.    */
DECL|method|runTestNameNodePorts (boolean withService)
specifier|public
name|void
name|runTestNameNodePorts
parameter_list|(
name|boolean
name|withService
parameter_list|)
throws|throws
name|Exception
block|{
name|NameNode
name|nn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn
operator|=
name|startNameNode
argument_list|(
name|withService
argument_list|)
expr_stmt|;
comment|// start another namenode on the same port
name|Configuration
name|conf2
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|fileAsURI
argument_list|(
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"name2"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
name|boolean
name|started
init|=
name|canStartNameNode
argument_list|(
name|conf2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should fail
comment|// start on a different main port
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf2
argument_list|,
literal|"hdfs://"
operator|+
name|THIS_HOST
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartNameNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should fail again
comment|// reset conf2 since NameNode modifies it
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf2
argument_list|,
literal|"hdfs://"
operator|+
name|THIS_HOST
argument_list|)
expr_stmt|;
comment|// different http port
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartNameNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
if|if
condition|(
name|withService
condition|)
block|{
name|assertFalse
argument_list|(
literal|"Should've failed on service port"
argument_list|,
name|started
argument_list|)
expr_stmt|;
comment|// reset conf2 since NameNode modifies it
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf2
argument_list|,
literal|"hdfs://"
operator|+
name|THIS_HOST
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
comment|// Set Service address
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartNameNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|started
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stopNameNode
argument_list|(
name|nn
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify datanode port usage.    */
annotation|@
name|Test
DECL|method|testDataNodePorts ()
specifier|public
name|void
name|testDataNodePorts
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn
operator|=
name|startNameNode
argument_list|()
expr_stmt|;
comment|// start data-node on the same port as name-node
name|Configuration
name|conf2
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_DATA_DIR_KEY
argument_list|,
operator|new
name|File
argument_list|(
name|hdfsDir
argument_list|,
literal|"data"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
name|FileSystem
operator|.
name|getDefaultUri
argument_list|(
name|config
argument_list|)
operator|.
name|getAuthority
argument_list|()
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|boolean
name|started
init|=
name|canStartDataNode
argument_list|(
name|conf2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should fail
comment|// bind http server to the same port as name-node
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartDataNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should fail
comment|// both ports are different from the name-node ones
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_DATANODE_IPC_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartDataNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should start now
block|}
finally|finally
block|{
name|stopNameNode
argument_list|(
name|nn
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Verify secondary namenode port usage.    */
annotation|@
name|Test
DECL|method|testSecondaryNodePorts ()
specifier|public
name|void
name|testSecondaryNodePorts
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn
operator|=
name|startNameNode
argument_list|()
expr_stmt|;
comment|// bind http server to the same port as name-node
name|Configuration
name|conf2
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"= Starting 1 on: "
operator|+
name|conf2
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|started
init|=
name|canStartSecondaryNode
argument_list|(
name|conf2
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should fail
comment|// bind http server to a different port
name|conf2
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"= Starting 2 on: "
operator|+
name|conf2
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|started
operator|=
name|canStartSecondaryNode
argument_list|(
name|conf2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|started
argument_list|)
expr_stmt|;
comment|// should start now
block|}
finally|finally
block|{
name|stopNameNode
argument_list|(
name|nn
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Verify BackupNode port usage.      */
annotation|@
name|Test
DECL|method|testBackupNodePorts ()
specifier|public
name|void
name|testBackupNodePorts
parameter_list|()
throws|throws
name|Exception
block|{
name|NameNode
name|nn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|nn
operator|=
name|startNameNode
argument_list|()
expr_stmt|;
name|Configuration
name|backup_config
init|=
operator|new
name|HdfsConfiguration
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|backup_config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
comment|// bind http server to the same port as name-node
name|backup_config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|,
name|backup_config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"= Starting 1 on: "
operator|+
name|backup_config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Backup started on same port as Namenode"
argument_list|,
name|canStartBackupNode
argument_list|(
name|backup_config
argument_list|)
argument_list|)
expr_stmt|;
comment|// should fail
comment|// bind http server to a different port
name|backup_config
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|,
name|THIS_HOST
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"= Starting 2 on: "
operator|+
name|backup_config
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|started
init|=
name|canStartBackupNode
argument_list|(
name|backup_config
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Backup Namenode should've started"
argument_list|,
name|started
argument_list|)
expr_stmt|;
comment|// should start now
block|}
finally|finally
block|{
name|stopNameNode
argument_list|(
name|nn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

