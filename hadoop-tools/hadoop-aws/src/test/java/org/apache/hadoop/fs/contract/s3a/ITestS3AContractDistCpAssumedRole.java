begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.s3a
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
name|s3a
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
name|fs
operator|.
name|s3a
operator|.
name|AssumedRoleCredentialProvider
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
name|fs
operator|.
name|s3a
operator|.
name|Constants
operator|.
name|ASSUMED_ROLE_ARN
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestUtils
operator|.
name|assume
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestUtils
operator|.
name|authenticationContains
import|;
end_import

begin_comment
comment|/**  * Run DistCP under an assumed role.  * This is skipped if the FS is already set to run under an assumed role,  * because it would duplicate that of the superclass.  */
end_comment

begin_class
DECL|class|ITestS3AContractDistCpAssumedRole
specifier|public
class|class
name|ITestS3AContractDistCpAssumedRole
extends|extends
name|ITestS3AContractDistCp
block|{
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
comment|// check for the fs having assumed roles
name|assume
argument_list|(
literal|"No ARN for role tests"
argument_list|,
operator|!
name|getAssumedRoleARN
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assume
argument_list|(
literal|"Already running as an assumed role"
argument_list|,
operator|!
name|authenticationContains
argument_list|(
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
argument_list|,
name|AssumedRoleCredentialProvider
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Probe for an ARN for the test FS.    * @return any ARN for the (previous created) filesystem.    */
DECL|method|getAssumedRoleARN ()
specifier|private
name|String
name|getAssumedRoleARN
parameter_list|()
block|{
return|return
name|getFileSystem
argument_list|()
operator|.
name|getConf
argument_list|()
operator|.
name|getTrimmed
argument_list|(
name|ASSUMED_ROLE_ARN
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

