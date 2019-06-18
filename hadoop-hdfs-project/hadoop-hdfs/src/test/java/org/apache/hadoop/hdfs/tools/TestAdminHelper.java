begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
comment|/**  * Test class to test Admin Helper.  */
end_comment

begin_class
DECL|class|TestAdminHelper
specifier|public
class|class
name|TestAdminHelper
block|{
annotation|@
name|Test
DECL|method|prettifyExceptionWithNpe ()
specifier|public
name|void
name|prettifyExceptionWithNpe
parameter_list|()
block|{
name|String
name|pretty
init|=
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
operator|new
name|NullPointerException
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Prettified exception message doesn't contain the required exception "
operator|+
literal|"message"
argument_list|,
name|pretty
operator|.
name|startsWith
argument_list|(
literal|"NullPointerException at org.apache.hadoop.hdfs.tools"
operator|+
literal|".TestAdminHelper.prettifyExceptionWithNpe"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|prettifyException ()
specifier|public
name|void
name|prettifyException
parameter_list|()
block|{
name|String
name|pretty
init|=
name|AdminHelper
operator|.
name|prettifyException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Something is wrong"
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Something is illegal"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"IllegalArgumentException: Something is wrong"
argument_list|,
name|pretty
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

