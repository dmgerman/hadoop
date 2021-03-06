begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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
name|assertTrue
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskErrorException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * The class to test BasicDiskValidator.  */
end_comment

begin_class
DECL|class|TestBasicDiskValidator
specifier|public
class|class
name|TestBasicDiskValidator
extends|extends
name|TestDiskChecker
block|{
annotation|@
name|Override
DECL|method|checkDirs (boolean isDir, String perm, boolean success)
specifier|protected
name|void
name|checkDirs
parameter_list|(
name|boolean
name|isDir
parameter_list|,
name|String
name|perm
parameter_list|,
name|boolean
name|success
parameter_list|)
throws|throws
name|Throwable
block|{
name|File
name|localDir
init|=
name|isDir
condition|?
name|createTempDir
argument_list|()
else|:
name|createTempFile
argument_list|()
decl_stmt|;
try|try
block|{
name|Shell
operator|.
name|execCommand
argument_list|(
name|Shell
operator|.
name|getSetPermissionCommand
argument_list|(
name|perm
argument_list|,
literal|false
argument_list|,
name|localDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|DiskValidatorFactory
operator|.
name|getInstance
argument_list|(
name|BasicDiskValidator
operator|.
name|NAME
argument_list|)
operator|.
name|checkStatus
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"call to checkDir() succeeded."
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskErrorException
name|e
parameter_list|)
block|{
comment|// call to checkDir() succeeded even though it was expected to fail
comment|// if success is false, otherwise throw the exception
if|if
condition|(
name|success
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
finally|finally
block|{
name|localDir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

