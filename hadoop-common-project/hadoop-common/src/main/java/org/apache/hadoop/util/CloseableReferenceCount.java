begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|AsynchronousCloseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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

begin_comment
comment|/**  * A closeable object that maintains a reference count.  *  * Once the object is closed, attempting to take a new reference will throw  * ClosedChannelException.  */
end_comment

begin_class
DECL|class|CloseableReferenceCount
specifier|public
class|class
name|CloseableReferenceCount
block|{
comment|/**    * Bit mask representing a closed domain socket.    */
DECL|field|STATUS_CLOSED_MASK
specifier|private
specifier|static
specifier|final
name|int
name|STATUS_CLOSED_MASK
init|=
literal|1
operator|<<
literal|30
decl_stmt|;
comment|/**    * The status bits.    *    * Bit 30: 0 = open, 1 = closed.    * Bits 29 to 0: the reference count.    */
DECL|field|status
specifier|private
specifier|final
name|AtomicInteger
name|status
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|CloseableReferenceCount ()
specifier|public
name|CloseableReferenceCount
parameter_list|()
block|{ }
comment|/**    * Increment the reference count.    *    * @throws ClosedChannelException      If the status is closed.    */
DECL|method|reference ()
specifier|public
name|void
name|reference
parameter_list|()
throws|throws
name|ClosedChannelException
block|{
name|int
name|curBits
init|=
name|status
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|curBits
operator|&
name|STATUS_CLOSED_MASK
operator|)
operator|!=
literal|0
condition|)
block|{
name|status
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ClosedChannelException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Decrement the reference count.    *    * @return          True if the object is closed and has no outstanding    *                  references.    */
DECL|method|unreference ()
specifier|public
name|boolean
name|unreference
parameter_list|()
block|{
name|int
name|newVal
init|=
name|status
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|newVal
operator|!=
literal|0xffffffff
argument_list|,
literal|"called unreference when the reference count was already at 0."
argument_list|)
expr_stmt|;
return|return
name|newVal
operator|==
name|STATUS_CLOSED_MASK
return|;
block|}
comment|/**    * Decrement the reference count, checking to make sure that the    * CloseableReferenceCount is not closed.    *    * @throws AsynchronousCloseException  If the status is closed.    */
DECL|method|unreferenceCheckClosed ()
specifier|public
name|void
name|unreferenceCheckClosed
parameter_list|()
throws|throws
name|ClosedChannelException
block|{
name|int
name|newVal
init|=
name|status
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|newVal
operator|&
name|STATUS_CLOSED_MASK
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AsynchronousCloseException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Return true if the status is currently open.    *    * @return                 True if the status is currently open.    */
DECL|method|isOpen ()
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
operator|(
operator|(
name|status
operator|.
name|get
argument_list|()
operator|&
name|STATUS_CLOSED_MASK
operator|)
operator|==
literal|0
operator|)
return|;
block|}
comment|/**    * Mark the status as closed.    *    * Once the status is closed, it cannot be reopened.    *    * @return                         The current reference count.    * @throws ClosedChannelException  If someone else closes the object    *                                 before we do.    */
DECL|method|setClosed ()
specifier|public
name|int
name|setClosed
parameter_list|()
throws|throws
name|ClosedChannelException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|curBits
init|=
name|status
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|curBits
operator|&
name|STATUS_CLOSED_MASK
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ClosedChannelException
argument_list|()
throw|;
block|}
if|if
condition|(
name|status
operator|.
name|compareAndSet
argument_list|(
name|curBits
argument_list|,
name|curBits
operator||
name|STATUS_CLOSED_MASK
argument_list|)
condition|)
block|{
return|return
name|curBits
operator|&
operator|(
operator|~
name|STATUS_CLOSED_MASK
operator|)
return|;
block|}
block|}
block|}
comment|/**    * Get the current reference count.    *    * @return                 The current reference count.    */
DECL|method|getReferenceCount ()
specifier|public
name|int
name|getReferenceCount
parameter_list|()
block|{
return|return
name|status
operator|.
name|get
argument_list|()
operator|&
operator|(
operator|~
name|STATUS_CLOSED_MASK
operator|)
return|;
block|}
block|}
end_class

end_unit

