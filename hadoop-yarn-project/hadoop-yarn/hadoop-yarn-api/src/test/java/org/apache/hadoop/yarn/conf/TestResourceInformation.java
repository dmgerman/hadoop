begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ResourceInformation
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
comment|/**  * Test class to verify various resource informations in a given resource.  */
end_comment

begin_class
DECL|class|TestResourceInformation
specifier|public
class|class
name|TestResourceInformation
block|{
annotation|@
name|Test
DECL|method|testName ()
specifier|public
name|void
name|testName
parameter_list|()
block|{
name|String
name|name
init|=
literal|"yarn.io/test"
decl_stmt|;
name|ResourceInformation
name|ri
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource name incorrect"
argument_list|,
name|name
argument_list|,
name|ri
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnits ()
specifier|public
name|void
name|testUnits
parameter_list|()
block|{
name|String
name|name
init|=
literal|"yarn.io/test"
decl_stmt|;
name|String
name|units
init|=
literal|"m"
decl_stmt|;
name|ResourceInformation
name|ri
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource name incorrect"
argument_list|,
name|name
argument_list|,
name|ri
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource units incorrect"
argument_list|,
name|units
argument_list|,
name|ri
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
name|units
operator|=
literal|"z"
expr_stmt|;
try|try
block|{
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
name|units
operator|+
literal|"is not a valid unit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ie
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
annotation|@
name|Test
DECL|method|testValue ()
specifier|public
name|void
name|testValue
parameter_list|()
block|{
name|String
name|name
init|=
literal|"yarn.io/test"
decl_stmt|;
name|long
name|value
init|=
literal|1L
decl_stmt|;
name|ResourceInformation
name|ri
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource name incorrect"
argument_list|,
name|name
argument_list|,
name|ri
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource value incorrect"
argument_list|,
name|value
argument_list|,
name|ri
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testResourceInformation ()
specifier|public
name|void
name|testResourceInformation
parameter_list|()
block|{
name|String
name|name
init|=
literal|"yarn.io/test"
decl_stmt|;
name|long
name|value
init|=
literal|1L
decl_stmt|;
name|String
name|units
init|=
literal|"m"
decl_stmt|;
name|ResourceInformation
name|ri
init|=
name|ResourceInformation
operator|.
name|newInstance
argument_list|(
name|name
argument_list|,
name|units
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource name incorrect"
argument_list|,
name|name
argument_list|,
name|ri
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource value incorrect"
argument_list|,
name|value
argument_list|,
name|ri
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Resource units incorrect"
argument_list|,
name|units
argument_list|,
name|ri
operator|.
name|getUnits
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

