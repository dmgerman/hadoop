begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Const Counters for an enum type.  *  * It's the const version of EnumCounters. Any modification ends with a  * ConstEnumException.  *  * @see org.apache.hadoop.hdfs.util.EnumCounters  */
end_comment

begin_class
DECL|class|ConstEnumCounters
specifier|public
class|class
name|ConstEnumCounters
parameter_list|<
name|E
extends|extends
name|Enum
parameter_list|<
name|E
parameter_list|>
parameter_list|>
extends|extends
name|EnumCounters
argument_list|<
name|E
argument_list|>
block|{
comment|/**    * An exception class for modification on ConstEnumCounters.    */
DECL|class|ConstEnumException
specifier|public
specifier|static
specifier|final
class|class
name|ConstEnumException
extends|extends
name|RuntimeException
block|{
DECL|method|ConstEnumException (String msg)
specifier|private
name|ConstEnumException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Throwing this exception if any modification occurs.    */
DECL|field|CONST_ENUM_EXCEPTION
specifier|private
specifier|static
specifier|final
name|ConstEnumException
name|CONST_ENUM_EXCEPTION
init|=
operator|new
name|ConstEnumException
argument_list|(
literal|"modification on const."
argument_list|)
decl_stmt|;
DECL|method|ConstEnumCounters (Class<E> enumClass, long defaultVal)
specifier|public
name|ConstEnumCounters
parameter_list|(
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|,
name|long
name|defaultVal
parameter_list|)
block|{
name|super
argument_list|(
name|enumClass
argument_list|)
expr_stmt|;
name|forceReset
argument_list|(
name|defaultVal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|negation ()
specifier|public
specifier|final
name|void
name|negation
parameter_list|()
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|set (final E e, final long value)
specifier|public
specifier|final
name|void
name|set
parameter_list|(
specifier|final
name|E
name|e
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|set (final EnumCounters<E> that)
specifier|public
specifier|final
name|void
name|set
parameter_list|(
specifier|final
name|EnumCounters
argument_list|<
name|E
argument_list|>
name|that
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|reset ()
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|add (final E e, final long value)
specifier|public
specifier|final
name|void
name|add
parameter_list|(
specifier|final
name|E
name|e
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|add (final EnumCounters<E> that)
specifier|public
specifier|final
name|void
name|add
parameter_list|(
specifier|final
name|EnumCounters
argument_list|<
name|E
argument_list|>
name|that
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|subtract (final E e, final long value)
specifier|public
specifier|final
name|void
name|subtract
parameter_list|(
specifier|final
name|E
name|e
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|subtract (final EnumCounters<E> that)
specifier|public
specifier|final
name|void
name|subtract
parameter_list|(
specifier|final
name|EnumCounters
argument_list|<
name|E
argument_list|>
name|that
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
annotation|@
name|Override
DECL|method|reset (long val)
specifier|public
specifier|final
name|void
name|reset
parameter_list|(
name|long
name|val
parameter_list|)
block|{
throw|throw
name|CONST_ENUM_EXCEPTION
throw|;
block|}
DECL|method|forceReset (long val)
specifier|private
name|void
name|forceReset
parameter_list|(
name|long
name|val
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

