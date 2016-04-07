begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Option
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
name|cli
operator|.
name|OptionBuilder
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
name|cli
operator|.
name|Options
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
name|cli
operator|.
name|ParseException
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|file
operator|.
name|tfile
operator|.
name|RandomDistribution
operator|.
name|DiscreteRNG
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
name|file
operator|.
name|tfile
operator|.
name|TFile
operator|.
name|Reader
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
name|file
operator|.
name|tfile
operator|.
name|TFile
operator|.
name|Writer
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
name|file
operator|.
name|tfile
operator|.
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_comment
comment|/**  * test the performance for seek.  *  */
end_comment

begin_class
DECL|class|TestTFileSeek
specifier|public
class|class
name|TestTFileSeek
block|{
DECL|field|options
specifier|private
name|MyOptions
name|options
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|path
specifier|private
name|Path
name|path
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|timer
specifier|private
name|NanoTimer
name|timer
decl_stmt|;
DECL|field|rng
specifier|private
name|Random
name|rng
decl_stmt|;
DECL|field|keyLenGen
specifier|private
name|DiscreteRNG
name|keyLenGen
decl_stmt|;
DECL|field|kvGen
specifier|private
name|KVGenerator
name|kvGen
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|options
operator|==
literal|null
condition|)
block|{
name|options
operator|=
operator|new
name|MyOptions
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"tfile.fs.input.buffer.size"
argument_list|,
name|options
operator|.
name|fsInputBufferSize
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"tfile.fs.output.buffer.size"
argument_list|,
name|options
operator|.
name|fsOutputBufferSize
argument_list|)
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|options
operator|.
name|rootDir
argument_list|)
argument_list|,
name|options
operator|.
name|file
argument_list|)
expr_stmt|;
name|fs
operator|=
name|path
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|timer
operator|=
operator|new
name|NanoTimer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rng
operator|=
operator|new
name|Random
argument_list|(
name|options
operator|.
name|seed
argument_list|)
expr_stmt|;
name|keyLenGen
operator|=
operator|new
name|RandomDistribution
operator|.
name|Zipf
argument_list|(
operator|new
name|Random
argument_list|(
name|rng
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|options
operator|.
name|minKeyLen
argument_list|,
name|options
operator|.
name|maxKeyLen
argument_list|,
literal|1.2
argument_list|)
expr_stmt|;
name|DiscreteRNG
name|valLenGen
init|=
operator|new
name|RandomDistribution
operator|.
name|Flat
argument_list|(
operator|new
name|Random
argument_list|(
name|rng
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|options
operator|.
name|minValLength
argument_list|,
name|options
operator|.
name|maxValLength
argument_list|)
decl_stmt|;
name|DiscreteRNG
name|wordLenGen
init|=
operator|new
name|RandomDistribution
operator|.
name|Flat
argument_list|(
operator|new
name|Random
argument_list|(
name|rng
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|,
name|options
operator|.
name|minWordLen
argument_list|,
name|options
operator|.
name|maxWordLen
argument_list|)
decl_stmt|;
name|kvGen
operator|=
operator|new
name|KVGenerator
argument_list|(
name|rng
argument_list|,
literal|true
argument_list|,
name|keyLenGen
argument_list|,
name|valLenGen
argument_list|,
name|wordLenGen
argument_list|,
name|options
operator|.
name|dictSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|createFSOutput (Path name, FileSystem fs)
specifier|private
specifier|static
name|FSDataOutputStream
name|createFSOutput
parameter_list|(
name|Path
name|name
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|FSDataOutputStream
name|fout
init|=
name|fs
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|fout
return|;
block|}
DECL|method|createTFile ()
specifier|private
name|void
name|createTFile
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
name|FSDataOutputStream
name|fout
init|=
name|createFSOutput
argument_list|(
name|path
argument_list|,
name|fs
argument_list|)
decl_stmt|;
try|try
block|{
name|Writer
name|writer
init|=
operator|new
name|Writer
argument_list|(
name|fout
argument_list|,
name|options
operator|.
name|minBlockSize
argument_list|,
name|options
operator|.
name|compress
argument_list|,
literal|"memcmp"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|BytesWritable
name|key
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|val
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|timer
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
literal|true
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
comment|// test the size for every 1000 rows.
if|if
condition|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
operator|>=
name|options
operator|.
name|fileSize
condition|)
block|{
break|break;
block|}
block|}
name|kvGen
operator|.
name|next
argument_list|(
name|key
argument_list|,
name|val
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|,
name|val
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|totalBytes
operator|+=
name|key
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|totalBytes
operator|+=
name|val
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|fout
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|double
name|duration
init|=
operator|(
name|double
operator|)
name|timer
operator|.
name|read
argument_list|()
operator|/
literal|1000
decl_stmt|;
comment|// in us.
name|long
name|fsize
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"time: %s...uncompressed: %.2fMB...raw thrpt: %.2fMB/s\n"
argument_list|,
name|timer
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|double
operator|)
name|totalBytes
operator|/
literal|1024
operator|/
literal|1024
argument_list|,
name|totalBytes
operator|/
name|duration
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"time: %s...file size: %.2fMB...disk thrpt: %.2fMB/s\n"
argument_list|,
name|timer
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|double
operator|)
name|fsize
operator|/
literal|1024
operator|/
literal|1024
argument_list|,
name|fsize
operator|/
name|duration
argument_list|)
expr_stmt|;
block|}
DECL|method|seekTFile ()
specifier|public
name|void
name|seekTFile
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|miss
init|=
literal|0
decl_stmt|;
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
name|FSDataInputStream
name|fsdis
init|=
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fsdis
argument_list|,
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|KeySampler
name|kSampler
init|=
operator|new
name|KeySampler
argument_list|(
name|rng
argument_list|,
name|reader
operator|.
name|getFirstKey
argument_list|()
argument_list|,
name|reader
operator|.
name|getLastKey
argument_list|()
argument_list|,
name|keyLenGen
argument_list|)
decl_stmt|;
name|Scanner
name|scanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
decl_stmt|;
name|BytesWritable
name|key
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|BytesWritable
name|val
init|=
operator|new
name|BytesWritable
argument_list|()
decl_stmt|;
name|timer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|timer
operator|.
name|start
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
name|options
operator|.
name|seekCount
condition|;
operator|++
name|i
control|)
block|{
name|kSampler
operator|.
name|next
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|lowerBound
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|scanner
operator|.
name|atEnd
argument_list|()
condition|)
block|{
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|totalBytes
operator|+=
name|key
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|totalBytes
operator|+=
name|val
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|++
name|miss
expr_stmt|;
block|}
block|}
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
name|double
name|duration
init|=
operator|(
name|double
operator|)
name|timer
operator|.
name|read
argument_list|()
operator|/
literal|1000
decl_stmt|;
comment|// in us.
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"time: %s...avg seek: %s...%d hit...%d miss...avg I/O size: %.2fKB\n"
argument_list|,
name|timer
operator|.
name|toString
argument_list|()
argument_list|,
name|NanoTimer
operator|.
name|nanoTimeToString
argument_list|(
name|timer
operator|.
name|read
argument_list|()
operator|/
name|options
operator|.
name|seekCount
argument_list|)
argument_list|,
name|options
operator|.
name|seekCount
operator|-
name|miss
argument_list|,
name|miss
argument_list|,
operator|(
name|double
operator|)
name|totalBytes
operator|/
literal|1024
operator|/
operator|(
name|options
operator|.
name|seekCount
operator|-
name|miss
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSeeks ()
specifier|public
name|void
name|testSeeks
parameter_list|()
throws|throws
name|IOException
block|{
name|String
index|[]
name|supported
init|=
name|TFile
operator|.
name|getSupportedCompressionAlgorithms
argument_list|()
decl_stmt|;
name|boolean
name|proceed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|supported
control|)
block|{
if|if
condition|(
name|c
operator|.
name|equals
argument_list|(
name|options
operator|.
name|compress
argument_list|)
condition|)
block|{
name|proceed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|proceed
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skipped for "
operator|+
name|options
operator|.
name|compress
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|options
operator|.
name|doCreate
argument_list|()
condition|)
block|{
name|createTFile
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|options
operator|.
name|doRead
argument_list|()
condition|)
block|{
name|seekTFile
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|IntegerRange
specifier|private
specifier|static
class|class
name|IntegerRange
block|{
DECL|field|from
DECL|field|to
specifier|private
specifier|final
name|int
name|from
decl_stmt|,
name|to
decl_stmt|;
DECL|method|IntegerRange (int from, int to)
specifier|public
name|IntegerRange
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
DECL|method|parse (String s)
specifier|public
specifier|static
name|IntegerRange
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|ParseException
block|{
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|s
argument_list|,
literal|" \t,"
argument_list|)
decl_stmt|;
if|if
condition|(
name|st
operator|.
name|countTokens
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Bad integer specification: "
operator|+
name|s
argument_list|)
throw|;
block|}
name|int
name|from
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|to
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|st
operator|.
name|nextToken
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntegerRange
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
DECL|method|from ()
specifier|public
name|int
name|from
parameter_list|()
block|{
return|return
name|from
return|;
block|}
DECL|method|to ()
specifier|public
name|int
name|to
parameter_list|()
block|{
return|return
name|to
return|;
block|}
block|}
DECL|class|MyOptions
specifier|private
specifier|static
class|class
name|MyOptions
block|{
comment|// hard coded constants
DECL|field|dictSize
name|int
name|dictSize
init|=
literal|1000
decl_stmt|;
DECL|field|minWordLen
name|int
name|minWordLen
init|=
literal|5
decl_stmt|;
DECL|field|maxWordLen
name|int
name|maxWordLen
init|=
literal|20
decl_stmt|;
DECL|field|osInputBufferSize
name|int
name|osInputBufferSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|osOutputBufferSize
name|int
name|osOutputBufferSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|fsInputBufferSizeNone
name|int
name|fsInputBufferSizeNone
init|=
literal|0
decl_stmt|;
DECL|field|fsInputBufferSizeLzo
name|int
name|fsInputBufferSizeLzo
init|=
literal|0
decl_stmt|;
DECL|field|fsInputBufferSizeGz
name|int
name|fsInputBufferSizeGz
init|=
literal|0
decl_stmt|;
DECL|field|fsOutputBufferSizeNone
name|int
name|fsOutputBufferSizeNone
init|=
literal|1
decl_stmt|;
DECL|field|fsOutputBufferSizeLzo
name|int
name|fsOutputBufferSizeLzo
init|=
literal|1
decl_stmt|;
DECL|field|fsOutputBufferSizeGz
name|int
name|fsOutputBufferSizeGz
init|=
literal|1
decl_stmt|;
DECL|field|rootDir
name|String
name|rootDir
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|file
name|String
name|file
init|=
literal|"TestTFileSeek"
decl_stmt|;
DECL|field|compress
name|String
name|compress
init|=
literal|"gz"
decl_stmt|;
DECL|field|minKeyLen
name|int
name|minKeyLen
init|=
literal|10
decl_stmt|;
DECL|field|maxKeyLen
name|int
name|maxKeyLen
init|=
literal|50
decl_stmt|;
DECL|field|minValLength
name|int
name|minValLength
init|=
literal|100
decl_stmt|;
DECL|field|maxValLength
name|int
name|maxValLength
init|=
literal|200
decl_stmt|;
DECL|field|minBlockSize
name|int
name|minBlockSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
DECL|field|fsOutputBufferSize
name|int
name|fsOutputBufferSize
init|=
literal|1
decl_stmt|;
DECL|field|fsInputBufferSize
name|int
name|fsInputBufferSize
init|=
literal|0
decl_stmt|;
DECL|field|fileSize
name|long
name|fileSize
init|=
literal|3
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|seekCount
name|long
name|seekCount
init|=
literal|1000
decl_stmt|;
DECL|field|seed
name|long
name|seed
decl_stmt|;
DECL|field|OP_CREATE
specifier|static
specifier|final
name|int
name|OP_CREATE
init|=
literal|1
decl_stmt|;
DECL|field|OP_READ
specifier|static
specifier|final
name|int
name|OP_READ
init|=
literal|2
decl_stmt|;
DECL|field|op
name|int
name|op
init|=
name|OP_CREATE
operator||
name|OP_READ
decl_stmt|;
DECL|field|proceed
name|boolean
name|proceed
init|=
literal|false
decl_stmt|;
DECL|method|MyOptions (String[] args)
specifier|public
name|MyOptions
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|seed
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
try|try
block|{
name|Options
name|opts
init|=
name|buildOptions
argument_list|()
decl_stmt|;
name|CommandLineParser
name|parser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|line
init|=
name|parser
operator|.
name|parse
argument_list|(
name|opts
argument_list|,
name|args
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|processOptions
argument_list|(
name|line
argument_list|,
name|opts
argument_list|)
expr_stmt|;
name|validateOptions
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Try \"--help\" option for details."
argument_list|)
expr_stmt|;
name|setStopProceed
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|proceed ()
specifier|public
name|boolean
name|proceed
parameter_list|()
block|{
return|return
name|proceed
return|;
block|}
DECL|method|buildOptions ()
specifier|private
name|Options
name|buildOptions
parameter_list|()
block|{
name|Option
name|compress
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"compress"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"[none|lzo|gz]"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"compression scheme"
argument_list|)
operator|.
name|create
argument_list|(
literal|'c'
argument_list|)
decl_stmt|;
name|Option
name|fileSize
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"file-size"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"size-in-MB"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"target size of the file (in MB)."
argument_list|)
operator|.
name|create
argument_list|(
literal|'s'
argument_list|)
decl_stmt|;
name|Option
name|fsInputBufferSz
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"fs-input-buffer"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"size"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"size of the file system input buffer (in bytes)."
argument_list|)
operator|.
name|create
argument_list|(
literal|'i'
argument_list|)
decl_stmt|;
name|Option
name|fsOutputBufferSize
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"fs-output-buffer"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"size"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"size of the file system output buffer (in bytes)."
argument_list|)
operator|.
name|create
argument_list|(
literal|'o'
argument_list|)
decl_stmt|;
name|Option
name|keyLen
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"key-length"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"min,max"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"the length range of the key (in bytes)"
argument_list|)
operator|.
name|create
argument_list|(
literal|'k'
argument_list|)
decl_stmt|;
name|Option
name|valueLen
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"value-length"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"min,max"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"the length range of the value (in bytes)"
argument_list|)
operator|.
name|create
argument_list|(
literal|'v'
argument_list|)
decl_stmt|;
name|Option
name|blockSz
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"block"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"size-in-KB"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"minimum block size (in KB)"
argument_list|)
operator|.
name|create
argument_list|(
literal|'b'
argument_list|)
decl_stmt|;
name|Option
name|seed
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"seed"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"long-int"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify the seed"
argument_list|)
operator|.
name|create
argument_list|(
literal|'S'
argument_list|)
decl_stmt|;
name|Option
name|operation
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"operation"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"r|w|rw"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"action: seek-only, create-only, seek-after-create"
argument_list|)
operator|.
name|create
argument_list|(
literal|'x'
argument_list|)
decl_stmt|;
name|Option
name|rootDir
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"root-dir"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"path"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify root directory where files will be created."
argument_list|)
operator|.
name|create
argument_list|(
literal|'r'
argument_list|)
decl_stmt|;
name|Option
name|file
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"file"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"name"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify the file name to be created or read."
argument_list|)
operator|.
name|create
argument_list|(
literal|'f'
argument_list|)
decl_stmt|;
name|Option
name|seekCount
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"seek"
argument_list|)
operator|.
name|withArgName
argument_list|(
literal|"count"
argument_list|)
operator|.
name|hasArg
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|"specify how many seek operations we perform (requires -x r or -x rw."
argument_list|)
operator|.
name|create
argument_list|(
literal|'n'
argument_list|)
decl_stmt|;
name|Option
name|help
init|=
name|OptionBuilder
operator|.
name|withLongOpt
argument_list|(
literal|"help"
argument_list|)
operator|.
name|hasArg
argument_list|(
literal|false
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"show this screen"
argument_list|)
operator|.
name|create
argument_list|(
literal|"h"
argument_list|)
decl_stmt|;
return|return
operator|new
name|Options
argument_list|()
operator|.
name|addOption
argument_list|(
name|compress
argument_list|)
operator|.
name|addOption
argument_list|(
name|fileSize
argument_list|)
operator|.
name|addOption
argument_list|(
name|fsInputBufferSz
argument_list|)
operator|.
name|addOption
argument_list|(
name|fsOutputBufferSize
argument_list|)
operator|.
name|addOption
argument_list|(
name|keyLen
argument_list|)
operator|.
name|addOption
argument_list|(
name|blockSz
argument_list|)
operator|.
name|addOption
argument_list|(
name|rootDir
argument_list|)
operator|.
name|addOption
argument_list|(
name|valueLen
argument_list|)
operator|.
name|addOption
argument_list|(
name|operation
argument_list|)
operator|.
name|addOption
argument_list|(
name|seekCount
argument_list|)
operator|.
name|addOption
argument_list|(
name|file
argument_list|)
operator|.
name|addOption
argument_list|(
name|help
argument_list|)
return|;
block|}
DECL|method|processOptions (CommandLine line, Options opts)
specifier|private
name|void
name|processOptions
parameter_list|(
name|CommandLine
name|line
parameter_list|,
name|Options
name|opts
parameter_list|)
throws|throws
name|ParseException
block|{
comment|// --help -h and --version -V must be processed first.
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'h'
argument_list|)
condition|)
block|{
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TFile and SeqFile benchmark."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
literal|100
argument_list|,
literal|"java ... TestTFileSeqFileComparison [options]"
argument_list|,
literal|"\nSupported options:"
argument_list|,
name|opts
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'c'
argument_list|)
condition|)
block|{
name|compress
operator|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'c'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'d'
argument_list|)
condition|)
block|{
name|dictSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'d'
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'s'
argument_list|)
condition|)
block|{
name|fileSize
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'s'
argument_list|)
argument_list|)
operator|*
literal|1024
operator|*
literal|1024
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'i'
argument_list|)
condition|)
block|{
name|fsInputBufferSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'i'
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'o'
argument_list|)
condition|)
block|{
name|fsOutputBufferSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'o'
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'n'
argument_list|)
condition|)
block|{
name|seekCount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'n'
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'k'
argument_list|)
condition|)
block|{
name|IntegerRange
name|ir
init|=
name|IntegerRange
operator|.
name|parse
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'k'
argument_list|)
argument_list|)
decl_stmt|;
name|minKeyLen
operator|=
name|ir
operator|.
name|from
argument_list|()
expr_stmt|;
name|maxKeyLen
operator|=
name|ir
operator|.
name|to
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'v'
argument_list|)
condition|)
block|{
name|IntegerRange
name|ir
init|=
name|IntegerRange
operator|.
name|parse
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'v'
argument_list|)
argument_list|)
decl_stmt|;
name|minValLength
operator|=
name|ir
operator|.
name|from
argument_list|()
expr_stmt|;
name|maxValLength
operator|=
name|ir
operator|.
name|to
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'b'
argument_list|)
condition|)
block|{
name|minBlockSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'b'
argument_list|)
argument_list|)
operator|*
literal|1024
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'r'
argument_list|)
condition|)
block|{
name|rootDir
operator|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'r'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'f'
argument_list|)
condition|)
block|{
name|file
operator|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'f'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'S'
argument_list|)
condition|)
block|{
name|seed
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'S'
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|'x'
argument_list|)
condition|)
block|{
name|String
name|strOp
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|'x'
argument_list|)
decl_stmt|;
if|if
condition|(
name|strOp
operator|.
name|equals
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
name|op
operator|=
name|OP_READ
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strOp
operator|.
name|equals
argument_list|(
literal|"w"
argument_list|)
condition|)
block|{
name|op
operator|=
name|OP_CREATE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|strOp
operator|.
name|equals
argument_list|(
literal|"rw"
argument_list|)
condition|)
block|{
name|op
operator|=
name|OP_CREATE
operator||
name|OP_READ
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unknown action specifier: "
operator|+
name|strOp
argument_list|)
throw|;
block|}
block|}
name|proceed
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|validateOptions ()
specifier|private
name|void
name|validateOptions
parameter_list|()
throws|throws
name|ParseException
block|{
if|if
condition|(
operator|!
name|compress
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
operator|&&
operator|!
name|compress
operator|.
name|equals
argument_list|(
literal|"lzo"
argument_list|)
operator|&&
operator|!
name|compress
operator|.
name|equals
argument_list|(
literal|"gz"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Unknown compression scheme: "
operator|+
name|compress
argument_list|)
throw|;
block|}
if|if
condition|(
name|minKeyLen
operator|>=
name|maxKeyLen
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Max key length must be greater than min key length."
argument_list|)
throw|;
block|}
if|if
condition|(
name|minValLength
operator|>=
name|maxValLength
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Max value length must be greater than min value length."
argument_list|)
throw|;
block|}
if|if
condition|(
name|minWordLen
operator|>=
name|maxWordLen
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"Max word length must be greater than min word length."
argument_list|)
throw|;
block|}
return|return;
block|}
DECL|method|setStopProceed ()
specifier|private
name|void
name|setStopProceed
parameter_list|()
block|{
name|proceed
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|doCreate ()
specifier|public
name|boolean
name|doCreate
parameter_list|()
block|{
return|return
operator|(
name|op
operator|&
name|OP_CREATE
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|doRead ()
specifier|public
name|boolean
name|doRead
parameter_list|()
block|{
return|return
operator|(
name|op
operator|&
name|OP_READ
operator|)
operator|!=
literal|0
return|;
block|}
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
block|{
name|TestTFileSeek
name|testCase
init|=
operator|new
name|TestTFileSeek
argument_list|()
decl_stmt|;
name|MyOptions
name|options
init|=
operator|new
name|MyOptions
argument_list|(
name|argv
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|proceed
operator|==
literal|false
condition|)
block|{
return|return;
block|}
name|testCase
operator|.
name|options
operator|=
name|options
expr_stmt|;
name|testCase
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|testCase
operator|.
name|testSeeks
argument_list|()
expr_stmt|;
name|testCase
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

