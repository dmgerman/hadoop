begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  *  * Class to store CLI Test Data  */
end_comment

begin_class
DECL|class|CLITestData
specifier|public
class|class
name|CLITestData
block|{
DECL|field|testDesc
specifier|private
name|String
name|testDesc
init|=
literal|null
decl_stmt|;
DECL|field|testCommands
specifier|private
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|testCommands
init|=
literal|null
decl_stmt|;
DECL|field|cleanupCommands
specifier|private
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|cleanupCommands
init|=
literal|null
decl_stmt|;
DECL|field|comparatorData
specifier|private
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|comparatorData
init|=
literal|null
decl_stmt|;
DECL|field|testResult
specifier|private
name|boolean
name|testResult
init|=
literal|false
decl_stmt|;
DECL|method|CLITestData ()
specifier|public
name|CLITestData
parameter_list|()
block|{    }
comment|/**    * @return the testDesc    */
DECL|method|getTestDesc ()
specifier|public
name|String
name|getTestDesc
parameter_list|()
block|{
return|return
name|testDesc
return|;
block|}
comment|/**    * @param testDesc the testDesc to set    */
DECL|method|setTestDesc (String testDesc)
specifier|public
name|void
name|setTestDesc
parameter_list|(
name|String
name|testDesc
parameter_list|)
block|{
name|this
operator|.
name|testDesc
operator|=
name|testDesc
expr_stmt|;
block|}
comment|/**    * @return the testCommands    */
DECL|method|getTestCommands ()
specifier|public
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|getTestCommands
parameter_list|()
block|{
return|return
name|testCommands
return|;
block|}
comment|/**    * @param testCommands the testCommands to set    */
DECL|method|setTestCommands (ArrayList<CLICommand> testCommands)
specifier|public
name|void
name|setTestCommands
parameter_list|(
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|testCommands
parameter_list|)
block|{
name|this
operator|.
name|testCommands
operator|=
name|testCommands
expr_stmt|;
block|}
comment|/**    * @return the comparatorData    */
DECL|method|getComparatorData ()
specifier|public
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|getComparatorData
parameter_list|()
block|{
return|return
name|comparatorData
return|;
block|}
comment|/**    * @param comparatorData the comparatorData to set    */
DECL|method|setComparatorData (ArrayList<ComparatorData> comparatorData)
specifier|public
name|void
name|setComparatorData
parameter_list|(
name|ArrayList
argument_list|<
name|ComparatorData
argument_list|>
name|comparatorData
parameter_list|)
block|{
name|this
operator|.
name|comparatorData
operator|=
name|comparatorData
expr_stmt|;
block|}
comment|/**    * @return the testResult    */
DECL|method|getTestResult ()
specifier|public
name|boolean
name|getTestResult
parameter_list|()
block|{
return|return
name|testResult
return|;
block|}
comment|/**    * @param testResult the testResult to set    */
DECL|method|setTestResult (boolean testResult)
specifier|public
name|void
name|setTestResult
parameter_list|(
name|boolean
name|testResult
parameter_list|)
block|{
name|this
operator|.
name|testResult
operator|=
name|testResult
expr_stmt|;
block|}
comment|/**    * @return the cleanupCommands    */
DECL|method|getCleanupCommands ()
specifier|public
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|getCleanupCommands
parameter_list|()
block|{
return|return
name|cleanupCommands
return|;
block|}
comment|/**    * @param cleanupCommands the cleanupCommands to set    */
DECL|method|setCleanupCommands (ArrayList<CLICommand> cleanupCommands)
specifier|public
name|void
name|setCleanupCommands
parameter_list|(
name|ArrayList
argument_list|<
name|CLICommand
argument_list|>
name|cleanupCommands
parameter_list|)
block|{
name|this
operator|.
name|cleanupCommands
operator|=
name|cleanupCommands
expr_stmt|;
block|}
block|}
end_class

end_unit

