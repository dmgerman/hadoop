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
name|protocol
operator|.
name|datatransfer
operator|.
name|DataTransferProtoUtil
operator|.
name|fromProto
import|;
end_import

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
name|protocol
operator|.
name|datatransfer
operator|.
name|DataTransferProtoUtil
operator|.
name|continueTraceSpan
import|;
end_import

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
name|DataInputStream
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
name|proto
operator|.
name|DataTransferProtos
operator|.
name|CachingStrategyProto
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
name|OpBlockChecksumProto
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
name|OpCopyBlockProto
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
name|OpReadBlockProto
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
name|OpReplaceBlockProto
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
name|OpRequestShortCircuitAccessProto
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
name|OpTransferBlockProto
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
name|OpWriteBlockProto
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
name|ReleaseShortCircuitAccessRequestProto
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
name|ShortCircuitShmRequestProto
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
name|PBHelper
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
name|CachingStrategy
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
name|shortcircuit
operator|.
name|ShortCircuitShm
operator|.
name|SlotId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|htrace
operator|.
name|TraceScope
import|;
end_import

begin_comment
comment|/** Receiver */
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
DECL|class|Receiver
specifier|public
specifier|abstract
class|class
name|Receiver
implements|implements
name|DataTransferProtocol
block|{
DECL|field|in
specifier|protected
name|DataInputStream
name|in
decl_stmt|;
comment|/** Initialize a receiver for DataTransferProtocol with a socket. */
DECL|method|initialize (final DataInputStream in)
specifier|protected
name|void
name|initialize
parameter_list|(
specifier|final
name|DataInputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/** Read an Op.  It also checks protocol version. */
DECL|method|readOp ()
specifier|protected
specifier|final
name|Op
name|readOp
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|short
name|version
init|=
name|in
operator|.
name|readShort
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
name|DataTransferProtocol
operator|.
name|DATA_TRANSFER_VERSION
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Version Mismatch (Expected: "
operator|+
name|DataTransferProtocol
operator|.
name|DATA_TRANSFER_VERSION
operator|+
literal|", Received: "
operator|+
name|version
operator|+
literal|" )"
argument_list|)
throw|;
block|}
return|return
name|Op
operator|.
name|read
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/** Process op by the corresponding method. */
DECL|method|processOp (Op op)
specifier|protected
specifier|final
name|void
name|processOp
parameter_list|(
name|Op
name|op
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|READ_BLOCK
case|:
name|opReadBlock
argument_list|()
expr_stmt|;
break|break;
case|case
name|WRITE_BLOCK
case|:
name|opWriteBlock
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|REPLACE_BLOCK
case|:
name|opReplaceBlock
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|COPY_BLOCK
case|:
name|opCopyBlock
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|BLOCK_CHECKSUM
case|:
name|opBlockChecksum
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|TRANSFER_BLOCK
case|:
name|opTransferBlock
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|REQUEST_SHORT_CIRCUIT_FDS
case|:
name|opRequestShortCircuitFds
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|RELEASE_SHORT_CIRCUIT_FDS
case|:
name|opReleaseShortCircuitFds
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|REQUEST_SHORT_CIRCUIT_SHM
case|:
name|opRequestShortCircuitShm
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown op "
operator|+
name|op
operator|+
literal|" in data stream"
argument_list|)
throw|;
block|}
block|}
DECL|method|getCachingStrategy (CachingStrategyProto strategy)
specifier|static
specifier|private
name|CachingStrategy
name|getCachingStrategy
parameter_list|(
name|CachingStrategyProto
name|strategy
parameter_list|)
block|{
name|Boolean
name|dropBehind
init|=
name|strategy
operator|.
name|hasDropBehind
argument_list|()
condition|?
name|strategy
operator|.
name|getDropBehind
argument_list|()
else|:
literal|null
decl_stmt|;
name|Long
name|readahead
init|=
name|strategy
operator|.
name|hasReadahead
argument_list|()
condition|?
name|strategy
operator|.
name|getReadahead
argument_list|()
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|CachingStrategy
argument_list|(
name|dropBehind
argument_list|,
name|readahead
argument_list|)
return|;
block|}
comment|/** Receive OP_READ_BLOCK */
DECL|method|opReadBlock ()
specifier|private
name|void
name|opReadBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|OpReadBlockProto
name|proto
init|=
name|OpReadBlockProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|readBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|proto
operator|.
name|getOffset
argument_list|()
argument_list|,
name|proto
operator|.
name|getLen
argument_list|()
argument_list|,
name|proto
operator|.
name|getSendChecksums
argument_list|()
argument_list|,
operator|(
name|proto
operator|.
name|hasCachingStrategy
argument_list|()
condition|?
name|getCachingStrategy
argument_list|(
name|proto
operator|.
name|getCachingStrategy
argument_list|()
argument_list|)
else|:
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive OP_WRITE_BLOCK */
DECL|method|opWriteBlock (DataInputStream in)
specifier|private
name|void
name|opWriteBlock
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|OpWriteBlockProto
name|proto
init|=
name|OpWriteBlockProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
index|[]
name|targets
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getTargetsList
argument_list|()
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|writeBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convertStorageType
argument_list|(
name|proto
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|targets
argument_list|,
name|PBHelper
operator|.
name|convertStorageTypes
argument_list|(
name|proto
operator|.
name|getTargetStorageTypesList
argument_list|()
argument_list|,
name|targets
operator|.
name|length
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getSource
argument_list|()
argument_list|)
argument_list|,
name|fromProto
argument_list|(
name|proto
operator|.
name|getStage
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getPipelineSize
argument_list|()
argument_list|,
name|proto
operator|.
name|getMinBytesRcvd
argument_list|()
argument_list|,
name|proto
operator|.
name|getMaxBytesRcvd
argument_list|()
argument_list|,
name|proto
operator|.
name|getLatestGenerationStamp
argument_list|()
argument_list|,
name|fromProto
argument_list|(
name|proto
operator|.
name|getRequestedChecksum
argument_list|()
argument_list|)
argument_list|,
operator|(
name|proto
operator|.
name|hasCachingStrategy
argument_list|()
condition|?
name|getCachingStrategy
argument_list|(
name|proto
operator|.
name|getCachingStrategy
argument_list|()
argument_list|)
else|:
name|CachingStrategy
operator|.
name|newDefaultStrategy
argument_list|()
operator|)
argument_list|,
operator|(
name|proto
operator|.
name|hasAllowLazyPersist
argument_list|()
condition|?
name|proto
operator|.
name|getAllowLazyPersist
argument_list|()
else|:
literal|false
operator|)
argument_list|,
operator|(
name|proto
operator|.
name|hasPinning
argument_list|()
condition|?
name|proto
operator|.
name|getPinning
argument_list|()
else|:
literal|false
operator|)
argument_list|,
operator|(
name|PBHelper
operator|.
name|convertBooleanList
argument_list|(
name|proto
operator|.
name|getTargetPinningsList
argument_list|()
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive {@link Op#TRANSFER_BLOCK} */
DECL|method|opTransferBlock (DataInputStream in)
specifier|private
name|void
name|opTransferBlock
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|OpTransferBlockProto
name|proto
init|=
name|OpTransferBlockProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|DatanodeInfo
index|[]
name|targets
init|=
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getTargetsList
argument_list|()
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|transferBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBaseHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getClientName
argument_list|()
argument_list|,
name|targets
argument_list|,
name|PBHelper
operator|.
name|convertStorageTypes
argument_list|(
name|proto
operator|.
name|getTargetStorageTypesList
argument_list|()
argument_list|,
name|targets
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive {@link Op#REQUEST_SHORT_CIRCUIT_FDS} */
DECL|method|opRequestShortCircuitFds (DataInputStream in)
specifier|private
name|void
name|opRequestShortCircuitFds
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|OpRequestShortCircuitAccessProto
name|proto
init|=
name|OpRequestShortCircuitAccessProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|SlotId
name|slotId
init|=
operator|(
name|proto
operator|.
name|hasSlotId
argument_list|()
operator|)
condition|?
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getSlotId
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|requestShortCircuitFds
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|,
name|slotId
argument_list|,
name|proto
operator|.
name|getMaxVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive {@link Op#RELEASE_SHORT_CIRCUIT_FDS} */
DECL|method|opReleaseShortCircuitFds (DataInputStream in)
specifier|private
name|void
name|opReleaseShortCircuitFds
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ReleaseShortCircuitAccessRequestProto
name|proto
init|=
name|ReleaseShortCircuitAccessRequestProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getTraceInfo
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|releaseShortCircuitFds
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getSlotId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive {@link Op#REQUEST_SHORT_CIRCUIT_SHM} */
DECL|method|opRequestShortCircuitShm (DataInputStream in)
specifier|private
name|void
name|opRequestShortCircuitShm
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ShortCircuitShmRequestProto
name|proto
init|=
name|ShortCircuitShmRequestProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getTraceInfo
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|requestShortCircuitShm
argument_list|(
name|proto
operator|.
name|getClientName
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive OP_REPLACE_BLOCK */
DECL|method|opReplaceBlock (DataInputStream in)
specifier|private
name|void
name|opReplaceBlock
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OpReplaceBlockProto
name|proto
init|=
name|OpReplaceBlockProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|replaceBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convertStorageType
argument_list|(
name|proto
operator|.
name|getStorageType
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|,
name|proto
operator|.
name|getDelHint
argument_list|()
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getSource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive OP_COPY_BLOCK */
DECL|method|opCopyBlock (DataInputStream in)
specifier|private
name|void
name|opCopyBlock
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OpCopyBlockProto
name|proto
init|=
name|OpCopyBlockProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|copyBlock
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Receive OP_BLOCK_CHECKSUM */
DECL|method|opBlockChecksum (DataInputStream in)
specifier|private
name|void
name|opBlockChecksum
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|OpBlockChecksumProto
name|proto
init|=
name|OpBlockChecksumProto
operator|.
name|parseFrom
argument_list|(
name|vintPrefixed
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|TraceScope
name|traceScope
init|=
name|continueTraceSpan
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
argument_list|,
name|proto
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|blockChecksum
argument_list|(
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getBlock
argument_list|()
argument_list|)
argument_list|,
name|PBHelper
operator|.
name|convert
argument_list|(
name|proto
operator|.
name|getHeader
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|traceScope
operator|!=
literal|null
condition|)
name|traceScope
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

