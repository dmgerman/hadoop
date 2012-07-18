begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|util
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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|test
operator|.
name|HTestCase
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
DECL|class|TestCheck
specifier|public
class|class
name|TestCheck
extends|extends
name|HTestCase
block|{
annotation|@
name|Test
DECL|method|notNullNotNull ()
specifier|public
name|void
name|notNullNotNull
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Check
operator|.
name|notNull
argument_list|(
literal|"value"
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notNullNull ()
specifier|public
name|void
name|notNullNull
parameter_list|()
block|{
name|Check
operator|.
name|notNull
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|notNullElementsNotNull ()
specifier|public
name|void
name|notNullElementsNotNull
parameter_list|()
block|{
name|Check
operator|.
name|notNullElements
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notNullElements
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notNullElementsNullList ()
specifier|public
name|void
name|notNullElementsNullList
parameter_list|()
block|{
name|Check
operator|.
name|notNullElements
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notNullElementsNullElements ()
specifier|public
name|void
name|notNullElementsNullElements
parameter_list|()
block|{
name|Check
operator|.
name|notNullElements
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|notEmptyElementsNotNull ()
specifier|public
name|void
name|notEmptyElementsNotNull
parameter_list|()
block|{
name|Check
operator|.
name|notEmptyElements
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|Check
operator|.
name|notEmptyElements
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notEmptyElementsNullList ()
specifier|public
name|void
name|notEmptyElementsNullList
parameter_list|()
block|{
name|Check
operator|.
name|notEmptyElements
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notEmptyElementsNullElements ()
specifier|public
name|void
name|notEmptyElementsNullElements
parameter_list|()
block|{
name|Check
operator|.
name|notEmptyElements
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notEmptyElementsEmptyElements ()
specifier|public
name|void
name|notEmptyElementsEmptyElements
parameter_list|()
block|{
name|Check
operator|.
name|notEmptyElements
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|notEmptyNotEmtpy ()
specifier|public
name|void
name|notEmptyNotEmtpy
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Check
operator|.
name|notEmpty
argument_list|(
literal|"value"
argument_list|,
literal|"name"
argument_list|)
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notEmptyNull ()
specifier|public
name|void
name|notEmptyNull
parameter_list|()
block|{
name|Check
operator|.
name|notEmpty
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|notEmptyEmpty ()
specifier|public
name|void
name|notEmptyEmpty
parameter_list|()
block|{
name|Check
operator|.
name|notEmpty
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|validIdentifierValid ()
specifier|public
name|void
name|validIdentifierValid
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"a1"
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"a1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"a_"
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"a_"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"_"
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|validIdentifierInvalid1 ()
specifier|public
name|void
name|validIdentifierInvalid1
parameter_list|()
throws|throws
name|Exception
block|{
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"!"
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|validIdentifierInvalid2 ()
specifier|public
name|void
name|validIdentifierInvalid2
parameter_list|()
throws|throws
name|Exception
block|{
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"a1"
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|validIdentifierInvalid3 ()
specifier|public
name|void
name|validIdentifierInvalid3
parameter_list|()
throws|throws
name|Exception
block|{
name|Check
operator|.
name|validIdentifier
argument_list|(
literal|"1"
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkGTZeroGreater ()
specifier|public
name|void
name|checkGTZeroGreater
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Check
operator|.
name|gt0
argument_list|(
literal|120
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|120
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|checkGTZeroZero ()
specifier|public
name|void
name|checkGTZeroZero
parameter_list|()
block|{
name|Check
operator|.
name|gt0
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|checkGTZeroLessThanZero ()
specifier|public
name|void
name|checkGTZeroLessThanZero
parameter_list|()
block|{
name|Check
operator|.
name|gt0
argument_list|(
operator|-
literal|1
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|checkGEZero ()
specifier|public
name|void
name|checkGEZero
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Check
operator|.
name|ge0
argument_list|(
literal|120
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|120
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Check
operator|.
name|ge0
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|checkGELessThanZero ()
specifier|public
name|void
name|checkGELessThanZero
parameter_list|()
block|{
name|Check
operator|.
name|ge0
argument_list|(
operator|-
literal|1
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

