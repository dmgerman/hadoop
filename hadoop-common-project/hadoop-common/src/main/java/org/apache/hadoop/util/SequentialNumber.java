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
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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

begin_comment
comment|/**  * Sequential number generator.  *   * This class is thread safe.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SequentialNumber
specifier|public
specifier|abstract
class|class
name|SequentialNumber
implements|implements
name|IdGenerator
block|{
DECL|field|currentValue
specifier|private
specifier|final
name|AtomicLong
name|currentValue
decl_stmt|;
comment|/** Create a new instance with the given initial value. */
DECL|method|SequentialNumber (final long initialValue)
specifier|protected
name|SequentialNumber
parameter_list|(
specifier|final
name|long
name|initialValue
parameter_list|)
block|{
name|currentValue
operator|=
operator|new
name|AtomicLong
argument_list|(
name|initialValue
argument_list|)
expr_stmt|;
block|}
comment|/** @return the current value. */
DECL|method|getCurrentValue ()
specifier|public
name|long
name|getCurrentValue
parameter_list|()
block|{
return|return
name|currentValue
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** Set current value. */
DECL|method|setCurrentValue (long value)
specifier|public
name|void
name|setCurrentValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|currentValue
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Increment and then return the next value. */
DECL|method|nextValue ()
specifier|public
name|long
name|nextValue
parameter_list|()
block|{
return|return
name|currentValue
operator|.
name|incrementAndGet
argument_list|()
return|;
block|}
comment|/** Skip to the new value. */
DECL|method|skipTo (long newValue)
specifier|public
name|void
name|skipTo
parameter_list|(
name|long
name|newValue
parameter_list|)
throws|throws
name|IllegalStateException
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|long
name|c
init|=
name|getCurrentValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|newValue
operator|<
name|c
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot skip to less than the current value (="
operator|+
name|c
operator|+
literal|"), where newValue="
operator|+
name|newValue
argument_list|)
throw|;
block|}
if|if
condition|(
name|currentValue
operator|.
name|compareAndSet
argument_list|(
name|c
argument_list|,
name|newValue
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|equals (final Object that)
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|==
literal|null
operator|||
name|this
operator|.
name|getClass
argument_list|()
operator|!=
name|that
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|AtomicLong
name|thatValue
init|=
operator|(
operator|(
name|SequentialNumber
operator|)
name|that
operator|)
operator|.
name|currentValue
decl_stmt|;
return|return
name|currentValue
operator|.
name|equals
argument_list|(
name|thatValue
argument_list|)
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
specifier|final
name|long
name|v
init|=
name|currentValue
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
operator|(
name|int
operator|)
name|v
operator|^
call|(
name|int
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
return|;
block|}
block|}
end_class

end_unit

