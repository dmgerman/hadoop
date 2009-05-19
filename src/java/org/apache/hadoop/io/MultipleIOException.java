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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/** Encapsulate a list of {@link IOException} into an {@link IOException} */
end_comment

begin_class
DECL|class|MultipleIOException
specifier|public
class|class
name|MultipleIOException
extends|extends
name|IOException
block|{
comment|/** Require by {@link java.io.Serializable} */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|exceptions
specifier|private
specifier|final
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
decl_stmt|;
comment|/** Constructor is private, use {@link #createIOException(List)}. */
DECL|method|MultipleIOException (List<IOException> exceptions)
specifier|private
name|MultipleIOException
parameter_list|(
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
parameter_list|)
block|{
name|super
argument_list|(
name|exceptions
operator|.
name|size
argument_list|()
operator|+
literal|" exceptions "
operator|+
name|exceptions
argument_list|)
expr_stmt|;
name|this
operator|.
name|exceptions
operator|=
name|exceptions
expr_stmt|;
block|}
comment|/** @return the underlying exceptions */
DECL|method|getExceptions ()
specifier|public
name|List
argument_list|<
name|IOException
argument_list|>
name|getExceptions
parameter_list|()
block|{
return|return
name|exceptions
return|;
block|}
comment|/** A convenient method to create an {@link IOException}. */
DECL|method|createIOException (List<IOException> exceptions)
specifier|public
specifier|static
name|IOException
name|createIOException
parameter_list|(
name|List
argument_list|<
name|IOException
argument_list|>
name|exceptions
parameter_list|)
block|{
if|if
condition|(
name|exceptions
operator|==
literal|null
operator|||
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|exceptions
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
operator|new
name|MultipleIOException
argument_list|(
name|exceptions
argument_list|)
return|;
block|}
block|}
end_class

end_unit

