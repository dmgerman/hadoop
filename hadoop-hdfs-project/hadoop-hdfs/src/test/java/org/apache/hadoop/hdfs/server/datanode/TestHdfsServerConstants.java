begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
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
name|assertEquals
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
name|fail
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|RollingUpgradeStartupOption
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
name|hdfs
operator|.
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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

begin_comment
comment|/**  * Test enumerations in TestHdfsServerConstants.  */
end_comment

begin_class
DECL|class|TestHdfsServerConstants
specifier|public
class|class
name|TestHdfsServerConstants
block|{
comment|/**    * Verify that parsing a StartupOption string gives the expected results.    * If a RollingUpgradeStartupOption is specified than it is also checked.    *    * @param value    * @param expectedOption    * @param expectedRollupOption optional, may be null.    */
DECL|method|verifyStartupOptionResult (String value, StartupOption expectedOption, RollingUpgradeStartupOption expectedRollupOption)
specifier|private
specifier|static
name|void
name|verifyStartupOptionResult
parameter_list|(
name|String
name|value
parameter_list|,
name|StartupOption
name|expectedOption
parameter_list|,
name|RollingUpgradeStartupOption
name|expectedRollupOption
parameter_list|)
block|{
name|StartupOption
name|option
init|=
name|StartupOption
operator|.
name|getEnum
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedOption
argument_list|,
name|option
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedRollupOption
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedRollupOption
argument_list|,
name|option
operator|.
name|getRollingUpgradeStartupOption
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that we can parse a StartupOption string without the optional    * RollingUpgradeStartupOption.    */
annotation|@
name|Test
DECL|method|testStartupOptionParsing ()
specifier|public
name|void
name|testStartupOptionParsing
parameter_list|()
block|{
name|verifyStartupOptionResult
argument_list|(
literal|"FORMAT"
argument_list|,
name|StartupOption
operator|.
name|FORMAT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"REGULAR"
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"CHECKPOINT"
argument_list|,
name|StartupOption
operator|.
name|CHECKPOINT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"UPGRADE"
argument_list|,
name|StartupOption
operator|.
name|UPGRADE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"ROLLBACK"
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"FINALIZE"
argument_list|,
name|StartupOption
operator|.
name|FINALIZE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"ROLLINGUPGRADE"
argument_list|,
name|StartupOption
operator|.
name|ROLLINGUPGRADE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"IMPORT"
argument_list|,
name|StartupOption
operator|.
name|IMPORT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"INITIALIZESHAREDEDITS"
argument_list|,
name|StartupOption
operator|.
name|INITIALIZESHAREDEDITS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyStartupOptionResult
argument_list|(
literal|"UNKNOWN(UNKNOWNOPTION)"
argument_list|,
name|StartupOption
operator|.
name|FORMAT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to get expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Expected!
block|}
block|}
comment|/**    * Test that we can parse a StartupOption string with a    * RollingUpgradeStartupOption.    */
annotation|@
name|Test
DECL|method|testRollingUpgradeStartupOptionParsing ()
specifier|public
name|void
name|testRollingUpgradeStartupOptionParsing
parameter_list|()
block|{
name|verifyStartupOptionResult
argument_list|(
literal|"ROLLINGUPGRADE(ROLLBACK)"
argument_list|,
name|StartupOption
operator|.
name|ROLLINGUPGRADE
argument_list|,
name|RollingUpgradeStartupOption
operator|.
name|ROLLBACK
argument_list|)
expr_stmt|;
name|verifyStartupOptionResult
argument_list|(
literal|"ROLLINGUPGRADE(STARTED)"
argument_list|,
name|StartupOption
operator|.
name|ROLLINGUPGRADE
argument_list|,
name|RollingUpgradeStartupOption
operator|.
name|STARTED
argument_list|)
expr_stmt|;
try|try
block|{
name|verifyStartupOptionResult
argument_list|(
literal|"ROLLINGUPGRADE(UNKNOWNOPTION)"
argument_list|,
name|StartupOption
operator|.
name|ROLLINGUPGRADE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Failed to get expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Expected!
block|}
block|}
block|}
end_class

end_unit

