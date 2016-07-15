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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|assertNotNull
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskErrorException
import|;
end_import

begin_comment
comment|/**  * The class to test DiskValidatorFactory.  */
end_comment

begin_class
DECL|class|TestDiskValidatorFactory
specifier|public
class|class
name|TestDiskValidatorFactory
block|{
comment|/**    * Trivial tests that make sure    * {@link DiskValidatorFactory#getInstance(String)} works as expected.    *    * @throws DiskErrorException if fail to get the instance.    */
annotation|@
name|Test
DECL|method|testGetInstance ()
specifier|public
name|void
name|testGetInstance
parameter_list|()
throws|throws
name|DiskErrorException
block|{
name|DiskValidator
name|diskValidator
init|=
name|DiskValidatorFactory
operator|.
name|getInstance
argument_list|(
literal|"basic"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Fail to get the instance."
argument_list|,
name|diskValidator
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fail to create the correct instance."
argument_list|,
name|diskValidator
operator|.
name|getClass
argument_list|()
argument_list|,
name|BasicDiskValidator
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Fail to cache the object"
argument_list|,
name|DiskValidatorFactory
operator|.
name|INSTANCES
operator|.
name|get
argument_list|(
name|BasicDiskValidator
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * To test whether an exception is threw out as expected if trying to create    * a non-exist class.    * @throws DiskErrorException if fail to get the instance.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|DiskErrorException
operator|.
name|class
argument_list|)
DECL|method|testGetInstanceOfNonExistClass ()
specifier|public
name|void
name|testGetInstanceOfNonExistClass
parameter_list|()
throws|throws
name|DiskErrorException
block|{
name|DiskValidatorFactory
operator|.
name|getInstance
argument_list|(
literal|"non-exist"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

