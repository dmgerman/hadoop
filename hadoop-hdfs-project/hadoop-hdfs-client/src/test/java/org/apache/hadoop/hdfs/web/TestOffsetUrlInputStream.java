begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestOffsetUrlInputStream
specifier|public
class|class
name|TestOffsetUrlInputStream
block|{
annotation|@
name|Test
DECL|method|testRemoveOffset ()
specifier|public
name|void
name|testRemoveOffset
parameter_list|()
throws|throws
name|IOException
block|{
block|{
comment|//no offset
name|String
name|s
init|=
literal|"http://test/Abc?Length=99"
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|//no parameters
name|String
name|s
init|=
literal|"http://test/Abc"
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|//offset as first parameter
name|String
name|s
init|=
literal|"http://test/Abc?offset=10&Length=99"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"http://test/Abc?Length=99"
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|//offset as second parameter
name|String
name|s
init|=
literal|"http://test/Abc?op=read&OFFset=10&Length=99"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"http://test/Abc?op=read&Length=99"
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|//offset as last parameter
name|String
name|s
init|=
literal|"http://test/Abc?Length=99&offset=10"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"http://test/Abc?Length=99"
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|//offset as the only parameter
name|String
name|s
init|=
literal|"http://test/Abc?offset=10"
decl_stmt|;
name|assertEquals
argument_list|(
literal|"http://test/Abc"
argument_list|,
name|WebHdfsFileSystem
operator|.
name|removeOffsetParam
argument_list|(
operator|new
name|URL
argument_list|(
name|s
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

