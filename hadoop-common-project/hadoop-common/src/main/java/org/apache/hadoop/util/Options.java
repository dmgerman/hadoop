begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|fs
operator|.
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|Path
import|;
end_import

begin_comment
comment|/**  * This class allows generic access to variable length type-safe parameter  * lists.  */
end_comment

begin_class
DECL|class|Options
specifier|public
class|class
name|Options
block|{
DECL|class|StringOption
specifier|public
specifier|static
specifier|abstract
class|class
name|StringOption
block|{
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|method|StringOption (String value)
specifier|protected
name|StringOption
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|ClassOption
specifier|public
specifier|static
specifier|abstract
class|class
name|ClassOption
block|{
DECL|field|value
specifier|private
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|value
decl_stmt|;
DECL|method|ClassOption (Class<?> value)
specifier|protected
name|ClassOption
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|BooleanOption
specifier|public
specifier|static
specifier|abstract
class|class
name|BooleanOption
block|{
DECL|field|value
specifier|private
specifier|final
name|boolean
name|value
decl_stmt|;
DECL|method|BooleanOption (boolean value)
specifier|protected
name|BooleanOption
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|boolean
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|IntegerOption
specifier|public
specifier|static
specifier|abstract
class|class
name|IntegerOption
block|{
DECL|field|value
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
DECL|method|IntegerOption (int value)
specifier|protected
name|IntegerOption
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|LongOption
specifier|public
specifier|static
specifier|abstract
class|class
name|LongOption
block|{
DECL|field|value
specifier|private
specifier|final
name|long
name|value
decl_stmt|;
DECL|method|LongOption (long value)
specifier|protected
name|LongOption
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|PathOption
specifier|public
specifier|static
specifier|abstract
class|class
name|PathOption
block|{
DECL|field|value
specifier|private
specifier|final
name|Path
name|value
decl_stmt|;
DECL|method|PathOption (Path value)
specifier|protected
name|PathOption
parameter_list|(
name|Path
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|Path
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|FSDataInputStreamOption
specifier|public
specifier|static
specifier|abstract
class|class
name|FSDataInputStreamOption
block|{
DECL|field|value
specifier|private
specifier|final
name|FSDataInputStream
name|value
decl_stmt|;
DECL|method|FSDataInputStreamOption (FSDataInputStream value)
specifier|protected
name|FSDataInputStreamOption
parameter_list|(
name|FSDataInputStream
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|FSDataInputStream
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|FSDataOutputStreamOption
specifier|public
specifier|static
specifier|abstract
class|class
name|FSDataOutputStreamOption
block|{
DECL|field|value
specifier|private
specifier|final
name|FSDataOutputStream
name|value
decl_stmt|;
DECL|method|FSDataOutputStreamOption (FSDataOutputStream value)
specifier|protected
name|FSDataOutputStreamOption
parameter_list|(
name|FSDataOutputStream
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|FSDataOutputStream
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
DECL|class|ProgressableOption
specifier|public
specifier|static
specifier|abstract
class|class
name|ProgressableOption
block|{
DECL|field|value
specifier|private
specifier|final
name|Progressable
name|value
decl_stmt|;
DECL|method|ProgressableOption (Progressable value)
specifier|protected
name|ProgressableOption
parameter_list|(
name|Progressable
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|Progressable
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
comment|/**    * Find the first option of the required class.    * @param<T> the static class to find    * @param<base> the parent class of the array    * @param cls the dynamic class to find    * @param opts the list of options to look through    * @return the first option that matches    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getOption (Class<T> cls, base [] opts )
specifier|public
specifier|static
parameter_list|<
name|base
parameter_list|,
name|T
extends|extends
name|base
parameter_list|>
name|T
name|getOption
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|cls
parameter_list|,
name|base
index|[]
name|opts
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|base
name|o
range|:
name|opts
control|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|==
name|cls
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|o
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Prepend some new options to the old options    * @param<T> the type of options    * @param oldOpts the old options    * @param newOpts the new options    * @return a new array of options    */
DECL|method|prependOptions (T[] oldOpts, T... newOpts)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|prependOptions
parameter_list|(
name|T
index|[]
name|oldOpts
parameter_list|,
name|T
modifier|...
name|newOpts
parameter_list|)
block|{
comment|// copy the new options to the front of the array
name|T
index|[]
name|result
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|newOpts
argument_list|,
name|newOpts
operator|.
name|length
operator|+
name|oldOpts
operator|.
name|length
argument_list|)
decl_stmt|;
comment|// now copy the old options
name|System
operator|.
name|arraycopy
argument_list|(
name|oldOpts
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
name|newOpts
operator|.
name|length
argument_list|,
name|oldOpts
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

