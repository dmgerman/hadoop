begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|EqualsBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|builder
operator|.
name|HashCodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|InvalidRequestException
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
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
name|io
operator|.
name|nativeio
operator|.
name|NativeIO
operator|.
name|POSIX
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
name|util
operator|.
name|Shell
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
name|util
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ComparisonChain
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|misc
operator|.
name|Unsafe
import|;
end_import

begin_comment
comment|/**  * A shared memory segment used to implement short-circuit reads.  */
end_comment

begin_class
DECL|class|ShortCircuitShm
specifier|public
class|class
name|ShortCircuitShm
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ShortCircuitShm
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BYTES_PER_SLOT
specifier|protected
specifier|static
specifier|final
name|int
name|BYTES_PER_SLOT
init|=
literal|64
decl_stmt|;
DECL|field|unsafe
specifier|private
specifier|static
specifier|final
name|Unsafe
name|unsafe
init|=
name|safetyDance
argument_list|()
decl_stmt|;
DECL|method|safetyDance ()
specifier|private
specifier|static
name|Unsafe
name|safetyDance
parameter_list|()
block|{
try|try
block|{
name|Field
name|f
init|=
name|Unsafe
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
name|Unsafe
operator|)
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to load misc.Unsafe"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Calculate the usable size of a shared memory segment.    * We round down to a multiple of the slot size and do some validation.    *    * @param stream The stream we're using.    * @return       The usable size of the shared memory segment.    */
DECL|method|getUsableLength (FileInputStream stream)
specifier|private
specifier|static
name|int
name|getUsableLength
parameter_list|(
name|FileInputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|intSize
init|=
name|Ints
operator|.
name|checkedCast
argument_list|(
name|stream
operator|.
name|getChannel
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|slots
init|=
name|intSize
operator|/
name|BYTES_PER_SLOT
decl_stmt|;
if|if
condition|(
name|slots
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"size of shared memory segment was "
operator|+
name|intSize
operator|+
literal|", but that is not enough to hold even one slot."
argument_list|)
throw|;
block|}
return|return
name|slots
operator|*
name|BYTES_PER_SLOT
return|;
block|}
comment|/**    * Identifies a DfsClientShm.    */
DECL|class|ShmId
specifier|public
specifier|static
class|class
name|ShmId
implements|implements
name|Comparable
argument_list|<
name|ShmId
argument_list|>
block|{
DECL|field|random
specifier|private
specifier|static
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|hi
specifier|private
specifier|final
name|long
name|hi
decl_stmt|;
DECL|field|lo
specifier|private
specifier|final
name|long
name|lo
decl_stmt|;
comment|/**      * Generate a random ShmId.      *       * We generate ShmIds randomly to prevent a malicious client from      * successfully guessing one and using that to interfere with another      * client.      */
DECL|method|createRandom ()
specifier|public
specifier|static
name|ShmId
name|createRandom
parameter_list|()
block|{
return|return
operator|new
name|ShmId
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|ShmId (long hi, long lo)
specifier|public
name|ShmId
parameter_list|(
name|long
name|hi
parameter_list|,
name|long
name|lo
parameter_list|)
block|{
name|this
operator|.
name|hi
operator|=
name|hi
expr_stmt|;
name|this
operator|.
name|lo
operator|=
name|lo
expr_stmt|;
block|}
DECL|method|getHi ()
specifier|public
name|long
name|getHi
parameter_list|()
block|{
return|return
name|hi
return|;
block|}
DECL|method|getLo ()
specifier|public
name|long
name|getLo
parameter_list|()
block|{
return|return
name|lo
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|(
name|o
operator|==
literal|null
operator|)
operator|||
operator|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ShmId
name|other
init|=
operator|(
name|ShmId
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|hi
argument_list|,
name|other
operator|.
name|hi
argument_list|)
operator|.
name|append
argument_list|(
name|lo
argument_list|,
name|other
operator|.
name|lo
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|hi
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|lo
argument_list|)
operator|.
name|toHashCode
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
name|String
operator|.
name|format
argument_list|(
literal|"%016x%016x"
argument_list|,
name|hi
argument_list|,
name|lo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ShmId other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ShmId
name|other
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|hi
argument_list|,
name|other
operator|.
name|hi
argument_list|)
operator|.
name|compare
argument_list|(
name|lo
argument_list|,
name|other
operator|.
name|lo
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
empty_stmt|;
comment|/**    * Uniquely identifies a slot.    */
DECL|class|SlotId
specifier|public
specifier|static
class|class
name|SlotId
block|{
DECL|field|shmId
specifier|private
specifier|final
name|ShmId
name|shmId
decl_stmt|;
DECL|field|slotIdx
specifier|private
specifier|final
name|int
name|slotIdx
decl_stmt|;
DECL|method|SlotId (ShmId shmId, int slotIdx)
specifier|public
name|SlotId
parameter_list|(
name|ShmId
name|shmId
parameter_list|,
name|int
name|slotIdx
parameter_list|)
block|{
name|this
operator|.
name|shmId
operator|=
name|shmId
expr_stmt|;
name|this
operator|.
name|slotIdx
operator|=
name|slotIdx
expr_stmt|;
block|}
DECL|method|getShmId ()
specifier|public
name|ShmId
name|getShmId
parameter_list|()
block|{
return|return
name|shmId
return|;
block|}
DECL|method|getSlotIdx ()
specifier|public
name|int
name|getSlotIdx
parameter_list|()
block|{
return|return
name|slotIdx
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|(
name|o
operator|==
literal|null
operator|)
operator|||
operator|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SlotId
name|other
init|=
operator|(
name|SlotId
operator|)
name|o
decl_stmt|;
return|return
operator|new
name|EqualsBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|shmId
argument_list|,
name|other
operator|.
name|shmId
argument_list|)
operator|.
name|append
argument_list|(
name|slotIdx
argument_list|,
name|other
operator|.
name|slotIdx
argument_list|)
operator|.
name|isEquals
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|.
name|shmId
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|slotIdx
argument_list|)
operator|.
name|toHashCode
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
name|String
operator|.
name|format
argument_list|(
literal|"SlotId(%s:%d)"
argument_list|,
name|shmId
operator|.
name|toString
argument_list|()
argument_list|,
name|slotIdx
argument_list|)
return|;
block|}
block|}
DECL|class|SlotIterator
specifier|public
class|class
name|SlotIterator
implements|implements
name|Iterator
argument_list|<
name|Slot
argument_list|>
block|{
DECL|field|slotIdx
name|int
name|slotIdx
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
synchronized|synchronized
init|(
name|ShortCircuitShm
operator|.
name|this
init|)
block|{
return|return
name|allocatedSlots
operator|.
name|nextSetBit
argument_list|(
name|slotIdx
operator|+
literal|1
argument_list|)
operator|!=
operator|-
literal|1
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|Slot
name|next
parameter_list|()
block|{
synchronized|synchronized
init|(
name|ShortCircuitShm
operator|.
name|this
init|)
block|{
name|int
name|nextSlotIdx
init|=
name|allocatedSlots
operator|.
name|nextSetBit
argument_list|(
name|slotIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextSlotIdx
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|slotIdx
operator|=
name|nextSlotIdx
expr_stmt|;
return|return
name|slots
index|[
name|nextSlotIdx
index|]
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"SlotIterator "
operator|+
literal|"doesn't support removal"
argument_list|)
throw|;
block|}
block|}
comment|/**    * A slot containing information about a replica.    *    * The format is:    * word 0    *   bit 0:32   Slot flags (see below).    *   bit 33:63  Anchor count.    * word 1:7    *   Reserved for future use, such as statistics.    *   Padding is also useful for avoiding false sharing.    *    * Little-endian versus big-endian is not relevant here since both the client    * and the server reside on the same computer and use the same orientation.    */
DECL|class|Slot
specifier|public
class|class
name|Slot
block|{
comment|/**      * Flag indicating that the slot is valid.        *       * The DFSClient sets this flag when it allocates a new slot within one of      * its shared memory regions.      *       * The DataNode clears this flag when the replica associated with this slot      * is no longer valid.  The client itself also clears this flag when it      * believes that the DataNode is no longer using this slot to communicate.      */
DECL|field|VALID_FLAG
specifier|private
specifier|static
specifier|final
name|long
name|VALID_FLAG
init|=
literal|1L
operator|<<
literal|63
decl_stmt|;
comment|/**      * Flag indicating that the slot can be anchored.      */
DECL|field|ANCHORABLE_FLAG
specifier|private
specifier|static
specifier|final
name|long
name|ANCHORABLE_FLAG
init|=
literal|1L
operator|<<
literal|62
decl_stmt|;
comment|/**      * The slot address in memory.      */
DECL|field|slotAddress
specifier|private
specifier|final
name|long
name|slotAddress
decl_stmt|;
comment|/**      * BlockId of the block this slot is used for.      */
DECL|field|blockId
specifier|private
specifier|final
name|ExtendedBlockId
name|blockId
decl_stmt|;
DECL|method|Slot (long slotAddress, ExtendedBlockId blockId)
name|Slot
parameter_list|(
name|long
name|slotAddress
parameter_list|,
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
name|this
operator|.
name|slotAddress
operator|=
name|slotAddress
expr_stmt|;
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
block|}
comment|/**      * Get the short-circuit memory segment associated with this Slot.      *      * @return      The enclosing short-circuit memory segment.      */
DECL|method|getShm ()
specifier|public
name|ShortCircuitShm
name|getShm
parameter_list|()
block|{
return|return
name|ShortCircuitShm
operator|.
name|this
return|;
block|}
comment|/**      * Get the ExtendedBlockId associated with this slot.      *      * @return      The ExtendedBlockId of this slot.      */
DECL|method|getBlockId ()
specifier|public
name|ExtendedBlockId
name|getBlockId
parameter_list|()
block|{
return|return
name|blockId
return|;
block|}
comment|/**      * Get the SlotId of this slot, containing both shmId and slotIdx.      *      * @return      The SlotId of this slot.      */
DECL|method|getSlotId ()
specifier|public
name|SlotId
name|getSlotId
parameter_list|()
block|{
return|return
operator|new
name|SlotId
argument_list|(
name|getShmId
argument_list|()
argument_list|,
name|getSlotIdx
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the Slot index.      *      * @return      The index of this slot.      */
DECL|method|getSlotIdx ()
specifier|public
name|int
name|getSlotIdx
parameter_list|()
block|{
return|return
name|Ints
operator|.
name|checkedCast
argument_list|(
operator|(
name|slotAddress
operator|-
name|baseAddress
operator|)
operator|/
name|BYTES_PER_SLOT
argument_list|)
return|;
block|}
DECL|method|isSet (long flag)
specifier|private
name|boolean
name|isSet
parameter_list|(
name|long
name|flag
parameter_list|)
block|{
name|long
name|prev
init|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
decl_stmt|;
return|return
operator|(
name|prev
operator|&
name|flag
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|setFlag (long flag)
specifier|private
name|void
name|setFlag
parameter_list|(
name|long
name|flag
parameter_list|)
block|{
name|long
name|prev
decl_stmt|;
do|do
block|{
name|prev
operator|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|prev
operator|&
name|flag
operator|)
operator|!=
literal|0
condition|)
block|{
return|return;
block|}
block|}
do|while
condition|(
operator|!
name|unsafe
operator|.
name|compareAndSwapLong
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|,
name|prev
argument_list|,
name|prev
operator||
name|flag
argument_list|)
condition|)
do|;
block|}
DECL|method|clearFlag (long flag)
specifier|private
name|void
name|clearFlag
parameter_list|(
name|long
name|flag
parameter_list|)
block|{
name|long
name|prev
decl_stmt|;
do|do
block|{
name|prev
operator|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|prev
operator|&
name|flag
operator|)
operator|==
literal|0
condition|)
block|{
return|return;
block|}
block|}
do|while
condition|(
operator|!
name|unsafe
operator|.
name|compareAndSwapLong
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|,
name|prev
argument_list|,
name|prev
operator|&
operator|(
operator|~
name|flag
operator|)
argument_list|)
condition|)
do|;
block|}
DECL|method|isValid ()
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
name|isSet
argument_list|(
name|VALID_FLAG
argument_list|)
return|;
block|}
DECL|method|makeValid ()
specifier|public
name|void
name|makeValid
parameter_list|()
block|{
name|setFlag
argument_list|(
name|VALID_FLAG
argument_list|)
expr_stmt|;
block|}
DECL|method|makeInvalid ()
specifier|public
name|void
name|makeInvalid
parameter_list|()
block|{
name|clearFlag
argument_list|(
name|VALID_FLAG
argument_list|)
expr_stmt|;
block|}
DECL|method|isAnchorable ()
specifier|public
name|boolean
name|isAnchorable
parameter_list|()
block|{
return|return
name|isSet
argument_list|(
name|ANCHORABLE_FLAG
argument_list|)
return|;
block|}
DECL|method|makeAnchorable ()
specifier|public
name|void
name|makeAnchorable
parameter_list|()
block|{
name|setFlag
argument_list|(
name|ANCHORABLE_FLAG
argument_list|)
expr_stmt|;
block|}
DECL|method|makeUnanchorable ()
specifier|public
name|void
name|makeUnanchorable
parameter_list|()
block|{
name|clearFlag
argument_list|(
name|ANCHORABLE_FLAG
argument_list|)
expr_stmt|;
block|}
DECL|method|isAnchored ()
specifier|public
name|boolean
name|isAnchored
parameter_list|()
block|{
name|long
name|prev
init|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|prev
operator|&
name|VALID_FLAG
operator|)
operator|==
literal|0
condition|)
block|{
comment|// Slot is no longer valid.
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|prev
operator|&
literal|0x7fffffff
operator|)
operator|!=
literal|0
operator|)
return|;
block|}
comment|/**      * Try to add an anchor for a given slot.      *      * When a slot is anchored, we know that the block it refers to is resident      * in memory.      *      * @return          True if the slot is anchored.      */
DECL|method|addAnchor ()
specifier|public
name|boolean
name|addAnchor
parameter_list|()
block|{
name|long
name|prev
decl_stmt|;
do|do
block|{
name|prev
operator|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|prev
operator|&
name|VALID_FLAG
operator|)
operator|==
literal|0
condition|)
block|{
comment|// Slot is no longer valid.
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
name|prev
operator|&
name|ANCHORABLE_FLAG
operator|)
operator|==
literal|0
condition|)
block|{
comment|// Slot can't be anchored right now.
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
name|prev
operator|&
literal|0x7fffffff
operator|)
operator|==
literal|0x7fffffff
condition|)
block|{
comment|// Too many other threads have anchored the slot (2 billion?)
return|return
literal|false
return|;
block|}
block|}
do|while
condition|(
operator|!
name|unsafe
operator|.
name|compareAndSwapLong
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|,
name|prev
argument_list|,
name|prev
operator|+
literal|1
argument_list|)
condition|)
do|;
return|return
literal|true
return|;
block|}
comment|/**      * Remove an anchor for a given slot.      */
DECL|method|removeAnchor ()
specifier|public
name|void
name|removeAnchor
parameter_list|()
block|{
name|long
name|prev
decl_stmt|;
do|do
block|{
name|prev
operator|=
name|unsafe
operator|.
name|getLongVolatile
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|(
name|prev
operator|&
literal|0x7fffffff
operator|)
operator|!=
literal|0
argument_list|,
literal|"Tried to remove anchor for slot "
operator|+
name|slotAddress
operator|+
literal|", which was "
operator|+
literal|"not anchored."
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|unsafe
operator|.
name|compareAndSwapLong
argument_list|(
literal|null
argument_list|,
name|this
operator|.
name|slotAddress
argument_list|,
name|prev
argument_list|,
name|prev
operator|-
literal|1
argument_list|)
condition|)
do|;
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
literal|"Slot(slotIdx="
operator|+
name|getSlotIdx
argument_list|()
operator|+
literal|", shm="
operator|+
name|getShm
argument_list|()
operator|+
literal|")"
return|;
block|}
block|}
comment|/**    * ID for this SharedMemorySegment.    */
DECL|field|shmId
specifier|private
specifier|final
name|ShmId
name|shmId
decl_stmt|;
comment|/**    * The base address of the memory-mapped file.    */
DECL|field|baseAddress
specifier|private
specifier|final
name|long
name|baseAddress
decl_stmt|;
comment|/**    * The mmapped length of the shared memory segment    */
DECL|field|mmappedLength
specifier|private
specifier|final
name|int
name|mmappedLength
decl_stmt|;
comment|/**    * The slots associated with this shared memory segment.    * slot[i] contains the slot at offset i * BYTES_PER_SLOT,    * or null if that slot is not allocated.    */
DECL|field|slots
specifier|private
specifier|final
name|Slot
name|slots
index|[]
decl_stmt|;
comment|/**    * A bitset where each bit represents a slot which is in use.    */
DECL|field|allocatedSlots
specifier|private
specifier|final
name|BitSet
name|allocatedSlots
decl_stmt|;
comment|/**    * Create the ShortCircuitShm.    *     * @param shmId       The ID to use.    * @param stream      The stream that we're going to use to create this     *                    shared memory segment.    *                        *                    Although this is a FileInputStream, we are going to    *                    assume that the underlying file descriptor is writable    *                    as well as readable. It would be more appropriate to use    *                    a RandomAccessFile here, but that class does not have    *                    any public accessor which returns a FileDescriptor,    *                    unlike FileInputStream.    */
DECL|method|ShortCircuitShm (ShmId shmId, FileInputStream stream)
specifier|public
name|ShortCircuitShm
parameter_list|(
name|ShmId
name|shmId
parameter_list|,
name|FileInputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|NativeIO
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"NativeIO is not available."
argument_list|)
throw|;
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"DfsClientShm is not yet implemented for Windows."
argument_list|)
throw|;
block|}
if|if
condition|(
name|unsafe
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"can't use DfsClientShm because we failed to "
operator|+
literal|"load misc.Unsafe."
argument_list|)
throw|;
block|}
name|this
operator|.
name|shmId
operator|=
name|shmId
expr_stmt|;
name|this
operator|.
name|mmappedLength
operator|=
name|getUsableLength
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|baseAddress
operator|=
name|POSIX
operator|.
name|mmap
argument_list|(
name|stream
operator|.
name|getFD
argument_list|()
argument_list|,
name|POSIX
operator|.
name|MMAP_PROT_READ
operator||
name|POSIX
operator|.
name|MMAP_PROT_WRITE
argument_list|,
literal|true
argument_list|,
name|mmappedLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|slots
operator|=
operator|new
name|Slot
index|[
name|mmappedLength
operator|/
name|BYTES_PER_SLOT
index|]
expr_stmt|;
name|this
operator|.
name|allocatedSlots
operator|=
operator|new
name|BitSet
argument_list|(
name|slots
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"creating "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"(shmId="
operator|+
name|shmId
operator|+
literal|", mmappedLength="
operator|+
name|mmappedLength
operator|+
literal|", baseAddress="
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%x"
argument_list|,
name|baseAddress
argument_list|)
operator|+
literal|", slots.length="
operator|+
name|slots
operator|.
name|length
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getShmId ()
specifier|public
specifier|final
name|ShmId
name|getShmId
parameter_list|()
block|{
return|return
name|shmId
return|;
block|}
comment|/**    * Determine if this shared memory object is empty.    *    * @return    True if the shared memory object is empty.    */
DECL|method|isEmpty ()
specifier|synchronized
specifier|final
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|allocatedSlots
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
operator|==
operator|-
literal|1
return|;
block|}
comment|/**    * Determine if this shared memory object is full.    *    * @return    True if the shared memory object is full.    */
DECL|method|isFull ()
specifier|synchronized
specifier|final
specifier|public
name|boolean
name|isFull
parameter_list|()
block|{
return|return
name|allocatedSlots
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
operator|>=
name|slots
operator|.
name|length
return|;
block|}
comment|/**    * Calculate the base address of a slot.    *    * @param slotIdx   Index of the slot.    * @return          The base address of the slot.    */
DECL|method|calculateSlotAddress (int slotIdx)
specifier|private
specifier|final
name|long
name|calculateSlotAddress
parameter_list|(
name|int
name|slotIdx
parameter_list|)
block|{
name|long
name|offset
init|=
name|slotIdx
decl_stmt|;
name|offset
operator|*=
name|BYTES_PER_SLOT
expr_stmt|;
return|return
name|this
operator|.
name|baseAddress
operator|+
name|offset
return|;
block|}
comment|/**    * Allocate a new slot and register it.    *    * This function chooses an empty slot, initializes it, and then returns    * the relevant Slot object.    *    * @return    The new slot.    */
DECL|method|allocAndRegisterSlot ( ExtendedBlockId blockId)
specifier|synchronized
specifier|public
specifier|final
name|Slot
name|allocAndRegisterSlot
parameter_list|(
name|ExtendedBlockId
name|blockId
parameter_list|)
block|{
name|int
name|idx
init|=
name|allocatedSlots
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
name|slots
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|this
operator|+
literal|": no more slots are available."
argument_list|)
throw|;
block|}
name|allocatedSlots
operator|.
name|set
argument_list|(
name|idx
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Slot
name|slot
init|=
operator|new
name|Slot
argument_list|(
name|calculateSlotAddress
argument_list|(
name|idx
argument_list|)
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
name|slot
operator|.
name|makeValid
argument_list|()
expr_stmt|;
name|slots
index|[
name|idx
index|]
operator|=
name|slot
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": allocAndRegisterSlot "
operator|+
name|idx
operator|+
literal|": allocatedSlots="
operator|+
name|allocatedSlots
operator|+
name|StringUtils
operator|.
name|getStackTrace
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|slot
return|;
block|}
DECL|method|getSlot (int slotIdx)
specifier|synchronized
specifier|public
specifier|final
name|Slot
name|getSlot
parameter_list|(
name|int
name|slotIdx
parameter_list|)
throws|throws
name|InvalidRequestException
block|{
if|if
condition|(
operator|!
name|allocatedSlots
operator|.
name|get
argument_list|(
name|slotIdx
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
name|this
operator|+
literal|": slot "
operator|+
name|slotIdx
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
return|return
name|slots
index|[
name|slotIdx
index|]
return|;
block|}
comment|/**    * Register a slot.    *    * This function looks at a slot which has already been initialized (by    * another process), and registers it with us.  Then, it returns the     * relevant Slot object.    *    * @return    The slot.    *    * @throws InvalidRequestException    *            If the slot index we're trying to allocate has not been    *            initialized, or is already in use.    */
DECL|method|registerSlot (int slotIdx, ExtendedBlockId blockId)
specifier|synchronized
specifier|public
specifier|final
name|Slot
name|registerSlot
parameter_list|(
name|int
name|slotIdx
parameter_list|,
name|ExtendedBlockId
name|blockId
parameter_list|)
throws|throws
name|InvalidRequestException
block|{
if|if
condition|(
name|slotIdx
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
name|this
operator|+
literal|": invalid negative slot "
operator|+
literal|"index "
operator|+
name|slotIdx
argument_list|)
throw|;
block|}
if|if
condition|(
name|slotIdx
operator|>=
name|slots
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
name|this
operator|+
literal|": invalid slot "
operator|+
literal|"index "
operator|+
name|slotIdx
argument_list|)
throw|;
block|}
if|if
condition|(
name|allocatedSlots
operator|.
name|get
argument_list|(
name|slotIdx
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
name|this
operator|+
literal|": slot "
operator|+
name|slotIdx
operator|+
literal|" is already in use."
argument_list|)
throw|;
block|}
name|Slot
name|slot
init|=
operator|new
name|Slot
argument_list|(
name|calculateSlotAddress
argument_list|(
name|slotIdx
argument_list|)
argument_list|,
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|slot
operator|.
name|isValid
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidRequestException
argument_list|(
name|this
operator|+
literal|": slot "
operator|+
name|slotIdx
operator|+
literal|" has not been allocated."
argument_list|)
throw|;
block|}
name|slots
index|[
name|slotIdx
index|]
operator|=
name|slot
expr_stmt|;
name|allocatedSlots
operator|.
name|set
argument_list|(
name|slotIdx
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": registerSlot "
operator|+
name|slotIdx
operator|+
literal|": allocatedSlots="
operator|+
name|allocatedSlots
operator|+
name|StringUtils
operator|.
name|getStackTrace
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|slot
return|;
block|}
comment|/**    * Unregisters a slot.    *     * This doesn't alter the contents of the slot.  It just means    *    * @param slotIdx  Index of the slot to unregister.    */
DECL|method|unregisterSlot (int slotIdx)
specifier|synchronized
specifier|public
specifier|final
name|void
name|unregisterSlot
parameter_list|(
name|int
name|slotIdx
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|allocatedSlots
operator|.
name|get
argument_list|(
name|slotIdx
argument_list|)
argument_list|,
literal|"tried to unregister slot "
operator|+
name|slotIdx
operator|+
literal|", which was not registered."
argument_list|)
expr_stmt|;
name|allocatedSlots
operator|.
name|set
argument_list|(
name|slotIdx
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|slots
index|[
name|slotIdx
index|]
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": unregisterSlot "
operator|+
name|slotIdx
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Iterate over all allocated slots.    *     * Note that this method isn't safe if     *    * @return        The slot iterator.    */
DECL|method|slotIterator ()
specifier|public
name|SlotIterator
name|slotIterator
parameter_list|()
block|{
return|return
operator|new
name|SlotIterator
argument_list|()
return|;
block|}
DECL|method|free ()
specifier|public
name|void
name|free
parameter_list|()
block|{
try|try
block|{
name|POSIX
operator|.
name|munmap
argument_list|(
name|baseAddress
argument_list|,
name|mmappedLength
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|this
operator|+
literal|": failed to munmap"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|": freed"
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|shmId
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

