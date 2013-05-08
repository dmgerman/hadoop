begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|Path
import|;
end_import

begin_class
DECL|class|TestS3InMemoryFileSystem
specifier|public
class|class
name|TestS3InMemoryFileSystem
extends|extends
name|TestCase
block|{
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PATH
init|=
literal|"s3://test/data.txt"
decl_stmt|;
DECL|field|TEST_DATA
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DATA
init|=
literal|"Sample data for testing."
decl_stmt|;
DECL|field|fs
specifier|private
name|S3InMemoryFileSystem
name|fs
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|fs
operator|=
operator|new
name|S3InMemoryFileSystem
argument_list|()
expr_stmt|;
name|fs
operator|.
name|initialize
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"s3://test/"
argument_list|)
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasicReadWriteIO ()
specifier|public
name|void
name|testBasicReadWriteIO
parameter_list|()
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|writeStream
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|writeStream
operator|.
name|write
argument_list|(
name|TEST_DATA
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|writeStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writeStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|readStream
init|=
name|fs
operator|.
name|open
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_PATH
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|readStream
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
literal|""
decl_stmt|;
name|StringBuffer
name|stringBuffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|stringBuffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|TEST_DATA
operator|.
name|equals
argument_list|(
name|stringBuffer
operator|.
name|toString
argument_list|()
argument_list|)
operator|)
assert|;
block|}
annotation|@
name|Override
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

