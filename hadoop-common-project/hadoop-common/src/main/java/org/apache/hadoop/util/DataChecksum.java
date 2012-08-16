begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Checksum
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
name|ChecksumException
import|;
end_import

begin_comment
comment|/**  * This class provides inteface and utilities for processing checksums for  * DFS data transfers.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DataChecksum
specifier|public
class|class
name|DataChecksum
implements|implements
name|Checksum
block|{
comment|// Misc constants
DECL|field|HEADER_LEN
specifier|public
specifier|static
specifier|final
name|int
name|HEADER_LEN
init|=
literal|5
decl_stmt|;
comment|/// 1 byte type and 4 byte len
comment|// checksum types
DECL|field|CHECKSUM_NULL
specifier|public
specifier|static
specifier|final
name|int
name|CHECKSUM_NULL
init|=
literal|0
decl_stmt|;
DECL|field|CHECKSUM_CRC32
specifier|public
specifier|static
specifier|final
name|int
name|CHECKSUM_CRC32
init|=
literal|1
decl_stmt|;
DECL|field|CHECKSUM_CRC32C
specifier|public
specifier|static
specifier|final
name|int
name|CHECKSUM_CRC32C
init|=
literal|2
decl_stmt|;
comment|/** The checksum types */
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enumConstant|NULL
name|NULL
argument_list|(
name|CHECKSUM_NULL
argument_list|,
literal|0
argument_list|)
block|,
DECL|enumConstant|CRC32
name|CRC32
argument_list|(
name|CHECKSUM_CRC32
argument_list|,
literal|4
argument_list|)
block|,
DECL|enumConstant|CRC32C
name|CRC32C
argument_list|(
name|CHECKSUM_CRC32C
argument_list|,
literal|4
argument_list|)
block|;
DECL|field|id
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|size
specifier|public
specifier|final
name|int
name|size
decl_stmt|;
DECL|method|Type (int id, int size)
specifier|private
name|Type
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/** @return the type corresponding to the id. */
DECL|method|valueOf (int id)
specifier|public
specifier|static
name|Type
name|valueOf
parameter_list|(
name|int
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|<
literal|0
operator|||
name|id
operator|>=
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"id="
operator|+
name|id
operator|+
literal|" out of range [0, "
operator|+
name|values
argument_list|()
operator|.
name|length
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|values
argument_list|()
index|[
name|id
index|]
return|;
block|}
block|}
DECL|method|newDataChecksum (Type type, int bytesPerChecksum )
specifier|public
specifier|static
name|DataChecksum
name|newDataChecksum
parameter_list|(
name|Type
name|type
parameter_list|,
name|int
name|bytesPerChecksum
parameter_list|)
block|{
if|if
condition|(
name|bytesPerChecksum
operator|<=
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NULL
case|:
return|return
operator|new
name|DataChecksum
argument_list|(
name|type
argument_list|,
operator|new
name|ChecksumNull
argument_list|()
argument_list|,
name|bytesPerChecksum
argument_list|)
return|;
case|case
name|CRC32
case|:
return|return
operator|new
name|DataChecksum
argument_list|(
name|type
argument_list|,
operator|new
name|PureJavaCrc32
argument_list|()
argument_list|,
name|bytesPerChecksum
argument_list|)
return|;
case|case
name|CRC32C
case|:
return|return
operator|new
name|DataChecksum
argument_list|(
name|type
argument_list|,
operator|new
name|PureJavaCrc32C
argument_list|()
argument_list|,
name|bytesPerChecksum
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Creates a DataChecksum from HEADER_LEN bytes from arr[offset].    * @return DataChecksum of the type in the array or null in case of an error.    */
DECL|method|newDataChecksum ( byte bytes[], int offset )
specifier|public
specifier|static
name|DataChecksum
name|newDataChecksum
parameter_list|(
name|byte
name|bytes
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|bytes
operator|.
name|length
operator|<
name|offset
operator|+
name|HEADER_LEN
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// like readInt():
name|int
name|bytesPerChecksum
init|=
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|offset
operator|+
literal|4
index|]
operator|&
literal|0xff
operator|)
operator|)
decl_stmt|;
return|return
name|newDataChecksum
argument_list|(
name|Type
operator|.
name|valueOf
argument_list|(
name|bytes
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|bytesPerChecksum
argument_list|)
return|;
block|}
comment|/**    * This constructucts a DataChecksum by reading HEADER_LEN bytes from    * input stream<i>in</i>    */
DECL|method|newDataChecksum ( DataInputStream in )
specifier|public
specifier|static
name|DataChecksum
name|newDataChecksum
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|int
name|bpc
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|DataChecksum
name|summer
init|=
name|newDataChecksum
argument_list|(
name|Type
operator|.
name|valueOf
argument_list|(
name|type
argument_list|)
argument_list|,
name|bpc
argument_list|)
decl_stmt|;
if|if
condition|(
name|summer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not create DataChecksum of type "
operator|+
name|type
operator|+
literal|" with bytesPerChecksum "
operator|+
name|bpc
argument_list|)
throw|;
block|}
return|return
name|summer
return|;
block|}
comment|/**    * Writes the checksum header to the output stream<i>out</i>.    */
DECL|method|writeHeader ( DataOutputStream out )
specifier|public
name|void
name|writeHeader
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|type
operator|.
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|bytesPerChecksum
argument_list|)
expr_stmt|;
block|}
DECL|method|getHeader ()
specifier|public
name|byte
index|[]
name|getHeader
parameter_list|()
block|{
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
name|DataChecksum
operator|.
name|HEADER_LEN
index|]
decl_stmt|;
name|header
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|type
operator|.
name|id
operator|&
literal|0xff
argument_list|)
expr_stmt|;
comment|// Writing in buffer just like DataOutput.WriteInt()
name|header
index|[
literal|1
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|bytesPerChecksum
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|header
index|[
literal|1
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|bytesPerChecksum
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|header
index|[
literal|1
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|bytesPerChecksum
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|header
index|[
literal|1
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|bytesPerChecksum
operator|&
literal|0xff
argument_list|)
expr_stmt|;
return|return
name|header
return|;
block|}
comment|/**    * Writes the current checksum to the stream.    * If<i>reset</i> is true, then resets the checksum.    * @return number of bytes written. Will be equal to getChecksumSize();    */
DECL|method|writeValue ( DataOutputStream out, boolean reset )
specifier|public
name|int
name|writeValue
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|boolean
name|reset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|.
name|size
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|type
operator|.
name|size
operator|==
literal|4
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown Checksum "
operator|+
name|type
argument_list|)
throw|;
block|}
if|if
condition|(
name|reset
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|type
operator|.
name|size
return|;
block|}
comment|/**     * Writes the current checksum to a buffer.     * If<i>reset</i> is true, then resets the checksum.     * @return number of bytes written. Will be equal to getChecksumSize();     */
DECL|method|writeValue ( byte[] buf, int offset, boolean reset )
specifier|public
name|int
name|writeValue
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|int
name|offset
parameter_list|,
name|boolean
name|reset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|type
operator|.
name|size
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|type
operator|.
name|size
operator|==
literal|4
condition|)
block|{
name|int
name|checksum
init|=
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|buf
index|[
name|offset
operator|+
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|checksum
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buf
index|[
name|offset
operator|+
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|checksum
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buf
index|[
name|offset
operator|+
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|checksum
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|buf
index|[
name|offset
operator|+
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|checksum
operator|&
literal|0xff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown Checksum "
operator|+
name|type
argument_list|)
throw|;
block|}
if|if
condition|(
name|reset
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|type
operator|.
name|size
return|;
block|}
comment|/**     * Compares the checksum located at buf[offset] with the current checksum.     * @return true if the checksum matches and false otherwise.     */
DECL|method|compare ( byte buf[], int offset )
specifier|public
name|boolean
name|compare
parameter_list|(
name|byte
name|buf
index|[]
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|size
operator|==
literal|4
condition|)
block|{
name|int
name|checksum
init|=
operator|(
operator|(
name|buf
index|[
name|offset
operator|+
literal|0
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|buf
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|buf
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|buf
index|[
name|offset
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
operator|)
decl_stmt|;
return|return
name|checksum
operator|==
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
return|;
block|}
return|return
name|type
operator|.
name|size
operator|==
literal|0
return|;
block|}
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|field|summer
specifier|private
specifier|final
name|Checksum
name|summer
decl_stmt|;
DECL|field|bytesPerChecksum
specifier|private
specifier|final
name|int
name|bytesPerChecksum
decl_stmt|;
DECL|field|inSum
specifier|private
name|int
name|inSum
init|=
literal|0
decl_stmt|;
DECL|method|DataChecksum ( Type type, Checksum checksum, int chunkSize )
specifier|private
name|DataChecksum
parameter_list|(
name|Type
name|type
parameter_list|,
name|Checksum
name|checksum
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|summer
operator|=
name|checksum
expr_stmt|;
name|bytesPerChecksum
operator|=
name|chunkSize
expr_stmt|;
block|}
comment|// Accessors
DECL|method|getChecksumType ()
specifier|public
name|Type
name|getChecksumType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getChecksumSize ()
specifier|public
name|int
name|getChecksumSize
parameter_list|()
block|{
return|return
name|type
operator|.
name|size
return|;
block|}
DECL|method|getBytesPerChecksum ()
specifier|public
name|int
name|getBytesPerChecksum
parameter_list|()
block|{
return|return
name|bytesPerChecksum
return|;
block|}
DECL|method|getNumBytesInSum ()
specifier|public
name|int
name|getNumBytesInSum
parameter_list|()
block|{
return|return
name|inSum
return|;
block|}
DECL|field|SIZE_OF_INTEGER
specifier|public
specifier|static
specifier|final
name|int
name|SIZE_OF_INTEGER
init|=
name|Integer
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
DECL|method|getChecksumHeaderSize ()
specifier|static
specifier|public
name|int
name|getChecksumHeaderSize
parameter_list|()
block|{
return|return
literal|1
operator|+
name|SIZE_OF_INTEGER
return|;
comment|// type byte, bytesPerChecksum int
block|}
comment|//Checksum Interface. Just a wrapper around member summer.
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|summer
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|summer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|inSum
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|update ( byte[] b, int off, int len )
specifier|public
name|void
name|update
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|summer
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|inSum
operator|+=
name|len
expr_stmt|;
block|}
block|}
DECL|method|update ( int b )
specifier|public
name|void
name|update
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|summer
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|inSum
operator|+=
literal|1
expr_stmt|;
block|}
comment|/**    * Verify that the given checksums match the given data.    *     * The 'mark' of the ByteBuffer parameters may be modified by this function,.    * but the position is maintained.    *      * @param data the DirectByteBuffer pointing to the data to verify.    * @param checksums the DirectByteBuffer pointing to a series of stored    *                  checksums    * @param fileName the name of the file being read, for error-reporting    * @param basePos the file position to which the start of 'data' corresponds    * @throws ChecksumException if the checksums do not match    */
DECL|method|verifyChunkedSums (ByteBuffer data, ByteBuffer checksums, String fileName, long basePos)
specifier|public
name|void
name|verifyChunkedSums
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|ByteBuffer
name|checksums
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
block|{
if|if
condition|(
name|type
operator|.
name|size
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|data
operator|.
name|hasArray
argument_list|()
operator|&&
name|checksums
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|verifyChunkedSums
argument_list|(
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|arrayOffset
argument_list|()
operator|+
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|arrayOffset
argument_list|()
operator|+
name|checksums
operator|.
name|position
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|NativeCrc32
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|NativeCrc32
operator|.
name|verifyChunkedSums
argument_list|(
name|bytesPerChecksum
argument_list|,
name|type
operator|.
name|id
argument_list|,
name|checksums
argument_list|,
name|data
argument_list|,
name|fileName
argument_list|,
name|basePos
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|startDataPos
init|=
name|data
operator|.
name|position
argument_list|()
decl_stmt|;
name|data
operator|.
name|mark
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|mark
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|bytesPerChecksum
index|]
decl_stmt|;
name|byte
index|[]
name|sum
init|=
operator|new
name|byte
index|[
name|type
operator|.
name|size
index|]
decl_stmt|;
while|while
condition|(
name|data
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|checksums
operator|.
name|get
argument_list|(
name|sum
argument_list|)
expr_stmt|;
name|data
operator|.
name|get
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|summer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|summer
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|int
name|calculated
init|=
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|stored
init|=
operator|(
name|sum
index|[
literal|0
index|]
operator|<<
literal|24
operator|&
literal|0xff000000
operator|)
operator||
operator|(
name|sum
index|[
literal|1
index|]
operator|<<
literal|16
operator|&
literal|0xff0000
operator|)
operator||
operator|(
name|sum
index|[
literal|2
index|]
operator|<<
literal|8
operator|&
literal|0xff00
operator|)
operator||
name|sum
index|[
literal|3
index|]
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
name|calculated
operator|!=
name|stored
condition|)
block|{
name|long
name|errPos
init|=
name|basePos
operator|+
name|data
operator|.
name|position
argument_list|()
operator|-
name|startDataPos
operator|-
name|n
decl_stmt|;
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum error: "
operator|+
name|fileName
operator|+
literal|" at "
operator|+
name|errPos
operator|+
literal|" exp: "
operator|+
name|stored
operator|+
literal|" got: "
operator|+
name|calculated
argument_list|,
name|errPos
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|data
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Implementation of chunked verification specifically on byte arrays. This    * is to avoid the copy when dealing with ByteBuffers that have array backing.    */
DECL|method|verifyChunkedSums ( byte[] data, int dataOff, int dataLen, byte[] checksums, int checksumsOff, String fileName, long basePos)
specifier|private
name|void
name|verifyChunkedSums
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|dataOff
parameter_list|,
name|int
name|dataLen
parameter_list|,
name|byte
index|[]
name|checksums
parameter_list|,
name|int
name|checksumsOff
parameter_list|,
name|String
name|fileName
parameter_list|,
name|long
name|basePos
parameter_list|)
throws|throws
name|ChecksumException
block|{
name|int
name|remaining
init|=
name|dataLen
decl_stmt|;
name|int
name|dataPos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|summer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|summer
operator|.
name|update
argument_list|(
name|data
argument_list|,
name|dataOff
operator|+
name|dataPos
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|dataPos
operator|+=
name|n
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
name|int
name|calculated
init|=
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|stored
init|=
operator|(
name|checksums
index|[
name|checksumsOff
index|]
operator|<<
literal|24
operator|&
literal|0xff000000
operator|)
operator||
operator|(
name|checksums
index|[
name|checksumsOff
operator|+
literal|1
index|]
operator|<<
literal|16
operator|&
literal|0xff0000
operator|)
operator||
operator|(
name|checksums
index|[
name|checksumsOff
operator|+
literal|2
index|]
operator|<<
literal|8
operator|&
literal|0xff00
operator|)
operator||
name|checksums
index|[
name|checksumsOff
operator|+
literal|3
index|]
operator|&
literal|0xff
decl_stmt|;
name|checksumsOff
operator|+=
literal|4
expr_stmt|;
if|if
condition|(
name|calculated
operator|!=
name|stored
condition|)
block|{
name|long
name|errPos
init|=
name|basePos
operator|+
name|dataPos
operator|-
name|n
decl_stmt|;
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum error: "
operator|+
name|fileName
operator|+
literal|" at "
operator|+
name|errPos
operator|+
literal|" exp: "
operator|+
name|stored
operator|+
literal|" got: "
operator|+
name|calculated
argument_list|,
name|errPos
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Calculate checksums for the given data.    *     * The 'mark' of the ByteBuffer parameters may be modified by this function,    * but the position is maintained.    *     * @param data the DirectByteBuffer pointing to the data to checksum.    * @param checksums the DirectByteBuffer into which checksums will be    *                  stored. Enough space must be available in this    *                  buffer to put the checksums.    */
DECL|method|calculateChunkedSums (ByteBuffer data, ByteBuffer checksums)
specifier|public
name|void
name|calculateChunkedSums
parameter_list|(
name|ByteBuffer
name|data
parameter_list|,
name|ByteBuffer
name|checksums
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|size
operator|==
literal|0
condition|)
return|return;
if|if
condition|(
name|data
operator|.
name|hasArray
argument_list|()
operator|&&
name|checksums
operator|.
name|hasArray
argument_list|()
condition|)
block|{
name|calculateChunkedSums
argument_list|(
name|data
operator|.
name|array
argument_list|()
argument_list|,
name|data
operator|.
name|arrayOffset
argument_list|()
operator|+
name|data
operator|.
name|position
argument_list|()
argument_list|,
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|checksums
operator|.
name|array
argument_list|()
argument_list|,
name|checksums
operator|.
name|arrayOffset
argument_list|()
operator|+
name|checksums
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|data
operator|.
name|mark
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|mark
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|bytesPerChecksum
index|]
decl_stmt|;
while|while
condition|(
name|data
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|data
operator|.
name|remaining
argument_list|()
argument_list|,
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|data
operator|.
name|get
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|summer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|summer
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|checksums
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|summer
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|data
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checksums
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Implementation of chunked calculation specifically on byte arrays. This    * is to avoid the copy when dealing with ByteBuffers that have array backing.    */
DECL|method|calculateChunkedSums ( byte[] data, int dataOffset, int dataLength, byte[] sums, int sumsOffset)
specifier|private
name|void
name|calculateChunkedSums
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|dataOffset
parameter_list|,
name|int
name|dataLength
parameter_list|,
name|byte
index|[]
name|sums
parameter_list|,
name|int
name|sumsOffset
parameter_list|)
block|{
name|int
name|remaining
init|=
name|dataLength
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|bytesPerChecksum
argument_list|)
decl_stmt|;
name|summer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|summer
operator|.
name|update
argument_list|(
name|data
argument_list|,
name|dataOffset
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|dataOffset
operator|+=
name|n
expr_stmt|;
name|remaining
operator|-=
name|n
expr_stmt|;
name|long
name|calculated
init|=
name|summer
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|sums
index|[
name|sumsOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|calculated
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|sums
index|[
name|sumsOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|calculated
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|sums
index|[
name|sumsOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|calculated
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|sums
index|[
name|sumsOffset
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|calculated
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|DataChecksum
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DataChecksum
name|o
init|=
operator|(
name|DataChecksum
operator|)
name|other
decl_stmt|;
return|return
name|o
operator|.
name|bytesPerChecksum
operator|==
name|this
operator|.
name|bytesPerChecksum
operator|&&
name|o
operator|.
name|type
operator|==
name|this
operator|.
name|type
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|type
operator|.
name|id
operator|+
literal|31
operator|)
operator|*
name|this
operator|.
name|bytesPerChecksum
return|;
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
literal|"DataChecksum(type="
operator|+
name|type
operator|+
literal|", chunkSize="
operator|+
name|bytesPerChecksum
operator|+
literal|")"
return|;
block|}
comment|/**    * This just provides a dummy implimentation for Checksum class    * This is used when there is no checksum available or required for     * data    */
DECL|class|ChecksumNull
specifier|static
class|class
name|ChecksumNull
implements|implements
name|Checksum
block|{
DECL|method|ChecksumNull ()
specifier|public
name|ChecksumNull
parameter_list|()
block|{}
comment|//Dummy interface
DECL|method|getValue ()
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{}
DECL|method|update (byte[] b, int off, int len)
specifier|public
name|void
name|update
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{}
DECL|method|update (int b)
specifier|public
name|void
name|update
parameter_list|(
name|int
name|b
parameter_list|)
block|{}
block|}
empty_stmt|;
block|}
end_class

end_unit

