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
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Comparator for the Command line tests.  *  * This comparator searches for an exact line as 'expected'  * in the string 'actual' and returns true if found  *  */
end_comment

begin_class
DECL|class|ExactLineComparator
specifier|public
class|class
name|ExactLineComparator
extends|extends
name|ComparatorBase
block|{
annotation|@
name|Override
DECL|method|compare (String actual, String expected)
specifier|public
name|boolean
name|compare
parameter_list|(
name|String
name|actual
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|actual
argument_list|,
literal|"\n\r"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
operator|&&
operator|!
name|success
condition|)
block|{
name|String
name|actualToken
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|success
operator|=
name|actualToken
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
return|return
name|success
return|;
block|}
block|}
end_class

end_unit

