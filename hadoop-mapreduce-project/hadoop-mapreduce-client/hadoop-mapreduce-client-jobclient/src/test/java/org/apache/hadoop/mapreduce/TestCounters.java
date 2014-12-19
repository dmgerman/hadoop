begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|DataInputBuffer
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
name|io
operator|.
name|DataOutputBuffer
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|mapreduce
operator|.
name|counters
operator|.
name|LimitExceededException
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
name|mapreduce
operator|.
name|counters
operator|.
name|Limits
import|;
end_import

begin_comment
comment|/**  * TestCounters checks the sanity and recoverability of {@code Counters}  */
end_comment

begin_class
DECL|class|TestCounters
specifier|public
class|class
name|TestCounters
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestCounters
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Verify counter value works    */
annotation|@
name|Test
DECL|method|testCounterValue ()
specifier|public
name|void
name|testCounterValue
parameter_list|()
block|{
specifier|final
name|int
name|NUMBER_TESTS
init|=
literal|100
decl_stmt|;
specifier|final
name|int
name|NUMBER_INC
init|=
literal|10
decl_stmt|;
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUMBER_TESTS
condition|;
name|i
operator|++
control|)
block|{
name|long
name|initValue
init|=
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|long
name|expectedValue
init|=
name|initValue
decl_stmt|;
name|Counter
name|counter
init|=
operator|new
name|Counters
argument_list|()
operator|.
name|findCounter
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|counter
operator|.
name|setValue
argument_list|(
name|initValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Counter value is not initialized correctly"
argument_list|,
name|expectedValue
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUMBER_INC
condition|;
name|j
operator|++
control|)
block|{
name|int
name|incValue
init|=
name|rand
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|counter
operator|.
name|increment
argument_list|(
name|incValue
argument_list|)
expr_stmt|;
name|expectedValue
operator|+=
name|incValue
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Counter value is not incremented correctly"
argument_list|,
name|expectedValue
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|expectedValue
operator|=
name|rand
operator|.
name|nextInt
argument_list|()
expr_stmt|;
name|counter
operator|.
name|setValue
argument_list|(
name|expectedValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Counter value is not set correctly"
argument_list|,
name|expectedValue
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLimits ()
annotation|@
name|Test
specifier|public
name|void
name|testLimits
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
operator|++
name|i
control|)
block|{
comment|// make sure limits apply to separate containers
name|testMaxCounters
argument_list|(
operator|new
name|Counters
argument_list|()
argument_list|)
expr_stmt|;
name|testMaxGroups
argument_list|(
operator|new
name|Counters
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testResetOnDeserialize ()
annotation|@
name|Test
specifier|public
name|void
name|testResetOnDeserialize
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Allow only one counterGroup
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|MRJobConfig
operator|.
name|COUNTER_GROUPS_MAX_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Limits
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Counters
name|countersWithOneGroup
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
name|countersWithOneGroup
operator|.
name|findCounter
argument_list|(
literal|"firstOf1Allowed"
argument_list|,
literal|"First group"
argument_list|)
expr_stmt|;
name|boolean
name|caughtExpectedException
init|=
literal|false
decl_stmt|;
try|try
block|{
name|countersWithOneGroup
operator|.
name|findCounter
argument_list|(
literal|"secondIsTooMany"
argument_list|,
literal|"Second group"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LimitExceededException
name|_
parameter_list|)
block|{
name|caughtExpectedException
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"Did not throw expected exception"
argument_list|,
name|caughtExpectedException
argument_list|)
expr_stmt|;
name|Counters
name|countersWithZeroGroups
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|countersWithZeroGroups
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|countersWithOneGroup
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// After reset one should be able to add a group
name|countersWithOneGroup
operator|.
name|findCounter
argument_list|(
literal|"firstGroupAfterReset"
argument_list|,
literal|"After reset "
operator|+
literal|"limit should be set back to zero"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountersIncrement ()
specifier|public
name|void
name|testCountersIncrement
parameter_list|()
block|{
name|Counters
name|fCounters
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
name|Counter
name|fCounter
init|=
name|fCounters
operator|.
name|findCounter
argument_list|(
name|FRAMEWORK_COUNTER
argument_list|)
decl_stmt|;
name|fCounter
operator|.
name|setValue
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|Counter
name|gCounter
init|=
name|fCounters
operator|.
name|findCounter
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|gCounter
operator|.
name|setValue
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|Counters
name|counters
init|=
operator|new
name|Counters
argument_list|()
decl_stmt|;
name|counters
operator|.
name|incrAllCounters
argument_list|(
name|fCounters
argument_list|)
expr_stmt|;
name|Counter
name|counter
decl_stmt|;
for|for
control|(
name|CounterGroup
name|cg
range|:
name|fCounters
control|)
block|{
name|CounterGroup
name|group
init|=
name|counters
operator|.
name|getGroup
argument_list|(
name|cg
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
name|counter
operator|=
name|counters
operator|.
name|findCounter
argument_list|(
literal|"test"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|counter
operator|=
name|counters
operator|.
name|findCounter
argument_list|(
name|FRAMEWORK_COUNTER
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|counter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|FRAMEWORK_COUNTER
specifier|static
specifier|final
name|Enum
argument_list|<
name|?
argument_list|>
name|FRAMEWORK_COUNTER
init|=
name|TaskCounter
operator|.
name|CPU_MILLISECONDS
decl_stmt|;
DECL|field|FRAMEWORK_COUNTER_VALUE
specifier|static
specifier|final
name|long
name|FRAMEWORK_COUNTER_VALUE
init|=
literal|8
decl_stmt|;
DECL|field|FS_SCHEME
specifier|static
specifier|final
name|String
name|FS_SCHEME
init|=
literal|"HDFS"
decl_stmt|;
DECL|field|FS_COUNTER
specifier|static
specifier|final
name|FileSystemCounter
name|FS_COUNTER
init|=
name|FileSystemCounter
operator|.
name|BYTES_READ
decl_stmt|;
DECL|field|FS_COUNTER_VALUE
specifier|static
specifier|final
name|long
name|FS_COUNTER_VALUE
init|=
literal|10
decl_stmt|;
DECL|method|testMaxCounters (final Counters counters)
specifier|private
name|void
name|testMaxCounters
parameter_list|(
specifier|final
name|Counters
name|counters
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"counters max="
operator|+
name|Limits
operator|.
name|getCountersMax
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
name|Limits
operator|.
name|getCountersMax
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|counters
operator|.
name|findCounter
argument_list|(
literal|"test"
argument_list|,
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|setExpected
argument_list|(
name|counters
argument_list|)
expr_stmt|;
name|shouldThrow
argument_list|(
name|LimitExceededException
operator|.
name|class
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|counters
operator|.
name|findCounter
argument_list|(
literal|"test"
argument_list|,
literal|"bad"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|checkExpected
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxGroups (final Counters counters)
specifier|private
name|void
name|testMaxGroups
parameter_list|(
specifier|final
name|Counters
name|counters
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"counter groups max="
operator|+
name|Limits
operator|.
name|getGroupsMax
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
name|Limits
operator|.
name|getGroupsMax
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
comment|// assuming COUNTERS_MAX> GROUPS_MAX
name|counters
operator|.
name|findCounter
argument_list|(
literal|"test"
operator|+
name|i
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|setExpected
argument_list|(
name|counters
argument_list|)
expr_stmt|;
name|shouldThrow
argument_list|(
name|LimitExceededException
operator|.
name|class
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|counters
operator|.
name|findCounter
argument_list|(
literal|"bad"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|checkExpected
argument_list|(
name|counters
argument_list|)
expr_stmt|;
block|}
DECL|method|setExpected (Counters counters)
specifier|private
name|void
name|setExpected
parameter_list|(
name|Counters
name|counters
parameter_list|)
block|{
name|counters
operator|.
name|findCounter
argument_list|(
name|FRAMEWORK_COUNTER
argument_list|)
operator|.
name|setValue
argument_list|(
name|FRAMEWORK_COUNTER_VALUE
argument_list|)
expr_stmt|;
name|counters
operator|.
name|findCounter
argument_list|(
name|FS_SCHEME
argument_list|,
name|FS_COUNTER
argument_list|)
operator|.
name|setValue
argument_list|(
name|FS_COUNTER_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|checkExpected (Counters counters)
specifier|private
name|void
name|checkExpected
parameter_list|(
name|Counters
name|counters
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|FRAMEWORK_COUNTER_VALUE
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|FRAMEWORK_COUNTER
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|FS_COUNTER_VALUE
argument_list|,
name|counters
operator|.
name|findCounter
argument_list|(
name|FS_SCHEME
argument_list|,
name|FS_COUNTER
argument_list|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|shouldThrow (Class<? extends Exception> ecls, Runnable runnable)
specifier|private
name|void
name|shouldThrow
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|ecls
parameter_list|,
name|Runnable
name|runnable
parameter_list|)
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertSame
argument_list|(
name|ecls
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got expected: "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertTrue
argument_list|(
literal|"Should've thrown "
operator|+
name|ecls
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

