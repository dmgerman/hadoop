begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
name|namenode
operator|.
name|ha
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KEYTAB_FILE_KEY
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|HadoopIllegalArgumentException
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
name|Configurable
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
name|HAUtil
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
name|NameNodeProxies
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
name|common
operator|.
name|InconsistentFSStateException
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
name|Storage
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
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|Storage
operator|.
name|StorageState
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
name|EditLogInputStream
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
name|FSImage
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
name|FSNamesystem
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
name|NNStorage
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
name|NNUpgradeUtil
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|TransferFsImage
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
name|NNStorage
operator|.
name|NameNodeFile
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
name|protocol
operator|.
name|NamenodeProtocol
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
name|protocol
operator|.
name|NamespaceInfo
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
name|tools
operator|.
name|DFSHAAdmin
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
name|IOUtils
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
name|MD5Hash
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
name|Tool
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Tool which allows the standby node's storage directories to be bootstrapped  * by copying the latest namespace snapshot from the active namenode. This is  * used when first configuring an HA cluster.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BootstrapStandby
specifier|public
class|class
name|BootstrapStandby
implements|implements
name|Tool
implements|,
name|Configurable
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
name|BootstrapStandby
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nsId
specifier|private
name|String
name|nsId
decl_stmt|;
DECL|field|nnId
specifier|private
name|String
name|nnId
decl_stmt|;
DECL|field|otherNNId
specifier|private
name|String
name|otherNNId
decl_stmt|;
DECL|field|otherHttpAddr
specifier|private
name|URL
name|otherHttpAddr
decl_stmt|;
DECL|field|otherIpcAddr
specifier|private
name|InetSocketAddress
name|otherIpcAddr
decl_stmt|;
DECL|field|dirsToFormat
specifier|private
name|Collection
argument_list|<
name|URI
argument_list|>
name|dirsToFormat
decl_stmt|;
DECL|field|editUrisToFormat
specifier|private
name|List
argument_list|<
name|URI
argument_list|>
name|editUrisToFormat
decl_stmt|;
DECL|field|sharedEditsUris
specifier|private
name|List
argument_list|<
name|URI
argument_list|>
name|sharedEditsUris
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|force
specifier|private
name|boolean
name|force
init|=
literal|false
decl_stmt|;
DECL|field|interactive
specifier|private
name|boolean
name|interactive
init|=
literal|true
decl_stmt|;
DECL|field|skipSharedEditsCheck
specifier|private
name|boolean
name|skipSharedEditsCheck
init|=
literal|false
decl_stmt|;
comment|// Exit/return codes.
DECL|field|ERR_CODE_FAILED_CONNECT
specifier|static
specifier|final
name|int
name|ERR_CODE_FAILED_CONNECT
init|=
literal|2
decl_stmt|;
DECL|field|ERR_CODE_INVALID_VERSION
specifier|static
specifier|final
name|int
name|ERR_CODE_INVALID_VERSION
init|=
literal|3
decl_stmt|;
comment|// Skip 4 - was used in previous versions, but no longer returned.
DECL|field|ERR_CODE_ALREADY_FORMATTED
specifier|static
specifier|final
name|int
name|ERR_CODE_ALREADY_FORMATTED
init|=
literal|5
decl_stmt|;
DECL|field|ERR_CODE_LOGS_UNAVAILABLE
specifier|static
specifier|final
name|int
name|ERR_CODE_LOGS_UNAVAILABLE
init|=
literal|6
decl_stmt|;
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|parseArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|parseConfAndFindOtherNN
argument_list|()
expr_stmt|;
name|NameNode
operator|.
name|checkAllowFormat
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|myAddr
init|=
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|DFS_NAMENODE_KEYTAB_FILE_KEY
argument_list|,
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|myAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|SecurityUtil
operator|.
name|doAsLoginUserOrFatal
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|doRun
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|parseArgs (String[] args)
specifier|private
name|void
name|parseArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
if|if
condition|(
literal|"-force"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|force
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-nonInteractive"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|interactive
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"-skipSharedEditsCheck"
operator|.
name|equals
argument_list|(
name|arg
argument_list|)
condition|)
block|{
name|skipSharedEditsCheck
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|printUsage
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Illegal argument: "
operator|+
name|arg
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" [-force] [-nonInteractive] [-skipSharedEditsCheck]"
argument_list|)
expr_stmt|;
block|}
DECL|method|createNNProtocolProxy ()
specifier|private
name|NamenodeProtocol
name|createNNProtocolProxy
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|NameNodeProxies
operator|.
name|createNonHAProxy
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|otherIpcAddr
argument_list|,
name|NamenodeProtocol
operator|.
name|class
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|getProxy
argument_list|()
return|;
block|}
DECL|method|doRun ()
specifier|private
name|int
name|doRun
parameter_list|()
throws|throws
name|IOException
block|{
name|NamenodeProtocol
name|proxy
init|=
name|createNNProtocolProxy
argument_list|()
decl_stmt|;
name|NamespaceInfo
name|nsInfo
decl_stmt|;
name|boolean
name|isUpgradeFinalized
decl_stmt|;
try|try
block|{
name|nsInfo
operator|=
name|proxy
operator|.
name|versionRequest
argument_list|()
expr_stmt|;
name|isUpgradeFinalized
operator|=
name|proxy
operator|.
name|isUpgradeFinalized
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Unable to fetch namespace information from active NN at "
operator|+
name|otherIpcAddr
operator|+
literal|": "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Full exception trace"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
return|return
name|ERR_CODE_FAILED_CONNECT
return|;
block|}
if|if
condition|(
operator|!
name|checkLayoutVersion
argument_list|(
name|nsInfo
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Layout version on remote node ("
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|") does not match "
operator|+
literal|"this node's layout version ("
operator|+
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|+
literal|")"
argument_list|)
expr_stmt|;
return|return
name|ERR_CODE_INVALID_VERSION
return|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"=====================================================\n"
operator|+
literal|"About to bootstrap Standby ID "
operator|+
name|nnId
operator|+
literal|" from:\n"
operator|+
literal|"           Nameservice ID: "
operator|+
name|nsId
operator|+
literal|"\n"
operator|+
literal|"        Other Namenode ID: "
operator|+
name|otherNNId
operator|+
literal|"\n"
operator|+
literal|"  Other NN's HTTP address: "
operator|+
name|otherHttpAddr
operator|+
literal|"\n"
operator|+
literal|"  Other NN's IPC  address: "
operator|+
name|otherIpcAddr
operator|+
literal|"\n"
operator|+
literal|"             Namespace ID: "
operator|+
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"            Block pool ID: "
operator|+
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"               Cluster ID: "
operator|+
name|nsInfo
operator|.
name|getClusterID
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"           Layout version: "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"       isUpgradeFinalized: "
operator|+
name|isUpgradeFinalized
operator|+
literal|"\n"
operator|+
literal|"====================================================="
argument_list|)
expr_stmt|;
name|NNStorage
name|storage
init|=
operator|new
name|NNStorage
argument_list|(
name|conf
argument_list|,
name|dirsToFormat
argument_list|,
name|editUrisToFormat
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isUpgradeFinalized
condition|)
block|{
comment|// the remote NameNode is in upgrade state, this NameNode should also
comment|// create the previous directory. First prepare the upgrade and rename
comment|// the current dir to previous.tmp.
name|LOG
operator|.
name|info
argument_list|(
literal|"The active NameNode is in Upgrade. "
operator|+
literal|"Prepare the upgrade for the standby NameNode as well."
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|doPreUpgrade
argument_list|(
name|storage
argument_list|,
name|nsInfo
argument_list|)
condition|)
block|{
return|return
name|ERR_CODE_ALREADY_FORMATTED
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|format
argument_list|(
name|storage
argument_list|,
name|nsInfo
argument_list|)
condition|)
block|{
comment|// prompt the user to format storage
return|return
name|ERR_CODE_ALREADY_FORMATTED
return|;
block|}
comment|// download the fsimage from active namenode
name|int
name|download
init|=
name|downloadImage
argument_list|(
name|storage
argument_list|,
name|proxy
argument_list|)
decl_stmt|;
if|if
condition|(
name|download
operator|!=
literal|0
condition|)
block|{
return|return
name|download
return|;
block|}
comment|// finish the upgrade: rename previous.tmp to previous
if|if
condition|(
operator|!
name|isUpgradeFinalized
condition|)
block|{
name|doUpgrade
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
comment|/**    * Iterate over all the storage directories, checking if it should be    * formatted. Format the storage if necessary and allowed by the user.    * @return True if formatting is processed    */
DECL|method|format (NNStorage storage, NamespaceInfo nsInfo)
specifier|private
name|boolean
name|format
parameter_list|(
name|NNStorage
name|storage
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check with the user before blowing away data.
if|if
condition|(
operator|!
name|Storage
operator|.
name|confirmFormat
argument_list|(
name|storage
operator|.
name|dirIterable
argument_list|(
literal|null
argument_list|)
argument_list|,
name|force
argument_list|,
name|interactive
argument_list|)
condition|)
block|{
name|storage
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
comment|// Format the storage (writes VERSION file)
name|storage
operator|.
name|format
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * This is called when using bootstrapStandby for HA upgrade. The SBN should    * also create previous directory so that later when it starts, it understands    * that the cluster is in the upgrade state. This function renames the old    * current directory to previous.tmp.    */
DECL|method|doPreUpgrade (NNStorage storage, NamespaceInfo nsInfo)
specifier|private
name|boolean
name|doPreUpgrade
parameter_list|(
name|NNStorage
name|storage
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isFormatted
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|StorageDirectory
argument_list|,
name|StorageState
argument_list|>
name|dataDirStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|isFormatted
operator|=
name|FSImage
operator|.
name|recoverStorageDirs
argument_list|(
name|StartupOption
operator|.
name|UPGRADE
argument_list|,
name|storage
argument_list|,
name|dataDirStates
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataDirStates
operator|.
name|values
argument_list|()
operator|.
name|contains
argument_list|(
name|StorageState
operator|.
name|NOT_FORMATTED
argument_list|)
condition|)
block|{
comment|// recoverStorageDirs returns true if there is a formatted directory
name|isFormatted
operator|=
literal|false
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"The original storage directory is not formatted."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InconsistentFSStateException
name|e
parameter_list|)
block|{
comment|// if the storage is in a bad state,
name|LOG
operator|.
name|warn
argument_list|(
literal|"The storage directory is in an inconsistent state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|storage
operator|.
name|unlockAll
argument_list|()
expr_stmt|;
block|}
comment|// if there is InconsistentFSStateException or the storage is not formatted,
comment|// format the storage. Although this format is done through the new
comment|// software, since in HA setup the SBN is rolled back through
comment|// "-bootstrapStandby", we should still be fine.
if|if
condition|(
operator|!
name|isFormatted
operator|&&
operator|!
name|format
argument_list|(
name|storage
argument_list|,
name|nsInfo
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// make sure there is no previous directory
name|FSImage
operator|.
name|checkUpgrade
argument_list|(
name|storage
argument_list|)
expr_stmt|;
comment|// Do preUpgrade for each directory
for|for
control|(
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|it
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
literal|false
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorageDirectory
name|sd
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|NNUpgradeUtil
operator|.
name|renameCurToTmp
argument_list|(
name|sd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to move aside pre-upgrade storage "
operator|+
literal|"in image directory "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
name|storage
operator|.
name|setStorageInfo
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
name|storage
operator|.
name|setBlockPoolID
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|doUpgrade (NNStorage storage)
specifier|private
name|void
name|doUpgrade
parameter_list|(
name|NNStorage
name|storage
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|it
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
literal|false
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|StorageDirectory
name|sd
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|NNUpgradeUtil
operator|.
name|doUpgrade
argument_list|(
name|sd
argument_list|,
name|storage
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|downloadImage (NNStorage storage, NamenodeProtocol proxy)
specifier|private
name|int
name|downloadImage
parameter_list|(
name|NNStorage
name|storage
parameter_list|,
name|NamenodeProtocol
name|proxy
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Load the newly formatted image, using all of the directories
comment|// (including shared edits)
specifier|final
name|long
name|imageTxId
init|=
name|proxy
operator|.
name|getMostRecentCheckpointTxId
argument_list|()
decl_stmt|;
specifier|final
name|long
name|curTxId
init|=
name|proxy
operator|.
name|getTransactionID
argument_list|()
decl_stmt|;
name|FSImage
name|image
init|=
operator|new
name|FSImage
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|image
operator|.
name|getStorage
argument_list|()
operator|.
name|setStorageInfo
argument_list|(
name|storage
argument_list|)
expr_stmt|;
name|image
operator|.
name|initEditLog
argument_list|(
name|StartupOption
operator|.
name|REGULAR
argument_list|)
expr_stmt|;
assert|assert
name|image
operator|.
name|getEditLog
argument_list|()
operator|.
name|isOpenForRead
argument_list|()
operator|:
literal|"Expected edit log to be open for read"
assert|;
comment|// Ensure that we have enough edits already in the shared directory to
comment|// start up from the last checkpoint on the active.
if|if
condition|(
operator|!
name|skipSharedEditsCheck
operator|&&
operator|!
name|checkLogsAvailableForRead
argument_list|(
name|image
argument_list|,
name|imageTxId
argument_list|,
name|curTxId
argument_list|)
condition|)
block|{
return|return
name|ERR_CODE_LOGS_UNAVAILABLE
return|;
block|}
name|image
operator|.
name|getStorage
argument_list|()
operator|.
name|writeTransactionIdFileToStorage
argument_list|(
name|curTxId
argument_list|)
expr_stmt|;
comment|// Download that checkpoint into our storage directories.
name|MD5Hash
name|hash
init|=
name|TransferFsImage
operator|.
name|downloadImageToStorage
argument_list|(
name|otherHttpAddr
argument_list|,
name|imageTxId
argument_list|,
name|storage
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|image
operator|.
name|saveDigestAndRenameCheckpointImage
argument_list|(
name|NameNodeFile
operator|.
name|IMAGE
argument_list|,
name|imageTxId
argument_list|,
name|hash
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
name|ioe
throw|;
block|}
finally|finally
block|{
name|image
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|checkLogsAvailableForRead (FSImage image, long imageTxId, long curTxIdOnOtherNode)
specifier|private
name|boolean
name|checkLogsAvailableForRead
parameter_list|(
name|FSImage
name|image
parameter_list|,
name|long
name|imageTxId
parameter_list|,
name|long
name|curTxIdOnOtherNode
parameter_list|)
block|{
if|if
condition|(
name|imageTxId
operator|==
name|curTxIdOnOtherNode
condition|)
block|{
comment|// The other node hasn't written any logs since the last checkpoint.
comment|// This can be the case if the NN was freshly formatted as HA, and
comment|// then started in standby mode, so it has no edit logs at all.
return|return
literal|true
return|;
block|}
name|long
name|firstTxIdInLogs
init|=
name|imageTxId
operator|+
literal|1
decl_stmt|;
assert|assert
name|curTxIdOnOtherNode
operator|>=
name|firstTxIdInLogs
operator|:
literal|"first="
operator|+
name|firstTxIdInLogs
operator|+
literal|" onOtherNode="
operator|+
name|curTxIdOnOtherNode
assert|;
try|try
block|{
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|streams
init|=
name|image
operator|.
name|getEditLog
argument_list|()
operator|.
name|selectInputStreams
argument_list|(
name|firstTxIdInLogs
argument_list|,
name|curTxIdOnOtherNode
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|EditLogInputStream
name|stream
range|:
name|streams
control|)
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Unable to read transaction ids "
operator|+
name|firstTxIdInLogs
operator|+
literal|"-"
operator|+
name|curTxIdOnOtherNode
operator|+
literal|" from the configured shared edits storage "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|sharedEditsUris
argument_list|)
operator|+
literal|". "
operator|+
literal|"Please copy these logs into the shared edits storage "
operator|+
literal|"or call saveNamespace on the active node.\n"
operator|+
literal|"Error: "
operator|+
name|e
operator|.
name|getLocalizedMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|fatal
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|method|checkLayoutVersion (NamespaceInfo nsInfo)
specifier|private
name|boolean
name|checkLayoutVersion
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|==
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|)
return|;
block|}
DECL|method|parseConfAndFindOtherNN ()
specifier|private
name|void
name|parseConfAndFindOtherNN
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
name|nsId
operator|=
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"HA is not enabled for this namenode."
argument_list|)
throw|;
block|}
name|nnId
operator|=
name|HAUtil
operator|.
name|getNameNodeId
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|conf
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|HAUtil
operator|.
name|usesSharedEditsDir
argument_list|(
name|conf
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Shared edits storage is not enabled for this namenode."
argument_list|)
throw|;
block|}
name|Configuration
name|otherNode
init|=
name|HAUtil
operator|.
name|getConfForOtherNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|otherNNId
operator|=
name|HAUtil
operator|.
name|getNameNodeId
argument_list|(
name|otherNode
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
name|otherIpcAddr
operator|=
name|NameNode
operator|.
name|getServiceAddress
argument_list|(
name|otherNode
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|otherIpcAddr
operator|.
name|getPort
argument_list|()
operator|!=
literal|0
operator|&&
operator|!
name|otherIpcAddr
operator|.
name|getAddress
argument_list|()
operator|.
name|isAnyLocalAddress
argument_list|()
argument_list|,
literal|"Could not determine valid IPC address for other NameNode (%s)"
operator|+
literal|", got: %s"
argument_list|,
name|otherNNId
argument_list|,
name|otherIpcAddr
argument_list|)
expr_stmt|;
specifier|final
name|String
name|scheme
init|=
name|DFSUtil
operator|.
name|getHttpClientScheme
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|otherHttpAddr
operator|=
name|DFSUtil
operator|.
name|getInfoServerWithDefaultHost
argument_list|(
name|otherIpcAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|otherNode
argument_list|,
name|scheme
argument_list|)
operator|.
name|toURL
argument_list|()
expr_stmt|;
name|dirsToFormat
operator|=
name|FSNamesystem
operator|.
name|getNamespaceDirs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|editUrisToFormat
operator|=
name|FSNamesystem
operator|.
name|getNamespaceEditsDirs
argument_list|(
name|conf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|sharedEditsUris
operator|=
name|FSNamesystem
operator|.
name|getSharedEditsDirs
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|DFSHAAdmin
operator|.
name|addSecurityConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|run (String[] argv, Configuration conf)
specifier|public
specifier|static
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|BootstrapStandby
name|bs
init|=
operator|new
name|BootstrapStandby
argument_list|()
decl_stmt|;
name|bs
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|ToolRunner
operator|.
name|run
argument_list|(
name|bs
argument_list|,
name|argv
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

