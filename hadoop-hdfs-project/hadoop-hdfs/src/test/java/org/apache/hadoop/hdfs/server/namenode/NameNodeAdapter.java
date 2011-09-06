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
name|IOException
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
name|protocol
operator|.
name|DatanodeCommand
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
name|ipc
operator|.
name|Server
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
argument_list|)
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
name|DatanodeCommand
index|[]
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
name|lmthread
operator|.
name|interrupt
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
return|return
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
operator|.
name|getHolder
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
block|}
end_class

end_unit

