begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
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
name|fs
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
name|io
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
name|io
operator|.
name|compress
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
name|util
operator|.
name|LineReader
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

begin_class
DECL|class|TestKeyValueTextInputFormat
specifier|public
class|class
name|TestKeyValueTextInputFormat
extends|extends
name|TestCase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestKeyValueTextInputFormat
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|MAX_LENGTH
specifier|private
specifier|static
name|int
name|MAX_LENGTH
init|=
literal|10000
decl_stmt|;
DECL|field|defaultConf
specifier|private
specifier|static
name|JobConf
name|defaultConf
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
init|=
literal|null
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|defaultConf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"init failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|workDir
specifier|private
specifier|static
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"."
argument_list|)
argument_list|,
literal|"data"
argument_list|)
argument_list|,
literal|"TestKeyValueTextInputFormat"
argument_list|)
decl_stmt|;
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
comment|// A reporter that does nothing
name|Reporter
name|reporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
name|int
name|seed
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"seed = "
operator|+
name|seed
argument_list|)
expr_stmt|;
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
decl_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
comment|// for a variety of lengths
for|for
control|(
name|int
name|length
init|=
literal|0
init|;
name|length
operator|<
name|MAX_LENGTH
condition|;
name|length
operator|+=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|10
argument_list|)
operator|+
literal|1
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating; entries = "
operator|+
name|length
argument_list|)
expr_stmt|;
comment|// create a file with length entries
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|localFs
operator|.
name|create
argument_list|(
name|file
argument_list|)
argument_list|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// try splitting the file in a variety of sizes
name|KeyValueTextInputFormat
name|format
init|=
operator|new
name|KeyValueTextInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|configure
argument_list|(
name|job
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numSplits
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|MAX_LENGTH
operator|/
literal|20
argument_list|)
operator|+
literal|1
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"splitting: requesting = "
operator|+
name|numSplits
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
name|numSplits
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"splitting: got =        "
operator|+
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// check each split
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|length
argument_list|)
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
name|splits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"split["
operator|+
name|j
operator|+
literal|"]= "
operator|+
name|splits
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|reader
init|=
name|format
operator|.
name|getRecordReader
argument_list|(
name|splits
index|[
name|j
index|]
argument_list|,
name|job
argument_list|,
name|reporter
argument_list|)
decl_stmt|;
name|Class
name|readerClass
init|=
name|reader
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"reader class is KeyValueLineRecordReader."
argument_list|,
name|KeyValueLineRecordReader
operator|.
name|class
argument_list|,
name|readerClass
argument_list|)
expr_stmt|;
name|Text
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|Class
name|keyClass
init|=
name|key
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|Text
name|value
init|=
name|reader
operator|.
name|createValue
argument_list|()
decl_stmt|;
name|Class
name|valueClass
init|=
name|value
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Key class is Text."
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|keyClass
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Value class is Text."
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|valueClass
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|int
name|v
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"read "
operator|+
name|v
argument_list|)
expr_stmt|;
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"conflict with "
operator|+
name|v
operator|+
literal|" in split "
operator|+
name|j
operator|+
literal|" at position "
operator|+
name|reader
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"Key in multiple partitions."
argument_list|,
name|bits
operator|.
name|get
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"splits["
operator|+
name|j
operator|+
literal|"]="
operator|+
name|splits
index|[
name|j
index|]
operator|+
literal|" count="
operator|+
name|count
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Some keys in no partition."
argument_list|,
name|length
argument_list|,
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|makeStream (String str)
specifier|private
name|LineReader
name|makeStream
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LineReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|str
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|,
name|defaultConf
argument_list|)
return|;
block|}
DECL|method|testUTF8 ()
specifier|public
name|void
name|testUTF8
parameter_list|()
throws|throws
name|Exception
block|{
name|LineReader
name|in
init|=
name|makeStream
argument_list|(
literal|"abcd\u20acbdcd\u20ac"
argument_list|)
decl_stmt|;
name|Text
name|line
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"readLine changed utf8 characters"
argument_list|,
literal|"abcd\u20acbdcd\u20ac"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|=
name|makeStream
argument_list|(
literal|"abc\u200axyz"
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"split on fake newline"
argument_list|,
literal|"abc\u200axyz"
argument_list|,
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNewLines ()
specifier|public
name|void
name|testNewLines
parameter_list|()
throws|throws
name|Exception
block|{
name|LineReader
name|in
init|=
name|makeStream
argument_list|(
literal|"a\nbb\n\nccc\rdddd\r\neeeee"
argument_list|)
decl_stmt|;
name|Text
name|out
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line1 length"
argument_list|,
literal|1
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line2 length"
argument_list|,
literal|2
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line3 length"
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line4 length"
argument_list|,
literal|3
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line5 length"
argument_list|,
literal|4
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"line5 length"
argument_list|,
literal|5
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"end of file"
argument_list|,
literal|0
argument_list|,
name|in
operator|.
name|readLine
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFile (FileSystem fs, Path name, CompressionCodec codec, String contents)
specifier|private
specifier|static
name|void
name|writeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|name
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|stm
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
name|stm
operator|=
name|fs
operator|.
name|create
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stm
operator|=
name|codec
operator|.
name|createOutputStream
argument_list|(
name|fs
operator|.
name|create
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|stm
operator|.
name|write
argument_list|(
name|contents
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|voidReporter
specifier|private
specifier|static
specifier|final
name|Reporter
name|voidReporter
init|=
name|Reporter
operator|.
name|NULL
decl_stmt|;
DECL|method|readSplit (KeyValueTextInputFormat format, InputSplit split, JobConf job)
specifier|private
specifier|static
name|List
argument_list|<
name|Text
argument_list|>
name|readSplit
parameter_list|(
name|KeyValueTextInputFormat
name|format
parameter_list|,
name|InputSplit
name|split
parameter_list|,
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Text
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Text
argument_list|>
argument_list|()
decl_stmt|;
name|RecordReader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|reader
init|=
name|format
operator|.
name|getRecordReader
argument_list|(
name|split
argument_list|,
name|job
argument_list|,
name|voidReporter
argument_list|)
decl_stmt|;
name|Text
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|Text
name|value
init|=
name|reader
operator|.
name|createValue
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
name|reader
operator|.
name|createValue
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Test using the gzip codec for reading    */
DECL|method|testGzip ()
specifier|public
specifier|static
name|void
name|testGzip
parameter_list|()
throws|throws
name|IOException
block|{
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|()
decl_stmt|;
name|CompressionCodec
name|gzip
init|=
operator|new
name|GzipCodec
argument_list|()
decl_stmt|;
name|ReflectionUtils
operator|.
name|setConf
argument_list|(
name|gzip
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|localFs
argument_list|,
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"part1.txt.gz"
argument_list|)
argument_list|,
name|gzip
argument_list|,
literal|"line-1\tthe quick\nline-2\tbrown\nline-3\tfox jumped\nline-4\tover\nline-5\t the lazy\nline-6\t dog\n"
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|localFs
argument_list|,
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"part2.txt.gz"
argument_list|)
argument_list|,
name|gzip
argument_list|,
literal|"line-1\tthis is a test\nline-1\tof gzip\n"
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|KeyValueTextInputFormat
name|format
init|=
operator|new
name|KeyValueTextInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|InputSplit
index|[]
name|splits
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"compressed splits == 2"
argument_list|,
literal|2
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
name|FileSplit
name|tmp
init|=
operator|(
name|FileSplit
operator|)
name|splits
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"part2.txt.gz"
argument_list|)
condition|)
block|{
name|splits
index|[
literal|0
index|]
operator|=
name|splits
index|[
literal|1
index|]
expr_stmt|;
name|splits
index|[
literal|1
index|]
operator|=
name|tmp
expr_stmt|;
block|}
name|List
argument_list|<
name|Text
argument_list|>
name|results
init|=
name|readSplit
argument_list|(
name|format
argument_list|,
name|splits
index|[
literal|0
index|]
argument_list|,
name|job
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"splits[0] length"
argument_list|,
literal|6
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits[0][5]"
argument_list|,
literal|" dog"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|5
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|results
operator|=
name|readSplit
argument_list|(
name|format
argument_list|,
name|splits
index|[
literal|1
index|]
argument_list|,
name|job
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits[1] length"
argument_list|,
literal|2
argument_list|,
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits[1][0]"
argument_list|,
literal|"this is a test"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits[1][1]"
argument_list|,
literal|"of gzip"
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
operator|new
name|TestKeyValueTextInputFormat
argument_list|()
operator|.
name|testFormat
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

