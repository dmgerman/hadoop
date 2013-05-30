begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_comment
comment|/**  * Class for test units to extend in order that their individual tests will  * be timed out and fail automatically should they run more than 10 seconds.  * This provides an automatic regression check for tests that begin running  * longer than expected.  */
end_comment

begin_class
DECL|class|UnitTestcaseTimeLimit
specifier|public
class|class
name|UnitTestcaseTimeLimit
block|{
DECL|field|timeOutSecs
specifier|public
specifier|final
name|int
name|timeOutSecs
init|=
literal|10
decl_stmt|;
DECL|field|globalTimeout
annotation|@
name|Rule
specifier|public
name|TestRule
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
name|timeOutSecs
operator|*
literal|1000
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

