begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|mapred
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * This class represents the metadata of a shard. Version is the version number  * of the entire index. Directory is the directory where this shard resides in.  * Generation is the Lucene index's generation. Version and generation are  * reserved for future use.  *   * Note: Currently the version number of the entire index is not used and  * defaults to -1.  */
end_comment

begin_class
DECL|class|Shard
specifier|public
class|class
name|Shard
implements|implements
name|WritableComparable
block|{
comment|// This method is copied from Path.
DECL|method|normalizePath (String path)
specifier|public
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// remove double slashes& backslashes
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|"//"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
comment|// trim trailing slash from non-root path (ignoring windows drive)
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
DECL|method|setIndexShards (IndexUpdateConfiguration conf, Shard[] shards)
specifier|public
specifier|static
name|void
name|setIndexShards
parameter_list|(
name|IndexUpdateConfiguration
name|conf
parameter_list|,
name|Shard
index|[]
name|shards
parameter_list|)
block|{
name|StringBuilder
name|shardsString
init|=
operator|new
name|StringBuilder
argument_list|(
name|shards
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shardsString
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|shardsString
operator|.
name|append
argument_list|(
name|shards
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setIndexShards
argument_list|(
name|shardsString
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexShards (IndexUpdateConfiguration conf)
specifier|public
specifier|static
name|Shard
index|[]
name|getIndexShards
parameter_list|(
name|IndexUpdateConfiguration
name|conf
parameter_list|)
block|{
name|String
name|shards
init|=
name|conf
operator|.
name|getIndexShards
argument_list|()
decl_stmt|;
if|if
condition|(
name|shards
operator|!=
literal|null
condition|)
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|list
init|=
name|Collections
operator|.
name|list
argument_list|(
operator|new
name|StringTokenizer
argument_list|(
name|shards
argument_list|,
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|Shard
index|[]
name|result
init|=
operator|new
name|Shard
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
index|[
name|i
index|]
operator|=
name|Shard
operator|.
name|createShardFromString
argument_list|(
operator|(
name|String
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// assume str is formatted correctly as a shard string
DECL|method|createShardFromString (String str)
specifier|private
specifier|static
name|Shard
name|createShardFromString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|int
name|first
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|"@"
argument_list|)
decl_stmt|;
name|int
name|second
init|=
name|str
operator|.
name|indexOf
argument_list|(
literal|"@"
argument_list|,
name|first
operator|+
literal|1
argument_list|)
decl_stmt|;
name|long
name|version
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|first
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|dir
init|=
name|str
operator|.
name|substring
argument_list|(
name|first
operator|+
literal|1
argument_list|,
name|second
argument_list|)
decl_stmt|;
name|long
name|gen
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|second
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Shard
argument_list|(
name|version
argument_list|,
name|dir
argument_list|,
name|gen
argument_list|)
return|;
block|}
comment|// index/shard version
comment|// the shards in the same version of an index have the same version number
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|field|dir
specifier|private
name|String
name|dir
decl_stmt|;
DECL|field|gen
specifier|private
name|long
name|gen
decl_stmt|;
comment|// Lucene's generation
comment|/**    * Constructor.    */
DECL|method|Shard ()
specifier|public
name|Shard
parameter_list|()
block|{
name|this
operator|.
name|version
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|dir
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|gen
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Construct a shard from a versio number, a directory and a generation    * number.    * @param version  the version number of the entire index    * @param dir  the directory where this shard resides    * @param gen  the generation of the Lucene instance    */
DECL|method|Shard (long version, String dir, long gen)
specifier|public
name|Shard
parameter_list|(
name|long
name|version
parameter_list|,
name|String
name|dir
parameter_list|,
name|long
name|gen
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|normalizePath
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|this
operator|.
name|gen
operator|=
name|gen
expr_stmt|;
block|}
comment|/**    * Construct using a shard object.    * @param shard  the shard used by the constructor    */
DECL|method|Shard (Shard shard)
specifier|public
name|Shard
parameter_list|(
name|Shard
name|shard
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|shard
operator|.
name|version
expr_stmt|;
name|this
operator|.
name|dir
operator|=
name|shard
operator|.
name|dir
expr_stmt|;
name|this
operator|.
name|gen
operator|=
name|shard
operator|.
name|gen
expr_stmt|;
block|}
comment|/**    * Get the version number of the entire index.    * @return the version number of the entire index    */
DECL|method|getVersion ()
specifier|public
name|long
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**    * Get the directory where this shard resides.    * @return the directory where this shard resides    */
DECL|method|getDirectory ()
specifier|public
name|String
name|getDirectory
parameter_list|()
block|{
return|return
name|dir
return|;
block|}
comment|/**    * Get the generation of the Lucene instance.    * @return the generation of the Lucene instance    */
DECL|method|getGeneration ()
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|gen
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#toString()    */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|version
operator|+
literal|"@"
operator|+
name|dir
operator|+
literal|"@"
operator|+
name|gen
return|;
block|}
comment|// ///////////////////////////////////
comment|// Writable
comment|// ///////////////////////////////////
comment|/* (non-Javadoc)    * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)    */
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
name|out
operator|.
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|gen
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)    */
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
name|version
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|dir
operator|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|gen
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
comment|// ///////////////////////////////////
comment|// Comparable
comment|// ///////////////////////////////////
comment|/* (non-Javadoc)    * @see java.lang.Comparable#compareTo(java.lang.Object)    */
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|Shard
operator|)
name|o
argument_list|)
return|;
block|}
comment|/**    * Compare to another shard.    * @param other  another shard    * @return compare version first, then directory and finally generation    */
DECL|method|compareTo (Shard other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Shard
name|other
parameter_list|)
block|{
comment|// compare version
if|if
condition|(
name|version
operator|<
name|other
operator|.
name|version
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|version
operator|>
name|other
operator|.
name|version
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// compare dir
name|int
name|result
init|=
name|dir
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
comment|// compare gen
if|if
condition|(
name|gen
operator|<
name|other
operator|.
name|gen
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|gen
operator|==
name|other
operator|.
name|gen
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#equals(java.lang.Object)    */
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
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Shard
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Shard
name|other
init|=
operator|(
name|Shard
operator|)
name|o
decl_stmt|;
return|return
name|version
operator|==
name|other
operator|.
name|version
operator|&&
name|dir
operator|.
name|equals
argument_list|(
name|other
operator|.
name|dir
argument_list|)
operator|&&
name|gen
operator|==
name|other
operator|.
name|gen
return|;
block|}
comment|/* (non-Javadoc)    * @see java.lang.Object#hashCode()    */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|version
operator|^
name|dir
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|int
operator|)
name|gen
return|;
block|}
block|}
end_class

end_unit

