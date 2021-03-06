begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.ftp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|ftp
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
name|contract
operator|.
name|AbstractContractRenameTest
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
name|contract
operator|.
name|AbstractFSContract
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
name|ftp
operator|.
name|FTPFileSystem
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

begin_class
DECL|class|TestFTPContractRename
specifier|public
class|class
name|TestFTPContractRename
extends|extends
name|AbstractContractRenameTest
block|{
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|FTPContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Check the exception was about cross-directory renames    * -if not, rethrow it.    * @param e exception raised    * @throws IOException    */
DECL|method|verifyUnsupportedDirRenameException (IOException e)
specifier|private
name|void
name|verifyUnsupportedDirRenameException
parameter_list|(
name|IOException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|FTPFileSystem
operator|.
name|E_SAME_DIRECTORY_ONLY
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|testRenameDirIntoExistingDir ()
specifier|public
name|void
name|testRenameDirIntoExistingDir
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|super
operator|.
name|testRenameDirIntoExistingDir
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|verifyUnsupportedDirRenameException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|testRenameFileNonexistentDir ()
specifier|public
name|void
name|testRenameFileNonexistentDir
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|super
operator|.
name|testRenameFileNonexistentDir
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected a failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|verifyUnsupportedDirRenameException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

