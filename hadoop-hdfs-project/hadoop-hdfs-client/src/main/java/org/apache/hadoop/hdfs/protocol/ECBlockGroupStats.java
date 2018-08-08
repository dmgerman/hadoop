begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Get statistics pertaining to blocks of type {@link BlockType#STRIPED}  * in the filesystem.  *<p>  * @see ClientProtocol#getECBlockGroupStats()  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ECBlockGroupStats
specifier|public
specifier|final
class|class
name|ECBlockGroupStats
block|{
DECL|field|lowRedundancyBlockGroups
specifier|private
specifier|final
name|long
name|lowRedundancyBlockGroups
decl_stmt|;
DECL|field|corruptBlockGroups
specifier|private
specifier|final
name|long
name|corruptBlockGroups
decl_stmt|;
DECL|field|missingBlockGroups
specifier|private
specifier|final
name|long
name|missingBlockGroups
decl_stmt|;
DECL|field|bytesInFutureBlockGroups
specifier|private
specifier|final
name|long
name|bytesInFutureBlockGroups
decl_stmt|;
DECL|field|pendingDeletionBlocks
specifier|private
specifier|final
name|long
name|pendingDeletionBlocks
decl_stmt|;
DECL|field|highestPriorityLowRedundancyBlocks
specifier|private
specifier|final
name|Long
name|highestPriorityLowRedundancyBlocks
decl_stmt|;
DECL|method|ECBlockGroupStats (long lowRedundancyBlockGroups, long corruptBlockGroups, long missingBlockGroups, long bytesInFutureBlockGroups, long pendingDeletionBlocks)
specifier|public
name|ECBlockGroupStats
parameter_list|(
name|long
name|lowRedundancyBlockGroups
parameter_list|,
name|long
name|corruptBlockGroups
parameter_list|,
name|long
name|missingBlockGroups
parameter_list|,
name|long
name|bytesInFutureBlockGroups
parameter_list|,
name|long
name|pendingDeletionBlocks
parameter_list|)
block|{
name|this
argument_list|(
name|lowRedundancyBlockGroups
argument_list|,
name|corruptBlockGroups
argument_list|,
name|missingBlockGroups
argument_list|,
name|bytesInFutureBlockGroups
argument_list|,
name|pendingDeletionBlocks
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ECBlockGroupStats (long lowRedundancyBlockGroups, long corruptBlockGroups, long missingBlockGroups, long bytesInFutureBlockGroups, long pendingDeletionBlocks, Long highestPriorityLowRedundancyBlocks)
specifier|public
name|ECBlockGroupStats
parameter_list|(
name|long
name|lowRedundancyBlockGroups
parameter_list|,
name|long
name|corruptBlockGroups
parameter_list|,
name|long
name|missingBlockGroups
parameter_list|,
name|long
name|bytesInFutureBlockGroups
parameter_list|,
name|long
name|pendingDeletionBlocks
parameter_list|,
name|Long
name|highestPriorityLowRedundancyBlocks
parameter_list|)
block|{
name|this
operator|.
name|lowRedundancyBlockGroups
operator|=
name|lowRedundancyBlockGroups
expr_stmt|;
name|this
operator|.
name|corruptBlockGroups
operator|=
name|corruptBlockGroups
expr_stmt|;
name|this
operator|.
name|missingBlockGroups
operator|=
name|missingBlockGroups
expr_stmt|;
name|this
operator|.
name|bytesInFutureBlockGroups
operator|=
name|bytesInFutureBlockGroups
expr_stmt|;
name|this
operator|.
name|pendingDeletionBlocks
operator|=
name|pendingDeletionBlocks
expr_stmt|;
name|this
operator|.
name|highestPriorityLowRedundancyBlocks
operator|=
name|highestPriorityLowRedundancyBlocks
expr_stmt|;
block|}
DECL|method|getBytesInFutureBlockGroups ()
specifier|public
name|long
name|getBytesInFutureBlockGroups
parameter_list|()
block|{
return|return
name|bytesInFutureBlockGroups
return|;
block|}
DECL|method|getCorruptBlockGroups ()
specifier|public
name|long
name|getCorruptBlockGroups
parameter_list|()
block|{
return|return
name|corruptBlockGroups
return|;
block|}
DECL|method|getLowRedundancyBlockGroups ()
specifier|public
name|long
name|getLowRedundancyBlockGroups
parameter_list|()
block|{
return|return
name|lowRedundancyBlockGroups
return|;
block|}
DECL|method|getMissingBlockGroups ()
specifier|public
name|long
name|getMissingBlockGroups
parameter_list|()
block|{
return|return
name|missingBlockGroups
return|;
block|}
DECL|method|getPendingDeletionBlocks ()
specifier|public
name|long
name|getPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|pendingDeletionBlocks
return|;
block|}
DECL|method|hasHighestPriorityLowRedundancyBlocks ()
specifier|public
name|boolean
name|hasHighestPriorityLowRedundancyBlocks
parameter_list|()
block|{
return|return
name|getHighestPriorityLowRedundancyBlocks
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|getHighestPriorityLowRedundancyBlocks ()
specifier|public
name|Long
name|getHighestPriorityLowRedundancyBlocks
parameter_list|()
block|{
return|return
name|highestPriorityLowRedundancyBlocks
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
name|StringBuilder
name|statsBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|statsBuilder
operator|.
name|append
argument_list|(
literal|"ECBlockGroupStats=["
argument_list|)
operator|.
name|append
argument_list|(
literal|"LowRedundancyBlockGroups="
argument_list|)
operator|.
name|append
argument_list|(
name|getLowRedundancyBlockGroups
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", CorruptBlockGroups="
argument_list|)
operator|.
name|append
argument_list|(
name|getCorruptBlockGroups
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", MissingBlockGroups="
argument_list|)
operator|.
name|append
argument_list|(
name|getMissingBlockGroups
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", BytesInFutureBlockGroups="
argument_list|)
operator|.
name|append
argument_list|(
name|getBytesInFutureBlockGroups
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", PendingDeletionBlocks="
argument_list|)
operator|.
name|append
argument_list|(
name|getPendingDeletionBlocks
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasHighestPriorityLowRedundancyBlocks
argument_list|()
condition|)
block|{
name|statsBuilder
operator|.
name|append
argument_list|(
literal|", HighestPriorityLowRedundancyBlocks="
argument_list|)
operator|.
name|append
argument_list|(
name|getHighestPriorityLowRedundancyBlocks
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|statsBuilder
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|statsBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

