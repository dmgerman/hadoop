begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
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
name|*
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
DECL|class|TestResult
specifier|public
class|class
name|TestResult
block|{
comment|// test the PASS value
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testPass ()
specifier|public
name|void
name|testPass
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|PASS
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the FAIL value
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testFail ()
specifier|public
name|void
name|testFail
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|FAIL
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the STOP value
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testStop ()
specifier|public
name|void
name|testStop
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|STOP
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test combine method with two PASSes
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combinePassPass ()
specifier|public
name|void
name|combinePassPass
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|PASS
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a PASS and a FAIL
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combinePassFail ()
specifier|public
name|void
name|combinePassFail
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|PASS
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a FAIL and a PASS
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combineFailPass ()
specifier|public
name|void
name|combineFailPass
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|FAIL
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with two FAILs
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combineFailFail ()
specifier|public
name|void
name|combineFailFail
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|FAIL
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a PASS and STOP
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combinePassStop ()
specifier|public
name|void
name|combinePassStop
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|PASS
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|STOP
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a STOP and FAIL
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combineStopFail ()
specifier|public
name|void
name|combineStopFail
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|STOP
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a STOP and a PASS
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combineStopPass ()
specifier|public
name|void
name|combineStopPass
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|STOP
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the combine method with a FAIL and a STOP
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|combineFailStop ()
specifier|public
name|void
name|combineFailStop
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|FAIL
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|STOP
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the negation of PASS
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|negatePass ()
specifier|public
name|void
name|negatePass
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|PASS
operator|.
name|negate
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the negation of FAIL
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|negateFail ()
specifier|public
name|void
name|negateFail
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|FAIL
operator|.
name|negate
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test the negation of STOP
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|negateStop ()
specifier|public
name|void
name|negateStop
parameter_list|()
block|{
name|Result
name|result
init|=
name|Result
operator|.
name|STOP
operator|.
name|negate
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isPass
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isDescend
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test equals with two PASSes
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|equalsPass ()
specifier|public
name|void
name|equalsPass
parameter_list|()
block|{
name|Result
name|one
init|=
name|Result
operator|.
name|PASS
decl_stmt|;
name|Result
name|two
init|=
name|Result
operator|.
name|PASS
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
expr_stmt|;
block|}
comment|// test equals with two FAILs
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|equalsFail ()
specifier|public
name|void
name|equalsFail
parameter_list|()
block|{
name|Result
name|one
init|=
name|Result
operator|.
name|FAIL
decl_stmt|;
name|Result
name|two
init|=
name|Result
operator|.
name|FAIL
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
expr_stmt|;
block|}
comment|// test equals with two STOPS
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|equalsStop ()
specifier|public
name|void
name|equalsStop
parameter_list|()
block|{
name|Result
name|one
init|=
name|Result
operator|.
name|STOP
decl_stmt|;
name|Result
name|two
init|=
name|Result
operator|.
name|STOP
operator|.
name|combine
argument_list|(
name|Result
operator|.
name|STOP
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|one
argument_list|,
name|two
argument_list|)
expr_stmt|;
block|}
comment|// test all combinations of not equals
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|notEquals ()
specifier|public
name|void
name|notEquals
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|Result
operator|.
name|PASS
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Result
operator|.
name|PASS
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|STOP
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Result
operator|.
name|FAIL
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Result
operator|.
name|FAIL
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|STOP
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Result
operator|.
name|STOP
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|PASS
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Result
operator|.
name|STOP
operator|.
name|equals
argument_list|(
name|Result
operator|.
name|FAIL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

