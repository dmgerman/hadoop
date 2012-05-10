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
name|*
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
DECL|class|TestVersionUtil
specifier|public
class|class
name|TestVersionUtil
block|{
annotation|@
name|Test
DECL|method|testCompareVersions ()
specifier|public
name|void
name|testCompareVersions
parameter_list|()
block|{
comment|// Equal versions are equal.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
literal|"2.0.0"
argument_list|,
literal|"2.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
literal|"2.0.0a"
argument_list|,
literal|"2.0.0a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
literal|"1"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
literal|"2.0.0-SNAPSHOT"
argument_list|,
literal|"2.0.0-SNAPSHOT"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Assert that lower versions are lower, and higher versions are higher.
name|assertExpectedValues
argument_list|(
literal|"1"
argument_list|,
literal|"2.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"2.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0"
argument_list|,
literal|"2.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"2.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.0a"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0.0"
argument_list|,
literal|"2.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.0-dev"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.1"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.2"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.1.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"2.0.0"
argument_list|,
literal|"10.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.0a"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.2a"
argument_list|,
literal|"1.0.10"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.2a"
argument_list|,
literal|"1.0.2b"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.2a"
argument_list|,
literal|"1.0.2ab"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0a1"
argument_list|,
literal|"1.0.0a2"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0a2"
argument_list|,
literal|"1.0.0a10"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0"
argument_list|,
literal|"1.a"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0"
argument_list|,
literal|"1.a0"
argument_list|)
expr_stmt|;
comment|// Snapshot builds precede their eventual releases.
name|assertExpectedValues
argument_list|(
literal|"1.0-SNAPSHOT"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0"
argument_list|,
literal|"1.0.0-SNAPSHOT"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0-SNAPSHOT"
argument_list|,
literal|"1.0.0"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.0"
argument_list|,
literal|"1.0.1-SNAPSHOT"
argument_list|)
expr_stmt|;
name|assertExpectedValues
argument_list|(
literal|"1.0.1-SNAPSHOT"
argument_list|,
literal|"1.0.1"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertExpectedValues (String lower, String higher)
specifier|private
specifier|static
name|void
name|assertExpectedValues
parameter_list|(
name|String
name|lower
parameter_list|,
name|String
name|higher
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
name|lower
argument_list|,
name|higher
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|VersionUtil
operator|.
name|compareVersions
argument_list|(
name|higher
argument_list|,
name|lower
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

