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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
import|;
end_import

begin_comment
comment|/**  * This class represents a batch of blocks under one trackId which needs to move  * its storage locations to satisfy the storage policy.  */
end_comment

begin_class
DECL|class|BlockStorageMovementInfosBatch
specifier|public
class|class
name|BlockStorageMovementInfosBatch
block|{
DECL|field|trackID
specifier|private
name|long
name|trackID
decl_stmt|;
DECL|field|blockMovingInfos
specifier|private
name|List
argument_list|<
name|BlockMovingInfo
argument_list|>
name|blockMovingInfos
decl_stmt|;
comment|/**    * Constructor to create the block storage movement infos batch.    *    * @param trackID    *          - unique identifier which will be used for tracking the given set    *          of blocks movement.    * @param blockMovingInfos    *          - list of block to storage infos.    */
DECL|method|BlockStorageMovementInfosBatch (long trackID, List<BlockMovingInfo> blockMovingInfos)
specifier|public
name|BlockStorageMovementInfosBatch
parameter_list|(
name|long
name|trackID
parameter_list|,
name|List
argument_list|<
name|BlockMovingInfo
argument_list|>
name|blockMovingInfos
parameter_list|)
block|{
name|this
operator|.
name|trackID
operator|=
name|trackID
expr_stmt|;
name|this
operator|.
name|blockMovingInfos
operator|=
name|blockMovingInfos
expr_stmt|;
block|}
DECL|method|getTrackID ()
specifier|public
name|long
name|getTrackID
parameter_list|()
block|{
return|return
name|trackID
return|;
block|}
DECL|method|getBlockMovingInfo ()
specifier|public
name|List
argument_list|<
name|BlockMovingInfo
argument_list|>
name|getBlockMovingInfo
parameter_list|()
block|{
return|return
name|blockMovingInfos
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
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"BlockStorageMovementInfosBatch(\n  "
argument_list|)
operator|.
name|append
argument_list|(
literal|"TrackID: "
argument_list|)
operator|.
name|append
argument_list|(
name|trackID
argument_list|)
operator|.
name|append
argument_list|(
literal|"  BlockMovingInfos: "
argument_list|)
operator|.
name|append
argument_list|(
name|blockMovingInfos
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

