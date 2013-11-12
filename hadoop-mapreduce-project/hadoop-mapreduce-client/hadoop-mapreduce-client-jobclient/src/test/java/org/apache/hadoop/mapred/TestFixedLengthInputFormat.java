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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|Random
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
name|Log
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
name|LogFactory
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
name|BytesWritable
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
name|LongWritable
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
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestFixedLengthInputFormat
specifier|public
class|class
name|TestFixedLengthInputFormat
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
decl_stmt|;
DECL|field|defaultConf
specifier|private
specifier|static
name|Configuration
name|defaultConf
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
DECL|field|workDir
specifier|private
specifier|static
name|Path
name|workDir
decl_stmt|;
DECL|field|voidReporter
specifier|private
specifier|static
name|Reporter
name|voidReporter
decl_stmt|;
comment|// some chars for the record data
DECL|field|chars
specifier|private
specifier|static
name|char
index|[]
name|chars
decl_stmt|;
DECL|field|charRand
specifier|private
specifier|static
name|Random
name|charRand
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|onlyOnce ()
specifier|public
specifier|static
name|void
name|onlyOnce
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestFixedLengthInputFormat
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|defaultConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|defaultConf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|defaultConf
argument_list|)
expr_stmt|;
name|voidReporter
operator|=
name|Reporter
operator|.
name|NULL
expr_stmt|;
comment|// our set of chars
name|chars
operator|=
operator|(
literal|"abcdefghijklmnopqrstuvABCDEFGHIJKLMN OPQRSTUVWXYZ1234567890)"
operator|+
literal|"(*&^%$#@!-=><?:\"{}][';/.,']"
operator|)
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|workDir
operator|=
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
literal|"TestKeyValueFixedLengthInputFormat"
argument_list|)
expr_stmt|;
name|charRand
operator|=
operator|new
name|Random
argument_list|()
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
comment|/**    * 20 random tests of various record, file, and split sizes.  All tests have    * uncompressed file as input.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500000
argument_list|)
DECL|method|testFormat ()
specifier|public
name|void
name|testFormat
parameter_list|()
throws|throws
name|IOException
block|{
name|runRandomTests
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * 20 random tests of various record, file, and split sizes.  All tests have    * compressed file as input.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|500000
argument_list|)
DECL|method|testFormatCompressedIn ()
specifier|public
name|void
name|testFormatCompressedIn
parameter_list|()
throws|throws
name|IOException
block|{
name|runRandomTests
argument_list|(
operator|new
name|GzipCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with no record length set.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testNoRecordLength ()
specifier|public
name|void
name|testNoRecordLength
parameter_list|()
throws|throws
name|IOException
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
operator|new
name|String
argument_list|(
literal|"testFormat.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Set the fixed length record length config property
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
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
name|splits
index|[]
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|exceptionThrown
init|=
literal|false
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
try|try
block|{
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|exceptionThrown
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception message:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Exception for not setting record length:"
argument_list|,
name|exceptionThrown
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with record length set to 0    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testZeroRecordLength ()
specifier|public
name|void
name|testZeroRecordLength
parameter_list|()
throws|throws
name|IOException
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
operator|new
name|String
argument_list|(
literal|"testFormat.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Set the fixed length record length config property
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|setRecordLength
argument_list|(
name|job
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|format
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|InputSplit
name|splits
index|[]
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|exceptionThrown
init|=
literal|false
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
try|try
block|{
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|exceptionThrown
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception message:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Exception for zero record length:"
argument_list|,
name|exceptionThrown
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with record length set to a negative value    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testNegativeRecordLength ()
specifier|public
name|void
name|testNegativeRecordLength
parameter_list|()
throws|throws
name|IOException
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
operator|new
name|String
argument_list|(
literal|"testFormat.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|file
argument_list|,
literal|null
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Set the fixed length record length config property
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|setRecordLength
argument_list|(
name|job
argument_list|,
operator|-
literal|10
argument_list|)
expr_stmt|;
name|format
operator|.
name|configure
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|InputSplit
name|splits
index|[]
init|=
name|format
operator|.
name|getSplits
argument_list|(
name|job
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|boolean
name|exceptionThrown
init|=
literal|false
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
try|try
block|{
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|exceptionThrown
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception message:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Exception for negative record length:"
argument_list|,
name|exceptionThrown
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with partial record at the end of a compressed input file.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testPartialRecordCompressedIn ()
specifier|public
name|void
name|testPartialRecordCompressedIn
parameter_list|()
throws|throws
name|IOException
block|{
name|CompressionCodec
name|gzip
init|=
operator|new
name|GzipCodec
argument_list|()
decl_stmt|;
name|runPartialRecordTest
argument_list|(
name|gzip
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test with partial record at the end of an uncompressed input file.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testPartialRecordUncompressedIn ()
specifier|public
name|void
name|testPartialRecordUncompressedIn
parameter_list|()
throws|throws
name|IOException
block|{
name|runPartialRecordTest
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test using the gzip codec with two input files.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|5000
argument_list|)
DECL|method|testGzipWithTwoInputs ()
specifier|public
name|void
name|testGzipWithTwoInputs
parameter_list|()
throws|throws
name|IOException
block|{
name|CompressionCodec
name|gzip
init|=
operator|new
name|GzipCodec
argument_list|()
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
comment|// Create files with fixed length records with 5 byte long records.
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
literal|"one  two  threefour five six  seveneightnine ten  "
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
literal|"ten  nine eightsevensix  five four threetwo  one  "
argument_list|)
expr_stmt|;
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|setRecordLength
argument_list|(
name|defaultConf
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|ReflectionUtils
operator|.
name|setConf
argument_list|(
name|gzip
argument_list|,
name|job
argument_list|)
expr_stmt|;
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
name|String
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
literal|10
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
literal|"six  "
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|5
argument_list|)
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
literal|10
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
literal|"ten  "
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"splits[1][1]"
argument_list|,
literal|"nine "
argument_list|,
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create a file containing fixed length records with random data
DECL|method|createFile (Path targetFile, CompressionCodec codec, int recordLen, int numRecords)
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|createFile
parameter_list|(
name|Path
name|targetFile
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|int
name|recordLen
parameter_list|,
name|int
name|numRecords
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|recordList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|numRecords
argument_list|)
decl_stmt|;
name|OutputStream
name|ostream
init|=
name|localFs
operator|.
name|create
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|ostream
operator|=
name|codec
operator|.
name|createOutputStream
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
block|}
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|ostream
argument_list|)
decl_stmt|;
try|try
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|numRecords
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|recordLen
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|chars
index|[
name|charRand
operator|.
name|nextInt
argument_list|(
name|chars
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
name|String
name|recordData
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|recordList
operator|.
name|add
argument_list|(
name|recordData
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|recordData
argument_list|)
expr_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
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
return|return
name|recordList
return|;
block|}
DECL|method|runRandomTests (CompressionCodec codec)
specifier|private
name|void
name|runRandomTests
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|fileName
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"testFormat.txt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|.
name|append
argument_list|(
literal|".gz"
argument_list|)
expr_stmt|;
block|}
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
name|fileName
operator|.
name|toString
argument_list|()
argument_list|)
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
literal|"Seed = "
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
name|int
name|MAX_TESTS
init|=
literal|20
decl_stmt|;
name|LongWritable
name|key
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|value
init|=
operator|new
name|BytesWritable
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
name|MAX_TESTS
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"----------------------------------------------------------"
argument_list|)
expr_stmt|;
comment|// Maximum total records of 999
name|int
name|totalRecords
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|999
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// Test an empty file
if|if
condition|(
name|i
operator|==
literal|8
condition|)
block|{
name|totalRecords
operator|=
literal|0
expr_stmt|;
block|}
comment|// Maximum bytes in a record of 100K
name|int
name|recordLength
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|1024
operator|*
literal|100
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// For the 11th test, force a record length of 1
if|if
condition|(
name|i
operator|==
literal|10
condition|)
block|{
name|recordLength
operator|=
literal|1
expr_stmt|;
block|}
comment|// The total bytes in the test file
name|int
name|fileSize
init|=
operator|(
name|totalRecords
operator|*
name|recordLength
operator|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"totalRecords="
operator|+
name|totalRecords
operator|+
literal|" recordLength="
operator|+
name|recordLength
argument_list|)
expr_stmt|;
comment|// Create the test file
name|ArrayList
argument_list|<
name|String
argument_list|>
name|recordList
init|=
name|createFile
argument_list|(
name|file
argument_list|,
name|codec
argument_list|,
name|recordLength
argument_list|,
name|totalRecords
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|localFs
operator|.
name|exists
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the fixed length record length config property
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|FixedLengthInputFormat
operator|.
name|setRecordLength
argument_list|(
name|testConf
argument_list|,
name|recordLength
argument_list|)
expr_stmt|;
name|int
name|numSplits
init|=
literal|1
decl_stmt|;
comment|// Arbitrarily set number of splits.
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
operator|(
name|MAX_TESTS
operator|-
literal|1
operator|)
condition|)
block|{
comment|// Test a split size that is less than record len
name|numSplits
operator|=
call|(
name|int
call|)
argument_list|(
name|fileSize
operator|/
name|Math
operator|.
name|floor
argument_list|(
name|recordLength
operator|/
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|MAX_TESTS
operator|%
name|i
operator|==
literal|0
condition|)
block|{
comment|// Let us create a split size that is forced to be
comment|// smaller than the end file itself, (ensures 1+ splits)
name|numSplits
operator|=
name|fileSize
operator|/
operator|(
name|fileSize
operator|-
name|random
operator|.
name|nextInt
argument_list|(
name|fileSize
argument_list|)
operator|)
expr_stmt|;
block|}
else|else
block|{
comment|// Just pick a random split size with no upper bound
name|numSplits
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|fileSize
operator|/
name|random
operator|.
name|nextInt
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of splits set to: "
operator|+
name|numSplits
argument_list|)
expr_stmt|;
block|}
comment|// Create the job, and setup the input path
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
comment|// Try splitting the file in a variety of sizes
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
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
name|splits
index|[]
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
name|info
argument_list|(
literal|"Actual number of splits = "
operator|+
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Test combined split lengths = total file size
name|long
name|recordOffset
init|=
literal|0
decl_stmt|;
name|int
name|recordNumber
init|=
literal|0
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|reader
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"RecordReader class should be FixedLengthRecordReader:"
argument_list|,
name|FixedLengthRecordReader
operator|.
name|class
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
comment|// Plow through the records in this split
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
name|assertEquals
argument_list|(
literal|"Checking key"
argument_list|,
call|(
name|long
call|)
argument_list|(
name|recordNumber
operator|*
name|recordLength
argument_list|)
argument_list|,
name|key
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|valueString
init|=
operator|new
name|String
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Checking record length:"
argument_list|,
name|recordLength
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Checking for more records than expected:"
argument_list|,
name|recordNumber
operator|<
name|totalRecords
argument_list|)
expr_stmt|;
name|String
name|origRecord
init|=
name|recordList
operator|.
name|get
argument_list|(
name|recordNumber
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Checking record content:"
argument_list|,
name|origRecord
argument_list|,
name|valueString
argument_list|)
expr_stmt|;
name|recordNumber
operator|++
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Total original records should be total read records:"
argument_list|,
name|recordList
operator|.
name|size
argument_list|()
argument_list|,
name|recordNumber
argument_list|)
expr_stmt|;
block|}
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
DECL|method|readSplit (FixedLengthInputFormat format, InputSplit split, JobConf job)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|readSplit
parameter_list|(
name|FixedLengthInputFormat
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
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|RecordReader
argument_list|<
name|LongWritable
argument_list|,
name|BytesWritable
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
name|LongWritable
name|key
init|=
name|reader
operator|.
name|createKey
argument_list|()
decl_stmt|;
name|BytesWritable
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
operator|new
name|String
argument_list|(
name|value
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|runPartialRecordTest (CompressionCodec codec)
specifier|private
name|void
name|runPartialRecordTest
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
throws|throws
name|IOException
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|workDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Create a file with fixed length records with 5 byte long
comment|// records with a partial record at the end.
name|StringBuilder
name|fileName
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"testFormat.txt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|fileName
operator|.
name|append
argument_list|(
literal|".gz"
argument_list|)
expr_stmt|;
block|}
name|writeFile
argument_list|(
name|localFs
argument_list|,
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
name|fileName
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|codec
argument_list|,
literal|"one  two  threefour five six  seveneightnine ten"
argument_list|)
expr_stmt|;
name|FixedLengthInputFormat
name|format
init|=
operator|new
name|FixedLengthInputFormat
argument_list|()
decl_stmt|;
name|format
operator|.
name|setRecordLength
argument_list|(
name|defaultConf
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|defaultConf
argument_list|)
decl_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|ReflectionUtils
operator|.
name|setConf
argument_list|(
name|codec
argument_list|,
name|job
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|codec
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"compressed splits == 1"
argument_list|,
literal|1
argument_list|,
name|splits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|boolean
name|exceptionThrown
init|=
literal|false
decl_stmt|;
for|for
control|(
name|InputSplit
name|split
range|:
name|splits
control|)
block|{
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
name|readSplit
argument_list|(
name|format
argument_list|,
name|split
argument_list|,
name|job
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|exceptionThrown
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception message:"
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Exception for partial record:"
argument_list|,
name|exceptionThrown
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

