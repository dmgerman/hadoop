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
name|fs
operator|.
name|StorageType
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
name|DatanodeInfo
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
name|DatanodeInfo
operator|.
name|DatanodeInfoBuilder
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
name|ExtendedBlock
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
name|LocatedBlock
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
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * ReportBadBlockAction is an instruction issued by {{BPOfferService}} to  * {{BPServiceActor}} to report bad block to namenode  *  */
end_comment

begin_class
DECL|class|ReportBadBlockAction
specifier|public
class|class
name|ReportBadBlockAction
implements|implements
name|BPServiceActorAction
block|{
DECL|field|block
specifier|private
specifier|final
name|ExtendedBlock
name|block
decl_stmt|;
DECL|field|storageUuid
specifier|private
specifier|final
name|String
name|storageUuid
decl_stmt|;
DECL|field|storageType
specifier|private
specifier|final
name|StorageType
name|storageType
decl_stmt|;
DECL|method|ReportBadBlockAction (ExtendedBlock block, String storageUuid, StorageType storageType)
specifier|public
name|ReportBadBlockAction
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|storageUuid
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|storageUuid
operator|=
name|storageUuid
expr_stmt|;
name|this
operator|.
name|storageType
operator|=
name|storageType
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reportTo (DatanodeProtocolClientSideTranslatorPB bpNamenode, DatanodeRegistration bpRegistration)
specifier|public
name|void
name|reportTo
parameter_list|(
name|DatanodeProtocolClientSideTranslatorPB
name|bpNamenode
parameter_list|,
name|DatanodeRegistration
name|bpRegistration
parameter_list|)
throws|throws
name|BPServiceActorActionException
block|{
if|if
condition|(
name|bpRegistration
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|DatanodeInfo
index|[]
name|dnArr
init|=
block|{
operator|new
name|DatanodeInfoBuilder
argument_list|()
operator|.
name|setNodeID
argument_list|(
name|bpRegistration
argument_list|)
operator|.
name|build
argument_list|()
block|}
decl_stmt|;
name|String
index|[]
name|uuids
init|=
block|{
name|storageUuid
block|}
decl_stmt|;
name|StorageType
index|[]
name|types
init|=
block|{
name|storageType
block|}
decl_stmt|;
name|LocatedBlock
index|[]
name|locatedBlock
init|=
block|{
operator|new
name|LocatedBlock
argument_list|(
name|block
argument_list|,
name|dnArr
argument_list|,
name|uuids
argument_list|,
name|types
argument_list|)
block|}
decl_stmt|;
try|try
block|{
name|bpNamenode
operator|.
name|reportBadBlocks
argument_list|(
name|locatedBlock
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RemoteException
name|re
parameter_list|)
block|{
name|DataNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"reportBadBlock encountered RemoteException for "
operator|+
literal|"block:  "
operator|+
name|block
argument_list|,
name|re
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BPServiceActorActionException
argument_list|(
literal|"Failed to report bad block "
operator|+
name|block
operator|+
literal|" to namenode."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|block
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|block
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|storageType
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|storageType
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|storageUuid
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|storageUuid
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
operator|!
operator|(
name|obj
operator|instanceof
name|ReportBadBlockAction
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReportBadBlockAction
name|other
init|=
operator|(
name|ReportBadBlockAction
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|block
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|block
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|block
operator|.
name|equals
argument_list|(
name|other
operator|.
name|block
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|storageType
operator|!=
name|other
operator|.
name|storageType
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|storageUuid
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|storageUuid
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|storageUuid
operator|.
name|equals
argument_list|(
name|other
operator|.
name|storageUuid
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

