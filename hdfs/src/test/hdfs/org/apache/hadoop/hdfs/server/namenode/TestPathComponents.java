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
name|namenode
operator|.
name|INode
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
name|junit
operator|.
name|Test
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
DECL|method|testBytes2ByteArray ()
specifier|public
name|void
name|testBytes2ByteArray
parameter_list|()
throws|throws
name|Exception
block|{
name|testString
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/file"
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/directory/"
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/dir//file"
argument_list|)
expr_stmt|;
name|testString
argument_list|(
literal|"/dir/dir1//"
argument_list|)
expr_stmt|;
block|}
DECL|method|testString (String str)
specifier|public
name|void
name|testString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|pathString
init|=
name|str
decl_stmt|;
name|byte
index|[]
index|[]
name|oldPathComponents
init|=
name|INode
operator|.
name|getPathComponents
argument_list|(
name|pathString
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|newPathComponents
init|=
name|DFSUtil
operator|.
name|bytes2byteArray
argument_list|(
name|pathString
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
operator|(
name|byte
operator|)
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldPathComponents
index|[
literal|0
index|]
operator|==
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|oldPathComponents
index|[
literal|0
index|]
operator|==
name|newPathComponents
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Path components do not match for "
operator|+
name|pathString
argument_list|,
name|Arrays
operator|.
name|deepEquals
argument_list|(
name|oldPathComponents
argument_list|,
name|newPathComponents
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

