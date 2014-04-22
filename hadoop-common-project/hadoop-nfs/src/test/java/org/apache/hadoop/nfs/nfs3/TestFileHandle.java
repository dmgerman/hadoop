begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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
name|oncrpc
operator|.
name|XDR
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
DECL|class|TestFileHandle
specifier|public
class|class
name|TestFileHandle
block|{
annotation|@
name|Test
DECL|method|testConstructor ()
specifier|public
name|void
name|testConstructor
parameter_list|()
block|{
name|FileHandle
name|handle
init|=
operator|new
name|FileHandle
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|XDR
name|xdr
init|=
operator|new
name|XDR
argument_list|()
decl_stmt|;
name|handle
operator|.
name|serialize
argument_list|(
name|xdr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|handle
operator|.
name|getFileId
argument_list|()
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// Deserialize it back
name|FileHandle
name|handle2
init|=
operator|new
name|FileHandle
argument_list|()
decl_stmt|;
name|handle2
operator|.
name|deserialize
argument_list|(
name|xdr
operator|.
name|asReadOnlyWrap
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Failed: Assert 1024 is id "
argument_list|,
literal|1024
argument_list|,
name|handle
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

