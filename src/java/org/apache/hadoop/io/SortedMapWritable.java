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
name|DataInput
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
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
comment|/**  * A Writable SortedMap.  */
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
DECL|class|SortedMapWritable
specifier|public
class|class
name|SortedMapWritable
extends|extends
name|AbstractMapWritable
implements|implements
name|SortedMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
block|{
DECL|field|instance
specifier|private
name|SortedMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
name|instance
decl_stmt|;
comment|/** default constructor. */
DECL|method|SortedMapWritable ()
specifier|public
name|SortedMapWritable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|instance
operator|=
operator|new
name|TreeMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Copy constructor.    *     * @param other the map to copy from    */
DECL|method|SortedMapWritable (SortedMapWritable other)
specifier|public
name|SortedMapWritable
parameter_list|(
name|SortedMapWritable
name|other
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|copy
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|comparator ()
specifier|public
name|Comparator
argument_list|<
name|?
super|super
name|WritableComparable
argument_list|>
name|comparator
parameter_list|()
block|{
comment|// Returning null means we use the natural ordering of the keys
return|return
literal|null
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|firstKey ()
specifier|public
name|WritableComparable
name|firstKey
parameter_list|()
block|{
return|return
name|instance
operator|.
name|firstKey
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
specifier|public
name|SortedMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
DECL|method|headMap (WritableComparable toKey)
name|headMap
parameter_list|(
name|WritableComparable
name|toKey
parameter_list|)
block|{
return|return
name|instance
operator|.
name|headMap
argument_list|(
name|toKey
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|lastKey ()
specifier|public
name|WritableComparable
name|lastKey
parameter_list|()
block|{
return|return
name|instance
operator|.
name|lastKey
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
specifier|public
name|SortedMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
DECL|method|subMap (WritableComparable fromKey, WritableComparable toKey)
name|subMap
parameter_list|(
name|WritableComparable
name|fromKey
parameter_list|,
name|WritableComparable
name|toKey
parameter_list|)
block|{
return|return
name|instance
operator|.
name|subMap
argument_list|(
name|fromKey
argument_list|,
name|toKey
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
specifier|public
name|SortedMap
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
DECL|method|tailMap (WritableComparable fromKey)
name|tailMap
parameter_list|(
name|WritableComparable
name|fromKey
parameter_list|)
block|{
return|return
name|instance
operator|.
name|tailMap
argument_list|(
name|fromKey
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|clear ()
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|instance
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|containsKey (Object key)
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|instance
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|containsValue (Object value)
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|instance
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|entrySet ()
specifier|public
name|Set
argument_list|<
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|instance
operator|.
name|entrySet
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|get (Object key)
specifier|public
name|Writable
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|instance
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|instance
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|keySet ()
specifier|public
name|Set
argument_list|<
name|WritableComparable
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|instance
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|put (WritableComparable key, Writable value)
specifier|public
name|Writable
name|put
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|)
block|{
name|addToMap
argument_list|(
name|key
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|addToMap
argument_list|(
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|instance
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|putAll (Map<? extends WritableComparable, ? extends Writable> t)
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|,
name|?
extends|extends
name|Writable
argument_list|>
name|t
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|,
name|?
extends|extends
name|Writable
argument_list|>
name|e
range|:
name|t
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|instance
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** {@inheritDoc} */
DECL|method|remove (Object key)
specifier|public
name|Writable
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|instance
operator|.
name|remove
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|instance
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|values ()
specifier|public
name|Collection
argument_list|<
name|Writable
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|instance
operator|.
name|values
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
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
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Read the number of entries in the map
name|int
name|entries
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// Then read each key/value pair
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entries
condition|;
name|i
operator|++
control|)
block|{
name|WritableComparable
name|key
init|=
operator|(
name|WritableComparable
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|getClass
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|key
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Writable
name|value
init|=
operator|(
name|Writable
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|getClass
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|instance
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
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
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// Write out the number of entries in the map
name|out
operator|.
name|writeInt
argument_list|(
name|instance
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Then write out each key/value pair
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
name|e
range|:
name|instance
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|getId
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|getId
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

