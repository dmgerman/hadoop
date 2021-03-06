begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
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
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|GroupMappingServiceProvider
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
name|JniBasedUnixGroupsMapping
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
name|ShellBasedUnixGroupsMapping
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
name|util
operator|.
name|NativeCodeLoader
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
name|Test
import|;
end_import

begin_class
DECL|class|TestJNIGroupsMapping
specifier|public
class|class
name|TestJNIGroupsMapping
block|{
annotation|@
name|Before
DECL|method|isNativeCodeLoaded ()
specifier|public
name|void
name|isNativeCodeLoaded
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJNIGroupsMapping ()
specifier|public
name|void
name|testJNIGroupsMapping
parameter_list|()
throws|throws
name|Exception
block|{
comment|//for the user running the test, check whether the
comment|//ShellBasedUnixGroupsMapping and the JniBasedUnixGroupsMapping
comment|//return the same groups
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|testForUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|//check for a dummy non-existent user (both the implementations should
comment|//return an empty list
name|testForUser
argument_list|(
literal|"fooBarBaz1234DoesNotExist"
argument_list|)
expr_stmt|;
block|}
DECL|method|testForUser (String user)
specifier|private
name|void
name|testForUser
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|Exception
block|{
name|GroupMappingServiceProvider
name|g
init|=
operator|new
name|ShellBasedUnixGroupsMapping
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|shellBasedGroups
init|=
name|g
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|g
operator|=
operator|new
name|JniBasedUnixGroupsMapping
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|jniBasedGroups
init|=
name|g
operator|.
name|getGroups
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|String
index|[]
name|shellBasedGroupsArray
init|=
name|shellBasedGroups
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|shellBasedGroupsArray
argument_list|)
expr_stmt|;
name|String
index|[]
name|jniBasedGroupsArray
init|=
name|jniBasedGroups
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|jniBasedGroupsArray
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|shellBasedGroupsArray
argument_list|,
name|jniBasedGroupsArray
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Groups returned by "
operator|+
name|ShellBasedUnixGroupsMapping
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" and "
operator|+
name|JniBasedUnixGroupsMapping
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" didn't match for "
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

