begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|fs
operator|.
name|Options
operator|.
name|ChecksumOpt
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
name|MD5Hash
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|znerd
operator|.
name|xmlenc
operator|.
name|XMLOutputter
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
name|MD5MD5CRC32CastagnoliFileChecksum
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
name|MD5MD5CRC32GzipFileChecksum
import|;
end_import

begin_comment
comment|/** MD5 of MD5 of CRC32. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|MD5MD5CRC32FileChecksum
specifier|public
class|class
name|MD5MD5CRC32FileChecksum
extends|extends
name|FileChecksum
block|{
DECL|field|LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH
init|=
name|MD5Hash
operator|.
name|MD5_LEN
operator|+
operator|(
name|Integer
operator|.
name|SIZE
operator|+
name|Long
operator|.
name|SIZE
operator|)
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
DECL|field|bytesPerCRC
specifier|private
name|int
name|bytesPerCRC
decl_stmt|;
DECL|field|crcPerBlock
specifier|private
name|long
name|crcPerBlock
decl_stmt|;
DECL|field|md5
specifier|private
name|MD5Hash
name|md5
decl_stmt|;
comment|/** Same as this(0, 0, null) */
DECL|method|MD5MD5CRC32FileChecksum ()
specifier|public
name|MD5MD5CRC32FileChecksum
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Create a MD5FileChecksum */
DECL|method|MD5MD5CRC32FileChecksum (int bytesPerCRC, long crcPerBlock, MD5Hash md5)
specifier|public
name|MD5MD5CRC32FileChecksum
parameter_list|(
name|int
name|bytesPerCRC
parameter_list|,
name|long
name|crcPerBlock
parameter_list|,
name|MD5Hash
name|md5
parameter_list|)
block|{
name|this
operator|.
name|bytesPerCRC
operator|=
name|bytesPerCRC
expr_stmt|;
name|this
operator|.
name|crcPerBlock
operator|=
name|crcPerBlock
expr_stmt|;
name|this
operator|.
name|md5
operator|=
name|md5
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAlgorithmName ()
specifier|public
name|String
name|getAlgorithmName
parameter_list|()
block|{
return|return
literal|"MD5-of-"
operator|+
name|crcPerBlock
operator|+
literal|"MD5-of-"
operator|+
name|bytesPerCRC
operator|+
name|getCrcType
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|getCrcTypeFromAlgorithmName (String algorithm)
specifier|public
specifier|static
name|DataChecksum
operator|.
name|Type
name|getCrcTypeFromAlgorithmName
parameter_list|(
name|String
name|algorithm
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|algorithm
operator|.
name|endsWith
argument_list|(
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
elseif|else
if|if
condition|(
name|algorithm
operator|.
name|endsWith
argument_list|(
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32C
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown checksum type in "
operator|+
name|algorithm
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|LENGTH
return|;
block|}
annotation|@
name|Override
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|WritableUtils
operator|.
name|toByteArray
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** returns the CRC type */
DECL|method|getCrcType ()
specifier|public
name|DataChecksum
operator|.
name|Type
name|getCrcType
parameter_list|()
block|{
comment|// default to the one that is understood by all releases.
return|return
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
return|;
block|}
annotation|@
name|Override
DECL|method|getChecksumOpt ()
specifier|public
name|ChecksumOpt
name|getChecksumOpt
parameter_list|()
block|{
return|return
operator|new
name|ChecksumOpt
argument_list|(
name|getCrcType
argument_list|()
argument_list|,
name|bytesPerCRC
argument_list|)
return|;
block|}
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
name|bytesPerCRC
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|crcPerBlock
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|md5
operator|=
name|MD5Hash
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|out
operator|.
name|writeInt
argument_list|(
name|bytesPerCRC
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|crcPerBlock
argument_list|)
expr_stmt|;
name|md5
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** Write that object to xml output. */
DECL|method|write (XMLOutputter xml, MD5MD5CRC32FileChecksum that )
specifier|public
specifier|static
name|void
name|write
parameter_list|(
name|XMLOutputter
name|xml
parameter_list|,
name|MD5MD5CRC32FileChecksum
name|that
parameter_list|)
throws|throws
name|IOException
block|{
name|xml
operator|.
name|startTag
argument_list|(
name|MD5MD5CRC32FileChecksum
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|that
operator|!=
literal|null
condition|)
block|{
name|xml
operator|.
name|attribute
argument_list|(
literal|"bytesPerCRC"
argument_list|,
literal|""
operator|+
name|that
operator|.
name|bytesPerCRC
argument_list|)
expr_stmt|;
name|xml
operator|.
name|attribute
argument_list|(
literal|"crcPerBlock"
argument_list|,
literal|""
operator|+
name|that
operator|.
name|crcPerBlock
argument_list|)
expr_stmt|;
name|xml
operator|.
name|attribute
argument_list|(
literal|"crcType"
argument_list|,
literal|""
operator|+
name|that
operator|.
name|getCrcType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|xml
operator|.
name|attribute
argument_list|(
literal|"md5"
argument_list|,
literal|""
operator|+
name|that
operator|.
name|md5
argument_list|)
expr_stmt|;
block|}
name|xml
operator|.
name|endTag
argument_list|()
expr_stmt|;
block|}
comment|/** Return the object represented in the attributes. */
DECL|method|valueOf (Attributes attrs )
specifier|public
specifier|static
name|MD5MD5CRC32FileChecksum
name|valueOf
parameter_list|(
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|String
name|bytesPerCRC
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"bytesPerCRC"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|crcPerBlock
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"crcPerBlock"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|md5
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"md5"
argument_list|)
decl_stmt|;
name|String
name|crcType
init|=
name|attrs
operator|.
name|getValue
argument_list|(
literal|"crcType"
argument_list|)
decl_stmt|;
name|DataChecksum
operator|.
name|Type
name|finalCrcType
decl_stmt|;
if|if
condition|(
name|bytesPerCRC
operator|==
literal|null
operator|||
name|crcPerBlock
operator|==
literal|null
operator|||
name|md5
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
comment|// old versions don't support crcType.
if|if
condition|(
name|crcType
operator|==
literal|null
operator|||
name|crcType
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|finalCrcType
operator|=
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
expr_stmt|;
block|}
else|else
block|{
name|finalCrcType
operator|=
name|DataChecksum
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|crcType
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|finalCrcType
condition|)
block|{
case|case
name|CRC32
case|:
return|return
operator|new
name|MD5MD5CRC32GzipFileChecksum
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|bytesPerCRC
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|crcPerBlock
argument_list|)
argument_list|,
operator|new
name|MD5Hash
argument_list|(
name|md5
argument_list|)
argument_list|)
return|;
case|case
name|CRC32C
case|:
return|return
operator|new
name|MD5MD5CRC32CastagnoliFileChecksum
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|bytesPerCRC
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|crcPerBlock
argument_list|)
argument_list|,
operator|new
name|MD5Hash
argument_list|(
name|md5
argument_list|)
argument_list|)
return|;
default|default:
comment|// we should never get here since finalCrcType will
comment|// hold a valid type or we should have got an exception.
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Invalid attributes: bytesPerCRC="
operator|+
name|bytesPerCRC
operator|+
literal|", crcPerBlock="
operator|+
name|crcPerBlock
operator|+
literal|", crcType="
operator|+
name|crcType
operator|+
literal|", md5="
operator|+
name|md5
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getAlgorithmName
argument_list|()
operator|+
literal|":"
operator|+
name|md5
return|;
block|}
block|}
end_class

end_unit

