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
name|FileNotFoundException
import|;
end_import

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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|CRC32
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Unit test to verify that the pure-Java CRC32 algorithm gives  * the same results as the built-in implementation.  */
end_comment

begin_class
DECL|class|TestPureJavaCrc32
specifier|public
class|class
name|TestPureJavaCrc32
block|{
DECL|field|theirs
specifier|private
specifier|final
name|CRC32
name|theirs
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
DECL|field|ours
specifier|private
specifier|final
name|PureJavaCrc32
name|ours
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testCorrectness ()
specifier|public
name|void
name|testCorrectness
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSame
argument_list|()
expr_stmt|;
name|theirs
operator|.
name|update
argument_list|(
literal|104
argument_list|)
expr_stmt|;
name|ours
operator|.
name|update
argument_list|(
literal|104
argument_list|)
expr_stmt|;
name|checkSame
argument_list|()
expr_stmt|;
name|checkOnBytes
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|40
block|,
literal|60
block|,
literal|97
block|,
operator|-
literal|70
block|}
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkOnBytes
argument_list|(
literal|"hello world!"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|false
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|randomBytes
index|[]
init|=
operator|new
name|byte
index|[
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2048
argument_list|)
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|randomBytes
argument_list|)
expr_stmt|;
name|checkOnBytes
argument_list|(
name|randomBytes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkOnBytes (byte[] bytes, boolean print)
specifier|private
name|void
name|checkOnBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|boolean
name|print
parameter_list|)
block|{
name|theirs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ours
operator|.
name|reset
argument_list|()
expr_stmt|;
name|checkSame
argument_list|()
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ours
operator|.
name|update
argument_list|(
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|theirs
operator|.
name|update
argument_list|(
name|bytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|checkSame
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|print
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"theirs:\t"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|theirs
operator|.
name|getValue
argument_list|()
argument_list|)
operator|+
literal|"\nours:\t"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|ours
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|theirs
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ours
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ours
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|theirs
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|print
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"theirs:\t"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|theirs
operator|.
name|getValue
argument_list|()
argument_list|)
operator|+
literal|"\nours:\t"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|ours
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|checkSame
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytes
operator|.
name|length
operator|>=
literal|10
condition|)
block|{
name|ours
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|theirs
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|checkSame
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|checkSame ()
specifier|private
name|void
name|checkSame
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|theirs
operator|.
name|getValue
argument_list|()
argument_list|,
name|ours
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate a table to perform checksums based on the same CRC-32 polynomial    * that java.util.zip.CRC32 uses.    */
DECL|class|Table
specifier|public
specifier|static
class|class
name|Table
block|{
DECL|field|tables
specifier|private
specifier|final
name|int
index|[]
index|[]
name|tables
decl_stmt|;
DECL|method|Table (final int nBits, final int nTables, long polynomial)
specifier|private
name|Table
parameter_list|(
specifier|final
name|int
name|nBits
parameter_list|,
specifier|final
name|int
name|nTables
parameter_list|,
name|long
name|polynomial
parameter_list|)
block|{
name|tables
operator|=
operator|new
name|int
index|[
name|nTables
index|]
index|[]
expr_stmt|;
specifier|final
name|int
name|size
init|=
literal|1
operator|<<
name|nBits
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
name|tables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|tables
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
comment|//compute the first table
specifier|final
name|int
index|[]
name|first
init|=
name|tables
index|[
literal|0
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
name|first
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|crc
init|=
name|i
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nBits
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|crc
operator|&
literal|1
operator|)
operator|==
literal|1
condition|)
block|{
name|crc
operator|>>>=
literal|1
expr_stmt|;
name|crc
operator|^=
name|polynomial
expr_stmt|;
block|}
else|else
block|{
name|crc
operator|>>>=
literal|1
expr_stmt|;
block|}
block|}
name|first
index|[
name|i
index|]
operator|=
name|crc
expr_stmt|;
block|}
comment|//compute the remaining tables
specifier|final
name|int
name|mask
init|=
name|first
operator|.
name|length
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|tables
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
index|[]
name|previous
init|=
name|tables
index|[
name|j
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|current
init|=
name|tables
index|[
name|j
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
name|current
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|current
index|[
name|i
index|]
operator|=
operator|(
name|previous
index|[
name|i
index|]
operator|>>>
name|nBits
operator|)
operator|^
name|first
index|[
name|previous
index|[
name|i
index|]
operator|&
name|mask
index|]
expr_stmt|;
block|}
block|}
block|}
DECL|method|toStrings (String nameformat)
name|String
index|[]
name|toStrings
parameter_list|(
name|String
name|nameformat
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|tables
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tables
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
index|[]
name|t
init|=
name|tables
index|[
name|j
index|]
decl_stmt|;
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"    /* "
operator|+
name|nameformat
operator|+
literal|" */"
argument_list|,
name|j
argument_list|)
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
name|t
operator|.
name|length
condition|;
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\n    "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
literal|4
condition|;
name|k
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"0x%08X, "
argument_list|,
name|t
index|[
name|i
operator|++
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|s
index|[
name|j
index|]
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|s
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
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|String
name|tableFormat
init|=
name|String
operator|.
name|format
argument_list|(
literal|"T%d_"
argument_list|,
name|Integer
operator|.
name|numberOfTrailingZeros
argument_list|(
name|tables
index|[
literal|0
index|]
operator|.
name|length
argument_list|)
argument_list|)
operator|+
literal|"%d"
decl_stmt|;
specifier|final
name|String
name|startFormat
init|=
literal|"  private static final int "
operator|+
name|tableFormat
operator|+
literal|"_start = %d*256;"
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|tables
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|startFormat
argument_list|,
name|j
argument_list|,
name|j
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"  private static final int[] T = new int[] {"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|toStrings
argument_list|(
name|tableFormat
argument_list|)
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|setCharAt
argument_list|(
name|b
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
literal|'\n'
argument_list|)
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" };\n"
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Generate CRC-32 lookup tables */
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
name|FileNotFoundException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: "
operator|+
name|Table
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"<polynomial>"
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
name|long
name|polynomial
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|8
decl_stmt|;
specifier|final
name|Table
name|t
init|=
operator|new
name|Table
argument_list|(
name|i
argument_list|,
literal|16
argument_list|,
name|polynomial
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|t
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|//print to a file
specifier|final
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
literal|"table"
operator|+
name|i
operator|+
literal|".txt"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|out
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Performance tests to compare performance of the Pure Java implementation    * to the built-in java.util.zip implementation. This can be run from the    * command line with:    *    *   java -cp path/to/test/classes:path/to/common/classes \    *      'org.apache.hadoop.util.TestPureJavaCrc32$PerformanceTest'    *    * The output is in JIRA table format.    */
DECL|class|PerformanceTest
specifier|public
specifier|static
class|class
name|PerformanceTest
block|{
DECL|field|MAX_LEN
specifier|public
specifier|static
specifier|final
name|int
name|MAX_LEN
init|=
literal|32
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// up to 32MB chunks
DECL|field|BYTES_PER_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|BYTES_PER_SIZE
init|=
name|MAX_LEN
operator|*
literal|4
decl_stmt|;
DECL|field|zip
specifier|static
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|zip
init|=
name|CRC32
operator|.
name|class
decl_stmt|;
DECL|field|CRCS
specifier|static
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
argument_list|>
name|CRCS
init|=
operator|new
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|CRCS
operator|.
name|add
argument_list|(
name|zip
argument_list|)
expr_stmt|;
name|CRCS
operator|.
name|add
argument_list|(
name|PureJavaCrc32
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String args[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|printSystemProperties
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|doBench
argument_list|(
name|CRCS
argument_list|,
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|printCell (String s, int width, PrintStream out)
specifier|private
specifier|static
name|void
name|printCell
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|width
parameter_list|,
name|PrintStream
name|out
parameter_list|)
block|{
specifier|final
name|int
name|w
init|=
name|s
operator|.
name|length
argument_list|()
operator|>
name|width
condition|?
name|s
operator|.
name|length
argument_list|()
else|:
name|width
decl_stmt|;
name|out
operator|.
name|printf
argument_list|(
literal|" %"
operator|+
name|w
operator|+
literal|"s |"
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|doBench (final List<Class<? extends Checksum>> crcs, final PrintStream out)
specifier|private
specifier|static
name|void
name|doBench
parameter_list|(
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
argument_list|>
name|crcs
parameter_list|,
specifier|final
name|PrintStream
name|out
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|MAX_LEN
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
comment|// Print header
name|out
operator|.
name|printf
argument_list|(
literal|"\nPerformance Table (The unit is MB/sec; #T = #Theads)\n"
argument_list|)
expr_stmt|;
comment|// Warm up implementations to get jit going.
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|c
range|:
name|crcs
control|)
block|{
name|doBench
argument_list|(
name|c
argument_list|,
literal|1
argument_list|,
name|bytes
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|doBench
argument_list|(
name|c
argument_list|,
literal|1
argument_list|,
name|bytes
argument_list|,
literal|2101
argument_list|)
expr_stmt|;
block|}
comment|// Test on a variety of sizes with different number of threads
for|for
control|(
name|int
name|size
init|=
literal|32
init|;
name|size
operator|<=
name|MAX_LEN
condition|;
name|size
operator|<<=
literal|1
control|)
block|{
name|doBench
argument_list|(
name|crcs
argument_list|,
name|bytes
argument_list|,
name|size
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doBench (final List<Class<? extends Checksum>> crcs, final byte[] bytes, final int size, final PrintStream out)
specifier|private
specifier|static
name|void
name|doBench
parameter_list|(
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
argument_list|>
name|crcs
parameter_list|,
specifier|final
name|byte
index|[]
name|bytes
parameter_list|,
specifier|final
name|int
name|size
parameter_list|,
specifier|final
name|PrintStream
name|out
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|numBytesStr
init|=
literal|" #Bytes "
decl_stmt|;
specifier|final
name|String
name|numThreadsStr
init|=
literal|"#T"
decl_stmt|;
specifier|final
name|String
name|diffStr
init|=
literal|"% diff"
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|numBytesStr
argument_list|,
literal|0
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|numThreadsStr
argument_list|,
literal|0
argument_list|,
name|out
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
name|crcs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|c
init|=
name|crcs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|c
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|8
argument_list|,
name|out
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
name|printCell
argument_list|(
name|diffStr
argument_list|,
name|diffStr
operator|.
name|length
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|printf
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|numThreads
init|=
literal|1
init|;
name|numThreads
operator|<=
literal|16
condition|;
name|numThreads
operator|<<=
literal|1
control|)
block|{
name|out
operator|.
name|printf
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
argument_list|,
name|numBytesStr
operator|.
name|length
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|numThreads
argument_list|)
argument_list|,
name|numThreadsStr
operator|.
name|length
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|BenchResult
name|expected
init|=
literal|null
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BenchResult
argument_list|>
name|previous
init|=
operator|new
name|ArrayList
argument_list|<
name|BenchResult
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|c
range|:
name|crcs
control|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
specifier|final
name|BenchResult
name|result
init|=
name|doBench
argument_list|(
name|c
argument_list|,
name|numThreads
argument_list|,
name|bytes
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%9.1f"
argument_list|,
name|result
operator|.
name|mbps
argument_list|)
argument_list|,
name|c
operator|.
name|getSimpleName
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|//check result
if|if
condition|(
name|c
operator|==
name|zip
condition|)
block|{
name|expected
operator|=
name|result
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The first class is "
operator|+
name|c
operator|.
name|getName
argument_list|()
operator|+
literal|" but not "
operator|+
name|zip
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|result
operator|.
name|value
operator|!=
name|expected
operator|.
name|value
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|c
operator|+
literal|" has bugs!"
argument_list|)
throw|;
block|}
comment|//compare result with previous
for|for
control|(
name|BenchResult
name|p
range|:
name|previous
control|)
block|{
specifier|final
name|double
name|diff
init|=
operator|(
name|result
operator|.
name|mbps
operator|-
name|p
operator|.
name|mbps
operator|)
operator|/
name|p
operator|.
name|mbps
operator|*
literal|100
decl_stmt|;
name|printCell
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%5.1f%%"
argument_list|,
name|diff
argument_list|)
argument_list|,
name|diffStr
operator|.
name|length
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|previous
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|printf
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doBench (Class<? extends Checksum> clazz, final int numThreads, final byte[] bytes, final int size)
specifier|private
specifier|static
name|BenchResult
name|doBench
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|int
name|numThreads
parameter_list|,
specifier|final
name|byte
index|[]
name|bytes
parameter_list|,
specifier|final
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|numThreads
index|]
decl_stmt|;
specifier|final
name|BenchResult
index|[]
name|results
init|=
operator|new
name|BenchResult
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
block|{
specifier|final
name|int
name|trials
init|=
name|BYTES_PER_SIZE
operator|/
name|size
decl_stmt|;
specifier|final
name|double
name|mbProcessed
init|=
name|trials
operator|*
name|size
operator|/
literal|1024.0
operator|/
literal|1024.0
decl_stmt|;
specifier|final
name|Constructor
argument_list|<
name|?
extends|extends
name|Checksum
argument_list|>
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|i
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
specifier|final
name|Checksum
name|crc
init|=
name|ctor
operator|.
name|newInstance
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|long
name|st
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|crc
operator|.
name|reset
argument_list|()
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
name|trials
condition|;
name|i
operator|++
control|)
block|{
name|crc
operator|.
name|update
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
specifier|final
name|long
name|et
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|double
name|secsElapsed
init|=
operator|(
name|et
operator|-
name|st
operator|)
operator|/
literal|1000000000.0d
decl_stmt|;
name|results
index|[
name|index
index|]
operator|=
operator|new
name|BenchResult
argument_list|(
name|crc
operator|.
name|getValue
argument_list|()
argument_list|,
name|mbProcessed
operator|/
name|secsElapsed
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
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
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|expected
init|=
name|results
index|[
literal|0
index|]
operator|.
name|value
decl_stmt|;
name|double
name|sum
init|=
name|results
index|[
literal|0
index|]
operator|.
name|mbps
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|results
index|[
name|i
index|]
operator|.
name|value
operator|!=
name|expected
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" results not matched."
argument_list|)
throw|;
block|}
name|sum
operator|+=
name|results
index|[
name|i
index|]
operator|.
name|mbps
expr_stmt|;
block|}
return|return
operator|new
name|BenchResult
argument_list|(
name|expected
argument_list|,
name|sum
operator|/
name|results
operator|.
name|length
argument_list|)
return|;
block|}
DECL|class|BenchResult
specifier|private
specifier|static
class|class
name|BenchResult
block|{
comment|/** CRC value */
DECL|field|value
specifier|final
name|long
name|value
decl_stmt|;
comment|/** Speed (MB per second) */
DECL|field|mbps
specifier|final
name|double
name|mbps
decl_stmt|;
DECL|method|BenchResult (long value, double mbps)
name|BenchResult
parameter_list|(
name|long
name|value
parameter_list|,
name|double
name|mbps
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|mbps
operator|=
name|mbps
expr_stmt|;
block|}
block|}
DECL|method|printSystemProperties (PrintStream out)
specifier|private
specifier|static
name|void
name|printSystemProperties
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|names
init|=
block|{
literal|"java.version"
block|,
literal|"java.runtime.name"
block|,
literal|"java.runtime.version"
block|,
literal|"java.vm.version"
block|,
literal|"java.vm.vendor"
block|,
literal|"java.vm.name"
block|,
literal|"java.vm.specification.version"
block|,
literal|"java.specification.version"
block|,
literal|"os.arch"
block|,
literal|"os.name"
block|,
literal|"os.version"
block|}
decl_stmt|;
specifier|final
name|Properties
name|p
init|=
name|System
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|n
range|:
name|names
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|n
operator|+
literal|" = "
operator|+
name|p
operator|.
name|getProperty
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

