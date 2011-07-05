begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|InputStream
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Holds file metadata including type (regular file, or directory),  * and the list of blocks that are pointers to the data.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|INode
specifier|public
class|class
name|INode
block|{
DECL|enum|FileType
enum|enum
name|FileType
block|{
DECL|enumConstant|DIRECTORY
DECL|enumConstant|FILE
name|DIRECTORY
block|,
name|FILE
block|}
DECL|field|FILE_TYPES
specifier|public
specifier|static
specifier|final
name|FileType
index|[]
name|FILE_TYPES
init|=
block|{
name|FileType
operator|.
name|DIRECTORY
block|,
name|FileType
operator|.
name|FILE
block|}
decl_stmt|;
DECL|field|DIRECTORY_INODE
specifier|public
specifier|static
specifier|final
name|INode
name|DIRECTORY_INODE
init|=
operator|new
name|INode
argument_list|(
name|FileType
operator|.
name|DIRECTORY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|field|fileType
specifier|private
name|FileType
name|fileType
decl_stmt|;
DECL|field|blocks
specifier|private
name|Block
index|[]
name|blocks
decl_stmt|;
DECL|method|INode (FileType fileType, Block[] blocks)
specifier|public
name|INode
parameter_list|(
name|FileType
name|fileType
parameter_list|,
name|Block
index|[]
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|fileType
operator|=
name|fileType
expr_stmt|;
if|if
condition|(
name|isDirectory
argument_list|()
operator|&&
name|blocks
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"A directory cannot contain blocks."
argument_list|)
throw|;
block|}
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
block|}
DECL|method|getBlocks ()
specifier|public
name|Block
index|[]
name|getBlocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|getFileType ()
specifier|public
name|FileType
name|getFileType
parameter_list|()
block|{
return|return
name|fileType
return|;
block|}
DECL|method|isDirectory ()
specifier|public
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
name|fileType
operator|==
name|FileType
operator|.
name|DIRECTORY
return|;
block|}
DECL|method|isFile ()
specifier|public
name|boolean
name|isFile
parameter_list|()
block|{
return|return
name|fileType
operator|==
name|FileType
operator|.
name|FILE
return|;
block|}
DECL|method|getSerializedLength ()
specifier|public
name|long
name|getSerializedLength
parameter_list|()
block|{
return|return
literal|1L
operator|+
operator|(
name|blocks
operator|==
literal|null
condition|?
literal|0
else|:
literal|4
operator|+
name|blocks
operator|.
name|length
operator|*
literal|16
operator|)
return|;
block|}
DECL|method|serialize ()
specifier|public
name|InputStream
name|serialize
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|bytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|out
init|=
operator|new
name|DataOutputStream
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|fileType
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isFile
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|blocks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|blocks
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
DECL|method|deserialize (InputStream in)
specifier|public
specifier|static
name|INode
name|deserialize
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|DataInputStream
name|dataIn
init|=
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|FileType
name|fileType
init|=
name|INode
operator|.
name|FILE_TYPES
index|[
name|dataIn
operator|.
name|readByte
argument_list|()
index|]
decl_stmt|;
switch|switch
condition|(
name|fileType
condition|)
block|{
case|case
name|DIRECTORY
case|:
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|INode
operator|.
name|DIRECTORY_INODE
return|;
case|case
name|FILE
case|:
name|int
name|numBlocks
init|=
name|dataIn
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|Block
index|[]
name|blocks
init|=
operator|new
name|Block
index|[
name|numBlocks
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
name|numBlocks
condition|;
name|i
operator|++
control|)
block|{
name|long
name|id
init|=
name|dataIn
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|length
init|=
name|dataIn
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|Block
argument_list|(
name|id
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|INode
argument_list|(
name|fileType
argument_list|,
name|blocks
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot deserialize inode."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

