begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|DFSUtil
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
comment|/**  *   */
end_comment

begin_class
DECL|class|TestPathComponents
specifier|public
class|class
name|TestPathComponents
block|{
annotation|@
name|Test
DECL|method|testBytes2ByteArrayFQ ()
specifier|public
name|void
name|testBytes2ByteArrayFQ
parameter_list|()
throws|throws
name|Exception
block|{
name|testString
argument_list|(
literal|"/"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/dir/"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"dir"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/dir//file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"dir"
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//dir/dir1//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"dir"
block|,
literal|"dir1"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//dir//dir1//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"dir"
block|,
literal|"dir1"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//dir//dir1//file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|""
block|,
literal|"dir"
block|,
literal|"dir1"
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBytes2ByteArrayRelative ()
specifier|public
name|void
name|testBytes2ByteArrayRelative
parameter_list|()
throws|throws
name|Exception
block|{
name|testString
argument_list|(
literal|"file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"file"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir/"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir//file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir/dir1//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|,
literal|"dir1"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir//dir1//"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|,
literal|"dir1"
block|}
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"dir//dir1//file"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"dir"
block|,
literal|"dir1"
block|,
literal|"file"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testByteArray2PathStringRoot ()
specifier|public
name|void
name|testByteArray2PathStringRoot
parameter_list|()
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|DFSUtil
operator|.
name|getPathComponents
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testByteArray2PathStringFQ ()
specifier|public
name|void
name|testByteArray2PathStringFQ
parameter_list|()
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|DFSUtil
operator|.
name|getPathComponents
argument_list|(
literal|"/1/2/3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/1/2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/1"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/1/2"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/1/2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1/2"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1/2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testByteArray2PathStringRelative ()
specifier|public
name|void
name|testByteArray2PathStringRelative
parameter_list|()
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|DFSUtil
operator|.
name|getPathComponents
argument_list|(
literal|"1/2/3"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1/2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1/2"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1/2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2/3"
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testString (String path, String[] expected)
specifier|public
name|void
name|testString
parameter_list|(
name|String
name|path
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|DFSUtil
operator|.
name|getPathComponents
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
index|[]
name|actual
init|=
operator|new
name|String
index|[
name|components
operator|.
name|length
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
name|components
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|components
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|actual
index|[
name|i
index|]
operator|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|expected
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
comment|// test the reconstituted path
name|path
operator|=
name|path
operator|.
name|replaceAll
argument_list|(
literal|"/+"
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|replaceAll
argument_list|(
literal|"/$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|path
argument_list|,
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

