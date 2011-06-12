begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|join
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|util
operator|.
name|HashSet
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
name|conf
operator|.
name|Configuration
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
name|Text
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
name|Writable
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
name|WritableUtils
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
name|serializer
operator|.
name|*
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
name|mapreduce
operator|.
name|InputSplit
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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**  * This InputSplit contains a set of child InputSplits. Any InputSplit inserted  * into this collection must have a public default constructor.  */
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
DECL|class|CompositeInputSplit
specifier|public
class|class
name|CompositeInputSplit
extends|extends
name|InputSplit
implements|implements
name|Writable
block|{
DECL|field|fill
specifier|private
name|int
name|fill
init|=
literal|0
decl_stmt|;
DECL|field|totsize
specifier|private
name|long
name|totsize
init|=
literal|0L
decl_stmt|;
DECL|field|splits
specifier|private
name|InputSplit
index|[]
name|splits
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|CompositeInputSplit ()
specifier|public
name|CompositeInputSplit
parameter_list|()
block|{ }
DECL|method|CompositeInputSplit (int capacity)
specifier|public
name|CompositeInputSplit
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|splits
operator|=
operator|new
name|InputSplit
index|[
name|capacity
index|]
expr_stmt|;
block|}
comment|/**    * Add an InputSplit to this collection.    * @throws IOException If capacity was not specified during construction    *                     or if capacity has been reached.    */
DECL|method|add (InputSplit s)
specifier|public
name|void
name|add
parameter_list|(
name|InputSplit
name|s
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
literal|null
operator|==
name|splits
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Uninitialized InputSplit"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fill
operator|==
name|splits
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Too many splits"
argument_list|)
throw|;
block|}
name|splits
index|[
name|fill
operator|++
index|]
operator|=
name|s
expr_stmt|;
name|totsize
operator|+=
name|s
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get ith child InputSplit.    */
DECL|method|get (int i)
specifier|public
name|InputSplit
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|splits
index|[
name|i
index|]
return|;
block|}
comment|/**    * Return the aggregate length of all child InputSplits currently added.    */
DECL|method|getLength ()
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|totsize
return|;
block|}
comment|/**    * Get the length of ith child InputSplit.    */
DECL|method|getLength (int i)
specifier|public
name|long
name|getLength
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|splits
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
return|;
block|}
comment|/**    * Collect a set of hosts from all child InputSplits.    */
DECL|method|getLocations ()
specifier|public
name|String
index|[]
name|getLocations
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|hosts
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|InputSplit
name|s
range|:
name|splits
control|)
block|{
name|String
index|[]
name|hints
init|=
name|s
operator|.
name|getLocations
argument_list|()
decl_stmt|;
if|if
condition|(
name|hints
operator|!=
literal|null
operator|&&
name|hints
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|host
range|:
name|hints
control|)
block|{
name|hosts
operator|.
name|add
argument_list|(
name|host
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|hosts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|hosts
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * getLocations from ith InputSplit.    */
DECL|method|getLocation (int i)
specifier|public
name|String
index|[]
name|getLocation
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|splits
index|[
name|i
index|]
operator|.
name|getLocations
argument_list|()
return|;
block|}
comment|/**    * Write splits in the following format.    * {@code    *<count><class1><class2>...<classn><split1><split2>...<splitn>    * }    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|InputSplit
name|s
range|:
name|splits
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|s
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|InputSplit
name|s
range|:
name|splits
control|)
block|{
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Serializer
name|serializer
init|=
name|factory
operator|.
name|getSerializer
argument_list|(
name|s
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|open
argument_list|(
operator|(
name|DataOutputStream
operator|)
name|out
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    * @throws IOException If the child InputSplit cannot be read, typically    *                     for failing access checks.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// Generic array assignment
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|card
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|splits
operator|==
literal|null
operator|||
name|splits
operator|.
name|length
operator|!=
name|card
condition|)
block|{
name|splits
operator|=
operator|new
name|InputSplit
index|[
name|card
index|]
expr_stmt|;
block|}
name|Class
argument_list|<
name|?
extends|extends
name|InputSplit
argument_list|>
index|[]
name|cls
init|=
operator|new
name|Class
index|[
name|card
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|card
condition|;
operator|++
name|i
control|)
block|{
name|cls
index|[
name|i
index|]
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
operator|.
name|asSubclass
argument_list|(
name|InputSplit
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|card
condition|;
operator|++
name|i
control|)
block|{
name|splits
index|[
name|i
index|]
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|cls
index|[
name|i
index|]
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|SerializationFactory
name|factory
init|=
operator|new
name|SerializationFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Deserializer
name|deserializer
init|=
name|factory
operator|.
name|getDeserializer
argument_list|(
name|cls
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|deserializer
operator|.
name|open
argument_list|(
operator|(
name|DataInputStream
operator|)
name|in
argument_list|)
expr_stmt|;
name|splits
index|[
name|i
index|]
operator|=
operator|(
name|InputSplit
operator|)
name|deserializer
operator|.
name|deserialize
argument_list|(
name|splits
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed split init"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

