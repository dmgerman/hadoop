begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.log
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|log
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
name|log
operator|.
name|LogThrottlingHelper
operator|.
name|LogAction
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
name|FakeTimer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for {@link LogThrottlingHelper}.  */
end_comment

begin_class
DECL|class|TestLogThrottlingHelper
specifier|public
class|class
name|TestLogThrottlingHelper
block|{
DECL|field|LOG_PERIOD
specifier|private
specifier|static
specifier|final
name|int
name|LOG_PERIOD
init|=
literal|100
decl_stmt|;
DECL|field|helper
specifier|private
name|LogThrottlingHelper
name|helper
decl_stmt|;
DECL|field|timer
specifier|private
name|FakeTimer
name|timer
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|timer
operator|=
operator|new
name|FakeTimer
argument_list|()
expr_stmt|;
name|helper
operator|=
operator|new
name|LogThrottlingHelper
argument_list|(
name|LOG_PERIOD
argument_list|,
literal|null
argument_list|,
name|timer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicLogging ()
specifier|public
name|void
name|testBasicLogging
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|()
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
operator|/
literal|10
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|()
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|()
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoggingWithValue ()
specifier|public
name|void
name|testLoggingWithValue
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|1
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
operator|/
literal|5
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
argument_list|)
expr_stmt|;
name|LogAction
name|action
init|=
name|helper
operator|.
name|record
argument_list|(
literal|0.5
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|action
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|action
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.5
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getMax
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getMin
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoggingWithMultipleValues ()
specifier|public
name|void
name|testLoggingWithMultipleValues
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|1
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
operator|/
literal|5
argument_list|)
expr_stmt|;
name|int
name|base
init|=
name|i
operator|%
literal|2
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
name|base
argument_list|,
name|base
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|timer
operator|.
name|advance
argument_list|(
name|LOG_PERIOD
argument_list|)
expr_stmt|;
name|LogAction
name|action
init|=
name|helper
operator|.
name|record
argument_list|(
literal|0.5
argument_list|,
literal|1.0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|action
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|action
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|0.5
operator|*
name|i
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1.0
operator|*
name|i
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getMax
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|action
operator|.
name|getStats
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|getMin
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testLoggingWithInconsistentValues ()
specifier|public
name|void
name|testLoggingWithInconsistentValues
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|helper
operator|.
name|record
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|helper
operator|.
name|record
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNamedLoggersWithoutSpecifiedPrimary ()
specifier|public
name|void
name|testNamedLoggersWithoutSpecifiedPrimary
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
operator|(
name|LOG_PERIOD
operator|*
literal|3
operator|)
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
operator|(
name|LOG_PERIOD
operator|*
literal|3
operator|)
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrimaryAndDependentLoggers ()
specifier|public
name|void
name|testPrimaryAndDependentLoggers
parameter_list|()
block|{
name|helper
operator|=
operator|new
name|LogThrottlingHelper
argument_list|(
name|LOG_PERIOD
argument_list|,
literal|"foo"
argument_list|,
name|timer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
operator|/
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// Both should log once the period has elapsed
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// "bar" should not log yet because "foo" hasn't been triggered
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
operator|*
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// The timing of "bar" shouldn't matter as it is dependent on "foo"
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleLoggersWithValues ()
specifier|public
name|void
name|testMultipleLoggersWithValues
parameter_list|()
block|{
name|helper
operator|=
operator|new
name|LogThrottlingHelper
argument_list|(
name|LOG_PERIOD
argument_list|,
literal|"foo"
argument_list|,
name|timer
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"baz"
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// "bar"/"baz" should not log yet because "foo" hasn't been triggered
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
argument_list|,
literal|2
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|helper
operator|.
name|record
argument_list|(
literal|"baz"
argument_list|,
name|LOG_PERIOD
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
comment|// All should log once the period has elapsed
name|LogAction
name|foo
init|=
name|helper
operator|.
name|record
argument_list|(
literal|"foo"
argument_list|,
name|LOG_PERIOD
argument_list|)
decl_stmt|;
name|LogAction
name|bar
init|=
name|helper
operator|.
name|record
argument_list|(
literal|"bar"
argument_list|,
name|LOG_PERIOD
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|LogAction
name|baz
init|=
name|helper
operator|.
name|record
argument_list|(
literal|"baz"
argument_list|,
name|LOG_PERIOD
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|foo
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bar
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baz
operator|.
name|shouldLog
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|foo
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|bar
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|baz
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|bar
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|baz
operator|.
name|getStats
argument_list|(
literal|0
argument_list|)
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|baz
operator|.
name|getStats
argument_list|(
literal|1
argument_list|)
operator|.
name|getMean
argument_list|()
argument_list|,
literal|0.01
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0
argument_list|,
name|helper
operator|.
name|getCurrentStats
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|)
operator|.
name|getMax
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3.0
argument_list|,
name|helper
operator|.
name|getCurrentStats
argument_list|(
literal|"baz"
argument_list|,
literal|0
argument_list|)
operator|.
name|getMax
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

