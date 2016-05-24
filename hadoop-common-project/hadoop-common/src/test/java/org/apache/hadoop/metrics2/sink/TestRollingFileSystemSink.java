begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.sink
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|configuration
operator|.
name|SubsetConfiguration
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
name|fs
operator|.
name|Path
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
name|metrics2
operator|.
name|MetricsException
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
name|metrics2
operator|.
name|impl
operator|.
name|ConfigBuilder
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
name|assertNull
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Test that the init() method picks up all the configuration settings  * correctly.  */
end_comment

begin_class
DECL|class|TestRollingFileSystemSink
specifier|public
class|class
name|TestRollingFileSystemSink
block|{
annotation|@
name|Test
DECL|method|testInit ()
specifier|public
name|void
name|testInit
parameter_list|()
block|{
name|ConfigBuilder
name|builder
init|=
operator|new
name|ConfigBuilder
argument_list|()
decl_stmt|;
name|SubsetConfiguration
name|conf
init|=
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
literal|"10m"
argument_list|)
operator|.
name|add
argument_list|(
literal|"sink.roll-offset-interval-millis"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|add
argument_list|(
literal|"sink.basepath"
argument_list|,
literal|"path"
argument_list|)
operator|.
name|add
argument_list|(
literal|"sink.ignore-error"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|add
argument_list|(
literal|"sink.allow-append"
argument_list|,
literal|"true"
argument_list|)
operator|.
name|add
argument_list|(
literal|"sink.source"
argument_list|,
literal|"src"
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
decl_stmt|;
name|RollingFileSystemSink
name|sink
init|=
operator|new
name|RollingFileSystemSink
argument_list|()
decl_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The roll interval was not set correctly"
argument_list|,
name|sink
operator|.
name|rollIntervalMillis
argument_list|,
literal|600000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The roll offset interval was not set correctly"
argument_list|,
name|sink
operator|.
name|rollOffsetIntervalMillis
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The base path was not set correctly"
argument_list|,
name|sink
operator|.
name|basePath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ignore-error was not set correctly"
argument_list|,
name|sink
operator|.
name|ignoreError
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"allow-append was not set correctly"
argument_list|,
name|sink
operator|.
name|allowAppend
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The source was not set correctly"
argument_list|,
name|sink
operator|.
name|source
argument_list|,
literal|"src"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test whether the initial roll interval is set correctly.    */
annotation|@
name|Test
DECL|method|testSetInitialFlushTime ()
specifier|public
name|void
name|testSetInitialFlushTime
parameter_list|()
block|{
name|RollingFileSystemSink
name|rfsSink
init|=
operator|new
name|RollingFileSystemSink
argument_list|(
literal|1000
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_YEAR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|2016
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Last flush time should have been null prior to calling init()"
argument_list|,
name|rfsSink
operator|.
name|nextFlush
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|diff
init|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"The initial flush time was calculated incorrectly"
argument_list|,
literal|0L
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The initial flush time was calculated incorrectly"
argument_list|,
operator|-
literal|10L
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The initial flush time was calculated incorrectly"
argument_list|,
operator|-
literal|10L
argument_list|,
name|diff
argument_list|)
expr_stmt|;
comment|// Try again with a random offset
name|rfsSink
operator|=
operator|new
name|RollingFileSystemSink
argument_list|(
literal|1000
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Last flush time should have been null prior to calling init()"
argument_list|,
name|rfsSink
operator|.
name|nextFlush
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The initial flush time was calculated incorrectly: "
operator|+
name|diff
argument_list|,
operator|(
name|diff
operator|>=
operator|-
literal|1000L
operator|)
operator|&&
operator|(
name|diff
operator|<
operator|-
literal|900L
operator|)
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The initial flush time was calculated incorrectly: "
operator|+
name|diff
argument_list|,
operator|(
operator|(
name|diff
operator|>=
operator|-
literal|10L
operator|)
operator|&&
operator|(
name|diff
operator|<=
literal|0L
operator|)
operator|||
operator|(
operator|(
name|diff
operator|>
operator|-
literal|1000L
operator|)
operator|&&
operator|(
name|diff
operator|<
operator|-
literal|910L
operator|)
operator|)
operator|)
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The initial flush time was calculated incorrectly: "
operator|+
name|diff
argument_list|,
operator|(
operator|(
name|diff
operator|>=
operator|-
literal|10L
operator|)
operator|&&
operator|(
name|diff
operator|<=
literal|0L
operator|)
operator|||
operator|(
operator|(
name|diff
operator|>
operator|-
literal|1000L
operator|)
operator|&&
operator|(
name|diff
operator|<
operator|-
literal|910L
operator|)
operator|)
operator|)
argument_list|)
expr_stmt|;
comment|// Now try pathological settings
name|rfsSink
operator|=
operator|new
name|RollingFileSystemSink
argument_list|(
literal|1000
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Last flush time should have been null prior to calling init()"
argument_list|,
name|rfsSink
operator|.
name|nextFlush
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|setInitialFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|diff
operator|=
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
operator|-
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The initial flush time was calculated incorrectly: "
operator|+
name|diff
argument_list|,
operator|(
name|diff
operator|>
operator|-
literal|1000L
operator|)
operator|&&
operator|(
name|diff
operator|<=
literal|0L
operator|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the roll time updates correctly.    */
annotation|@
name|Test
DECL|method|testUpdateRollTime ()
specifier|public
name|void
name|testUpdateRollTime
parameter_list|()
block|{
name|RollingFileSystemSink
name|rfsSink
init|=
operator|new
name|RollingFileSystemSink
argument_list|(
literal|1000
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|DAY_OF_YEAR
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|2016
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|nextFlush
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|rfsSink
operator|.
name|nextFlush
operator|.
name|setTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|updateFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The next roll time should have been 1 second in the future"
argument_list|,
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
operator|+
literal|1000
argument_list|,
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|nextFlush
operator|.
name|setTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|updateFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The next roll time should have been 990 ms in the future"
argument_list|,
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
operator|+
literal|990
argument_list|,
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|nextFlush
operator|.
name|setTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|rfsSink
operator|.
name|updateFlushTime
argument_list|(
name|calendar
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"The next roll time should have been 990 ms in the future"
argument_list|,
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
operator|+
literal|990
argument_list|,
name|rfsSink
operator|.
name|nextFlush
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test whether the roll interval is correctly calculated from the    * configuration settings.    */
annotation|@
name|Test
DECL|method|testGetRollInterval ()
specifier|public
name|void
name|testGetRollInterval
parameter_list|()
block|{
name|doTestGetRollInterval
argument_list|(
literal|1
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"m"
block|,
literal|"min"
block|,
literal|"minute"
block|,
literal|"minutes"
block|}
argument_list|,
literal|60
operator|*
literal|1000L
argument_list|)
expr_stmt|;
name|doTestGetRollInterval
argument_list|(
literal|1
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"h"
block|,
literal|"hr"
block|,
literal|"hour"
block|,
literal|"hours"
block|}
argument_list|,
literal|60
operator|*
literal|60
operator|*
literal|1000L
argument_list|)
expr_stmt|;
name|doTestGetRollInterval
argument_list|(
literal|1
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"d"
block|,
literal|"day"
block|,
literal|"days"
block|}
argument_list|,
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000L
argument_list|)
expr_stmt|;
name|ConfigBuilder
name|builder
init|=
operator|new
name|ConfigBuilder
argument_list|()
decl_stmt|;
name|SubsetConfiguration
name|conf
init|=
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
decl_stmt|;
comment|// We can reuse the same sink evry time because we're setting the same
comment|// property every time.
name|RollingFileSystemSink
name|sink
init|=
operator|new
name|RollingFileSystemSink
argument_list|()
decl_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3600000L
argument_list|,
name|sink
operator|.
name|getRollInterval
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|char
name|c
range|:
literal|"abcefgijklnopqrtuvwxyz"
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|builder
operator|=
operator|new
name|ConfigBuilder
argument_list|()
expr_stmt|;
name|conf
operator|=
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
literal|"90 "
operator|+
name|c
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
expr_stmt|;
try|try
block|{
name|sink
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|sink
operator|.
name|getRollInterval
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Allowed flush interval with bad units: "
operator|+
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MetricsException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
block|}
comment|/**    * Test the basic unit conversions with the given unit name modifier applied.    *    * @param mod a unit name modifier    */
DECL|method|doTestGetRollInterval (int num, String[] units, long expected)
specifier|private
name|void
name|doTestGetRollInterval
parameter_list|(
name|int
name|num
parameter_list|,
name|String
index|[]
name|units
parameter_list|,
name|long
name|expected
parameter_list|)
block|{
name|RollingFileSystemSink
name|sink
init|=
operator|new
name|RollingFileSystemSink
argument_list|()
decl_stmt|;
name|ConfigBuilder
name|builder
init|=
operator|new
name|ConfigBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|unit
range|:
name|units
control|)
block|{
name|sink
operator|.
name|init
argument_list|(
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
name|num
operator|+
name|unit
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|sink
operator|.
name|getRollInterval
argument_list|()
argument_list|)
expr_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
name|num
operator|+
name|unit
operator|.
name|toUpperCase
argument_list|()
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|sink
operator|.
name|getRollInterval
argument_list|()
argument_list|)
expr_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
name|num
operator|+
literal|" "
operator|+
name|unit
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|sink
operator|.
name|getRollInterval
argument_list|()
argument_list|)
expr_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|builder
operator|.
name|add
argument_list|(
literal|"sink.roll-interval"
argument_list|,
name|num
operator|+
literal|" "
operator|+
name|unit
operator|.
name|toUpperCase
argument_list|()
argument_list|)
operator|.
name|subset
argument_list|(
literal|"sink"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|sink
operator|.
name|getRollInterval
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

