begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.sps
package|package
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
name|sps
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
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
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
name|InetSocketAddress
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|DFSConfigKeys
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
name|DFSUtil
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
name|HdfsConfiguration
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
name|protocol
operator|.
name|Block
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
name|protocol
operator|.
name|HdfsConstants
operator|.
name|StoragePolicySatisfierMode
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
name|balancer
operator|.
name|NameNodeConnector
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
name|sps
operator|.
name|BlockMovementListener
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
name|sps
operator|.
name|StoragePolicySatisfier
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
name|NetUtils
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
name|SecurityUtil
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
comment|/**  * This class starts and runs external SPS service.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ExternalStoragePolicySatisfier
specifier|public
specifier|final
class|class
name|ExternalStoragePolicySatisfier
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ExternalStoragePolicySatisfier
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|ExternalStoragePolicySatisfier ()
specifier|private
name|ExternalStoragePolicySatisfier
parameter_list|()
block|{
comment|// This is just a class to start and run external sps.
block|}
comment|/**    * Main method to start SPS service.    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|NameNodeConnector
name|nnc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|StoragePolicySatisfier
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|HdfsConfiguration
name|spsConf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
comment|// login with SPS keytab
name|secureLogin
argument_list|(
name|spsConf
argument_list|)
expr_stmt|;
name|StoragePolicySatisfier
name|sps
init|=
operator|new
name|StoragePolicySatisfier
argument_list|(
name|spsConf
argument_list|)
decl_stmt|;
name|nnc
operator|=
name|getNameNodeConnector
argument_list|(
name|spsConf
argument_list|)
expr_stmt|;
name|boolean
name|spsRunning
decl_stmt|;
name|spsRunning
operator|=
name|nnc
operator|.
name|getDistributedFileSystem
argument_list|()
operator|.
name|getClient
argument_list|()
operator|.
name|isInternalSatisfierRunning
argument_list|()
expr_stmt|;
if|if
condition|(
name|spsRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Startup failed due to StoragePolicySatisfier"
operator|+
literal|" running inside Namenode."
argument_list|)
throw|;
block|}
name|ExternalSPSContext
name|context
init|=
operator|new
name|ExternalSPSContext
argument_list|(
name|sps
argument_list|,
name|nnc
argument_list|)
decl_stmt|;
name|ExternalBlockMovementListener
name|blkMoveListener
init|=
operator|new
name|ExternalBlockMovementListener
argument_list|()
decl_stmt|;
name|ExternalSPSBlockMoveTaskHandler
name|externalHandler
init|=
operator|new
name|ExternalSPSBlockMoveTaskHandler
argument_list|(
name|spsConf
argument_list|,
name|nnc
argument_list|,
name|sps
argument_list|)
decl_stmt|;
name|externalHandler
operator|.
name|init
argument_list|()
expr_stmt|;
name|sps
operator|.
name|init
argument_list|(
name|context
argument_list|,
operator|new
name|ExternalSPSFileIDCollector
argument_list|(
name|context
argument_list|,
name|sps
argument_list|)
argument_list|,
name|externalHandler
argument_list|,
name|blkMoveListener
argument_list|)
expr_stmt|;
name|sps
operator|.
name|start
argument_list|(
literal|true
argument_list|,
name|StoragePolicySatisfierMode
operator|.
name|EXTERNAL
argument_list|)
expr_stmt|;
if|if
condition|(
name|sps
operator|!=
literal|null
condition|)
block|{
name|sps
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to start storage policy satisfier."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|nnc
operator|!=
literal|null
condition|)
block|{
name|nnc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|secureLogin (Configuration conf)
specifier|private
specifier|static
name|void
name|secureLogin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|addr
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_SPS_ADDRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_SPS_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|socAddr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|addr
argument_list|,
literal|0
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_SPS_ADDRESS_KEY
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_SPS_KEYTAB_FILE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_SPS_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getNameNodeConnector (Configuration conf)
specifier|private
specifier|static
name|NameNodeConnector
name|getNameNodeConnector
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Collection
argument_list|<
name|URI
argument_list|>
name|namenodes
init|=
name|DFSUtil
operator|.
name|getInternalNsRpcUris
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|externalSPSPathId
init|=
name|HdfsServerConstants
operator|.
name|MOVER_ID_PATH
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|NameNodeConnector
argument_list|>
name|nncs
init|=
name|NameNodeConnector
operator|.
name|newNameNodeConnectors
argument_list|(
name|namenodes
argument_list|,
name|ExternalStoragePolicySatisfier
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|externalSPSPathId
argument_list|,
name|conf
argument_list|,
name|NameNodeConnector
operator|.
name|DEFAULT_MAX_IDLE_ITERATIONS
argument_list|)
decl_stmt|;
return|return
name|nncs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to connect with namenode"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
comment|// retry the connection after few secs
block|}
block|}
block|}
comment|/**    * It is implementation of BlockMovementListener.    */
DECL|class|ExternalBlockMovementListener
specifier|private
specifier|static
class|class
name|ExternalBlockMovementListener
implements|implements
name|BlockMovementListener
block|{
DECL|field|actualBlockMovements
specifier|private
name|List
argument_list|<
name|Block
argument_list|>
name|actualBlockMovements
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|notifyMovementTriedBlocks (Block[] moveAttemptFinishedBlks)
specifier|public
name|void
name|notifyMovementTriedBlocks
parameter_list|(
name|Block
index|[]
name|moveAttemptFinishedBlks
parameter_list|)
block|{
for|for
control|(
name|Block
name|block
range|:
name|moveAttemptFinishedBlks
control|)
block|{
name|actualBlockMovements
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Movement attempted blocks"
argument_list|,
name|actualBlockMovements
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

