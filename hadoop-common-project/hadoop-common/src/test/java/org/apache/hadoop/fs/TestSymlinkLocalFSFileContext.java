begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|PlatformAssumptions
operator|.
name|assumeNotWindows
import|;
end_import

begin_class
DECL|class|TestSymlinkLocalFSFileContext
specifier|public
class|class
name|TestSymlinkLocalFSFileContext
extends|extends
name|TestSymlinkLocalFS
block|{
annotation|@
name|BeforeClass
DECL|method|testSetup ()
specifier|public
specifier|static
name|void
name|testSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|FileContext
name|context
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|wrapper
operator|=
operator|new
name|FileContextTestWrapper
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testRenameFileWithDestParentSymlink ()
specifier|public
name|void
name|testRenameFileWithDestParentSymlink
parameter_list|()
throws|throws
name|IOException
block|{
name|assumeNotWindows
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRenameFileWithDestParentSymlink
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

