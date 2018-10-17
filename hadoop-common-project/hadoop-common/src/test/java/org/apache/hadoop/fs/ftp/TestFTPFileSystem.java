begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ftp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ftp
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|ftp
operator|.
name|FTP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|ftp
operator|.
name|FTPClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|ftp
operator|.
name|FTPFile
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
name|permission
operator|.
name|FsAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test basic @{link FTPFileSystem} class methods. Contract tests are in  * TestFTPContractXXXX.  */
end_comment

begin_class
DECL|class|TestFTPFileSystem
specifier|public
class|class
name|TestFTPFileSystem
block|{
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|180000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testFTPDefaultPort ()
specifier|public
name|void
name|testFTPDefaultPort
parameter_list|()
throws|throws
name|Exception
block|{
name|FTPFileSystem
name|ftp
init|=
operator|new
name|FTPFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|FTP
operator|.
name|DEFAULT_PORT
argument_list|,
name|ftp
operator|.
name|getDefaultPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFTPTransferMode ()
specifier|public
name|void
name|testFTPTransferMode
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
name|FTPFileSystem
name|ftp
init|=
operator|new
name|FTPFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|FTP
operator|.
name|BLOCK_TRANSFER_MODE
argument_list|,
name|ftp
operator|.
name|getTransferMode
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_TRANSFER_MODE
argument_list|,
literal|"STREAM_TRANSFER_MODE"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTP
operator|.
name|STREAM_TRANSFER_MODE
argument_list|,
name|ftp
operator|.
name|getTransferMode
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_TRANSFER_MODE
argument_list|,
literal|"COMPRESSED_TRANSFER_MODE"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTP
operator|.
name|COMPRESSED_TRANSFER_MODE
argument_list|,
name|ftp
operator|.
name|getTransferMode
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_TRANSFER_MODE
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTPClient
operator|.
name|BLOCK_TRANSFER_MODE
argument_list|,
name|ftp
operator|.
name|getTransferMode
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFTPDataConnectionMode ()
specifier|public
name|void
name|testFTPDataConnectionMode
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
name|FTPClient
name|client
init|=
operator|new
name|FTPClient
argument_list|()
decl_stmt|;
name|FTPFileSystem
name|ftp
init|=
operator|new
name|FTPFileSystem
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|FTPClient
operator|.
name|ACTIVE_LOCAL_DATA_CONNECTION_MODE
argument_list|,
name|client
operator|.
name|getDataConnectionMode
argument_list|()
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setDataConnectionMode
argument_list|(
name|client
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTPClient
operator|.
name|ACTIVE_LOCAL_DATA_CONNECTION_MODE
argument_list|,
name|client
operator|.
name|getDataConnectionMode
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_DATA_CONNECTION_MODE
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setDataConnectionMode
argument_list|(
name|client
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTPClient
operator|.
name|ACTIVE_LOCAL_DATA_CONNECTION_MODE
argument_list|,
name|client
operator|.
name|getDataConnectionMode
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_DATA_CONNECTION_MODE
argument_list|,
literal|"PASSIVE_LOCAL_DATA_CONNECTION_MODE"
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setDataConnectionMode
argument_list|(
name|client
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FTPClient
operator|.
name|PASSIVE_LOCAL_DATA_CONNECTION_MODE
argument_list|,
name|client
operator|.
name|getDataConnectionMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetFsAction ()
specifier|public
name|void
name|testGetFsAction
parameter_list|()
block|{
name|FTPFileSystem
name|ftp
init|=
operator|new
name|FTPFileSystem
argument_list|()
decl_stmt|;
name|int
index|[]
name|accesses
init|=
operator|new
name|int
index|[]
block|{
name|FTPFile
operator|.
name|USER_ACCESS
block|,
name|FTPFile
operator|.
name|GROUP_ACCESS
block|,
name|FTPFile
operator|.
name|WORLD_ACCESS
block|}
decl_stmt|;
name|FsAction
index|[]
name|actions
init|=
name|FsAction
operator|.
name|values
argument_list|()
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
name|accesses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|actions
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|enhancedAssertEquals
argument_list|(
name|actions
index|[
name|j
index|]
argument_list|,
name|ftp
operator|.
name|getFsAction
argument_list|(
name|accesses
index|[
name|i
index|]
argument_list|,
name|getFTPFileOf
argument_list|(
name|accesses
index|[
name|i
index|]
argument_list|,
name|actions
index|[
name|j
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|enhancedAssertEquals (FsAction actionA, FsAction actionB)
specifier|private
name|void
name|enhancedAssertEquals
parameter_list|(
name|FsAction
name|actionA
parameter_list|,
name|FsAction
name|actionB
parameter_list|)
block|{
name|String
name|notNullErrorMessage
init|=
literal|"FsAction cannot be null here."
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|actionA
argument_list|,
name|notNullErrorMessage
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|actionB
argument_list|,
name|notNullErrorMessage
argument_list|)
expr_stmt|;
name|String
name|errorMessageFormat
init|=
literal|"expect FsAction is %s, whereas it is %s now."
decl_stmt|;
name|String
name|notEqualErrorMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|errorMessageFormat
argument_list|,
name|actionA
operator|.
name|name
argument_list|()
argument_list|,
name|actionB
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|notEqualErrorMessage
argument_list|,
name|actionA
argument_list|,
name|actionB
argument_list|)
expr_stmt|;
block|}
DECL|method|getFTPFileOf (int access, FsAction action)
specifier|private
name|FTPFile
name|getFTPFileOf
parameter_list|(
name|int
name|access
parameter_list|,
name|FsAction
name|action
parameter_list|)
block|{
name|boolean
name|check
init|=
name|access
operator|==
name|FTPFile
operator|.
name|USER_ACCESS
operator|||
name|access
operator|==
name|FTPFile
operator|.
name|GROUP_ACCESS
operator|||
name|access
operator|==
name|FTPFile
operator|.
name|WORLD_ACCESS
decl_stmt|;
name|String
name|errorFormat
init|=
literal|"access must be in [%d,%d,%d], but it is %d now."
decl_stmt|;
name|String
name|errorMessage
init|=
name|String
operator|.
name|format
argument_list|(
name|errorFormat
argument_list|,
name|FTPFile
operator|.
name|USER_ACCESS
argument_list|,
name|FTPFile
operator|.
name|GROUP_ACCESS
argument_list|,
name|FTPFile
operator|.
name|WORLD_ACCESS
argument_list|,
name|access
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|check
argument_list|,
name|errorMessage
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|FTPFile
name|ftpFile
init|=
operator|new
name|FTPFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|action
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|READ
argument_list|)
condition|)
block|{
name|ftpFile
operator|.
name|setPermission
argument_list|(
name|access
argument_list|,
name|FTPFile
operator|.
name|READ_PERMISSION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|WRITE
argument_list|)
condition|)
block|{
name|ftpFile
operator|.
name|setPermission
argument_list|(
name|access
argument_list|,
name|FTPFile
operator|.
name|WRITE_PERMISSION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|action
operator|.
name|implies
argument_list|(
name|FsAction
operator|.
name|EXECUTE
argument_list|)
condition|)
block|{
name|ftpFile
operator|.
name|setPermission
argument_list|(
name|access
argument_list|,
name|FTPFile
operator|.
name|EXECUTE_PERMISSION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|ftpFile
return|;
block|}
annotation|@
name|Test
DECL|method|testFTPSetTimeout ()
specifier|public
name|void
name|testFTPSetTimeout
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FTPClient
name|client
init|=
operator|new
name|FTPClient
argument_list|()
decl_stmt|;
name|FTPFileSystem
name|ftp
init|=
operator|new
name|FTPFileSystem
argument_list|()
decl_stmt|;
name|ftp
operator|.
name|setTimeout
argument_list|(
name|client
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|client
operator|.
name|getControlKeepAliveTimeout
argument_list|()
argument_list|,
name|FTPFileSystem
operator|.
name|DEFAULT_TIMEOUT
argument_list|)
expr_stmt|;
name|long
name|timeout
init|=
literal|600
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|FTPFileSystem
operator|.
name|FS_FTP_TIMEOUT
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setTimeout
argument_list|(
name|client
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|client
operator|.
name|getControlKeepAliveTimeout
argument_list|()
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

