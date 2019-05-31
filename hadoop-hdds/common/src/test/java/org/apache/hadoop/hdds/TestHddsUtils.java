begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Testing HddsUtils.  */
end_comment

begin_class
DECL|class|TestHddsUtils
specifier|public
class|class
name|TestHddsUtils
block|{
annotation|@
name|Test
DECL|method|testGetHostName ()
specifier|public
name|void
name|testGetHostName
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
literal|"localhost"
argument_list|)
argument_list|,
name|HddsUtils
operator|.
name|getHostName
argument_list|(
literal|"localhost:1234"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
literal|"localhost"
argument_list|)
argument_list|,
name|HddsUtils
operator|.
name|getHostName
argument_list|(
literal|"localhost"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|Optional
operator|.
name|empty
argument_list|()
argument_list|,
name|HddsUtils
operator|.
name|getHostName
argument_list|(
literal|":1234"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

