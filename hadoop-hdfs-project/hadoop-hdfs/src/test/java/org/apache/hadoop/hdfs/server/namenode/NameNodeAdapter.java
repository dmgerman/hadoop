begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|UnresolvedLinkException
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
name|PermissionStatus
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
name|DatanodeID
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
name|HdfsFileStatus
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
name|LocatedBlocks
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSecretManager
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
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|namenode
operator|.
name|FSEditLogOp
operator|.
name|MkdirOp
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
operator|.
name|SafeModeInfo
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
name|LeaseManager
operator|.
name|Lease
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
name|DatanodeRegistration
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
name|HeartbeatResponse
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
name|ipc
operator|.
name|Server
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
name|ipc
operator|.
name|StandbyException
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * This is a utility class to expose NameNode functionality for unit tests.  */
end_comment

begin_class
DECL|class|NameNodeAdapter
specifier|public
class|class
name|NameNodeAdapter
block|{
comment|/**    * Get the namesystem from the namenode    */
DECL|method|getNamesystem (NameNode namenode)
specifier|public
specifier|static
name|FSNamesystem
name|getNamesystem
parameter_list|(
name|NameNode
name|namenode
parameter_list|)
block|{
return|return
name|namenode
operator|.
name|getNamesystem
argument_list|()
return|;
block|}
comment|/**    * Get block locations within the specified range.    */
DECL|method|getBlockLocations (NameNode namenode, String src, long offset, long length)
specifier|public
specifier|static
name|LocatedBlocks
name|getBlockLocations
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|String
name|src
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockLocations
argument_list|(
name|src
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getFileInfo (NameNode namenode, String src, boolean resolveLink)
specifier|public
specifier|static
name|HdfsFileStatus
name|getFileInfo
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|String
name|src
parameter_list|,
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|UnresolvedLinkException
throws|,
name|StandbyException
block|{
return|return
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|src
argument_list|,
name|resolveLink
argument_list|)
return|;
block|}
DECL|method|mkdirs (NameNode namenode, String src, PermissionStatus permissions, boolean createParent)
specifier|public
specifier|static
name|boolean
name|mkdirs
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|String
name|src
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|IOException
block|{
return|return
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|mkdirs
argument_list|(
name|src
argument_list|,
name|permissions
argument_list|,
name|createParent
argument_list|)
return|;
block|}
DECL|method|saveNamespace (NameNode namenode)
specifier|public
specifier|static
name|void
name|saveNamespace
parameter_list|(
name|NameNode
name|namenode
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|IOException
block|{
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|saveNamespace
argument_list|()
expr_stmt|;
block|}
DECL|method|enterSafeMode (NameNode namenode, boolean resourcesLow)
specifier|public
specifier|static
name|void
name|enterSafeMode
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|boolean
name|resourcesLow
parameter_list|)
throws|throws
name|IOException
block|{
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|enterSafeMode
argument_list|(
name|resourcesLow
argument_list|)
expr_stmt|;
block|}
DECL|method|leaveSafeMode (NameNode namenode, boolean checkForUpgrades)
specifier|public
specifier|static
name|void
name|leaveSafeMode
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|boolean
name|checkForUpgrades
parameter_list|)
throws|throws
name|SafeModeException
block|{
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|leaveSafeMode
argument_list|(
name|checkForUpgrades
argument_list|)
expr_stmt|;
block|}
DECL|method|abortEditLogs (NameNode nn)
specifier|public
specifier|static
name|void
name|abortEditLogs
parameter_list|(
name|NameNode
name|nn
parameter_list|)
block|{
name|FSEditLog
name|el
init|=
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
decl_stmt|;
name|el
operator|.
name|abortCurrentLogSegment
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the internal RPC server instance.    * @return rpc server    */
DECL|method|getRpcServer (NameNode namenode)
specifier|public
specifier|static
name|Server
name|getRpcServer
parameter_list|(
name|NameNode
name|namenode
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NameNodeRpcServer
operator|)
name|namenode
operator|.
name|getRpcServer
argument_list|()
operator|)
operator|.
name|clientRpcServer
return|;
block|}
DECL|method|getDtSecretManager ( final FSNamesystem ns)
specifier|public
specifier|static
name|DelegationTokenSecretManager
name|getDtSecretManager
parameter_list|(
specifier|final
name|FSNamesystem
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|.
name|getDelegationTokenSecretManager
argument_list|()
return|;
block|}
DECL|method|sendHeartBeat (DatanodeRegistration nodeReg, DatanodeDescriptor dd, FSNamesystem namesystem)
specifier|public
specifier|static
name|HeartbeatResponse
name|sendHeartBeat
parameter_list|(
name|DatanodeRegistration
name|nodeReg
parameter_list|,
name|DatanodeDescriptor
name|dd
parameter_list|,
name|FSNamesystem
name|namesystem
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|namesystem
operator|.
name|handleHeartbeat
argument_list|(
name|nodeReg
argument_list|,
name|dd
operator|.
name|getCapacity
argument_list|()
argument_list|,
name|dd
operator|.
name|getDfsUsed
argument_list|()
argument_list|,
name|dd
operator|.
name|getRemaining
argument_list|()
argument_list|,
name|dd
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|setReplication (final FSNamesystem ns, final String src, final short replication)
specifier|public
specifier|static
name|boolean
name|setReplication
parameter_list|(
specifier|final
name|FSNamesystem
name|ns
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ns
operator|.
name|setReplication
argument_list|(
name|src
argument_list|,
name|replication
argument_list|)
return|;
block|}
DECL|method|getLeaseManager (final FSNamesystem ns)
specifier|public
specifier|static
name|LeaseManager
name|getLeaseManager
parameter_list|(
specifier|final
name|FSNamesystem
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|.
name|leaseManager
return|;
block|}
comment|/** Set the softLimit and hardLimit of client lease periods. */
DECL|method|setLeasePeriod (final FSNamesystem namesystem, long soft, long hard)
specifier|public
specifier|static
name|void
name|setLeasePeriod
parameter_list|(
specifier|final
name|FSNamesystem
name|namesystem
parameter_list|,
name|long
name|soft
parameter_list|,
name|long
name|hard
parameter_list|)
block|{
name|getLeaseManager
argument_list|(
name|namesystem
argument_list|)
operator|.
name|setLeasePeriod
argument_list|(
name|soft
argument_list|,
name|hard
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|leaseManager
operator|.
name|triggerMonitorCheckNow
argument_list|()
expr_stmt|;
block|}
DECL|method|getLeaseHolderForPath (NameNode namenode, String path)
specifier|public
specifier|static
name|String
name|getLeaseHolderForPath
parameter_list|(
name|NameNode
name|namenode
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Lease
name|l
init|=
name|namenode
operator|.
name|getNamesystem
argument_list|()
operator|.
name|leaseManager
operator|.
name|getLeaseByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|l
operator|==
literal|null
condition|?
literal|null
else|:
name|l
operator|.
name|getHolder
argument_list|()
return|;
block|}
comment|/**    * @return the timestamp of the last renewal of the given lease,    *   or -1 in the case that the lease doesn't exist.    */
DECL|method|getLeaseRenewalTime (NameNode nn, String path)
specifier|public
specifier|static
name|long
name|getLeaseRenewalTime
parameter_list|(
name|NameNode
name|nn
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|LeaseManager
name|lm
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|leaseManager
decl_stmt|;
name|Lease
name|l
init|=
name|lm
operator|.
name|getLeaseByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|l
operator|.
name|getLastUpdate
argument_list|()
return|;
block|}
comment|/**    * Return the datanode descriptor for the given datanode.    */
DECL|method|getDatanode (final FSNamesystem ns, DatanodeID id)
specifier|public
specifier|static
name|DatanodeDescriptor
name|getDatanode
parameter_list|(
specifier|final
name|FSNamesystem
name|ns
parameter_list|,
name|DatanodeID
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|ns
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|ns
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getDatanodeManager
argument_list|()
operator|.
name|getDatanode
argument_list|(
name|id
argument_list|)
return|;
block|}
finally|finally
block|{
name|ns
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Return the FSNamesystem stats    */
DECL|method|getStats (final FSNamesystem fsn)
specifier|public
specifier|static
name|long
index|[]
name|getStats
parameter_list|(
specifier|final
name|FSNamesystem
name|fsn
parameter_list|)
block|{
return|return
name|fsn
operator|.
name|getStats
argument_list|()
return|;
block|}
DECL|method|spyOnFsLock (FSNamesystem fsn)
specifier|public
specifier|static
name|ReentrantReadWriteLock
name|spyOnFsLock
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|)
block|{
name|ReentrantReadWriteLock
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fsn
operator|.
name|getFsLockForTests
argument_list|()
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|setFsLockForTests
argument_list|(
name|spy
argument_list|)
expr_stmt|;
return|return
name|spy
return|;
block|}
DECL|method|spyOnFsImage (NameNode nn1)
specifier|public
specifier|static
name|FSImage
name|spyOnFsImage
parameter_list|(
name|NameNode
name|nn1
parameter_list|)
block|{
name|FSImage
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|dir
operator|.
name|fsImage
argument_list|)
decl_stmt|;
name|nn1
operator|.
name|getNamesystem
argument_list|()
operator|.
name|dir
operator|.
name|fsImage
operator|=
name|spy
expr_stmt|;
return|return
name|spy
return|;
block|}
DECL|method|getMkdirOpPath (FSEditLogOp op)
specifier|public
specifier|static
name|String
name|getMkdirOpPath
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
block|{
if|if
condition|(
name|op
operator|.
name|opCode
operator|==
name|FSEditLogOpCodes
operator|.
name|OP_MKDIR
condition|)
block|{
return|return
operator|(
operator|(
name|MkdirOp
operator|)
name|op
operator|)
operator|.
name|path
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**    * @return the number of blocks marked safe by safemode, or -1    * if safemode is not running.    */
DECL|method|getSafeModeSafeBlocks (NameNode nn)
specifier|public
specifier|static
name|int
name|getSafeModeSafeBlocks
parameter_list|(
name|NameNode
name|nn
parameter_list|)
block|{
name|SafeModeInfo
name|smi
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafeModeInfoForTests
argument_list|()
decl_stmt|;
if|if
condition|(
name|smi
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|smi
operator|.
name|blockSafe
return|;
block|}
comment|/**    * @return true if safemode is not running, or if safemode has already    * initialized the replication queues    */
DECL|method|safeModeInitializedReplQueues (NameNode nn)
specifier|public
specifier|static
name|boolean
name|safeModeInitializedReplQueues
parameter_list|(
name|NameNode
name|nn
parameter_list|)
block|{
name|SafeModeInfo
name|smi
init|=
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getSafeModeInfoForTests
argument_list|()
decl_stmt|;
if|if
condition|(
name|smi
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|smi
operator|.
name|initializedReplQueues
return|;
block|}
DECL|method|getInProgressEditsFile (StorageDirectory sd, long startTxId)
specifier|public
specifier|static
name|File
name|getInProgressEditsFile
parameter_list|(
name|StorageDirectory
name|sd
parameter_list|,
name|long
name|startTxId
parameter_list|)
block|{
return|return
name|NNStorage
operator|.
name|getInProgressEditsFile
argument_list|(
name|sd
argument_list|,
name|startTxId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

