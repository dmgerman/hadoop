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

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_comment
comment|/**  * Test for FastNumberFormat  */
end_comment

begin_class
DECL|class|TestFastNumberFormat
specifier|public
class|class
name|TestFastNumberFormat
block|{
DECL|field|MIN_DIGITS
specifier|private
specifier|final
name|int
name|MIN_DIGITS
init|=
literal|6
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testLongWithPadding ()
specifier|public
name|void
name|testLongWithPadding
parameter_list|()
throws|throws
name|Exception
block|{
name|NumberFormat
name|numberFormat
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|numberFormat
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|numberFormat
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|long
index|[]
name|testLongs
init|=
block|{
literal|1
block|,
literal|23
block|,
literal|456
block|,
literal|7890
block|,
literal|12345
block|,
literal|678901
block|,
literal|2345689
block|,
literal|0
block|,
operator|-
literal|0
block|,
operator|-
literal|1
block|,
operator|-
literal|23
block|,
operator|-
literal|456
block|,
operator|-
literal|7890
block|,
operator|-
literal|12345
block|,
operator|-
literal|678901
block|,
operator|-
literal|2345689
block|}
decl_stmt|;
for|for
control|(
name|long
name|l
range|:
name|testLongs
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|FastNumberFormat
operator|.
name|format
argument_list|(
name|sb
argument_list|,
name|l
argument_list|,
name|MIN_DIGITS
argument_list|)
expr_stmt|;
name|String
name|fastNumberStr
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Number formats should be equal"
argument_list|,
name|numberFormat
operator|.
name|format
argument_list|(
name|l
argument_list|)
argument_list|,
name|fastNumberStr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

