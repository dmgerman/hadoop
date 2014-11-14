begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.input
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|net
operator|.
name|URL
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|compress
operator|.
name|compressors
operator|.
name|bzip2
operator|.
name|BZip2CompressorInputStream
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
name|compress
operator|.
name|BZip2Codec
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
name|CodecPool
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
name|Decompressor
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
name|mapreduce
operator|.
name|TaskAttemptContext
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
name|mapreduce
operator|.
name|TaskAttemptID
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
name|mapreduce
operator|.
name|task
operator|.
name|TaskAttemptContextImpl
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

begin_class
DECL|class|TestLineRecordReader
specifier|public
class|class
name|TestLineRecordReader
block|{
DECL|method|testSplitRecords (String testFileName, long firstSplitLength)
specifier|private
name|void
name|testSplitRecords
parameter_list|(
name|String
name|testFileName
parameter_list|,
name|long
name|firstSplitLength
parameter_list|)
throws|throws
name|IOException
block|{
name|URL
name|testFileUrl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|testFileName
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Cannot find "
operator|+
name|testFileName
argument_list|,
name|testFileUrl
argument_list|)
expr_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testFileUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|testFileSize
init|=
name|testFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|Path
name|testFilePath
init|=
operator|new
name|Path
argument_list|(
name|testFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|LineRecordReader
operator|.
name|MAX_LINE_LENGTH
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"unexpected test data at "
operator|+
name|testFile
argument_list|,
name|testFileSize
operator|>
name|firstSplitLength
argument_list|)
expr_stmt|;
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
operator|new
name|TaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
comment|// read the data without splitting to count the records
name|FileSplit
name|split
init|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
literal|0
argument_list|,
name|testFileSize
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|LineRecordReader
name|reader
init|=
operator|new
name|LineRecordReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|int
name|numRecordsNoSplits
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
operator|++
name|numRecordsNoSplits
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// count the records in the first split
name|split
operator|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
literal|0
argument_list|,
name|firstSplitLength
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|LineRecordReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|int
name|numRecordsFirstSplit
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
operator|++
name|numRecordsFirstSplit
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// count the records in the second split
name|split
operator|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
name|firstSplitLength
argument_list|,
name|testFileSize
operator|-
name|firstSplitLength
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|LineRecordReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|int
name|numRecordsRemainingSplits
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
operator|++
name|numRecordsRemainingSplits
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected number of records in bzip2 compressed split"
argument_list|,
name|numRecordsNoSplits
argument_list|,
name|numRecordsFirstSplit
operator|+
name|numRecordsRemainingSplits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBzip2SplitEndsAtCR ()
specifier|public
name|void
name|testBzip2SplitEndsAtCR
parameter_list|()
throws|throws
name|IOException
block|{
comment|// the test data contains a carriage-return at the end of the first
comment|// split which ends at compressed offset 136498 and the next
comment|// character is not a linefeed
name|testSplitRecords
argument_list|(
literal|"blockEndingInCR.txt.bz2"
argument_list|,
literal|136498
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBzip2SplitEndsAtCRThenLF ()
specifier|public
name|void
name|testBzip2SplitEndsAtCRThenLF
parameter_list|()
throws|throws
name|IOException
block|{
comment|// the test data contains a carriage-return at the end of the first
comment|// split which ends at compressed offset 136498 and the next
comment|// character is a linefeed
name|testSplitRecords
argument_list|(
literal|"blockEndingInCRThenLF.txt.bz2"
argument_list|,
literal|136498
argument_list|)
expr_stmt|;
block|}
comment|// Use the LineRecordReader to read records from the file
DECL|method|readRecords (URL testFileUrl, int splitSize)
specifier|public
name|ArrayList
argument_list|<
name|String
argument_list|>
name|readRecords
parameter_list|(
name|URL
name|testFileUrl
parameter_list|,
name|int
name|splitSize
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Set up context
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testFileUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|testFileSize
init|=
name|testFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|Path
name|testFilePath
init|=
operator|new
name|Path
argument_list|(
name|testFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
operator|new
name|TaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
comment|// Gather the records returned by the record reader
name|ArrayList
argument_list|<
name|String
argument_list|>
name|records
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|long
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|testFileSize
condition|)
block|{
name|FileSplit
name|split
init|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
name|offset
argument_list|,
name|splitSize
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LineRecordReader
name|reader
init|=
operator|new
name|LineRecordReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
name|records
operator|.
name|add
argument_list|(
name|reader
operator|.
name|getCurrentValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|offset
operator|+=
name|splitSize
expr_stmt|;
block|}
return|return
name|records
return|;
block|}
comment|// Gather the records by just splitting on new lines
DECL|method|readRecordsDirectly (URL testFileUrl, boolean bzip)
specifier|public
name|String
index|[]
name|readRecordsDirectly
parameter_list|(
name|URL
name|testFileUrl
parameter_list|,
name|boolean
name|bzip
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|MAX_DATA_SIZE
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|MAX_DATA_SIZE
index|]
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|testFileUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|count
decl_stmt|;
if|if
condition|(
name|bzip
condition|)
block|{
name|BZip2CompressorInputStream
name|bzIn
init|=
operator|new
name|BZip2CompressorInputStream
argument_list|(
name|fis
argument_list|)
decl_stmt|;
name|count
operator|=
name|bzIn
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|bzIn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|count
operator|=
name|fis
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Test file data too big for buffer"
argument_list|,
name|count
operator|<
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|count
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
return|;
block|}
DECL|method|checkRecordSpanningMultipleSplits (String testFile, int splitSize, boolean bzip)
specifier|public
name|void
name|checkRecordSpanningMultipleSplits
parameter_list|(
name|String
name|testFile
parameter_list|,
name|int
name|splitSize
parameter_list|,
name|boolean
name|bzip
parameter_list|)
throws|throws
name|IOException
block|{
name|URL
name|testFileUrl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|records
init|=
name|readRecords
argument_list|(
name|testFileUrl
argument_list|,
name|splitSize
argument_list|)
decl_stmt|;
name|String
index|[]
name|actuals
init|=
name|readRecordsDirectly
argument_list|(
name|testFileUrl
argument_list|,
name|bzip
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of records"
argument_list|,
name|actuals
operator|.
name|length
argument_list|,
name|records
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|hasLargeRecord
init|=
literal|false
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
name|actuals
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|assertEquals
argument_list|(
name|actuals
index|[
name|i
index|]
argument_list|,
name|records
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|actuals
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|2
operator|*
name|splitSize
condition|)
block|{
name|hasLargeRecord
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"Invalid test data. Doesn't have a large enough record"
argument_list|,
name|hasLargeRecord
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecordSpanningMultipleSplits ()
specifier|public
name|void
name|testRecordSpanningMultipleSplits
parameter_list|()
throws|throws
name|IOException
block|{
name|checkRecordSpanningMultipleSplits
argument_list|(
literal|"recordSpanningMultipleSplits.txt"
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRecordSpanningMultipleSplitsCompressed ()
specifier|public
name|void
name|testRecordSpanningMultipleSplitsCompressed
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The file is generated with bz2 block size of 100k. The split size
comment|// needs to be larger than that for the CompressedSplitLineReader to
comment|// work.
name|checkRecordSpanningMultipleSplits
argument_list|(
literal|"recordSpanningMultipleSplits.txt.bz2"
argument_list|,
literal|200
operator|*
literal|1000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStripBOM ()
specifier|public
name|void
name|testStripBOM
parameter_list|()
throws|throws
name|IOException
block|{
comment|// the test data contains a BOM at the start of the file
comment|// confirm the BOM is skipped by LineRecordReader
name|String
name|UTF8_BOM
init|=
literal|"\uFEFF"
decl_stmt|;
name|URL
name|testFileUrl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"testBOM.txt"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Cannot find testBOM.txt"
argument_list|,
name|testFileUrl
argument_list|)
expr_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testFileUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|testFilePath
init|=
operator|new
name|Path
argument_list|(
name|testFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|testFileSize
init|=
name|testFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|LineRecordReader
operator|.
name|MAX_LINE_LENGTH
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
operator|new
name|TaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
comment|// read the data and check whether BOM is skipped
name|FileSplit
name|split
init|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
literal|0
argument_list|,
name|testFileSize
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|LineRecordReader
name|reader
init|=
operator|new
name|LineRecordReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|int
name|numRecords
init|=
literal|0
decl_stmt|;
name|boolean
name|firstLine
init|=
literal|true
decl_stmt|;
name|boolean
name|skipBOM
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|firstLine
condition|)
block|{
name|firstLine
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|getCurrentValue
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|UTF8_BOM
argument_list|)
condition|)
block|{
name|skipBOM
operator|=
literal|false
expr_stmt|;
block|}
block|}
operator|++
name|numRecords
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"BOM is not skipped"
argument_list|,
name|skipBOM
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleClose ()
specifier|public
name|void
name|testMultipleClose
parameter_list|()
throws|throws
name|IOException
block|{
name|URL
name|testFileUrl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"recordSpanningMultipleSplits.txt.bz2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Cannot find recordSpanningMultipleSplits.txt.bz2"
argument_list|,
name|testFileUrl
argument_list|)
expr_stmt|;
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|testFileUrl
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|Path
name|testFilePath
init|=
operator|new
name|Path
argument_list|(
name|testFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|testFileSize
init|=
name|testFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|LineRecordReader
operator|.
name|MAX_LINE_LENGTH
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|TaskAttemptContext
name|context
init|=
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
operator|new
name|TaskAttemptID
argument_list|()
argument_list|)
decl_stmt|;
comment|// read the data and check whether BOM is skipped
name|FileSplit
name|split
init|=
operator|new
name|FileSplit
argument_list|(
name|testFilePath
argument_list|,
literal|0
argument_list|,
name|testFileSize
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LineRecordReader
name|reader
init|=
operator|new
name|LineRecordReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|initialize
argument_list|(
name|split
argument_list|,
name|context
argument_list|)
expr_stmt|;
comment|//noinspection StatementWithEmptyBody
while|while
condition|(
name|reader
operator|.
name|nextKeyValue
argument_list|()
condition|)
empty_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|BZip2Codec
name|codec
init|=
operator|new
name|BZip2Codec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Decompressor
argument_list|>
name|decompressors
init|=
operator|new
name|HashSet
argument_list|<
name|Decompressor
argument_list|>
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|decompressors
operator|.
name|add
argument_list|(
name|CodecPool
operator|.
name|getDecompressor
argument_list|(
name|codec
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|decompressors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

