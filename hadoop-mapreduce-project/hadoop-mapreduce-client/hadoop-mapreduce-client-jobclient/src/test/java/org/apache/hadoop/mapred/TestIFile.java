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
name|LocalFileSystem
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
name|Text
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
name|DefaultCodec
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
name|GzipCodec
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

begin_class
DECL|class|TestIFile
specifier|public
class|class
name|TestIFile
block|{
annotation|@
name|Test
comment|/**    * Create an IFile.Writer using GzipCodec since this code does not    * have a compressor when run via the tests (ie no native libraries).    */
DECL|method|testIFileWriterWithCodec ()
specifier|public
name|void
name|testIFileWriterWithCodec
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|rfs
init|=
operator|(
operator|(
name|LocalFileSystem
operator|)
name|localFs
operator|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
literal|"build/test.ifile"
argument_list|)
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|DefaultCodec
name|codec
init|=
operator|new
name|GzipCodec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|writer
init|=
operator|new
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|conf
argument_list|,
name|rfs
operator|.
name|create
argument_list|(
name|path
argument_list|)
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
comment|/** Same as above but create a reader. */
DECL|method|testIFileReaderWithCodec ()
specifier|public
name|void
name|testIFileReaderWithCodec
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|rfs
init|=
operator|(
operator|(
name|LocalFileSystem
operator|)
name|localFs
operator|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
literal|"build/test.ifile"
argument_list|)
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
name|DefaultCodec
name|codec
init|=
operator|new
name|GzipCodec
argument_list|()
decl_stmt|;
name|codec
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|rfs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|writer
init|=
operator|new
name|IFile
operator|.
name|Writer
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|conf
argument_list|,
name|out
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|Text
operator|.
name|class
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|in
init|=
name|rfs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|IFile
operator|.
name|Reader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
name|reader
init|=
operator|new
name|IFile
operator|.
name|Reader
argument_list|<
name|Text
argument_list|,
name|Text
argument_list|>
argument_list|(
name|conf
argument_list|,
name|in
argument_list|,
name|rfs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|codec
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test check sum
name|byte
index|[]
name|ab
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|int
name|readed
init|=
name|reader
operator|.
name|checksumIn
operator|.
name|readWithChecksum
argument_list|(
name|ab
argument_list|,
literal|0
argument_list|,
name|ab
operator|.
name|length
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|readed
argument_list|,
name|reader
operator|.
name|checksumIn
operator|.
name|getChecksum
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

