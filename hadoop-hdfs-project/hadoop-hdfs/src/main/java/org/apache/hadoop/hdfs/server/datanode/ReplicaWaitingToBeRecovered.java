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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
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
name|fsdataset
operator|.
name|FsVolumeSpi
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
name|ReplicaRecoveryInfo
import|;
end_import

begin_comment
comment|/**  * This class represents a replica that is waiting to be recovered.  * After a datanode restart, any replica in "rbw" directory is loaded  * as a replica waiting to be recovered.  * A replica waiting to be recovered does not provision read nor  * participates in any pipeline recovery. It will become outdated if its  * client continues to write or be recovered as a result of  * lease recovery.  */
end_comment

begin_class
DECL|class|ReplicaWaitingToBeRecovered
specifier|public
class|class
name|ReplicaWaitingToBeRecovered
extends|extends
name|LocalReplica
block|{
comment|/**    * Constructor    * @param blockId block id    * @param len replica length    * @param genStamp replica generation stamp    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    */
DECL|method|ReplicaWaitingToBeRecovered (long blockId, long len, long genStamp, FsVolumeSpi vol, File dir)
specifier|public
name|ReplicaWaitingToBeRecovered
parameter_list|(
name|long
name|blockId
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|blockId
argument_list|,
name|len
argument_list|,
name|genStamp
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    * @param block a block    * @param vol volume where replica is located    * @param dir directory path where block and meta files are located    */
DECL|method|ReplicaWaitingToBeRecovered (Block block, FsVolumeSpi vol, File dir)
specifier|public
name|ReplicaWaitingToBeRecovered
parameter_list|(
name|Block
name|block
parameter_list|,
name|FsVolumeSpi
name|vol
parameter_list|,
name|File
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|,
name|vol
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Copy constructor.    * @param from where to copy from    */
DECL|method|ReplicaWaitingToBeRecovered (ReplicaWaitingToBeRecovered from)
specifier|public
name|ReplicaWaitingToBeRecovered
parameter_list|(
name|ReplicaWaitingToBeRecovered
name|from
parameter_list|)
block|{
name|super
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|//ReplicaInfo
DECL|method|getState ()
specifier|public
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|ReplicaState
operator|.
name|RWR
return|;
block|}
annotation|@
name|Override
comment|//ReplicaInfo
DECL|method|getVisibleLength ()
specifier|public
name|long
name|getVisibleLength
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
comment|//no bytes are visible
block|}
annotation|@
name|Override
DECL|method|getBytesOnDisk ()
specifier|public
name|long
name|getBytesOnDisk
parameter_list|()
block|{
return|return
name|getNumBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
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
return|;
block|}
annotation|@
name|Override
DECL|method|getOriginalReplica ()
specifier|public
name|ReplicaInfo
name|getOriginalReplica
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Replica of type "
operator|+
name|getState
argument_list|()
operator|+
literal|" does not support getOriginalReplica"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getRecoveryID ()
specifier|public
name|long
name|getRecoveryID
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Replica of type "
operator|+
name|getState
argument_list|()
operator|+
literal|" does not support getRecoveryID"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|setRecoveryID (long recoveryId)
specifier|public
name|void
name|setRecoveryID
parameter_list|(
name|long
name|recoveryId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Replica of type "
operator|+
name|getState
argument_list|()
operator|+
literal|" does not support getRecoveryID"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createInfo ()
specifier|public
name|ReplicaRecoveryInfo
name|createInfo
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Replica of type "
operator|+
name|getState
argument_list|()
operator|+
literal|" does not support createInfo"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

