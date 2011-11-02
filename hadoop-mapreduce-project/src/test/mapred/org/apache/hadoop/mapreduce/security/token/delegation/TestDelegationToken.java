begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.security.token.delegation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|delegation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
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
name|io
operator|.
name|DataInputBuffer
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
name|io
operator|.
name|Text
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
name|mapred
operator|.
name|JobClient
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|MiniMRCluster
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
name|security
operator|.
name|AccessControlException
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
name|security
operator|.
name|UserGroupInformation
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|security
operator|.
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
import|;
end_import

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
name|junit
operator|.
name|Before
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

begin_class
DECL|class|TestDelegationToken
specifier|public
class|class
name|TestDelegationToken
block|{
DECL|field|cluster
specifier|private
name|MiniMRCluster
name|cluster
decl_stmt|;
DECL|field|user1
specifier|private
name|UserGroupInformation
name|user1
decl_stmt|;
DECL|field|user2
specifier|private
name|UserGroupInformation
name|user2
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|user1
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"alice"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"users"
block|}
argument_list|)
expr_stmt|;
name|user2
operator|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"bob"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"users"
block|}
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|"file:///"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testDelegationToken ()
specifier|public
name|void
name|testDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
name|JobClient
name|client
decl_stmt|;
name|client
operator|=
name|user1
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|JobClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|JobClient
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|JobClient
argument_list|(
name|cluster
operator|.
name|createJobConf
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|JobClient
name|bobClient
decl_stmt|;
name|bobClient
operator|=
name|user2
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|JobClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|JobClient
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|JobClient
argument_list|(
name|cluster
operator|.
name|createJobConf
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
init|=
name|client
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|user1
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DataInputBuffer
name|inBuf
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|token
operator|.
name|getIdentifier
argument_list|()
decl_stmt|;
name|inBuf
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|DelegationTokenIdentifier
name|ident
init|=
operator|new
name|DelegationTokenIdentifier
argument_list|()
decl_stmt|;
name|ident
operator|.
name|readFields
argument_list|(
name|inBuf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"alice"
argument_list|,
name|ident
operator|.
name|getUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|createTime
init|=
name|ident
operator|.
name|getIssueDate
argument_list|()
decl_stmt|;
name|long
name|maxTime
init|=
name|ident
operator|.
name|getMaxDate
argument_list|()
decl_stmt|;
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"create time: "
operator|+
name|createTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"current time: "
operator|+
name|currentTime
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"max time: "
operator|+
name|maxTime
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"createTime< current"
argument_list|,
name|createTime
operator|<
name|currentTime
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"current< maxTime"
argument_list|,
name|currentTime
operator|<
name|maxTime
argument_list|)
expr_stmt|;
name|client
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|client
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
try|try
block|{
name|bobClient
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"bob renew"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
comment|// PASS
block|}
try|try
block|{
name|bobClient
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"bob renew"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ace
parameter_list|)
block|{
comment|// PASS
block|}
name|client
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"second alice cancel"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidToken
name|it
parameter_list|)
block|{
comment|// PASS
block|}
block|}
block|}
end_class

end_unit

