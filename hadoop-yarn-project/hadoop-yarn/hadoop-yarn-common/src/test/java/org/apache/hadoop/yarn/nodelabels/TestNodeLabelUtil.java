begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
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
name|fail
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
DECL|class|TestNodeLabelUtil
specifier|public
class|class
name|TestNodeLabelUtil
block|{
annotation|@
name|Test
DECL|method|testAttributeValueAddition ()
specifier|public
name|void
name|testAttributeValueAddition
parameter_list|()
block|{
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[]
block|{
literal|"1_8"
block|,
literal|"1.8"
block|,
literal|"ABZ"
block|,
literal|"ABZ"
block|,
literal|"az"
block|,
literal|"a-z"
block|,
literal|"a_z"
block|,
literal|"123456789"
block|}
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|values
control|)
block|{
try|try
block|{
name|NodeLabelUtil
operator|.
name|checkAndThrowAttributeValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Valid values for NodeAttributeValue :"
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|invalidVals
init|=
operator|new
name|String
index|[]
block|{
literal|"_18"
block|,
literal|"1,8"
block|,
literal|"1/5"
block|,
literal|".15"
block|,
literal|"1\\5"
block|}
decl_stmt|;
for|for
control|(
name|String
name|val
range|:
name|invalidVals
control|)
block|{
try|try
block|{
name|NodeLabelUtil
operator|.
name|checkAndThrowAttributeValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Valid values for NodeAttributeValue :"
operator|+
name|val
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// IGNORE
block|}
block|}
block|}
block|}
end_class

end_unit

