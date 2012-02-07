begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
package|;
end_package

begin_comment
comment|/**  * Report of block received and deleted per Datanode  * storage.  */
end_comment

begin_class
DECL|class|StorageReceivedDeletedBlocks
specifier|public
class|class
name|StorageReceivedDeletedBlocks
block|{
DECL|field|storageID
specifier|private
specifier|final
name|String
name|storageID
decl_stmt|;
DECL|field|blocks
specifier|private
specifier|final
name|ReceivedDeletedBlockInfo
index|[]
name|blocks
decl_stmt|;
DECL|method|getStorageID ()
specifier|public
name|String
name|getStorageID
parameter_list|()
block|{
return|return
name|storageID
return|;
block|}
DECL|method|getBlocks ()
specifier|public
name|ReceivedDeletedBlockInfo
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|StorageReceivedDeletedBlocks (final String storageID, final ReceivedDeletedBlockInfo[] blocks)
specifier|public
name|StorageReceivedDeletedBlocks
parameter_list|(
specifier|final
name|String
name|storageID
parameter_list|,
specifier|final
name|ReceivedDeletedBlockInfo
index|[]
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|storageID
operator|=
name|storageID
expr_stmt|;
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
block|}
end_class

end_unit

