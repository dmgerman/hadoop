begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|common
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
name|InputStream
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
name|util
operator|.
name|Iterator
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|TextFileRegionFormat
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
name|DataInputBuffer
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
name|DataOutputBuffer
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
name|CompressionCodec
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
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Test for the text based block format for provided block maps.  */
end_comment

begin_class
DECL|class|TestTextBlockFormat
specifier|public
class|class
name|TestTextBlockFormat
block|{
DECL|field|OUTFILE
specifier|static
specifier|final
name|Path
name|OUTFILE
init|=
operator|new
name|Path
argument_list|(
literal|"hdfs://dummyServer:0000/dummyFile.txt"
argument_list|)
decl_stmt|;
DECL|method|check (TextWriter.Options opts, final Path vp, final Class<? extends CompressionCodec> vc)
name|void
name|check
parameter_list|(
name|TextWriter
operator|.
name|Options
name|opts
parameter_list|,
specifier|final
name|Path
name|vp
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|CompressionCodec
argument_list|>
name|vc
parameter_list|)
throws|throws
name|IOException
block|{
name|TextFileRegionFormat
name|mFmt
init|=
operator|new
name|TextFileRegionFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TextWriter
name|createWriter
parameter_list|(
name|Path
name|file
parameter_list|,
name|CompressionCodec
name|codec
parameter_list|,
name|String
name|delim
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|assertEquals
argument_list|(
name|vp
argument_list|,
name|file
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|vc
condition|)
block|{
name|assertNull
argument_list|(
name|codec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|vc
argument_list|,
name|codec
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
comment|// ignored
block|}
block|}
decl_stmt|;
name|mFmt
operator|.
name|getWriter
argument_list|(
name|opts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriterOptions ()
specifier|public
name|void
name|testWriterOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|TextWriter
operator|.
name|Options
name|opts
init|=
name|TextWriter
operator|.
name|defaults
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|opts
operator|instanceof
name|WriterOptions
argument_list|)
expr_stmt|;
name|WriterOptions
name|wopts
init|=
operator|(
name|WriterOptions
operator|)
name|opts
decl_stmt|;
name|Path
name|def
init|=
operator|new
name|Path
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_BLOCK_MAP_PATH_DEFAULT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|def
argument_list|,
name|wopts
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|wopts
operator|.
name|getCodec
argument_list|()
argument_list|)
expr_stmt|;
name|opts
operator|.
name|filename
argument_list|(
name|OUTFILE
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|opts
argument_list|,
name|OUTFILE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|opts
operator|.
name|filename
argument_list|(
name|OUTFILE
argument_list|)
expr_stmt|;
name|opts
operator|.
name|codec
argument_list|(
literal|"gzip"
argument_list|)
expr_stmt|;
name|Path
name|cp
init|=
operator|new
name|Path
argument_list|(
name|OUTFILE
operator|.
name|getParent
argument_list|()
argument_list|,
name|OUTFILE
operator|.
name|getName
argument_list|()
operator|+
literal|".gz"
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|opts
argument_list|,
name|cp
argument_list|,
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
name|GzipCodec
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCSVReadWrite ()
specifier|public
name|void
name|testCSVReadWrite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|FileRegion
name|r1
init|=
operator|new
name|FileRegion
argument_list|(
literal|4344L
argument_list|,
name|OUTFILE
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|FileRegion
name|r2
init|=
operator|new
name|FileRegion
argument_list|(
literal|4345L
argument_list|,
name|OUTFILE
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|FileRegion
name|r3
init|=
operator|new
name|FileRegion
argument_list|(
literal|4346L
argument_list|,
name|OUTFILE
argument_list|,
literal|2048
argument_list|,
literal|512
argument_list|)
decl_stmt|;
try|try
init|(
name|TextWriter
name|csv
init|=
operator|new
name|TextWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|)
argument_list|,
literal|","
argument_list|)
init|)
block|{
name|csv
operator|.
name|store
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|csv
operator|.
name|store
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|csv
operator|.
name|store
argument_list|(
name|r3
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i3
decl_stmt|;
try|try
init|(
name|TextReader
name|csv
init|=
operator|new
name|TextReader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|","
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|createStream
parameter_list|()
block|{
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|in
return|;
block|}
block|}
init|)
block|{
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i1
init|=
name|csv
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|r1
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i2
init|=
name|csv
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|r1
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r3
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r3
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i1
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|i3
operator|=
name|csv
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|i3
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
return|return;
block|}
name|fail
argument_list|(
literal|"Invalid iterator"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCSVReadWriteTsv ()
specifier|public
name|void
name|testCSVReadWriteTsv
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|FileRegion
name|r1
init|=
operator|new
name|FileRegion
argument_list|(
literal|4344L
argument_list|,
name|OUTFILE
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|FileRegion
name|r2
init|=
operator|new
name|FileRegion
argument_list|(
literal|4345L
argument_list|,
name|OUTFILE
argument_list|,
literal|1024
argument_list|,
literal|1024
argument_list|)
decl_stmt|;
name|FileRegion
name|r3
init|=
operator|new
name|FileRegion
argument_list|(
literal|4346L
argument_list|,
name|OUTFILE
argument_list|,
literal|2048
argument_list|,
literal|512
argument_list|)
decl_stmt|;
try|try
init|(
name|TextWriter
name|csv
init|=
operator|new
name|TextWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|)
argument_list|,
literal|"\t"
argument_list|)
init|)
block|{
name|csv
operator|.
name|store
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|csv
operator|.
name|store
argument_list|(
name|r2
argument_list|)
expr_stmt|;
name|csv
operator|.
name|store
argument_list|(
name|r3
argument_list|)
expr_stmt|;
block|}
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i3
decl_stmt|;
try|try
init|(
name|TextReader
name|csv
init|=
operator|new
name|TextReader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"\t"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|createStream
parameter_list|()
block|{
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|in
return|;
block|}
block|}
init|)
block|{
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i1
init|=
name|csv
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|r1
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|FileRegion
argument_list|>
name|i2
init|=
name|csv
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|r1
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r3
argument_list|,
name|i2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r3
argument_list|,
name|i1
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i1
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|i2
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|i3
operator|=
name|csv
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|i3
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
return|return;
block|}
name|fail
argument_list|(
literal|"Invalid iterator"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

