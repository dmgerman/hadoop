begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|GenerationStamp
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
name|*
import|;
end_import

begin_comment
comment|/**************************************************  * A Block is a Hadoop FS primitive, identified by a   * long.  *  **************************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Block
specifier|public
class|class
name|Block
implements|implements
name|Writable
implements|,
name|Comparable
argument_list|<
name|Block
argument_list|>
block|{
DECL|field|BLOCK_FILE_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|BLOCK_FILE_PREFIX
init|=
literal|"blk_"
decl_stmt|;
DECL|field|METADATA_EXTENSION
specifier|public
specifier|static
specifier|final
name|String
name|METADATA_EXTENSION
init|=
literal|".meta"
decl_stmt|;
static|static
block|{
comment|// register a ctor
name|WritableFactories
operator|.
name|setFactory
argument_list|(
name|Block
operator|.
name|class
argument_list|,
operator|new
name|WritableFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Writable
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|Block
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|field|blockFilePattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|blockFilePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|BLOCK_FILE_PREFIX
operator|+
literal|"(-??\\d++)$"
argument_list|)
decl_stmt|;
DECL|field|metaFilePattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|metaFilePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|BLOCK_FILE_PREFIX
operator|+
literal|"(-??\\d++)_(\\d++)\\"
operator|+
name|METADATA_EXTENSION
operator|+
literal|"$"
argument_list|)
decl_stmt|;
DECL|field|metaOrBlockFilePattern
specifier|public
specifier|static
specifier|final
name|Pattern
name|metaOrBlockFilePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|BLOCK_FILE_PREFIX
operator|+
literal|"(-??\\d++)(_(\\d++)\\"
operator|+
name|METADATA_EXTENSION
operator|+
literal|")?$"
argument_list|)
decl_stmt|;
DECL|method|isBlockFilename (File f)
specifier|public
specifier|static
name|boolean
name|isBlockFilename
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|String
name|name
init|=
name|f
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|blockFilePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|filename2id (String name)
specifier|public
specifier|static
name|long
name|filename2id
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|blockFilePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|m
operator|.
name|matches
argument_list|()
condition|?
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
else|:
literal|0
return|;
block|}
DECL|method|isMetaFilename (String name)
specifier|public
specifier|static
name|boolean
name|isMetaFilename
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|metaFilePattern
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
DECL|method|metaToBlockFile (File metaFile)
specifier|public
specifier|static
name|File
name|metaToBlockFile
parameter_list|(
name|File
name|metaFile
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|metaFile
operator|.
name|getParent
argument_list|()
argument_list|,
name|metaFile
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|metaFile
operator|.
name|getName
argument_list|()
operator|.
name|lastIndexOf
argument_list|(
literal|'_'
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get generation stamp from the name of the metafile name    */
DECL|method|getGenerationStamp (String metaFile)
specifier|public
specifier|static
name|long
name|getGenerationStamp
parameter_list|(
name|String
name|metaFile
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|metaFilePattern
operator|.
name|matcher
argument_list|(
name|metaFile
argument_list|)
decl_stmt|;
return|return
name|m
operator|.
name|matches
argument_list|()
condition|?
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
else|:
name|GenerationStamp
operator|.
name|GRANDFATHER_GENERATION_STAMP
return|;
block|}
comment|/**    * Get the blockId from the name of the meta or block file    */
DECL|method|getBlockId (String metaOrBlockFile)
specifier|public
specifier|static
name|long
name|getBlockId
parameter_list|(
name|String
name|metaOrBlockFile
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|metaOrBlockFilePattern
operator|.
name|matcher
argument_list|(
name|metaOrBlockFile
argument_list|)
decl_stmt|;
return|return
name|m
operator|.
name|matches
argument_list|()
condition|?
name|Long
operator|.
name|parseLong
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
else|:
literal|0
return|;
block|}
DECL|field|blockId
specifier|private
name|long
name|blockId
decl_stmt|;
DECL|field|numBytes
specifier|private
name|long
name|numBytes
decl_stmt|;
DECL|field|generationStamp
specifier|private
name|long
name|generationStamp
decl_stmt|;
DECL|method|Block ()
specifier|public
name|Block
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|Block (final long blkid, final long len, final long generationStamp)
specifier|public
name|Block
parameter_list|(
specifier|final
name|long
name|blkid
parameter_list|,
specifier|final
name|long
name|len
parameter_list|,
specifier|final
name|long
name|generationStamp
parameter_list|)
block|{
name|set
argument_list|(
name|blkid
argument_list|,
name|len
argument_list|,
name|generationStamp
argument_list|)
expr_stmt|;
block|}
DECL|method|Block (final long blkid)
specifier|public
name|Block
parameter_list|(
specifier|final
name|long
name|blkid
parameter_list|)
block|{
name|this
argument_list|(
name|blkid
argument_list|,
literal|0
argument_list|,
name|GenerationStamp
operator|.
name|GRANDFATHER_GENERATION_STAMP
argument_list|)
expr_stmt|;
block|}
DECL|method|Block (Block blk)
specifier|public
name|Block
parameter_list|(
name|Block
name|blk
parameter_list|)
block|{
name|this
argument_list|(
name|blk
operator|.
name|blockId
argument_list|,
name|blk
operator|.
name|numBytes
argument_list|,
name|blk
operator|.
name|generationStamp
argument_list|)
expr_stmt|;
block|}
comment|/**    * Find the blockid from the given filename    */
DECL|method|Block (File f, long len, long genstamp)
specifier|public
name|Block
parameter_list|(
name|File
name|f
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genstamp
parameter_list|)
block|{
name|this
argument_list|(
name|filename2id
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|len
argument_list|,
name|genstamp
argument_list|)
expr_stmt|;
block|}
DECL|method|set (long blkid, long len, long genStamp)
specifier|public
name|void
name|set
parameter_list|(
name|long
name|blkid
parameter_list|,
name|long
name|len
parameter_list|,
name|long
name|genStamp
parameter_list|)
block|{
name|this
operator|.
name|blockId
operator|=
name|blkid
expr_stmt|;
name|this
operator|.
name|numBytes
operator|=
name|len
expr_stmt|;
name|this
operator|.
name|generationStamp
operator|=
name|genStamp
expr_stmt|;
block|}
comment|/**    */
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
block|{
return|return
name|blockId
return|;
block|}
DECL|method|setBlockId (long bid)
specifier|public
name|void
name|setBlockId
parameter_list|(
name|long
name|bid
parameter_list|)
block|{
name|blockId
operator|=
name|bid
expr_stmt|;
block|}
comment|/**    */
DECL|method|getBlockName ()
specifier|public
name|String
name|getBlockName
parameter_list|()
block|{
return|return
name|BLOCK_FILE_PREFIX
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|blockId
argument_list|)
return|;
block|}
comment|/**    */
DECL|method|getNumBytes ()
specifier|public
name|long
name|getNumBytes
parameter_list|()
block|{
return|return
name|numBytes
return|;
block|}
DECL|method|setNumBytes (long len)
specifier|public
name|void
name|setNumBytes
parameter_list|(
name|long
name|len
parameter_list|)
block|{
name|this
operator|.
name|numBytes
operator|=
name|len
expr_stmt|;
block|}
DECL|method|getGenerationStamp ()
specifier|public
name|long
name|getGenerationStamp
parameter_list|()
block|{
return|return
name|generationStamp
return|;
block|}
DECL|method|setGenerationStamp (long stamp)
specifier|public
name|void
name|setGenerationStamp
parameter_list|(
name|long
name|stamp
parameter_list|)
block|{
name|generationStamp
operator|=
name|stamp
expr_stmt|;
block|}
comment|/**    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getBlockName
argument_list|()
operator|+
literal|"_"
operator|+
name|getGenerationStamp
argument_list|()
return|;
block|}
DECL|method|appendStringTo (StringBuilder sb)
specifier|public
name|void
name|appendStringTo
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|BLOCK_FILE_PREFIX
argument_list|)
operator|.
name|append
argument_list|(
name|blockId
argument_list|)
operator|.
name|append
argument_list|(
literal|"_"
argument_list|)
operator|.
name|append
argument_list|(
name|getGenerationStamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/////////////////////////////////////
comment|// Writable
comment|/////////////////////////////////////
annotation|@
name|Override
comment|// Writable
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
name|writeHelper
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
comment|// Writable
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
name|readHelper
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|writeHelper (DataOutput out)
specifier|final
name|void
name|writeHelper
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
name|blockId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|numBytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|generationStamp
argument_list|)
expr_stmt|;
block|}
DECL|method|readHelper (DataInput in)
specifier|final
name|void
name|readHelper
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|blockId
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|numBytes
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|generationStamp
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|numBytes
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected block size: "
operator|+
name|numBytes
argument_list|)
throw|;
block|}
block|}
comment|// write only the identifier part of the block
DECL|method|writeId (DataOutput out)
specifier|public
name|void
name|writeId
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
name|blockId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|generationStamp
argument_list|)
expr_stmt|;
block|}
comment|// Read only the identifier part of the block
DECL|method|readId (DataInput in)
specifier|public
name|void
name|readId
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|blockId
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|generationStamp
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
comment|// Comparable
DECL|method|compareTo (Block b)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
return|return
name|blockId
operator|<
name|b
operator|.
name|blockId
condition|?
operator|-
literal|1
else|:
name|blockId
operator|>
name|b
operator|.
name|blockId
condition|?
literal|1
else|:
literal|0
return|;
block|}
annotation|@
name|Override
comment|// Object
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
name|Block
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|Block
operator|)
name|o
argument_list|)
operator|==
literal|0
return|;
block|}
comment|/**    * @return true if the two blocks have the same block ID and the same    * generation stamp, or if both blocks are null.    */
DECL|method|matchingIdAndGenStamp (Block a, Block b)
specifier|public
specifier|static
name|boolean
name|matchingIdAndGenStamp
parameter_list|(
name|Block
name|a
parameter_list|,
name|Block
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
name|b
condition|)
return|return
literal|true
return|;
comment|// same block, or both null
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// only one null
return|return
name|a
operator|.
name|blockId
operator|==
name|b
operator|.
name|blockId
operator|&&
name|a
operator|.
name|generationStamp
operator|==
name|b
operator|.
name|generationStamp
return|;
block|}
annotation|@
name|Override
comment|// Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|//GenerationStamp is IRRELEVANT and should not be used here
return|return
call|(
name|int
call|)
argument_list|(
name|blockId
operator|^
operator|(
name|blockId
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

