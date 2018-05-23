begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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

begin_comment
comment|/**  * This is a simple ByteBufferPool which just creates ByteBuffers as needed.  * It also caches ByteBuffers after they're released.  It will always return  * the smallest cached buffer with at least the capacity you request.  * We don't try to do anything clever here like try to limit the maximum cache  * size.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ElasticByteBufferPool
specifier|public
specifier|final
class|class
name|ElasticByteBufferPool
implements|implements
name|ByteBufferPool
block|{
DECL|class|Key
specifier|private
specifier|static
specifier|final
class|class
name|Key
implements|implements
name|Comparable
argument_list|<
name|Key
argument_list|>
block|{
DECL|field|capacity
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
DECL|field|insertionTime
specifier|private
specifier|final
name|long
name|insertionTime
decl_stmt|;
DECL|method|Key (int capacity, long insertionTime)
name|Key
parameter_list|(
name|int
name|capacity
parameter_list|,
name|long
name|insertionTime
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|insertionTime
operator|=
name|insertionTime
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Key other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Key
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
name|capacity
argument_list|,
name|other
operator|.
name|capacity
argument_list|)
operator|.
name|compare
argument_list|(
name|insertionTime
argument_list|,
name|other
operator|.
name|insertionTime
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object rhs)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|rhs
parameter_list|)
block|{
if|if
condition|(
name|rhs
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|Key
name|o
init|=
operator|(
name|Key
operator|)
name|rhs
decl_stmt|;
return|return
operator|(
name|compareTo
argument_list|(
name|o
argument_list|)
operator|==
literal|0
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
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
return|return
operator|new
name|HashCodeBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|capacity
argument_list|)
operator|.
name|append
argument_list|(
name|insertionTime
argument_list|)
operator|.
name|toHashCode
argument_list|()
return|;
block|}
block|}
DECL|field|buffers
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|buffers
init|=
operator|new
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|directBuffers
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|directBuffers
init|=
operator|new
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getBufferTree (boolean direct)
specifier|private
specifier|final
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|getBufferTree
parameter_list|(
name|boolean
name|direct
parameter_list|)
block|{
return|return
name|direct
condition|?
name|directBuffers
else|:
name|buffers
return|;
block|}
annotation|@
name|Override
DECL|method|getBuffer (boolean direct, int length)
specifier|public
specifier|synchronized
name|ByteBuffer
name|getBuffer
parameter_list|(
name|boolean
name|direct
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|tree
init|=
name|getBufferTree
argument_list|(
name|direct
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|entry
init|=
name|tree
operator|.
name|ceilingEntry
argument_list|(
operator|new
name|Key
argument_list|(
name|length
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
return|return
name|direct
condition|?
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|length
argument_list|)
else|:
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|length
argument_list|)
return|;
block|}
name|tree
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|putBuffer (ByteBuffer buffer)
specifier|public
specifier|synchronized
name|void
name|putBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|TreeMap
argument_list|<
name|Key
argument_list|,
name|ByteBuffer
argument_list|>
name|tree
init|=
name|getBufferTree
argument_list|(
name|buffer
operator|.
name|isDirect
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Key
name|key
init|=
operator|new
name|Key
argument_list|(
name|buffer
operator|.
name|capacity
argument_list|()
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tree
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|tree
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Buffers are indexed by (capacity, time).
comment|// If our key is not unique on the first try, we try again, since the
comment|// time will be different.  Since we use nanoseconds, it's pretty
comment|// unlikely that we'll loop even once, unless the system clock has a
comment|// poor granularity.
block|}
block|}
comment|/**    * Get the size of the buffer pool, for the specified buffer type.    *    * @param direct Whether the size is returned for direct buffers    * @return The size    */
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|method|size (boolean direct)
specifier|public
name|int
name|size
parameter_list|(
name|boolean
name|direct
parameter_list|)
block|{
return|return
name|getBufferTree
argument_list|(
name|direct
argument_list|)
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

