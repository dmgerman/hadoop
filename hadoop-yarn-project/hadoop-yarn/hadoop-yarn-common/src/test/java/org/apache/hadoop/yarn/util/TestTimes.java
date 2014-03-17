begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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

begin_class
DECL|class|TestTimes
specifier|public
class|class
name|TestTimes
block|{
annotation|@
name|Test
DECL|method|testNegativeStartTimes ()
specifier|public
name|void
name|testNegativeStartTimes
parameter_list|()
block|{
name|long
name|elapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
operator|-
literal|5
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not 0"
argument_list|,
literal|0
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
name|elapsed
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
operator|-
literal|5
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNegativeFinishTimes ()
specifier|public
name|void
name|testNegativeFinishTimes
parameter_list|()
block|{
name|long
name|elapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
literal|5
argument_list|,
operator|-
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNegativeStartandFinishTimes ()
specifier|public
name|void
name|testNegativeStartandFinishTimes
parameter_list|()
block|{
name|long
name|elapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
operator|-
literal|5
argument_list|,
operator|-
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not -1"
argument_list|,
operator|-
literal|1
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPositiveStartandFinishTimes ()
specifier|public
name|void
name|testPositiveStartandFinishTimes
parameter_list|()
block|{
name|long
name|elapsed
init|=
name|Times
operator|.
name|elapsed
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not 5"
argument_list|,
literal|5
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
name|elapsed
operator|=
name|Times
operator|.
name|elapsed
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Elapsed time is not 5"
argument_list|,
literal|5
argument_list|,
name|elapsed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

