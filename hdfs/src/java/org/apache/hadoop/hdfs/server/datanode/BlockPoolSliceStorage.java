begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|HardLink
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
name|FSConstants
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
name|LayoutVersion
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
name|LayoutVersion
operator|.
name|Feature
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
name|StorageInfo
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
name|HdfsConstants
operator|.
name|NodeType
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
name|HdfsConstants
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
name|util
operator|.
name|Daemon
import|;
end_import

begin_comment
comment|/**  * Manages storage for the set of BlockPoolSlices which share a particular   * block pool id, on this DataNode.  *   * This class supports the following functionality:  *<ol>  *<li> Formatting a new block pool storage</li>  *<li> Recovering a storage state to a consistent state (if possible></li>  *<li> Taking a snapshot of the block pool during upgrade</li>  *<li> Rolling back a block pool to a previous snapshot</li>  *<li> Finalizing block storage by deletion of a snapshot</li>  *</ul>  *   * @see Storage  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockPoolSliceStorage
specifier|public
class|class
name|BlockPoolSliceStorage
extends|extends
name|Storage
block|{
DECL|field|BLOCK_POOL_PATH_PATTERN
specifier|private
specifier|static
specifier|final
name|Pattern
name|BLOCK_POOL_PATH_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(.*)"
operator|+
literal|"(\\/BP-[0-9]+\\-\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\-[0-9]+\\/.*)$"
argument_list|)
decl_stmt|;
DECL|field|blockpoolID
specifier|private
name|String
name|blockpoolID
init|=
literal|""
decl_stmt|;
comment|// id of the blockpool
DECL|method|BlockPoolSliceStorage (StorageInfo storageInfo, String bpid)
specifier|public
name|BlockPoolSliceStorage
parameter_list|(
name|StorageInfo
name|storageInfo
parameter_list|,
name|String
name|bpid
parameter_list|)
block|{
name|super
argument_list|(
name|NodeType
operator|.
name|DATA_NODE
argument_list|,
name|storageInfo
argument_list|)
expr_stmt|;
name|blockpoolID
operator|=
name|bpid
expr_stmt|;
block|}
DECL|method|BlockPoolSliceStorage (int namespaceID, String bpID, long cTime, String clusterId)
name|BlockPoolSliceStorage
parameter_list|(
name|int
name|namespaceID
parameter_list|,
name|String
name|bpID
parameter_list|,
name|long
name|cTime
parameter_list|,
name|String
name|clusterId
parameter_list|)
block|{
name|super
argument_list|(
name|NodeType
operator|.
name|DATA_NODE
argument_list|)
expr_stmt|;
name|this
operator|.
name|namespaceID
operator|=
name|namespaceID
expr_stmt|;
name|this
operator|.
name|blockpoolID
operator|=
name|bpID
expr_stmt|;
name|this
operator|.
name|cTime
operator|=
name|cTime
expr_stmt|;
name|this
operator|.
name|clusterID
operator|=
name|clusterId
expr_stmt|;
block|}
comment|/**    * Analyze storage directories. Recover from previous transitions if required.    *     * @param datanode Datanode to which this storage belongs to    * @param nsInfo namespace information    * @param dataDirs storage directories of block pool    * @param startOpt startup option    * @throws IOException on error    */
DECL|method|recoverTransitionRead (DataNode datanode, NamespaceInfo nsInfo, Collection<File> dataDirs, StartupOption startOpt)
name|void
name|recoverTransitionRead
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|Collection
argument_list|<
name|File
argument_list|>
name|dataDirs
parameter_list|,
name|StartupOption
name|startOpt
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|FSConstants
operator|.
name|LAYOUT_VERSION
operator|==
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|:
literal|"Block-pool and name-node layout versions must be the same."
assert|;
comment|// 1. For each BP data directory analyze the state and
comment|// check whether all is consistent before transitioning.
name|this
operator|.
name|storageDirs
operator|=
operator|new
name|ArrayList
argument_list|<
name|StorageDirectory
argument_list|>
argument_list|(
name|dataDirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|StorageState
argument_list|>
name|dataDirStates
init|=
operator|new
name|ArrayList
argument_list|<
name|StorageState
argument_list|>
argument_list|(
name|dataDirs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|File
argument_list|>
name|it
init|=
name|dataDirs
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|File
name|dataDir
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|StorageDirectory
name|sd
init|=
operator|new
name|StorageDirectory
argument_list|(
name|dataDir
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StorageState
name|curState
decl_stmt|;
try|try
block|{
name|curState
operator|=
name|sd
operator|.
name|analyzeStorage
argument_list|(
name|startOpt
argument_list|)
expr_stmt|;
comment|// sd is locked but not opened
switch|switch
condition|(
name|curState
condition|)
block|{
case|case
name|NORMAL
case|:
break|break;
case|case
name|NON_EXISTENT
case|:
comment|// ignore this storage
name|LOG
operator|.
name|info
argument_list|(
literal|"Storage directory "
operator|+
name|dataDir
operator|+
literal|" does not exist."
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
continue|continue;
case|case
name|NOT_FORMATTED
case|:
comment|// format
name|LOG
operator|.
name|info
argument_list|(
literal|"Storage directory "
operator|+
name|dataDir
operator|+
literal|" is not formatted."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Formatting ..."
argument_list|)
expr_stmt|;
name|format
argument_list|(
name|sd
argument_list|,
name|nsInfo
argument_list|)
expr_stmt|;
break|break;
default|default:
comment|// recovery part is common
name|sd
operator|.
name|doRecover
argument_list|(
name|curState
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|sd
operator|.
name|unlock
argument_list|()
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
comment|// add to the storage list. This is inherited from parent class, Storage.
name|addStorageDir
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|dataDirStates
operator|.
name|add
argument_list|(
name|curState
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dataDirs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
comment|// none of the data dirs exist
throw|throw
operator|new
name|IOException
argument_list|(
literal|"All specified directories are not accessible or do not exist."
argument_list|)
throw|;
comment|// 2. Do transitions
comment|// Each storage directory is treated individually.
comment|// During startup some of them can upgrade or roll back
comment|// while others could be up-to-date for the regular startup.
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|getNumStorageDirs
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|doTransition
argument_list|(
name|datanode
argument_list|,
name|getStorageDir
argument_list|(
name|idx
argument_list|)
argument_list|,
name|nsInfo
argument_list|,
name|startOpt
argument_list|)
expr_stmt|;
assert|assert
name|getLayoutVersion
argument_list|()
operator|==
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|:
literal|"Data-node and name-node layout versions must be the same."
assert|;
assert|assert
name|getCTime
argument_list|()
operator|==
name|nsInfo
operator|.
name|getCTime
argument_list|()
operator|:
literal|"Data-node and name-node CTimes must be the same."
assert|;
block|}
comment|// 3. Update all storages. Some of them might have just been formatted.
name|this
operator|.
name|writeAll
argument_list|()
expr_stmt|;
block|}
comment|/**    * Format a block pool slice storage.     * @param dnCurDir DataStorage current directory    * @param nsInfo the name space info    * @throws IOException Signals that an I/O exception has occurred.    */
DECL|method|format (File dnCurDir, NamespaceInfo nsInfo)
name|void
name|format
parameter_list|(
name|File
name|dnCurDir
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|curBpDir
init|=
name|getBpRoot
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|,
name|dnCurDir
argument_list|)
decl_stmt|;
name|StorageDirectory
name|bpSdir
init|=
operator|new
name|StorageDirectory
argument_list|(
name|curBpDir
argument_list|)
decl_stmt|;
name|format
argument_list|(
name|bpSdir
argument_list|,
name|nsInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Format a block pool slice storage.     * @param sd the block pool storage    * @param nsInfo the name space info    * @throws IOException Signals that an I/O exception has occurred.    */
DECL|method|format (StorageDirectory bpSdir, NamespaceInfo nsInfo)
specifier|private
name|void
name|format
parameter_list|(
name|StorageDirectory
name|bpSdir
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Formatting block pool "
operator|+
name|blockpoolID
operator|+
literal|" directory "
operator|+
name|bpSdir
operator|.
name|getCurrentDir
argument_list|()
argument_list|)
expr_stmt|;
name|bpSdir
operator|.
name|clearDirectory
argument_list|()
expr_stmt|;
comment|// create directory
name|this
operator|.
name|layoutVersion
operator|=
name|FSConstants
operator|.
name|LAYOUT_VERSION
expr_stmt|;
name|this
operator|.
name|cTime
operator|=
name|nsInfo
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|namespaceID
operator|=
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
expr_stmt|;
name|this
operator|.
name|blockpoolID
operator|=
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|NodeType
operator|.
name|DATA_NODE
expr_stmt|;
name|bpSdir
operator|.
name|write
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set layoutVersion, namespaceID and blockpoolID into block pool storage    * VERSION file    */
annotation|@
name|Override
DECL|method|setFields (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|setFields
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|props
operator|.
name|setProperty
argument_list|(
literal|"layoutVersion"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|layoutVersion
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"namespaceID"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|namespaceID
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"blockpoolID"
argument_list|,
name|blockpoolID
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"cTime"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|cTime
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Validate and set block pool ID */
DECL|method|setBlockPoolID (File storage, String bpid)
specifier|private
name|void
name|setBlockPoolID
parameter_list|(
name|File
name|storage
parameter_list|,
name|String
name|bpid
parameter_list|)
throws|throws
name|InconsistentFSStateException
block|{
if|if
condition|(
name|bpid
operator|==
literal|null
operator|||
name|bpid
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|storage
argument_list|,
literal|"file "
operator|+
name|STORAGE_FILE_VERSION
operator|+
literal|" is invalid."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|blockpoolID
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
operator|!
name|blockpoolID
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|storage
argument_list|,
literal|"Unexepcted blockpoolID "
operator|+
name|bpid
operator|+
literal|" . Expected "
operator|+
name|blockpoolID
argument_list|)
throw|;
block|}
name|blockpoolID
operator|=
name|bpid
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFields (Properties props, StorageDirectory sd)
specifier|protected
name|void
name|getFields
parameter_list|(
name|Properties
name|props
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|setLayoutVersion
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|setNamespaceID
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|setcTime
argument_list|(
name|props
argument_list|,
name|sd
argument_list|)
expr_stmt|;
name|String
name|sbpid
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"blockpoolID"
argument_list|)
decl_stmt|;
name|setBlockPoolID
argument_list|(
name|sd
operator|.
name|getRoot
argument_list|()
argument_list|,
name|sbpid
argument_list|)
expr_stmt|;
block|}
comment|/**    * Analyze whether a transition of the BP state is required and    * perform it if necessary.    *<br>    * Rollback if previousLV>= LAYOUT_VERSION&& prevCTime<= namenode.cTime.    * Upgrade if this.LV> LAYOUT_VERSION || this.cTime< namenode.cTime Regular    * startup if this.LV = LAYOUT_VERSION&& this.cTime = namenode.cTime    *     * @param dn DataNode to which this storage belongs to    * @param sd storage directory<SD>/current/<bpid>    * @param nsInfo namespace info    * @param startOpt startup option    * @throws IOException    */
DECL|method|doTransition (DataNode datanode, StorageDirectory sd, NamespaceInfo nsInfo, StartupOption startOpt)
specifier|private
name|void
name|doTransition
parameter_list|(
name|DataNode
name|datanode
parameter_list|,
name|StorageDirectory
name|sd
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|StartupOption
name|startOpt
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|startOpt
operator|==
name|StartupOption
operator|.
name|ROLLBACK
condition|)
name|doRollback
argument_list|(
name|sd
argument_list|,
name|nsInfo
argument_list|)
expr_stmt|;
comment|// rollback if applicable
name|sd
operator|.
name|read
argument_list|()
expr_stmt|;
name|checkVersionUpgradable
argument_list|(
name|this
operator|.
name|layoutVersion
argument_list|)
expr_stmt|;
assert|assert
name|this
operator|.
name|layoutVersion
operator|>=
name|FSConstants
operator|.
name|LAYOUT_VERSION
operator|:
literal|"Future version is not allowed"
assert|;
if|if
condition|(
name|getNamespaceID
argument_list|()
operator|!=
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible namespaceIDs in "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|": namenode namespaceID = "
operator|+
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
operator|+
literal|"; datanode namespaceID = "
operator|+
name|getNamespaceID
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|blockpoolID
operator|.
name|equals
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incompatible blockpoolIDs in "
operator|+
name|sd
operator|.
name|getRoot
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|": namenode blockpoolID = "
operator|+
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
operator|+
literal|"; datanode blockpoolID = "
operator|+
name|blockpoolID
argument_list|)
throw|;
block|}
if|if
condition|(
name|this
operator|.
name|layoutVersion
operator|==
name|FSConstants
operator|.
name|LAYOUT_VERSION
operator|&&
name|this
operator|.
name|cTime
operator|==
name|nsInfo
operator|.
name|getCTime
argument_list|()
condition|)
return|return;
comment|// regular startup
comment|// verify necessity of a distributed upgrade
name|UpgradeManagerDatanode
name|um
init|=
name|datanode
operator|.
name|getUpgradeManagerDatanode
argument_list|(
name|nsInfo
operator|.
name|getBlockPoolID
argument_list|()
argument_list|)
decl_stmt|;
name|verifyDistributedUpgradeProgress
argument_list|(
name|um
argument_list|,
name|nsInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|layoutVersion
operator|>
name|FSConstants
operator|.
name|LAYOUT_VERSION
operator|||
name|this
operator|.
name|cTime
operator|<
name|nsInfo
operator|.
name|getCTime
argument_list|()
condition|)
block|{
name|doUpgrade
argument_list|(
name|sd
argument_list|,
name|nsInfo
argument_list|)
expr_stmt|;
comment|// upgrade
return|return;
block|}
comment|// layoutVersion == LAYOUT_VERSION&& this.cTime> nsInfo.cTime
comment|// must shutdown
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Datanode state: LV = "
operator|+
name|this
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|" CTime = "
operator|+
name|this
operator|.
name|getCTime
argument_list|()
operator|+
literal|" is newer than the namespace state: LV = "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|" CTime = "
operator|+
name|nsInfo
operator|.
name|getCTime
argument_list|()
argument_list|)
throw|;
block|}
comment|/**    * Upgrade to any release after 0.22 (0.22 included) release e.g. 0.22 => 0.23    * Upgrade procedure is as follows:    *<ol>    *<li>If<SD>/current/<bpid>/previous exists then delete it</li>    *<li>Rename<SD>/current/<bpid>/current to    *<SD>/current/bpid/current/previous.tmp</li>    *<li>Create new<SD>current/<bpid>/current directory</li>    *<ol>    *<li>Hard links for block files are created from previous.tmp to current</li>    *<li>Save new version file in current directory</li>    *</ol>    *<li>Rename previous.tmp to previous</li></ol>    *     * @param bpSd storage directory<SD>/current/<bpid>    * @param nsInfo Namespace Info from the namenode    * @throws IOException on error    */
DECL|method|doUpgrade (StorageDirectory bpSd, NamespaceInfo nsInfo)
name|void
name|doUpgrade
parameter_list|(
name|StorageDirectory
name|bpSd
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Upgrading is applicable only to release with federation or after
if|if
condition|(
operator|!
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|FEDERATION
argument_list|,
name|layoutVersion
argument_list|)
condition|)
block|{
return|return;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Upgrading block pool storage directory "
operator|+
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|+
literal|".\n   old LV = "
operator|+
name|this
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|"; old CTime = "
operator|+
name|this
operator|.
name|getCTime
argument_list|()
operator|+
literal|".\n   new LV = "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|"; new CTime = "
operator|+
name|nsInfo
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// get<SD>/previous directory
name|String
name|dnRoot
init|=
name|getDataNodeStorageRoot
argument_list|(
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|StorageDirectory
name|dnSdStorage
init|=
operator|new
name|StorageDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|dnRoot
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|dnPrevDir
init|=
name|dnSdStorage
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
comment|// If<SD>/previous directory exists delete it
if|if
condition|(
name|dnPrevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleteDir
argument_list|(
name|dnPrevDir
argument_list|)
expr_stmt|;
block|}
name|File
name|bpCurDir
init|=
name|bpSd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
name|File
name|bpPrevDir
init|=
name|bpSd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
assert|assert
name|bpCurDir
operator|.
name|exists
argument_list|()
operator|:
literal|"BP level current directory must exist."
assert|;
name|cleanupDetachDir
argument_list|(
operator|new
name|File
argument_list|(
name|bpCurDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_DETACHED
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1. Delete<SD>/current/<bpid>/previous dir before upgrading
if|if
condition|(
name|bpPrevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|deleteDir
argument_list|(
name|bpPrevDir
argument_list|)
expr_stmt|;
block|}
name|File
name|bpTmpDir
init|=
name|bpSd
operator|.
name|getPreviousTmp
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|bpTmpDir
operator|.
name|exists
argument_list|()
operator|:
literal|"previous.tmp directory must not exist."
assert|;
comment|// 2. Rename<SD>/curernt/<bpid>/current to<SD>/curernt/<bpid>/previous.tmp
name|rename
argument_list|(
name|bpCurDir
argument_list|,
name|bpTmpDir
argument_list|)
expr_stmt|;
comment|// 3. Create new<SD>/current with block files hardlinks and VERSION
name|linkAllBlocks
argument_list|(
name|bpTmpDir
argument_list|,
name|bpCurDir
argument_list|)
expr_stmt|;
name|this
operator|.
name|layoutVersion
operator|=
name|FSConstants
operator|.
name|LAYOUT_VERSION
expr_stmt|;
assert|assert
name|this
operator|.
name|namespaceID
operator|==
name|nsInfo
operator|.
name|getNamespaceID
argument_list|()
operator|:
literal|"Data-node and name-node layout versions must be the same."
assert|;
name|this
operator|.
name|cTime
operator|=
name|nsInfo
operator|.
name|getCTime
argument_list|()
expr_stmt|;
name|bpSd
operator|.
name|write
argument_list|()
expr_stmt|;
comment|// 4.rename<SD>/curernt/<bpid>/previous.tmp to<SD>/curernt/<bpid>/previous
name|rename
argument_list|(
name|bpTmpDir
argument_list|,
name|bpPrevDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Upgrade of block pool "
operator|+
name|blockpoolID
operator|+
literal|" at "
operator|+
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" is complete."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Cleanup the detachDir.    *     * If the directory is not empty report an error; Otherwise remove the    * directory.    *     * @param detachDir detach directory    * @throws IOException if the directory is not empty or it can not be removed    */
DECL|method|cleanupDetachDir (File detachDir)
specifier|private
name|void
name|cleanupDetachDir
parameter_list|(
name|File
name|detachDir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|LayoutVersion
operator|.
name|supports
argument_list|(
name|Feature
operator|.
name|APPEND_RBW_DIR
argument_list|,
name|layoutVersion
argument_list|)
operator|&&
name|detachDir
operator|.
name|exists
argument_list|()
operator|&&
name|detachDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
name|FileUtil
operator|.
name|list
argument_list|(
name|detachDir
argument_list|)
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Detached directory "
operator|+
name|detachDir
operator|+
literal|" is not empty. Please manually move each file under this "
operator|+
literal|"directory to the finalized directory if the finalized "
operator|+
literal|"directory tree does not have the file."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|detachDir
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot remove directory "
operator|+
name|detachDir
argument_list|)
throw|;
block|}
block|}
block|}
comment|/*    * Roll back to old snapshot at the block pool level    * If previous directory exists:     *<ol>    *<li>Rename<SD>/current/<bpid>/current to removed.tmp</li>    *<li>Rename *<SD>/current/<bpid>/previous to current</li>    *<li>Remove removed.tmp</li>    *</ol>    *     * Do nothing if previous directory does not exist.    * @param bpSd Block pool storage directory at<SD>/current/<bpid>    */
DECL|method|doRollback (StorageDirectory bpSd, NamespaceInfo nsInfo)
name|void
name|doRollback
parameter_list|(
name|StorageDirectory
name|bpSd
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|prevDir
init|=
name|bpSd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
comment|// regular startup if previous dir does not exist
if|if
condition|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
condition|)
return|return;
comment|// read attributes out of the VERSION file of previous directory
name|DataStorage
name|prevInfo
init|=
operator|new
name|DataStorage
argument_list|()
decl_stmt|;
name|StorageDirectory
name|prevSD
init|=
name|prevInfo
operator|.
expr|new
name|StorageDirectory
argument_list|(
name|bpSd
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|prevSD
operator|.
name|read
argument_list|(
name|prevSD
operator|.
name|getPreviousVersionFile
argument_list|()
argument_list|)
expr_stmt|;
comment|// We allow rollback to a state, which is either consistent with
comment|// the namespace state or can be further upgraded to it.
comment|// In another word, we can only roll back when ( storedLV>= software LV)
comment|//&& ( DN.previousCTime<= NN.ctime)
if|if
condition|(
operator|!
operator|(
name|prevInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|>=
name|FSConstants
operator|.
name|LAYOUT_VERSION
operator|&&
name|prevInfo
operator|.
name|getCTime
argument_list|()
operator|<=
name|nsInfo
operator|.
name|getCTime
argument_list|()
operator|)
condition|)
block|{
comment|// cannot rollback
throw|throw
operator|new
name|InconsistentFSStateException
argument_list|(
name|prevSD
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"Cannot rollback to a newer state.\nDatanode previous state: LV = "
operator|+
name|prevInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|" CTime = "
operator|+
name|prevInfo
operator|.
name|getCTime
argument_list|()
operator|+
literal|" is newer than the namespace state: LV = "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|" CTime = "
operator|+
name|nsInfo
operator|.
name|getCTime
argument_list|()
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Rolling back storage directory "
operator|+
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|+
literal|".\n   target LV = "
operator|+
name|nsInfo
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|"; target CTime = "
operator|+
name|nsInfo
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
name|bpSd
operator|.
name|getRemovedTmp
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|tmpDir
operator|.
name|exists
argument_list|()
operator|:
literal|"removed.tmp directory must not exist."
assert|;
comment|// 1. rename current to tmp
name|File
name|curDir
init|=
name|bpSd
operator|.
name|getCurrentDir
argument_list|()
decl_stmt|;
assert|assert
name|curDir
operator|.
name|exists
argument_list|()
operator|:
literal|"Current directory must exist."
assert|;
name|rename
argument_list|(
name|curDir
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
comment|// 2. rename previous to current
name|rename
argument_list|(
name|prevDir
argument_list|,
name|curDir
argument_list|)
expr_stmt|;
comment|// 3. delete removed.tmp dir
name|deleteDir
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Rollback of "
operator|+
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|+
literal|" is complete."
argument_list|)
expr_stmt|;
block|}
comment|/*    * Finalize the block pool storage by deleting<BP>/previous directory    * that holds the snapshot.    */
DECL|method|doFinalize (File dnCurDir)
name|void
name|doFinalize
parameter_list|(
name|File
name|dnCurDir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|bpRoot
init|=
name|getBpRoot
argument_list|(
name|blockpoolID
argument_list|,
name|dnCurDir
argument_list|)
decl_stmt|;
name|StorageDirectory
name|bpSd
init|=
operator|new
name|StorageDirectory
argument_list|(
name|bpRoot
argument_list|)
decl_stmt|;
comment|// block pool level previous directory
name|File
name|prevDir
init|=
name|bpSd
operator|.
name|getPreviousDir
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|prevDir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
comment|// already finalized
block|}
specifier|final
name|String
name|dataDirPath
init|=
name|bpSd
operator|.
name|getRoot
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalizing upgrade for storage directory "
operator|+
name|dataDirPath
operator|+
literal|".\n   cur LV = "
operator|+
name|this
operator|.
name|getLayoutVersion
argument_list|()
operator|+
literal|"; cur CTime = "
operator|+
name|this
operator|.
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|bpSd
operator|.
name|getCurrentDir
argument_list|()
operator|.
name|exists
argument_list|()
operator|:
literal|"Current directory must exist."
assert|;
comment|// rename previous to finalized.tmp
specifier|final
name|File
name|tmpDir
init|=
name|bpSd
operator|.
name|getFinalizedTmp
argument_list|()
decl_stmt|;
name|rename
argument_list|(
name|prevDir
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
comment|// delete finalized.tmp dir in a separate thread
operator|new
name|Daemon
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|deleteDir
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Finalize upgrade for "
operator|+
name|dataDirPath
operator|+
literal|" failed."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finalize upgrade for "
operator|+
name|dataDirPath
operator|+
literal|" is complete."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Finalize "
operator|+
name|dataDirPath
return|;
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Hardlink all finalized and RBW blocks in fromDir to toDir    *     * @param fromDir directory where the snapshot is stored    * @param toDir the current data directory    * @throws IOException if error occurs during hardlink    */
DECL|method|linkAllBlocks (File fromDir, File toDir)
specifier|private
name|void
name|linkAllBlocks
parameter_list|(
name|File
name|fromDir
parameter_list|,
name|File
name|toDir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// do the link
name|int
name|diskLayoutVersion
init|=
name|this
operator|.
name|getLayoutVersion
argument_list|()
decl_stmt|;
comment|// hardlink finalized blocks in tmpDir
name|HardLink
name|hardLink
init|=
operator|new
name|HardLink
argument_list|()
decl_stmt|;
name|DataStorage
operator|.
name|linkBlocks
argument_list|(
operator|new
name|File
argument_list|(
name|fromDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|toDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_FINALIZED
argument_list|)
argument_list|,
name|diskLayoutVersion
argument_list|,
name|hardLink
argument_list|)
expr_stmt|;
name|DataStorage
operator|.
name|linkBlocks
argument_list|(
operator|new
name|File
argument_list|(
name|fromDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|toDir
argument_list|,
name|DataStorage
operator|.
name|STORAGE_DIR_RBW
argument_list|)
argument_list|,
name|diskLayoutVersion
argument_list|,
name|hardLink
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|hardLink
operator|.
name|linkStats
operator|.
name|report
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDistributedUpgradeProgress (UpgradeManagerDatanode um, NamespaceInfo nsInfo)
specifier|private
name|void
name|verifyDistributedUpgradeProgress
parameter_list|(
name|UpgradeManagerDatanode
name|um
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|um
operator|!=
literal|null
operator|:
literal|"DataNode.upgradeManager is null."
assert|;
name|um
operator|.
name|setUpgradeState
argument_list|(
literal|false
argument_list|,
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|um
operator|.
name|initializeUpgrade
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * gets the data node storage directory based on block pool storage    *     * @param bpRoot    * @return    */
DECL|method|getDataNodeStorageRoot (String bpRoot)
specifier|private
specifier|static
name|String
name|getDataNodeStorageRoot
parameter_list|(
name|String
name|bpRoot
parameter_list|)
block|{
name|Matcher
name|matcher
init|=
name|BLOCK_POOL_PATH_PATTERN
operator|.
name|matcher
argument_list|(
name|bpRoot
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// return the data node root directory
return|return
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
return|;
block|}
return|return
name|bpRoot
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|";bpid="
operator|+
name|blockpoolID
return|;
block|}
comment|/**    * Get a block pool storage root based on data node storage root    * @param bpID block pool ID    * @param dnCurDir data node storage root directory    * @return root directory for block pool storage    */
DECL|method|getBpRoot (String bpID, File dnCurDir)
specifier|public
specifier|static
name|File
name|getBpRoot
parameter_list|(
name|String
name|bpID
parameter_list|,
name|File
name|dnCurDir
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|dnCurDir
argument_list|,
name|bpID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isPreUpgradableLayout (StorageDirectory sd)
specifier|public
name|boolean
name|isPreUpgradableLayout
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

