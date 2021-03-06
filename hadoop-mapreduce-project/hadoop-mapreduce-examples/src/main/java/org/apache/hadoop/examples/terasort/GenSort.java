begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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
name|util
operator|.
name|PureJavaCrc32
import|;
end_import

begin_comment
comment|/**   * A single process data generator for the terasort data. Based on gensort.c   * version 1.1 (3 Mar 2009) from Chris Nyberg&lt;chris.nyberg@ordinal.com&gt;.  */
end_comment

begin_class
DECL|class|GenSort
specifier|public
class|class
name|GenSort
block|{
comment|/**    * Generate a "binary" record suitable for all sort benchmarks *except*     * PennySort.    */
DECL|method|generateRecord (byte[] recBuf, Unsigned16 rand, Unsigned16 recordNumber)
specifier|static
name|void
name|generateRecord
parameter_list|(
name|byte
index|[]
name|recBuf
parameter_list|,
name|Unsigned16
name|rand
parameter_list|,
name|Unsigned16
name|recordNumber
parameter_list|)
block|{
comment|/* generate the 10-byte key using the high 10 bytes of the 128-bit      * random number      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|recBuf
index|[
name|i
index|]
operator|=
name|rand
operator|.
name|getByte
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* add 2 bytes of "break" */
name|recBuf
index|[
literal|10
index|]
operator|=
literal|0x00
expr_stmt|;
name|recBuf
index|[
literal|11
index|]
operator|=
literal|0x11
expr_stmt|;
comment|/* convert the 128-bit record number to 32 bits of ascii hexadecimal      * as the next 32 bytes of the record.      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|32
condition|;
name|i
operator|++
control|)
block|{
name|recBuf
index|[
literal|12
operator|+
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|recordNumber
operator|.
name|getHexDigit
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* add 4 bytes of "break" data */
name|recBuf
index|[
literal|44
index|]
operator|=
operator|(
name|byte
operator|)
literal|0x88
expr_stmt|;
name|recBuf
index|[
literal|45
index|]
operator|=
operator|(
name|byte
operator|)
literal|0x99
expr_stmt|;
name|recBuf
index|[
literal|46
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xAA
expr_stmt|;
name|recBuf
index|[
literal|47
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xBB
expr_stmt|;
comment|/* add 48 bytes of filler based on low 48 bits of random number */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|12
condition|;
operator|++
name|i
control|)
block|{
name|recBuf
index|[
literal|48
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|49
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|50
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|51
operator|+
name|i
operator|*
literal|4
index|]
operator|=
operator|(
name|byte
operator|)
name|rand
operator|.
name|getHexDigit
argument_list|(
literal|20
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* add 4 bytes of "break" data */
name|recBuf
index|[
literal|96
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xCC
expr_stmt|;
name|recBuf
index|[
literal|97
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xDD
expr_stmt|;
name|recBuf
index|[
literal|98
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xEE
expr_stmt|;
name|recBuf
index|[
literal|99
index|]
operator|=
operator|(
name|byte
operator|)
literal|0xFF
expr_stmt|;
block|}
DECL|method|makeBigInteger (long x)
specifier|private
specifier|static
name|BigInteger
name|makeBigInteger
parameter_list|(
name|long
name|x
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8
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
literal|8
condition|;
operator|++
name|i
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>>
operator|(
literal|56
operator|-
literal|8
operator|*
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BigInteger
argument_list|(
literal|1
argument_list|,
name|data
argument_list|)
return|;
block|}
DECL|field|NINETY_FIVE
specifier|private
specifier|static
specifier|final
name|BigInteger
name|NINETY_FIVE
init|=
operator|new
name|BigInteger
argument_list|(
literal|"95"
argument_list|)
decl_stmt|;
comment|/**    * Generate an ascii record suitable for all sort benchmarks including     * PennySort.    */
DECL|method|generateAsciiRecord (byte[] recBuf, Unsigned16 rand, Unsigned16 recordNumber)
specifier|static
name|void
name|generateAsciiRecord
parameter_list|(
name|byte
index|[]
name|recBuf
parameter_list|,
name|Unsigned16
name|rand
parameter_list|,
name|Unsigned16
name|recordNumber
parameter_list|)
block|{
comment|/* generate the 10-byte ascii key using mostly the high 64 bits.      */
name|long
name|temp
init|=
name|rand
operator|.
name|getHigh8
argument_list|()
decl_stmt|;
if|if
condition|(
name|temp
operator|<
literal|0
condition|)
block|{
comment|// use biginteger to avoid the negative sign problem
name|BigInteger
name|bigTemp
init|=
name|makeBigInteger
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|recBuf
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|bigTemp
operator|.
name|mod
argument_list|(
name|NINETY_FIVE
argument_list|)
operator|.
name|longValue
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|bigTemp
operator|.
name|divide
argument_list|(
name|NINETY_FIVE
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|recBuf
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|temp
operator|%
literal|95
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|/=
literal|95
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|8
condition|;
operator|++
name|i
control|)
block|{
name|recBuf
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|temp
operator|%
literal|95
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|/=
literal|95
expr_stmt|;
block|}
name|temp
operator|=
name|rand
operator|.
name|getLow8
argument_list|()
expr_stmt|;
if|if
condition|(
name|temp
operator|<
literal|0
condition|)
block|{
name|BigInteger
name|bigTemp
init|=
name|makeBigInteger
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|recBuf
index|[
literal|8
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|bigTemp
operator|.
name|mod
argument_list|(
name|NINETY_FIVE
argument_list|)
operator|.
name|longValue
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|=
name|bigTemp
operator|.
name|divide
argument_list|(
name|NINETY_FIVE
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|recBuf
index|[
literal|8
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|temp
operator|%
literal|95
operator|)
argument_list|)
expr_stmt|;
name|temp
operator|/=
literal|95
expr_stmt|;
block|}
name|recBuf
index|[
literal|9
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
literal|' '
operator|+
operator|(
name|temp
operator|%
literal|95
operator|)
argument_list|)
expr_stmt|;
comment|/* add 2 bytes of "break" */
name|recBuf
index|[
literal|10
index|]
operator|=
literal|' '
expr_stmt|;
name|recBuf
index|[
literal|11
index|]
operator|=
literal|' '
expr_stmt|;
comment|/* convert the 128-bit record number to 32 bits of ascii hexadecimal      * as the next 32 bytes of the record.      */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|32
condition|;
name|i
operator|++
control|)
block|{
name|recBuf
index|[
literal|12
operator|+
name|i
index|]
operator|=
operator|(
name|byte
operator|)
name|recordNumber
operator|.
name|getHexDigit
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* add 2 bytes of "break" data */
name|recBuf
index|[
literal|44
index|]
operator|=
literal|' '
expr_stmt|;
name|recBuf
index|[
literal|45
index|]
operator|=
literal|' '
expr_stmt|;
comment|/* add 52 bytes of filler based on low 48 bits of random number */
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|13
condition|;
operator|++
name|i
control|)
block|{
name|recBuf
index|[
literal|46
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|47
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|48
operator|+
name|i
operator|*
literal|4
index|]
operator|=
name|recBuf
index|[
literal|49
operator|+
name|i
operator|*
literal|4
index|]
operator|=
operator|(
name|byte
operator|)
name|rand
operator|.
name|getHexDigit
argument_list|(
literal|19
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|/* add 2 bytes of "break" data */
name|recBuf
index|[
literal|98
index|]
operator|=
literal|'\r'
expr_stmt|;
comment|/* nice for Windows */
name|recBuf
index|[
literal|99
index|]
operator|=
literal|'\n'
expr_stmt|;
block|}
DECL|method|usage ()
specifier|private
specifier|static
name|void
name|usage
parameter_list|()
block|{
name|PrintStream
name|out
init|=
name|System
operator|.
name|out
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"usage: gensort [-a] [-c] [-bSTARTING_REC_NUM] NUM_RECS FILE_NAME"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-a        Generate ascii records required for PennySort or JouleSort."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          These records are also an alternative input for the other"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          sort benchmarks.  Without this flag, binary records will be"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          generated that contain the highest density of randomness in"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          the 10-byte key."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-c        Calculate the sum of the crc32 checksums of each of the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          generated records and send it to standard error."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"-bN       Set the beginning record generated to N. By default the"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"          first record generated is record 0."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"NUM_RECS  The number of sequential records to generate."
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"FILE_NAME The name of the file to write the records to.\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Example 1 - to generate 1000000 ascii records starting at record 0 to"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"the file named \"pennyinput\":"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    gensort -a 1000000 pennyinput\n"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Example 2 - to generate 1000 binary records beginning with record 2000"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"to the file named \"partition2\":"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    gensort -b2000 1000 partition2"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|outputRecords (OutputStream out, boolean useAscii, Unsigned16 firstRecordNumber, Unsigned16 recordsToGenerate, Unsigned16 checksum )
specifier|public
specifier|static
name|void
name|outputRecords
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|boolean
name|useAscii
parameter_list|,
name|Unsigned16
name|firstRecordNumber
parameter_list|,
name|Unsigned16
name|recordsToGenerate
parameter_list|,
name|Unsigned16
name|checksum
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|row
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|Unsigned16
name|recordNumber
init|=
operator|new
name|Unsigned16
argument_list|(
name|firstRecordNumber
argument_list|)
decl_stmt|;
name|Unsigned16
name|lastRecordNumber
init|=
operator|new
name|Unsigned16
argument_list|(
name|firstRecordNumber
argument_list|)
decl_stmt|;
name|Checksum
name|crc
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
name|Unsigned16
name|tmp
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
name|lastRecordNumber
operator|.
name|add
argument_list|(
name|recordsToGenerate
argument_list|)
expr_stmt|;
name|Unsigned16
name|ONE
init|=
operator|new
name|Unsigned16
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Unsigned16
name|rand
init|=
name|Random16
operator|.
name|skipAhead
argument_list|(
name|firstRecordNumber
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|recordNumber
operator|.
name|equals
argument_list|(
name|lastRecordNumber
argument_list|)
condition|)
block|{
name|Random16
operator|.
name|nextRand
argument_list|(
name|rand
argument_list|)
expr_stmt|;
if|if
condition|(
name|useAscii
condition|)
block|{
name|generateAsciiRecord
argument_list|(
name|row
argument_list|,
name|rand
argument_list|,
name|recordNumber
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generateRecord
argument_list|(
name|row
argument_list|,
name|rand
argument_list|,
name|recordNumber
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checksum
operator|!=
literal|null
condition|)
block|{
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|crc
operator|.
name|update
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|row
operator|.
name|length
argument_list|)
expr_stmt|;
name|tmp
operator|.
name|set
argument_list|(
name|crc
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|checksum
operator|.
name|add
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
name|recordNumber
operator|.
name|add
argument_list|(
name|ONE
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Unsigned16
name|startingRecord
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
name|Unsigned16
name|numberOfRecords
decl_stmt|;
name|OutputStream
name|out
decl_stmt|;
name|boolean
name|useAscii
init|=
literal|false
decl_stmt|;
name|Unsigned16
name|checksum
init|=
literal|null
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
name|int
name|argLength
init|=
name|arg
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|argLength
operator|>=
literal|1
operator|&&
name|arg
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|argLength
operator|<
literal|2
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
block|}
switch|switch
condition|(
name|arg
operator|.
name|charAt
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
literal|'a'
case|:
name|useAscii
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|'b'
case|:
name|startingRecord
operator|=
name|Unsigned16
operator|.
name|fromDecimal
argument_list|(
name|arg
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'c'
case|:
name|checksum
operator|=
operator|new
name|Unsigned16
argument_list|()
expr_stmt|;
break|break;
default|default:
name|usage
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|-
name|i
operator|!=
literal|2
condition|)
block|{
name|usage
argument_list|()
expr_stmt|;
block|}
name|numberOfRecords
operator|=
name|Unsigned16
operator|.
name|fromDecimal
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|args
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|outputRecords
argument_list|(
name|out
argument_list|,
name|useAscii
argument_list|,
name|startingRecord
argument_list|,
name|numberOfRecords
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|checksum
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|checksum
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

