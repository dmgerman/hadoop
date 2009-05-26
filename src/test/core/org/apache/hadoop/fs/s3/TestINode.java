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
name|fs
operator|.
name|s3
operator|.
name|INode
operator|.
name|FileType
import|;
end_import

begin_class
DECL|class|TestINode
specifier|public
class|class
name|TestINode
extends|extends
name|TestCase
block|{
DECL|method|testSerializeFileWithSingleBlock ()
specifier|public
name|void
name|testSerializeFileWithSingleBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|Block
index|[]
name|blocks
init|=
block|{
operator|new
name|Block
argument_list|(
literal|849282477840258181L
argument_list|,
literal|128L
argument_list|)
block|}
decl_stmt|;
name|INode
name|inode
init|=
operator|new
name|INode
argument_list|(
name|FileType
operator|.
name|FILE
argument_list|,
name|blocks
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
literal|1L
operator|+
literal|4
operator|+
literal|16
argument_list|,
name|inode
operator|.
name|getSerializedLength
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|inode
operator|.
name|serialize
argument_list|()
decl_stmt|;
name|INode
name|deserialized
init|=
name|INode
operator|.
name|deserialize
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FileType"
argument_list|,
name|inode
operator|.
name|getFileType
argument_list|()
argument_list|,
name|deserialized
operator|.
name|getFileType
argument_list|()
argument_list|)
expr_stmt|;
name|Block
index|[]
name|deserializedBlocks
init|=
name|deserialized
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
literal|1
argument_list|,
name|deserializedBlocks
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Id"
argument_list|,
name|blocks
index|[
literal|0
index|]
operator|.
name|getId
argument_list|()
argument_list|,
name|deserializedBlocks
index|[
literal|0
index|]
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
name|blocks
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|,
name|deserializedBlocks
index|[
literal|0
index|]
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerializeDirectory ()
specifier|public
name|void
name|testSerializeDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|INode
name|inode
init|=
name|INode
operator|.
name|DIRECTORY_INODE
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Length"
argument_list|,
literal|1L
argument_list|,
name|inode
operator|.
name|getSerializedLength
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|in
init|=
name|inode
operator|.
name|serialize
argument_list|()
decl_stmt|;
name|INode
name|deserialized
init|=
name|INode
operator|.
name|deserialize
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|INode
operator|.
name|DIRECTORY_INODE
argument_list|,
name|deserialized
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeserializeNull ()
specifier|public
name|void
name|testDeserializeNull
parameter_list|()
throws|throws
name|IOException
block|{
name|assertNull
argument_list|(
name|INode
operator|.
name|deserialize
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

