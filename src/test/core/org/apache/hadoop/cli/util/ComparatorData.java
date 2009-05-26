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

begin_comment
comment|/**  *  * Class to store CLI Test Comparators Data  */
end_comment

begin_class
DECL|class|ComparatorData
specifier|public
class|class
name|ComparatorData
block|{
DECL|field|expectedOutput
specifier|private
name|String
name|expectedOutput
init|=
literal|null
decl_stmt|;
DECL|field|actualOutput
specifier|private
name|String
name|actualOutput
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
DECL|field|exitCode
specifier|private
name|int
name|exitCode
init|=
literal|0
decl_stmt|;
DECL|field|comparatorType
specifier|private
name|String
name|comparatorType
init|=
literal|null
decl_stmt|;
DECL|method|ComparatorData ()
specifier|public
name|ComparatorData
parameter_list|()
block|{    }
comment|/**    * @return the expectedOutput    */
DECL|method|getExpectedOutput ()
specifier|public
name|String
name|getExpectedOutput
parameter_list|()
block|{
return|return
name|expectedOutput
return|;
block|}
comment|/**    * @param expectedOutput the expectedOutput to set    */
DECL|method|setExpectedOutput (String expectedOutput)
specifier|public
name|void
name|setExpectedOutput
parameter_list|(
name|String
name|expectedOutput
parameter_list|)
block|{
name|this
operator|.
name|expectedOutput
operator|=
name|expectedOutput
expr_stmt|;
block|}
comment|/**    * @return the actualOutput    */
DECL|method|getActualOutput ()
specifier|public
name|String
name|getActualOutput
parameter_list|()
block|{
return|return
name|actualOutput
return|;
block|}
comment|/**    * @param actualOutput the actualOutput to set    */
DECL|method|setActualOutput (String actualOutput)
specifier|public
name|void
name|setActualOutput
parameter_list|(
name|String
name|actualOutput
parameter_list|)
block|{
name|this
operator|.
name|actualOutput
operator|=
name|actualOutput
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
comment|/**    * @return the exitCode    */
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
comment|/**    * @param exitCode the exitCode to set    */
DECL|method|setExitCode (int exitCode)
specifier|public
name|void
name|setExitCode
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
block|}
comment|/**    * @return the comparatorType    */
DECL|method|getComparatorType ()
specifier|public
name|String
name|getComparatorType
parameter_list|()
block|{
return|return
name|comparatorType
return|;
block|}
comment|/**    * @param comparatorType the comparatorType to set    */
DECL|method|setComparatorType (String comparatorType)
specifier|public
name|void
name|setComparatorType
parameter_list|(
name|String
name|comparatorType
parameter_list|)
block|{
name|this
operator|.
name|comparatorType
operator|=
name|comparatorType
expr_stmt|;
block|}
block|}
end_class

end_unit

