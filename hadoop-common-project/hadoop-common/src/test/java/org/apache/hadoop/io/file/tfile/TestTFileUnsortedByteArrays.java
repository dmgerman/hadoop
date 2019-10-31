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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|fail
import|;
end_import

begin_class
DECL|class|TestTFileUnsortedByteArrays
specifier|public
class|class
name|TestTFileUnsortedByteArrays
block|{
DECL|field|ROOT
specifier|private
specifier|static
name|String
name|ROOT
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|BLOCK_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BLOCK_SIZE
init|=
literal|512
decl_stmt|;
DECL|field|BUF_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|BUF_SIZE
init|=
literal|64
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
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
DECL|field|out
specifier|private
name|FSDataOutputStream
name|out
decl_stmt|;
DECL|field|writer
specifier|private
name|Writer
name|writer
decl_stmt|;
DECL|field|compression
specifier|private
name|String
name|compression
init|=
name|Compression
operator|.
name|Algorithm
operator|.
name|GZ
operator|.
name|getName
argument_list|()
decl_stmt|;
DECL|field|outputFile
specifier|private
name|String
name|outputFile
init|=
literal|"TFileTestUnsorted"
decl_stmt|;
comment|/*    * pre-sampled numbers of records in one block, based on the given the    * generated key and value strings    */
DECL|field|records1stBlock
specifier|private
name|int
name|records1stBlock
init|=
literal|4314
decl_stmt|;
DECL|field|records2ndBlock
specifier|private
name|int
name|records2ndBlock
init|=
literal|4108
decl_stmt|;
DECL|method|init (String compression, String outputFile, int numRecords1stBlock, int numRecords2ndBlock)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|compression
parameter_list|,
name|String
name|outputFile
parameter_list|,
name|int
name|numRecords1stBlock
parameter_list|,
name|int
name|numRecords2ndBlock
parameter_list|)
block|{
name|this
operator|.
name|compression
operator|=
name|compression
expr_stmt|;
name|this
operator|.
name|outputFile
operator|=
name|outputFile
expr_stmt|;
name|this
operator|.
name|records1stBlock
operator|=
name|numRecords1stBlock
expr_stmt|;
name|this
operator|.
name|records2ndBlock
operator|=
name|numRecords2ndBlock
expr_stmt|;
block|}
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|path
operator|=
operator|new
name|Path
argument_list|(
name|ROOT
argument_list|,
name|outputFile
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
name|out
operator|=
name|fs
operator|.
name|create
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|Writer
argument_list|(
name|out
argument_list|,
name|BLOCK_SIZE
argument_list|,
name|compression
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"keyZ"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"valueZ"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"keyM"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"valueM"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"keyN"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"valueN"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|"keyA"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"valueA"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|closeOutput
argument_list|()
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
comment|// we still can scan records in an unsorted TFile
annotation|@
name|Test
DECL|method|testFailureScannerWithKeys ()
specifier|public
name|void
name|testFailureScannerWithKeys
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
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
init|)
block|{
name|assertThat
argument_list|(
name|reader
operator|.
name|isSorted
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reader
operator|.
name|getEntryCount
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
try|try
block|{
name|reader
operator|.
name|createScannerByKey
argument_list|(
literal|"aaa"
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"zzz"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to catch creating scanner with keys on unsorted file."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|expected
parameter_list|)
block|{       }
block|}
block|}
comment|// we still can scan records in an unsorted TFile
annotation|@
name|Test
DECL|method|testScan ()
specifier|public
name|void
name|testScan
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
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
init|)
block|{
name|assertThat
argument_list|(
name|reader
operator|.
name|isSorted
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reader
operator|.
name|getEntryCount
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
try|try
init|(
name|Scanner
name|scanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
init|)
block|{
comment|// read key and value
name|byte
index|[]
name|kbuf
init|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
decl_stmt|;
name|int
name|klen
init|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKeyLength
argument_list|()
decl_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKey
argument_list|(
name|kbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|kbuf
argument_list|,
literal|0
argument_list|,
name|klen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"keyZ"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|vbuf
init|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
decl_stmt|;
name|int
name|vlen
init|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValueLength
argument_list|()
decl_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValue
argument_list|(
name|vbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|vbuf
argument_list|,
literal|0
argument_list|,
name|vlen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"valueZ"
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|advance
argument_list|()
expr_stmt|;
comment|// now try get value first
name|vbuf
operator|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
expr_stmt|;
name|vlen
operator|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValueLength
argument_list|()
expr_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValue
argument_list|(
name|vbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|vbuf
argument_list|,
literal|0
argument_list|,
name|vlen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"valueM"
argument_list|)
expr_stmt|;
name|kbuf
operator|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
expr_stmt|;
name|klen
operator|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKeyLength
argument_list|()
expr_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKey
argument_list|(
name|kbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|kbuf
argument_list|,
literal|0
argument_list|,
name|klen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"keyM"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// we still can scan records in an unsorted TFile
annotation|@
name|Test
DECL|method|testScanRange ()
specifier|public
name|void
name|testScanRange
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
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
init|)
block|{
name|assertThat
argument_list|(
name|reader
operator|.
name|isSorted
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|reader
operator|.
name|getEntryCount
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|4
argument_list|)
expr_stmt|;
try|try
init|(
name|Scanner
name|scanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
init|)
block|{
comment|// read key and value
name|byte
index|[]
name|kbuf
init|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
decl_stmt|;
name|int
name|klen
init|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKeyLength
argument_list|()
decl_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKey
argument_list|(
name|kbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|kbuf
argument_list|,
literal|0
argument_list|,
name|klen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"keyZ"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|vbuf
init|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
decl_stmt|;
name|int
name|vlen
init|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValueLength
argument_list|()
decl_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValue
argument_list|(
name|vbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|vbuf
argument_list|,
literal|0
argument_list|,
name|vlen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"valueZ"
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|advance
argument_list|()
expr_stmt|;
comment|// now try get value first
name|vbuf
operator|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
expr_stmt|;
name|vlen
operator|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValueLength
argument_list|()
expr_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getValue
argument_list|(
name|vbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|vbuf
argument_list|,
literal|0
argument_list|,
name|vlen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"valueM"
argument_list|)
expr_stmt|;
name|kbuf
operator|=
operator|new
name|byte
index|[
name|BUF_SIZE
index|]
expr_stmt|;
name|klen
operator|=
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKeyLength
argument_list|()
expr_stmt|;
name|scanner
operator|.
name|entry
argument_list|()
operator|.
name|getKey
argument_list|(
name|kbuf
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|String
argument_list|(
name|kbuf
argument_list|,
literal|0
argument_list|,
name|klen
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"keyM"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testFailureSeek ()
specifier|public
name|void
name|testFailureSeek
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|Reader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
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
init|;
name|Scanner
name|scanner
operator|=
name|reader
operator|.
name|createScanner
argument_list|()
init|)
block|{
comment|// can't find ceil
try|try
block|{
name|scanner
operator|.
name|lowerBound
argument_list|(
literal|"keyN"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot search in a unsorted TFile!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{       }
comment|// can't find higher
try|try
block|{
name|scanner
operator|.
name|upperBound
argument_list|(
literal|"keyA"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot search higher in a unsorted TFile!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{       }
comment|// can't seek
try|try
block|{
name|scanner
operator|.
name|seekTo
argument_list|(
literal|"keyM"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Cannot search a unsorted TFile!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{       }
block|}
block|}
DECL|method|closeOutput ()
specifier|private
name|void
name|closeOutput
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|=
literal|null
expr_stmt|;
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
block|}
block|}
end_class

end_unit

