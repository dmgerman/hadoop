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
name|fs
operator|.
name|FileSystem
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
comment|/** A file-based set of keys. */
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
DECL|class|SetFile
specifier|public
class|class
name|SetFile
extends|extends
name|MapFile
block|{
DECL|method|SetFile ()
specifier|protected
name|SetFile
parameter_list|()
block|{}
comment|// no public ctor
comment|/**     * Write a new set file.    */
DECL|class|Writer
specifier|public
specifier|static
class|class
name|Writer
extends|extends
name|MapFile
operator|.
name|Writer
block|{
comment|/** Create the named set for keys of the named class.       *  @deprecated pass a Configuration too      */
DECL|method|Writer (FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass)
specifier|public
name|Writer
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
name|fs
argument_list|,
name|dirName
argument_list|,
name|keyClass
argument_list|,
name|NullWritable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Create a set naming the element class and compression type. */
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, Class<? extends WritableComparable> keyClass, SequenceFile.CompressionType compress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|WritableComparable
argument_list|>
name|keyClass
parameter_list|,
name|SequenceFile
operator|.
name|CompressionType
name|compress
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|conf
argument_list|,
name|fs
argument_list|,
name|dirName
argument_list|,
name|WritableComparator
operator|.
name|get
argument_list|(
name|keyClass
argument_list|)
argument_list|,
name|compress
argument_list|)
expr_stmt|;
block|}
comment|/** Create a set naming the element comparator and compression type. */
DECL|method|Writer (Configuration conf, FileSystem fs, String dirName, WritableComparator comparator, SequenceFile.CompressionType compress)
specifier|public
name|Writer
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|SequenceFile
operator|.
name|CompressionType
name|compress
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|,
name|valueClass
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|)
argument_list|,
name|compression
argument_list|(
name|compress
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Append a key to a set.  The key must be strictly greater than the      * previous key added to the set. */
DECL|method|append (WritableComparable key)
specifier|public
name|void
name|append
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|append
argument_list|(
name|key
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Provide access to an existing set file. */
DECL|class|Reader
specifier|public
specifier|static
class|class
name|Reader
extends|extends
name|MapFile
operator|.
name|Reader
block|{
comment|/** Construct a set reader for the named set.*/
DECL|method|Reader (FileSystem fs, String dirName, Configuration conf)
specifier|public
name|Reader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|fs
argument_list|,
name|dirName
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Construct a set reader for the named set using the named comparator.*/
DECL|method|Reader (FileSystem fs, String dirName, WritableComparator comparator, Configuration conf)
specifier|public
name|Reader
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|dirName
parameter_list|,
name|WritableComparator
name|comparator
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|Path
argument_list|(
name|dirName
argument_list|)
argument_list|,
name|conf
argument_list|,
name|comparator
argument_list|(
name|comparator
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// javadoc inherited
DECL|method|seek (WritableComparable key)
specifier|public
name|boolean
name|seek
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|seek
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** Read the next key in a set into<code>key</code>.  Returns      * true if such a key exists and false when at the end of the set. */
DECL|method|next (WritableComparable key)
specifier|public
name|boolean
name|next
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
argument_list|(
name|key
argument_list|,
name|NullWritable
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|/** Read the matching key from a set into<code>key</code>.      * Returns<code>key</code>, or null if no match exists. */
DECL|method|get (WritableComparable key)
specifier|public
name|WritableComparable
name|get
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|seek
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
name|key
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

