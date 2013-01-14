begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol.datatransfer
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
operator|.
name|datatransfer
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
name|protocolPB
operator|.
name|PBHelper
operator|.
name|vintPrefixed
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|classification
operator|.
name|InterfaceStability
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
name|proto
operator|.
name|DataTransferProtos
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|PipelineAckProto
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|TextFormat
import|;
end_import

begin_comment
comment|/** Pipeline Acknowledgment **/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|PipelineAck
specifier|public
class|class
name|PipelineAck
block|{
DECL|field|proto
name|PipelineAckProto
name|proto
decl_stmt|;
DECL|field|UNKOWN_SEQNO
specifier|public
specifier|final
specifier|static
name|long
name|UNKOWN_SEQNO
init|=
operator|-
literal|2
decl_stmt|;
comment|/** default constructor **/
DECL|method|PipelineAck ()
specifier|public
name|PipelineAck
parameter_list|()
block|{   }
comment|/**    * Constructor assuming no next DN in pipeline    * @param seqno sequence number    * @param replies an array of replies    */
DECL|method|PipelineAck (long seqno, Status[] replies)
specifier|public
name|PipelineAck
parameter_list|(
name|long
name|seqno
parameter_list|,
name|Status
index|[]
name|replies
parameter_list|)
block|{
name|this
argument_list|(
name|seqno
argument_list|,
name|replies
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor    * @param seqno sequence number    * @param replies an array of replies    * @param downstreamAckTimeNanos ack RTT in nanoseconds, 0 if no next DN in pipeline    */
DECL|method|PipelineAck (long seqno, Status[] replies, long downstreamAckTimeNanos)
specifier|public
name|PipelineAck
parameter_list|(
name|long
name|seqno
parameter_list|,
name|Status
index|[]
name|replies
parameter_list|,
name|long
name|downstreamAckTimeNanos
parameter_list|)
block|{
name|proto
operator|=
name|PipelineAckProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setSeqno
argument_list|(
name|seqno
argument_list|)
operator|.
name|addAllStatus
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|replies
argument_list|)
argument_list|)
operator|.
name|setDownstreamAckTimeNanos
argument_list|(
name|downstreamAckTimeNanos
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the sequence number    * @return the sequence number    */
DECL|method|getSeqno ()
specifier|public
name|long
name|getSeqno
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getSeqno
argument_list|()
return|;
block|}
comment|/**    * Get the number of replies    * @return the number of replies    */
DECL|method|getNumOfReplies ()
specifier|public
name|short
name|getNumOfReplies
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|proto
operator|.
name|getStatusCount
argument_list|()
return|;
block|}
comment|/**    * get the ith reply    * @return the the ith reply    */
DECL|method|getReply (int i)
specifier|public
name|Status
name|getReply
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|proto
operator|.
name|getStatus
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**    * Get the time elapsed for downstream ack RTT in nanoseconds    * @return time elapsed for downstream ack in nanoseconds, 0 if no next DN in pipeline    */
DECL|method|getDownstreamAckTimeNanos ()
specifier|public
name|long
name|getDownstreamAckTimeNanos
parameter_list|()
block|{
return|return
name|proto
operator|.
name|getDownstreamAckTimeNanos
argument_list|()
return|;
block|}
comment|/**    * Check if this ack contains error status    * @return true if all statuses are SUCCESS    */
DECL|method|isSuccess ()
specifier|public
name|boolean
name|isSuccess
parameter_list|()
block|{
for|for
control|(
name|DataTransferProtos
operator|.
name|Status
name|reply
range|:
name|proto
operator|.
name|getStatusList
argument_list|()
control|)
block|{
if|if
condition|(
name|reply
operator|!=
name|DataTransferProtos
operator|.
name|Status
operator|.
name|SUCCESS
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**** Writable interface ****/
DECL|method|readFields (InputStream in)
specifier|public
name|void
name|readFields
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|proto
operator|=
name|PipelineAckProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|write (OutputStream out)
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|proto
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|//Object
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|proto
argument_list|)
return|;
block|}
block|}
end_class

end_unit

