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
name|net
operator|.
name|URI
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
comment|/**  * This class is used for provided replicas that are finalized.  */
end_comment

begin_class
DECL|class|FinalizedProvidedReplica
specifier|public
class|class
name|FinalizedProvidedReplica
extends|extends
name|ProvidedReplica
block|{
DECL|method|FinalizedProvidedReplica (long blockId, URI fileURI, long fileOffset, long blockLen, long genStamp, FsVolumeSpi volume, Configuration conf)
specifier|public
name|FinalizedProvidedReplica
parameter_list|(
name|long
name|blockId
parameter_list|,
name|URI
name|fileURI
parameter_list|,
name|long
name|fileOffset
parameter_list|,
name|long
name|blockLen
parameter_list|,
name|long
name|genStamp
parameter_list|,
name|FsVolumeSpi
name|volume
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|blockId
argument_list|,
name|fileURI
argument_list|,
name|fileOffset
argument_list|,
name|blockLen
argument_list|,
name|genStamp
argument_list|,
name|volume
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getState ()
specifier|public
name|ReplicaState
name|getState
parameter_list|()
block|{
return|return
name|ReplicaState
operator|.
name|FINALIZED
return|;
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
DECL|method|getVisibleLength ()
specifier|public
name|long
name|getVisibleLength
parameter_list|()
block|{
return|return
name|getNumBytes
argument_list|()
return|;
comment|//all bytes are visible
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
literal|" does not support setRecoveryID"
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

