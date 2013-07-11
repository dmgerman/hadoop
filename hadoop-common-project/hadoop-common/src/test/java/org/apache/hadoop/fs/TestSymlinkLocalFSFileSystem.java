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
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
DECL|class|TestSymlinkLocalFSFileSystem
specifier|public
class|class
name|TestSymlinkLocalFSFileSystem
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
name|FileSystem
name|filesystem
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|wrapper
operator|=
operator|new
name|FileSystemTestWrapper
argument_list|(
name|filesystem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"RawLocalFileSystem#mkdir does not treat existence of directory"
operator|+
literal|" as an error"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testMkdirExistingLink ()
specifier|public
name|void
name|testMkdirExistingLink
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"FileSystem#create defaults to creating parents,"
operator|+
literal|" throwing an IOException instead of FileNotFoundException"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testCreateFileViaDanglingLinkParent ()
specifier|public
name|void
name|testCreateFileViaDanglingLinkParent
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"RawLocalFileSystem does not throw an exception if the path"
operator|+
literal|" already exists"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testCreateFileDirExistingLink ()
specifier|public
name|void
name|testCreateFileDirExistingLink
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Ignore
argument_list|(
literal|"ChecksumFileSystem does not support append"
argument_list|)
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testAccessFileViaInterSymlinkAbsTarget ()
specifier|public
name|void
name|testAccessFileViaInterSymlinkAbsTarget
parameter_list|()
throws|throws
name|IOException
block|{}
block|}
end_class

end_unit

